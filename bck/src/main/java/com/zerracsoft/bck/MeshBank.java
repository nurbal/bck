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
import java.util.ArrayList;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class MeshBank 
{
	private static MeshBank instance;
	public static MeshBank get() {return instance;}
	
	private Resources mResources;
	public MeshBank(Resources res)
	{
		mResources = res;
		instance = this;
	}

	private ArrayList<ZInstance> mInstances = new ArrayList<ZInstance>();
	
	public void loadAll()
	{
		loadMesh(R.raw.pen,"pen",2,null,ZQuaternion.constZ90,ZInstance.LOADFLAG_DEFAUT);
		loadMesh(R.raw.lego,"lego",2,null,ZQuaternion.constZ90,ZInstance.LOADFLAG_DEFAUT);
		loadMesh(R.raw.cube,"cube",2,null,ZQuaternion.constZ90,ZInstance.LOADFLAG_DEFAUT);
		loadMesh(R.raw.gare,"gare",0.5f,new ZVector(0,2,0),ZQuaternion.constZ90,ZInstance.LOADFLAG_DEFAUT);
		
		ZInstance bigBoat = loadMesh(R.raw.cargo,"boat_big",3,new ZVector(0,-0.75f,-2.05f),ZQuaternion.constZ270,ZInstance.LOADFLAG_DEFAUT);
		ZInstance smallBoat = loadMesh(R.raw.voilier,"boat_small",1,new ZVector(0,-0.125f,-1.575f),ZQuaternion.constZ270,ZInstance.LOADFLAG_DEFAUT);
		
		// temp: boat collision box ghosts
		/*
		ZMesh m = new ZMesh(); 
		m.createBox(new ZVector(ReleaseSettings.BOAT_SMALL_HALF_WIDTH,ReleaseSettings.BOAT_SMALL_HALF_LENGTH,ReleaseSettings.BOAT_SMALL_HALF_HEIGHT));
		ZInstance i = new ZInstance(); i.setTranslation(0, 0, ReleaseSettings.BOAT_SMALL_HALF_WIDTH-ReleaseSettings.BOAT_SMALL_CENTER_HEIGHT);
		i.add(m);
		//i.setColor(new ZColor(1,0,0,0.5f));
		smallBoat.add(i);
		m = new ZMesh();
		m.createBox(new ZVector(ReleaseSettings.BOAT_BIG_HALF_WIDTH,ReleaseSettings.BOAT_BIG_HALF_LENGTH,ReleaseSettings.BOAT_BIG_HALF_HEIGHT));
		i = new ZInstance(); i.setTranslation(0, 0, ReleaseSettings.BOAT_BIG_HALF_WIDTH-ReleaseSettings.BOAT_BIG_CENTER_HEIGHT);
		i.add(m);
		i.setColor(new ZColor(0,0,1,0.5f));
		bigBoat.add(i);
		*/ 
		
		ZInstance room = loadMesh(R.raw.room,"room",8*254,null,null,ZInstance.LOADFLAG_DEFAUT);//50);
		//appendMesh(room, R.raw.room_alpha,1,null,null);
		//appendMesh(room, R.raw.room_no_light,1,null,null);
		loadMesh(R.raw.room_alpha,"room_alpha",8*254,null,null,ZInstance.LOADFLAG_DEFAUT);
		loadMesh(R.raw.room_no_light,"room_no_light",8*254,null,null,ZInstance.LOADFLAG_NO_NORMAL);
	} 
	
	public ZInstance loadMesh(int resID, String name, float scale, ZVector translation, ZQuaternion rotation, int loadFlags)
	{ 
		ZInstance i = new ZInstance(); 
		try {
			i.load(mResources.openRawResource(resID),loadFlags);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
			return null; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		i.mName = name;
		i.setScale(scale*0.0254f,scale*0.0254f,scale*0.0254f);
		if (translation!=null) i.setTranslation(translation);
		if (rotation!=null) i.setRotation(rotation);
		mInstances.add(i);
		return i;
	}
	
	public boolean appendMesh(ZInstance parent, int resID, float scale, ZVector translation, ZQuaternion rotation, int loadFlags)
	{
		ZInstance i = new ZInstance();
		try {
			i.load(mResources.openRawResource(resID),loadFlags);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
			return false; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		//i.setScale(scale*0.0254f,scale*0.0254f,scale*0.0254f);
		if (translation!=null) i.setTranslation(translation);
		if (rotation!=null) i.setRotation(rotation);
		parent.add(i);
		return true;
	}
	
	public ZInstance instanciate(String name)
	{
		for (ZInstance i:mInstances)
			if (i.mName.equals(name))
			{
				ZInstance i2 = new ZInstance (i);
				//ZActivity.instance.mScene.add(i2);
				return i2;
			}
		return null;
	}
}
