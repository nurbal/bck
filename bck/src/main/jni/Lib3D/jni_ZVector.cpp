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
#include "ZVector.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZVector_JNIconstructor(JNIEnv* env, jobject thiz)
{
	PVector* pv = new PVector();
	*pv = new ZVector();
	return (jlong)(unsigned long)(pv);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PVector*)obj);
#endif
}

extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIadd(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	(*(PVector*)dst)->add(*(PVector*)src);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIadd2(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	(*(PVector*)dst)->add(*(PVector*)src1,*(PVector*)src2);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIadd3(JNIEnv* env, jobject thiz, jlong dst, jint index, jfloat value)
{
	switch(index)
	{
	case 0:	(*(PVector*)dst)->x+=value;	break;
	case 1:	(*(PVector*)dst)->y+=value;	break;
	case 2:	(*(PVector*)dst)->z+=value;	break;
	}
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIadd4(JNIEnv* env, jobject thiz, jlong dst, jfloat x, jfloat y, jfloat z)
{
	(*(PVector*)dst)->add(x,y,z);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIsub(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	(*(PVector*)dst)->sub(*(PVector*)src);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIsub2(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	(*(PVector*)dst)->sub(*(PVector*)src1,*(PVector*)src2);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIset(JNIEnv* env, jobject thiz, jlong dst, jfloat x, jfloat y, jfloat z)
{
	(*(PVector*)dst)->set(x,y,z);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIset2(JNIEnv* env, jobject thiz, jlong dst, jint index, jfloat value)
{
	switch(index)
	{
	case 0:	(*(PVector*)dst)->x=value;	break;
	case 1:	(*(PVector*)dst)->y=value;	break;
	case 2:	(*(PVector*)dst)->z=value;	break;
	}
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZVector_JNIget(JNIEnv* env, jobject thiz, jlong dst, jint index)
{
	switch(index)
	{
	case 0:	return (*(PVector*)dst)->x;
	case 1:	return (*(PVector*)dst)->y;
	default:	return (*(PVector*)dst)->z;
	}
}

extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIcopy(JNIEnv* env, jobject thiz, jlong dst, jlong src)
{
	(*(PVector*)dst)->copy(*(PVector*)src);
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZVector_JNIdot(JNIEnv* env, jobject thiz, jlong src1, jlong src2)
{
	return (*(PVector*)src1)->dot(*(PVector*)src2);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIcross(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jlong src2)
{
	(*(PVector*)dst)->cross(*(PVector*)src1,*(PVector*)src2);
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZVector_JNInorme(JNIEnv* env, jobject thiz, jlong obj)
{
	return (*(PVector*)obj)->norme();
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNInormalize(JNIEnv* env, jobject thiz, jlong obj)
{
	(*(PVector*)obj)->normalize();
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNImul(JNIEnv* env, jobject thiz, jlong dst, jfloat f)
{
	(*(PVector*)dst)->mul(f);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNImul2(JNIEnv* env, jobject thiz, jlong dst, jlong src, jfloat f)
{
	(*(PVector*)dst)->mul(*(PVector*)src,f);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNIaddMul(JNIEnv* env, jobject thiz, jlong dst, jlong src, jfloat f)
{
	(*(PVector*)dst)->addMul(*(PVector*)src,f);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZVector_JNImulAddMul(JNIEnv* env, jobject thiz, jlong dst, jlong src1, jfloat f1, jlong src2, jfloat f2)
{
	(*(PVector*)dst)->mulAddMul(*(PVector*)src1,f1,*(PVector*)src2,f2);
}
extern "C" jboolean Java_com_zerracsoft_Lib3D_ZVector_JNIisEqual(JNIEnv* env, jobject thiz, jlong v1, jlong v2)
{
	return (*(PVector*)v1)->isEqual(*(PVector*)v2);
}


