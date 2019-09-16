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
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMaterial;
import com.zerracsoft.Lib3D.ZMenu;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.Lib3D.ZMenu.Item;
import com.zerracsoft.Lib3D.ZMenu.State;
import com.zerracsoft.bck.MenuText.TextItem;

public class MenuShopMain extends MenuText 
{

	//static final int idCancel = 3;

	static final int idItemNoAds = 10;
	static final int idItemFreebuildMode = 11;
	static final int idItemScreenshots = 12;
	static final int idItemShowConstraints = 13;
	static final int idItemOnboardCamera = 14;
	
	static ZVector titlePosition = new ZVector(0,0.8f,0);
	
	static ZColor color = new ZColor();
	
	public class ShopButton extends Item
	{
		public ShopManager.AddonType mAddon;
		private float mX;
		private float mY;

		protected ZInstance mFrameInstance;
		
		private static final float halfHeight = 0.18f;
		private static final float halfWidth = halfHeight*4.f;
		private static final float iconFrameHalfSize = 0.14f;
		private static final float iconHalfSize = 0.10f;
		
		
		public ShopButton(int id,ShopManager.AddonType addon,float x, float y)
		{
			mID = id;
			mAddon = addon;
			mX = x;
			mY = y;
			xMin = ZRenderer.alignScreenGrid(x-halfWidth);
			xMax = ZRenderer.alignScreenGrid(x+halfWidth);
			yMin = ZRenderer.alignScreenGrid(y-halfHeight);
			yMax = ZRenderer.alignScreenGrid(y+halfHeight);

		}
		
		public void destroy()
		{
			super.destroy();
			ZActivity.instance.mScene.remove(mFrameInstance);
		}
	
		public boolean checkInstance()
		{
			// deffered text instance creation, for deffered material loading...
			if (mFrameInstance!=null) 
				return true;
			// materials
			ZMaterial frameMat = ZActivity.instance.mScene.getMaterial("button");
			ZMaterial iconFrameMat = ShopManager.canPurchase(mAddon)?ZActivity.instance.mScene.getMaterial("level_choice_button"):ZActivity.instance.mScene.getMaterial("level_choice_button_locked");
			ZMaterial iconMat = ZActivity.instance.mScene.getMaterial(ShopManager.getMaterialName(mAddon));
			if (frameMat==null || iconFrameMat==null || iconMat==null)
				return false;
			if (GameActivity.mFont.getHeight()==0)
				return false;
			// frame mesh
			ZMesh mesh = new ZMesh();
			mesh.createRectangle(-halfWidth, -halfHeight, halfWidth, halfHeight, 0,1,1,0);
			mesh.setMaterial(frameMat);
			mFrameInstance = new ZInstance(mesh); 
			mFrameInstance.setTranslation(mX,mY,0); 
			color.set(1,1,1,0); 
			mFrameInstance.setColor(color); 
			ZActivity.instance.mScene.add2D(mFrameInstance);
			// icon frame mesh
			mesh = new ZMesh();
			mesh.createRectangle(-iconFrameHalfSize, -iconFrameHalfSize, iconFrameHalfSize, iconFrameHalfSize, 0,1,1,0);
			mesh.setMaterial(iconFrameMat);
			ZInstance inst = new ZInstance(mesh); 
			inst.setTranslation(-halfWidth+halfHeight*1.2f,0,1); 
			mFrameInstance.add(inst);
			// icon mesh
			mesh = new ZMesh();
			mesh.createRectangle(-iconHalfSize, -iconHalfSize, iconHalfSize, iconHalfSize, 0,1,1,0);
			mesh.setMaterial(iconMat);
			inst = new ZInstance(mesh); 
			inst.setTranslation(-halfWidth+halfHeight*1.2f,0,2); 
			mFrameInstance.add(inst);
			// text mesh
			mesh = GameActivity.mFont.createStringMesh(ShopManager.getTitle(mAddon)+" ("+Integer.toString(ShopManager.getPrice(mAddon))+")", ZFont.AlignH.LEFT, ZFont.AlignV.CENTER);
			if (mesh==null)
				return false;
			inst = new ZInstance(mesh);
			inst.setTranslation(-halfWidth+2.2f*halfHeight,0,1);
			mFrameInstance.add(inst);

			return true;
		}
		
