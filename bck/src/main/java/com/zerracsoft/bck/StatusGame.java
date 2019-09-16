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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.zerracsoft.Lib3D.ZActivity;

import android.os.Environment;

/*
 * The game status object is the complete status of all levels, in all worlds
 * It also contains some specific user preferences (like sound preferences)
 */

public class StatusGame 
{
	private ArrayList<StatusWorld> mWorlds = new ArrayList<StatusWorld>(100);
	
	// game state values
	private int mLastPlayedWorldID = 1;
	
	public int nbSkippedWorlds = 0;
	
	// public boolean mSound = true; // use ZActivity.instance.mSoundEnabled instead !
	
	public StatusWorld getWorld(int id)
	{
		for (StatusWorld w:mWorlds)
			if (w.mID==id)
				return w;
		return null;
	}
	
	public StatusWorld getLastPlayedWorld()
	{
		StatusWorld w = getWorld(mLastPlayedWorldID);
		if (w!=null)
			return w;
		return getWorld(1); // par defaut...
	}
	public void setLastPlayedWorld(StatusWorld w)
	{
		mLastPlayedWorldID = w.mID;
	}
	
	public StatusLevel getLastPlayedLevel()
	{
		return getLastPlayedWorld().getLastPlayedLevel();
	}
	
	public StatusWorld getNextWorld(StatusWorld world)
	{
		for (int i=0; i<mWorlds.size()-1; i++)
			if (mWorlds.get(i)==world)
				return mWorlds.get(i+1);
		return null; 
	}
	
	public StatusWorld getPreviousWorld(StatusWorld world)
	{
		for (int i=1; i<mWorlds.size(); i++)
			if (mWorlds.get(i)==world)
				return mWorlds.get(i-1);
		return null; 
	}
	
	public StatusGame()
	{
		// init world
		init();
		
		// load status
		load();
	}
	
	public void init()
	{
		StatusWorld w;
		if (ReleaseSettings.DEBUG_LEVELS)
		{
			w = new StatusWorld(this);	w.initDebugWorld1(); mWorlds.add(w);
			w = new StatusWorld(this);	w.initDebugWorld2(); mWorlds.add(w);
		}
		
		//if (BCKTapjoyVirtualGoodsManager.instance.isFreebuildModeAvailable())
		{
			w = new StatusWorld(this);	w.initFreebuildWorld1(); mWorlds.add(w);
			//w = new StatusWorld(this);	w.initFreebuildWorld2(); mWorlds.add(w); Not yet implemented....
		}
		
		w = new StatusWorld(this);	w.initWorld1(); mWorlds.add(w);
		w = new StatusWorld(this);	w.initWorld2(); mWorlds.add(w);
		w = new StatusWorld(this);	w.initWorld3(); mWorlds.add(w);
		w = new StatusWorld(this);	w.initWorld4(); mWorlds.add(w);
		w = new StatusWorld(this);	w.initWorld5(); mWorlds.add(w);
		
	}

	public void load()
	{
		String filename = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX+ReleaseSettings.STATUS_GAME_FILENAME;
		if (!ReleaseSettings.DEBUG_SAVEGAME_SDCARD)
			filename = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX+ReleaseSettings.STATUS_GAME_FILENAME;	
		File f = new File(filename);
		try { load(new BufferedInputStream(new FileInputStream(f),8192)); } catch (FileNotFoundException e) { e.printStackTrace(); }
	}	
	
	public void load(InputStream in)
	{
		StatusGameLoader ldr = new StatusGameLoader();
		ldr.load(in, this);	
		nbSkippedWorlds = 0;
		for (StatusWorld w:mWorlds)
		{
			w.updateState();
			w.updateBudget();
			if (w.mSkipped) nbSkippedWorlds++;
		}
	}

	public void load(Attributes attributes)
	{
		String lastWorld = attributes.getValue("lastworld");
		if (lastWorld!=null)
			mLastPlayedWorldID = Integer.parseInt(lastWorld);
		String sound = attributes.getValue("sound");
		if (sound!=null)
			ZActivity.instance.mSoundEnabled = Boolean.parseBoolean(sound);
	}

	public void save()
	{
		String dirname = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX;
		if (!ReleaseSettings.DEBUG_SAVEGAME_SDCARD)
			dirname = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX;	
		File dir = new File(dirname);
		dir.mkdirs();
		String filename = dirname+ReleaseSettings.STATUS_GAME_FILENAME;
		File f = new File(filename);
		try { 
			OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			save(out);
			out.flush();
			} catch (IOException e) { e.printStackTrace(); }
	}	
	
	public void save(OutputStream out) throws IOException
	{
		out.write(("<game lastworld=\""+Integer.toString(mLastPlayedWorldID)+"\" sound=\""+Boolean.toString(ZActivity.instance.mSoundEnabled)+"\">\n").getBytes());
		for (StatusWorld w:mWorlds)
			w.save(out);
		out.write("</game>\n".getBytes());
	}
}
