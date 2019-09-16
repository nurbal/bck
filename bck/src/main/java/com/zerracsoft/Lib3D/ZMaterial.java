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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class ZMaterial {

	// native NDK functions
	protected native static long JNIconstructor(boolean clamp);
	protected native static void JNIdestructor(long obj);
	
	protected native static void JNIsetID(long obj,int id);
	protected native static void JNIsetAlpha(long obj,boolean alpha);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}

	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}   
	
	public String 	mName;
	protected int[]	mID;	
	public int		mWidth = 0;
	public int		mHeight = 0;
	protected boolean mHasAlpha = false;
	protected Context mContext;
	protected int mResourceID;
	protected int mSubScale = 1;
	
	public void SetHasAlpha(boolean hasAlpha)
	{
		mHasAlpha = hasAlpha;
		JNIsetAlpha(mNativeObject,mHasAlpha);
	}
	
	public ZMaterial(GL10 gl,String name,Context context,int resourceID,boolean clamp,int subScale)
	{
		mNativeObject = JNIconstructor(clamp); 
		// intialize the texture ID and the name....
		mName = name;
		mContext = context;
		mResourceID = resourceID;
		mSubScale = subScale;
		
		
		mID = new int[1];
		gl.glGenTextures(1, mID, 0);
		JNIsetID(mNativeObject,mID[0]);	
		
	}

	public ZMaterial(GL10 gl,String name,Context context,boolean clamp,int subScale)
	{
		mNativeObject = JNIconstructor(clamp); 
		// intialize the texture ID and the name....
		mName = name;
		mContext = context;
		mResourceID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		mSubScale = subScale;
		
		
		mID = new int[1];
		gl.glGenTextures(1, mID, 0);
		JNIsetID(mNativeObject,mID[0]);	
		
	}

	public void load(GL10 gl)
	{
/*		
	    InputStream is = mContext.getResources().openRawResource(mResourceID);
	    Bitmap bitmap;
	    try {
	        bitmap = BitmapFactory.decodeStream(is);
	        if (bitmap==null)
	        	Log.e("Lib3D","ZMaterial.load bitmap load error in material \""+mName+"\"");
	    } 
	    catch (Exception e)
	    {
			Log.e("Lib3D","ZMaterial.load error in BitmapFactory.decodeStream for material \""+mName+"\"");
			return;
	    }
	    finally 
	    { 
	        try { is.close(); } catch(IOException e) 
	        {
				Log.e("Lib3D","ZMaterial.load error : "+e.getMessage());
	        	return; 
	        }
	    }
*/
	    Bitmap bitmap;
	    try {
	    	BitmapFactory.Options opt = new BitmapFactory.Options();
	    	opt.inSampleSize = mSubScale;
	    	opt.inScaled=false; // NEVER scale textures depending on screen DPI !!!!
	        bitmap = BitmapFactory.decodeResource(mContext.getResources(),mResourceID,opt);
	        if (bitmap==null)
	        	Log.e("Lib3D","ZMaterial.load bitmap load error in material \""+mName+"\"");
	    } 
	    catch (Exception e)
	    {
			Log.e("Lib3D","ZMaterial.load error in BitmapFactory.decodeStream for material \""+mName+"\"");
			return;
	    }
		
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mID[0]);
	    mWidth = bitmap.getWidth();
	    mHeight = bitmap.getHeight();
	    SetHasAlpha(bitmap.hasAlpha());
    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); // TODO alpha foireux
	    bitmap.recycle();
	    
	}
	
	// destroy the texture...
	public void destroy(GL10 gl)
	{
		gl.glDeleteTextures(mID.length, mID, 0);
	}
	
}
