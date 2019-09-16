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

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMaterial;
import com.zerracsoft.Lib3D.ZMenu;
import com.zerracsoft.Lib3D.ZMenu.Item;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
 
public abstract class MenuButtons extends ZMenu {

@Override
	public void onItemJustSelected(Item item) {
		// TODO Auto-generated method stub
		super.onItemJustSelected(item);
		
		// sound ! 
		GameActivity.instance.playSnd(GameActivity.sndMenuSelect,1);
	}
	@Override
	public boolean doesHandleBackKey() {
		// TODO Auto-generated method stub
		return false;
	}

	//	protected static final float buttonsSize = 0.4f;	// 5 buttons/screen height
	private static float mButtonsSize = -1;
	private static float mButtonsLines = 0;
	private static float mButtonsColumns = 0;
	
	protected enum Direction {up,down,left,right,none};
	
	private static void computeButtonsSize()
	{
		if (mButtonsLines==0)
		{
			// compute buttons size ONCE
			float displayWidth = (float)ZActivity.instance.mDisplayMetrics.widthPixels / (float)ZActivity.instance.mDisplayMetrics.densityDpi;
			if (displayWidth>=6)
				mButtonsLines = 8;	// xlarge screen (10in), 8 buttons height
			else if (displayWidth>=4)
				mButtonsLines = 6;	// large screen (7in), 6 buttons height
			else
				mButtonsLines = 5;	// normal/small, 5 buttons height
			mButtonsColumns = ZRenderer.getScreenRatioF() * mButtonsLines;
			mButtonsSize = 2.f/(float)mButtonsLines;
		}	
	}
	protected static float getButtonsSize()
	{
		computeButtonsSize();
		return mButtonsSize;
	}
	protected static float getButtonsLines()
	{
		computeButtonsSize();
		return mButtonsLines;
	}
	protected static float getButtonsColumns()
	{
		computeButtonsSize();
		return mButtonsColumns;
	}
	
	protected boolean mAnimate = false; // animation d'entree et de sortie
	
	void animate(boolean anim)
	{
		mAnimate = anim;
		if (anim)
		{
			mEnterTime = ReleaseSettings.MENU_BUTTON_ENTER_TIME;
			mExitTime = ReleaseSettings.MENU_BUTTON_EXIT_TIME;
		}
		else
			mEnterTime = mExitTime = 0;
			
	}
	
	public MenuButtons()
	{
		animate(false);
	}
	
	

	class IconItem extends Item
	{

		protected ZInstance mMainInstance;
		protected float mScaleX;
		protected float mScaleY;
		
		protected ZInstance mButtonInstance;	// 96x96
		
		public Direction mDirection;
			
		@Override
		public void destroy() 
		{
			super.destroy();
			ZActivity.instance.mScene.remove(mMainInstance); 
		}
		
		ZMaterial mMaterialButton; 
		ZMaterial mMaterialIcon; 
		ZMaterial mMaterialSelected = null;
		
		float u[] = new float[4];
		float v[] = new float[4];
	
		static final float btnU = 96.f/512.f;
		static final float btnV = 96.f/256.f;
		static final float iconU = 64.f/512.f;
		static final float iconV = 64.f/256.f;
		private void initButtonInstance(boolean selected)
		{
			mMaterialButton = ZActivity.instance.mScene.getMaterial("buttons");
			ZMesh mesh = new ZMesh();
			mScaleX = (xMax-xMin)/2;
			mScaleY = (yMax-yMin)/2;
			float x = (xMax+xMin)/2;
			float y = (yMax+yMin)/2;
			if (!selected)
				mesh.createRectangle(-1,-1,1,1,btnU,btnV,btnU*2.f,0);
			else
				mesh.createRectangle(-1,-1,1,1,0,btnV,btnU,0);
			mesh.setMaterial(mMaterialButton);
			mButtonInstance = new ZInstance(mesh);
			mButtonInstance.setTranslation(0,0,0);
			mMainInstance = new ZInstance(mButtonInstance); 
			mMainInstance.setTranslation(x,y,0);
			mMainInstance.setScale(mScaleX,mScaleY,1);
			ZActivity.instance.mScene.add2D(mMainInstance);
		}

		private ZInstance initToolInstance(int toolX, int toolY)
		{
			// UVs
			u[0] = u[2] = (float)toolX*iconU;
			u[1] = u[3] = (float)(toolX+1)*iconU;
			v[0] = v[1] = (float)(toolY+1)*iconV;
			v[2] = v[3] = (float)toolY*iconV;

			// icon mesh			
			ZMesh mesh = new ZMesh();
			mesh.createRectangle(-0.66f,-0.66f,0.66f,0.66f,u[0],v[0],u[1],v[1],u[2],v[2],u[3],v[3]);
			mesh.setMaterial(mMaterialIcon);
			return new ZInstance(mesh);
		}
		
		void initTool(int id, boolean selected, int toolX, int toolY, Direction direction)
		{
			mID = id; 
			mDirection = direction;

			initButtonInstance(selected);
			mMaterialIcon = ZActivity.instance.mScene.getMaterial("buttons");
						
			ZInstance iconInstance = initToolInstance(toolX,toolY);
			iconInstance.setTranslation(0,0,0.25f);
			mButtonInstance.add(iconInstance);
			
			// prevent phantom frame
			if (mAnimate) updateEnter(0,0,0); 
			
		}	

		@Override
		public void updateEnter(int elapsedTime, float progress, int time) {
			// TODO Auto-generated method stub
			super.updateEnter(elapsedTime, progress, time);
			if (mAnimate)
			{
				switch (mDirection)
				{
				case up:
					mButtonInstance.setTranslation(0,2.f*(1.f-progress),0);
					break;
				case down:
					mButtonInstance.setTranslation(0,-2.f*(1.f-progress),0);
					break;
				case left:
					mButtonInstance.setTranslation(-2.f*(1.f-progress),0,0);
					break;
				case right:
					mButtonInstance.setTranslation(2.f*(1.f-progress),0,0);
					break;
				}
			}
			
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			// TODO Auto-generated method stub
			super.updateIdle(elapsedTime,time);
			mButtonInstance.setTranslation(0,0,0);
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time, boolean selected) {
			// TODO Auto-generated method stub
			super.updateExit(elapsedTime, progress, time, selected);
			if (mAnimate)
			{
				switch (mDirection)
				{
				case up:
					mButtonInstance.setTranslation(0,2.f*(progress),0);
					break;
				case down:
					mButtonInstance.setTranslation(0,-2.f*(progress),0);
					break;
				case left:
					mButtonInstance.setTranslation(-2.f*(progress),0,0);
					break;
				case right:
					mButtonInstance.setTranslation(2.f*(progress),0,0);
					break;
				}
			}
		}
		
	}
	
}
