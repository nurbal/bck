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

/*
 * A fixed-point SRT transformer class
 *
 */

#include <jni.h>
#include <math.h>
#include <stdio.h>
#include "ZSRT.h"
#include "helpers.h"


ZSRT::ZSRT() : ZSmartObject()
{
	setIdentity();
}

void ZSRT::rebuildMatrix()
{
	if (!mDirty) return;
	mMatrix.setIdentity();
	// rotation/scale
	float x = mRotation.get(0);
	float y = mRotation.get(1);
	float z = mRotation.get(2);
	float w = mRotation.get(3);
	float x2 = 2*x*x;
	float y2 = 2*y*y;
	float z2 = 2*z*z;
	float xy = 2*x*y;
	float yz = 2*y*z;
	float xz = 2*z*x;
	float xw = 2*x*w;
	float yw = 2*y*w;
	float zw = 2*z*w;
	float sx = mScale.x;
	float sy = mScale.y;
	float sz = mScale.z;
	mMatrix.set(0,sx*(1.f-y2-z2));	mMatrix.set(4,sy*(xy-zw));		mMatrix.set(8,sz*(xz+yw));
	mMatrix.set(1,sx*(xy+zw));		mMatrix.set(5,sy*(1.f-x2-z2));	mMatrix.set(9,sz*(yz-xw));
	mMatrix.set(2,sx*(xz-yw));		mMatrix.set(6,sy*(yz+xw));		mMatrix.set(10,sz*(1.f-x2-y2));
	// translation
	mMatrix.set(12,mTranslation.x);
	mMatrix.set(13,mTranslation.y);
	mMatrix.set(14,mTranslation.z);
	mDirty = false;
}

ZMatrix* ZSRT::getMatrix()
{
	rebuildMatrix();
	return &mMatrix;
}

void ZSRT::setTranslation(const ZVector* t)
{
	mTranslation.copy(t);
	mDirty=true;
}

void ZSRT::setTranslation(float x, float y, float z)
{
	mTranslation.set(x, y, z);
	mDirty=true;
}

void ZSRT::setRotation(const ZQuaternion* r)
{
	mRotation.copy(r);
	mDirty=true;
}

void ZSRT::setRotation(const ZVector* axe, float angle)
{
	mRotation.set(axe,angle);
	mDirty=true;
}

void ZSRT::setScale(const ZVector* s)
{
	mScale.copy(s);
	mDirty=true;
}

void ZSRT::setScale(float x, float y, float z)
{
	mScale.set(x, y, z);
	mDirty=true;
}

void ZSRT::setIdentity()
{
	mTranslation.zero();
	mScale.set(1.f,1.f,1.f);
	mRotation.zero();
	mDirty=true;
}

void ZSRT::translate(const ZVector* t)
{
	mTranslation.add(t);
	mDirty=true;
}

void ZSRT::translate(float x, float y, float z)
{
	mTranslation.add(x, y, z);
	mDirty=true;
}

void ZSRT::rotate(const ZQuaternion* r)
{
	mRotation.rotate(r);
	mDirty=true;
}

void ZSRT::rotate(const ZVector* axe, float angle)
{
	mRotation.rotate(axe,angle);
	mDirty=true;
}

void ZSRT::rotate(const ZVector* r)
{
	mRotation.rotate(r);
	mDirty=true;
}

