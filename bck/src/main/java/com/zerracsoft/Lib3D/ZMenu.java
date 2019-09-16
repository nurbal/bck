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

package com.zerracsoft.Lib3D;

import java.util.ArrayList;

/*
 * This class will handle user-interaction, with  "clickable" items onscreen
 * 
 */
public abstract class ZMenu {

	static final int nbItemsMax = 20;
	protected ArrayList<Item> mItems = new ArrayList<Item>(nbItemsMax);;
	
	// length of the appearing/disappearing animations 
	public int mEnterTime = 500;	
	public int mExitTime = 500;
	public int mLongTouchTime = 300;
	protected Item mSelectedItem = null;
	protected Item mTouchedItem = null;
	protected ZMenu mNextMenu = null;
	
	// states
	public enum State
	{
		ENTER,
		IDLE,
		TOUCHING,
		EXIT,
		EXIT_TO_ANOTHER_MENU,
		DONE
	}
	protected State mState = State.ENTER;
	int mStateTime = 0;
	public void setState(State state)
	{
		mStateTime = 0;
		mState = state;
	}
	public State getState() {return mState;}
	
	private boolean mCreated = false;
	public void create() {}	// called on first update
	
	public void destroy()
	{
		for (int i=0; i<mItems.size(); i++)		mItems.get(i).destroy();
	}
	
	public void update(int timeIncrement)
	{
		if (!mCreated)
		{
			create();
			mCreated = true;
		}
		
		mStateTime += timeIncrement;
		
		// state change?
		switch (mState)
		{
		case ENTER:
			if (mStateTime > mEnterTime)			
				setState(State.IDLE);
			break;
		case TOUCHING:
			if (mStateTime > mLongTouchTime)
				if (onItemLongTouched(mTouchedItem))
				{
					destroy();
					setState(State.DONE);
				}
			break;
		case EXIT:
			if (mStateTime > mExitTime)		
			{
				setState(State.DONE);
				destroy();
				onItemSelected(mSelectedItem);
			}
			break;
		case EXIT_TO_ANOTHER_MENU:
			if (mStateTime > mExitTime)		
			{
				setState(State.DONE);
				destroy();
				ZActivity.instance.mScene.setMenu(mNextMenu);
			}
			break;
			
		}
		
		// update menu items
		switch (mState)
		{
		case ENTER:
			{
				float progress = 1;
				if (mEnterTime>0) progress = (float)mStateTime / (float)mEnterTime;
				updateEnter(timeIncrement, progress);
			}
			break;
		case IDLE:
		case TOUCHING:
			updateIdle(timeIncrement,mStateTime);
			break;
		case EXIT:
		case EXIT_TO_ANOTHER_MENU:
			{
				float progress = 0;
				if (mExitTime>0) progress = (float)mStateTime / (float)mExitTime;
				updateExit(timeIncrement,progress);
			}
			break;
		}
	}
	
	public void updateEnter(int elapsedTime, float progress)
	{
		for (int i=0; i<mItems.size(); i++)		mItems.get(i).updateEnter(elapsedTime,progress,mStateTime);		
	}

	public void updateIdle(int elapsedTime, int stateTime)
	{
		for (int i=0; i<mItems.size(); i++)		
			if (mItems.get(i)==mTouchedItem)
			{
				mItems.get(i).updateTouched(elapsedTime,stateTime,Math.min(1.f, (float)stateTime/(float)mLongTouchTime));
			}
			else
				mItems.get(i).updateIdle(elapsedTime,stateTime);
	}
	
	public void updateExit(int elapsedTime, float progress)
	{
		for (int i=0; i<mItems.size(); i++)		mItems.get(i).updateExit(elapsedTime,progress,mStateTime,mSelectedItem==mItems.get(i));
	}

	
	public void selectItem(int itemID)
	{
		if (mState!=State.IDLE && mState!=State.TOUCHING) return;
		for (int i=0; i<mItems.size(); i++)		
			if (mItems.get(i).mID==itemID)	
				mSelectedItem = mItems.get(i);
		setState(State.EXIT); 
		onItemJustSelected(mSelectedItem);
	}
	
	public void exitToAnotherMenu(ZMenu menu) // exits WITHOUT selecting any item
	{
		if (mState!=State.IDLE && mState!=State.TOUCHING) return;
		mSelectedItem = null;
		mNextMenu = menu;
		setState(State.EXIT_TO_ANOTHER_MENU); 
		onItemJustSelected(null);
	}
	
	// overridables
	public void onItemJustSelected(Item item) {} // called at the beginning of menu exit
	public void onItemSelected(Item item) {}
	public void onItemTouched(Item item) {}
	public boolean onItemLongTouched(Item item) {return false;}	// return true if you must exit menu
	
	/*
	 * This class will handle clickable menu items
	 */
	public class Item
	{
		// display
		public void updateIdle(int elapsedTime, int time) {}
		public void updateTouched(int elapsedTime, int time, float longTouchProgress) {updateIdle(elapsedTime,time);}	// same as idle but when the butotn is pressed
		public void updateEnter(int elapsedTime, float progress, int time) {}
		public void updateExit(int elapsedTime, float progress, int time,boolean selected) {}
		
		// destroy graphics assets
		public void destroy() {}
		
		public int mID;
		public boolean mEnabled = true;
		public float xMin,xMax,yMin,yMax;	
		public boolean testPointF(float x, float y)		{return (x>=xMin && x<= xMax && y>=yMin && y<=yMax);}
	}

	public boolean onTouchStart(float mTouchStartX, float mTouchStartY) 
	{
		if (mState != State.IDLE) return false;	// ignore if not in the right state
		setState(State.TOUCHING);
		for (int i=0; i<mItems.size(); i++)
		{
			Item item = mItems.get(i);
			if (item.testPointF(mTouchStartX,mTouchStartY))
			{
				if (item.mEnabled)
					
				// this is the selected item! remember it please
				mTouchedItem = item;
				onItemTouched(item);
				return true;
			}
		}
		return onTouchOutsideMenu(mTouchStartX, mTouchStartY);
		
	}
	
	// return true if touch is treated
	public boolean onTouchOutsideMenu(float touchX, float touchY)
	{
		return false;
	}
	
	public void onTouchMove(float mTouchStartX, float mTouchStartY,	float touchMoveX, float touchMoveY) {	}
	public void onTouchSwipe(boolean itemTouched,float dx, float dy) {}
	
	public void onTouchEnd(float startX, float startY, float endX, float endY) // coords in [-1..1] range
	{
		if (mState != State.TOUCHING) return;	// ignore if not in the right state
		setState(State.IDLE);
		if (mTouchedItem!=null && mTouchedItem.testPointF(endX,endY))
		{
			// this is the selected item! start the "exiting menu" animation
			selectItem(mTouchedItem.mID);
			mTouchedItem = null;
		}
		else
		{
			//swipe!
			onTouchSwipe(mTouchedItem!=null,endX-startX,endY-startY);
		}
	}
	
	public boolean isTouchExclusive() {return false;}
	
	public void handleBackKey() {}
	
	public abstract boolean doesHandleBackKey();
		
	
}
