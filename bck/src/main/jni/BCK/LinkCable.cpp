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

#include "LinkCable.h"
#include <GLES/gl.h>
#include <math.h>
#include <stdlib.h>
#include "Level.h"
#include "log.h"

ZVector D,D1,D2,v;

LinkCable::LinkCable()
{
	mCableSegments = 0;
	mCableNodes = 0;
	mIs3D = false;
}

LinkCable::~LinkCable()
{
	DestroySegments();
}

void LinkCable::Init(PNode n1, PNode n2)
{
	Link::Init(n1,n2);
	// ne plus utiliser n1 et n2 plus bas, ils ont ete tri�s; utiliser mNodes[] � la place
	CreateSegments();

}

void LinkCable::CreateSegments()
{
	DestroySegments();
	mCableNodes = new LinkCableNode*[CABLE_NB_SUBDIVISIONS+2];
	for (int i=0; i<CABLE_NB_SUBDIVISIONS+2; i++)
	{
		mCableNodes[i]=new LinkCableNode();
		float coef = (float)i / (float)(CABLE_NB_SUBDIVISIONS+1);
		v.mulAddMul(mNodes[0]->GetPosition(this), 1.f-coef, mNodes[1]->GetPosition(this), coef);
		mCableNodes[i]->setPosition(&v);
		mCableNodes[i]->mFixed = (i==0 || i==CABLE_NB_SUBDIVISIONS+1);
	}
	mCableSegments = new LinkCableSegment*[CABLE_NB_SUBDIVISIONS+1];
	float len = mNominalLength / (float)(CABLE_NB_SUBDIVISIONS+1);
	for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
		mCableSegments[i] = new LinkCableSegment(mCableNodes[i],mCableNodes[i+1],len);
}

void LinkCable::DestroySegments()
{
	if (mCableSegments)
		for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
			if (mCableSegments[i])
				delete mCableSegments[i];
	if (mCableNodes)
		for (int i=0; i<CABLE_NB_SUBDIVISIONS+2; i++)
			if (mCableNodes[i])
				delete mCableNodes[i];
	mCableSegments = 0;
	mCableNodes = 0;
}

bool LinkCable::IsBroken()
{
	//LOGD("LinkCable::IsBroken()");
	if (mBroken) return true;

	// calcul de la rupture
	mBreaking = GetNominalLengthFactor()>CABLE_MAXLENGTHFACTOR;

	if (mBreaking && mBreakTimer>BREAK_TIME_THRESHOLD)
		SetBroken();

	return mBroken;
}

float LinkCable::GetStressFactor()
{
	float lengthFactor = GetNominalLengthFactor();
	if (lengthFactor<1.f)
		return 0.f;
	else
		return (lengthFactor-1.f)/(CABLE_MAXLENGTHFACTOR-1.f);
}

void LinkCable::SetBroken()
{
	mBroken = true;
	mCableSegments[CABLE_NB_SUBDIVISIONS / 2]->mBroken = true;
	Level::instance->FireParticleSystemLinkCableBreak(this);
}

void LinkCable::StartSimulationFrame(float dt)
{
	if (!IsBroken())
	{
		if (mBreaking)
			mBreakTimer += dt;
		else
			mBreakTimer=0;
	}
	mSimulationFrameCounter = CABLE_UPDATE_ITERATIONS;

	for (int i=1; i<CABLE_NB_SUBDIVISIONS+1; i++)
		mCableNodes[i]->update(dt);
}

static float sMaxLengthFactor = 0.f;
float LinkCable::GetNominalLengthFactor()
{
	float f = GetLength()/mNominalLength;
	//LOGD("Cable length factor=%f = %f / %f",f,GetLength(),mNominalLength);

	if (f>sMaxLengthFactor)
	{
		LOGD("Cable max length factor=%f",f);
		sMaxLengthFactor = f;
	}
	return f;
}


