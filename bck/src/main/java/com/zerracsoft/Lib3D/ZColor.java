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

public class ZColor {
	
	@Override
	public String toString() {
		// format [ 1.2 ; 2.3 ; 3.4 ]
		return "[ "+Float.toString(get(0))+" ; "+Float.toString(get(1))+" ; "+Float.toString(get(2))+" ]";
	}
	// native NDK functions
	protected native long JNIconstructor();
	protected native void JNIdestructor(long obj);
	protected native void JNIset(long dst, float r, float g, float b, float a);
	protected native void JNIset2(long dst, int index, float value);
	protected native float JNIget(long obj, int index);
	protected native void JNIadd(long dst, long src);
	protected native void JNIadd2(long dst,long src1,long src2);
	protected native void JNIadd3(long dst,int index,float value);
	protected native void JNIsub(long dst,long src);
	protected native void JNIsub2(long dst,long src1,long src2);
	protected native void JNIcopy(long dst,long src);
	protected native void JNIclamp(long obj);
	protected native void JNImul(long dst,float f);
	protected native void JNImul2(long dst,long src,float f);
	protected native void JNIaddMul(long dst,long src,float f);
	protected native void JNImulAddMul(long dst,long src1,float f1,long src2,float f2);
	protected native boolean JNIisEqual(long c1, long c2);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	
	public int F2I(float f) {return (int)(f*65536.f);}
	public float I2F(int i) {return (float)i/65536.f;}
	public int mulI(int a, int b) {return (int)(long)(((long)a*(long)b)>>16);}
	
	public ZColor() 
	{
		mNativeObject = JNIconstructor();   
	}
	public ZColor(float r, float g, float b, float a)
	{
		mNativeObject = JNIconstructor();
		JNIset(mNativeObject,r,g,b,a); 
	}
	public ZColor(ZColor other)
	{
		mNativeObject = JNIconstructor();
		copy(other);
	}
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject);
		super.finalize();
	}

	
	// setters
	public void set(float r, float g, float b, float a)	{ JNIset(mNativeObject,r,g,b,a); }
	public void set(int index, float value)		{ JNIset2(mNativeObject,index, value); }
	
	// getters
	public float get(int index)					{ return JNIget(mNativeObject, index); }				

	// basic vector operations
	public void add(ZColor other)			{ JNIadd(mNativeObject,other.mNativeObject); }
	public void add(int index, float value)	{ JNIadd3(mNativeObject,index,value); }
	public void add(ZColor v1,ZColor v2)	{ JNIadd2(mNativeObject,v1.mNativeObject,v2.mNativeObject); }
	
	public void sub(ZColor other)			{ JNIsub(mNativeObject,other.mNativeObject); }
	public void sub(ZColor v1,ZColor v2)	{ JNIsub2(mNativeObject,v1.mNativeObject,v2.mNativeObject); } 
	 
	public void mul(float f) 				{ JNImul(mNativeObject,f); }
	public void mul(ZColor v, float f)		{ JNImul2(mNativeObject,v.mNativeObject,f); }

	public void addMul(ZColor v, float f)	{ JNIaddMul(mNativeObject,v.mNativeObject,f); }
	public void mulAddMul(ZColor v1, float f1, ZColor v2, float f2) 	{ JNImulAddMul(mNativeObject,v1.mNativeObject,f1,v2.mNativeObject,f2); }

	public void copy(ZColor other)		{ JNIcopy(mNativeObject,other.mNativeObject); }

	public boolean isEqual(ZColor other) { return JNIisEqual(mNativeObject,other.mNativeObject); }
	
	public void clamp()				{ JNIclamp(mNativeObject);	}
	
	public void setTransparent()			{ set(0.f,0.f,0.f,0.f); }
	public void setBlack()					{ set(0.f,0.f,0.f,1.f); }
	public void setWhite()					{ set(1.f,1.f,1.f,1.f); }
	
	public static final ZColor constBlack = new ZColor(0,0,0,1);
	public static final ZColor constWhite = new ZColor(1,1,1,1);
	public static final ZColor constRed = new ZColor(1,0,0,1);
	public static final ZColor constGreen = new ZColor(0,1,0,1);
	public static final ZColor constBlue = new ZColor(0,0,1,1);
	public static final ZColor constCyan = new ZColor(0,1,1,1);
	public static final ZColor constYellow = new ZColor(1,0,1,1);
	public static final ZColor constMagenta = new ZColor(1,1,0,1);
	
	public static final ZColor constR = new ZColor(1,0,0,0);
	public static final ZColor constG = new ZColor(0,1,0,0);
	public static final ZColor constB = new ZColor(0,0,1,0);
	public static final ZColor constA = new ZColor(0,0,0,1);
	
	
	public void interpolateSoft(ZColor start, ZColor end, float coef)
	{
		coef = 0.5f*(1.f-(float)Math.cos((double)coef*Math.PI));
		interpolateLinear(start,end,coef);
	}
	
	public void interpolateLinear(ZColor start, ZColor end, float coef)
	{
		mulAddMul(start,1.f-coef,end,coef);
	}

}
