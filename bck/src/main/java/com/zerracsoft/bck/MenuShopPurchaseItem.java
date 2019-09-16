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
import com.zerracsoft.bck.MenuText.TextItem;

public class MenuShopPurchaseItem extends MenuText 
{
	Runnable mExitSuccess;
	Runnable mExitFail;
	ShopManager.AddonType mAddon;
	public MenuShopPurchaseItem(ShopManager.AddonType addon, Runnable exitSuccess, Runnable exitFail, boolean anim,boolean animFrame)
	{
		super (anim,animFrame);
		mExitSuccess = exitSuccess;
		mExitFail = (exitFail!=null)?exitFail:mExitSuccess;
		mAddon = addon;
	}
	
	static final int idOk = 1;
	static final int idCancel = 2;
//	static final int idGetCredits = 3;
	
	static ZVector titlePosition = new ZVector(0,ZRenderer.alignScreenGrid(0.7f),1);
	static ZVector iconPosition = new ZVector(0,0.4f,1);
	static ZVector descPosition = new ZVector(0,ZRenderer.alignScreenGrid(0.1f),1);
	static ZVector pricePosition = new ZVector(0,ZRenderer.alignScreenGrid(0.0f),1);
	static final float iconFrameHalfSize = 0.2f;
	static final float iconHalfSize = 0.15f;
	
	ZInstance mInstanceText;
	ZInstance mInstanceIcon;
//	ZInstance mInstanceCredits;
	ZInstance mInstancePrice;
	static ZColor color = new ZColor();
	
	
	public void create()
	{

		setTitle(ShopManager.getTitle(mAddon),titlePosition,ZFont.AlignH.CENTER);
				
		mItems.add(new TextItem(idCancel,R.string.menu_purchase_item_cancel,-ZRenderer.getScreenRatioF()*0.5f,-0.3f));
		if (ShopManager.canPurchase(mAddon))
			mItems.add(new TextItem(idOk,R.string.menu_purchase_item_ok,ZRenderer.getScreenRatioF()*0.5f,-0.3f));
//		else
//			mItems.add(new TextItem(idGetCredits,R.string.main_menu_earn_credits,ZRenderer.getScreenRatioF()*0.5f,-0.3f));

		// description
		String s = ShopManager.getDesc(mAddon);
		mInstanceText = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
		mInstanceText.setTranslation(descPosition);
		color.set(1,1,1,0);
		mInstanceText.setColor(color);
		ZActivity.instance.mScene.add2D(mInstanceText);
		
		// price
		s = ShopManager.canPurchase(mAddon)?ZActivity.instance.getResources().getString(R.string.menu_purchase_item_price):ZActivity.instance.getResources().getString(R.string.menu_purchase_item_price_unsufficient);
		mInstancePrice = new ZInstance(GameActivity.mFont.createStringMesh(String.format(s,ShopManager.getPrice(mAddon)), ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
		mInstancePrice.setTranslation(pricePosition);
		mInstancePrice.setColor(color);
		ZActivity.instance.mScene.add2D(mInstancePrice);

		
		// Icone
		ZMaterial iconFrameMat = ShopManager.canPurchase(mAddon)?ZActivity.instance.mScene.getMaterial("level_choice_button"):ZActivity.instance.mScene.getMaterial("level_choice_button_locked");
		ZMaterial iconMat = ZActivity.instance.mScene.getMaterial(ShopManager.getMaterialName(mAddon));
		// icon frame mesh
		ZMesh mesh = new ZMesh();
		mesh.createRectangle(-iconFrameHalfSize, -iconFrameHalfSize, iconFrameHalfSize, iconFrameHalfSize, 0,1,1,0);
		mesh.setMaterial(iconFrameMat);
		mInstanceIcon = new ZInstance(mesh); 
		mInstanceIcon.setTranslation(iconPosition); 
		ZActivity.instance.mScene.add2D(mInstanceIcon);
		// icon mesh
		mesh = new ZMesh();
		mesh.createRectangle(-iconHalfSize, -iconHalfSize, iconHalfSize, iconHalfSize, 0,1,1,0);
		mesh.setMaterial(iconMat);
		ZInstance inst = new ZInstance(mesh); 
		inst.setTranslation(0,0,2); 
		mInstanceIcon.add(inst);

		
	}
	
	@Override
	public void destroy() 
	{
		ZActivity.instance.mScene.remove(mInstanceText);
		ZActivity.instance.mScene.remove(mInstanceIcon);
		ZActivity.instance.mScene.remove(mInstancePrice);
//		ZActivity.instance.mScene.remove(mInstanceCredits);
		super.destroy();
	}

	@Override
	public void updateExit(int elapsedTime, float progress) {
		super.updateExit(elapsedTime, progress);
		color.set(1,1,1,1-progress);
		mInstanceText.setColor(color);
		mInstancePrice.setColor(color);
//		mInstanceCredits.setColor(color);
		mInstanceIcon.setColor(color);
	}

	@Override
	public void updateIdle(int elapsedTime, int stateTime) {
		super.updateIdle(elapsedTime, stateTime);
		color.set(1,1,1,1);
		mInstanceText.setColor(color);
		mInstancePrice.setColor(color);
//		mInstanceCredits.setColor(color);
		mInstanceIcon.setColor(color);
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		super.updateEnter(elapsedTime, progress);
		color.set(1,1,1,progress);
		mInstanceText.setColor(color);
		mInstancePrice.setColor(color);
//		mInstanceCredits.setColor(color);
		mInstanceIcon.setColor(color);
	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			mExitFail.run();
			return;
		}
		
		switch(item.mID)
		{
		case idOk:
			ShopManager.instance.purchase(mAddon,mExitSuccess,mExitFail);
			//mExitAction.run();
			break;
		case idCancel:
			mExitFail.run();
			break;
		}
		
		super.onItemSelected(item);
	}


	
	@Override
	public void handleBackKey() { selectItem(idCancel); }
	public boolean doesHandleBackKey() {return true;}	// returns true if handled

	
}
