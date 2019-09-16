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

// JNI interface for ZVector class

#include <jni.h>
#include "helpers.h"
#include "log.h"
#include "ZSRT.h"
#include "jni_Watchdogs.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZSRT_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	PSRT* ps = new PSRT();
	*ps = new ZSRT();
	JNI_OUT;
	return (jlong)(unsigned long)(ps);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PSRT*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetIdentity(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	(*(PSRT*)obj)->setIdentity();
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetTranslation(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
	(*(PSRT*)obj)->setTranslation(*(PVector*)v);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetTranslation2(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat y, jfloat z)
{
	JNI_IN;
	(*(PSRT*)obj)->setTranslation(x,y,z);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetScale(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
	(*(PSRT*)obj)->setScale(*(PVector*)v);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetScale2(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat y, jfloat z)
{
	JNI_IN;
	(*(PSRT*)obj)->setScale(x,y,z);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIgetMatrix(JNIEnv* env, jobject thiz, jlong obj, jlong m)
{
	JNI_IN;
	(*(PMatrix*)m)->copy((*(PSRT*)obj)->getMatrix());
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetRotation(JNIEnv* env, jobject thiz, jlong obj, jlong q)
{
	JNI_IN;
	(*(PSRT*)obj)->setRotation((*(PQuaternion*)q));
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZSRT_JNIsetRotation2(JNIEnv* env, jobject thiz, jlong obj, jlong v, jfloat angle)
{
	JNI_IN;
	(*(PSRT*)obj)->setRotation(*(PVector*)v,angle);
	JNI_OUT;
}

