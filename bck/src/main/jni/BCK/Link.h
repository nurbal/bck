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

#ifndef __LINK_H__
#define __LINK_H__

#include "../Lib3D/Lib3D.h"

class Link;
#define PLink ZSmartPtr<Link>

#include "release_settings.h"
#include "Node.h"


class Node;



class Link : public ZSmartObject
{
public:
	Link();

	enum Type
	{
		bar,
		cable,
		jack
	};
	virtual bool Is(Type t) = 0;

	unsigned long mJavaNativeObject;

	virtual void Init(PNode n1, PNode n2);
	virtual void Copy(Link *src);

	virtual void ResetSimulation();
	virtual void RecomputeInitialState();
	void ReplaceNode(PNode from, PNode to, bool recomputeInitState);
	PNode GetNode(int index) {return mNodes[index];}
	Node* GetOtherNode(Node *n);
	virtual float GetWeight() = 0;

	virtual float GetLength();
	virtual float GetNominalLength() {return mNominalLength;}

	virtual void StartSimulationFrame(float dt) = 0;
	virtual bool UpdateSimulation(float dt) = 0; 	// dt = total frame time. returns true if more updates necessary (stiffness-dependant)

	virtual bool IsBroken() = 0;
	virtual void SetBroken() = 0;
	virtual float GetStressFactor() = 0;			// returns >=1 if "about to break"

	void GetDirection(ZVector *v);
	void GetPosition(ZVector *v);

	bool IsUnderwater();

	// graphics
public:
	virtual void InitGraphics(ZScene *scene,bool is3D) = 0;
	virtual void ResetGraphics(ZScene *scene) = 0;
	virtual void UpdateGraphics(int elpaseTime) = 0;


protected:
	PNode mNodes[2];
	float mNominalLength;
	bool mBroken;
	bool mBreaking;
	float mBreakTimer;

	int mSimulationFrameCounter;

};


#endif //__LINK_H__
