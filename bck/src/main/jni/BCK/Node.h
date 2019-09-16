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

#ifndef __NODE_H__
#define __NODE_H__

#include "../Lib3D/Lib3D.h"

class Node;
#define PNode ZSmartPtr<Node>

#include "Link.h"

#include "release_settings.h"


class Node : public ZSmartObject
{
public:
	Node();
	~Node();

	unsigned long mJavaNativeObject;

	void Copy(Node* n);

public:
	void AddLink(Link* l);
	void RemoveLink(Link* l);
private:
	void RecomputeLinksWeight();

public:
	void SetPosition(float x, float z);
	void Fusion(Node *target);

	void AddDynamicWeight(float w,Link *l);
	void ResetDynamicWeight();
	float GetWeight(Link * l) const; // si l=null, c'est demand� par l'�diteur...

	void UpdateSimulation(float dt);
	void ResetSimulation();

	void Move(ZVector *v,Link * l); // si l=null, c'est demand� par l'�diteur...

	bool IsOnRailsHeight() const;

	const ZVector* GetPosition(Link * l) const; // si l=null, c'est demand� par l'�diteur...
	const ZVector* GetResetPosition() const {return &mResetPosition;}

	int GetNbLinks() const {return mLinks.GetNbElements();}
	const Link* GetLink(int index) const {return mLinks.Get(index);}

public:
	bool mFixed;
private:
	ZVector	mPosition;
	ZVector	mPrevPosition;

	ZVector	mResetPosition;

	ZList<Link*> mLinks;
	float mLinksWeight;
	float mDynamicWeight;

	// separation nodes...
public:
	void SeparateNode(bool separate);
	void SetSeparator(float angle);	// direction de s�paration, pas de la fronti�re
	void UnsetSeparator();
	bool IsSeparator() const {return mIsSeparator;}
	float GetSeparatorAngle() const {return mSeparatorAngle;}
	int mSaveID;

private:
	ZVector	mPositionSeparated;
	ZVector	mPrevPositionSeparated;
	float mLinksWeightSeparated;
	float mDynamicWeightSeparated;

	bool mIsSeparator;
	ZVector mSeparatorDirection;
	float mSeparatorAngle;
	bool mIsSeparated;
	bool mLockWanted;
	ZList<Link*> mLinksSeparated;
};

#endif //__NODE_H__
