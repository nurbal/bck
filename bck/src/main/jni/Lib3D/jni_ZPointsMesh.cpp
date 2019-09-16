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
#include "ZPointsMesh.h"
#include "ZVector.h"
#include "ZColor.h"
#include "release_settings.h"


extern "C" jlong Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIconstructor");
	PObject* po = new PObject();
	*po = new ZPointsMesh();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIconstructor (end) value=%X",(int)po);
	JNI_OUT;
	return (jlong)(unsigned long)(po);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIdestructor");
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PObject*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIallocMesh(JNIEnv* env, jobject thiz, jlong obj, jint nbVerts, jboolean sizes, jboolean colors)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIallocMesh mesh=%X verts=%i faces=%i",obj,nbVerts,nbFaces);
	((ZPointsMesh*)(ZObject*)*(PObject*)obj)->allocMesh(nbVerts,sizes,colors);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIallocMesh");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIresetMesh(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIresetMesh mesh=%X",obj);
	((ZPointsMesh*)(ZObject*)*(PObject*)obj)->resetMesh();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIresetMesh");
	JNI_OUT;
}

extern "C" jint Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIgetNbVerts(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIgetNbVerts");
	int res = ((ZPointsMesh*)(ZObject*)*(PObject*)obj)->getNbVerts();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIaddVertex(JNIEnv* env, jobject thiz, jlong obj, jlong vert, jfloat size, jlong color)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZPointsMesh_JNIaddVertex");
	if (color)
		((ZPointsMesh*)(ZObject*)*(PObject*)obj)->addVertex(*(PVector*)vert,size,*(PColor*)color);
	else
		((ZPointsMesh*)(ZObject*)*(PObject*)obj)->addVertex(*(PVector*)vert,size,0);
	JNI_OUT;
}
