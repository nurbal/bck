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
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.Lib3D.ZMenu.Item;
import com.zerracsoft.bck.MenuText.TextItem;

public class MenuResetLevel extends MenuText 
{

	static final int idOk = 1;
	static final int idCancel = 2;
	
	static ZVector titlePosition = new ZVector(0,0.6f,0);
	
	ZInstance mInstanceText;
	static ZColor color = new ZColor();
	
	UIeditor2D mEditorUI;
		
	public MenuResetLevel(UIeditor2D ui)
	{
		super(true,true);
		setTitle(R.string.menu_reset_level_title,titlePosition,ZFont.AlignH.CENTER);
		mEditorUI = ui;
				
		mItems.add(new TextItem(idCancel,R.string.menu_reset_level_cancel,-ZRenderer.getScreenRatioF()*0.5f,-0.2f));
		mItems.add(new TextItem(idOk,R.string.menu_reset_level_ok,ZRenderer.getScreenRatioF()*0.5f,-0.2f));

		String s = ZActivity.instance.getResources().getString(R.string.menu_reset_level_text);
		mInstanceText = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
		mInstanceText.setTranslation(0,ZRenderer.alignScreenGrid(0.2f),1);
		color.set(1,1,1,0);
		mInstanceText.setColor(color);
		ZActivity.instance.mScene.add2D(mInstanceText);
		
	}
	
	@Override
	public void destroy() 
	{
		ZActivity.instance.mScene.remove(mInstanceText);
		super.destroy();
	}

	@Override
	public void updateExit(int elapsedTime, float progress) {
		super.updateExit(elapsedTime, progress);
		color.set(1,1,1,1-progress);
		mInstanceText.setColor(color);
	}

	@Override
	public void updateIdle(int elapsedTime, int stateTime) {
		super.updateIdle(elapsedTime, stateTime);
		color.set(1,1,1,1);
		mInstanceText.setColor(color);
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		super.updateEnter(elapsedTime, progress);
		color.set(1,1,1,progress);
		mInstanceText.setColor(color);
	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			return;
		}
		
		Level level = ((GameActivity)ZActivity.instance).mLevel;
		
		switch(item.mID)
		{
		case idOk:
			mEditorUI.backupLevel();
			level.resetGameplay(false);
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		case idCancel:
			ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(mEditorUI,true));
			break;
		}
		
		super.onItemSelected(item);
	}


	
	@Override
	public void handleBackKey() { selectItem(idCancel); }
	public boolean doesHandleBackKey() {return true;}	// returns true if handled

	
	
}
