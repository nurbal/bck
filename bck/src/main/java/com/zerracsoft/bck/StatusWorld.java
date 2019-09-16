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
import java.io.OutputStream;

import org.xml.sax.Attributes;

import com.zerracsoft.Lib3D.ZActivity;


/*
 * 
 * This class describes a world and its status
 * A world is constituted by 5 levels: 4 bridges and 1 tower
 * The tower is unlocked once all bridges are built, and has to be made with the remaining budget of each level
 * The world is complete once the tower height objective is reached.
 * 
 */

public class StatusWorld 
{
	private StatusGame mStatusGame;
	public StatusWorld(StatusGame game) { mStatusGame = game; }
	@SuppressWarnings("unused")
	private StatusWorld() {}
	
	public String mName = "Temp world name";
	public int mID = 0;
	
	public enum State { NONE, LOCKED, DONE }
	public State mState = State.NONE;
	
	// world skip tapjoy
	public boolean mSkipped = false;

	
	// budget
	public int mBudgetBar = 1000;
	public int mSpentBar = 0;
	public int mBudgetCable = 1000;
	public int mSpentCable = 0;
	public int mBudgetJack = 1000;
	public int mSpentJack = 0;
	
	public boolean mInfiniteBudget = false;
	public boolean mFreebuild = false;
	
	// level status
	public StatusLevel mLevels[] = new StatusLevel[5];

	
	private int mLastPlayedLevelIndex = 0;
	
	public StatusLevel getLastPlayedLevel() { return mLevels[mLastPlayedLevelIndex]; }

	public void setLastPlayedLevel(StatusLevel l)
	{
		for (int i=0; i<5; i++)
			if (mLevels[i]==l)
			{
				mLastPlayedLevelIndex = i;
				mStatusGame.setLastPlayedWorld(this);
				return;
			}
	}
	
	
	
	public void initDebugWorld1()
	{
		mName = "Debug & tests World 1";
		mID = -1;
		mLevels[0]=new StatusLevel(this,0,R.raw.debug1_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.debug1_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.debug1_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.debug1_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.debug1_5);
		
		mLevels[0].mState = StatusLevel.State.NONE;
		mLevels[1].mState = StatusLevel.State.NONE;
		mLevels[2].mState = StatusLevel.State.NONE;
		mLevels[3].mState = StatusLevel.State.NONE;
		mLevels[4].mState = StatusLevel.State.NONE;
		updateState();
	}
	public void initDebugWorld2()
	{
		mName = "Debug & tests World 2";
		mID = -2;
		mLevels[0]=new StatusLevel(this,0,R.raw.debug2_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.debug2_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.debug2_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.debug2_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.debug2_5);
		
		mLevels[0].mState = StatusLevel.State.NONE;
		mLevels[1].mState = StatusLevel.State.NONE;
		mLevels[2].mState = StatusLevel.State.NONE;
		mLevels[3].mState = StatusLevel.State.NONE;
		mLevels[4].mState = StatusLevel.State.NONE;
		updateState();
	}
	
