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
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMenu.Item;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;

public class MenuMain extends MenuText 
{

	static final int idPlay = 0;
	static final int idAbout = 1;
//	static final int idGetCredits = 2;
//	static final int idShop = 3;
	static final int idSound = 4;
	//static final int idShopOld = 10;
	
	static ZColor col = new ZColor();
	// swtch de son....
	public class SoundSwitch extends Item
	{
		ZInstance mInstance;
		
		
		public SoundSwitch(int id, float x, float y, float halfSize)
		{
			// create instance
			ZMesh mesh = new ZMesh();
			mesh.createRectangle(-halfSize,-halfSize,+halfSize,+halfSize,0,1,1,0);
			if (ZActivity.instance.mSoundEnabled)
				mesh.setMaterial(ZActivity.instance.mScene.getMaterial("sound_on"));
			else
				mesh.setMaterial(ZActivity.instance.mScene.getMaterial("sound_off"));
			mInstance = new ZInstance(mesh);
			col.set(1,1,1,0);
			mInstance.setColor(col);
			mInstance.setTranslation(x, y, 1);
			ZActivity.instance.mScene.add2D(mInstance);
			
			xMin = x-halfSize;
			xMax = x+halfSize;
			yMin = y-halfSize;
			yMax = y+halfSize;
			
			mID = id;
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			col.set(1,1,1,1);
			mInstance.setColor(col);
			super.updateIdle(elapsedTime, time);
		}

		@Override
		public void updateEnter(int elapsedTime, float progress, int time) {
			col.set(1,1,1,progress);
			mInstance.setColor(col);
			super.updateEnter(elapsedTime, progress, time);
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time,
				boolean selected) {
			col.set(1,1,1,1-progress);
			mInstance.setColor(col);
			super.updateExit(elapsedTime, progress, time, selected);
		}

		@Override
		public void destroy() {
			ZActivity.instance.mScene.remove(mInstance);
			super.destroy();
		}
		
	}
	
	
	static ZColor color = new ZColor();
	
	public MenuMain(boolean anim,boolean animFrame)
	{
		super(anim,animFrame);
	}
	
	public void create()
	{	
		ZMesh m;
		
		// logo Zerrac
		GameActivity ga = (GameActivity)GameActivity.instance;
		if (null==ga.mZerracLogoInstance) 
			ga.mZerracLogoInstance = new ZerracLogoInstance();
		ga.mZerracLogoInstance.setScale(0.3f,0.3f,1);		
		ga.mZerracLogoInstance.setTranslation(-ZRenderer.getScreenRatioF()*0.75f,-0.8f,0);
		color.set(1,1,1,0);
		ga.mZerracLogoInstance.setColor(color);
		ZActivity.instance.mScene.add2D(ga.mZerracLogoInstance);
		
		// logo du jeu
		m = new ZMesh();
		m.createRectangle(-0.5f,-0.5f,0.5f,0.5f,0,1,1,0);
		m.setMaterial("bcklogo");
		if (null==ga.mGameLogoInstance)
			ga.mGameLogoInstance = new ZInstance(m);
		ga.mGameLogoInstance.setTranslation(-ZRenderer.getViewportWidthF()*0.25f,0.25f,0);
		color.set(1,1,1,0);
		ga.mGameLogoInstance.setColor(color);
		ZActivity.instance.mScene.add2D(ga.mGameLogoInstance);

		
		
		float x = ZRenderer.getViewportWidthF()*0.25f;

//		mItems.add(new TextItem(idPlay,R.string.main_menu_play,x,0.6f));
//		mItems.add(new TextItem(idShop,R.string.main_menu_shop,x,0.2f));
//		mItems.add(new TextItem(idGetCredits,R.string.main_menu_earn_credits,x,-0.2f));
//		mItems.add(new TextItem(idAbout,R.string.main_menu_about,x,-0.6f));

		mItems.add(new TextItem(idPlay,R.string.main_menu_play,x,0.2f));
		mItems.add(new TextItem(idAbout,R.string.main_menu_about,x,-0.2f));

		mItems.add(new SoundSwitch(idSound,-0.25f*ZRenderer.getScreenRatioF(),-0.85f,0.1f));

		//mItems.add(new TextItem(idShopOld,String.format(ZActivity.instance.getResources().getString(R.string.main_menu_shop_old),BCKTapjoyManager.instance.mCredits),-x,-0.8f));
		
	}

	
	
