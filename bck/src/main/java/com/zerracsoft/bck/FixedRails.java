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
import com.zerracsoft.Lib3D.ZVector;


public class FixedRails implements Rails,GraphicsObject
{
	protected float mXMin;
	protected float mXMax;
	protected float mZ;
	
	protected ZInstance mInstance;
	
	public FixedRails(float xMin, float xMax, float z)
	{
		mXMin = xMin;
		mXMax = xMax;
		mZ = z;
	}
	
	static ZVector V = new ZVector();
	@Override
	public void initGraphics()
	{
		if (UI.get().getViewMode()==UI.ViewMode.MODE3D)
		{
			float x = (mXMin+mXMax)*0.5f;
			mInstance = new ZInstance(Rails.Helper.createMesh(mXMax-mXMin));
			mInstance.setTranslation(x,0,mZ);
			ZActivity.instance.mScene.add(mInstance);
		}
		if (UI.get().getViewMode()==UI.ViewMode.MODE2D)
		{
			float x = (mXMin+mXMax)*0.5f;
			mInstance = new ZInstance(Rails.Helper.createMesh2D(mXMax-mXMin));
			mInstance.setTranslation(x,ReleaseSettings.layer2dRails,mZ);
			ZActivity.instance.mScene.add(mInstance);
		}
	}

	@Override
	public void resetGraphics() 
	{
		if (mInstance!=null)
			ZActivity.instance.mScene.remove(mInstance);
		mInstance = null;
	}

	@Override
	public void updateGraphics(int elapsedTime) 
	{
		// que dalle ici
	}

	@Override
	public boolean getRailsNearestPoint(ZVector inPoint, float inMaxRay, Proximity out) 
	{
		float x = Math.max(mXMin, Math.min(mXMax, inPoint.get(0)));
		if (Math.abs(inPoint.get(0)-x)>inMaxRay) return false;
		out.point.set(x,0,mZ);
		V.sub(inPoint,out.point);
		out.distance=V.norme();
		out.normal.set(0,0,1);
		out.tangent.set(1,0,0);
		out.rails=this;
		return true;
	}

	@Override
	public void addWeight(float x, float w) {}

	@Override
	public ZVector getRailsDirection() { return ZVector.constX; }

}
