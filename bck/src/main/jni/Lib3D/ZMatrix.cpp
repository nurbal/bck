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
#include "ZMatrix.h"
#include "helpers.h"

ZMatrix::ZMatrix() : ZSmartObject()
{
	setIdentity();
}

void ZMatrix::copy(ZMatrix *other)
{
	for (int i=0; i<16; i++) mCoefs[i] = other->mCoefs[i];
}

void ZMatrix::setIdentity()
{
	mCoefs[0]=1.f;	mCoefs[4]=0;	mCoefs[8]=0;	mCoefs[12]=0;
	mCoefs[1]=0;	mCoefs[5]=1.f;	mCoefs[9]=0;	mCoefs[13]=0;
	mCoefs[2]=0;	mCoefs[6]=0;	mCoefs[10]=1.f;	mCoefs[14]=0;
	mCoefs[3]=0;	mCoefs[7]=0;	mCoefs[11]=0;	mCoefs[15]=1.f;

}

float* ZMatrix::getOpenGLMatrix()
{
	return mCoefs;
}

void ZMatrix::set(int index,float value)
{
	mCoefs[index]=value;
}

float ZMatrix::get(int index)
{
	return mCoefs[index];
}

void ZMatrix::transform(ZVector* in,ZVector* out)
{
	out->set(get(0)*in->x + get(4)*in->y + get(8)*in->z + get(12),
			get(1)*in->x + get(5)*in->y + get(9)*in->z + get(13),
			get(2)*in->x + get(6)*in->y + get(10)*in->z + get(14)
			);
}

void ZMatrix::initFromBase(const ZVector *x,const ZVector *y,const ZVector *z,const ZVector *scale,const ZVector *translation)
{
	mCoefs[0] = x->x*scale->x;
	mCoefs[1] = x->y*scale->x;
	mCoefs[2] = x->z*scale->x;
	mCoefs[3] = 0.f;

	mCoefs[4] = y->x*scale->y;
	mCoefs[5] = y->y*scale->y;
	mCoefs[6] = y->z*scale->y;
	mCoefs[7] = 0.f;

	mCoefs[8] = z->x*scale->z;
	mCoefs[9] = z->y*scale->z;
	mCoefs[10] = z->z*scale->z;
	mCoefs[11] = 0.f;

	mCoefs[12] = translation->x;
	mCoefs[13] = translation->y;
	mCoefs[14] = translation->z;
	mCoefs[15] = 1.f;
}

