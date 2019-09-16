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

/* my own little vector class... in fixed-point :) */

public class ZVector {
	
	@Override
	public String toString() {
		// format [ 1.2 ; 2.3 ; 3.4 ]
		return "[ "+Float.toString(get(0))+" ; "+Float.toString(get(1))+" ; "+Float.toString(get(2))+" ]";
	}
	// native NDK functions
	protected native long JNIconstructor();
	protected native void JNIdestructor(long obj);
	protected native void JNIset(long dst, float x, float y, float z);
	protected native void JNIset2(long dst, int index, float value);
	protected native float JNIget(long obj, int index);
	protected native void JNIadd(long dst, long src);
	protected native void JNIadd2(long dst1,long src1,long src2);
	protected native void JNIadd3(long dst,int index,float value);
	protected native void JNIadd4(long dst,float x, float y, float z);
	protected native void JNIsub(long dst,long src);
	protected native void JNIsub2(long dst,long src1,long src2);
	protected native void JNIcopy(long dst,long src);
	protected native float JNIdot(long v1,long v2);
	protected native void JNIcross(long dst,long src1,long src2);
	protected native float JNInorme(long obj);
	protected native void JNInormalize(long obj);
	protected native void JNImul(long dst,float f);
	protected native void JNImul2(long dst,long src,float f);
	protected native void JNIaddMul(long dst,long src,float f);
	protected native void JNImulAddMul(long dst,long src1,float f1,long src2,float f2);
	protected native boolean JNIisEqual(long v1, long v2);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	
	public int F2I(float f) {return (int)(f*65536.f);}
	public float I2F(int i) {return (float)i/65536.f;}
	public int mulI(int a, int b) {return (int)(long)(((long)a*(long)b)>>16);}
	
