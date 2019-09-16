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
#include "ZMaterial.h"
#include "release_settings.h"


extern "C" jlong Java_com_zerracsoft_Lib3D_ZMaterial_JNIconstructor(JNIEnv* env, jobject thiz, jboolean clamp)
{
	JNI_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIconstructor");
	PMaterial* pm = new PMaterial();
	*pm = new ZMaterial();
	(*pm)->mClamp = clamp;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIconstructor (end) value=%X",(int)pm);
	JNI_OUT;
	return (jlong)(unsigned long)(pm);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMaterial_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIdestructor");
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PMaterial*)obj);
#endif
	LOGI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIdestructor end");
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMaterial_JNIsetID(JNIEnv* env, jobject thiz, jlong obj, jint id)
{
	JNI_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIset");
	(*(PMaterial*)obj)->mID = id;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIset (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMaterial_JNIsetAlpha(JNIEnv* env, jobject thiz, jlong obj, jboolean alpha)
{
	JNI_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIset");
	(*(PMaterial*)obj)->mHasAlpha = alpha;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMaterial_JNIset (end)");
	JNI_OUT;
}