bool LinkCable::UpdateSimulation(float dt)
{
	if (0==mSimulationFrameCounter) return false;
	D.sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	float d = D.norme();

	if (!IsBroken())
	{
		if (d>0.00001f && d>mNominalLength)	// avoids divide by 0!
		{
			D.mul((d-mNominalLength)/d);

			// corrects the distance between the two nodes
			if (!mNodes[0]->mFixed && !mNodes[1]->mFixed)
			{
				// move both nodes
				float w1 = mNodes[0]->GetWeight(this);
				float w2 = mNodes[1]->GetWeight(this);
				D1.mul(&D,w2/(w1+w2));
				D2.sub(&D1,&D); //D2.mul(D,-w1/(w1+w2));
				D1.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*w1));
				D2.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*w2));
				mNodes[0]->Move(&D1,this);
				mNodes[1]->Move(&D2,this);
			}
			else if (!mNodes[0]->mFixed)
			{
				// move first node only
				D.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*mNodes[0]->GetWeight(this)));
				mNodes[0]->Move(&D,this);
			}
			else if (!mNodes[1]->mFixed)
			{
				// move second node only
				D.mul(-expf(-dt*LINK_WEIGHT_TIME_FACTOR*mNodes[1]->GetWeight(this)));
				mNodes[1]->Move(&D,this);
			}
		}
	}

	mCableNodes[0]->setPosition(mNodes[0]->GetPosition(this));
	mCableNodes[CABLE_NB_SUBDIVISIONS+1]->setPosition(mNodes[1]->GetPosition(this));

	for (int j=0; j<CABLE_SEGMENTS_UPDATE_ITERATIONS; j++)
		for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
			mCableSegments[i]->update();

	mSimulationFrameCounter--;
	return (mSimulationFrameCounter>0);
}

void LinkCable::ResetSimulation()
{
	Link::ResetSimulation();
	for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
		mCableSegments[i]->mBroken = false;

	for (int i=0; i<CABLE_NB_SUBDIVISIONS+2; i++)
	{
		float coef = (float)i / (float)(CABLE_NB_SUBDIVISIONS+1);
		v.mulAddMul(mNodes[0]->GetResetPosition(), 1.f-coef, mNodes[1]->GetResetPosition(), coef);
		mCableNodes[i]->setPosition(&v);
	}
	float len = mNominalLength / (float)(CABLE_NB_SUBDIVISIONS+1);
	for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
		mCableSegments[i]->mNominalLength = len*0.95f;

}

void LinkCable::RecomputeInitialState()
{
	Link::RecomputeInitialState();
	CreateSegments();
}



void LinkCable::InitGraphics(ZScene *scene,bool is3D)
{
	mIs3D = is3D;
}

void LinkCable::ResetGraphics(ZScene *scene)
{
	//TODO
}

void LinkCable::UpdateGraphics(int elsapedTime)
{
	CHECK_PTR(mCableSegments);
	for (int i=0; i<CABLE_NB_SUBDIVISIONS+1; i++)
	{
		CHECK_PTR(mCableSegments[i]);
		mCableSegments[i]->updateGraphics(mIs3D,&Level::instance->mLinkCablesMesh);
	}
}










LinkCableNode::LinkCableNode()
{
	mFixed = false;
}

void LinkCableNode::setPosition(const ZVector *pos)
{
	mPosition.copy(pos);
	mPrevPosition.copy(pos);
}
void LinkCableNode::setPosition(float x, float y, float z)
{
	mPosition.set(x,y,z);
	mPrevPosition.set(x,y,z);
}

void LinkCableNode::update(float dt)
{
	// on reproduit le pr�c�dent d�placement, en ajoutant la pesanteur
	static ZVector D;
	D.sub(&mPosition, &mPrevPosition);
	mPrevPosition.copy(&mPosition);
	if (Level::instance->mHasWater && mPosition.z<Level::instance->mWaterHeight)
		D.z -= dt*dt*GRAVITY_WATER;
	else
		D.z -= dt*dt*GRAVITY;

	// damper speed
	float dampingCoef = expf(-dt*DAMPER_FACTOR);
	D.mul(dampingCoef);


	mPosition.add(&D);

	// floor collision
	static ZVector N;
	static ZVector T;
	if (Level::instance->CorrectFloorPosition(&mPosition,&N, &T))
	{
		float frictionCoef = 1.f-expf(-dt*FRICTION_FACTOR);
		frictionCoef *= fabs(N.z);
		mPosition.addMul(&T,-T.dot(&D)*frictionCoef);
	}

	// speed cap (in water)
	if (Level::instance->mHasWater && mPosition.z<Level::instance->mWaterHeight)
	{
		D.sub(&mPosition, &mPrevPosition);
		float d = D.norme();
		if (d>WATER_MAX_SPEED_NODE*dt)
			mPrevPosition.mulAddMul(&mPosition,1,&D, -WATER_MAX_SPEED_NODE*dt/d);
	}
}