bool ZMatrix::getInverseMatrix(ZMatrix *inv)
{

	float A0 = mCoefs[0]*mCoefs[5] - mCoefs[1]*mCoefs[4];	//m(0,0)*m(1,1) - m(0,1)*m(1,0)
	float A1 = mCoefs[0]*mCoefs[6] - mCoefs[2]*mCoefs[4];	//m(0,0)*m(1,2) - m(0,2)*m(1,0)
	float A2 = mCoefs[0]*mCoefs[7] - mCoefs[3]*mCoefs[4];	//m(0,0)*m(1,3) - m(0,3)*m(1,0);
	float A3 = mCoefs[1]*mCoefs[6] - mCoefs[2]*mCoefs[5];	// m(0,1)*m(1,2) - m(0,2)*m(1,1);
    float A4 = mCoefs[1]*mCoefs[7] - mCoefs[3]*mCoefs[5];	// m(0,1)*m(1,3) - m(0,3)*m(1,1);
	float A5 = mCoefs[2]*mCoefs[7] - mCoefs[3]*mCoefs[6];	//m(0,2)*m(1,3) - m(0,3)*m(1,2);

	float B0 = mCoefs[8]*mCoefs[13] - mCoefs[9]*mCoefs[12];	// m(2,0)*m(3,1) - m(2,1)*m(3,0);
    float B1 = mCoefs[8]*mCoefs[14] - mCoefs[10]*mCoefs[12];	// m(2,0)*m(3,2) - m(2,2)*m(3,0);
	float B2 = mCoefs[8]*mCoefs[15] - mCoefs[11]*mCoefs[12];	// m(2,0)*m(3,3) - m(2,3)*m(3,0);
	float B3 = mCoefs[9]*mCoefs[14] - mCoefs[10]*mCoefs[13];	// m(2,1)*m(3,2) - m(2,2)*m(3,1);
	float B4 = mCoefs[9]*mCoefs[15] - mCoefs[11]*mCoefs[13];	// m(2,1)*m(3,3) - m(2,3)*m(3,1);
	float B5 = mCoefs[10]*mCoefs[15] - mCoefs[11]*mCoefs[14];	// m(2,2)*m(3,3) - m(2,3)*m(3,2);

    float det = A0*B5 - A1*B4 + A2*B3 + A3*B2 - A4*B1 + A5*B0;
	if (fabs (det) < FEPSILON)
		return false;

	det = 1.f/det;
	inv->mCoefs[0] = det*(+mCoefs[5]*B5 - mCoefs[6]*B4 + mCoefs[7]*B3);	// + m(1,1)*fB5 - m(1,2)*fB4 + m(1,3)*fB3
	inv->mCoefs[4] = det*(-mCoefs[4]*B5 + mCoefs[6]*B2 - mCoefs[7]*B1);	// - m(1,0)*fB5 + m(1,2)*fB2 - m(1,3)*fB1
	inv->mCoefs[8] = det*(+mCoefs[4]*B4 - mCoefs[5]*B2 + mCoefs[7]*B0);	// + m(1,0)*fB4 - m(1,1)*fB2 + m(1,3)*fB0
	inv->mCoefs[12] = det*(-mCoefs[4]*B3 + mCoefs[5]*B1 - mCoefs[6]*B0);	// - m(1,0)*fB3 + m(1,1)*fB1 - m(1,2)*fB0

	inv->mCoefs[1] = det*(-mCoefs[1]*B5 + mCoefs[2]*B4 - mCoefs[3]*B3);	// - m(0,1)*fB5 + m(0,2)*fB4 - m(0,3)*fB3
	inv->mCoefs[5] = det*(+mCoefs[0]*B5 - mCoefs[2]*B2 + mCoefs[3]*B1);	// + m(0,0)*fB5 - m(0,2)*fB2 + m(0,3)*fB1
	inv->mCoefs[9] = det*(-mCoefs[0]*B4 + mCoefs[1]*B2 - mCoefs[3]*B0);	// - m(0,0)*fB4 + m(0,1)*fB2 - m(0,3)*fB0
	inv->mCoefs[13] = det*(+mCoefs[0]*B3 - mCoefs[1]*B1 + mCoefs[2]*B0);	// + m(0,0)*fB3 - m(0,1)*fB1 + m(0,2)*fB0

	inv->mCoefs[2] = det*(+mCoefs[13]*A5 - mCoefs[14]*A4 + mCoefs[15]*A3);	// + m(3,1)*fA5 - m(3,2)*fA4 + m(3,3)*fA3
	inv->mCoefs[6] = det*(-mCoefs[12]*A5 + mCoefs[14]*A2 - mCoefs[15]*A1);	// - m(3,0)*fA5 + m(3,2)*fA2 - m(3,3)*fA1
	inv->mCoefs[10] = det*(+mCoefs[12]*A4 - mCoefs[13]*A2 + mCoefs[15]*A0);	// + m(3,0)*fA4 - m(3,1)*fA2 + m(3,3)*fA0
	inv->mCoefs[14] = det*(-mCoefs[12]*A3 + mCoefs[13]*A1 - mCoefs[14]*A0);	// - m(3,0)*fA3 + m(3,1)*fA1 - m(3,2)*fA0

	inv->mCoefs[3] = det*(-mCoefs[9]*A5 + mCoefs[10]*A4 - mCoefs[11]*A3);	// - m(2,1)*fA5 + m(2,2)*fA4 - m(2,3)*fA3
	inv->mCoefs[7] = det*(+mCoefs[8]*A5 - mCoefs[10]*A2 + mCoefs[11]*A1);	// + m(2,0)*fA5 - m(2,2)*fA2 + m(2,3)*fA1
	inv->mCoefs[11] = det*(-mCoefs[8]*A4 + mCoefs[9]*A2 - mCoefs[11]*A0);	// - m(2,0)*fA4 + m(2,1)*fA2 - m(2,3)*fA0
	inv->mCoefs[15] = det*(+mCoefs[8]*A3 - mCoefs[9]*A1 + mCoefs[10]*A0);	// + m(2,0)*fA3 - m(2,1)*fA1 + m(2,2)*fA0

	return true;
}
