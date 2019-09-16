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
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMenu;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;


public class MenuLevelChoice extends ZMenu 
{
	public static final int idLeft = 1;
	public static final int idRight = 2;
	public static final int idLevel1 = 3;
	public static final int idLevel2 = 4;
	public static final int idLevel3 = 5;
	public static final int idLevel4 = 6;
	public static final int idLevel5 = 7;
//	public static final int idNextWorld = 8;

	
	public abstract class MenuLevelChoiceItem extends Item
	{
		ZInstance mBtnInstance;
		float mX = 0;
		float mY = 0;
		public MenuLevelChoiceItem(int id,float x, float y, float halfWidth, float halfHeight, String materialName)
		{
			mID = id;
			mX = x;
			mY = y;
			xMin = x-halfWidth;
			xMax = x+halfWidth;
			yMin = y-halfHeight;
			yMax = y+halfHeight;
			
			ZMesh m =new ZMesh();
			m.createRectangle(-halfWidth, -halfHeight, halfWidth, halfHeight, 0,1,1,0);
			m.setMaterial(materialName);
			mBtnInstance = new ZInstance(m);
			ZActivity.instance.mScene.add2D(mBtnInstance);
			mBtnInstance.setTranslation(0, 2, 0); 	// hors-ecran
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			mBtnInstance.setTranslation(mX,mY,1);
			super.updateIdle(elapsedTime, time);
		}

		@Override
		public void updateEnter(int elapsedTime, float progress, int time) 
		{
			float translation = getEnterTranslation(progress);

			switch (mDirection)
			{
			case Left:
				mBtnInstance.setTranslation(mX+translation*(1+ZRenderer.getScreenRatioF()),mY,1);
				break;
			case Right:
				mBtnInstance.setTranslation(mX-translation*(1+ZRenderer.getScreenRatioF()),mY,1);
				break;
			case Up:
				mBtnInstance.setTranslation(mX,mY-translation*2.f,1);
				break;
			case Down:
				mBtnInstance.setTranslation(mX,mY+translation*2.f,1);
				break;
			}
			super.updateEnter(elapsedTime, progress, time);
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time, boolean selected) {
			float translation = getExitTranslation(progress);
			switch (mDirection)
			{
			case Left:
				mBtnInstance.setTranslation(mX-translation*(1+ZRenderer.getScreenRatioF()),mY,1);
				break;
			case Right:
				mBtnInstance.setTranslation(mX+translation*(1+ZRenderer.getScreenRatioF()),mY,1);
				break;
			case Up:
				mBtnInstance.setTranslation(mX,mY+translation*2.f,1);
				break;
			case Down:
				mBtnInstance.setTranslation(mX,mY-translation*2.f,1);
				break;
			}
			super.updateExit(elapsedTime, progress, time, selected);
		}

		@Override
		public void destroy() {
			ZActivity.instance.mScene.remove(mBtnInstance);
			super.destroy();
		}
		
		public abstract void activate();
		
	}
	
	public class MenuLevelChoiceItemWorld extends MenuLevelChoiceItem
	{
		private StatusWorld mWorld;
		public MenuLevelChoiceItemWorld(StatusWorld world,int id,float x, float y, float halfWidth, float halfHeight, String materialName)
		{
			super(id,x,y,halfWidth,halfHeight,materialName);
			mWorld = world;
		}
		
		@Override
		public void activate() { ZActivity.instance.mScene.setMenu(new MenuLevelChoice(mWorld,mDirection)); }
		
	}
	
