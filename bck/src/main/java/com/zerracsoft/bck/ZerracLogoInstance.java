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

import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class ZerracLogoInstance extends ZInstance 
{
	protected ZInstance mainInstance;
	protected ZInstance wheelInstance;
	
	// les UVs des differents elements
	private static final float Wlogo = 528.f/790.f;
	private static final float Hlogo = 340.f/395.f;
	private static final float RayWheel = 125.f/790.f;
	private static final float Wwheel = 250.f/790.f;
	private static final float Hwheel = 250.f/395.f;
	private static final float UwheelPlace = 247.f/790.f;
	private static final float VwheelPlace = 215.f/395.f;
	private static final float UwheelSource = 660.f/790.f;
	private static final float VwheelSource = 125.f/395.f;
	
	// angle de ratation en static, car ne doit pas etre remis a zero a chaque fois qu'on change de contexte....
	private static float wheelRotation;
	
	public ZerracLogoInstance()
	{
		float logoRatio = 2*Wlogo/Hlogo;
		
		ZMesh m;
		m = new ZMesh();
		m.createRectangle(-1, -1/logoRatio, 1, 1/logoRatio, 0,Hlogo,Wlogo,0);
		m.setMaterial("zerraclogo");
		mainInstance = new ZInstance(m);
		add(mainInstance);
		
		m = new ZMesh();
		float ray = RayWheel*2/Wlogo;	// le logo est considere faire 2 de large
		m.createRectangle(-ray, -ray, ray, ray, UwheelSource-Wwheel*0.5f,VwheelSource+Hwheel*0.5f,UwheelSource+Wwheel*0.5f,VwheelSource-Hwheel*0.5f);
		m.setMaterial("zerraclogo");
		wheelInstance = new ZInstance(m);
		wheelInstance.setTranslation(-1.f+2.f*UwheelPlace/Wlogo, (1-2*VwheelPlace/Hlogo)/logoRatio, 0);
		add(wheelInstance);
		
	}
	
	static ZQuaternion q = new ZQuaternion();
	public void update(int elapsedTime)
	{
		
		float dt = 0.001f*(float)elapsedTime;
		
		wheelRotation -= 0.25f*dt;
		while (wheelRotation<0) wheelRotation += Math.PI*2.f;
		q.set(ZVector.constZ, wheelRotation);
		wheelInstance.setRotation(q);

	}
}
