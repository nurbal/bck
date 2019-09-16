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

#include "../Lib3D/Lib3D.h"

#include "release_settings.h"

class Level;
#define PLevel ZSmartPtr<Level>

class P6PoolLinkBreak;


#include "Link.h"
#include "Node.h"

class Level : public ZSmartObject
{
public:
	static Level *instance;
	Level();
	virtual ~Level();

	bool CorrectFloorPosition(ZVector *point, ZVector *N, ZVector *T); // returns true if collision
	float GetFloorNearestPoint(ZVector *inPoint,float inMaxRay,ZVector *outPoint,ZVector *outNormal,ZVector *outTangent); // can return negative values!! (if underground)

	bool mHasWater;
	float mWaterHeight;

	float mRailsHeight;

	float mWindValue;

	bool mTowerMode;
	float mTowerGoal;

	// extents
	int mXMin,mXMax;
	float GetXCenter() { return 0.5f*(float)(mXMin+mXMax); }
	float GetXSize() { return (float)(mXMax-mXMin); }
	float mZMin,mZMax;
	float GetZCenter() { return 0.5f*(float)(mZMin+mZMax); }
	//float GetZSize() { return (float)(mZMax-mXMin); }

	// floor
	// d�compos� en trois niveaux: NearHeight, FloorHeight, FarHeight
	// seul FloorHeight compte pour le gameplay (=milieu, o� passe le train)
	// par defaut, s'ils ne sont pas s�par�ment d�finis:
	// Near prend la valeur de far et vice-versa, et si aucun n'est d�fini ils prennent la valeur de Floor
	// seul Floor doit �tre d�fini obligatoirement
	// les autres prennent la valeur qui leur est attribu�e
private:
	float *mNearHeight; // hauteur du sol sur les bords
	float *mFarHeight; // hauteur du sol sur les bords
	float *mFloorHeight; // hauteur du sol au milieu

	bool isHeightValid (float h) {return h>getInvalidHeight()+1.f;}	// hauteur initialis�e ?
	float getInvalidHeight() {return -10000.f;}							// hauteur non-initialis�e
public:
	float GetFloorHeight(int x);
	void SetFloorHeight(int x, float height);
	float GetNearHeight(int x);
	void SetNearHeight(int x, float height);
	float GetFarHeight(int x);
	void SetFarHeight(int x, float height);

	void AllocFloor(int xMin, int xMax);
	void CompleteFloor();
	void SetZExtents(int zMin, int zMax) { mZMin = zMin; mZMax = zMax; }

	// graphics
public:
	void InitGraphics(ZScene *scene,bool is3D);
	void ResetGraphics(ZScene *scene);
	void UpdateGraphics(int elpaseTime);
	void SetP6LinkBreakMaterials(PMaterial barBreak,PMaterial jackBreak,PMaterial cableBreak);
	void SetLinksMaterials(PMaterial bar,PMaterial jackHead,PMaterial jackRetract,PMaterial jackExpand,PMaterial cable);
private:
	bool	mUpdateGraphicsEnabled;
	PMaterial mMatP6Bars;
	PMaterial mMatP6Jacks;
	PMaterial mMatP6Cables;
	P6PoolLinkBreak* mP6PoolBars;
	P6PoolLinkBreak* mP6PoolJacks;
	P6PoolLinkBreak* mP6PoolCables;
	PMaterial mMatBars;
	PMaterial mMatJackHead;
	PMaterial mMatJackRetract;
	PMaterial mMatJackExpand;
	PMaterial mMatCables;
public:
	ZMesh mLinkBarsMesh;
	ZMesh mLinkCablesMesh;
protected:
	PInstance mLinkBarsInstance;
	PInstance mLinkCablesInstance;


	// special effects
public:
	void FireParticleSystemLinkBarBreak(Link *l);
	void FireParticleSystemLinkCableBreak(Link *l);
	void FireParticleSystemLinkJackBreak(Link *l);

	// Links & nodes (containers)
public:
	void AddMobileNode(PNode n)		{mMobileNodes.Add(n);}
	void RemoveMobileNode(PNode n)	{mMobileNodes.Unlink(n);}
	void ClearMobileNodes()			{mMobileNodes.Unlink();}
	void AddLink(PLink l)			{mLinks.Add(l);}
	void RemoveLink(PLink l)		{mLinks.Unlink(l);}
	void ClearLinks()				{mLinks.Unlink();}
protected:
	ZList<PNode> mMobileNodes;
	ZList<PLink> mLinks;

	// updates
public:
	void UpdateSimulation(float dt);
};
