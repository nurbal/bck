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

package com.zerracsoft.bck;

import javax.microedition.khronos.opengles.GL10;


import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.Lib3D.ZFont.AlignH;
import com.zerracsoft.Lib3D.ZFont.AlignV;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.WindowManager;

public class GameActivity extends ZActivity {
 
	static { System.loadLibrary("BCK"); } 	// On est certain que c'est appel� ici avant toute reference � la lib

	PowerManager.WakeLock mWakeLock;

	public ZerracLogoInstance mZerracLogoInstance;
	public ZInstance mGameLogoInstance;

	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mWakeLock.release();	
		if (mUI!=null) 
			mUI.close();
		
		if (mGameStatus!=null) 
			mGameStatus.save(); // NULL POINTER EXCEPTION

		if (ShopManager.instance!=null) 
			ShopManager.instance.save(); 

		
	} 


	@Override
	protected void onResume() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
		mWakeLock.acquire();
		
		if (mUI!=null) 
			mUI.init();
		
		super.onResume();
	}


	static public Handler handler;
		
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	ShopManager.instance = new ShopManager();

        super.onCreate(savedInstanceState);
        
      //  ZRenderer.emulateScreenSize(1024,599);
        
		instance = this;
		handler = new Handler();
		
		// fullscreen mode
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		


    }
 
    
	public enum State
	{
		UNDEFINED,
		SPLASHSCREEN,
		GAMESTARTED
	}
	public State mState = State.UNDEFINED;
	public int mStateTime = 0;
	
	
	public void setState(State newState)
	{
		if (mState==newState) return;
		//State oldState = mState;
		mState = newState;
		mStateTime = 0;
		switch (newState)
		{
		
		}
	}
	
	public void updateState(int elapsedTime)
	{
		mStateTime += elapsedTime;
		switch (mState)
		{
		
		}
	}
    
	@Override
	public void onPreFirstUpdate(GL10 gl) 
	{
		super.onPreFirstUpdate(gl); 
	
		// load fonts
		mFontBig.load(this, "fontbig", mFontBigResourceID);
		mFont.load(this, "font", mFontResourceID);			

		
		// initialisations et cr�ations diverses
		mMeshBank = new MeshBank(getResources());
		MeshBank.get().loadAll();
		mGameStatus = new StatusGame();
		mGameStatus.load();
		
		// chargement du shop APRES le game status
		if (ShopManager.instance!=null)
			ShopManager.instance.load();
		
		ShopManager.instance.checkTransferedVirtualGoods();
		
		mLevel = new Level();
		mLevel.init(mGameStatus.getLastPlayedLevel()); 
		mLevel.loadGame();
		
		setUI(new UImenus(false));
//		setUI(new UIeditor2D());
		
	}
	
	
	@Override
	public void onPostFirstUpdate() 
	{
		super.onPostFirstUpdate();
	}

	public static ZFont mFont = new ZFont();
	public static ZFont mFontBig = new ZFont();
	private int mFontResourceID;
	private int mFontBigResourceID;
	
	
	@Override
	protected void createMaterials(GL10 gl) 
	{
		int subScale = 1;
		if (ZRenderer.getScreenHeight()<480)
		{
			mScene.createMaterial(gl, "fontbig", R.drawable.wimpout_24, true,1);	mFontBigResourceID = R.raw.wimpout_24;
			mScene.createMaterial(gl, "font", R.drawable.wimpout_16, true,1);		mFontResourceID = R.raw.wimpout_16;
			subScale = 2; // graphismes low-def...
		}
		else if (ZRenderer.getScreenHeight()<720)
		{
			mScene.createMaterial(gl, "fontbig", R.drawable.wimpout_36, true,1);	mFontBigResourceID = R.raw.wimpout_36;
			mScene.createMaterial(gl, "font", R.drawable.wimpout_24, true,1);		mFontResourceID = R.raw.wimpout_24;	
		}
		else 
		{
			mScene.createMaterial(gl, "fontbig", R.drawable.wimpout_48, true,1);	mFontBigResourceID = R.raw.wimpout_48;
			mScene.createMaterial(gl, "font", R.drawable.wimpout_36, true,1);		mFontResourceID = R.raw.wimpout_36;	
			
		}	
		
		
		
		
		mScene.createMaterial(gl, "p6_smoke", R.drawable.p6_smoke, true,1);
		mScene.createMaterial(gl, "p6_bar", R.drawable.p6_bar, true,1);
		mScene.createMaterial(gl, "p6_cable", R.drawable.p6_cable, true,1);
		mScene.createMaterial(gl, "p6_jack", R.drawable.p6_hydraulic, true,1);
		mScene.createMaterial(gl, "linkbar", R.drawable.bar, false,subScale);
		mScene.createMaterial(gl, "cable", R.drawable.cable, false,subScale);
		mScene.createMaterial(gl, "jackHead", R.drawable.jackhead, false,subScale); 
		mScene.createMaterial(gl, "jackBottomRetract", R.drawable.jackbottomretract, false,subScale); 
		mScene.createMaterial(gl, "jackBottomExpand", R.drawable.jackbottomexpand, false,subScale); 
		mScene.createMaterial(gl, "node2d", R.drawable.node2d, false,subScale);
		mScene.createMaterial(gl, "separator", R.drawable.separator, false,subScale); 
		
//		mScene.createMaterial(gl, "buttons", R.drawable.buttons, false);
		mScene.createMaterial(gl, "buttons", true,1);
		mScene.createMaterial(gl, "button", R.drawable.button, true,subScale);
		mScene.createMaterial(gl, "checked", R.drawable.checked, true,subScale);
		mScene.createMaterial(gl, "locked", R.drawable.locked, true,subScale);

		mScene.createMaterial(gl, "wind_gauge",true,1);
		mScene.createMaterial(gl, "wind_value",false,1);
		
		mScene.createMaterial(gl, "level_choice_frame", true,subScale);
		mScene.createMaterial(gl, "level_choice_button", R.drawable.level_choice_button, true,subScale);
		mScene.createMaterial(gl, "level_choice_button_locked", R.drawable.level_choice_button_locked, true,subScale);
		mScene.createMaterial(gl, "level_choice_left", R.drawable.level_choice_left, true,subScale);
		mScene.createMaterial(gl, "level_choice_right", R.drawable.level_choice_right, true,subScale);
		
		mScene.createMaterial(gl, "tower_goal_green", R.drawable.tower_goal_green, false,subScale);
		mScene.createMaterial(gl, "tower_goal_red", R.drawable.tower_goal_red, false,subScale);
		
		mScene.createMaterial(gl, "mud", R.drawable.mud, false,subScale);
		mScene.createMaterial(gl, "stone", R.drawable.stone, false,subScale);
		mScene.createMaterial(gl, "grass", R.drawable.grass, false,subScale);
		mScene.createMaterial(gl, "grass2d", R.drawable.grass2d, false,subScale);
		mScene.createMaterial(gl, "rails", R.drawable.rails, false,subScale);
		mScene.createMaterial(gl, "water", R.drawable.water, false,subScale);
		mScene.createMaterial(gl, "wood_clair", R.drawable.wood_clair, false,subScale);
		mScene.createMaterial(gl, "wood_fonce", R.drawable.wood_fonce, false,subScale);
		mScene.createMaterial(gl, "wood_blue", R.drawable.wheel_blue, false,subScale);
		mScene.createMaterial(gl, "wood_red", R.drawable.wheel_red, false,subScale);
		mScene.createMaterial(gl, "wood_yellow", R.drawable.wheel_yellow, false,subScale);
		mScene.createMaterial(gl, "wood_green", R.drawable.wheel_green, false,subScale);
		mScene.createMaterial(gl, "wood_magenta", R.drawable.wheel_magenta, false,subScale);
		
		mScene.createMaterial(gl, "bcklogo", R.drawable.bcklogo, true,subScale);
		mScene.createMaterial(gl, "zerraclogo", R.drawable.zerraclogo, true,subScale);
		mScene.createMaterial(gl, "menu_bg", R.drawable.menu_bg, true,subScale);
		
		mScene.createMaterial(gl, "win", R.drawable.win, true,subScale);
		mScene.createMaterial(gl, "loose", R.drawable.loose, true,subScale);

		mScene.createMaterial(gl, "select_cursor", R.drawable.select_cursor, true,subScale);
		mScene.createMaterial(gl, "node_highlight", R.drawable.node_highlight, true,subScale);
		
		mScene.createMaterial(gl, "matte_painting_water", R.drawable.decor2, true,subScale);
		mScene.createMaterial(gl, "matte_painting_back", R.drawable.matte_back, false,subScale);
		mScene.createMaterial(gl, "plexi", R.drawable.plexi, false,subScale);
				
		mScene.createMaterial(gl, "boat", R.drawable.boat, true,subScale);
		mScene.createMaterial(gl, "boat02", R.drawable.boat02, true,subScale);
		mScene.createMaterial(gl, "cube", R.drawable.cube, true,subScale);
	
		mScene.createMaterial(gl, "room01", R.drawable.room01, true,subScale);
		mScene.createMaterial(gl, "room_alpha", R.drawable.room_alpha, true,subScale);
		
		mScene.createMaterial(gl, "gare", R.drawable.gare, true,subScale);
		mScene.createMaterial(gl, "lego", R.drawable.lego, true,subScale);
		mScene.createMaterial(gl, "pen", R.drawable.pen, true,subScale);
		
		mScene.createMaterial(gl, "addon_free_build", true,subScale);
		mScene.createMaterial(gl, "addon_no_ads", true,subScale);
		mScene.createMaterial(gl, "addon_onboard_camera", true,subScale);
		mScene.createMaterial(gl, "addon_screenshots", true,subScale);
		mScene.createMaterial(gl, "addon_show_constraints", true,subScale);
		mScene.createMaterial(gl, "addon_skip_world", true,subScale);
		
		mScene.createMaterial(gl, "sound_on", true, subScale);
		mScene.createMaterial(gl, "sound_off", true, subScale);
		
	}	


	// debug text
	ZInstance mDebugStringVersionInstance; 
	ZInstance mDebugStringFPSInstance;
	ZInstance mDebugStringInfosInstance;
	
	// mesh bank
	MeshBank mMeshBank;
	
	// Level
	public Level mLevel;
	
	// game status
	public StatusGame mGameStatus;
	
	// User interface
	private UI mUI;
	public UI getUI() { return mUI; }
	public void setUI(UI ui)
	{
		if (mUI!=null) 
			mUI.close();
		mUI = ui;
		ui.init();
	}	
	
	static ZVector v = new ZVector();
	static ZVector tmp = new ZVector();
	@Override
	public void update(int elapsedTime) {

		super.update(elapsedTime);
		
		// check if renderer is still here....
		if (!ZRenderer.mEnabled) return;
		
		// debug text onscreen
		if (ReleaseSettings.DEBUG_SHOW_VERSION && mFont!=null) 
		{
			if (mDebugStringVersionInstance == null) 
			{
				ZMesh m = mFont.createStringMesh(ReleaseSettings.DEBUG_VERSION, AlignH.CENTER, AlignV.TOP);
				if (m!=null)
				{
					mScene.add2D(mDebugStringVersionInstance = new ZInstance(m));
					mDebugStringVersionInstance.setTranslation(new ZVector(0, 1, 0));
				}
			}
		}
		
		
		if (mLevel!=null)
			mLevel.update(elapsedTime*ReleaseSettings.DEBUG_TIME_FACTOR,mUI.isSimulationOn());
		
		if (mUI!=null)
			mUI.update(elapsedTime*ReleaseSettings.DEBUG_TIME_FACTOR);
		
		updateState(elapsedTime*ReleaseSettings.DEBUG_TIME_FACTOR);
		//updateCamera(elapsedTime);

		updateTrainSound(0.001f*(float)elapsedTime);
		
	}