	public class MenuLevelChoiceItemLevel extends MenuLevelChoiceItem
	{
		ZInstance mInstanceLevelStateIcon;
		private StatusLevel mLevel;
		public MenuLevelChoiceItemLevel(StatusLevel level,int id,float x, float y, float halfWidth, float halfHeight)
		{
			super(id,x,y,halfWidth,halfHeight,(level.mState==StatusLevel.State.LOCKED?"level_choice_button_locked":"level_choice_button"));
			mLevel = level;
			ZMesh m;
			switch(mLevel.mState)
			{
			case LOCKED:
				m=new ZMesh(); 
				m.createRectangle(-halfWidth*0.5f,-halfWidth*0.5f,halfWidth*0.5f,halfWidth*0.5f,0,1,1,0);
				m.setMaterial("locked");
				mInstanceLevelStateIcon = new ZInstance(m);
				mInstanceLevelStateIcon.setTranslation(0,0,1);			
				mBtnInstance.add(mInstanceLevelStateIcon);
				mEnabled = false;
				break;
			case DONE:
				m=new ZMesh(); 
				m.createRectangle(-halfWidth*0.5f,-halfWidth*0.5f,halfWidth*0.5f,halfWidth*0.5f,0,1,1,0);
				m.setMaterial("checked");
				mInstanceLevelStateIcon = new ZInstance(m);
				mInstanceLevelStateIcon.setTranslation(0,0,1);			
				mBtnInstance.add(mInstanceLevelStateIcon);
				break;
			}
		}
		
		@Override
		public void activate() 
		{
			mWorld.setLastPlayedWorld();
			mLevel.markAsLastPLayed();
			((GameActivity)ZActivity.instance).mLevel.init(mLevel);
			((GameActivity)ZActivity.instance).mLevel.loadGame();
			((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
		}
		
	}
	
	public class MenuLevelChoiceItemFreebuildLocked extends MenuLevelChoiceItem
	{
		ZInstance mInstanceLevelStateIcon;
		private StatusLevel mLevel;
		public MenuLevelChoiceItemFreebuildLocked(StatusLevel level,int id,float x, float y, float halfWidth, float halfHeight)
		{
			super(id,x,y,halfWidth,halfHeight,(ShopManager.canPurchase(ShopManager.AddonType.SKIP_WORLD)?"level_choice_button":"level_choice_button_locked"));
			mLevel = level;
			//mEnabled=enabled;
			ZMesh m=new ZMesh(); 
			m.createRectangle(-halfWidth*0.5f,-halfWidth*0.5f,halfWidth*0.5f,halfWidth*0.5f,0,1,1,0);
			m.setMaterial("locked");
			mInstanceLevelStateIcon = new ZInstance(m);
			mInstanceLevelStateIcon.setTranslation(0,0,1);			
			mBtnInstance.add(mInstanceLevelStateIcon);
			mEnabled = true;
		}
		
		@Override
		public void activate() 
		{
			//ZActivity.instance.mScene.setMenu(new MenuSkipWorld(mWorld)); // VERY temp
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.FREEBUILD_MODE,
					new Runnable()
					{ 
						public void run() 
						{	
							mWorld.setLastPlayedWorld();
							mLevel.markAsLastPLayed();
							((GameActivity)ZActivity.instance).mLevel.init(mLevel);
							((GameActivity)ZActivity.instance).mLevel.loadGame();
							((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
						} 
					},
					new Runnable()
					{ 
						public void run() 
						{	
							ZActivity.instance.mScene.setMenu(new MenuLevelChoice(mLevel.mWorld,MenuLevelChoice.Direction.Down));
						} 
					},
					true,true));

		}
		
	}
		
	public class MenuLevelChoiceItemNextWorld extends MenuLevelChoiceItem
	{
		ZInstance mInstanceNextWorldIcon;
		private StatusWorld mWorld;
		public MenuLevelChoiceItemNextWorld(StatusWorld world,int id,float x, float y, float halfWidth, float halfHeight)
		{
			super(id,x,y,halfWidth,halfHeight,(ShopManager.canPurchase(ShopManager.AddonType.SKIP_WORLD)?"level_choice_button":"level_choice_button_locked"));
			mWorld = world;
			//mEnabled=enabled;
			ZMesh m=new ZMesh(); 
			m.createRectangle(-halfWidth*0.5f,-halfWidth*0.5f,halfWidth*0.5f,halfWidth*0.5f,0,1,1,0);
			m.setMaterial(ShopManager.getMaterialName(ShopManager.AddonType.SKIP_WORLD));
			mInstanceNextWorldIcon = new ZInstance(m);
			mInstanceNextWorldIcon.setTranslation(0,0,1);			
			mBtnInstance.add(mInstanceNextWorldIcon);
		}
		
		@Override
		public void activate() 
		{
			//ZActivity.instance.mScene.setMenu(new MenuSkipWorld(mWorld)); // VERY temp
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.SKIP_WORLD,
					new Runnable()
					{ 
						public void run() 
						{	
							mWorld.getNext().mLevels[0].mState = StatusLevel.State.NONE;
							mWorld.getNext().updateState();
							mWorld.mSkipped=true;
							((GameActivity)GameActivity.instance).mGameStatus.nbSkippedWorlds++;
							((GameActivity)ZActivity.instance).mGameStatus.save();	// evite les gros TRICHEURS!!! (et les problemes)
							ZActivity.instance.mScene.setMenu(new MenuLevelChoice(mWorld.getNext(),MenuLevelChoice.Direction.Left));
						} 
					},
					new Runnable()
					{ 
						public void run() 
						{	
							ZActivity.instance.mScene.setMenu(new MenuLevelChoice(mWorld,MenuLevelChoice.Direction.Right));
						} 
					},
					true,true));

		}
		
	}
	

	
	ZInstance mInstanceFrame;
//	ZInstance mLeftArrowInstance;
//	ZInstance mRightArrowInstance;
	ZInstance mInstanceWorldName;
	ZInstance mInstanceWorldStateIcon;
	ZInstance mInstanceBudgetTitle;
	ZInstance mInstanceBudgetBars;
	ZInstance mInstanceBudgetCables;
	ZInstance mInstanceBudgetHydraulics;
	
