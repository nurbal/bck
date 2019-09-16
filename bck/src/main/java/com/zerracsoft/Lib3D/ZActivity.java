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

import javax.microedition.khronos.opengles.GL10;

import android.os.Handler;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class ZActivity extends android.app.Activity {

//	static { System.loadLibrary("Lib3D"); } 	// On est certain que c'est appel� ici avant toute reference � la lib
// not needed any more. Lib3D code has been moved to the "BCK" JNI module

	public static ZActivity instance;
	
	public FrameLayout mFrameLayout;
    public GLSurfaceView mGLSurfaceView;
	public ZRenderer mRenderer; 
	public ZScene	mScene;
	
    public static final Handler handler = new Handler();	// handler used for update
    
	
    static final int	SoundNbStreams = 8;
    protected SoundPool	mSoundPool = new SoundPool(SoundNbStreams,AudioManager.STREAM_MUSIC,0);	// the sound engine
	public boolean	mSoundEnabled =  true;
	
	Vibrator mVibrator;
	
	protected void initView()
	{
		mFrameLayout = new FrameLayout(this);
		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mFrameLayout.addView(mGLSurfaceView);
        setContentView(mFrameLayout);
	}
	
	public android.util.DisplayMetrics mDisplayMetrics = new android.util.DisplayMetrics();

	// cacher la barre de statut ICS (tr�s tr�s moche)
	public void hideICSStatusBar()
	{
		if (android.os.Build.VERSION.SDK_INT>=14)
			mGLSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
	
	protected ZTouchListener mTouchListener = new ZTouchListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// singleton init
        instance = this; 
        
        // get display metrics
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		
		// set the volume buttons to our sounds
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		// create our renderer
		mScene = new ZScene(this);
        mRenderer = new ZRenderer(this);

        // Create our Preview view and set it as the content of our
        // Activity
        initView();
        //if (mTranslucentBackground)        	mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        
        // for alpha support...
//        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);  
//        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        
        //if (mTranslucentBackground)        	mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        
        mGLSurfaceView.setRenderer(mRenderer);
		mGLSurfaceView.setOnTouchListener(mTouchListener);
		
        
        // load sounds
        loadSounds();
        
		// initialize Vibrator
		mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		//hideICSStatusBar();

	}

	public void vibrate (int time)
	{
		try{mVibrator.vibrate(time);} catch(Exception e){}		
	}
	
	@Override
	protected void onPause() {
		synchronized (ZRenderer.renderSync)	// on est certain de ne pas �tre en train de rendre...
		{
			super.onPause();
			mGLSurfaceView.onPause();
			ZRenderer.mEnabled = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
        mGLSurfaceView.onResume();
		hideICSStatusBar();	
		ZRenderer.mEnabled = true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		finish();	// fix anti-freeze après mise en veille del'appareil (comprends pas...)
	}
	
	public void update(int elapsedTime)
	{
		mTouchListener.update();	// traitement des inputs...
		mScene.update(elapsedTime);	// update de a scene...
	}
	
	
	
	protected void createMaterials(GL10 gl) {} // must be implemented to ... create materials :)
	
	public void onPreFirstUpdate(GL10 gl) {} // use it for initialization BEFORE first frame is rendered
	public void onPostFirstUpdate() {} // use it for initialization AFTER first frame is rendered (splash screen, etc...)
	public void onMaterialsLoadingStart(int total) {} // use it for notification
	public void onMaterialsLoading(int done, int total) {} // use it for notification
	public void onMaterialsLoaded() {} // use it for notification
	
	protected boolean mTouchMenu;
	protected float mTouchStartX,mTouchStartY;
/*
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{		
		try{Thread.sleep(33);} catch(Exception e){}
		
		float x = ZRenderer.screenToViewportXF(event.getX());
		float y = ZRenderer.screenToViewportYF(event.getY());	
		
		if (!handleMenuEvent(event.getAction(), event.getX(), event.getY()) && !(mScene.getMenu()!=null && mScene.getMenu().isTouchExclusive()))
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				mTouchStartX = x;
				mTouchStartY = y;
				onTouchStart(mTouchStartX,mTouchStartY);
				break;
			case MotionEvent.ACTION_UP:
				onTouchEnd(mTouchStartX,mTouchStartY, x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				onTouchMove(mTouchStartX,mTouchStartY, x, y);
				break;
			}
			
		}
		
		return super.onTouchEvent(event);
	}
*/	
	public boolean handleMenuEvent(int eventAction, float screenX, float screenY) // returns true if menu caught
	{
		float x = ZRenderer.screenToViewportXF(screenX);
		float y = ZRenderer.screenToViewportYF(screenY);	
		
		if (mScene.getMenu()==null) 
			mTouchMenu = false;
		
		switch (eventAction)
		{
		case MotionEvent.ACTION_DOWN:
			mTouchStartX = x;
			mTouchStartY = y;
			if (mScene.getMenu()!=null)
				mTouchMenu = mScene.getMenu().onTouchStart(mTouchStartX,mTouchStartY);
			break;
		case MotionEvent.ACTION_UP:
			if (mScene.getMenu()!=null)// && mTouchMenu)
				mScene.getMenu().onTouchEnd(mTouchStartX,mTouchStartY, x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mScene.getMenu()!=null)// && mTouchMenu)
				mScene.getMenu().onTouchMove(mTouchStartX,mTouchStartY, x, y);
			break;
		}
		
		return mTouchMenu;
		
	}
	
	// gestion de la touche "back"
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			// TODO: faire g�rer ca par mTouchListener...
			if (mTouchListener.handleBackKey())
				return true;
		}
		return super.onKeyDown(keyCode, event); 	
	}
	

/*	
	public void onTouchMove(float mTouchStartX2, float mTouchStartY2,
			float touchMoveX, float touchMoveY) {	
	}

	public void onTouchStart(float mTouchStartX2, float mTouchStartY2) {
		
	}

	public void onTouchEnd(float startX, float startY, float endX, float endY) {} // coords in [-1..1] range
*/
	// touch interactions; coordinates in screen values (pixels)
	public void onTouchMoveStart(float xStart, float yStart) {}
	public void onTouchMove(float xStart, float yStart, float x, float y) {}
	public void onTouchMoveEnd() {}
	public void onTouchPinchStart(float xStart, float yStart) {}
	public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) {}
	public void onTouchPinchEnd() {}
	
	
	// Sound
	public int playSnd(int ID,float volume)
	{
		if (!mSoundEnabled) return 0;
		return mSoundPool.play(ID, volume, volume, 0, 0, 1);
	}
	public int playSnd(int[] IDs,float volume) // variants version
	{
		if (!mSoundEnabled) return 0;
		int index = (int)(Math.random() * IDs.length);
		return mSoundPool.play(IDs[index], volume, volume, 0, 0, 1);
	}
	
	public void stopSnd(int ID)
	{
		mSoundPool.stop(ID);
	}
	
	public void loadSounds() {}
	
	// misc
	public void onMetrixFPS(float fps) {}

}
