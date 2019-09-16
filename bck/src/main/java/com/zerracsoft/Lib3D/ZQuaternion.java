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

public class ZQuaternion {

	@Override
	public String toString() {
		// format [ 1.2 ; 2.3 ; 3.4 ; 4.5 ]
		return "[ "+Float.toString(get(0))+" ; "+Float.toString(get(1))+" ; "+Float.toString(get(2))+" ; "+Float.toString(get(3))+" ]";
	}
	// native NDK functions
	protected native long JNIconstructor();
	protected native void JNIdestructor(long obj);
	protected native void JNIset(long dst, float x, float y, float z, float w);
//	protected native void JNIset2(int dst, int index, float value);
	protected native void JNIset3(long dst, long vector, float angle);
	protected native void JNIset4(long dst, long vector);
	protected native float JNIget(long obj, int index);
	protected native float JNIzero(long obj);
	protected native void JNIcopy(long dst,long src);
	protected native void JNInormalize(long obj);
	protected native void JNImul(long dst, long src1, long src2);
	protected native void JNIrotate(long dst, long src);
	protected native void JNIrotate2(long dst, long vector, float angle);
	protected native void JNIrotate3(long dst, long vector);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	public ZQuaternion()
	{
		mNativeObject = JNIconstructor(); 
	}
	public ZQuaternion(float X, float Y, float Z, float W)
	{
		mNativeObject = JNIconstructor();
		JNIset(mNativeObject,X,Y,Z,W);
	}
	public ZQuaternion(ZVector v,float angle)
	{
		mNativeObject = JNIconstructor();
		JNIset3(mNativeObject,v.getNativeObject(),angle);
	}
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject);
		super.finalize();
	}
	
	public void normalize()				{ JNInormalize(mNativeObject);	}
	
	public void zero() 									{JNIzero(mNativeObject);}
//	public void set(float X, float Y, float Z, float W)	{JNIset(mNativeObject,X,Y,Z,W);}
//	public void set(int index, float value)				{JNIset2(mNativeObject,index,value);}
	public void set(ZVector v,float angle)				{JNIset3(mNativeObject,v.getNativeObject(),angle);}
	public void set(ZVector v)							{JNIset4(mNativeObject,v.getNativeObject());}
	public float get(int index)							{return JNIget(mNativeObject,index);}
	public void copy(ZQuaternion other)					{JNIcopy(mNativeObject,other.mNativeObject);}
//	public void normalize()								{JNInormalize(mNativeObject);}
	public void mul(ZQuaternion a, ZQuaternion b)		{JNImul(mNativeObject,a.mNativeObject,b.mNativeObject);}
	public void rotate(ZQuaternion q)					{JNIrotate(mNativeObject,q.mNativeObject);}
	public void rotate(ZVector v,float angle)			{JNIrotate2(mNativeObject,v.getNativeObject(),angle);}
	public void rotate(ZVector v)						{JNIrotate3(mNativeObject,v.getNativeObject());}
	
	// some constants...
	public static final ZQuaternion constZero = new ZQuaternion(0,0,0,1);
	public static final ZQuaternion constX90 = new ZQuaternion(ZVector.constX,0.5f*(float)Math.PI);
	public static final ZQuaternion constX180 = new ZQuaternion(ZVector.constX,(float)Math.PI);
	public static final ZQuaternion constX270 = new ZQuaternion(ZVector.constX,-0.5f*(float)Math.PI);
	public static final ZQuaternion constY90 = new ZQuaternion(ZVector.constY,0.5f*(float)Math.PI);
	public static final ZQuaternion constY180 = new ZQuaternion(ZVector.constY,(float)Math.PI);
	public static final ZQuaternion constY270 = new ZQuaternion(ZVector.constY,-0.5f*(float)Math.PI);
	public static final ZQuaternion constZ90 = new ZQuaternion(ZVector.constZ,0.5f*(float)Math.PI);
	public static final ZQuaternion constZ180 = new ZQuaternion(ZVector.constZ,(float)Math.PI);
	public static final ZQuaternion constZ270 = new ZQuaternion(ZVector.constZ,-0.5f*(float)Math.PI);
	
}