LinkCableSegment::LinkCableSegment(LinkCableNode *n1, LinkCableNode *n2, float length)
{
	mNodes[0] = n1;
	mNodes[1] = n2;
	mBroken = false;
	mNominalLength = length;
}

void LinkCableSegment::update()
{
	if (mBroken) return;
	static ZVector D;
	D.sub(&mNodes[1]->mPosition,&mNodes[0]->mPosition);
	float d = D.norme();

	if (d>0.00001f)	// avoids divide by 0!
	{
		D.mul((d-mNominalLength)/d);

		// corrects the distance between the two nodes
		if (!mNodes[0]->mFixed && !mNodes[1]->mFixed)
		{
			// move both nodes
			mNodes[0]->mPosition.addMul(&D,0.5f);
			mNodes[1]->mPosition.addMul(&D,-0.5f);
		}
		else if (!mNodes[0]->mFixed)
		{
			// move first node only
			mNodes[0]->mPosition.add(&D);
		}
		else if (!mNodes[1]->mFixed)
		{
			// move second node only
			mNodes[1]->mPosition.sub(&D);
		}
	}
}
void LinkCableSegment::updateGraphics(bool is3D,ZMesh *mesh)
{
	if (mBroken) return;
	int index = mesh->getNbVerts();

	static ZVector X,Z,V;

	X.sub(&mNodes[1]->mPosition,&mNodes[0]->mPosition);
	X.normalize();
	Z.set(-X.z,0,X.x);

	if (is3D)
	{
		V.mulAddMul(&mNodes[0]->mPosition,1,ZVector::Y(),-BRIDGE_HALF_WIDTH,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,0);
		V.mulAddMul(&mNodes[1]->mPosition,1,ZVector::Y(),-BRIDGE_HALF_WIDTH,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,0);
		V.mulAddMul(&mNodes[0]->mPosition,1,ZVector::Y(),-BRIDGE_HALF_WIDTH,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,1);
		V.mulAddMul(&mNodes[1]->mPosition,1,ZVector::Y(),-BRIDGE_HALF_WIDTH,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,1);
		mesh->addFace(index+0,index+1,index+3);
		mesh->addFace(index+0,index+3,index+2);
		V.mulAddMul(&mNodes[0]->mPosition,1,ZVector::Y(),BRIDGE_HALF_WIDTH,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,0);
		V.mulAddMul(&mNodes[1]->mPosition,1,ZVector::Y(),BRIDGE_HALF_WIDTH,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,0);
		V.mulAddMul(&mNodes[0]->mPosition,1,ZVector::Y(),BRIDGE_HALF_WIDTH,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,1);
		V.mulAddMul(&mNodes[1]->mPosition,1,ZVector::Y(),BRIDGE_HALF_WIDTH,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,1);
		mesh->addFace(index+4,index+5,index+7);
		mesh->addFace(index+4,index+7,index+6);
	}
	else
	{
		V.mulAddMul(&mNodes[0]->mPosition,1,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,0);
		V.mulAddMul(&mNodes[1]->mPosition,1,&Z,-CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,0);
		V.mulAddMul(&mNodes[0]->mPosition,1,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),0,1);
		V.mulAddMul(&mNodes[1]->mPosition,1,&Z,CABLE_HALF_HEIGHT); mesh->addVertex(&V,ZVector::Yneg(),1,1);
		mesh->addFace(index+0,index+1,index+3);
		mesh->addFace(index+0,index+3,index+2);
	}
}
