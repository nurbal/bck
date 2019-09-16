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

#include "ZFrustumCamera.h"
#include "lib3d_mutex.h"
#include <GLES/gl.h>
#include <math.h>
#include "ZRenderer.h"
#include "helpers.h"
#include "release_settings.h"
#include "log.h"












ZFrustumCamera::ZFrustumCamera() : ZCamera()
{
	mCameraEye.set(0, -2.f, 0);
	mCameraFront.set(0, 1.f, 0);
	mCameraRight.set(1.f, 0, 0);
	mCameraUp.set(0, 0, 1.f);

	mCameraFOV = 1.017f;	//45�
	mNearClippingPlane = 0.1f;
	mFarClippingPlane = 100.f;


}


void ZFrustumCamera::setParameters(float fov, float near, float far,const ZVector* pos, const ZVector* front, const ZVector* right, const ZVector* up)
{
	CAMERA_MUTEX_LOCK
	mCameraFOV = fov;
	mNearClippingPlane = near;
	mFarClippingPlane = far;
	mCameraEye.copy(pos);
	mCameraFront.copy(front);
	mCameraRight.copy(right);
	mCameraUp.copy(up);

	mCameraFrontUnparalaxed.cross(up,right);
	mCameraFrontUnparalaxed.normalize();

	CAMERA_MUTEX_UNLOCK
}

void ZFrustumCamera::computeProjection()
{
	// formerly known as ZRenderer::computeFrustum()
	glMatrixMode( GL_PROJECTION);
//*
	glLoadIdentity();

	CAMERA_MUTEX_LOCK

	mNearClippingPlaneHalfWidth = tanf(mCameraFOV * 0.5f) * mNearClippingPlane;
	mNearClippingPlaneHalfHeigth = mNearClippingPlaneHalfWidth / gRenderer.getScreenRatio();

	float clipXCenter = mNearClippingPlane*mCameraFront.dot(&mCameraRight)/mCameraFront.dot(&mCameraFrontUnparalaxed);
	float clipYCenter = mNearClippingPlane*mCameraFront.dot(&mCameraUp)/mCameraFront.dot(&mCameraFrontUnparalaxed);


	glFrustumf(clipXCenter-mNearClippingPlaneHalfWidth, clipXCenter+mNearClippingPlaneHalfWidth,
			clipYCenter-mNearClippingPlaneHalfHeigth, clipYCenter+mNearClippingPlaneHalfHeigth,
			mNearClippingPlane, mFarClippingPlane);

	CAMERA_MUTEX_UNLOCK
//*/
}

void ZFrustumCamera::computePosition()
{
	CAMERA_MUTEX_LOCK
	glMatrixMode( GL_MODELVIEW);
	glLoadIdentity();
	gluLookAt(mCameraEye.x, mCameraEye.y, mCameraEye.z,
			mCameraEye.x+mCameraFrontUnparalaxed.x, mCameraEye.y+mCameraFrontUnparalaxed.y, mCameraEye.z+mCameraFrontUnparalaxed.z,
			mCameraUp.x, mCameraUp.y, mCameraUp.z);
	CAMERA_MUTEX_UNLOCK
}



