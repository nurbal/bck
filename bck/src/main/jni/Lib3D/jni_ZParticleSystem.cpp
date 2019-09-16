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
#include "ZParticleSystem.h"
#include "ZParticleSystemNewton.h"
#include "ZVector.h"
#include "release_settings.h"


extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIdestructor");
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PObject*)obj);
#endif
	LOGI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIdestructor end");
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIsetMaterial(JNIEnv* env, jobject thiz, jlong obj, jlong mat, jint matVariantsX, jint matVariantsY)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIsetMaterial");
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->mMaterial = (*(PMaterial*)mat);
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->mMaterialVariantsX = matVariantsX;
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->mMaterialVariantsY = matVariantsY;
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIsetEmmiterPos(JNIEnv* env, jobject thiz, jlong obj, jlong pos)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIsetEmmiterPos");
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->SetEmmiterPos(*(PVector*)pos);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIpause(JNIEnv* env, jobject thiz, jlong obj, jboolean paused)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIpause");
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->Pause(paused);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIinit(JNIEnv* env, jobject thiz, jlong obj, jint nbParticles)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIinit");
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->Init(nbParticles);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIclearParticles(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIclearParticles");
	((ZParticleSystem*)(ZObject*)*(PObject*)obj)->ClearParticles();
	JNI_OUT;
}



///////////////////////////////////////////////////////////////// NEWTON


extern "C" jlong Java_com_zerracsoft_Lib3D_ZParticleSystemNewton_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIconstructor");
	PObject* po = new PObject();
	*po = new ZParticleSystemNewton();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystem_JNIconstructor (end) value=%X",(int)po);
	JNI_OUT;
	return (jlong)(unsigned long)(po);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystemNewton_JNIsetParameters(JNIEnv* env, jobject thiz, jlong obj, jlong emmiterrandomX, jlong emmiterrandomY, jlong emmiterrandomZ, jlong emmiterrandomCoefs, jlong speed, jlong speedrandom, jlong gravity, jlong gravityrandom, jint lifetime, jint lifetimerandom, jfloat size, jfloat sizerandom, jfloat sizefactorbirth, jfloat sizefactordeath, jboolean oneshot)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystemNewton_JNIsetParameters");
	((ZParticleSystemNewton*)(ZObject*)*(PObject*)obj)->SetParameters(*(PVector*)emmiterrandomX, *(PVector*)emmiterrandomY, *(PVector*)emmiterrandomZ, *(PVector*)emmiterrandomCoefs, *(PVector*)speed, *(PVector*)speedrandom, *(PVector*)gravity, *(PVector*)gravityrandom, lifetime, lifetimerandom, lifetime, size, sizerandom, sizefactorbirth, sizefactordeath, oneshot);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZParticleSystemNewton_JNIfire(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZParticleSystemNewton_JNIfire");
	((ZParticleSystemNewton*)(ZObject*)*(PObject*)obj)->Fire();
	JNI_OUT;
}






