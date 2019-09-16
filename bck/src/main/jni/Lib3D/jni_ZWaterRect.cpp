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
#include "ZWaterRect.h"
#include "ZVector.h"
#include "release_settings.h"


extern "C" jlong Java_com_zerracsoft_Lib3D_ZWaterRect_JNIconstructor(JNIEnv* env, jobject thiz,jfloat x1, jfloat y1, jfloat x2, jfloat y2, jfloat u1, jfloat v1, jfloat u2, jfloat v2, jint resolX, jint resolY, jfloat UVamplitude, jfloat freq, jboolean norms)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZWaterRect_JNIconstructor");
	PObject* po = new PObject();
	*po = new ZWaterRect(x1, y1, x2, y2, u1, v1, u2, v2, resolX, resolY, UVamplitude, freq, norms);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZWaterRect_JNIconstructor (end) value=%X",(int)po);
	JNI_OUT;
	return (jlong)(po);
}