/*
	static ProgressDialog mLoadingDialog = null;
	static Object mLoadingDialogSyncObject = new Object();
	
	void showLoadingDialog()
	{
		handler.post(new Runnable()
		{
			public void run()
			{
				synchronized(mLoadingDialogSyncObject)
				{
					if (mLoadingDialog==null)
					{
						mLoadingDialog = ProgressDialog.show(ZActivity.instance, "", ZActivity.instance.getResources().getString(R.string.loading), true);
					}
				}
				
			}
		});
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void hideLoadingDialog()
	{
		handler.post(new Runnable()
		{
			public void run()
			{
				synchronized(mLoadingDialogSyncObject)
				{
					if (mLoadingDialog!=null)
					{
						mLoadingDialog.dismiss();
						mLoadingDialog=null;
					}
				}
			}
		});
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	
	@Override
	public void onMaterialsLoadingStart(int total) {
		super.onMaterialsLoadingStart(total);
		BCKLog.i("Builder","Loading Textures...");
//		showLoadingDialog();
	}


	@Override
	public void onMaterialsLoading(int done, int total) {
		super.onMaterialsLoading(done, total);
		BCKLog.i("Builder","Loading Textures "+Integer.toString((done*100)/total)+"%");
	}
		@Override
	public void onMaterialsLoaded() 
	{
		super.onMaterialsLoaded();
		BCKLog.i("Builder","Textures loaded.");
//		hideLoadingDialog();
	}


	@Override
	public void onMetrixFPS(float fps) {
		super.onMetrixFPS(fps);
		if (ReleaseSettings.DEBUG_SHOW_FPS)
		{
			if (mDebugStringFPSInstance != null) mScene.remove(mDebugStringFPSInstance);
			mScene.add2D(mDebugStringFPSInstance = new ZInstance(mFont
						.createStringMesh(String.format("%.2f FPS",fps),
								AlignH.RIGHT, AlignV.BOTTOM)));
			mDebugStringFPSInstance.setTranslation(new ZVector(ZRenderer.alignScreenGrid(ZRenderer.getScreenRatioF()), ZRenderer.alignScreenGrid(-1), 0));

			/*
			if (mDebugStringInfosInstance != null) mScene.remove(mDebugStringInfosInstance);
			
			String status;
			if (UI.get().getClass() == UIeditor2D.class)
				status = String.format("%d(mob)+%d(fix) nodes - %d links - %d rails - %s",mLevel.getNbMobileNodes(),mLevel.getNbFixedNodes(),mLevel.getNbLinks(),mLevel.getNbRails(),((UIeditor2D)UI.get()).getTouchState().toString());
			else
				status = String.format("%d(mob)+%d(fix) nodes - %d links - %d rails - %s",mLevel.getNbMobileNodes(),mLevel.getNbFixedNodes(),mLevel.getNbLinks(),mLevel.getNbRails(),mLevel.getSimulationState().toString());
			
			mScene.add2D(mDebugStringInfosInstance = new ZInstance(mFont.createStringMesh(status,AlignH.LEFT, AlignV.BOTTOM)));
			mDebugStringInfosInstance.setTranslation(new ZVector(ZRenderer.alignScreenGrid(-ZRenderer.getScreenRatioF()), ZRenderer.alignScreenGrid(-1), 0));
			*/
		}
	}

	
	
	// touch interactions; coordinates in screen values (pixels)
	public void onTouchMoveStart(float xStart, float yStart) {if (mUI!=null) mUI.onTouchMoveStart(xStart, yStart);}
	public void onTouchMove(float xStart, float yStart, float x, float y) {if (mUI!=null) mUI.onTouchMove(xStart, yStart, x, y);}
	public void onTouchMoveEnd() {if (mUI!=null) mUI.onTouchMoveEnd();}
	public void onTouchPinchStart(float xStart, float yStart) {if (mUI!=null) mUI.onTouchPinchStart(xStart, yStart);}
	public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) {if (mUI!=null) mUI.onTouchPinch(xStart, yStart, x, y, factor);}
	public void onTouchPinchEnd() {if (mUI!=null) mUI.onTouchPinchEnd();}


	// DIALOGUES
	public static final int dlgAbout = 0;
	public static final int dlgShopBugReward = 1;
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id)
		{
		case dlgAbout: 
            return new AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_launcher)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_text)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	ZRenderer.postUpdateRunnable( new Runnable() { public void run() { ZActivity.instance.mScene.setMenu(new MenuMain(true,true)); } }); 
                	
                }
            })
	        .setNeutralButton(R.string.about_rate_us, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	
	            	Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zerracsoft.bck"));
	            	//marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	            	startActivity(marketIntent);
	            }
            })
            .setCancelable(false)
            .create();
            
		case dlgShopBugReward: 
            return new AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_launcher)
            .setTitle(R.string.shop_bug_reward_title)
            .setMessage(R.string.shop_bug_reward_text)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	//ZRenderer.postUpdateRunnable( new Runnable() { public void run() { ZActivity.instance.mScene.setMenu(new MenuMain(true,true)); } }); 
                	
                }
            })
            .setCancelable(false)
            .create();

			
		}
		
		// default
		return super.onCreateDialog(id);
	}

	public static int sndMenuSelect;
	public static int sndLoose;
	public static int sndLock;
	public static int sndHydraulics;
	public static int sndBoatBig;
	public static int sndBoatSmall;
	public static int sndUnderwater;
	public static int sndRewind;
	public static int sndDeath;
	public static int sndSplash[] = new int[4];
	public static int sndBreak[] = new int[7];
 	
	public void loadSounds()
	{
	//	if (!ReleaseSettings.SOUNDS) return;
	//	printfLog("MainScreen::loadSounds");
		// sound load
		sndMenuSelect = mSoundPool.load(this, R.raw.sound_menu_select, 1);
		sndLoose = mSoundPool.load(this, R.raw.sound_loose, 1);
		sndUnderwater = mSoundPool.load(this, R.raw.sound_underwater, 1);
		sndLock = mSoundPool.load(this, R.raw.sound_lock, 1);
		sndHydraulics = mSoundPool.load(this, R.raw.sound_hydraulics, 1);
		sndBoatBig = mSoundPool.load(this, R.raw.sound_boat_big, 1);
		sndBoatSmall = mSoundPool.load(this, R.raw.sound_boat_small, 1);
		sndRewind = mSoundPool.load(this, R.raw.sound_rewind, 1);
		sndSplash[0] = mSoundPool.load(this, R.raw.sound_splash01, 1);
		sndSplash[1] = mSoundPool.load(this, R.raw.sound_splash02, 1);
		sndSplash[2] = mSoundPool.load(this, R.raw.sound_splash03, 1);
		sndSplash[3] = mSoundPool.load(this, R.raw.sound_splash04, 1);
		sndDeath = mSoundPool.load(this, R.raw.sound_death, 1);
		sndBreak[0] = mSoundPool.load(this, R.raw.sound_break01, 1);
		sndBreak[1] = mSoundPool.load(this, R.raw.sound_break02, 1);
		sndBreak[2] = mSoundPool.load(this, R.raw.sound_break03, 1);
		sndBreak[3] = mSoundPool.load(this, R.raw.sound_break04, 1);
		sndBreak[4] = mSoundPool.load(this, R.raw.sound_break05, 1);
		sndBreak[5] = mSoundPool.load(this, R.raw.sound_break06, 1);
		sndBreak[6] = mSoundPool.load(this, R.raw.sound_break07, 1);
	}

	protected static MediaPlayer mMediaPlayerMenu;
	protected static MediaPlayer mMediaPlayerWin;
	protected static MediaPlayer mMediaPlayerLoose;
	public static void startMusicMenu()
	{
		if (mMediaPlayerMenu==null && ZActivity.instance.mSoundEnabled)
		{
			mMediaPlayerMenu = MediaPlayer.create(GameActivity.instance, R.raw.music_menu);
			if (mMediaPlayerMenu!=null)
			{
				mMediaPlayerMenu.start();
				mMediaPlayerMenu.setLooping(true);
			}
			setMenuMusicVolume(1);
		}		
		//if (!prefsSound)
		//	stopMusic();
	
	}
	public static void startMusicWin()
	{
		if (mMediaPlayerWin!=null)
		{
			mMediaPlayerWin.release();
			mMediaPlayerWin = null;
		}		
		if (mMediaPlayerWin==null && ZActivity.instance.mSoundEnabled)
		{
			mMediaPlayerWin = MediaPlayer.create(GameActivity.instance, R.raw.sound_win);
			if (mMediaPlayerWin!=null)
			{
				mMediaPlayerWin.start();
				mMediaPlayerWin.setLooping(false);
			}
		}		
		//if (!prefsSound)
		//	stopMusic();
	
	}
	public static void startMusicLoose()
	{
		if (mMediaPlayerLoose!=null)
		{
			mMediaPlayerLoose.release();
			mMediaPlayerLoose = null;
		}		
		if (mMediaPlayerLoose==null && ZActivity.instance.mSoundEnabled)
		{
			mMediaPlayerLoose = MediaPlayer.create(GameActivity.instance, R.raw.sound_loose);
			if (mMediaPlayerLoose!=null)
			{
				mMediaPlayerLoose.start();
				mMediaPlayerLoose.setLooping(false);
			}
		}		
		//if (!prefsSound)
		//	stopMusic();
	
	}
	public static void stopMusics()
	{
		if (mMediaPlayerMenu!=null)
		{
			mMediaPlayerMenu.release();
			mMediaPlayerMenu = null;
		}		
		if (mMediaPlayerWin!=null)
		{
			mMediaPlayerWin.release();
			mMediaPlayerWin = null;
		}		
		if (mMediaPlayerLoose!=null)
		{
			mMediaPlayerLoose.release();
			mMediaPlayerLoose = null;
		}		
	}

	public static void setMenuMusicVolume(float vol)
	{
		if (mMediaPlayerMenu!=null)
			mMediaPlayerMenu.setVolume(vol,vol);
	}

	
	// bruit du train: un stream un peu special...
	protected static MediaPlayer mMediaPlayerTrain;
	protected float mTrainVolume = 1;
	protected boolean mTrainOn = false;
	
	public static void setTrainSoundOn(boolean b)
	{
		((GameActivity)instance).mTrainOn = b;
	}
	
	protected void updateTrainSound(float dt)
	{
		if (!ZActivity.instance.mSoundEnabled) return;
		if (mTrainOn)
		{
			// mise en route ou fadein du son
			if (mMediaPlayerTrain==null)
			{
				// d�marrage de z�ro: volume � fond tout de suite
				mMediaPlayerTrain = MediaPlayer.create(GameActivity.instance, R.raw.sound_train);
				if (mMediaPlayerTrain!=null)
				{
					mMediaPlayerTrain.start();
					mMediaPlayerTrain.setLooping(false);
					mTrainVolume = 1;
				}
			}		
			else
			{
				// on update le volume
				mTrainVolume += dt;
				if (mTrainVolume>1) mTrainVolume=1;
				mMediaPlayerTrain.setVolume(mTrainVolume,mTrainVolume);
			}
				
		}
		else
		{
			// arr�t du son
			if (mMediaPlayerTrain!=null)
			{
				// on update le volume
				mTrainVolume -= dt;
				if (mTrainVolume>0) 
					mMediaPlayerTrain.setVolume(mTrainVolume,mTrainVolume);
				else
				{
					mMediaPlayerTrain.release();
					mMediaPlayerTrain = null;
				}
				
			}
		}
	}
		
}