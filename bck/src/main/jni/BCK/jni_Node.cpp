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
#include "Node.h"
#include "log.h"
#include "../Lib3D/Lib3D.h"
#include "../Lib3D/jni_Watchdogs.h"

extern "C" jlong Java_com_zerracsoft_bck_Node_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN
	PNode* pn = new PNode();
	*pn = new Node();
	(*pn)->mJavaNativeObject = (unsigned long)pn;
	LOGD("Node_JNIconstructor %lu",(*pn)->mJavaNativeObject);
	JNI_OUT
	return (*pn)->mJavaNativeObject;
}
extern "C" void Java_com_zerracsoft_bck_Node_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN
	CHECK_SPTR((PNode*)obj);
	delete ((PNode*)obj);
	JNI_DESTRUCTOR_OUT
}
extern "C" void Java_com_zerracsoft_bck_Node_JNIcopy(JNIEnv* env, jobject thiz, jlong obj, jlong other)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	CHECK_SPTR((PNode*)other);
	PNode n = *(PNode*)obj;
	PNode o = *(PNode*)other;
	n->Copy(o);
	JNI_OUT
}


extern "C" void Java_com_zerracsoft_bck_Node_JNIresetSimulation(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->ResetSimulation();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIsetSaveID(JNIEnv* env, jobject thiz, jlong obj, jint saveID)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
//	LOGD("Node_JNIsetSaveID %d (old=%d)",saveID,n->mSaveID);
	n->mSaveID = saveID;
	JNI_OUT
}

extern "C" jint Java_com_zerracsoft_bck_Node_JNIgetSaveID(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
//	LOGD("Node_JNIgetSaveID %d",n->mSaveID);
	int res = n->mSaveID;
	JNI_OUT
	return res;

}

extern "C" jint Java_com_zerracsoft_bck_Node_JNIgetNbLinks(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
//	LOGD("Node_JNIgetNbLinks=%d",n->mLinks.GetNbElements());
	int res = n->GetNbLinks();
	JNI_OUT
	return res;

}
extern "C" jlong Java_com_zerracsoft_bck_Node_JNIgetLinkNativeObject(JNIEnv* env, jobject thiz, jlong obj, jint index)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
//	LOGD("Node_JNIgetLinkNativeObject(%d)=%d",index,n->mLinks.Get(index)->mJavaNativeObject);
	unsigned long res = n->GetLink(index)->mJavaNativeObject;
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_bck_Node_JNIremoveLink(JNIEnv* env, jobject thiz, jlong obj, jlong link)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	CHECK_SPTR((PLink*)link);
	PNode n = *(PNode*)obj;
	PLink l = *(PLink*)link;
	LOGD("Node_JNIremoveLink(%ul)",l->mJavaNativeObject);
	n->RemoveLink(l);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIsetPosition(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat z)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->SetPosition(x,z);
	JNI_OUT
}

extern "C" jfloat Java_com_zerracsoft_bck_Node_JNIgetPositionX(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	float res = n->GetPosition(0)->x;
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Node_JNIgetPositionZ(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	float res = n->GetPosition(0)->z;
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Node_JNIgetResetPositionX(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	float res = n->GetResetPosition()->x;
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Node_JNIgetResetPositionZ(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	float res = n->GetResetPosition()->z;
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIsetFixed(JNIEnv* env, jobject thiz, jlong obj, jboolean fixed)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->mFixed = fixed;
	JNI_OUT
}
extern "C" jboolean Java_com_zerracsoft_bck_Node_JNIisFixed(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	bool res = n->mFixed;
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIresetDynamicWeight(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->ResetDynamicWeight();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIseparateNode(JNIEnv* env, jobject thiz, jlong obj, jboolean separate)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->SeparateNode(separate);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIfusion(JNIEnv* env, jobject thiz, jlong obj, jlong target)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	CHECK_SPTR((PNode*)target);
//	LOGD("JNIfusion");
	PNode n = *(PNode*)obj;
	PNode t = *(PNode*)target;
	n->Fusion(t);
	JNI_OUT
}

extern "C" jboolean Java_com_zerracsoft_bck_Node_JNIisSeparator(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	bool res = n->IsSeparator();
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Node_JNIgetSeparatorAngle(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	float res = n->GetSeparatorAngle();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIsetSeparator(JNIEnv* env, jobject thiz, jlong obj, jfloat angle)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->SetSeparator(angle);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Node_JNIunsetSeparator(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PNode*)obj);
	PNode n = *(PNode*)obj;
	n->UnsetSeparator();
	JNI_OUT
}
