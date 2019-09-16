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
#include "Link.h"
#include "../Lib3D/Lib3D.h"
#include "log.h"
#include "../Lib3D/jni_Watchdogs.h"

extern "C" void Java_com_zerracsoft_bck_Link_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN
	CHECK_SPTR((PLink*)obj);
	delete ((PLink*)obj);
	JNI_DESTRUCTOR_OUT
}

extern "C" void Java_com_zerracsoft_bck_Link_JNIinit(JNIEnv* env, jobject thiz, jlong obj, jlong node1, jlong node2)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	CHECK_SPTR((PNode*)node1);
	CHECK_SPTR((PNode*)node2);
	PLink link = *((PLink*)obj);
	PNode n1 = *((PNode*)node1);
	PNode n2 = *((PNode*)node2);
	LOGD("Link_JNIinit 1 obj=%d node1=%d node2=%d",obj,node1,node2);
	link->Init(n1,n2);
	LOGD("Link_JNIinit 2");
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Link_JNIcopy(JNIEnv* env, jobject thiz, jlong obj, jlong src)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	CHECK_SPTR((PLink*)src);
	PLink link = *((PLink*)obj);
	PLink linkSrc = *((PLink*)src);
	link->Copy(linkSrc);
	JNI_OUT
}

extern "C" jlong Java_com_zerracsoft_bck_Link_JNIgetNodeNativeObject(JNIEnv* env, jobject thiz, jlong obj, jint index)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	unsigned long result=link->GetNode(index)->mJavaNativeObject;
//	LOGD("Link_JNIgetNodeNativeObject(%d) %d",index,result);
	JNI_OUT
	return result;
}


extern "C" jfloat Java_com_zerracsoft_bck_Link_JNIgetNodeX(JNIEnv* env, jobject thiz, jlong obj, jint index)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	float res = link->GetNode(index)->GetPosition(link)->x;
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Link_JNIgetNodeZ(JNIEnv* env, jobject thiz, jlong obj, jint index)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	float res = link->GetNode(index)->GetPosition(link)->z;
	JNI_OUT
	return res;
}

extern "C" jint Java_com_zerracsoft_bck_Link_JNIgetNodeSaveID(JNIEnv* env, jobject thiz, jlong obj, jint index)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	int res = link->GetNode(index)->mSaveID;
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Link_JNIresetSimulation(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	link->ResetSimulation();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Link_JNIstartSimulationFrame(JNIEnv* env, jobject thiz, jlong obj, jfloat dt)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
//	LOGD("JNIstartSimulationFrame(%f)*****************************************",dt);
	PLink link = *((PLink*)obj);
	link->StartSimulationFrame(dt);
	JNI_OUT
}

extern "C" jboolean Java_com_zerracsoft_bck_Link_JNIupdateSimulation(JNIEnv* env, jobject thiz, jlong obj, jfloat dt)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
//	LOGD("JNIupdateSimulation(%f)",dt);

	PLink link = *((PLink*)obj);
	bool res = link->UpdateSimulation(dt);
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Link_JNIgetLength(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	float res = link->GetLength();
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Link_JNIgetNominalLength(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	float res = link->GetNominalLength();
	JNI_OUT
	return res;
}

extern "C" jboolean Java_com_zerracsoft_bck_Link_JNIisBroken(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	bool res = link->IsBroken();
	JNI_OUT
	return res;
}

extern "C" jboolean Java_com_zerracsoft_bck_Link_JNIisUnderwater(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	bool res = link->IsUnderwater();
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Link_JNIgetStressFactor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	float res = link->GetStressFactor();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Link_JNIsetBroken(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	link->SetBroken();
	JNI_OUT
}

