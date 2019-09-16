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
#include "jni_Watchdogs.h"
#include "ZQuaternion.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZQuaternion_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	PQuaternion* pq = new PQuaternion();
	*pq = new ZQuaternion();
	JNI_OUT;
	return (jlong)(unsigned long)(pq);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PQuaternion*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIset(JNIEnv* env, jobject thiz, jlong dst, jfloat x, jfloat y, jfloat z, jfloat w)
{
	JNI_IN
	(*(PQuaternion*)dst)->set(x,y,z,w);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIset3(JNIEnv* env, jobject thiz, jlong dst, jlong vector, jfloat angle)
{
	JNI_IN;
	(*(PQuaternion*)dst)->set(*(PVector*)vector,angle);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIset4(JNIEnv* env, jobject thiz, jlong dst, jlong vector)
{
	JNI_IN;
	(*(PQuaternion*)dst)->set(*(PVector*)vector);
	JNI_OUT;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZQuaternion_JNIget(JNIEnv* env, jobject thiz, jlong dst, jint index)
{
	JNI_IN
	float res = (*(PQuaternion*)dst)->get(index);
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIzero(JNIEnv* env, jobject thiz, jlong dst)
{
	JNI_IN;
	(*(PQuaternion*)dst)->zero();
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIcopy(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	JNI_IN;
	(*(PQuaternion*)dst)->copy(*(PQuaternion*)src);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNImul(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	JNI_IN;
	(*(PQuaternion*)dst)->mul(*(PQuaternion*)src1,*(PQuaternion*)src2);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIrotate(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	JNI_IN;
	(*(PQuaternion*)dst)->rotate(*(PQuaternion*)src);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIrotate2(JNIEnv* env, jobject thiz, jlong dst, jlong vector, jfloat angle)
{
	JNI_IN;
	(*(PQuaternion*)dst)->rotate(*(PVector*)vector,angle);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNIrotate3(JNIEnv* env, jobject thiz, jlong dst, jlong vector)
{
	JNI_IN;
	(*(PQuaternion*)dst)->rotate(*(PVector*)vector);
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZQuaternion_JNInormalize(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	(*(PQuaternion*)obj)->normalize();
	JNI_OUT
}

