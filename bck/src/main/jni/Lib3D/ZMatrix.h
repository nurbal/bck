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

#ifndef __ZMATRIX__
#define __ZMATRIX__


// Classe ZMatrix
// 0  4  8  12
// 1  5  9  13
// 2  6  10 14
// 3  7  11 15


#include "ZSmartPtr.h"
#include "ZVector.h"
#include "ZQuaternion.h"
#include "release_settings.h"

class ZMatrix;
#define PMatrix ZSmartPtr<ZMatrix>

class ZMatrix : public ZSmartObject
{
public:
	ZMatrix();
	
	void copy(ZMatrix *other);
	void setIdentity();
	float* getOpenGLMatrix();	// OpenGL matrix
	void set(int index,float value);
	float get(int index);
	void transform(ZVector* in,ZVector* out);

	void initFromBase(const ZVector *x,const ZVector *y,const ZVector *z,const ZVector *scale,const ZVector *translation);
	bool getInverseMatrix(ZMatrix *inv);

protected:
	float mCoefs[16]; // fixed-point
};





#endif // __ZMATRIX__
