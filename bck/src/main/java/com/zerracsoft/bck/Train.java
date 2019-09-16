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

import java.util.ArrayList;

import com.zerracsoft.Lib3D.ZVector;

public class Train implements TestVehicle
{
	private ArrayList<TrainElement> mElements = new ArrayList<TrainElement>(ReleaseSettings.MAX_TRAIN_SIZE);
	
	private boolean mBroken = false;
	
	public void setBroken(boolean b)
	{
		if (mBroken == b) return;
		mBroken = b;
		for (TrainElement elt:mElements)
			elt.setBroken(b);
		if (mBroken) 
		{
			Level.get().vehicleFailed();
			GameActivity.instance.playSnd(GameActivity.sndDeath,1);
		}
		
	}
	
	private boolean mSunk = false;
	private int mUnderwaterSound = 0;
	public void setSunk(boolean b)
	{
		if (mSunk == b) return;
		mSunk = b;
		if (mSunk) Level.get().vehicleFailed();
		
		if (mSunk && 0==mUnderwaterSound)
			mUnderwaterSound = GameActivity.instance.playSnd(GameActivity.sndUnderwater,1);
		if (!mSunk && 0!=mUnderwaterSound)
		{
			GameActivity.instance.stopSnd(mUnderwaterSound);
			mUnderwaterSound = 0;
		}			
	}
	
	public Train()
	{
		// todo init des wagons
		
		mElements.add(new TrainLoco());
		mElements.add(new TrainWagon("wood_yellow","wood_blue","wood_green"));
		mElements.add(new TrainWagon("wood_blue","wood_green","wood_magenta"));
		mElements.add(new TrainWagon("wood_red","wood_yellow","wood_blue"));
		mElements.add(new TrainWagon("wood_green","wood_magenta","wood_yellow"));
	}
	
	public static Train get()
	{
		return (Train)(Level.get().mTrain);
	}
	
	public void updateSimulation(float dt)
	{
		for (TrainElement elt:mElements) 
			elt.updateSimulation(dt);
		// self-collision
		if (mBroken)
			for (TrainElement elt1:mElements) 
				for (TrainElement elt2:mElements) 
					elt1.collide(elt2);

	}
	
	public void initGraphics()
	{
		for (TrainElement elt:mElements) 
			elt.initGraphics();
		setVisible(false);
	}
	
	public ZVector getPosition()
	{
		return mElements.get(0).getPosition(); 
	}
	
	public void detachElements(TrainElement elt, TrainElement previsousElt)
	{
		// todo: marquage du level failed
		elt.mPrevious = null;
		setBroken(true);
	}
	
	public void resetSimulation() 
	{
		setBroken(false);
		setSunk(false);
		
		for (int i=0; i<mElements.size(); i++)
		{
			TrainElement elt = mElements.get(i);
			
			// init order between elements
			if (i==0) elt.mPrevious = null;
			else elt.mPrevious = mElements.get(i-1);
			
			// set position
			elt.setPosition(Level.get().getXMin() + (mElements.size()-i)*2.f*(ReleaseSettings.TRAIN_WHEEL_HALF_DISTANCE+ReleaseSettings.TRAIN_WHEEL_RAY) - 1);
			
			elt.resetSimulation();
		}
		
		
	}
	
	public void updateGraphics(int elapsedTime)
	{
		for (TrainElement elt:mElements)
			elt.updateGraphics(elapsedTime);
	}

	public void resetGraphics()
	{
		for (TrainElement elt:mElements)
			elt.resetGraphics();
	}

	@Override
	public void setVisible(boolean visible) 
	{ 
		for (TrainElement elt:mElements) 
			elt.setVisible(visible);
	}

	@Override
	public boolean isVisible() 
	{
		return mElements.get(0).isVisible();
	}

	@Override
	public boolean isSuccess() 
	{
		// succes si arrive au bout en un seul morceau...
		return (!mBroken && (mElements.get(0).getPosition().get(0)>=Level.get().getXMax()-1));
	}

	@Override
	public ZVector getFrontDirection() {
		return mElements.get(0).getDirection();
	}

	@Override
	public void setPaused(boolean paused) {
		for (TrainElement elt:mElements) 
			elt.setPaused(paused);
	}
}
