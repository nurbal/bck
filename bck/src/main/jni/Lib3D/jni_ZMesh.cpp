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
#include "ZMesh.h"
#include "ZVector.h"
#include "release_settings.h"


extern "C" jlong Java_com_zerracsoft_Lib3D_ZMesh_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIconstructor");
	PObject* po = new PObject();
	*po = new ZMesh();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIconstructor (end) value=%X",(int)po);
	JNI_OUT;
	return (jlong)(unsigned long)(po);
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIdestructor");
#ifdef JNI_DESTRUCTOR_ENABLED
	delete ((PObject*)obj);
#endif
	JNI_DESTRUCTOR_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIallocMesh(JNIEnv* env, jobject thiz, jlong obj, jint nbVerts, jint nbFaces, jboolean norms)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIallocMesh mesh=%X verts=%i faces=%i",obj,nbVerts,nbFaces);
	((ZMesh*)(ZObject*)*(PObject*)obj)->allocMesh(nbVerts,nbFaces,norms);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIallocMesh");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIenableBackfaceCulling(JNIEnv* env, jobject thiz, jlong obj, jboolean culling)
{
	JNI_IN;
	((ZMesh*)(ZObject*)*(PObject*)obj)->enableBackfaceCulling(culling);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIresetMesh(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIresetMesh mesh=%X",obj);
	((ZMesh*)(ZObject*)*(PObject*)obj)->resetMesh();
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIresetMesh");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIaddFace(JNIEnv* env, jobject thiz, jlong obj, jint i1, jint i2, jint i3)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIaddFace");
	((ZMesh*)(ZObject*)*(PObject*)obj)->addFace(i1,i2,i3);
	JNI_OUT;
}

extern "C" jint Java_com_zerracsoft_Lib3D_ZMesh_JNIgetNbVerts(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIgetNbVerts");
	int res = ((ZMesh*)(ZObject*)*(PObject*)obj)->getNbVerts();
	JNI_OUT
	return res;
}

extern "C" jint Java_com_zerracsoft_Lib3D_ZMesh_JNIgetNbFaces(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIgetNbFaces");
	int res = ((ZMesh*)(ZObject*)*(PObject*)obj)->getNbFaces();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIaddFace2(JNIEnv* env, jobject thiz, jlong obj, jlong p1, jlong p2, jlong p3, jlong n1, jlong n2, jlong n3, jfloat u1, jfloat v1, jfloat u2, jfloat v2, jfloat u3, jfloat v3)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIaddFace2");
	((ZMesh*)(ZObject*)*(PObject*)obj)->addFace(*(PVector*)p1,*(PVector*)p2,*(PVector*)p3,
								*(PVector*)n1,*(PVector*)n2,*(PVector*)n3,
								u1,v1,u2,v2,u3,v3);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIaddVertex(JNIEnv* env, jobject thiz, jlong obj, jlong vert, jlong norm, jfloat u, jfloat v)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIaddVertex");
	if (norm)
		((ZMesh*)(ZObject*)*(PObject*)obj)->addVertex(*(PVector*)vert,*(PVector*)norm,u,v);
	else
		((ZMesh*)(ZObject*)*(PObject*)obj)->addVertex(*(PVector*)vert,0,u,v);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateBox(JNIEnv* env, jobject thiz, jlong obj, jlong halfsize)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateBox");
	((ZMesh*)(ZObject*)*(PObject*)obj)->createBox(*(PVector*)halfsize);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateCylinder(JNIEnv* env, jobject thiz, jlong obj, jint resol, jfloat ray, jfloat halfheight)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateCylinder");
	((ZMesh*)(ZObject*)*(PObject*)obj)->createCylinder(resol,ray,halfheight);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateTube(JNIEnv* env, jobject thiz, jlong obj, jint resol, jfloat rayIn, jfloat rayOut, jfloat halfheight)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateTube");
	((ZMesh*)(ZObject*)*(PObject*)obj)->createTube(resol,rayIn,rayOut,halfheight);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateSphere(JNIEnv* env, jobject thiz, jlong obj, jint resolX, jint resolY, jfloat ray)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateSphere");
	((ZMesh*)(ZObject*)*(PObject*)obj)->createSphere(resolX,resolY,ray);
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateSkySphere(JNIEnv* env, jobject thiz, jlong obj, jint resolX, jint resolY)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateSkySphere");
	((ZMesh*)(ZObject*)*(PObject*)obj)->createSkySphere(resolX,resolY);
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIcreateSkySphere (end)");
	JNI_OUT;
}

extern "C" void Java_com_zerracsoft_Lib3D_ZMesh_JNIsetMaterial(JNIEnv* env, jobject thiz, jlong obj, jlong mat)
{
	JNI_IN;
	LOGJNI("Java_com_zerracsoft_Lib3D_ZMesh_JNIsetMaterial");
	((ZMesh*)(ZObject*)*(PObject*)obj)->mMaterial = (*(PMaterial*)mat);
	JNI_OUT;
}

