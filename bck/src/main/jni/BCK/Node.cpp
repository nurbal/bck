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

#include "Node.h"
#include "Level.h"
#include <GLES/gl.h>
#include <math.h>
#include "log.h"


Node::Node()
{
	mFixed = false;
	mLinksWeight = 0;
	mDynamicWeight = 0;
	mIsSeparator = false;
	mIsSeparated = false;
	mSeparatorAngle = 0.f;
	mLockWanted = false;
	mJavaNativeObject = 0;
	mSaveID = 0;
	LOGD("Node::Node %X",this)
}

Node::~Node()
{
}

void Node::Copy(Node* n)
{
	mFixed = n->mFixed;
	mPosition.copy(&n->mPosition);
	mResetPosition.copy(&n->mResetPosition);
	mPrevPosition.copy(&n->mPrevPosition);
	mIsSeparator = n->mIsSeparator;
	mSeparatorAngle = n->mSeparatorAngle;
	mSeparatorDirection.copy(&n->mSeparatorDirection);
	mSaveID = n->mSaveID;
}

void Node::SetPosition(float x, float z)
{
	mResetPosition.set(x,0,z);
	mPosition.set(x,0,z);
	mPrevPosition.set(x,0,z);

	// recompute links length
	ZListIterator<Link*> it = mLinks.Begin();
	while (!it.IsEnd())
	{
		it.Get()->RecomputeInitialState();
		it.Next();
	}

}

static ZVector D;
void Node::ResetSimulation()
{
	mPosition.copy(&mResetPosition);
	mPrevPosition.copy(&mResetPosition);
	// pour que la d�-s�paration marche � coup s�r, on fusionne les deux points
	mPositionSeparated.copy(&mResetPosition);
	mPrevPositionSeparated.copy(&mResetPosition);

	SeparateNode(false);

	RecomputeLinksWeight();

	// compute links to separate...
	mLinksSeparated.Unlink();
	if (mIsSeparator)
	{
		ZListIterator<Link*> it = mLinks.Begin();
		while (!it.IsEnd())
		{
			D.sub(&it.Get()->GetOtherNode(this)->mResetPosition,&mResetPosition);
			if( D.dot(&mSeparatorDirection)>0)
				mLinksSeparated.Add(it.Get());
			it.Next();
		}
	}

}

void Node::RecomputeLinksWeight()
{
//	LOGD("Node::RecomputeLinksWeight 1 nb=%d",mLinks.GetNbElements());
	mLinksWeight = 0;
	mLinksWeightSeparated = 0;
	ZListIterator<Link*> it = mLinks.Begin();
	while (!it.IsEnd())
	{
//		LOGD("Node::RecomputeLinksWeight 2 node=%X",(int)(void*)it.Get());
		if (mIsSeparated && mLinksSeparated.IsInList(it.Get()))
			mLinksWeightSeparated += it.Get()->GetWeight();
		else
			mLinksWeight += it.Get()->GetWeight();
		it.Next();
	}
}


void Node::AddLink(Link* l)
{
	LOGD("Node::AddLink 1")
	mLinks.Add(l);
	LOGD("Node::AddLink 2")
	RecomputeLinksWeight();
	LOGD("Node::AddLink 3")
}
void Node::RemoveLink(Link* l)
{
	mLinks.Unlink(l);
	mLinksSeparated.Unlink(l);
	RecomputeLinksWeight();
}

void Node::AddDynamicWeight(float w,Link *l)
{
	if (mIsSeparated && mLinksSeparated.IsInList(l))
		mDynamicWeightSeparated += w;
	else
		mDynamicWeight += w;
}
void Node::ResetDynamicWeight()
{
	mDynamicWeight = 0.f;
	mDynamicWeightSeparated = 0.f;
}


// helper fonction to simulate a point, given its speed
static void sUpdatePointSimulation(float dt, ZVector *pos, ZVector *prevPos)
{
	// on reproduit le pr�c�dent d�placement, en ajoutant la pesanteur et le vent
	D.sub(pos, prevPos);
	prevPos->copy(pos);
	if (Level::instance->mHasWater && pos->z<Level::instance->mWaterHeight)
		D.addMul(ZVector::Z(), -dt*dt*GRAVITY_WATER);
	else
		D.addMul(ZVector::Z(), -dt*dt*GRAVITY);
	D.addMul(ZVector::X(), dt*dt*Level::instance->mWindValue*WIND_FORCE);

	// damper speed
	float dampingCoef = expf(-dt*DAMPER_FACTOR);
	D.mul(dampingCoef);

	pos->add(&D);

	// floor collision
	ZVector N,T;
	if (Level::instance->CorrectFloorPosition(pos,&N, &T))
	{
		float frictionCoef = 1.f-expf(-dt*FRICTION_FACTOR);
		frictionCoef *= fabs(N.z);
		pos->addMul(&T,-T.dot(&D)*frictionCoef);
	}

	// speed cap (in water)
	if (Level::instance->mHasWater && pos->z<Level::instance->mWaterHeight)
	{
		D.sub(pos, prevPos);
		float d = D.norme();
		if (d>WATER_MAX_SPEED_NODE*dt)
			prevPos->mulAddMul(pos,1,&D, -WATER_MAX_SPEED_NODE*dt/d);
	}

}

