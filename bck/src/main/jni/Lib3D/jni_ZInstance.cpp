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
#include "ZInstance.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZInstance_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIconstructor");
	PInstance* pi = new PInstance();
	*pi = new ZInstance();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIconstructor (end) value=%X",(int)pi);
	JNI_OUT;
	return (jlong)(unsigned long)(pi);
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIdestructor inst=%X",obj);
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PInstance*)obj);
#endif
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIdestructor (end)");
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIaddObject(JNIEnv* env, jobject thiz, jlong instance, jlong object)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIaddObject inst=%X obj=%X",instance,object);
	if (!object)
	{
		LOGE("Java_com_zerracsoft_Lib3D_ZInstance_JNIaddObject object=0");
		JNI_OUT;
		return;
	}
	(*(PInstance*)instance)->Add(*(PObject*)object);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIaddObject end");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIresetObjects(JNIEnv* env, jobject thiz, jlong instance)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIresetObjects inst=%X",instance);
	(*(PInstance*)instance)->ResetObjects();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIresetObjects end");
	JNI_OUT;
}


extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetVisible(JNIEnv* env, jobject thiz, jlong obj, jboolean visible)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetVisible inst=%X",obj);
	(*(PInstance*)obj)->SetVisible(visible);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetVisible end");
	JNI_OUT;
}
extern "C" jboolean Java_com_zerracsoft_Lib3D_ZInstance_JNIisVisible(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIisVisible inst=%X",obj);
	bool res = (*(PInstance*)obj)->IsVisible();
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetColor(JNIEnv* env, jobject thiz, jlong obj, jlong color)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetAlphaCoef inst=%X",obj);
	(*(PInstance*)obj)->SetColor(*(PColor*)color);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetAlphaCoef end");
	JNI_OUT;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZInstance_JNIgetAlpha(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIgetAlphaCoef inst=%X",obj);
	float res = (*(PInstance*)obj)->GetAlpha();
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetColored(JNIEnv* env, jobject thiz, jlong obj, jboolean colored)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetAlpha inst=%X",obj);
	(*(PInstance*)obj)->SetIsColored(colored);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetAlpha end");
	JNI_OUT;
}
extern "C" jboolean Java_com_zerracsoft_Lib3D_ZInstance_JNIisColored(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIisAlpha inst=%X",obj);
	bool res = (*(PInstance*)obj)->IsColored();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->SetTranslation(*(PVector*)v);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation2(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat y, jfloat z)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->SetTranslation(x,y,z);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale inst=%X",obj);
	(*(PInstance*)obj)->SetScale(*(PVector*)v);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale end");
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale2(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat y, jfloat z)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale inst=%X",obj);
	(*(PInstance*)obj)->SetScale(x,y,z);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetScale end");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIsetRotation(JNIEnv* env, jobject thiz, jlong obj, jlong q)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetRotation inst=%X",obj);
	(*(PInstance*)obj)->SetRotation(*(PQuaternion*)q);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetRotation end");
	JNI_OUT;
}

//protected native static void JNItranslate(int inst,int v);
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNItranslate(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->Translate(*(PVector*)v);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}
//protected native static void JNItranslate2(int inst,float x,float y,float z);
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNItranslate2(JNIEnv* env, jobject thiz, jlong obj, jfloat x, jfloat y, jfloat z)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->Translate(x,y,z);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}
//protected native static void JNIrotate(int inst,int q);
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIrotate(JNIEnv* env, jobject thiz, jlong obj, jlong q)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetRotation inst=%X",obj);
	(*(PInstance*)obj)->Rotate(*(PQuaternion*)q);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetRotation end");
	JNI_OUT;
}
//protected native static void JNIrotate2(int inst,int v,float angle);
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIrotate2(JNIEnv* env, jobject thiz, jlong obj, jlong v, jfloat angle)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->Rotate(*(PVector*)v,angle);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}
//protected native static void JNIrotate3(int inst,int v);
extern "C" void Java_com_zerracsoft_Lib3D_ZInstance_JNIrotate3(JNIEnv* env, jobject thiz, jlong obj, jlong v)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation inst=%X",obj);
	(*(PInstance*)obj)->Rotate(*(PVector*)v);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZInstance_JNIsetTranslation end");
	JNI_OUT;
}

