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

import android.util.Log;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZParticleSystemNewton;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class TrainLoco extends TrainElement {
	
	ZParticleSystemNewton mSmokeP6;
	ZInstance mSmokeInstance;

	static ZVector v1 = new ZVector();
	static ZVector v2 = new ZVector();


	@Override
	public void initGraphics() {
		super.initGraphics();

		if (UI.get().getViewMode() != UI.ViewMode.MODE3D)
			return;

		mSmokeP6 = new ZParticleSystemNewton();
		mSmokeP6.setMaterial("p6_smoke", 8,1);
		v1.set(0,0,0.3f);
		v2.set(0,0,0.3f);
		mSmokeP6.init(ReleaseSettings.P6_TRAIN_SMOKE_NBPARTILES);// c'est cette ligne la qui bloque (eh merde)
		mSmokeP6.setParameters(ZVector.constZero, v1, v2, ZVector.constZero, ZVector.constZero, 4000, 3000, 0.5f, 0.3f, 0.2f,2,false);
		mSmokeInstance = new ZInstance(mSmokeP6);
		ZActivity.instance.mScene.add(mSmokeInstance);
		
		// creation d'une petite loco...
		// cabine 
		ZMesh m = new ZMesh();
		m.createBox(new ZVector(0.25f,0.375f,0.375f));
		m.setMaterial("wood_magenta");
		ZInstance i = new ZInstance(m);
		i.setTranslation(-0.5f,0,0.125f);
		mInstance = new ZInstance(i);
		// chaudiere
		m = new ZMesh();
		m.createCylinder(16, 0.25f, 0.625f);
		m.setMaterial("wood_green");
		i = new ZInstance(m);
		i.setTranslation(0.25f,0,0);
		i.setRotation(ZQuaternion.constY90);
		mInstance.add(i);
		// cheminee
		m = new ZMesh();
		m.createCylinder(16, 0.125f, 0.2f);
		m.setMaterial("wood_red");
		i = new ZInstance(m);
		i.setTranslation(0.75f,0,0.3f);
		mInstance.add(i);
		// roues
		m = new ZMesh();
		m.createCylinder(16, 0.25f, 0.0625f);
		m.setMaterial("wood_red");
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
		m.setMaterial("wood_fonce");
		i = new ZInstance(m);
		i.setTranslation(0,0,-0.195f);
		mInstance.add(i);		
		
		//mInstance.setColor(ZColor.constRed);
		ZActivity.instance.mScene.add(mInstance);
	}

	@Override
	public void updateGraphics(int elapsedTime) {
		// TODO Auto-generated method stub
		super.updateGraphics(elapsedTime);
		if (mSmokeP6!=null)
		{
			ZVector X = getDirection();
			v2.set(-X.get(2),0,X.get(0));
			v1.mulAddMul(X,0.25f,v2,0.5f);
			v1.add(mWheels[0].mPosition);
			mSmokeP6.setEmmiterPos(v1);
		}
	}

	@Override
	public void resetSimulation() 
	{
		if (mSmokeP6!=null) mSmokeP6.clearParticles();
		BCKLog.d("smokeP6","clearParticles");
		super.resetSimulation();
	}

	@Override
	public void resetGraphics() {
		// TODO Auto-generated method stub
		super.resetGraphics();
		ZActivity.instance.mScene.remove(mSmokeInstance);
		mSmokeInstance = null;
		mSmokeP6 = null;
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		super.setVisible(visible);
		if (mSmokeInstance!=null) mSmokeInstance.setVisible(visible);
		if (mSmokeP6!=null && !visible) mSmokeP6.pause(true);
	}

	@Override
	public void setPaused(boolean paused) {
		if (mSmokeP6!=null) mSmokeP6.pause(paused);
	}

}
