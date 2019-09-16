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

// JNI interface for ZRenderer class

#include <jni.h>
#include "helpers.h"
#include "log.h"
#include "jni_Watchdogs.h"
#include "ZRenderer.h"
#include "ZScene.h"
#include <GLES/gl.h>
#include <GLES/glext.h>
#include "release_settings.h"


extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIInit(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIInit");
	gRenderer.Init();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIInit end");
	JNI_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIResize(JNIEnv* env, jobject thiz, jint w, jint h)
{
	JNI_DESTRUCTOR_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIResize");
	gRenderer.Resize(w,h);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIResize end");
	JNI_DESTRUCTOR_OUT;
}
extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIRender(JNIEnv* env, jobject thiz, jlong scene)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIRender");
	gRenderer.Render(*(PScene*)scene);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIRender (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetFadeOut(JNIEnv* env, jobject thiz, jfloat progress)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIseFadeOut");
	gRenderer.SetFadeOut(progress);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIseFadeOut (end)");
	JNI_OUT;
}


extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIPreRender(JNIEnv* env, jobject thiz, jlong scene)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIPreRender");
	gRenderer.PreRender(*(PScene*)scene);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIPreRender (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetCamera(JNIEnv* env, jobject thiz, jlong cam)
{
	JNI_IN;
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetCamera2");
	gRenderer.setCamera(*(PCamera*)cam);
//	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIsetCamera2 end");
	JNI_OUT;
}




extern "C" jfloat Java_com_zerracsoft_Lib3D_ZRenderer_JNIscreenToViewportX(JNIEnv* env, jobject thiz, jfloat x)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIscreenToViewportX");
	float res = gRenderer.screenToViewportX(x);
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZRenderer_JNIscreenToViewportY(JNIEnv* env, jobject thiz, jfloat y)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIscreenToViewportY");
	float res = gRenderer.screenToViewportY(y);
	JNI_OUT
	return res;
}
extern "C" jint Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenWidth(JNIEnv* env, jobject thiz)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenWidth");
	int res = gRenderer.getScreenWidth();
	JNI_OUT
	return res;
}
extern "C" jint Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenHeight(JNIEnv* env, jobject thiz)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenHeight");
	int res = gRenderer.getScreenHeight();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZRenderer_JNIemulateScreenSize(JNIEnv* env, jobject thiz, jint width, jint height)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIemulateScreenSize");
	gRenderer.emulateScreenSize(width, height);
	JNI_OUT
}

extern "C" jfloat Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetViewportWidthF(JNIEnv* env, jobject thiz)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenWidth");
	float res = gRenderer.getViewportWidth();
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetViewportHeightF(JNIEnv* env, jobject thiz)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenWidth");
	float res = gRenderer.getViewportHeight();
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetViewportPixelSize(JNIEnv* env, jobject thiz)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZRenderer_JNIgetScreenWidth");
	float res = gRenderer.getViewportPixelSize();
	JNI_OUT
	return res;
}


