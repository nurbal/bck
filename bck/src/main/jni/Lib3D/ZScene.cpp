/*
 * This file is part of the Bridge Construction Kit distribution (https://github.com/https://github.com/nurbal/bck).
 * Copyright (c) 2019 Bruno Carrez.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * A fixed-point SRT transformer class
 *
 */

#include <jni.h>
#include <math.h>
#include "ZRenderer.h"
#include "ZScene.h"
#include "ZCamera.h"
#include "helpers.h"
#include "log.h"
#include "release_settings.h"
#include "lib3d_mutex.h"
#include "../BCK/jni_Misc.h"

ZScene::ZScene() : ZSmartObject()
{
	// light
	mLightPosition.set(-1.f,1.f,1.f);
	mAmbientLight = 1.f;
	mDiffuseLight = 1.f;
	mSpecularLight = 0.f;
}

ZScene::~ZScene()
{
}


void ZScene::SetLight(float ambient,float diffuse,float specular,const ZVector* position)
{
	LIGHT_MUTEX_LOCK
	mAmbientLight = ambient;
	mSpecularLight = specular;
	mDiffuseLight = diffuse;
	mLightPosition.copy(position);
	LIGHT_MUTEX_UNLOCK
}

void ZScene::Update(int elapsedTime)
{
	gRenderer.DBG_CheckThread();

	INSTANCES_MUTEX_LOCK
	ZListIterator<PInstance> it;
	it = mInstances.Begin(); 			while (!it.IsEnd()) { it.Get()->Update(elapsedTime); it.Next(); }
	it = mInstancesAlpha.Begin(); 		while (!it.IsEnd()) { it.Get()->Update(elapsedTime); it.Next(); }
	it = mInstancesSky.Begin(); 		while (!it.IsEnd()) { it.Get()->Update(elapsedTime); it.Next(); }
	it = mInstancesSkyAlpha.Begin(); 	while (!it.IsEnd()) { it.Get()->Update(elapsedTime); it.Next(); }
	it = mInstances2D.Begin(); 			while (!it.IsEnd()) { it.Get()->Update(elapsedTime); it.Next(); }
	INSTANCES_MUTEX_UNLOCK
}

void ZScene::Add(const PInstance& inst)
{
	QUEUED_INSTANCES_MUTEX_LOCK
	if (inst->mIsColored)
		mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::ADDALPHA,inst));
	else
		mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::ADD,inst));
	QUEUED_INSTANCES_MUTEX_UNLOCK
}

void ZScene::AddSky(const PInstance& inst)
{
	QUEUED_INSTANCES_MUTEX_LOCK
	if (inst->mIsColored)
		mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::ADDSKYALPHA,inst));
	else
		mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::ADDSKY,inst));
	QUEUED_INSTANCES_MUTEX_UNLOCK
}

void ZScene::Add2D(const PInstance& inst)
{
	QUEUED_INSTANCES_MUTEX_LOCK
	mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::ADD2D,inst));
	QUEUED_INSTANCES_MUTEX_UNLOCK
}

void ZScene::Remove(const PInstance& inst)
{
	QUEUED_INSTANCES_MUTEX_LOCK
	mWaitingInstances.Add(new ZQueuedInstance(ZQueuedInstance::REMOVE,inst));
	QUEUED_INSTANCES_MUTEX_UNLOCK
}

void ZScene::Clear()
{
	INSTANCES_MUTEX_LOCK
	mInstances.Unlink();
	mInstancesAlpha.Unlink();
	mInstancesSky.Unlink();
	mInstancesSkyAlpha.Unlink();
	mInstances2D.Unlink();
	INSTANCES_MUTEX_UNLOCK

	QUEUED_INSTANCES_MUTEX_LOCK
	ZQueuedInstance *qi;
	while ((qi=mWaitingInstances.GetFirst()))
	{
		mWaitingInstances.Unlink(qi);
		delete(qi);
	}
	QUEUED_INSTANCES_MUTEX_UNLOCK
}

