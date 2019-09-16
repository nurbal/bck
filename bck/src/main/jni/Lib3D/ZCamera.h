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

#ifndef __ZCAMERA__
#define __ZCAMERA__

#include "ZSmartPtr.h"
#include "release_settings.h"

#define ZCAMERA_TYPE_UNDEFINED				0
#define ZCAMERA_TYPE_FRUSTUM				1
#define ZCAMERA_TYPE_ORTHO					2

class ZVector;

class ZCamera;
#define PCamera ZSmartPtr<ZCamera>

class ZCamera : public ZSmartObject
{
public:
	ZCamera();

	virtual bool GetType() {return ZCAMERA_TYPE_UNDEFINED;}

	virtual const ZVector* getCameraFront() const {return 0;}
	virtual const ZVector* getCameraRight() const {return 0;}
	virtual const ZVector* getCameraUp() const {return 0;}
	virtual const ZVector* getCameraEye() const {return 0;}

	virtual void computeProjection() {}	// frustum, cone, ortho matrix, whatever
	virtual void computePosition() {}	// eye position, lookat

};

#endif // __ZCAMERA__
