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
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;

public class MenuLevelResult extends MenuText 
{
	
	static final int idReplay = 1;
	static final int idEditor = 2;
	static final int idNextLevel = 3;
	
	static ZVector titlePosition = new ZVector(0,0.65f,0);
	boolean mSucceed;
	UIpreview3D mUI;
	StatusLevel mNextLevel;
	StatusWorld mNextWorld;
	
	
	private ZInstance logo;
	
	private int mTextAnimTime = 0;
	
	private static ZQuaternion mTitleAngle=new ZQuaternion();
	@Override
	public void updateIdle(int elapsedTime, int stateTime) {
		// TODO Auto-generated method stub
		super.updateIdle(elapsedTime, stateTime);
		
		if (stateTime == 0)
		{
			if (mSucceed)
				GameActivity.startMusicWin();
			else
				GameActivity.startMusicLoose();
		}
		
		color.set(1,1,1,1);
		logo.setColor(color);
		logo.setScale(1, 1, 1);
		
		if (!mSucceed)
		{
			mTextAnimTime += elapsedTime;
			// animation de decrochage du texte
			if (mTextAnimTime>1000)
			{
				float t = (float)(mTextAnimTime-1000)*0.001f;
				float angle = (float)(Math.PI*0.5*(1-/*(1-t/3.0)*/Math.exp(-t*0.5)*Math.cos(t*5.0))); 
				mTitleAngle.set(ZVector.constZNeg, angle);
				mTitleTextInstance.setRotation(mTitleAngle);
			}
			/*
			 * on ne fait plus tomber le texte completement
			if (stateTime>4000)
			{
				float t = (float)(stateTime-4000)*0.001f;
				mTitleTextInstance.translate(0,-t*0.5f,0);
			}
			*/
		}
		
	}

	
	
	@Override
	public void updateExit(int elapsedTime, float progress) {
		super.updateExit(elapsedTime, progress);
		
		color.set(1,1,1,1-progress);
		logo.setColor(color);
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		super.updateEnter(elapsedTime, progress);

		color.set(1,1,1,progress);
		logo.setColor(color);
		logo.setScale(2-progress, 2-progress, 1);
	}



	private static ZColor color = new ZColor();
	
	public MenuLevelResult(boolean succeed)
	{
		super(true,true);
		mSucceed = succeed;
		mUI = (UIpreview3D)UI.get();
		mNextLevel = Level.get().mStatus.getNext();
		mNextWorld = Level.get().mStatus.mWorld.getNext();
		if (succeed)
			setTitle(R.string.level_succeed,titlePosition,ZFont.AlignH.CENTER);
		else
			setTitle(R.string.level_failed,titlePosition,ZFont.AlignH.LEFT);
				
		mItems.add(new TextItem(idReplay,R.string.menu_level_result_replay,ZRenderer.getScreenRatioF()*0.5f,0.3f));
		mItems.add(new TextItem(idEditor,R.string.menu_level_result_back_to_editor,ZRenderer.getScreenRatioF()*0.5f,-0.0f));
		if (succeed && mNextLevel!=null) mItems.add(new TextItem(idNextLevel,R.string.menu_level_result_next_level,ZRenderer.getScreenRatioF()*0.5f,-0.3f));

		ZMesh m = new ZMesh();
		m.createRectangle(-0.5f,-0.5f,0.5f,0.5f,0,1,1,0);
		if (succeed) m.setMaterial("win"); else m.setMaterial("loose");
		logo = new ZInstance(m);
		logo.setTranslation(new ZVector(-ZRenderer.getScreenRatioF()*0.5f,0,-1));
		color.set(1,1,1,0);
		logo.setColor(color);
		logo.setScale(2, 2, 1);
		ZActivity.instance.mScene.add2D(logo);

	}

	@Override
	public void onItemSelected(Item item) 
	{
		if (item==null)
		{
			((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
			return;
		}
		
		switch(item.mID)
		{
		case idReplay:
			mUI.setPaused(false);
			Level.get().resetSimulation();
			ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(mUI,true));
			break;
		case idEditor:
			((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
			break;
		case idNextLevel:
			mNextLevel.markAsLastPLayed();
			Level.get().init(mNextLevel);
			Level.get().loadGame();
			((GameActivity)ZActivity.instance).setUI(new UIeditor2D());
			break;
		}
		
		super.onItemSelected(item);
	}

	@Override
	public void handleBackKey() { selectItem(idEditor); }
	public boolean doesHandleBackKey() {return true;}	// returns true if handled

	public void destroy()
	{
		super.destroy();
		ZActivity.instance.mScene.remove(logo);
	}
}
