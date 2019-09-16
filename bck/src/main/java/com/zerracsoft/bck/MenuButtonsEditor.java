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
import com.zerracsoft.bck.MenuButtons.Direction;
import com.zerracsoft.bck.MenuButtonsPreview.IconItemMain;

public class MenuButtonsEditor extends MenuButtons {

	public static ZColor colorDimmed = new ZColor(1,1,1,0.5f);
	class IconItemEditor extends IconItem
	{
		public IconItemEditor(int id, float posX, float posY, boolean selected, int toolX, int toolY,Direction dir,boolean dimmed)
		{
			xMin = -1*ZRenderer.getScreenRatioF()+(float)posX*getButtonsSize();
			xMax = xMin+getButtonsSize();
			yMax = 1.f-(float)posY*getButtonsSize();
			yMin = yMax-getButtonsSize();				
			
			initTool(id,selected,toolX,toolY,dir);
			dimButton();
			if (dimmed) mButtonInstance.setColor(colorDimmed);
		}
		
		protected void dimButton()
		{
			// griser le bouton "undo" si pas de undo disponible
			if (mID==idUndo)
			{
				if (!mEditorUI.isUndoable())
					mButtonInstance.setColor(colorDimmed);
				else
					mButtonInstance.setColor(ZColor.constWhite);
			}
			if (mID==idRedo)
			{
				if (!mEditorUI.isRedoable())
					mButtonInstance.setColor(colorDimmed);
				else
					mButtonInstance.setColor(ZColor.constWhite);
			}
			
		}
		
		@Override
		public void updateIdle(int elapsedTime, int time) 
		{ 
			dimButton();
			super.updateIdle(elapsedTime, time);
		}
	}
	
	
	
	private static final int idUndo = 1;
	private static final int idFileReset = 0;
	private static final int idZoomIn = 2;
	private static final int idZoomOut = 3;
	private static final int idPlayView = 4;
	private static final int idHome = 5;
	private static final int idTools = 6;
	private static final int idRedo = 7;
	private static final int idScreenshot = 8;
	private static final int idScreenshotBuy = 9;
	private static final int idReplayTuto = 10;
	

	protected UIeditor2D mEditorUI;
	
	public MenuButtonsEditor(UIeditor2D ui,boolean anim)
	{
		mEditorUI = ui;
		animate(anim);
		initItems();
	}
	
	public void initItems()
	{
		mItems.add(new IconItemEditor(idFileReset,0,0,false,7,1,Direction.left,false));
		if (Level.get().mTutorialsID.size()!=0)
			mItems.add(new IconItemEditor(idReplayTuto,0,1,false,6,1,Direction.left,false));
		mItems.add(new IconItemEditor(idHome,0,getButtonsLines()-1,false,3,0,Direction.down,false));
		mItems.add(new IconItemEditor(idPlayView,1,getButtonsLines()-1,false,5,0,Direction.down,false));
		mItems.add(new IconItemEditor(idUndo,getButtonsColumns()*0.5f-1.f,getButtonsLines()-1,false,4,1,Direction.down,false));
		mItems.add(new IconItemEditor(idRedo,getButtonsColumns()*0.5f,getButtonsLines()-1,false,5,1,Direction.down,false));
		mItems.add(new IconItemEditor(idZoomIn,getButtonsColumns()-1,getButtonsLines()-1,false,1,3,Direction.down,false));
		mItems.add(new IconItemEditor(idZoomOut,getButtonsColumns()-2,getButtonsLines()-1,false,0,3,Direction.down,false));

		switch (mEditorUI.mEditMode)
		{
		case MOVENODE: mItems.add(new IconItemEditor(idTools,0,getButtonsLines()*0.5f-0.5f,false,0,2,Direction.left,false)); break;
		case CREATEBAR: mItems.add(new IconItemEditor(idTools,0,getButtonsLines()*0.5f-0.5f,false,2,2,Direction.left,false)); break;
		case CREATECABLE: mItems.add(new IconItemEditor(idTools,0,getButtonsLines()*0.5f-0.5f,false,3,2,Direction.left,false)); break;
		case CREATEHYDRAULICEXTEND: mItems.add(new IconItemEditor(idTools,0,getButtonsLines()*0.5f-0.5f,false,4,2,Direction.left,false)); break;
		case CREATEHYDRAULICRETRACT: mItems.add(new IconItemEditor(idTools,0,getButtonsLines()*0.5f-0.5f,false,5,2,Direction.left,false)); break;
		}
		
		if (ShopManager.instance.isPurchased(ShopManager.AddonType.SCREENSHOT)) 
			mItems.add(new IconItemEditor(idScreenshot,getButtonsColumns()-1,getButtonsLines()*0.5f-0.5f,false,7,0,Direction.right,false));
		else
			mItems.add(new IconItemEditor(idScreenshotBuy,getButtonsColumns()-1,getButtonsLines()*0.5f-0.5f,false,7,0,Direction.right,true));
		
	}
	

	@Override
	public void onItemJustSelected(Item item) {
		// TODO Auto-generated method stub
		super.onItemJustSelected(item);
		
		if (item==null)
		{
			animate(true);
			return;
		}
		animate(false);
		
		switch (item.mID)
		{
		case idTools: 
		case idFileReset:
		case idPlayView: 
		case idHome: 
		case idScreenshot:
		case idScreenshotBuy:
			animate(true); 
			break;
		case idZoomIn: 
		case idZoomOut:
		case idUndo:
		case idRedo:
		case idReplayTuto:
			animate(false); 
			break;
		}
		
	}

	@Override
	public void onItemSelected(Item item) {
		super.onItemSelected(item);
/*		
		if (item==null)
		{
			//ZActivity.instance.mScene.setMenu(new MenuLevelChoice(((MainScreen)ZActivity.instance).mGameStatus.mWorlds[0],MenuLevelChoice.Direction.Up));
			Level.get().saveGame();
			((GameActivity)ZActivity.instance).setUI(new UImenus(true));
			return;
		}
*/		
		switch (item.mID)
		{
		case idFileReset:
			//ZActivity.instance.mScene.setMenu(new MenuPopupFile(mEditorUI));
			ZActivity.instance.mScene.setMenu(new MenuResetLevel(mEditorUI));
			break;
		case idTools:
			ZActivity.instance.mScene.setMenu(new MenuPopupTools(mEditorUI));
			break;
		case idHome:
			Level.get().saveGame();
			((GameActivity)ZActivity.instance).setUI(new UImenus(true));
			break;
		case idPlayView:
			Level.get().saveGame();
			((GameActivity)ZActivity.instance).setUI(new UIpreview3D());
			break;
		case idZoomIn:
			mEditorUI.zoomFactor.set(mEditorUI.zoomFactor.get()*0.5f);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,false));
			break;
		case idZoomOut:
			mEditorUI.zoomFactor.set(mEditorUI.zoomFactor.get()*2.f);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,false));
			break;
			
		case idUndo:
			mEditorUI.undo();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,false));
			break;
		case idRedo:
			mEditorUI.redo();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,false));
			break;
			
		case idScreenshot:
			mEditorUI.takeScreenshot(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idScreenshotBuy:
			ZActivity.instance.mScene.setMenu(new MenuShopPurchaseItem(ShopManager.AddonType.SCREENSHOT,
					new Runnable(){ public void run() {ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));} },
					null,
					true,true));
			break;

		case idReplayTuto:
			mEditorUI.replayTuto();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,false));
			break;

		}
	}



	@Override
	public void handleBackKey() { selectItem(idHome); }
	
	public boolean doesHandleBackKey() {return true;}	
	
	

}