	@Override
	public void onItemJustSelected(Item item) {
		// TODO Auto-generated method stub
		super.onItemJustSelected(item);
		
		if (item==null)
			animate(true,true);
		else
		{
			switch (item.mID)
			{
//			case idShop:
//				animate(true,false);
//				break;
			case idSound:
				animate(false,false);
				break;
			default:
				animate(true,true);
				break;
			}
		}
	}

	@Override
	public void onItemSelected(Item item) {
		super.onItemSelected(item);
		
		if (item==null)
		{
			// on quitte le jeu
			ZActivity.instance.finish();
			return;
		}
		
		switch (item.mID)
		{
		case idPlay: 		
			//((MainScreen)ZActivity.instance).setUI(new UIeditor2D());
			ZActivity.instance.mScene.setMenu(new MenuLevelChoice(((GameActivity)ZActivity.instance).mGameStatus.getLastPlayedWorld(),MenuLevelChoice.Direction.Up));
			break;


		//case idShopOld:
		//	BCKTapjoyManager.instance.showVirtualGoods();
		//	break;	
			
		//case idShop:
		//	ZActivity.instance.mScene.setMenu(new MenuShopMain(true,false));
		//	break;
			
		case idAbout:
			ZActivity.handler.post( new Runnable() {public void run() { ZActivity.instance.showDialog(GameActivity.dlgAbout); }});	
			break;		
			
		case idSound:
			ZActivity.instance.mSoundEnabled = !ZActivity.instance.mSoundEnabled;
			if (ZActivity.instance.mSoundEnabled)
				GameActivity.startMusicMenu();
			else
				GameActivity.stopMusics();
			ZActivity.instance.mScene.setMenu(new MenuMain(false,false));
			break;
		}
	}

	@Override
	public void updateExit(int elapsedTime, float progress) {
		color.set(1,1,1,1.f-progress);
		((GameActivity)GameActivity.instance).mZerracLogoInstance.setColor(color);
		((GameActivity)GameActivity.instance).mGameLogoInstance.setColor(color);
		super.updateExit(elapsedTime, progress);
		
		// fondu musique
		if (this.mSelectedItem == null
				||	(mSelectedItem.mID!=idPlay
						&& mSelectedItem.mID!=idAbout
		//				&& mSelectedItem.mID!=idShop
						))
				GameActivity.setMenuMusicVolume(1-progress);
	}

	static int lastStateTime = 0;
	@Override
	public void setState(State state) 
	{
		lastStateTime = 0;
		super.setState(state);
	}

	@Override
	public void updateIdle(int elapsedTime, int stateTime) {
		color.set(1,1,1,1);
		((GameActivity)GameActivity.instance).mZerracLogoInstance.setColor(color);
		((GameActivity)GameActivity.instance).mGameLogoInstance.setColor(color);
		
		int dt = stateTime-lastStateTime;
		lastStateTime = stateTime;
		((GameActivity)GameActivity.instance).mZerracLogoInstance.update(dt);
		
		super.updateIdle(elapsedTime, stateTime);
		
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		color.set(1,1,1,progress);
		((GameActivity)GameActivity.instance).mZerracLogoInstance.setColor(color);
		((GameActivity)GameActivity.instance).mGameLogoInstance.setColor(color);
		super.updateEnter(elapsedTime, progress);
	}

	
	
	@Override
	public void destroy() {
		super.destroy();
		ZActivity.instance.mScene.remove(((GameActivity)GameActivity.instance).mZerracLogoInstance);
		ZActivity.instance.mScene.remove(((GameActivity)GameActivity.instance).mGameLogoInstance);
	}

	@Override
	public void handleBackKey() { if (mState==State.IDLE) selectItem(-1); }
	
	public boolean doesHandleBackKey() {return true;}	// returns true if handled

}
