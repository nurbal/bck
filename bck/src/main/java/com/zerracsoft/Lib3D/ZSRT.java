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


/*
 * 0 4 8  12     Vx
 * 1 5 9  13  x  Vy
 * 2 6 10 14     Vz
 * 3 7 11 15     1
 */

public class ZSRT {

	// native NDK functions
	protected native long JNIconstructor();
	protected native void JNIdestructor(long obj);
	protected native void JNIsetIdentity(long obj);
	protected native void JNIgetMatrix(long obj,long matrix);
	protected native void JNIsetTranslation(long obj,long v);
	protected native void JNIsetTranslation2(long obj, float x, float y, float z);
	protected native void JNIsetRotation(long obj,long q);
	protected native void JNIsetRotation2(long obj, long v, float angle);
	protected native void JNIsetScale(long obj,long v);
	protected native void JNIsetScale2(long obj, float x, float y, float z);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	 
	public ZSRT()			{ mNativeObject = JNIconstructor(); }   
	
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject);
		super.finalize();
	} 
	
	public ZMatrix getMatrix()
	{
		ZMatrix m = new ZMatrix();
		JNIgetMatrix(mNativeObject,m.getNativeObject());
		return m;
	}

	public void setTranslation(ZVector t)					{ JNIsetTranslation(mNativeObject,t.getNativeObject()); }
	public void setTranslation(float x, float y, float z)	{ JNIsetTranslation2(mNativeObject,x,y,z); }
	public void setScale(ZVector t)							{ JNIsetScale(mNativeObject,t.getNativeObject()); }
	public void setScale(float x, float y, float z)			{ JNIsetScale2(mNativeObject,x,y,z); }
	public void setIdentity()								{ JNIsetIdentity(mNativeObject); }
	public void setRotation(ZQuaternion r)					{ JNIsetRotation(mNativeObject,r.getNativeObject()); }
	public void setRotation(ZVector axe, float angle)		{ JNIsetRotation2(mNativeObject,axe.getNativeObject(),angle); }
}

/*
public class ZSRT {
	private ZMatrix mMatrix = new ZMatrix();
	private ZVector mTranslation = new ZVector();
	private ZVector mScale = new ZVector(1.f,1.f,1.f);
	private ZQuaternion mRotation = new ZQuaternion();
	private boolean mDirty = true;
	
	private void rebuildMatrix()
	{
		if (!mDirty) return;
		mMatrix.setIdentity();
		// rotation/scale
		float x = mRotation.get(0);
		float y = mRotation.get(1);
		float z = mRotation.get(2);
		float w = mRotation.get(3);
		float x2 = 2.f*x*x; 
		float y2 = 2.f*y*y;
		float z2 = 2.f*z*z;
		float xy = 2.f*x*y;
		float yz = 2.f*y*z;
		float xz = 2.f*z*x;
		float xw = 2.f*x*w;
		float yw = 2.f*y*w;
		float zw = 2.f*z*w;
		float sx = mScale.get(0);
		float sy = mScale.get(1);
		float sz = mScale.get(2);
		mMatrix.set(0,sx*(1.f-y2-z2));	mMatrix.set(4,sy*(xy-zw));		mMatrix.set(8,sz*(xz+yw));
		mMatrix.set(1,sx*(xy+zw));		mMatrix.set(5,sy*(1.f-x2-z2));	mMatrix.set(9,sz*(yz-xw));
		mMatrix.set(2,sx*(xz-yw));		mMatrix.set(6,sy*(yz+xw));		mMatrix.set(10,sz*(1.f-x2-y2));
		// translation
		mMatrix.set(12,mTranslation.get(0));
		mMatrix.set(13,mTranslation.get(1));
		mMatrix.set(14,mTranslation.get(2));
		mDirty = false;
	}

	public ZMatrix getMatrix()
	{
		rebuildMatrix();
		return mMatrix;
	}
	
	public void setTranslation(ZVector t)
	{
		mTranslation.copy(t);
		mDirty=true;
	}
	
	public void setTranslation(float x, float y, float z)
	{
		mTranslation.set(x, y, z);
		mDirty=true;
	}
	
	public void setRotation(ZQuaternion r)
	{
		mRotation.copy(r);
		mDirty=true;
	}
	
	public void setRotation(ZVector axe, float angle)
	{
		mRotation.set(axe,angle);
		mDirty=true;
	}

	public void setScale(ZVector s)
	{
		mScale.copy(s);
		mDirty=true;
	}
	
	public void setScale(float x, float y, float z)
	{
		mScale.set(x, y, z);
		mDirty=true;
	}
	
	public void setIdentity()
	{
		mTranslation.zero();
		mScale.set(1.f,1.f,1.f);
		mRotation.zero();
		mDirty=true;
	}
}
*/