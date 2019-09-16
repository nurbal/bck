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
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.bck.MenuShopMain.ShopButton;

public class MenuButtonsPreview extends MenuButtons {
	
	public static ZColor colorDimmed = new ZColor(1,1,1,0.5f);
	
	class IconItemMain extends IconItem
	{
		public IconItemMain(int id, float posX, float posY, boolean selected, int toolX, int toolY,Direction dir,boolean dimmed)
		{
			xMin = -1*ZRenderer.getScreenRatioF()+(float)posX*getButtonsSize();
			xMax = xMin+getButtonsSize();
			yMax = 1.f-(float)posY*getButtonsSize();
			yMin = yMax-getButtonsSize();				
			
			initTool(id,selected,toolX,toolY,dir);
			if (dimmed) mButtonInstance.setColor(colorDimmed);

		}
	}
	
	
	
	private static final int idReset = 0;
	private static final int idPlay = 1;
	private static final int idPause = 2;
	private static final int idZoomIn = 3;
	private static final int idZoomOut = 4;
	private static final int idEditorView = 5; 
	private static final int idHome = 6;
	
	private static final int idCameraSwitch = 7;
	private static final int idScreenshot = 8;
	private static final int idShowConstraints = 9;
	
	private static final int idCameraSwitchBuy = 10;
	private static final int idScreenshotBuy = 11;
	private static final int idShowConstraintsBuy = 12;

	private UIpreview3D mUI;
	
	public MenuButtonsPreview(UIpreview3D ui,boolean anim)
	{
		animate(anim); 
		
		mUI = ui;
		mItems.add(new IconItemMain(idReset,getButtonsColumns()*0.5f-1.5f,getButtonsLines()-1,false,4,0,Direction.down,false));
		mItems.add(new IconItemMain(idPlay,getButtonsColumns()*0.5f-0.5f,getButtonsLines()-1,mUI.isSimulationOn(),5,0,Direction.down,false));
		mItems.add(new IconItemMain(idPause,getButtonsColumns()*0.5f+0.5f,getButtonsLines()-1,!mUI.isSimulationOn(),6,0,Direction.down,false));
		
		
		mItems.add(new IconItemMain(idHome,0,getButtonsLines()-1,false,3,0,Direction.down,false));
		mItems.add(new IconItemMain(idEditorView,1,getButtonsLines()-1,false,3,1,Direction.down,false));

		if (ShopManager.instance.isPurchased(ShopManager.AddonType.ONBOARD_CAMERA)) 
			mItems.add(new IconItemMain(idCameraSwitch,0,getButtonsLines()*0.5f-1,false,2,3,Direction.left,false));
		else
			mItems.add(new IconItemMain(idCameraSwitchBuy,0,getButtonsLines()*0.5f-1,false,2,3,Direction.left,true));
		if (ShopManager.instance.isPurchased(ShopManager.AddonType.SHOW_CONSTRAINTS)) 
			mItems.add(new IconItemMain(idShowConstraints,0,getButtonsLines()*0.5f,mUI.mShowConstraints,7,2,Direction.left,false));
		else
			mItems.add(new IconItemMain(idShowConstraintsBuy,0,getButtonsLines()*0.5f,false,7,2,Direction.left,true));
		
		if (ShopManager.instance.isPurchased(ShopManager.AddonType.SCREENSHOT)) 
			mItems.add(new IconItemMain(idScreenshot,getButtonsColumns()-1,getButtonsLines()*0.5f-0.5f,false,7,0,Direction.right,false));
		else
			mItems.add(new IconItemMain(idScreenshotBuy,getButtonsColumns()-1,getButtonsLines()*0.5f-0.5f,false,7,0,Direction.right,true));
		
		mItems.add(new IconItemMain(idZoomIn,getButtonsColumns()-1,getButtonsLines()-1,false,1,3,Direction.down,false));
		mItems.add(new IconItemMain(idZoomOut,getButtonsColumns()-2,getButtonsLines()-1,false,0,3,Direction.down,false));
	}

	

	@Override
	public void onItemJustSelected(Item item) {
		super.onItemJustSelected(item);
				
		switch (item.mID)
		{
		case idHome:
		case idEditorView:
		case idScreenshot:
		case idScreenshotBuy:
		case idShowConstraintsBuy:
		case idCameraSwitchBuy:
			animate(true);
			break;
		default:
			animate(false);
			break;
			

		}
	}



	@Override
	public void onItemSelected(Item item) {
		super.onItemSelected(item);
		
		switch (item.mID)
		{
		case idReset:
			mUI.setPaused(true);
			Level.get().resetSimulation();
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			GameActivity.instance.playSnd(GameActivity.sndRewind,1);
			break;
		case idPlay:
			mUI.setPaused(false);
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			//mUI.mCamEffects.addEffect(new ZFrustumCameraEffectShake(500,20,24,32,1,1,1));
			break;
		case idPause:
			mUI.setPaused(true);
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			break;
		case idCameraSwitch:
			mUI.switchCamera();
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			break;
		case idShowConstraints:
			mUI.mShowConstraints = !mUI.mShowConstraints;
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			break;
		case idScreenshot:
			mUI.takeScreenshot(new MenuButtonsPreview(mUI,true));
			break;
		case idHome:
			((GameActivity)ZActivity.instance).setUI(new UImenus(true));
			break;
		case idEditorView:
			((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
			break;
		case idZoomIn:
			mUI.mCamController.zoomIn();
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			break;
		case idZoomOut:
			mUI.mCamController.zoomOut();
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,false));
			break;
			
		case idCameraSwitchBuy:
			mUI.setPaused(true);
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.ONBOARD_CAMERA,
					new Runnable(){ public void run() {ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,true));} },
					null,
					true,true));
			break;
		case idShowConstraintsBuy:
			mUI.setPaused(true);
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.SHOW_CONSTRAINTS,
					new Runnable(){ public void run() {ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,true));} },
					null,
					true,true));
			break;
		case idScreenshotBuy:
			mUI.setPaused(true);
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.SCREENSHOT,
					new Runnable(){ public void run() {ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,true));} },
					null,
					true,true));
			break;

		}
	}




	@Override
	public void handleBackKey() { selectItem(idEditorView); }

	public boolean doesHandleBackKey() {return true;}	
}