// prepare for rendering: copy transformers, insert/remove instances, etc...
void ZScene::PrepareForRendering(const ZCamera *camera,const ZCamera *camera2D)
{
	gRenderer.DBG_CheckThread();

	// first, update insertion / remove lists
	LOGV("ZScene::PrepareForRendering 1");
	QUEUED_INSTANCES_MUTEX_LOCK
	INSTANCES_MUTEX_LOCK
	ZQueuedInstance *qi;
	while ((qi=mWaitingInstances.GetFirst()))
	{
		mWaitingInstances.Unlink(qi);
		switch (qi->mType)
		{
		case ZQueuedInstance::REMOVE:
			LOGV("Removing instance %X refcount=%i",(ZInstance*)qi->mInstance,qi->mInstance->GetRefCount());
			mInstances.Unlink(qi->mInstance);
			mInstancesAlpha.Unlink(qi->mInstance);
			mInstancesSky.Unlink(qi->mInstance);
			mInstancesSkyAlpha.Unlink(qi->mInstance);
			mInstances2D.Unlink(qi->mInstance);
			LOGI("Removing instance %X refcount=%i (done)",(ZInstance*)qi->mInstance,qi->mInstance->GetRefCount());
			break;
		case ZQueuedInstance::ADD:
			mInstances.Add(qi->mInstance);
			break;
		case ZQueuedInstance::ADDALPHA:
			mInstancesAlpha.Add(qi->mInstance);
			break;
		case ZQueuedInstance::ADDSKY:
			mInstancesSky.Add(qi->mInstance);
			break;
		case ZQueuedInstance::ADDSKYALPHA:
			mInstancesSkyAlpha.Add(qi->mInstance);
			break;
		case ZQueuedInstance::ADD2D:
			mInstances2D.Add(qi->mInstance);
			break;
		}
		delete(qi);
	}
/*
	PInstance inst;
	while (!mRemovedInstances.IsEmpty())
	{
		inst=mRemovedInstances.GetFirst();
		LOGV("Removing instance %X refcount=%i",(ZInstance*)inst,inst->GetRefCount());
		mInstances.Unlink(inst);
		mInstancesAlpha.Unlink(inst);
		mInstancesSky.Unlink(inst);
		mInstancesSkyAlpha.Unlink(inst);
		mInstances2D.Unlink(inst);
		LOGI("Removing instance %X refcount=%i",(ZInstance*)inst,inst->GetRefCount());
		mRemovedInstances.Unlink(inst);
		LOGI("Removing instance %X refcount=%i (done)",(ZInstance*)inst,inst->GetRefCount());
	}
	while (!mQueuedInstances.IsEmpty()) { inst=mQueuedInstances.GetFirst(); mQueuedInstances.Unlink(inst); mInstances.Add(inst); }
	while (!mQueuedInstancesAlpha.IsEmpty()) { inst=mQueuedInstancesAlpha.GetFirst(); mQueuedInstancesAlpha.Unlink(inst); mInstancesAlpha.Add(inst); }
	while (!mQueuedInstancesSky.IsEmpty()) { inst=mQueuedInstancesSky.GetFirst(); mQueuedInstancesSky.Unlink(inst); mInstancesSky.Add(inst); }
	while (!mQueuedInstancesSkyAlpha.IsEmpty()) { inst=mQueuedInstancesSkyAlpha.GetFirst(); mQueuedInstancesSkyAlpha.Unlink(inst); mInstancesSkyAlpha.Add(inst); }
	while (!mQueuedInstances2D.IsEmpty())
	{
		inst=mQueuedInstances2D.GetFirst();
		mQueuedInstances2D.Unlink(inst);
		mInstances2D.Add(inst);
	}
*/
	QUEUED_INSTANCES_MUTEX_UNLOCK
	LOGV("ZScene::PrepareForRendering 2");

	INSTANCES_TRANSFORMER_MUTEX_LOCK
		ZListIterator<PInstance> it;
		it = mInstances.Begin(); 			while (!it.IsEnd()) { it.Get()->PrepareForRendering(camera); it.Next(); }
		it = mInstancesAlpha.Begin(); 		while (!it.IsEnd()) { it.Get()->PrepareForRendering(camera); it.Next(); }
		it = mInstancesSky.Begin(); 		while (!it.IsEnd()) { it.Get()->PrepareForRendering(camera); it.Next(); }
		it = mInstancesSkyAlpha.Begin(); 	while (!it.IsEnd()) { it.Get()->PrepareForRendering(camera); it.Next(); }
		it = mInstances2D.Begin(); 			while (!it.IsEnd()) { it.Get()->PrepareForRendering(camera2D); it.Next(); }
		INSTANCES_TRANSFORMER_MUTEX_UNLOCK

		if (camera)
		{
			SortInstances(&mInstancesAlpha);
			SortInstances(&mInstancesSkyAlpha);
			SortInstances(&mInstances2D);
		}
	INSTANCES_MUTEX_UNLOCK
}

void ZScene::SortInstances(ZList<PInstance> *list)	// sorts instances before render, farthest first, nearest last.
{
	gRenderer.DBG_CheckThread();

	if (list->GetNbElements()<2) return;	// rien � trier du tout
	// calcul des distances
	ZListIterator<PInstance> current,next;
	next = list->Begin();	next.Next();
	while (!next.IsEnd())
	{
		// on m�morise le suivant pour le tour suivant
		current = next; next.Next();
		// on descend le noeud courant tant qu'il le faut...
		while (current.GetPrevious() && (*current.GetPrevious())->mRenderSortValue < current.Get()->mRenderSortValue)
			current.MoveBackward();
	}

}

