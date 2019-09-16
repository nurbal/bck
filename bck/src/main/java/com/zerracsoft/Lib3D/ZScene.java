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

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;


public class ZScene { 

	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long scene);
	
	protected native static void JNIsetLight(long scene,float ambient,float diffuse,float specular, long position);
	protected native static void JNIupdate(long scene,int elapsedTime);
	protected native static void JNIaddInstance(long scene,long inst);
	protected native static void JNIaddInstanceSky(long scene,long inst);
	protected native static void JNIaddInstance2D(long scene,long inst);
	protected native static void JNIclear(long scene);
	protected native static void JNIremoveInstance(long scene,long inst);
	protected native static void JNIlinkMaterials(long scene);

	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}

	protected ZActivity mActivity;
	public ZScene(ZActivity activity)
	{
		mNativeObject = JNIconstructor();
		mActivity = activity;
	}
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject);
		super.finalize();
	}   
	
	public void setLight(float ambient,float diffuse,float specular, ZVector position) {JNIsetLight(mNativeObject,ambient,diffuse,specular,position.getNativeObject());}
	
	// les listes sont gardees cote natif.
	// les objets sont detruits une fois les references ici detruites et les objets retirees de la scene (smart pointers)

	public static final int mMaterialsMax = 50;
	protected  ArrayList<ZMaterial>	mMaterials = new ArrayList<ZMaterial>(mMaterialsMax);

	private ZMenu	mMenu = null;
	public void setMenu(ZMenu menu)
	{
		if (mMenu!=null)
			mMenu.destroy();
		mMenu = menu;
		if (mMenu!=null)
			mMenu.update(0); // pour eviter les frames fantomes...
	}
	public void setMenuWithTransition(ZMenu menu)
	{
		if (mMenu==null) 
			setMenu(menu);
		else
			mMenu.exitToAnotherMenu(menu);
	}
	
	public ZMenu getMenu()
	{
		return mMenu;
	}
	
	protected void deleteMaterials(GL10 gl)
	{
		/*
		 * TODO
		Log.i("C3dActivity","deleteMaterials");
        for (int i=0; i<m3dObjects.size(); i++)
        	m3dObjects.get(i).unlinkMaterial();
        for (int i=0; i<m3dSkyObjects.size(); i++)
        	m3dSkyObjects.get(i).unlinkMaterial();
		for (int i=0; i<mMaterials.size(); i++)
		{
			ZMaterial mat = mMaterials.get(i);
			mat.destroy(gl);
		}		
		mMaterials.clear();
		 */
	}
	
	public ZMaterial getMaterial(String name)
	{
		for (int i=0; i<mMaterials.size(); i++)
		{
			ZMaterial mat = mMaterials.get(i);
			if (mat.mName.equals(name))
				return mat;
		}		
		return null;
	}
	
	public void update(int elapsedTime)
	{
		// update instances
		JNIupdate(mNativeObject,elapsedTime);
		
		// update menu
		if (mMenu!=null)
		{
			if (mMenu.mState == ZMenu.State.DONE) 
				mMenu=null;
			else 
				mMenu.update(elapsedTime);
		}
	}

	public ZMaterial createMaterial(GL10 gl,String name,int resourceID,boolean clamp,int subScale)
	{
		ZMaterial mat = new ZMaterial(gl,name,ZActivity.instance,resourceID,clamp,subScale);
		mMaterials.add(mat);
		return mat;
	}

	public ZMaterial createMaterial(GL10 gl,String name,boolean clamp,int subScale)
	{
		ZMaterial mat = new ZMaterial(gl,name,ZActivity.instance,clamp,subScale);
		mMaterials.add(mat);
		return mat;
	}
	
	public void loadMaterials(GL10 gl)
	{
		mActivity.onMaterialsLoadingStart(mMaterials.size());
		for (int i=0; i<mMaterials.size(); i++)
		{
			mMaterials.get(i).load(gl);
			mActivity.onMaterialsLoading(i+1,mMaterials.size());
		}
		mActivity.onMaterialsLoaded();
	}
	
	public void add(ZInstance inst)
	{
//		ZRenderer.suspendRendering(true);
		JNIaddInstance(mNativeObject,inst.getNativeObject());
//		ZRenderer.suspendRendering(false);
	}

	public void addSky(ZInstance inst)
	{
//		ZRenderer.suspendRendering(true);
		JNIaddInstanceSky(mNativeObject,inst.getNativeObject());
//		ZRenderer.suspendRendering(false);
	}

	// la cam�ra 2D est une cam ortho X � droite, Y � gauche, Z sortant de l'�cran
	// X = {-getScreenRatio() , +getScreenRatio()}
	// Y = {-1 , +1}
	// Z = {-100 , +100}
	public void add2D(ZInstance inst) 
	{
//		ZRenderer.suspendRendering(true);
		JNIaddInstance2D(mNativeObject,inst.getNativeObject());
//		ZRenderer.suspendRendering(false);
	}
	
/*
	public ZInstance add(ZObject obj)
	{
		ZInstance inst = new ZInstance(obj);
		add(inst);
		return inst;
	}

	public ZInstance addSky(ZObject obj)
	{
		ZInstance inst = new ZInstance(obj);
		addSky(inst);
		return inst;
	}

	public ZInstance add2D(ZObject obj)
	{
		ZInstance inst = new ZInstance(obj);
		add2D(inst);
		return inst;
	}
*/

	public void remove(ZInstance inst) 
	{
		if (inst==null) return;
//		ZRenderer.suspendRendering(true);
		JNIremoveInstance(mNativeObject,inst.getNativeObject());
//		ZRenderer.suspendRendering(false);
	}

	public void clear()
	{
//		ZRenderer.suspendRendering(true);
		JNIclear(mNativeObject);
//		ZRenderer.suspendRendering(false);
	}

}
