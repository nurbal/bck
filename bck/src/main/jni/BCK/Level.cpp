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

#include "Level.h"
#include <GLES/gl.h>
#include <math.h>
#include "log.h"

#include "P6PoolLinkBreak.h"


Level * Level::instance = 0;

Level::Level()
{
	LOGD("Level::Level() instance = %d",(long)(void*)instance);
	instance = this;

	mHasWater = false;

	mWindValue = 0.f;

	mRailsHeight = 0.f;

	mNearHeight=0;
	mFarHeight=0;
	mFloorHeight=0;

	mTowerMode = false;
	mTowerGoal = 0.f;

	mP6PoolBars=0;
	mP6PoolJacks=0;
	mP6PoolCables=0;

	mUpdateGraphicsEnabled=false;

	mLinkBarsMesh.allocMesh(BARS_MESH_NB_VERTS,BARS_MESH_NB_FACES,true);
	mLinkBarsMesh.enableBackfaceCulling(false);
	mLinkCablesMesh.allocMesh(CABLES_MESH_NB_VERTS,CABLES_MESH_NB_FACES,true);
	mLinkCablesMesh.enableBackfaceCulling(false);


}

Level::~Level()
{
	LOGD("Level::~Level()");
}

float Level::GetFloorHeight(int x) { return mFloorHeight[x-mXMin]; }
float Level::GetNearHeight(int x) { return mNearHeight[x-mXMin]; }
float Level::GetFarHeight(int x) { return mFarHeight[x-mXMin]; }

void Level::SetNearHeight(int x, float height)	{ if (!isHeightValid(mNearHeight[x-mXMin])) mNearHeight[x-mXMin] = height; }
void Level::SetFarHeight(int x, float height)	{ if (!isHeightValid(mFarHeight[x-mXMin])) mFarHeight[x-mXMin] = height; }
void Level::SetFloorHeight(int x, float height)	{ if (!isHeightValid(mFloorHeight[x-mXMin])) mFloorHeight[x-mXMin] = height; }

void Level::AllocFloor(int xMin, int xMax)
{
	mXMin = xMin;
	mXMax = xMax;
	if (mNearHeight) delete [] mNearHeight;
	if (mFarHeight) delete [] mFarHeight;
	if (mFloorHeight) delete [] mFloorHeight;
	mNearHeight = new float[mXMax-mXMin+1];
	mFarHeight = new float[mXMax-mXMin+1];
	mFloorHeight = new float[mXMax-mXMin+1];
	for (int x=0; x<mXMax-mXMin+1; x++) mNearHeight[x] = mFarHeight[x] = mFloorHeight[x] = getInvalidHeight();
}

void Level::CompleteFloor()
{
	// on remplit les bords correctement.....
	for (int x=0; x<mXMax-mXMin+1; x++)
	{
		if (!isHeightValid(mFloorHeight[x]))
			mFloorHeight[x] = isHeightValid(mFarHeight[x])?mFarHeight[x]:mNearHeight[x];
		if (!isHeightValid(mNearHeight[x]))
			mNearHeight[x] = isHeightValid(mFarHeight[x])?mFarHeight[x]:mFloorHeight[x];
		if (!isHeightValid(mFarHeight[x]))
			mFarHeight[x] = isHeightValid(mNearHeight[x])?mNearHeight[x]:mFloorHeight[x];
	}
}

float Level::GetFloorNearestPoint(ZVector *inPoint,float inMaxRay,ZVector *outPoint,ZVector *outNormal,ZVector *outTangent) // can return negative values!! (if underground)
{
	float xPoint = inPoint->x;
	if (xPoint+inMaxRay-0.1f<(float)mXMin || xPoint-inMaxRay+0.1f>(float)mXMax) return 10000;
	int xMin = (int)(xPoint-inMaxRay); if (xMin<mXMin) xMin=mXMin;
	int xMax = (int)(xPoint+inMaxRay); if (xMax>mXMax-1) xMax=mXMax-1;
	float bestDist = -1;
	ZVector T,D,P;
	for (int X=xMin; X<=xMax; X++)
	{
		float z1 = GetFloorHeight(X);
		float z2 = GetFloorHeight(X+1);
		T.set(1,0,z2-z1); // T = p0->p1 (non normalise encore)
		float l = T.norme(); // l=longueur du segment
		T.mul(1.f/l);	// T normalise
		// vecteur D distance p0->inPoint, abscisse x sur outTangent [0..l]
		D.set(-X,0,-z1); D.add(inPoint);
		float x = D.dot(&T);
		if (x<0) x=0; else if (x>l) x=l;
		// vecteur P point le plus proche, D distance P->inPoint, d norme de D
		P.set(X, 0,z1); P.addMul(&T,x);
		D.sub(inPoint,&P);
		float d = D.norme();

		if (bestDist<0 || d<bestDist)
		{
			bestDist = d;
			outPoint->copy(&P);
			outNormal->copy(&D); outNormal->normalize();
			outTangent->copy(&T);
		}
	}
	if (outNormal->z<0)
	{
		outNormal->mul(-1); // N toujours vers le haut pour le sol
		bestDist = -bestDist;
	}
	return bestDist;
}