		@Override
		public void updateEnter(int timeIncrement, float progress, int time) {
			// TODO Auto-generated method stub
			super.updateEnter(timeIncrement, progress, time);
			
			if (!checkInstance()) return;
			
			color.set(1,1,1,progress); 
			mFrameInstance.setColor(color); 
			
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time,
				boolean selected) {
			// TODO Auto-generated method stub
			super.updateExit(elapsedTime,progress, time, selected);

			if (!checkInstance()) return;
			
			float alpha = 1.f;
			if (selected)
			{
				alpha = (1.f-progress)*2.f; if (alpha>1) alpha = 1;
			}
			else
			{
				alpha = 1.f-progress;
			}

			color.set(1,1,1,1-progress); 
			mFrameInstance.setColor(color); 
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			// TODO Auto-generated method stub
			super.updateIdle(elapsedTime,time);

			if (!checkInstance()) return;

			color.set(1,1,1,1); 
			mFrameInstance.setColor(color); 
		}
		
	}
	
	public MenuShopMain(boolean anim,boolean animFrame)
	{
		super(anim,animFrame);
	}
	
	private int mShopManagerStateCounter; // sert � voir les changements d'�tat du shop...
	
	private float getItemX(int index)
	{
		if (index<3)
			return -ZRenderer.getScreenRatioF()*0.5f;
		return ZRenderer.getScreenRatioF()*0.5f;
	}
	private float getItemY(int index)
	{
		return 0.5f-(index%3)*0.4f;
	}
	
	public void create()
	{
		setTitle(R.string.menu_shop_title,titlePosition,ZFont.AlignH.CENTER);
		

		mShopManagerStateCounter = ShopManager.instance.mStateCounter;
		if (ShopManager.instance.isPurchasingAny())
		{
			// en attente....
		}
		else
		{
			int index = 0;	// placement des boutons sur la grille
			if (!ShopManager.instance.isPurchased(ShopManager.AddonType.FREEBUILD_MODE))		
				mItems.add(new ShopButton(idItemFreebuildMode,	ShopManager.AddonType.FREEBUILD_MODE,	getItemX(index),getItemY(index++)));
			if (!ShopManager.instance.isPurchased(ShopManager.AddonType.NO_ADS))		
				mItems.add(new ShopButton(idItemNoAds,	ShopManager.AddonType.NO_ADS,	getItemX(index),getItemY(index++)));
			if (!ShopManager.instance.isPurchased(ShopManager.AddonType.SCREENSHOT))		
				mItems.add(new ShopButton(idItemScreenshots,	ShopManager.AddonType.SCREENSHOT,	getItemX(index),getItemY(index++)));
			if (!ShopManager.instance.isPurchased(ShopManager.AddonType.SHOW_CONSTRAINTS))		
				mItems.add(new ShopButton(idItemShowConstraints,	ShopManager.AddonType.SHOW_CONSTRAINTS,	getItemX(index),getItemY(index++)));
			if (!ShopManager.instance.isPurchased(ShopManager.AddonType.ONBOARD_CAMERA))		
				mItems.add(new ShopButton(idItemOnboardCamera,	ShopManager.AddonType.ONBOARD_CAMERA,	getItemX(index),getItemY(index++)));
		}

		color.set(1,1,1,0);
		
	}
	
	@Override
	public void destroy() 
	{
		super.destroy();
	}

	@Override
	public void updateExit(int elapsedTime, float progress) {
		super.updateExit(elapsedTime, progress);
	}

	@Override
	public void updateIdle(int elapsedTime, int stateTime) {
		super.updateIdle(elapsedTime, stateTime);

		// redessiner le menu?
		if (mShopManagerStateCounter != ShopManager.instance.mStateCounter)
		{
			animate(true,false);
			exitToAnotherMenu(new MenuShopMain(true,false));
		}
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		super.updateEnter(elapsedTime, progress);
	}

	@Override
	public void onItemJustSelected(Item item) {
		// TODO Auto-generated method stub
		super.onItemJustSelected(item);
		
		if (item==null)
			animate(true,false);
		else
		{
			switch (item.mID)
			{
			default:
				animate(true,false);
				break;
			}
		}
	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			ZActivity.instance.mScene.setMenu(new MenuMain(true,false));
			return;
		}
		
		switch(item.mID)
		{

		case idItemNoAds:
		case idItemFreebuildMode:
		case idItemScreenshots:
		case idItemShowConstraints:
		case idItemOnboardCamera:
			animate (true, false);
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(((ShopButton)item).mAddon,
												new Runnable(){ public void run() {ZActivity.instance.mScene.setMenu(new MenuShopMain(true,false));} },
												null,
												true,false));
			break;

		}
		
		super.onItemSelected(item);
	}

	public void handleBackKey() { selectItem(0); }
	public boolean doesHandleBackKey() {return true;}	// returns true if handled

		
	
	
}