	private StatusWorld mWorld;
	
	public enum Direction
	{
		Left,
		Right,
		Up,
		Down
	}
	private Direction mDirection = Direction.Left;
	
	@SuppressWarnings("unused")
	private MenuLevelChoice() {}
	
	public MenuLevelChoice(StatusWorld world, Direction direction)
	{
		mWorld = world;
		mDirection = direction;
		
		ZMesh m;
		m=new ZMesh(); 
		m.createRectangle(-1,-1,1,1,0,1,1,0);
		m.setMaterial("level_choice_frame");
		mInstanceFrame = new ZInstance(m);
		mInstanceFrame.setTranslation(1+ZRenderer.getScreenRatioF(),0,0);
		ZActivity.instance.mScene.add2D(mInstanceFrame);
		
		mInstanceWorldName = new ZInstance(GameActivity.mFontBig.createStringMesh(mWorld.mName, ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
		mInstanceWorldName.setTranslation(0,ZRenderer.alignScreenGrid(0.716f),1);
		mInstanceFrame.add(mInstanceWorldName);
		
		String s = ZActivity.instance.getResources().getString(R.string.budget);
		mInstanceBudgetTitle = new ZInstance(GameActivity.mFontBig.createStringMesh(s, ZFont.AlignH.LEFT, ZFont.AlignV.CENTER));
		mInstanceBudgetTitle.setTranslation(-ZRenderer.alignScreenGrid(0.8f),ZRenderer.alignScreenGrid(0.47f),1);
		mInstanceFrame.add(mInstanceBudgetTitle);
		
		if (world.mInfiniteBudget)
		{
			s = ZActivity.instance.getResources().getString(R.string.budget_infinite);
			mInstanceBudgetCables = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.LEFT, ZFont.AlignV.CENTER));
			mInstanceBudgetCables.setTranslation(-ZRenderer.alignScreenGrid(0.8f),ZRenderer.alignScreenGrid(0.25f),1);
			mInstanceFrame.add(mInstanceBudgetCables);
		}
		else
		{
			if(world.mBudgetBar!=0)
			{
				s = ZActivity.instance.getResources().getString(R.string.budget_bar) + " " + Integer.toString(world.mBudgetBar-world.mSpentBar) + " / "+Integer.toString(world.mBudgetBar);
				mInstanceBudgetBars = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.LEFT, ZFont.AlignV.CENTER));
				mInstanceBudgetBars.setTranslation(-ZRenderer.alignScreenGrid(0.8f),ZRenderer.alignScreenGrid(0.35f),1);
				mInstanceFrame.add(mInstanceBudgetBars);
			}
	
