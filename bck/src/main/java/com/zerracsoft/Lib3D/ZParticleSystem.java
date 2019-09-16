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

public class ZParticleSystem implements ZObject {

	// native NDK functions
	//protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	
	protected native static void JNIinit(long obj, int nbParticles);
	protected native static void JNIsetMaterial(long obj, long mat, int matVariantsX, int matVariantsY);
	protected native static void JNIsetEmmiterPos(long obj, long pos);
	protected native static void JNIpause(long obj, boolean paused);
	protected native static void JNIclearParticles(long obj);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	
//	public ZParticleSystem()			{ mNativeObject = JNIconstructor(); }   
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}   
	
	public boolean isAlpha() {return true;}	// by default... a p6 should always be in alpha!
	
	// material is global to particle system...
	private int 		mMaterialVariantsX,mMaterialVariantsY;
    private ZMaterial 	mMaterial = null;	// passé en private pour empecher d'y accéder directement (très important pour le lifecycle de l'application!)
	
	// helper
	public void setMaterial(String materialName, int matVariantsX, int matVariantsY)
	{
  		mMaterial = ZActivity.instance.mScene.getMaterial(materialName);
		mMaterialVariantsX = matVariantsX;
		mMaterialVariantsY = matVariantsY;
  		if (mMaterial!=null) 
  		{
  			JNIsetMaterial(mNativeObject, mMaterial.getNativeObject(),mMaterialVariantsX,mMaterialVariantsY);
  		}
	}

	public void setEmmiterPos(ZVector pos) { JNIsetEmmiterPos(mNativeObject,pos.getNativeObject()); }	
	// since particle systems are so CPU expensive, almost everything has to be done in C++
	
    public void init(int nbParticles) { JNIinit(mNativeObject,nbParticles); }

    public void pause(boolean paused) { JNIpause(mNativeObject,paused); }
    public void clearParticles() { JNIclearParticles(mNativeObject); }



}
