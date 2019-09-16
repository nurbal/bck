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
#include "ZVector.h"
#include "helpers.h"

static ZVector constNull(0,0,0);
static ZVector constXYZ(1.f,1.f,1.f);
static ZVector constX(1.f,0,0);
static ZVector constY(0,1.f,0);
static ZVector constZ(0,0,1.f);
static ZVector constXneg(-1.f,0,0);
static ZVector constYneg(0,-1.f,0);
static ZVector constZneg(0,0,-1.f);
const ZVector* ZVector::Null() {return &constNull;}
const ZVector* ZVector::XYZ() {return &constXYZ;}
const ZVector* ZVector::X() {return &constX;}
const ZVector* ZVector::Y() {return &constY;}
const ZVector* ZVector::Z() {return &constZ;}
const ZVector* ZVector::Xneg() {return &constXneg;}
const ZVector* ZVector::Yneg() {return &constYneg;}
const ZVector* ZVector::Zneg() {return &constZneg;}

ZVector::ZVector() : ZSmartObject()
{
	x=y=z=0;
}
ZVector::ZVector(float X, float Y, float Z)
{
	x=X;
	y=Y;
	z=Z;
}

ZVector::~ZVector()
{

}


// setters
void ZVector::set(float X, float Y, float Z)
{
	x=X;
	y=Y;
	z=Z;
}
/*
void ZVector::set(int index, int value)
{
	mCoefs[index]=value;
}

// getters
int ZVector::get(int index) const { return mCoefs[index]; }
*/
// basic vector operations
void ZVector::add(const ZVector* other)
{
	x += other->x;
	y += other->y;
	z += other->z;
}
void ZVector::add(float X, float Y, float Z)
{
	x += X;
	y += Y;
	z += Z;
}
/*
void ZVector::add(int index, int value)
{
	mCoefs[index] += value;
}
*/
void ZVector::add(const ZVector* v1,const ZVector* v2)
{
	x = v1->x + v2->x;
	y = v1->y + v2->y;
	z = v1->z + v2->z;
}

void ZVector::sub(const ZVector* other)
{
	x -= other->x;
	y -= other->y;
	z -= other->z;
}
void ZVector::sub(const ZVector* v1,const ZVector* v2)
{
	x = v1->x - v2->x;
	y = v1->y - v2->y;
	z = v1->z - v2->z;
}

float ZVector::dot(const ZVector* other) const
{
	return ((x*other->x)
			+ (y*other->y)
			+ (z*other->z));
}
void ZVector::cross(const ZVector* a,const ZVector* b)
{
	x = (a->y*b->z) - (a->z*b->y);
	y = (a->z*b->x) - (a->x*b->z);
	z = (a->x*b->y) - (a->y*b->x);
}

void ZVector::mul(float f)
{
	x *= f;
	y *= f;
	z *= f;
}
void ZVector::mul(const ZVector* v, float f)
{
	x = (v->x*f);
	y = (v->y*f);
	z = (v->z*f);
}
void ZVector::addMul(const ZVector* v, float f)
{
	x += (v->x*f);
	y += (v->y*f);
	z += (v->z*f);
}

void ZVector::mulAddMul(const ZVector* v1, float f1, const ZVector* v2, float f2)
{
	x = (v1->x*f1) + (v2->x*f2);
	y = (v1->y*f1) + (v2->y*f2);
	z = (v1->z*f1) + (v2->z*f2);
}

void ZVector::mulAddMul(const ZVector* v1, float f1, const ZVector* v2, float f2, const ZVector* v3, float f3)
{
	x = (v1->x*f1) + (v2->x*f2) + (v3->x*f3);
	y = (v1->y*f1) + (v2->y*f2) + (v3->y*f3);
	z = (v1->z*f1) + (v2->z*f2) + (v3->z*f3);
}

void ZVector::copy(const ZVector* other)
{
	x = other->x;
	y = other->y;
	z = other->z;
}
float ZVector::norme() const
{
	return sqrtf(x*x + y*y + z*z);
}
void ZVector::normalize()
{
	mul(1.f/norme());
}

void ZVector::zero()
{
	set(0,0,0);
}

