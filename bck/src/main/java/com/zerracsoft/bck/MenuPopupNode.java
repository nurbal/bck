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

public class MenuPopupNode extends MenuPopup
{
	private static final int idSeparator0 = 0;
	private static final int idSeparator45 = 1;
	private static final int idSeparator90 = 2;
	private static final int idSeparator135 = 3;
	private static final int idNotSeparator = 4;
	private static final int idDelete = 5;

	UIeditor2D mEditorUI;
	Level mLevel;
	Node mNode;
	public MenuPopupNode(UIeditor2D ui,Level level,Node node) 
	{
		super(-3.f*getButtonsSize(),Math.min(ui.zWorld2yViewport(node.getPositionZ())+getButtonsSize(), ZRenderer.screenToViewportYF(0)-0.5f*getButtonsSize()),
				ui.xWorld2xViewport(node.getPositionX()),ui.zWorld2yViewport(node.getPositionZ()),
				0);
		mEditorUI = ui;
		mLevel = level;
		mNode = node;
		initItems();
	}

	public void initItems() {
		StatusWorld status = Level.get().mStatus.mWorld;
		mItems.add(new IconItemPopup(idNotSeparator,0,!mNode.isSeparator(),3,3,true));
		mItems.add(new IconItemPopup(idSeparator0,1,mNode.isSeparator0(),4,3,status.mBudgetJack>0));
		mItems.add(new IconItemPopup(idSeparator45,2,mNode.isSeparator45(),5,3,status.mBudgetJack>0));
		mItems.add(new IconItemPopup(idSeparator90,3,mNode.isSeparator90(),6,3,status.mBudgetJack>0));
		mItems.add(new IconItemPopup(idSeparator135,4,mNode.isSeparator135(),7,3,status.mBudgetJack>0));
		mItems.add(new IconItemPopup(idDelete,6,false,1,2,true));
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
		switch (item.mID)
		{
		case idNotSeparator:
			mEditorUI.backupLevel();
			mNode.unsetSeparator();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idSeparator0:
			mEditorUI.backupLevel();
			mNode.setSeparator(0);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idSeparator45:
			mEditorUI.backupLevel();
			mNode.setSeparator(0.25f*(float)Math.PI);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idSeparator90:
			mEditorUI.backupLevel();
			mNode.setSeparator(0.5f*(float)Math.PI);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idSeparator135:
			mEditorUI.backupLevel();
			mNode.setSeparator(0.75f*(float)Math.PI);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;
		case idDelete:
			mEditorUI.backupLevel();
			mNode.destroy();
			mLevel.remove(mNode);
			mLevel.fixIntegrity();
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			mEditorUI.setTouchState(TouchState.NONE);
			break;

		}
		
	}


}
