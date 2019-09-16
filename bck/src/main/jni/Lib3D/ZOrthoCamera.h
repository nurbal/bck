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

#ifndef __ZORTHOCAMERA__
#define __ZORTHOCAMERA__

#include "ZCamera.h"
#include "ZVector.h"
#include "release_settings.h"

class ZOrthoCamera : public ZCamera
{
public:
	ZOrthoCamera();

	virtual bool GetType() {return ZCAMERA_TYPE_ORTHO;}

	virtual void Log() {};

	virtual const ZVector* getCameraFront() const {return &mCameraFront;}
	virtual const ZVector* getCameraRight() const {return &mCameraRight;}
	virtual const ZVector* getCameraUp() const {return &mCameraUp;}
	virtual const ZVector* getCameraEye() const {return &mCameraUp;}

	virtual void computeProjection();
	virtual void computePosition();

	void setParameters(float near, float far,float viewportWidthFactor, float viewportHeightFactor, const ZVector* pos,const ZVector* front,const ZVector* right,const ZVector* up);

protected:
	ZVector	mCameraEye;

	ZVector	mCameraFront;
	ZVector	mCameraRight;
	ZVector	mCameraUp;

	float		mNearClippingPlane;
	float		mFarClippingPlane;
	float 		mClippingPlaneHalfWidth;
	float 		mClippingPlaneHalfHeight;

};

#endif // __ZORTHOCAMERA__
