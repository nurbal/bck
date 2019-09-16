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

#ifndef __ZSRT__
#define __ZSRT__


// Classe ZQuaternion
// en code natif, toutes les donn�es sont en virgule fixe!

#include "ZSmartPtr.h"
#include "ZVector.h"
#include "ZQuaternion.h"
#include "ZMatrix.h"
#include "release_settings.h"

class ZSRT;
#define PSRT ZSmartPtr<ZSRT>

class ZSRT : public ZSmartObject
{
public:
	ZSRT();
	
protected:
	ZMatrix mMatrix;
	ZVector mTranslation;
	ZVector mScale;
	ZQuaternion mRotation;
	bool mDirty;

	void rebuildMatrix();
	
public:
	ZMatrix* getMatrix();

	void setTranslation(const ZVector* t);
	const ZVector* getTranslation() const {return &mTranslation;}
	void setTranslation(float x, float y, float z);
	void translate(const ZVector* t);
	void translate(float x, float y, float z);

	void setRotation(const ZQuaternion* r);
	void setRotation(const ZVector* axe, float angle);
	void rotate(const ZQuaternion* r);
	void rotate(const ZVector* axe, float angle);
	void rotate(const ZVector* r);

	void setScale(const ZVector* s);
	void setScale(float x, float y, float z);

	void setIdentity();

};





#endif // __ZSRT__
