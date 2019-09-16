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


import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class ZRenderer implements Renderer
{
	// no constructor/destructor here, since we use a native singleton

    private static native void JNIInit();
    private static native void JNIResize(int w, int h);
    private static native void JNIPreRender(long scene);
    private static native void JNIRender(long scene);
    //private static native void JNIDone();
    //private static native void JNIsetCameraFrustum(float fov, float near, float far);
    //private static native void JNIsetCameraPos(int pos, int lookat, int up);
    private static native void JNIsetCamera(long cam);
    private static native float JNIscreenToViewportX(float x);
    private static native float JNIscreenToViewportY(float y);
    private static native float JNIsetFadeOut(float progress);	
    private static native int JNIgetScreenWidth();
    private static native int JNIgetScreenHeight();
    private static native float JNIgetViewportWidthF();
    private static native float JNIgetViewportHeightF();
    private static native float JNIgetViewportPixelSize();
    
    // emulate smaller resolutions
    private static native void JNIemulateScreenSize(int width, int height);	// 0 = max size available
    
    
	// mise en pause du rendering
    public static class RenderingControler
    {
    	public void suspend(boolean suspend)
    	{
    		
    		synchronized(this)
    		{
    			if (suspend)
    			{
    				if (mRenderingSuspended==0) ZActivity.instance.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    				mRenderingSuspended++;
    			}
    			else 
    			{
    				mRenderingSuspended--;
    				if (mRenderingSuspended==0) ZActivity.instance.mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    			}
    		}
    		// on sort du bloc synchronized pour eviter les deadlocks...
    		while (suspend && mRenderingInProgress && mRenderingThreadID != Thread.currentThread().getId())
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
       	public boolean tryBeginRendering()
    	{
    		synchronized(this)
    		{
    			if (mRenderingSuspended>0) 
    				return false;
    			mRenderingInProgress = true;
    			mRenderingThreadID = Thread.currentThread().getId();
    			return true;
    		}
    		
    	}
       	public void endRendering()
    	{
    		synchronized(this)
    		{
    			mRenderingInProgress = false;
    		}
    	}
       	
		private int mRenderingSuspended = 0;	// 
		private boolean mRenderingInProgress = false;
		private long mRenderingThreadID = 0;
    }
    
    private static RenderingControler mRenderingControler  = new RenderingControler();
    
	// mise en pause du rendering (et attente si nécessaire)
	public static void suspendRendering(boolean p)
	{
		mRenderingControler.suspend(p);
	}
	
	public static boolean mEnabled = false; // pour garder une trace des onPause/onResume
	
	// multitexturing (pour le bump mapping)
	static protected boolean mBumpMappingSupported = false;
	static public boolean isBumpMappingSupported() {return  mBumpMappingSupported;}
	
	// point-sprites (pour les particle systems)
	static protected boolean mPointSpriteSupported = false;
	static public boolean isPointSpriteSupported() {return  mPointSpriteSupported;}

	public static int getScreenWidth() { return JNIgetScreenWidth(); }
	public static int getScreenHeight() { return JNIgetScreenHeight(); }
	
	public static void emulateScreenSize(int width, int height) { JNIemulateScreenSize(width,height); }
	
	public static float getViewportWidthF() {return JNIgetViewportWidthF();}
	public static float getViewportHeightF() {return JNIgetViewportHeightF();}
	public static float getViewportPixelSize() {return JNIgetViewportPixelSize();}
	
	public static float screenToViewportXF(float x)	{ return JNIscreenToViewportX(x); }
	public static float screenToViewportYF(float y)	{ return JNIscreenToViewportY(y); }

	static public float getScreenRatioF()	{ return (float)getScreenWidth()/(float)getScreenHeight(); }

	public static float alignScreenGrid(float x)
	{
		int screenX = Math.round(x / getViewportPixelSize());
		return (float)screenX * getViewportPixelSize();
	}	
	
	private static ZCamera mCamera;
	public static void setCamera(ZCamera cam) {mCamera=cam; JNIsetCamera(cam.getNativeObject());}
	public static ZCamera getCamera() {return mCamera;}
	
	public static void setFadeOut(float progress)							{ JNIsetFadeOut(progress); }
	
	private ZActivity		mActivity;
	private long 			mLastUpdateTime = 0;
	private int				mMetrixFrameCount = 0;
	private int				mMetrixCount = 0;
	private int  			mMetrixTime = 0;
	private float			mMetrixFPS = 0.f;
	public float getFPS() {return mMetrixFPS;}

	public ZRenderer(ZActivity activity)
	{		
		// Context for resource access
		mActivity = activity;
	}

	public static void reloadTextures()
	{
		bMaterialLoadNeeded = true;
	}
	
	static public Object renderSync = new Object();
	
	protected int mFrameTime;
	private boolean mFirstUpdate = true;
	
	private static ArrayList<Runnable>mUpdateRunnables = new ArrayList<Runnable>(32);
	public static void postUpdateRunnable(Runnable r)
	{
		synchronized(mUpdateRunnables)
		{
			mUpdateRunnables.add(r);
		}
	}

	public void onDrawFrame(GL10 gl) 
	{

		synchronized(renderSync)
		{
		
			if (!mEnabled)
				return;
			
			// pump all update runnables
			synchronized(mUpdateRunnables)
			{
				while (!mUpdateRunnables.isEmpty())
					mUpdateRunnables.remove(0).run();
			}
			
			//Log.i("Zrenderer(java)","onDrawFrame 1");
			
			// elpasedtime calculation
	        long time = SystemClock.uptimeMillis();
	        mFrameTime = (int)(time - mLastUpdateTime);
	        mLastUpdateTime = time;
	        if (mFrameTime<0) mFrameTime+=0x80000000;  
	        if (mFirstUpdate) mFrameTime=0;
	        
			
			//Log.i("Zrenderer(java)","onDrawFrame 2");
	        
	        
	        // first update notification
	        boolean callPostFirstUpdate = mFirstUpdate;
	        if (mFirstUpdate)
	        {
	        	mFirstUpdate = false;
	        	// create and load materials
	        	mActivity.createMaterials(gl);
	        	//bMaterialLoadNeeded = true;
		        mActivity.mScene.loadMaterials(gl);
		        bMaterialLoadNeeded = false;
	       	
	        	// init everything before first update
	        	mActivity.onPreFirstUpdate(gl);
	        }
	        
	        if (bMaterialLoadNeeded)	// reload textures ?
	        {
		        //mActivity.deleteMaterials(gl); // done in loadMaterials
		        mActivity.mScene.loadMaterials(gl);
		        bMaterialLoadNeeded = false;
		        
	        }
	       
	        // metrics
	        mMetrixTime += mFrameTime; 
	        mMetrixFrameCount++;
	        if (mMetrixTime>=2000)
	        {
	        	mMetrixFPS = 1000.f * (float)mMetrixFrameCount / (float)mMetrixTime;
	        	mMetrixTime = 0;
	        	mMetrixFrameCount = 0;
	        	mMetrixCount++;
	        	if (mMetrixCount==5)
	        	{
	        		mMetrixCount = 0; 
	        	}
	        	mActivity.onMetrixFPS(mMetrixFPS);
	        }
	       
	       // activity update
	        if (mFrameTime>100) mFrameTime = 100;
	        
	        // update synchrone ou asynchrone ? Choose your destiny
	        mActivity.update(mFrameTime);
	        
			// rendu suspendu ?
	        if (!mRenderingControler.tryBeginRendering()) return;
	              
	        // prepare for rendering, BEFORE game update (so that transformers can be changed during rendering)
			JNIPreRender(mActivity.mScene.getNativeObject());
	        
	         
	        
	        //Thread updateThread = new Thread() {public void run() {	mActivity.update(mFrameTime);}};
	        //updateThread.start();
			
	        //ZActivity.handler.post( new Runnable() {public void run() {	mActivity.update(mFrameTime);}});
	
	        
			//Log.i("Zrenderer(java)","onDrawFrame 4");
			 		
			// THE RENDU (fast)
			JNIRender(mActivity.mScene.getNativeObject());
			//Log.i("Zrenderer(java)","onDrawFrame 5");
	
	        if (callPostFirstUpdate)
	        	mActivity.onPostFirstUpdate();
	        
	        // screenshot?
	        if (mScreenshotWanted)
	        	mScreenshotReceiver.onScreenshotTaken(getScreenshot(gl));
	        mScreenshotWanted = false;
	        
			// fin du rendu
	        mRenderingControler.endRendering();
	        
	        
			//Log.i("Zrenderer(java)","onDrawFrame end");
	        
		} // synchronized
	}

	public void onSurfaceChanged(GL10 gl, int width, int height)
	{ 
		gl.glViewport(0, 0, width, height);
		JNIResize(width,height);
	}

	private static boolean bMaterialLoadNeeded = false;
	
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) 
	{
		JNIInit();
        bMaterialLoadNeeded = true;	// deffered material loading
	}

	
	// take a screenshot and return a Bitmap
	public static void takeScreenshot(ZScreenshotReceiver receiver) 
	{
		mScreenshotWanted=true;
		mScreenshotReceiver = receiver;
	}
	private static boolean mScreenshotWanted = false;
	private static ZScreenshotReceiver mScreenshotReceiver = null;
	private Bitmap getScreenshot(GL10 gl)
	{
		// code taken from http://blog.javia.org/android-opengl-es-screenshot/
/*
		int size = getScreenWidth() * getScreenHeight();
		ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
		buf.order(ByteOrder.nativeOrder());
		gl.glReadPixels(0, 0, getScreenWidth(), getScreenHeight(), GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buf);
		int data[] = new int[size];
		buf.asIntBuffer().get(data);
		buf = null;
		Bitmap bitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(), Bitmap.Config.RGB_565);
		bitmap.setPixels(data, size-getScreenWidth(), -getScreenWidth(), 0, 0, getScreenWidth(), getScreenHeight());
		data = null;

		short sdata[] = new short[size];
		ShortBuffer sbuf = ShortBuffer.wrap(sdata);
		bitmap.copyPixelsToBuffer(sbuf);
		
		for (int i = 0; i < size; ++i) {
		    //BGR-565 to RGB-565
		    short v = sdata[i];
		    sdata[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
		}
		sbuf.rewind();
		bitmap.copyPixelsFromBuffer(sbuf);
		return bitmap;
*/		
		int w = getScreenWidth();
		int h = getScreenHeight();
        int b[]=new int[w*h];
        int bt[]=new int[w*h];
        IntBuffer ib=IntBuffer.wrap(b);
        ib.position(0);
        gl.glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);
        for(int i=0; i<h; i++)
        {//remember, that OpenGL bitmap is incompatible with Android bitmap
         //and so, some correction need.        
             for(int j=0; j<w; j++)
             {
                  int pix=b[i*w+j];
                  int pb=(pix>>16)&0xff;
                  int pr=(pix<<16)&0x00ff0000;
                  int pix1=(pix&0xff00ff00) | pr | pb;
                  bt[(h-i-1)*w+j]=pix1;
             }
        }                  
        Bitmap sb=Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;

		/*
			try {
			    FileOutputStream fos = new FileOutputStream("/sdcard/screeshot.png");
			    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			    fos.flush();
			    fos.close();
			} catch (Exception e) {
			    // handle
			}
			*/
	}
	
}
