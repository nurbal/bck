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
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.bck.UIeditor2D.TouchState;

public class MenuPopupLink extends MenuPopup
{
	private static final int idDelete = 0;
	private static final int idBar = 1;
	private static final int idCable = 2;
	private static final int idHydraulicExtend = 3;
	private static final int idHydraulicRetract = 4;

	UIeditor2D mEditorUI;
	Level mLevel;
	Link mLink;
	public MenuPopupLink(UIeditor2D ui,Level level,Link link) {
//		super(-2.5f*getButtonsSize(),Math.min(ui.zWorld2yViewport(link.getPosition().get(2))+getButtonsSize(), 1.f-0.5f*getButtonsSize()),
//				ui.xWorld2xViewport(link.getPosition().get(0)),ui.zWorld2yViewport(link.getPosition().get(2)),
//				0);
		super(-2.5f*getButtonsSize(),Math.min(ui.zWorld2yViewport(link.getPosition().get(2))+getButtonsSize(), ZRenderer.screenToViewportYF(0)-0.5f*getButtonsSize()),
				ui.xWorld2xViewport(link.getPosition().get(0)),ui.zWorld2yViewport(link.getPosition().get(2)),
				0);
		mEditorUI = ui;
		mLevel = level;
		mLink = link;
		initItems();
	}

	public void initItems() {
		StatusWorld status = Level.get().mStatus.mWorld;
		float length = mLink.getNominalLength();		
		mItems.add(new IconItemPopup(idBar,0,(mLink.getClass()==LinkBar.class),2,2,(length<=ReleaseSettings.BAR_MAX_LENGTH && (status.mInfiniteBudget || status.mSpentBar<status.mBudgetBar))));
		mItems.add(new IconItemPopup(idCable,1,(mLink.getClass()==LinkCable.class),3,2,(length<=ReleaseSettings.CABLE_MAX_LENGTH && (status.mInfiniteBudget || status.mSpentCable<status.mBudgetCable))));
		mItems.add(new IconItemPopup(idHydraulicExtend,2,(mLink.getClass()==LinkJack.class && ((LinkJack)mLink).mType==LinkJack.Type.EXTEND),4,2,(length<=ReleaseSettings.HYDRAULIC_MAX_LENGTH && (status.mInfiniteBudget || status.mSpentJack<status.mBudgetJack))));
		mItems.add(new IconItemPopup(idHydraulicRetract,3,(mLink.getClass()==LinkJack.class && ((LinkJack)mLink).mType==LinkJack.Type.RETRACT),5,2,(length<=ReleaseSettings.HYDRAULIC_MAX_LENGTH && (status.mInfiniteBudget || status.mSpentJack<status.mBudgetJack))));
		mItems.add(new IconItemPopup(idDelete,5,false,1,2,true));
	}

	
	@Override
	public void onItemSelected(Item item) 
	{
		
		if (item==null)
		{
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			return;
		}
		
		Link newLink = null;
		switch (item.mID)
		{
		case idDelete:
			mEditorUI.backupLevel();
			mLink.destroy();
			mLevel.remove(mLink);
			mLevel.fixIntegrity();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idBar:
			mEditorUI.backupLevel();
			newLink = new LinkBar(mLink);
			mLink.destroy();
			mLevel.remove(mLink);
			mLink = newLink;
			mLink.initGraphics();
			mLevel.add(mLink);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idCable:
			mEditorUI.backupLevel();
			newLink = new LinkCable(mLink);
			mLink.destroy();
			mLevel.remove(mLink);
			mLink = newLink;
			mLink.initGraphics();
			mLevel.add(mLink);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idHydraulicExtend:
			mEditorUI.backupLevel();
			newLink = new LinkJack(mLink,LinkJack.Type.EXTEND);
			mLink.destroy();
			mLevel.remove(mLink);
			mLink = newLink;
			mLink.initGraphics();
			mLevel.add(mLink);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idHydraulicRetract:
			mEditorUI.backupLevel();
			newLink = new LinkJack(mLink,LinkJack.Type.RETRACT);
			mLink.destroy();
			mLevel.remove(mLink);
			mLink = newLink;
			mLink.initGraphics();
			mLevel.add(mLink);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		}
		
	}
	


}
