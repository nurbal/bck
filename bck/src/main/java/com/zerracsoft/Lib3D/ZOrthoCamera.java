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

public class ZOrthoCamera implements ZCamera 
{
	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
    private static native void JNIsetParameters(long obj,float near, float far,float viewportWidthFactor, float viewportHeightFactor, long pos,long front,long right,long up);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() { return mNativeObject; }

	public ZOrthoCamera()	{ mNativeObject = JNIconstructor(); }   

	@Override
	protected void finalize() throws Throwable 
	{
		JNIdestructor(mNativeObject); 
		super.finalize();
	}  

	public void setParameters(float near, float far,float viewportWidthFactor, float viewportHeightFactor, ZVector pos,ZVector front,ZVector right,ZVector up)	{ JNIsetParameters(mNativeObject, near, far, viewportWidthFactor, viewportHeightFactor, pos.getNativeObject(), front.getNativeObject(), right.getNativeObject(), up.getNativeObject()); }

}
