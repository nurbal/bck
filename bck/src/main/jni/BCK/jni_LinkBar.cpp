#include <jni.h>
#include "release_settings.h"
#include "LinkBar.h"
#include "../Lib3D/Lib3D.h"
#include "log.h"
#include "../Lib3D/jni_Watchdogs.h"

extern "C" jlong Java_com_zerracsoft_bck_LinkBar_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN
	PLink* pl = new PLink();
	*pl = new LinkBar();
	(*pl)->mJavaNativeObject = (unsigned long)pl;
	LOGD("LinkBar_JNIconstructor %lu",(*pl)->mJavaNativeObject);
	JNI_OUT
	return (jlong)(unsigned long)(pl);
}

extern "C" void Java_com_zerracsoft_bck_LinkBar_JNIaddDynamicWeight(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat w)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	((LinkBar*)(Link*)(link))->AddDynamicWeight(x,w);
	JNI_OUT
}

extern "C" jboolean Java_com_zerracsoft_bck_LinkBar_JNIisRails(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLink*)obj);
	PLink link = *((PLink*)obj);
	bool res = ((LinkBar*)(Link*)(link))->IsRails();
	JNI_OUT
	return res;
}
