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
#include "ZColor.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZColor_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN
	PColor* pc = new PColor();
	*pc = new ZColor();
	long res = (jlong)(unsigned long)(pc);
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PColor*)obj);
#endif
	JNI_DESTRUCTOR_OUT
}

extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIset(JNIEnv* env, jobject thiz, jlong dst, jfloat r, jfloat g, jfloat b, jfloat a)
{
	JNI_IN
	(*(PColor*)dst)->set(r,g,b,a);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIset2(JNIEnv* env, jobject thiz, jlong dst, jint index, jfloat value)
{
	JNI_IN
	switch(index)
	{
	case 0:	(*(PColor*)dst)->r=value;	break;
	case 1:	(*(PColor*)dst)->g=value;	break;
	case 2:	(*(PColor*)dst)->b=value;	break;
	case 3:	(*(PColor*)dst)->a=value;	break;
	}
	JNI_OUT
}

extern "C" jfloat Java_com_zerracsoft_Lib3D_ZColor_JNIget(JNIEnv* env, jobject thiz, jlong dst, jint index)
{
	JNI_IN
	float res = 0.f;
	switch(index)
	{
	case 0:	res = (*(PColor*)dst)->r;
	case 1:	res = (*(PColor*)dst)->g;
	case 2:	res = (*(PColor*)dst)->b;
	case 3:	res = (*(PColor*)dst)->a;
	}
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIadd(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	JNI_IN
	(*(PColor*)dst)->add(*(PColor*)src);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIadd2(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	JNI_IN
	(*(PColor*)dst)->add(*(PColor*)src1,*(PColor*)src2);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIadd3(JNIEnv* env, jobject thiz, jlong dst, jint index, jfloat value)
{
	JNI_IN
	switch(index)
	{
	case 0:	(*(PColor*)dst)->r+=value;	break;
	case 1:	(*(PColor*)dst)->g+=value;	break;
	case 2:	(*(PColor*)dst)->b+=value;	break;
	case 3:	(*(PColor*)dst)->a+=value;	break;
	}
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIsub(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	JNI_IN
	(*(PColor*)dst)->sub(*(PColor*)src);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIsub2(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	JNI_IN
	(*(PColor*)dst)->sub(*(PColor*)src1,*(PColor*)src2);
	JNI_OUT
}


extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIcopy(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	JNI_IN
	(*(PColor*)dst)->copy(*(PColor*)src);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIclamp(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	(*(PColor*)obj)->clamp();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNImul(JNIEnv* env, jobject thiz, jlong dst, jfloat f)
{
	JNI_IN
	(*(PColor*)dst)->mul(f);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNImul2(JNIEnv* env, jobject thiz, jlong dst, jlong src, jfloat f)
{
	JNI_IN
	(*(PColor*)dst)->mul(*(PColor*)src,f);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNIaddMul(JNIEnv* env, jobject thiz, jlong dst, jlong src, jfloat f)
{
	JNI_IN
	(*(PColor*)dst)->addMul(*(PColor*)src,f);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_Lib3D_ZColor_JNImulAddMul(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jfloat f1, jlong src2, jfloat f2)
{
	JNI_IN
	(*(PColor*)dst)->mulAddMul(*(PColor*)src1,f1,*(PColor*)src2,f2);
	JNI_OUT
}
extern "C" jboolean Java_com_zerracsoft_Lib3D_ZColor_JNIisEqual(JNIEnv* env, jobject thiz, jlong v1, jlong v2)
{
	JNI_IN
	bool res = (*(PColor*)v1)->isEqual(*(PColor*)v2);
	JNI_OUT
	return res;
}