	public ZVector() 
	{
		mNativeObject = JNIconstructor();   
	}
	public ZVector(float x, float y, float z)
	{
		mNativeObject = JNIconstructor();
		JNIset(mNativeObject,x,y,z); 
	}
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject);
		super.finalize();
	}

	
	// setters
	public void set(float X, float Y, float Z)	{ JNIset(mNativeObject,X,Y,Z); }
	public void set(int index, float value)		{ JNIset2(mNativeObject,index, value); }
	
	// getters
	public float get(int index)					{ return JNIget(mNativeObject, index); }				

	// basic vector operations
	public void add(ZVector other)			{ JNIadd(mNativeObject,other.mNativeObject); }
	public void add(float X, float Y, float Z)	{ JNIadd4(mNativeObject,X,Y,Z); }
	public void add(int index, float value)	{ JNIadd3(mNativeObject,index,value); }
	public void add(ZVector v1,ZVector v2)	{ JNIadd2(mNativeObject,v1.mNativeObject,v2.mNativeObject); }
	
	public void sub(ZVector other)			{ JNIsub(mNativeObject,other.mNativeObject); }
	public void sub(ZVector v1,ZVector v2)	{ JNIsub2(mNativeObject,v1.mNativeObject,v2.mNativeObject); } 

	public float dot(ZVector other)			{ return JNIdot(mNativeObject,other.mNativeObject); } 
	public void cross(ZVector a,ZVector b)	{ JNIcross(mNativeObject,a.mNativeObject,b.mNativeObject); }
	 
	public void mul(float f) 				{ JNImul(mNativeObject,f); }
	public void mul(ZVector v, float f)		{ JNImul2(mNativeObject,v.mNativeObject,f); }

	public void addMul(ZVector v, float f)	{ JNIaddMul(mNativeObject,v.mNativeObject,f); }
	public void mulAddMul(ZVector v1, float f1, ZVector v2, float f2) 	{ JNImulAddMul(mNativeObject,v1.mNativeObject,f1,v2.mNativeObject,f2); }

	public void copy(ZVector other)		{ JNIcopy(mNativeObject,other.mNativeObject); }

	public float norme()				{ return JNInorme(mNativeObject); }
	public boolean isEqual(ZVector other) { return JNIisEqual(mNativeObject,other.mNativeObject); }
	
	public void normalize()				{ JNInormalize(mNativeObject);	}
	
	public void zero()					{ set(0.f,0.f,0.f); }
	
	public static final ZVector constZero = new ZVector(0,0,0);
	public static final ZVector constX = new ZVector(1,0,0);
	public static final ZVector constY = new ZVector(0,1,0);
	public static final ZVector constZ = new ZVector(0,0,1);
	public static final ZVector constXNeg = new ZVector(-1,0,0);
	public static final ZVector constYNeg = new ZVector(0,-1,0);
	public static final ZVector constZNeg = new ZVector(0,0,-1);
	
	// Calcul de la normale � une face (sens direct)
	private static ZVector AB = new ZVector();
	private static ZVector BC = new ZVector();
	private static ZVector CA = new ZVector();
	
	public void faceNormal(ZVector a,ZVector b,ZVector c)
	{
		AB.sub(b,a);
		BC.sub(c,b);
		cross(AB,BC);
		normalize();
	}
	
	private static ZVector Xa = new ZVector();
	private static ZVector Xb = new ZVector();
	private static ZVector Xc = new ZVector();
	private static ZVector Ya = new ZVector();
	private static ZVector Yb = new ZVector();
	private static ZVector Yc = new ZVector();
	private static ZVector AV = new ZVector();
	private static ZVector P = new ZVector();
	private static ZVector N = new ZVector();
	private static ZVector AP = new ZVector();
	private static ZVector BP = new ZVector();
	private static ZVector CP = new ZVector();
	
	public void faceClosestPoint(ZVector A,ZVector B,ZVector C,ZVector V)
	{
		AB.sub(B,A); float ab=AB.norme();
		BC.sub(C,B); float bc=BC.norme();
		CA.sub(A,C); float ca=CA.norme();

		Ya.mul(BC,1.f/bc);
		Yb.mul(CA,1.f/ca);
		Yc.mul(AB,1.f/ab);
		
		N.cross(Ya,Yb); N.normalize();

		Xa.cross(Ya,N);
		Xb.cross(Yb,N);
		Xc.cross(Yc,N);
		
		// projet� sur la face
		AV.sub(V,A); 
		P.copy(V); P.addMul(N, -AV.dot(N));
		
		AP.sub(P,A);
		BP.sub(P,B);
		CP.sub(P,C);
		
		float xa = Xa.dot(BP);
		float xb = Xb.dot(CP);
		float xc = Xc.dot(AP);
		
		// int�rieur � la face
		if (xa<0.f && xb<0.f && xc<0.f) 
			{copy(P); return;}
	
		float ya=Ya.dot(BP);
		float yb=Yb.dot(CP);
		float yc=Yc.dot(AP);
		
		// A, B ou C
		if (yb>=ca && yc<=0.f) {copy(A); return;}
		if (yc>=ab && ya<=0.f) {copy(B); return;}
		if (ya>=bc && yb<=0.f) {copy(C); return;}
		// sur AB
		if (xc>0.f && yc>=0.f && yc<=ab)
		{
			copy(A);
			addMul(Yc,yc);
			return;
		}
		// sur BC
		if (xa>0.f && ya>=0.f && ya<=bc)
		{
			copy(B);
			addMul(Ya,ya);
			return;
		}
		// sur CA
		//if (xb>0.f && yb>=0.f && yb<=ca)
		{
			copy(C);
			addMul(Yb,yb);
		}
		
	}

	public float faceProjectedPoint(ZVector A,ZVector B,ZVector C,ZVector From, ZVector Direction)
	{
		// cotés
		AB.sub(B,A); float ab=AB.norme();
		BC.sub(C,B); float bc=BC.norme();
		CA.sub(A,C); float ca=CA.norme();
		Ya.mul(BC,1.f/bc);
		Yb.mul(CA,1.f/ca);
		Yc.mul(AB,1.f/ab);
		// normale
		N.cross(Ya,Yb); N.normalize();
		
		// point dans le plan de la face
		// projet� sur la face
		AV.sub(From,A); 
		float DirN = N.dot(Direction);
		if (DirN>=0) 
			return -1;
		float dist =  -AV.dot(N)/DirN;
		P.copy(From); P.addMul(Direction,dist);
		
		// perpendiculaires extérieures
		Xa.cross(Ya,N);
		Xb.cross(Yb,N);
		Xc.cross(Yc,N);

		AP.sub(P,A);
		BP.sub(P,B);
		CP.sub(P,C);
		
		float xa = Xa.dot(BP);
		float xb = Xb.dot(CP);
		float xc = Xc.dot(AP);
		
		// int�rieur � la face
		if (xa<0.f && xb<0.f && xc<0.f) 
			{copy(P); return dist;}
	
		return -1;
	}

	public void interpolateSoft(ZVector start, ZVector end, float coef)
	{
		coef = 0.5f*(1.f-(float)Math.cos((double)coef*Math.PI));
		interpolateLinear(start,end,coef);
	}
	
	public void interpolateLinear(ZVector start, ZVector end, float coef)
	{
		mulAddMul(start,1.f-coef,end,coef);
	}

	
}