// Calcul de la normale � une face (sens direct)
void ZVector::faceNormal(const ZVector* a,const ZVector* b,const ZVector* c)
{
	static ZVector AB,BC;
	AB.sub(b,a);
	BC.sub(c,b);
	cross(&AB,&BC);
	normalize();
}

	static ZVector AB,BC,CA,Xa,Xb,Xc,Ya,Yb,Yc,N,AV,P,AP,BP,CP;

void ZVector::faceClosestPoint(const ZVector* A,const ZVector* B,const ZVector* C,const ZVector* V)
{

	AB.sub(B,A); float ab=AB.norme();
	BC.sub(C,B); float bc=BC.norme();
	CA.sub(A,C); float ca=CA.norme();

	Ya.mul(&BC,1.f/bc);
	Yb.mul(&CA,1.f/ca);
	Yc.mul(&AB,1.f/ab);

	N.cross(&Ya,&Yb); N.normalize();

	Xa.cross(&Ya,&N);
	Xb.cross(&Yb,&N);
	Xc.cross(&Yc,&N);

	// projet� sur la face
	AV.sub(V,A);
	P.copy(V); P.addMul(&N, -AV.dot(&N));

	AP.sub(&P,A);
	BP.sub(&P,B);
	CP.sub(&P,C);

	float xa = Xa.dot(&BP);
	float xb = Xb.dot(&CP);
	float xc = Xc.dot(&AP);

	// int�rieur � la face
	if (xa<0 && xb<0 && xc<0)
		{copy(&P); return;}

	float ya=Ya.dot(&BP);
	float yb=Yb.dot(&CP);
	float yc=Yc.dot(&AP);

	// A, B ou C
	if (yb>=ca && yc<=0) {copy(A); return;}
	if (yc>=ab && ya<=0) {copy(B); return;}
	if (ya>=bc && yb<=0) {copy(C); return;}
	// sur AB
	if (xc>0 && yc>=0 && yc<=ab)
	{
		copy(A);
		addMul(&Yc,yc);
		return;
	}
	// sur BC
	if (xa>0 && ya>=0 && ya<=bc)
	{
		copy(B);
		addMul(&Ya,ya);
		return;
	}
	// sur CA
	//if (xb>0.f && yb>=0.f && yb<=ca)
	{
		copy(C);
		addMul(&Yb,yb);
	}

}

float ZVector::faceProjectedPoint(const ZVector* A,const ZVector* B,const ZVector* C,const ZVector* From, const ZVector* Direction)
{
	// cotés
	AB.sub(B,A); float ab=AB.norme();
	BC.sub(C,B); float bc=BC.norme();
	CA.sub(A,C); float ca=CA.norme();
	Ya.mul(&BC,1.f/bc);
	Yb.mul(&CA,1.f/ca);
	Yc.mul(&AB,1.f/ab);
	// normale
	N.cross(&Ya,&Yb); N.normalize();

	// point dans le plan de la face
	// projet� sur la face
	AV.sub(From,A);
	float DirN = N.dot(Direction);
	if (DirN>=0)
		return -1.f;
	float dist =  -AV.dot(&N)/DirN;
	P.copy(From); P.addMul(Direction,dist);

	// perpendiculaires extérieures
	Xa.cross(&Ya,&N);
	Xb.cross(&Yb,&N);
	Xc.cross(&Yc,&N);

	AP.sub(&P,A);
	BP.sub(&P,B);
	CP.sub(&P,C);

	float xa = Xa.dot(&BP);
	float xb = Xb.dot(&CP);
	float xc = Xc.dot(&AP);

	// int�rieur � la face
	if (xa<0 && xb<0 && xc<0)
		{copy(&P); return dist;}

	return -1.f;
}

void ZVector::interpolateSoft(const ZVector* start, const ZVector* end, float coef)
{
	coef = 0.5f*(1.f-cosf(coef*3.1415926f));
	interpolateLinear(start,end,coef);
}

void ZVector::interpolateLinear(const ZVector* start, const ZVector* end, float coef)
{
	mulAddMul(start,1.f-coef,end,coef);
}

bool ZVector::isEqual(const ZVector* other) const
{
	return (x==other->x && y==other->y && z==other->z);
}


