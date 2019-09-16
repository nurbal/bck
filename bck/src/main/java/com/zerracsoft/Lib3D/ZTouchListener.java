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

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


// This class is meant to receive all touch events, and queue them.
// These events won't be executed until ZTouchListener::update() is called,
// so that user interaction will occur in the same thread as ZScene::update

public class ZTouchListener implements OnTouchListener {



	enum TouchMode
	{
		NONE,
		MOVE,
		PINCH
	}
	private TouchMode mTouchMode = TouchMode.NONE;
	float mTouchMoveStartX;
	float mTouchMoveStartY;
	float mTouchPinchStartX;
	float mTouchPinchStartY;
	float mTouchPinchStartDistance;
	
	public interface InputEvent
	{
		public void run();	// run this f***ing event
	}
	
	public class backKeyEvent implements InputEvent
	{
		public void run()
		{
			ZActivity.instance.mScene.getMenu().handleBackKey();
		}
	}
	
	public class touchEvent implements InputEvent
	{
		private int mAction;
		private float mX;
		private float mY;
		private float mPinchDistance;
		private int mNbContacts = 1;
		public touchEvent(MotionEvent evt)
		{
			// type d'action
			mAction = evt.getAction() & MotionEvent.ACTION_MASK;
			
			// x/y
			mX = evt.getX();
			mY = evt.getY();
			
			// multi-touch?
			mPinchDistance = 0;
			mNbContacts= 1;
			if (android.os.Build.VERSION.SDK_INT >= 5)
			{
				// multi-touch enabled
				mNbContacts = evt.getPointerCount();
				if (2==mNbContacts)
				{
					mX = (evt.getX(1)+evt.getX())*0.5f;
					mY = (evt.getY(1)+evt.getY())*0.5f;
					float dx = evt.getX(1)-evt.getX();
					float dy = evt.getY(1)-evt.getY();
					mPinchDistance = (float)Math.sqrt(dx*dx+dy*dy);
				}			
			}
			
		}
		public void run()
		{
			// traitement par defaut: menus ?
			if (ZActivity.instance.handleMenuEvent(mAction, mX, mY)) return; // pour les menus !!
			
			// traitement custom: move/pinch
			switch (mTouchMode)
			{
			case NONE:
				if (1==mNbContacts)
				{
					// begin move
					mTouchMoveStartX = mX;
					mTouchMoveStartY = mY;
					mTouchMode = TouchMode.MOVE;
					ZActivity.instance.onTouchMoveStart(mTouchMoveStartX, mTouchMoveStartY);
				}
				else if (2==mNbContacts)
				{
					// begin pinch
					mTouchPinchStartX = mX;
					mTouchPinchStartY = mY;
					mTouchPinchStartDistance = mPinchDistance;
					mTouchMode = TouchMode.PINCH;
					ZActivity.instance.onTouchPinchStart(mTouchPinchStartX,mTouchPinchStartY);
				}
				break;
			case MOVE:
				if (1==mNbContacts)
				{
					// move
					ZActivity.instance.onTouchMove(mTouchMoveStartX, mTouchMoveStartY, mX, mY);
				}
				else if (2==mNbContacts)
				{
					// begin pinch
					ZActivity.instance.onTouchMoveEnd();
					mTouchPinchStartX = mX;
					mTouchPinchStartY = mY;
					mTouchPinchStartDistance = mPinchDistance;
					mTouchMode = TouchMode.PINCH;
					ZActivity.instance.onTouchPinchStart(mTouchPinchStartX,mTouchPinchStartY);
				}
				if (mAction==MotionEvent.ACTION_UP)
				{
					// end move
					ZActivity.instance.onTouchMoveEnd();
					mTouchMode = TouchMode.NONE;
				}
				break;
			case PINCH:
				if (1==mNbContacts)
				{
					// move; ignore
				}
				else if (2==mNbContacts) 
				{
					// pinch
					float factor = mPinchDistance/mTouchPinchStartDistance;
					ZActivity.instance.onTouchPinch(mTouchPinchStartX,mTouchPinchStartY,mX, mY,factor);
				}
				if (mAction==MotionEvent.ACTION_UP)
				{
					// end pinch
					ZActivity.instance.onTouchPinchEnd();
					mTouchMode = TouchMode.NONE;
				}
				break;
			}	

		}
	}
 
	private static final Object mSyncObject = new Object();
	private ArrayList<InputEvent> mEventsList = new ArrayList<InputEvent>(16);	// preallocation pour 16 events, largement plus que suffisant en theorie...
		
	
	public boolean onTouch(View v, MotionEvent event) { 
		
		synchronized (mSyncObject)
		{
			ZActivity.instance.hideICSStatusBar();
			
			mEventsList.add(new touchEvent(event));

		} 
		return true;
	}
	
	public boolean handleBackKey()
	{
		synchronized(mSyncObject)
		{

			if (ZActivity.instance.mScene.getMenu()!=null && ZActivity.instance.mScene.getMenu().doesHandleBackKey())
			{
				// queue for async. treatment
				mEventsList.add(new backKeyEvent());
				//ZActivity.instance.mScene.getMenu().handleBackKey();
				return true;
			}
			
			return false;
		}
	}
	
	public void update()
	{
		// traitement de tous les messages stackes...
		synchronized(mSyncObject)
		{
			while (!mEventsList.isEmpty())
				mEventsList.remove(0).run();
		}
	}
}
