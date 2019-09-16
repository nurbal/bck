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

#ifndef __ZVECTOR__
#define __ZVECTOR__


// Classe ZVector
// en code natif, toutes les donn�es sont en virgule fixe!

#include "ZSmartPtr.h"
#include "release_settings.h"

class ZVector;
#define PVector ZSmartPtr<ZVector>

class ZVector : public ZSmartObject
{
public:
	
	static const ZVector* Null();	// 0 0 0
	static const ZVector* XYZ();	// 1 1 1
	static const ZVector* X();
	static const ZVector* Y();
	static const ZVector* Z();
	static const ZVector* Xneg();
	static const ZVector* Yneg();
	static const ZVector* Zneg();

	ZVector();
	ZVector(float X, float Y, float Z);
	virtual ~ZVector();
	
	void copy(const ZVector* other);
	bool isEqual(const ZVector* other) const;

	// setters
	void set(float X, float Y, float Z);
//	void set(int index, int value);
	// getters
//	int get(int index) const;

	// basic vector operations
	void add(const ZVector* other);
	void add(float X, float Y, float Z);
//	void add(int index, int value);

	void add(const ZVector* v1,const ZVector* v2);

	void sub(const ZVector* other);
	void sub(const ZVector* v1,const ZVector* v2);
	float dot(const ZVector* other) const;
	void cross(const ZVector* a,const ZVector* b);

	void mul(float f) ;
	void mul(const ZVector* v, float f);
	void addMul(const ZVector* v, float f);
	void mulAddMul(const ZVector* v1, float f1, const ZVector* v2, float f2);
	void mulAddMul(const ZVector* v1, float f1, const ZVector* v2, float f2, const ZVector* v3, float f3);
	float norme() const;
	void normalize();

	void zero();

	void faceNormal(const ZVector* a,const ZVector* b,const ZVector* c);

	void faceClosestPoint(const ZVector* A,const ZVector* B,const ZVector* C,const ZVector* V);
	float faceProjectedPoint(const ZVector* A,const ZVector* B,const ZVector* C,const ZVector* From, const ZVector* Direction);

	void interpolateSoft(const ZVector* start, const ZVector* end, float coef);
	void interpolateLinear(const ZVector* start, const ZVector* end, float coef);

public:
	//float mCoefs[3];
	float x,y,z;
};

#endif // __ZVECTOR__
