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
 * A fixed-point vector class
 *
 */

#include <jni.h>
#include <math.h>
#include "ZQuaternion.h"
#include "helpers.h"

ZQuaternion::ZQuaternion() : ZSmartObject()
{
	zero();
}

ZQuaternion::~ZQuaternion()
{

}

ZQuaternion::ZQuaternion(float X, float Y, float Z, float W) : ZSmartObject()
{
	set(X,Y,Z,W);
}

ZQuaternion::ZQuaternion(const ZVector* v,float angle) : ZSmartObject()
{
	set(v,angle);
}

void ZQuaternion::zero()
{
	set(0,0,0,1.f);
}

void ZQuaternion::set(float X, float Y, float Z, float W)
{
	mCoefs[0]=X;
	mCoefs[1]=Y;
	mCoefs[2]=Z;
	mCoefs[3]=W;
}
void ZQuaternion::set(int index, float value)
{
	mCoefs[index]=value;
}
float ZQuaternion::get(int index)
{
	return mCoefs[index];
}

void ZQuaternion::copy(const ZQuaternion* other)
{
	set(other->mCoefs[0],other->mCoefs[1],other->mCoefs[2],other->mCoefs[3]);
}

void ZQuaternion::set(const ZVector* v,float angle)
{
	float n = v->norme();
	if (n<FEPSILON)
	{
		zero();
		return;
	}
	float s = sinf(angle*n*0.5f);
	float c = cosf(angle*n*0.5f);
	float invNorme = 1.f/n;
	set(v->x*invNorme*s,
			v->y*invNorme*s,
			v->z*invNorme*s,
			c);
}

void ZQuaternion::set(const ZVector* v)
{
	float angle = v->norme();
	if (angle<FEPSILON)
	{
		zero();
		return;
	}
	float s = sinf(angle*0.5f);
	float c = cosf(angle*0.5f);
	float invNorme = 1.f/angle;
	set(v->x*invNorme*s,
			v->y*invNorme*s,
			v->z*invNorme*s,
			c);
}

void ZQuaternion::rotate(const ZQuaternion* q)
{
	static ZQuaternion tmp;
	tmp.mul(q, this);
	copy(&tmp);
}

void ZQuaternion::rotate(const ZVector* v,float angle)
{
	ZQuaternion q(v,angle);
	rotate(&q);
}

void ZQuaternion::rotate(const ZVector* v)
{
	ZQuaternion q(v,1.f);
	rotate(&q);
}

void ZQuaternion::mul(const ZQuaternion* a, const ZQuaternion* b)
{
	mCoefs[0] = (a->mCoefs[3]*b->mCoefs[0]) + (a->mCoefs[0]*b->mCoefs[3]) + (a->mCoefs[1]*b->mCoefs[2]) - (a->mCoefs[2]*b->mCoefs[1]);
	mCoefs[1] = (a->mCoefs[3]*b->mCoefs[1]) + (a->mCoefs[1]*b->mCoefs[3]) + (a->mCoefs[2]*b->mCoefs[0]) - (a->mCoefs[0]*b->mCoefs[2]);
	mCoefs[2] = (a->mCoefs[3]*b->mCoefs[2]) + (a->mCoefs[2]*b->mCoefs[3]) + (a->mCoefs[0]*b->mCoefs[1]) - (a->mCoefs[1]*b->mCoefs[0]);
	mCoefs[3] = (a->mCoefs[3]*b->mCoefs[3]) - (a->mCoefs[0]*b->mCoefs[0]) - (a->mCoefs[1]*b->mCoefs[1]) - (a->mCoefs[2]*b->mCoefs[2]);
}

void ZQuaternion::normalize()
{
	float invNorme = 1.f/sqrtf(mCoefs[0]*mCoefs[0] + mCoefs[1]*mCoefs[1] + mCoefs[2]*mCoefs[2] + mCoefs[3]*mCoefs[3]);
	mCoefs[0] *= invNorme;
	mCoefs[1] *= invNorme;
	mCoefs[2] *= invNorme;
	mCoefs[3] *= invNorme;
}