	public void initWorld1()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_1);
		mID = 1;
		mBudgetBar = 45;
		mBudgetJack = 0;
		mBudgetCable = 0;
		mLevels[0]=new StatusLevel(this,0,R.raw.level1_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.level1_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.level1_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.level1_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.level1_5);
		mLevels[0].mState = StatusLevel.State.NONE;
		updateState();
	}
	
	public void initFreebuildWorld1()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_free_1);
		mID = -100; // eh ouais, c'est .... special
		mInfiniteBudget = true;
		mFreebuild = true;
		mLevels[0]=new StatusLevel(this,0,R.raw.free1_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.free1_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.free1_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.free1_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.free1_5);
		for (StatusLevel level : mLevels)
		{
			level.mState = StatusLevel.State.NONE;
			level.mRewardable = false;
		}
		updateState();
	}
	public void initFreebuildWorld2()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_free_2);
		mID = -99; // eh ouais, c'est .... special
		mBudgetBar = 80;
		mBudgetJack = 0;
		mBudgetCable = 0;
		mLevels[0]=new StatusLevel(this,0,R.raw.free2_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.free2_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.free2_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.free2_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.free2_5);
		mLevels[0].mState = StatusLevel.State.NONE;
		mLevels[1].mState = StatusLevel.State.NONE;
		mLevels[2].mState = StatusLevel.State.NONE;
		mLevels[3].mState = StatusLevel.State.NONE;
		mLevels[4].mState = StatusLevel.State.NONE;
		updateState();
	}
	
	public void initWorld2()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_2);
		mID = 2;
		mBudgetBar = 40;
		mBudgetJack = 0;
		mBudgetCable = 60;
		mLevels[0]=new StatusLevel(this,0,R.raw.level2_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.level2_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.level2_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.level2_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.level2_5);
		updateState();
	}
	
	public void initWorld3()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_3);
		mID = 3;
		mBudgetBar = 70;
		mBudgetJack = 10;
		mBudgetCable = 0;
		mLevels[0]=new StatusLevel(this,0,R.raw.level3_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.level3_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.level3_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.level3_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.level3_5);
		updateState();
	}
	
	public void initWorld4()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_4);
		mID = 4;
		mBudgetBar = 125;
		mBudgetJack = 0;
		mBudgetCable = 6;
		mLevels[0]=new StatusLevel(this,0,R.raw.level4_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.level4_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.level4_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.level4_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.level4_5);
		updateState();
	}
	
	public void initWorld5()
	{
		mName = ZActivity.instance.getResources().getString(R.string.world_5);
		mID = 5;
		mBudgetBar = 600;
		mBudgetJack = 10;
		mBudgetCable = 50;
		mLevels[0]=new StatusLevel(this,0,R.raw.level5_1);
		mLevels[1]=new StatusLevel(this,1,R.raw.level5_2);
		mLevels[2]=new StatusLevel(this,2,R.raw.level5_3);
		mLevels[3]=new StatusLevel(this,3,R.raw.level5_4);
		mLevels[4]=new StatusLevel(this,4,R.raw.level5_5);
		updateState();
	}
	
	
	
	public void load(Attributes attributes)
	{
		String lastLevel = attributes.getValue("lastlevel");
		if (lastLevel!=null)
			mLastPlayedLevelIndex = Integer.parseInt(lastLevel);	
		
		String skipped = attributes.getValue("skipped");
		if (skipped!=null)
			mSkipped = Boolean.parseBoolean(skipped);		
		
	}
	
	public void save(OutputStream out) throws IOException
	{
		String s = "\t<world id=\""+Integer.toString(mID)+"\" lastlevel=\""+Integer.toString(mLastPlayedLevelIndex)+"\" skipped=\""+Boolean.toString(mSkipped)+"\">\n";
		out.write(s.getBytes());
		for (int i=0; i<5; i++)
			mLevels[i].save(i,out);
		out.write("\t</world>\n".getBytes());		 
	}
	
	StatusWorld getNext() { return mStatusGame.getNextWorld(this); }
	StatusWorld getPrevious() { return mStatusGame.getPreviousWorld(this); }
	void setLastPlayedWorld() { mStatusGame.setLastPlayedWorld(this); }
	
	public StatusLevel getNextLevel(StatusLevel level)
	{
		for (int i=0; i<mLevels.length; i++)
			if (mLevels[i]==level)
			{
				if (i==mLevels.length-1)
				{
					StatusWorld nextWorld = getNext();
					if (nextWorld!=null)
						return nextWorld.mLevels[0];
					return null;
				}
				else
					return mLevels[i+1];
			}
		return null; 
	}

	public void updateState()
	{
		// auto-unlock first level if previous world is complete...
		if (null!=getPrevious() && getPrevious().mState==State.DONE)
		{
			if (mLevels[0].mState == StatusLevel.State.LOCKED)
				mLevels[0].mState = StatusLevel.State.NONE;
		}
		
		// check state of all levels to updtae world's state
		boolean allLocked = true;
		boolean allDone = true;
		for (int i=0; i<5; i++)
		{
			allDone &= mLevels[i].mState==StatusLevel.State.DONE;
			allLocked &= mLevels[i].mState==StatusLevel.State.LOCKED;
		}
		mState = State.NONE;
		if (allDone) mState = State.DONE;
		if (allLocked) mState = State.LOCKED;
	}
	
	public void updateBudget()
	{
		mSpentBar = mSpentCable = mSpentJack = 0;
		for (int i=0; i<5; i++)
		{
			mSpentBar += mLevels[i].mSpentBar;
			mSpentCable += mLevels[i].mSpentCable;
			mSpentJack += mLevels[i].mSpentHydraulic;
		}
	}

}
