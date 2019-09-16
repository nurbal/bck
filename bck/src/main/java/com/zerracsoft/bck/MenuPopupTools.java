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

public class MenuPopupTools extends MenuPopup
{
	private static final int idMove = 0;
	private static final int idCreateBar = 2;
	private static final int idCreateCable = 3;
	private static final int idCreateHydraulicExtend = 4;
	private static final int idCreateHydraulicRetract = 5;

	UIeditor2D mEditorUI;
	public MenuPopupTools(UIeditor2D ui) {
//		super(-ZRenderer.getScreenRatioF()+0.5f*getButtonsSize(),1.f-2.5f*getButtonsSize(),-ZRenderer.getScreenRatioF()-0.5f*getButtonsSize(),1.f-2.5f*getButtonsSize(),0);
		super(-ZRenderer.getScreenRatioF()+0.5f*getButtonsSize(),1.f-(getButtonsLines()*0.5f)*getButtonsSize(),-ZRenderer.getScreenRatioF()-0.5f*getButtonsSize(),1.f-(getButtonsLines()*0.5f)*getButtonsSize(),0);
		mEditorUI = ui;
		initItems();
	}

	public void initItems() {
		StatusWorld status = Level.get().mStatus.mWorld;
		mItems.add(new IconItemPopup(idMove,0,mEditorUI.mEditMode == UIeditor2D.EditMode.MOVENODE,0,2,true));
		mItems.add(new IconItemPopup(idCreateBar,1,mEditorUI.mEditMode == UIeditor2D.EditMode.CREATEBAR,2,2,(status.mInfiniteBudget || status.mSpentBar<status.mBudgetBar)));
		mItems.add(new IconItemPopup(idCreateCable,2,mEditorUI.mEditMode == UIeditor2D.EditMode.CREATECABLE,3,2,(status.mInfiniteBudget || status.mSpentCable<status.mBudgetCable)));
		mItems.add(new IconItemPopup(idCreateHydraulicExtend,3,mEditorUI.mEditMode == UIeditor2D.EditMode.CREATEHYDRAULICEXTEND,4,2,(status.mInfiniteBudget || status.mSpentJack<status.mBudgetJack)));
		mItems.add(new IconItemPopup(idCreateHydraulicRetract,4,mEditorUI.mEditMode == UIeditor2D.EditMode.CREATEHYDRAULICRETRACT,5,2,(status.mInfiniteBudget || status.mSpentJack<status.mBudgetJack)));
	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			return;
		}
		
		switch (item.mID)
		{
		case idMove:
			mEditorUI.mEditMode = UIeditor2D.EditMode.MOVENODE;
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idCreateBar:
			mEditorUI.mEditMode = UIeditor2D.EditMode.CREATEBAR;
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idCreateCable:
			mEditorUI.mEditMode = UIeditor2D.EditMode.CREATECABLE;
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idCreateHydraulicExtend:
			mEditorUI.mEditMode = UIeditor2D.EditMode.CREATEHYDRAULICEXTEND;
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idCreateHydraulicRetract:
			mEditorUI.mEditMode = UIeditor2D.EditMode.CREATEHYDRAULICRETRACT;
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		}
		
	}

}
