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

import android.util.Log;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZFrustumCamera;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;

public class UImenus extends UI {
	
	@SuppressWarnings("unused")
	private UImenus() {}
	
	private boolean mGoToLevelChoiceMenu;
	public UImenus(boolean goToLevelChoiceMenu)
	{
		mGoToLevelChoiceMenu = goToLevelChoiceMenu;
	}

	@Override
	public void init() {	
		Level.get().resetSimulation();
		Level.get().initGraphics();
		ReleaseSettings.suspendRendering(false);
		// creation du menu principal
	//	if (ZActivity.instance.mScene.getMenu() == null)
		{
			if (mGoToLevelChoiceMenu)
				ZActivity.instance.mScene.setMenu(new MenuLevelChoice(((GameActivity)ZActivity.instance).mGameStatus.getLastPlayedWorld(),MenuLevelChoice.Direction.Down));
			else
				ZActivity.instance.mScene.setMenu(new MenuMain(true,true));		
			mGoToLevelChoiceMenu = false;
		}
		
		// music
		GameActivity.startMusicMenu();
	}

	ZVector camTarget = new ZVector();
	ZFrustumCamera mFrustumCamera = new ZFrustumCamera();
	protected ZVector mCamFront = new ZVector();
	protected ZVector mCamRight = new ZVector();
	protected ZVector mCamUp = new ZVector();
	protected ZVector mCamPos = new ZVector();
	int mCameraTimer = 0;
	
	@Override
	public void update(int elapsedTime) 
	{
		super.update(elapsedTime);

		// camera update
		// c'est une camera polaire tournat autour du pont, montant et descendant...
		mCameraTimer += elapsedTime;
		
		float angle = (float)Math.sin((float)(mCameraTimer % ReleaseSettings.MENU_BACKGROUND_ANIM_PERIOD)/(float)ReleaseSettings.MENU_BACKGROUND_ANIM_PERIOD * 6.2832f);
		float c = (float)Math.cos((double)angle);
		float s = (float)Math.sin((double)angle);
		float angle2 = (float)(mCameraTimer % (ReleaseSettings.MENU_BACKGROUND_ANIM_PERIOD*3))/(float)ReleaseSettings.MENU_BACKGROUND_ANIM_PERIOD/3.f * 6.2832f;
		float c2 = (float)Math.cos((double)angle2);
		
		camTarget.set(Level.get().getXCenter(),0,Level.get().getDefaultRailsHeight());
		float camDistance = Math.max(Level.get().getXSize()*0.75f,8+ReleaseSettings.floorExtrudeHalfWidth);
		
		camDistance = 100; // hardcode, bouhouhou

		mCamFront.set(-s,c,-0.5f*(1.f+c2)); mCamFront.normalize();
		mCamRight.cross(mCamFront,ZVector.constZ); mCamRight.normalize();
		mCamUp.cross(mCamRight,mCamFront);
		mCamPos.mulAddMul(camTarget,1,mCamFront,-camDistance);
		
		mFrustumCamera.setParameters(1, 5, 1000, mCamPos, mCamFront, mCamRight, mCamUp);
		ZRenderer.setCamera(mFrustumCamera);  
		
		

	}

	@Override
	public void onTouchMoveStart(float xStart, float yStart) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchMove(float xStart, float yStart, float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchMoveEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchPinchStart(float xStart, float yStart) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTouchPinchEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public ViewMode getViewMode() {return ViewMode.MODE3D; }

	@Override
	public boolean isEditMode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimulationOn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		ReleaseSettings.suspendRendering(true);
		Level.get().resetGraphics();
		// music
		GameActivity.stopMusics();
	}
	
	@Override
	public void updateBudget() {}
	@Override
	public void shakeCamera() {}


}
