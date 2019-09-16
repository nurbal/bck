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

#include "Link.h"
#include "Level.h"
#include <GLES/gl.h>
#include <math.h>
#include "log.h"

Link::Link()
{
	mJavaNativeObject = 0;
	mNominalLength = 0;
	mBroken = false;
	mBreaking = false;
	mBreakTimer = 0;
	mSimulationFrameCounter = 0;
}

void Link::Init(PNode n1, PNode n2)
{
	mNodes[0] = n1;	n1->AddLink(this);
	mNodes[1] = n2; n2->AddLink(this);
	RecomputeInitialState();
}

void Link::Copy(Link *src)
{
	/*
	 * les lignes suivantes faisaient planter la copie d'un cable: l'appel de Init() est vital!
	mNodes[0] = src->mNodes[0];	mNodes[0]->AddLink(this);
	mNodes[1] = src->mNodes[1]; mNodes[1]->AddLink(this);
	mNominalLength = GetLength();
	*/
	Init(src->mNodes[0],src->mNodes[1]);
}

Node* Link::GetOtherNode(Node *n)
{
	if (n==mNodes[0])
		return mNodes[1];
	return mNodes[0];
}

void Link::ResetSimulation()
{
	mBroken = false;
	mBreaking = false;
}

bool Link::IsUnderwater()
{
	if (Level::instance->mHasWater)
		if (mNodes[0]->GetPosition(this)->z<Level::instance->mWaterHeight || mNodes[1]->GetPosition(this)->z<Level::instance->mWaterHeight)
			return true;
	return false;
}

float Link::GetLength()
{
	ZVector D;
	D.sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	return D.norme();
}

void Link::RecomputeInitialState()
{
	mNominalLength = GetLength();
	if (mNodes[0]->GetPosition(this)->x>mNodes[1]->GetPosition(this)->x)
	{
		PNode n = mNodes[0];
		mNodes[0] = mNodes[1];
		mNodes[1] = n;
	}
}

void Link::ReplaceNode(PNode from, PNode to, bool recomputeInitState)
{
	LOGD("Link::ReplaceNode this=%X from=%X to=%X n0=%X n1=%X from.fixed=%d to.fixed=%d",this,(Node*)from,(Node*)to,(Node*)mNodes[0],(Node*)mNodes[1],(int)from->mFixed,(int)to->mFixed);
	if ((Node*)mNodes[0] == (Node*)from)
	{
		LOGD("Link::ReplaceNode 0");
		mNodes[0]=to;
		from->RemoveLink(this);
		to->AddLink(this);
		if (recomputeInitState) RecomputeInitialState();
		LOGD("Link::ReplaceNode");
	}
	else if ((Node*)mNodes[1] == (Node*)from)
	{
		LOGD("Link::ReplaceNode 1");
		mNodes[1]=to;
		from->RemoveLink(this);
		to->AddLink(this);
		if (recomputeInitState) RecomputeInitialState();
		LOGD("Link::ReplaceNode");
	}
	LOGD("Link::ReplaceNode");

}

void Link::GetDirection(ZVector *v)
{
	v->sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	v->normalize();
	if (v->x<0) v->mul(-1);
}

void Link::GetPosition(ZVector *v)
{
	v->set(0.5f*(mNodes[0]->GetPosition(this)->x+mNodes[1]->GetPosition(this)->x),0.f,0.5f*(mNodes[0]->GetPosition(this)->z+mNodes[1]->GetPosition(this)->z));
}