void Node::UpdateSimulation(float dt)
{
	// REFACTOR
	if (!mFixed && dt>0.0001f)
	{
		::sUpdatePointSimulation(dt,&mPosition, &mPrevPosition);

	}
	if (mIsSeparated)
	{
		::sUpdatePointSimulation(dt,&mPositionSeparated, &mPrevPositionSeparated);

		if (mLockWanted) SeparateNode(false); // tentative de verrouillage
	}
}

const ZVector* Node::GetPosition(Link * l) const // si l=null, c'est la position demand�e par l'�diteur...
{
	if (mIsSeparated && mLinksSeparated.IsInList(l))
		return &mPositionSeparated;
	return &mPosition;
}


float Node::GetWeight(Link *l) const
{
	if (mFixed) return 0.f;
	// tenir compte du link qui demande (si on est "separated")
	if (mIsSeparated && mLinksSeparated.IsInList(l))
		return 0.5f*mLinksWeightSeparated + mDynamicWeightSeparated; // on prend la moiti� du poids des barres
	return 0.5f*mLinksWeight + mDynamicWeight; // on prend la moiti� du poids des barres
}

void Node::Move(ZVector *v,Link * l)
{
	if (mIsSeparated && mLinksSeparated.IsInList(l))
		mPositionSeparated.add(v);
	else
		mPosition.add(v);
}

void Node::SeparateNode(bool separate)
{
	if (!mIsSeparator) return;
	if (separate==mIsSeparated) return;

LOGD("Node::SeparateNode this=%X angle=%f dx=%f dz=%f",this,mSeparatorAngle,mSeparatorDirection.x,mSeparatorDirection.z);

	if (separate)
	{
		// positionnement du noeud s�par�
		mPositionSeparated.copy(&mPosition);
		mPrevPositionSeparated.copy(&mPrevPosition);
	}
	else
	{
		// calcul du barycentre des deux points s�par�s
		float w = 0.5f*mLinksWeight + mDynamicWeight + 0.5f*mLinksWeightSeparated + mDynamicWeightSeparated;
		float coef = 0.5f*mLinksWeight + mDynamicWeight / w;
		ZVector v;
		v.sub(&mPosition,&mPositionSeparated);
		if (v.norme()>HYDRAULIC_LOCK_DISTANCEMAX)
		{
			// mise en attente...
			mLockWanted = true;
			return;
		}
		mLockWanted = false; // c'est bon!
		v.mulAddMul(&mPosition,coef,&mPositionSeparated,1-coef);			mPosition.copy(&v);
		v.mulAddMul(&mPrevPosition,coef,&mPrevPositionSeparated,1-coef);	mPrevPosition.copy(&v);
	}
	mIsSeparated = separate;
	RecomputeLinksWeight();
}

// fusion de nodes: transfert de tous les links vers un autre
void Node::Fusion(Node *target)
{
	if (target==this) return;
	LOGD("Node::Fusion this=%X target=%X",(long)(void*)this,(long)(void*)target);

	while (!mLinks.IsEmpty())
		mLinks.GetFirst()->ReplaceNode(this, target, true);

	LOGD("Node::Fusion");
	// REFACTOR
	if (mIsSeparator)
	{
		LOGD("Node::Fusion");
		// transfert des infos de s�paration
		target->SetSeparator(mSeparatorAngle);
		LOGD("Node::Fusion");
	}
	LOGD("Node::Fusion");

}

// separation nodes...
void Node::SetSeparator(float angle)
{
	mSeparatorAngle = angle;
	mSeparatorDirection.set(cosf(angle),0,sinf(angle));

	mIsSeparator = true;
}

void Node::UnsetSeparator()
{
	mIsSeparator = false;
}

bool Node::IsOnRailsHeight() const
{
	return (0.1f>fabs(mPosition.z-Level::instance->mRailsHeight));
}
