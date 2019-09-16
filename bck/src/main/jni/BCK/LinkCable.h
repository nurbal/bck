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
#include "Link.h"

class LinkCableNode;
class LinkCableSegment;

class LinkCable : public Link
{
public:
	LinkCable();
	virtual ~LinkCable();

	virtual bool Is(Type t) {return t==cable;}

	virtual void Init(PNode n1, PNode n2);
	virtual void RecomputeInitialState();

	virtual float GetWeight() { return CABLE_DENSITY*mNominalLength; }

	virtual void StartSimulationFrame(float dt);
	virtual bool UpdateSimulation(float dt);
	virtual void ResetSimulation();

	virtual bool IsBroken();
	virtual void SetBroken();
	virtual float GetStressFactor();
private:
	float GetNominalLengthFactor();

	void CreateSegments();
	void DestroySegments();
	LinkCableNode** 	mCableNodes;
	LinkCableSegment**	mCableSegments;

	bool mIs3D;


	// graphics
public:
	virtual void InitGraphics(ZScene *scene,bool is3D);
	virtual void ResetGraphics(ZScene *scene);
	virtual void UpdateGraphics(int elpaseTime);
};




class LinkCableNode
{
public:
	LinkCableNode();

	bool mFixed;
	ZVector mPosition;
	ZVector mPrevPosition;

	void setPosition(const ZVector *pos);
	void setPosition(float x, float y, float z);

	void update(float dt);
};

class LinkCableSegment
{
public:
	LinkCableSegment(LinkCableNode *n1, LinkCableNode *n2, float length);

	LinkCableNode* mNodes[2];
	float mNominalLength;
	bool mBroken;

	void updateGraphics(bool is3D,ZMesh *mesh);
	void update();

};


