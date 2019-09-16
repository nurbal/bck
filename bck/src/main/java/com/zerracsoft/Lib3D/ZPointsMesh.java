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

package com.zerracsoft.Lib3D;

// Points meshs;
// these objects are not lighted, nor textured
// no normal array
// the two optional data are points sizes and points colors 

public class ZPointsMesh implements ZObject {

	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	protected native static void JNIallocMesh(long obj, int nbVerts, boolean sizes, boolean colors);
	protected native static void JNIresetMesh(long obj);
	protected native static int JNIgetNbVerts(long obj);
	protected native static void JNIaddVertex(long obj, long vert, float size, long color);

	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}

	public ZPointsMesh()			{ mNativeObject = JNIconstructor(); }   
	@Override
	protected void finalize() throws Throwable { 
		JNIdestructor(mNativeObject); 
		super.finalize();
	}   

	public void allocMesh(int nbVerts, boolean sizes, boolean colors)	{JNIallocMesh(mNativeObject,nbVerts,sizes,colors);}
	public void resetMesh() {JNIresetMesh(mNativeObject);}	// removes all points, but keeps allocations
	
	public int getNbVerts()		{ return JNIgetNbVerts(mNativeObject); }
	public void addVertex(ZVector vert, float size, ZColor color)
	{
		if (color!=null)
			JNIaddVertex(mNativeObject,vert.getNativeObject(),size,color.getNativeObject());
		else
			JNIaddVertex(mNativeObject,vert.getNativeObject(),size,0);
	}
	
	

}
