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

#include <jni.h>
#include "release_settings.h"
#include "LinkJack.h"
#include "../Lib3D/Lib3D.h"
#include "log.h"
#include "../Lib3D/jni_Watchdogs.h"


extern "C" jlong Java_com_zerracsoft_bck_LinkJack_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN
	PLink* pl = new PLink();
	*pl = new LinkJack();
	(*pl)->mJavaNativeObject = (unsigned long)pl;
	LOGD("LinkJack_JNIconstructor %lu",(*pl)->mJavaNativeObject);
	JNI_OUT
	return (jlong)(unsigned long)(pl);
}

extern "C" void Java_com_zerracsoft_bck_LinkJack_JNIsetDynamicLengthFactor(JNIEnv* env, jobject thiz, jlong obj, jfloat factor)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
//	LOGD("JNIsetDynamicLengthFactor obj=%d length=%f",obj, factor);
	PLink link = *((PLink*)obj);
	((LinkJack*)(Link*)(link))->mDynamicNominalLengthFactor = factor;
	JNI_OUT
}
