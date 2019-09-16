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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.Attributes;

import com.zerracsoft.Lib3D.ZActivity;

public class StatusLevel 
{
	public StatusWorld mWorld;
//	private boolean mAchieved = false;	
//	private boolean mAchievedOnce = false;	// utile pour le rewarding
//	private int mBudgetSpent = 0;
//	private int mBudgetRemaining = 1337;
	private int mResourceID;	// resource ID of the level design
	private int mSaveID;
	

	public enum State { NONE, LOCKED, DONE }
	public State mState = State.LOCKED;
		
	// budget
	public int mSpentBar = 0;
	public int mSpentCable = 0;
	public int mSpentHydraulic = 0;
	
	// tutorial
	public boolean mTutorialPlayed = false;
	
	// rewarding tapjoy
	public boolean mRewarded = false;
	public boolean mRewardable = true;
	
	@SuppressWarnings("unused")
	private StatusLevel() {}
	
	public StatusLevel(StatusWorld world, int saveID, int resourceID)
	{
		mSaveID = saveID;
		mWorld = world;
		mResourceID = resourceID;
	}
	
	public InputStream getLevelDesignInputStream() {return ZActivity.instance.getResources().openRawResource(mResourceID);}
	
	public void load(Attributes attributes)
	{
		mState = State.NONE; 
		String locked = attributes.getValue("locked");
		if (locked!=null && Boolean.parseBoolean(locked) && !ReleaseSettings.DEBUG_UNLOCK_ALL_LEVELS) mState = State.LOCKED; 
		String done = attributes.getValue("done");
		if (done!=null && Boolean.parseBoolean(done)) mState = State.DONE; 
		String budgetBar = attributes.getValue("bars");
		if (budgetBar!=null) mSpentBar = Integer.parseInt(budgetBar); 
		String budgetCable = attributes.getValue("cables");
		if (budgetCable!=null) mSpentCable = Integer.parseInt(budgetCable); 
		String budgetHydraulic = attributes.getValue("hydraulics");
		if (budgetHydraulic!=null) mSpentHydraulic = Integer.parseInt(budgetHydraulic); 
		String tutorialPlayed = attributes.getValue("tuto");
		if (tutorialPlayed!=null) mTutorialPlayed = Boolean.parseBoolean(tutorialPlayed); 
		String rewarded = attributes.getValue("rewarded");
		if (tutorialPlayed!=null) mRewarded = Boolean.parseBoolean(rewarded); 
	}
	
	public void save(int id,OutputStream out) throws IOException
	{
		out.write(("\t\t<level id=\""+Integer.toString(mSaveID)+"\" locked=\""+Boolean.toString(mState==State.LOCKED)+"\" tuto=\""+Boolean.toString(mTutorialPlayed)+"\" done=\""+Boolean.toString(mState==State.DONE)+"\" rewarded=\""+Boolean.toString(mRewarded)+"\" bars=\""+Integer.toString(mSpentBar)+"\" cables=\""+Integer.toString(mSpentCable)+"\" hydraulics=\""+Integer.toString(mSpentHydraulic)+"\"/>\n").getBytes());
	}

	StatusLevel getNext() { return mWorld.getNextLevel(this); }
	
	public void markAsLastPLayed() 
	{
		mWorld.setLastPlayedLevel(this); 
	}
	
	public boolean isFinalWorldLevel()
	{
		return this==mWorld.mLevels[mWorld.mLevels.length-1];
	}
	
	public void setDone()
	{
		mState = State.DONE;
		StatusLevel next = getNext();
		if (next!=null)
			if (next.mState==State.LOCKED)
				next.mState = State.NONE;
		mWorld.updateState();
		if (next!=null && next.mWorld!=mWorld)
			next.mWorld.updateState();
		// reward
		/*
		if (!mRewarded && mRewardable)
		{
			if (isFinalWorldLevel())
				BCKTapjoyManager.instance.rewardCredits(ReleaseSettings.CREDITS_WORLD,null,null);
			else
				BCKTapjoyManager.instance.rewardCredits(ReleaseSettings.CREDITS_LEVEL,null,null);
			mRewarded = true;
		}
		*/

		((GameActivity)ZActivity.instance).mGameStatus.save();	// evite les gros TRICHEURS!!!
	}
	
}
