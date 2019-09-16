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

// JNI interface for ZFrustumCamera class

#include <jni.h>
#include "helpers.h"
#include "log.h"
#include "jni_Watchdogs.h"
#include "ZFrustumCamera.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZFrustumCamera_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	PCamera* pc = new PCamera();
	*pc = new ZFrustumCamera();
	JNI_OUT;
	return (jlong)(unsigned long)(pc);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZFrustumCamera_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PCamera*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZFrustumCamera_JNIsetParameters(JNIEnv* env, jobject thiz, jlong obj, jfloat fov, jfloat near, jfloat far, jlong pos, jlong front, jlong right, jlong up)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetCamera2");
	((ZFrustumCamera*)(ZCamera*)*(PCamera*)obj)->setParameters(fov,near,far,*(PVector*)pos,*(PVector*)front,*(PVector*)right,*(PVector*)up);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetCamera2 end");
	JNI_OUT;
}

extern "C" jfloat Java_com_zerracsoft_Lib3D_ZFrustumCamera_JNIgetClippingPlaneWidthCoef(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetClippingPlaneWidthCoef");
	float res = ((ZFrustumCamera*)(ZCamera*)*(PCamera*)obj)->getClippingPlaneWidthCoef();
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZFrustumCamera_JNIgetClippingPlaneHeightCoef(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetClippingPlaneHeightCoef");
	float res = ((ZFrustumCamera*)(ZCamera*)*(PCamera*)obj)->getClippingPlaneHeightCoef();
	JNI_OUT
	return res;
}
