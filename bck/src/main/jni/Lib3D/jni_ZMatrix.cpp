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
#include "ZMatrix.h"
#include <stdlib.h>
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZMatrix_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	PMatrix* pm = new PMatrix();
	*pm = new ZMatrix();
	JNI_OUT;
	return (jlong)(unsigned long)(pm);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMatrix_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PMatrix*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMatrix_JNIsetIdentity(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	(*(PMatrix*)obj)->setIdentity();
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMatrix_JNIset(JNIEnv* env, jobject thiz, jlong obj, jint index, jfloat value)
{
	JNI_IN;
	(*(PMatrix*)obj)->set(index,value);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMatrix_JNItransform(JNIEnv* env, jobject thiz, jlong obj, jlong in, jlong out)
{
	JNI_IN;
	(*(PMatrix*)obj)->transform(*(PVector*)in,*(PVector*)out);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMatrix_JNIgetInverseMatrix(JNIEnv* env, jobject thiz, jlong obj, jlong inv)
{
	JNI_IN;
	(*(PMatrix*)obj)->getInverseMatrix(*(PMatrix*)inv);
	JNI_OUT;
}


