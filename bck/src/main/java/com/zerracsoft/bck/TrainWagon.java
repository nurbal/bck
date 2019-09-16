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
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class TrainWagon extends TrainElement {

	public TrainWagon(String cabinMaterial,String wheelMaterial,String barMaterial)
	{
		mCabinMaterial = cabinMaterial;
		mWheelMaterial = wheelMaterial;
		mBarMaterial = barMaterial;
	}
	
	String mWheelMaterial;
	String mCabinMaterial;
	String mBarMaterial;
	@Override
	public void initGraphics() {
		super.initGraphics();

		if (UI.get().getViewMode() != UI.ViewMode.MODE3D)
			return;
		
		// creation d'un petit wagon...
		// cabine
		ZMesh m = new ZMesh();
		m.createBox(new ZVector(0.75f,0.375f,0.375f));
		m.setMaterial(mCabinMaterial);
		ZInstance i = new ZInstance(m);
		i.setTranslation(0,0,0.125f);
		mInstance = new ZInstance(i);
		// roues
		m = new ZMesh();
		m.createCylinder(16, 0.25f, 0.0625f);
		m.setMaterial(mWheelMaterial);
		mWheelsSubInstances[0] = new ZInstance(m);
		mWheelsSubInstances[0].setTranslation(0.5f,0.4375f,ReleaseSettings.RAILS_THICKNESS-0.25f);
		mWheelsSubInstances[0].setRotation(ZQuaternion.constX90);
		mInstance.add(mWheelsSubInstances[0]);
		mWheelsSubInstances[1] = new ZInstance(m);
		mWheelsSubInstances[1].setTranslation(0.5f,-0.4375f,ReleaseSettings.RAILS_THICKNESS-0.25f);
		mWheelsSubInstances[1].setRotation(ZQuaternion.constX90);
		mInstance.add(mWheelsSubInstances[1]);
		mWheelsSubInstances[2] = new ZInstance(m);
		mWheelsSubInstances[2].setTranslation(-0.5f,0.4375f,ReleaseSettings.RAILS_THICKNESS-0.25f);
		mWheelsSubInstances[2].setRotation(ZQuaternion.constX90);
		mInstance.add(mWheelsSubInstances[2]);
		mWheelsSubInstances[3] = new ZInstance(m);
		mWheelsSubInstances[3].setTranslation(-0.5f,-0.4375f,ReleaseSettings.RAILS_THICKNESS-0.25f);
		mWheelsSubInstances[3].setRotation(ZQuaternion.constX90);
		mInstance.add(mWheelsSubInstances[3]);
		// barre de raccordement
		m = new ZMesh();
		m.createBox(new ZVector(1,0.125f,0.0625f));
		m.setMaterial(mBarMaterial);
		i = new ZInstance(m);
		i.setTranslation(0,0,-0.195f);
		mInstance.add(i);		
	
		ZActivity.instance.mScene.add(mInstance);
	}
	
	@Override
	public void setPaused(boolean paused) {}
	
}
