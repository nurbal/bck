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

#ifndef __ZFRUSTUMCAMERA__
#define __ZFRUSTUMCAMERA__

#include "ZCamera.h"
#include "ZVector.h"
#include "release_settings.h"

class ZFrustumCamera : public ZCamera
{
public:
	ZFrustumCamera();

	virtual bool GetType() {return ZCAMERA_TYPE_FRUSTUM;}

	virtual const ZVector* getCameraFront() const {return &mCameraFront;}
	virtual const ZVector* getCameraRight() const {return &mCameraRight;}
	virtual const ZVector* getCameraUp() const {return &mCameraUp;}
	virtual const ZVector* getCameraEye() const {return &mCameraEye;}

	virtual void computeProjection();
	virtual void computePosition();

	float getClippingPlaneWidthCoef()	{return mNearClippingPlaneHalfWidth / mNearClippingPlane;}
	float getClippingPlaneHeightCoef()	{return mNearClippingPlaneHalfHeigth / mNearClippingPlane;}

	void setParameters(float fov, float near, float far,const ZVector* pos,const ZVector* front,const ZVector* right,const ZVector* up);

protected:
	ZVector	mCameraEye;

	ZVector	mCameraFront;
	ZVector	mCameraFrontUnparalaxed;
	ZVector	mCameraRight;
	ZVector	mCameraUp;

	float		mCameraFOV;
	float		mNearClippingPlane;
	float		mFarClippingPlane;

	float 		mNearClippingPlaneHalfWidth;	// updated by computeFrustum()
	float 		mNearClippingPlaneHalfHeigth;	// updated by computeFrustum()

};

#endif // __ZFRUSTUMCAMERA__