bool Level::CorrectFloorPosition(ZVector *point, ZVector *N, ZVector *T) // returns true if collision
{
	ZVector P;
	float dist = GetFloorNearestPoint(point,0,&P,N,T);
	if (dist<0)
	{
		point->copy(&P);
		return true;
	}
	return false;
}

void Level::UpdateSimulation(float dt)
{
	// update des nodes (calcul des vitesses et des positions)
	ZListIterator<PNode> itNode = mMobileNodes.Begin();
	while (!itNode.IsEnd())
	{
		itNode.Get()->UpdateSimulation(dt);
		itNode.Next();
	}
	// update des links (correction des positions)
	ZListIterator<PLink> itLink = mLinks.Begin();
	while (!itLink.IsEnd())
	{
		itLink.Get()->StartSimulationFrame(dt);
		itLink.Next();
	}
	bool moreUpdateNeeded = false;
	do
	{
		moreUpdateNeeded = false;
		itLink = mLinks.Begin();
		while (!itLink.IsEnd())
		{
			moreUpdateNeeded |= itLink.Get()->UpdateSimulation(dt);
			itLink.Next();
		}
	} while (moreUpdateNeeded);

}

void Level::FireParticleSystemLinkBarBreak(Link *l)
{
	mP6PoolBars->Fire(l);
}

void Level::FireParticleSystemLinkJackBreak(Link *l)
{
	mP6PoolJacks->Fire(l);
}

void Level::FireParticleSystemLinkCableBreak(Link *l)
{
	mP6PoolCables->Fire(l);
}

void Level::SetP6LinkBreakMaterials(PMaterial barBreak,PMaterial jackBreak,PMaterial cableBreak)
{
	mMatP6Bars = barBreak;
	mMatP6Jacks = jackBreak;
	mMatP6Cables = cableBreak;
}

void Level::SetLinksMaterials(PMaterial bar,PMaterial jackHead,PMaterial jackRetract,PMaterial jackExpand,PMaterial cable)
{
	mMatBars = bar; mLinkBarsMesh.mMaterial = mMatBars;
	mMatJackHead = jackHead;
	mMatJackRetract = jackRetract;
	mMatJackExpand = jackExpand;
	mMatCables = cable; mLinkCablesMesh.mMaterial = mMatCables;
}

void Level::InitGraphics(ZScene *scene,bool is3D)
{
	LOGD("Level::InitGraphics is3D=%d",is3D);
	if (is3D)
	{
		mP6PoolBars = new P6PoolLinkBreak(scene,mMatP6Bars,8,1,P6_BARS_BREAK_NBPARTILES,P6_BARS_BREAK_COUNT);
		mP6PoolJacks = new P6PoolLinkBreak(scene,mMatP6Jacks,8,1,P6_JACKS_BREAK_NBPARTILES,P6_JACKS_BREAK_COUNT);
		mP6PoolCables = new P6PoolLinkBreak(scene,mMatP6Cables,8,1,P6_CABLES_BREAK_NBPARTILES,P6_CABLES_BREAK_COUNT);
	}

	mLinkBarsMesh.resetMesh();
	mLinkCablesMesh.resetMesh();
	ZListIterator<PLink> itLink = mLinks.Begin();
	while (!itLink.IsEnd())
	{
		itLink.Get()->InitGraphics(scene,is3D);
		itLink.Next();
	}

	if (mLinkBarsInstance == (void*)0)
	{
		mLinkBarsInstance = new ZInstance();
		mLinkBarsInstance->Add(&mLinkBarsMesh);
		scene->Add(mLinkBarsInstance);
	}
	if (mLinkCablesInstance == (void*)0)
	{
		mLinkCablesInstance = new ZInstance();
		mLinkCablesInstance->Add(&mLinkCablesMesh);
		scene->Add(mLinkCablesInstance);
	}
	mUpdateGraphicsEnabled=true;
}

void Level::ResetGraphics(ZScene *scene)
{
	mUpdateGraphicsEnabled=false;
	LOGD("Level::ResetGraphics");
	if (mP6PoolBars)
	{
		LOGD("Level::ResetGraphics 1");
		mP6PoolBars->Destroy(scene);
		LOGD("Level::ResetGraphics 2");
		delete mP6PoolBars;
		mP6PoolBars=0;
	}
	LOGD("Level::ResetGraphics 3");
	if (mP6PoolJacks)
	{
		mP6PoolJacks->Destroy(scene);
		delete mP6PoolJacks;
		mP6PoolJacks=0;
	}
	if (mP6PoolCables)
	{
		mP6PoolCables->Destroy(scene);
		delete mP6PoolCables;
		mP6PoolCables=0;
	}

	ZListIterator<PLink> itLink = mLinks.Begin();
	while (!itLink.IsEnd())
	{
		itLink.Get()->ResetGraphics(scene);
		itLink.Next();
	}
	mLinkBarsMesh.resetMesh();
	mLinkCablesMesh.resetMesh();
}

void Level::UpdateGraphics(int elapsedTime)
{
	mLinkBarsMesh.resetMesh();
	mLinkCablesMesh.resetMesh();
	if (!mUpdateGraphicsEnabled) return;
	ZListIterator<PLink> itLink = mLinks.Begin();
	while (!itLink.IsEnd())
	{
		CHECK_PTR(itLink.Get());
		itLink.Get()->UpdateGraphics(elapsedTime);
		itLink.Next();
	}
}
