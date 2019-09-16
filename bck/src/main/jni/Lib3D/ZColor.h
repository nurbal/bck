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

#ifndef __ZCOLOR__
#define __ZCOLOR__


// Classe ZVector
// en code natif, toutes les donn�es sont en virgule fixe!

#include "ZSmartPtr.h"
#include "release_settings.h"

class ZColor;
#define PColor ZSmartPtr<ZColor>

class ZColor : public ZSmartObject
{
public:

	static const ZColor* R();
	static const ZColor* G();
	static const ZColor* B();
	static const ZColor* A();
	static const ZColor* WHITE();
	static const ZColor* BLACK();
	static const ZColor* TRANSPARENT();

	ZColor();
	ZColor(float R,float G,float B,float A);
	virtual ~ZColor();
	
	void copy(const ZColor* other);
	bool isEqual(const ZColor* other) const;

	// setters
	void set(float R,float G,float B,float A);

	// basic vector operations
	void add(const ZColor* other);
	void add(const ZColor* v1,const ZColor* v2);

	void sub(const ZColor* other);
	void sub(const ZColor* v1,const ZColor* v2);

	void mul(float f) ;
	void mul(const ZColor* v) ;
	void mul(const ZColor* c1,const ZColor* c2) ;
	void mul(const ZColor* v, float f);
	void addMul(const ZColor* v, float f);
	void mulAddMul(const ZColor* v1, float f1, const ZColor* v2, float f2);
	void clamp();	// clamps all values between [0..1]

	void SetBlack();
	void SetWhite();

	void interpolateSoft(const ZColor* start, const ZColor* end, float coef);
	void interpolateLinear(const ZColor* start, const ZColor* end, float coef);

public:
	float r,g,b,a;
};

#endif // __ZCOLOR__