			if(world.mBudgetCable!=0)
			{
				s = ZActivity.instance.getResources().getString(R.string.budget_cable) + " " + Integer.toString(world.mBudgetCable-world.mSpentCable) + " / "+Integer.toString(world.mBudgetCable);
				mInstanceBudgetCables = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.LEFT, ZFont.AlignV.CENTER));
				mInstanceBudgetCables.setTranslation(-ZRenderer.alignScreenGrid(0.8f),ZRenderer.alignScreenGrid(0.25f),1);
				mInstanceFrame.add(mInstanceBudgetCables);
			}
			
			if(world.mBudgetJack!=0)
			{
				s = ZActivity.instance.getResources().getString(R.string.budget_hydraulic) + " " + Integer.toString(world.mBudgetJack-world.mSpentJack) + " / "+Integer.toString(world.mBudgetJack);
				mInstanceBudgetHydraulics = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.LEFT, ZFont.AlignV.CENTER));
				mInstanceBudgetHydraulics.setTranslation(-ZRenderer.alignScreenGrid(0.8f),ZRenderer.alignScreenGrid(0.15f),1);
				mInstanceFrame.add(mInstanceBudgetHydraulics);
			}
		}
		
		switch(mWorld.mState)
		{
		case LOCKED:
			m=new ZMesh(); 
			m.createRectangle(-0.25f,-0.25f,0.25f,0.25f,0,1,1,0);
			m.setMaterial("locked");
			mInstanceWorldStateIcon = new ZInstance(m);
			mInstanceWorldStateIcon.setTranslation(0.5f,0.318f,1);			
			mInstanceFrame.add(mInstanceWorldStateIcon);
			break;
		case DONE:
			m=new ZMesh(); 
			m.createRectangle(-0.25f,-0.25f,0.25f,0.25f,0,1,1,0);
			m.setMaterial("checked");
			mInstanceWorldStateIcon = new ZInstance(m);
			mInstanceWorldStateIcon.setTranslation(0.5f,0.318f,1);			
			mInstanceFrame.add(mInstanceWorldStateIcon);
			break;
		}
		
		mEnterTime = 400;
		mExitTime = 300;
		
		StatusWorld w = mWorld.getPrevious();
		if (w!=null)
			mItems.add(new MenuLevelChoiceItemWorld(w,idLeft,-1.25f,0,0.25f,0.25f,"level_choice_left"));
		w = mWorld.getNext();
		if (w!=null)
			mItems.add(new MenuLevelChoiceItemWorld(w,idRight,1.25f,0,0.25f,0.25f,"level_choice_right"));
		
		if (mWorld.mFreebuild && !ShopManager.instance.isPurchased(ShopManager.AddonType.FREEBUILD_MODE))
		{
			mItems.add(new MenuLevelChoiceItemFreebuildLocked(mWorld.mLevels[0],idLevel1,-0.675f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemFreebuildLocked(mWorld.mLevels[1],idLevel2,-0.225f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemFreebuildLocked(mWorld.mLevels[2],idLevel3,0.225f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemFreebuildLocked(mWorld.mLevels[3],idLevel4,-0.45f,-0.625f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemFreebuildLocked(mWorld.mLevels[4],idLevel5,0,-0.625f,0.175f,0.175f)); 			
		}
		else
		{
			mItems.add(new MenuLevelChoiceItemLevel(mWorld.mLevels[0],idLevel1,-0.675f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemLevel(mWorld.mLevels[1],idLevel2,-0.225f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemLevel(mWorld.mLevels[2],idLevel3,0.225f,-0.175f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemLevel(mWorld.mLevels[3],idLevel4,-0.45f,-0.625f,0.175f,0.175f));
			mItems.add(new MenuLevelChoiceItemLevel(mWorld.mLevels[4],idLevel5,0,-0.625f,0.175f,0.175f)); 
		}
		// on montre le bouton "skip" (meme grise) seulement s'il est possible de skipper
		boolean skipPossible = (mWorld.getNext()!=null) 
								//&& !mWorld.mSkipped
								&& (mWorld.mState!=StatusWorld.State.LOCKED)
								&& (mWorld.getNext().mState==StatusWorld.State.LOCKED);
		// on le degrise seulement si on a assez de credits
		//boolean skipEnabled = skipPossible && (BCKTapjoyVirtualGoodsManager.instance.getSkipWorldAvailable()>((GameActivity)GameActivity.instance).mGameStatus.nbSkippedWorlds);
		//if (skipPossible) mItems.add(new MenuLevelChoiceItemNextWorld(mWorld,idNextWorld,0.675f,-0.625f,0.175f,0.175f));
	}

	@Override
	public void destroy() 
	{
		ZActivity.instance.mScene.remove(mInstanceFrame);
		super.destroy();
	}
	
	public static float getEnterTranslation(float progress)
	{
		//return 1.f-(float)Math.sin(progress*0.5*Math.PI);
		
		// parabole pour l'acceleration, mais avec un depassement ;-)
		float x0 = 0.75f;	// sommet de la parabole
		float scaleY = 1.f/(1.f-(1-1/x0)*(1-1/x0));
		float offsetY = 1-scaleY;
		float x = progress/x0-1.f;
		
		return offsetY+scaleY*(x*x);
		/*
		float coef = 0.5f*0.5f;
		float x = progress*1.5f - 1 ;
		return 1.f-coef*(float)Math.sin(progress*0.75*Math.PI);
		*/
	}
	public static float getExitTranslation(float progress)
	{
		return progress*progress;
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) 
	{
		float translation = getEnterTranslation(progress);
		
		switch (mDirection)
		{
		case Left:
			mInstanceFrame.setTranslation(translation*(1+ZRenderer.getScreenRatioF()),0,0);
			break;
		case Right:
			mInstanceFrame.setTranslation(-translation*(1+ZRenderer.getScreenRatioF()),0,0);
			break;
		case Up:
			mInstanceFrame.setTranslation(0,-translation*2.f,0);
			break;
		case Down:
			mInstanceFrame.setTranslation(0,translation*2.f,0);
			break;
		}
		super.updateEnter(elapsedTime, progress);
	}

	@Override
	public void updateIdle(int elapsedTime, int stateTime)
	{
		mInstanceFrame.setTranslation(0,0,0);
		super.updateIdle(elapsedTime, stateTime);
	}

	@Override
	public void updateExit(int elapsedTime, float progress) {
		float translation = getExitTranslation(progress);
		switch (mDirection)
		{
		case Left:
			mInstanceFrame.setTranslation(-translation*(1+ZRenderer.getScreenRatioF()),0,0);
			break;
		case Right:
			mInstanceFrame.setTranslation(translation*(1+ZRenderer.getScreenRatioF()),0,0);
			break;
		case Up:
			mInstanceFrame.setTranslation(0,translation*2.f,0);
			break;
		case Down:
			mInstanceFrame.setTranslation(0,-translation*2.f,0);
			break;
		}
		super.updateExit(elapsedTime, progress);
		
		// fondu musique
		if (this.mSelectedItem != null
			&& mSelectedItem.getClass()==MenuLevelChoiceItemLevel.class)
			GameActivity.setMenuMusicVolume(1-progress);
	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			ZActivity.instance.mScene.setMenu(new MenuMain(true,true));
			return;
		}
		
		((MenuLevelChoiceItem)item).activate();
		
		super.onItemSelected(item);
	}

	@Override 
	public void onItemJustSelected(Item item) 
	{
		if (item!=null)
			switch(item.mID)
			{
			case idLeft: mDirection = Direction.Right; break;
			case idRight: mDirection = Direction.Left; break;
		//	case idNextWorld: mDirection = Direction.Left; break;
			case idLevel1: mDirection = Direction.Up;  break;
			case idLevel2: mDirection = Direction.Up;  break;
			case idLevel3: mDirection = Direction.Up;  break;
			case idLevel4: mDirection = Direction.Up;  break;
			case idLevel5: mDirection = Direction.Up;  break;
			}

		super.onItemSelected(item);
		
		GameActivity.instance.playSnd(GameActivity.sndMenuSelect,1);
	}

	@Override
	public void handleBackKey() 
	{
		mDirection = Direction.Down;
		selectItem(-1);
	}
	public boolean doesHandleBackKey() {return true;}	
	

	@Override
	public void onTouchSwipe(boolean itemTouched,float dx, float dy) {
		if (Math.abs(dx)>1 && Math.abs(dy/dx)<0.5f)
		{
			if (dx>0)
			{
				if (mWorld.getPrevious()!=null) selectItem(idLeft);
			}
			else
			{
				if (mWorld.getNext()!=null) selectItem(idRight);
			}
		}
	}
	
}
