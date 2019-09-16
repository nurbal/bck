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

#include <GLES/gl.h>
#include "lib3d_mutex.h"
#include "ZInstance.h"
#include "ZCamera.h"
#include "helpers.h"


ZInstance::ZInstance() : ZObject()
{
	mVisible = true;
	mColor.SetWhite();
	mIsColored =false;
}

/*
ZInstance::ZInstance(ZObject* obj) : ZSmartObject()
{
	set(obj);
}
*/

void ZInstance::Add(ZObject* obj)
{
	mObjects.Add(obj);
	if (obj)
		mIsColored |= obj->IsColored();
}

void ZInstance::ResetObjects()
{
	mObjects.Unlink();
}

void ZInstance::Render(SceneContext context, bool parentIsColored, const ZColor* parentColor )
{
	ZListIterator<PObject> it;
	it = mObjects.Begin();

	while (!it.IsEnd())
	{
		PObject object = it.Get();
		it.Next();

		// lightning context...
		switch (context)
		{
		case SKY:
			glDisable(GL_LIGHTING);
			glDisable(GL_NORMALIZE);	// necessaire pour les normales, corrompues si l'objet est scal�
			break;
		case UI:
			glDisable(GL_LIGHTING);
			glDisable(GL_NORMALIZE);
			break;
		default:
			glEnable(GL_LIGHTING);
			glEnable(GL_NORMALIZE);
			break;
		}


		// some tests....
		if (mRenderIsColored || parentIsColored)
		{
			glDisable(GL_LIGHTING); //TEMP (moche)
			glDisable(GL_NORMALIZE);
	        glEnable(GL_BLEND);
			glColor4f(mRenderColor.r*parentColor->r,mRenderColor.g*parentColor->g,mRenderColor.b*parentColor->b,mRenderColor.a*parentColor->a);
			glDepthMask(false);
		}
		else
		{
	        glDisable(GL_BLEND);
	        if (object->GetLightMode()==ZObject::LIGHTABLE)
	        	glColor4f(1,1,1,1);
	        else
	        {
	        	glDisable(GL_LIGHTING);
	        	glDisable(GL_NORMALIZE);
	        }
			glDepthMask(true);
		}


		glPushMatrix();

		glMultMatrixf(mRenderMatrix.getOpenGLMatrix());
		ZColor c; c.mul(&mColor,parentColor);
		object->Render(context,mRenderIsColored||parentIsColored,&c);

		glPopMatrix();

	}
}

void ZInstance::Update(int elapsedTime)
{
	ZListIterator<PObject> it;
	it = mObjects.Begin();

	while (!it.IsEnd())
	{
		PObject object = it.Get();
		it.Next();
		object->Update(elapsedTime);
	}
}

void ZInstance::PrepareForRendering(const ZCamera *camera)
{
//	INSTANCES_TRANSFORMER_MUTEX_LOCK
	// c'est appel� par le renderer lui-m�me, pas besoin de mutex pour lui
	mRenderVisible = mVisible;
	if (mVisible)
	{
		mRenderColor.copy(&mColor);
		mRenderIsColored = mIsColored;
		mRenderMatrix.copy(mTransformer.getMatrix());

		ZListIterator<PObject> it;
		it = mObjects.Begin();

		// calcul de la distance
		// TEMP: simple dotProduct camFront/center
		if (camera)
		{
			const ZVector *camFront = camera->getCameraFront();
			mRenderSortValue = camFront->dot(mTransformer.getTranslation());
		}

		while (!it.IsEnd())
		{
			PObject object = it.Get();
			it.Next();
			object->PrepareForRendering(camera);
		}
	}
//	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetVisible(bool visible)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mVisible = visible;
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetColor(ZColor* color)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mColor.copy(color);
	mIsColored |= !mColor.isEqual(ZColor::WHITE());
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetIsColored(bool isColored)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mIsColored = isColored;
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetTranslation (ZVector *translation)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.setTranslation(translation);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetTranslation(float x,float y,float z)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.setTranslation(x,y,z);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetScale (ZVector *scale)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.setScale(scale);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetScale (float x,float y,float z)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.setScale(x,y,z);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::SetRotation (ZQuaternion *rotation)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.setRotation(rotation);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::Translate(const ZVector* v)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.translate(v);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::Translate(float x,float y,float z)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.translate(x,y,z);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::Rotate(const ZQuaternion* q)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.rotate(q);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::Rotate(const ZVector* v,float angle)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.rotate(v,angle);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}

void ZInstance::Rotate(const ZVector* v)
{
	INSTANCES_TRANSFORMER_MUTEX_LOCK
	mTransformer.rotate(v);
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
}


