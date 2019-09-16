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

#include "LinkBar.h"
#include "Level.h"
#include "Rails.h"
#include <GLES/gl.h>
#include <math.h>
#include "log.h"

void LinkBar::AddDynamicWeight(float x, float w)
{
	float width = mNodes[1]->GetPosition(this)->x - mNodes[0]->GetPosition(this)->x;
	float coef = (x-mNodes[0]->GetPosition(this)->x) / width;
	if (coef<0) coef=0; else if (coef>1) coef=1;
	mNodes[0]->AddDynamicWeight(w*(1-coef),this);
	mNodes[1]->AddDynamicWeight(w*coef,this);
}

void LinkBar::StartSimulationFrame(float dt)
{
//	LOGD("LinkBar::StartSimulationFrame(%f)",dt);
	if (!IsBroken())
	{
		if (mBreaking)
			mBreakTimer += dt;
		else
			mBreakTimer=0;
	}
	mSimulationFrameCounter = BAR_UPDATE_ITERATIONS;
}

bool LinkBar::UpdateSimulation(float dt)
{
	if (0==mSimulationFrameCounter) return false;

//	LOGD("LinkBar::UpdateSimulation(%f) mSimulationFrameCounter=%d",dt,mSimulationFrameCounter);

	ZVector D,D1,D2; D.sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	float d = D.norme();

	if (!IsBroken())
	{
		if (d>0.00001f)	// avoids divide by 0!
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

	mSimulationFrameCounter--;
	return (mSimulationFrameCounter>0);
}


bool LinkBar::IsBroken()
{
	if (mBroken) return true;

	// calcul de la rupture
	float lengthFactor = GetNominalLengthFactor();
	mBreaking = (lengthFactor>BAR_MAXLENGTHFACTOR || lengthFactor<BAR_MINLENGTHFACTOR);

	if (mBreaking && mBreakTimer>BREAK_TIME_THRESHOLD)
		SetBroken();

	return mBroken;
}

float LinkBar::GetStressFactor()
{
	float lengthFactor = GetNominalLengthFactor();
	if (lengthFactor<1.f)
		return (1.f-lengthFactor)/(1.f-BAR_MINLENGTHFACTOR);
	else
		return (lengthFactor-1.f)/(BAR_MAXLENGTHFACTOR-1.f);
}

void LinkBar::SetBroken()
{
	mBroken = true;
	Level::instance->FireParticleSystemLinkBarBreak(this);
}

void LinkBar::InitGraphics(ZScene *scene,bool is3D)
{
	// nothing to do here
	mIs3D = is3D; // draw in 3D, or in 2D ?
}

void LinkBar::ResetGraphics(ZScene *scene)
{
	// nothing to do here
}

void LinkBar::UpdateGraphics(int elsapedTime)
{
	// THIS is where the mesh will be constructed

	if (IsBroken()) return;

	ZMesh *mesh = &Level::instance->mLinkBarsMesh;
	int index = mesh->getNbVerts();
	static ZVector P; P.copy(mNodes[0]->GetPosition(this));
	static ZVector X; X.sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	static ZVector Z; Z.set(-X.z,0.f,X.x); Z.normalize();
	static ZVector Y(0,1,0);
	static ZVector v;
	//static ZVector nDiag1 = new ZVector(0.707f,-0.707f,0);
	//static ZVector nDiag2 = new ZVector(-0.707f,-0.707f,0);
	static ZVector nDiag1(0.707f,-0.707f,0); // TEMP
	static ZVector nDiag2(-0.707f,-0.707f,0); // TEMP

	if (mIs3D)
	{
		// draw the mesh in 3D
		if (mIsRails)
		{
			// CAS 1: tablier du pont (rails)
			// front
			v.mulAddMul(&X,0,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.5f);
			v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.5f);
			v.mulAddMul(&X,0,&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
			v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
			mesh->addFace(index+0,index+1,index+3);
			mesh->addFace(index+0,index+3,index+2);
			// rear
			v.mulAddMul(&X,0,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.5f);
			v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.5f);
			v.mulAddMul(&X,0,&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
			v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
			mesh->addFace(index+4,index+5,index+7);
			mesh->addFace(index+4,index+7,index+6);
			// tablier
			v.mulAddMul(&X,0,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
			v.mulAddMul(&X,BAR_TRAVERSE_WIDTH,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);
			v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
			v.mulAddMul(&X,1.f-BAR_TRAVERSE_WIDTH,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);

			v.mulAddMul(&X,0,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
			v.mulAddMul(&X,BAR_TRAVERSE_WIDTH,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);
			v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
			v.mulAddMul(&X,1.f-BAR_TRAVERSE_WIDTH,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);

			mesh->addFace(index+8,index+9,index+13);				mesh->addFace(index+8,index+12,index+13);
			mesh->addFace(index+10,index+11,index+15);				mesh->addFace(index+10,index+14,index+15);

			RailsHelper::CreateRailsMesh(true,mesh,&P,&X,&Z,0,0.25f);
		}
		else
		{
			if (!mIsRailsHeight)
			{
				// CAS 3: barres complexes
				// front
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag1, 0, 1);
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag1, 0, 0.75f);
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0);
				v.mulAddMul(&X,BAR_TRAVERSE_WIDTH,&Y,-BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);

				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag2, 1, 1);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.5f);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag2, 1, 0.75f);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0);
				v.mulAddMul(&X,1.f-BAR_TRAVERSE_WIDTH,&Y,-BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 0, 0.75f);

				// rear
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag2, 0, 1);
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 1, 0.5f);
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag2, 0, 0.75f);
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0);
				v.mulAddMul(&X,BAR_TRAVERSE_WIDTH,&Y,BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 1, 0.75f);

				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag1, 1, 1);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 1, 0.5f);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT2); v.add(&P); mesh->addVertex(&v, &nDiag1, 1, 0.75f);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0);
				v.mulAddMul(&X,1.f-BAR_TRAVERSE_WIDTH,&Y,BRIDGE_HALF_WIDTH,&Z,0); v.add(&P); mesh->addVertex(&v, &Z, 1, 0.75f);

				// a bunch of faces....
				mesh->addFace(index+0,index+6,index+10);		mesh->addFace(index+0,index+10,index+4); // front
				mesh->addFace(index+12,index+18,index+22);		mesh->addFace(index+12,index+22,index+16); // rear
				mesh->addFace(index+1,index+19,index+21);		mesh->addFace(index+1,index+21,index+3); // diag1
				mesh->addFace(index+13,index+7,index+9);		mesh->addFace(index+13,index+9,index+15); // diag2
				mesh->addFace(index+2,index+5,index+17);		mesh->addFace(index+2,index+17,index+14); // traverse1
				mesh->addFace(index+11,index+8,index+20);		mesh->addFace(index+11,index+20,index+23); // traverse2

			}
			else
			{
				// CAS 2: barres simples
				// front
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
				v.mulAddMul(&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0);
				v.mulAddMul(&X,1,&Y,-BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0);
				mesh->addFace(index+0,index+1,index+3);
				mesh->addFace(index+0,index+3,index+2);
				// rear
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
				v.mulAddMul(&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0);
				v.mulAddMul(&X,1,&Y,BRIDGE_HALF_WIDTH,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0);
				mesh->addFace(index+4,index+5,index+7);
				mesh->addFace(index+4,index+7,index+6);

			}

		}
	}
	else
	{
		// draw the mesh in 2D

		if (mIsRails)
		{
			v.mulAddMul(&Y,LAYER2D_LINK,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.5f);
			v.mulAddMul(&X,1,&Y,LAYER2D_LINK,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.5f);
			v.mulAddMul(&Y,LAYER2D_LINK,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
			v.mulAddMul(&X,1,&Y,LAYER2D_LINK,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
			mesh->addFace(index+0,index+1,index+3);
			mesh->addFace(index+0,index+3,index+2);

			RailsHelper::CreateRailsMesh(false,mesh,&P,&X,&Z,0,0.25f);
		}
		else
		{
			v.mulAddMul(&Y,LAYER2D_LINK,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0.25f);
			v.mulAddMul(&X,1,&Y,LAYER2D_LINK,&Z,-BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0.25f);
			v.mulAddMul(&Y,LAYER2D_LINK,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 0, 0);
			v.mulAddMul(&X,1,&Y,LAYER2D_LINK,&Z,BAR_HALF_HEIGHT); v.add(&P); mesh->addVertex(&v, ZVector::Yneg(), 1, 0);
			mesh->addFace(index+0,index+1,index+3);
			mesh->addFace(index+0,index+3,index+2);

		}
	}

}

void LinkBar::RecomputeInitialState()
{
	Link::RecomputeInitialState();
	mIsRails = !Level::instance->mTowerMode && mNodes[0]->IsOnRailsHeight() && mNodes[1]->IsOnRailsHeight();

	float zMax = mNodes[0]->GetPosition(this)->z>mNodes[1]->GetPosition(this)->z?mNodes[0]->GetPosition(this)->z:mNodes[1]->GetPosition(this)->z;
	float zMin = mNodes[0]->GetPosition(this)->z<mNodes[1]->GetPosition(this)->z?mNodes[0]->GetPosition(this)->z:mNodes[1]->GetPosition(this)->z;
	mIsRailsHeight =  (zMin<Level::instance->mRailsHeight+1.99f	&& zMax>Level::instance->mRailsHeight+0.01f);

}



/*
	static ZVector v = new ZVector();
	static ZVector nDiag1 = new ZVector(0.707f,-0.707f,0);
	static ZVector nDiag2 = new ZVector(-0.707f,-0.707f,0);
	@Override
	public void initGraphics()
	{
		resetGraphics();
		if (UI.get().getViewMode() == UI.ViewMode.MODE2D)
		{
			mMesh = new ZMesh();
			if (mRails)
			{
				mMesh.createRectangle(0, -BAR_HALF_HEIGHT, 1, BAR_HALF_HEIGHT, 0, 0.5f, 1, 0.25f);
				mMesh.setMaterial("linkbar");
				ZMesh meshRail = Rails.Helper.createMesh2D(1);
				ZInstance instRail = new ZInstance(meshRail);
				instRail.setTranslation(0.5f,0,0);
				instRail.setRotation(ZQuaternion.constX270);
				mInstance2D = new ZInstance(mMesh);
				mInstance2D.add(instRail);
			}
			else
			{
				mMesh.createRectangle(0, -BAR_HALF_HEIGHT, 1, BAR_HALF_HEIGHT, 0, 0.25f, 1, 0);
				mMesh.setMaterial("linkbar");
				mInstance2D = new ZInstance(mMesh);
			}
			ZActivity.instance.mScene.add(mInstance2D);
		}
		if (UI.get().getViewMode() == UI.ViewMode.MODE3D)
		{
			mMesh = new ZMesh();
			if (mRails)
			{
				// CAS 1: tablier du pont (rails)
				mMesh.allocMesh(16, 8, true);
				// front
				v.set(0,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.5f);
				v.set(1,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.5f);
				v.set(0,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
				v.set(1,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
				mMesh.addFace(0,1,3);
				mMesh.addFace(0,3,2);
				// rear
				v.set(0,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.5f);
				v.set(1,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.5f);
				v.set(0,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
				v.set(1,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
				mMesh.addFace(4,5,7);
				mMesh.addFace(4,7,6);
				// tablier
				v.set(0,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 0, 0.5f);
				v.set(BAR_TRAVERSE_WIDTH,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 0, 0.75f);
				v.set(1,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 0, 0.5f);
				v.set(1.f-BAR_TRAVERSE_WIDTH,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 0, 0.75f);

				v.set(0,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 1, 0.5f);
				v.set(BAR_TRAVERSE_WIDTH,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 1, 0.75f);
				v.set(1,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 1, 0.5f);
				v.set(1.f-BAR_TRAVERSE_WIDTH,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, ZVector::Z(), 1, 0.75f);

				mMesh.addFace(8,9,13);				mMesh.addFace(8,12,13);
				mMesh.addFace(10,11,15);				mMesh.addFace(10,14,15);
			}
			else
			{
				float zMax = Math.max(getNode(0).getPositionZ(), getNode(1).getPositionZ());
				float zMin = Math.min(getNode(0).getPositionZ(), getNode(1).getPositionZ());
				if (zMin>Level.get().getDefaultRailsHeight()+1.99f
						|| zMax<Level.get().getDefaultRailsHeight()+0.01f)
				{
					// CAS 3: barres complexes
					mMesh.allocMesh(24, 12, true);
					// front
					v.set(0,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
					v.set(0,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag1, 0, 1);
					v.set(0,-BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 0, 0.5f);
					v.set(0,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag1, 0, 0.75f);
					v.set(0,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0);
					v.set(BAR_TRAVERSE_WIDTH,-BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 0, 0.75f);

					v.set(1,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
					v.set(1,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag2, 1, 1);
					v.set(1,-BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 0, 0.5f);
					v.set(1,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag2, 1, 0.75f);
					v.set(1,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0);
					v.set(1.f-BAR_TRAVERSE_WIDTH,-BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 0, 0.75f);

					// rear
					v.set(0,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
					v.set(0,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag2, 0,1);
					v.set(0,BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 1, 0.5f);
					v.set(0,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag2, 0, 0.75f);
					v.set(0,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0);
					v.set(BAR_TRAVERSE_WIDTH,BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 1, 0.75f);

					v.set(1,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
					v.set(1,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag1, 1, 1);
					v.set(1,BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 1, 0.5f);
					v.set(1,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT2); mMesh.addVertex(v, nDiag1, 1, 0.75f);
					v.set(1,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0);
					v.set(1.f-BAR_TRAVERSE_WIDTH,BRIDGE_HALF_WIDTH, 0); mMesh.addVertex(v, ZVector::Z(), 1, 0.75f);

					// a bunch of faces....
					mMesh.addFace(0,6,10);		mMesh.addFace(0,10,4); // front
					mMesh.addFace(12,18,22);	mMesh.addFace(12,22,16); // rear
					mMesh.addFace(1,19,21);		mMesh.addFace(1,21,3); // diag1
					mMesh.addFace(13,7,9);		mMesh.addFace(13,9,15); // diag2
					mMesh.addFace(2,5,17);		mMesh.addFace(2,17,14); // traverse1
					mMesh.addFace(11,8,20);		mMesh.addFace(11,20,23); // traverse2

				}
				else
				{
					// CAS 2: barres simples
					mMesh.allocMesh(8, 4, true);
					// front
					v.set(0,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
					v.set(1,-BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
					v.set(0,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0);
					v.set(1,-BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0);
					mMesh.addFace(0,1,3);
					mMesh.addFace(0,3,2);
					// rear
					v.set(0,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0.25f);
					v.set(1,BRIDGE_HALF_WIDTH, -BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0.25f);
					v.set(0,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 0, 0);
					v.set(1,BRIDGE_HALF_WIDTH, BAR_HALF_HEIGHT); mMesh.addVertex(v, ZVector::Yneg(), 1, 0);
					mMesh.addFace(4,5,7);
					mMesh.addFace(4,7,6);

				}

			}

			mMesh.enableBackfaceCulling(false);
			mMesh.setMaterial("linkbar");
			mInstance3D = new ZInstance(mMesh);

			if (mRails)
			{
				ZInstance railsInstance = new ZInstance(Rails.Helper.createMesh(1));
				railsInstance.setTranslation(0.5f,0,0);
				mInstance3D.add(railsInstance);
			}

			ZActivity.instance.mScene.add(mInstance3D);

		}
	}

	static ZQuaternion q = new ZQuaternion();
	static ZColor col = new ZColor();
	@Override
	public void updateGraphics(int elapsedTime)
	{
		if (mInstance2D!=null)
		{
			mInstance2D.setRotation(ZQuaternion.constX90);
			mInstance2D.setTranslation(getNode(0).getPositionX(),0,getNode(0).getPositionZ());
			mInstance2D.translate(0, layer2dLink, 0);
			mInstance2D.rotate(ZVector::Y(),-getAngle());
			mInstance2D.setScale(getLength(),1,1);

			if (UI.get().isEditMode())
			{
				// color update
				if (mSelected)
				{
					mAnimCounter = (mAnimCounter+elapsedTime)%1000;
					float animperiod = (float)mAnimCounter * (float)Math.PI * 0.002f;
					float scale = 1.f + 0.5f*(1.f+(float)Math.cos(animperiod));
					float color = 0.5f*(1.f+(float)Math.cos(animperiod));
					mInstance2D.setScale(getLength(),scale,1);
					col.set(1,1-color,1-color,1);
					mInstance2D.setColor(col);
				}
				else
				{
					mInstance2D.setColor(ZColor.constWhite);
					mInstance2D.setColored(false);
				}
			}
			mInstance2D.setVisible(!isBroken());
		}
		if (mInstance3D!=null)
		{
			mInstance3D.setTranslation(getNode(0).getPositionX(),0,getNode(0).getPositionZ());
			q.set(ZVector::Y(),-getAngle());
			mInstance3D.setRotation(q);
			mInstance3D.setScale(getLength(),1,1);

			mInstance3D.setVisible(!isBroken());

			mInstance3D.setColored(false);
		}
	}

 */
