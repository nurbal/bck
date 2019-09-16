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

#ifndef __ZQUATERNION__
#define __ZQUATERNION__


// Classe ZQuaternion
// en code natif, toutes les donn�es sont en virgule fixe!

#include "ZSmartPtr.h"
#include "ZVector.h"
#include "release_settings.h"

class ZQuaternion;
#define PQuaternion ZSmartPtr<ZQuaternion>

class ZQuaternion : public ZSmartObject
{
public:
	
	ZQuaternion();
	ZQuaternion(float X, float Y, float Z, float W);
	ZQuaternion(const ZVector* v, float angle);
	virtual ~ZQuaternion();
	
	void zero();
	void set(float X, float Y, float Z, float W);
	void set(int index, float value);
	float get(int index);
	void copy(const ZQuaternion* other);
	void set(const ZVector* v,float angle);
	void set(const ZVector* v);
	void normalize();
	void mul(const ZQuaternion* a, const ZQuaternion* b);
	void rotate(const ZQuaternion* q);
	void rotate(const ZVector* v,float angle);
	void rotate(const ZVector* v);

	const char* getLogString();	// get the quaternion in a human-readable format

protected:
	float mCoefs[4];
	
};





#endif // __ZQUATERNION__
