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

import com.zerracsoft.Lib3D.ZColor;


public abstract class MenuPopup extends MenuButtons
{
	public class IconItemPopup extends IconItem 
	{
		public IconItemPopup(int id, int pos, boolean selected, int toolX, int toolY, boolean enabled)
		{
			xMin = mX+(float)pos*getButtonsSize()-0.5f*getButtonsSize();
			xMax = xMin+getButtonsSize();
			yMax = mY+0.5f*getButtonsSize();
			yMin = yMax-getButtonsSize();				
			
			mEnabled = enabled;
			
			initTool(id,selected,toolX,toolY,MenuButtons.Direction.none);
			
			float x = (mPopupX-0.5f*(xMin+xMax))/mScaleX;
			float y = (mPopupY-0.5f*(yMin+yMax))/mScaleY;
			float z=0; if (selected) z+=2;
			if (mButtonInstance!=null)
			{
				mButtonInstance.setTranslation(x, y, z-1);
				if (!mEnabled) mButtonInstance.setColor(new ZColor(0.5f,0.5f,0.5f,0.5f));
			}
			//if (mSelectedInstance!=null) mSelectedInstance.setTranslation(x, y, z-0.5f);
			//if (mIconInstance!=null) 
			//{
			//	mIconInstance.setTranslation(x, y, z);
			//	if (!mEnabled) mIconInstance.setColor(new ZColor(0.5f,0.5f,0.5f,0.5f));
			//}

		}
		
		@Override
		public void updateEnter(int elapsedTime, float progress, int time) 
		{
			float x = (mPopupX-0.5f*(xMin+xMax))*(1-progress)/mScaleX;
			float y = (mPopupY-0.5f*(yMin+yMax))*(1-progress)/mScaleY;
			float z=0; 
			//if (mSelectedInstance!=null) z+=2;
			if (mButtonInstance!=null) mButtonInstance.setTranslation(x, y, z-1);
			//if (mSelectedInstance!=null) mSelectedInstance.setTranslation(x, y, z-0.5f);
			//if (mIconInstance!=null) mIconInstance.setTranslation(x, y, z);
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			float z=0; 
			if (mButtonInstance!=null) mButtonInstance.setTranslation(0,0, z-1);
			//if (mSelectedInstance!=null) mSelectedInstance.setTranslation(x, y, z-0.5f);
			//if (mIconInstance!=null) mIconInstance.setTranslation(x, y, z);
			super.updateIdle(elapsedTime, time);
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time, boolean selected) {
			float x = (mPopupX-0.5f*(xMin+xMax))*(progress)/mScaleX;
			float y = (mPopupY-0.5f*(yMin+yMax))*(progress)/mScaleY;
			float z=0; if (selected) z+=2;
			if (mButtonInstance!=null) mButtonInstance.setTranslation(x, y, z-1);
			//if (mSelectedInstance!=null) mSelectedInstance.setTranslation(x, y, z-0.5f);
			//if (mIconInstance!=null) mIconInstance.setTranslation(x, y, z);
		}


	}
	
	protected float mPopupX,mPopupY,mX,mY;
	protected int mSelectedButton;
	// on dit ou ca pop, et quelle est le bouton selectionne par defaut
	public MenuPopup(float x, float y, float popupX, float popupY, int selectedBtn)
	{
		mX = x;
		mY = y;
		mPopupX = popupX;
		mPopupY = popupY;
		mSelectedButton = selectedBtn;
		mEnterTime = ReleaseSettings.MENU_POPUP_ENTER_TIME;
		mExitTime = ReleaseSettings.MENU_POPUP_EXIT_TIME;
	}

	
	
	@Override
	public void handleBackKey() { selectItem(-1); }
	
	public boolean doesHandleBackKey()
	{
		return true;
	}



	@Override
	public boolean onTouchOutsideMenu(float touchX, float touchY)
	{
		selectItem(-1);
		return true;
	}
	
}
