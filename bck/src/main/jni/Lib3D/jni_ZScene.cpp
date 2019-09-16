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
#include "ZScene.h"
#include "release_settings.h"

extern "C" jlong Java_com_zerracsoft_Lib3D_ZScene_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIconstructor");
	PScene* ps = new PScene();
	*ps = new ZScene();

	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIconstructor end");
	JNI_OUT;
	return (jlong)(unsigned long)(ps);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIdestructor(JNIEnv* env, jobject thiz, jlong scene)
{
	JNI_DESTRUCTOR_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIdestructor");
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PScene*)scene);
#endif
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIdestructor end");
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance(JNIEnv* env, jobject thiz, jlong scene, jlong instance)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance");
	(*(PScene*)scene)->Add(*(PInstance*)instance);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstanceSky(JNIEnv* env, jobject thiz, jlong scene, jlong instance)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstanceSky");
	(*(PScene*)scene)->AddSky(*(PInstance*)instance);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstanceSky (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance2D(JNIEnv* env, jobject thiz, jlong scene, jlong instance)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance2D");
	(*(PScene*)scene)->Add2D(*(PInstance*)instance);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIaddInstance2D (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIremoveInstance(JNIEnv* env, jobject thiz, jlong scene, jlong instance)
{
	JNI_IN;
	PInstance *pi = (PInstance*)instance;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIremoveInstance refcount=%i",(*pi)->GetRefCount());
	(*(PScene*)scene)->Remove(*pi);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIremoveInstance end refcount=%i",(*pi)->GetRefCount());
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIclear(JNIEnv* env, jobject thiz, jlong scene)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIclear");
	(*(PScene*)scene)->Clear();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIclear");
	JNI_OUT;
}



extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIsetLight(JNIEnv* env, jobject thiz, jlong scene,jfloat ambient,jfloat diffuse,jfloat specular, jlong position)
{
	JNI_IN;
	(*(PScene*)scene)->SetLight(ambient, diffuse, specular, *(PVector*)position);
	JNI_OUT;

}

extern "C" void Java_com_zerracsoft_Lib3D_ZScene_JNIupdate(JNIEnv* env, jobject thiz, jlong scene,jint elapsedTime)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIupdate");
	(*(PScene*)scene)->Update(elapsedTime);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZScene_JNIupdate (end)");
	JNI_OUT;

}
