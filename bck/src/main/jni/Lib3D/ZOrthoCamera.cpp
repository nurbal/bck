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

#include "ZOrthoCamera.h"
#include "ZVector.h"
#include "ZMatrix.h"
#include <GLES/gl.h>
#include "lib3d_mutex.h"
#include "helpers.h"






ZOrthoCamera::ZOrthoCamera() : ZCamera()
{

}


void ZOrthoCamera::setParameters(float near, float far,float viewportWidthFactor, float viewportHeightFactor, const ZVector* pos,const ZVector* front,const ZVector* right,const ZVector* up)
{
	mCameraEye.copy(pos);
	mCameraFront.copy(front);
	mCameraRight.copy(right);
	mCameraUp.copy(up);
//	ZMatrix base; base.initFromBase(right, up, front, ZVector::XYZ(), ZVector::Null());
//	ZMatrix invBase; base.getInverseMatrix(&invBase);
	mNearClippingPlane = near;
	mFarClippingPlane = far;
	mClippingPlaneHalfWidth = viewportWidthFactor;
	mClippingPlaneHalfHeight = viewportHeightFactor;
}

void ZOrthoCamera::computeProjection()
{
	glMatrixMode( GL_PROJECTION);
	glLoadIdentity();
	glOrthof(-mClippingPlaneHalfWidth,mClippingPlaneHalfWidth,-mClippingPlaneHalfHeight,mClippingPlaneHalfHeight,mNearClippingPlane,mFarClippingPlane);
}

void ZOrthoCamera::computePosition()
{
	CAMERA_MUTEX_LOCK
	glMatrixMode( GL_MODELVIEW);

	glLoadIdentity();
	gluLookAt(mCameraEye.x, mCameraEye.y, mCameraEye.z,
			mCameraEye.x+mCameraFront.x, mCameraEye.y+mCameraFront.y, mCameraEye.z+mCameraFront.z,
			mCameraUp.x, mCameraUp.y, mCameraUp.z);


	CAMERA_MUTEX_UNLOCK
}
