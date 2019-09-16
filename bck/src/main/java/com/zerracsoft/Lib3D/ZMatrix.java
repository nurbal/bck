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

public class ZMatrix {

	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	protected native static void JNIsetIdentity(long obj);
	protected native static void JNIset(long dst, int index, float value);
	protected native static void JNItransform(long obj,long in, long out);
//	protected native static void JNIgetOpenGLMatrix(long obj,int[] coefs);
	protected native static void JNIgetInverseMatrix(long obj,long inv);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}

	
	public ZMatrix()			{ mNativeObject = JNIconstructor(); }   
	

	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}   
	
	 
	public void setIdentity()	{ JNIsetIdentity(mNativeObject); }

	public void set(int index,float value)	{ JNIset(mNativeObject,index,value); }
	
	public void transform(ZVector in,ZVector out) { JNItransform(mNativeObject,in.getNativeObject(),out.getNativeObject()); }
	
	public ZMatrix getInverseMatrix()
	{
		ZMatrix inv = new ZMatrix();
		JNIgetInverseMatrix(mNativeObject,inv.getNativeObject());
		return inv;
	}

}
