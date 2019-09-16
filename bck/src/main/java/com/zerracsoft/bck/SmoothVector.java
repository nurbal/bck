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

import com.zerracsoft.Lib3D.ZVector;

public class SmoothVector 
{
	public SmoothVector(float x, float y, float z, float dampingFactor,float xmin, float xmax,float ymin, float ymax,float zmin, float zmax) 
	{
		mX = new SmoothFloat(x,dampingFactor,xmin,xmax);
		mY = new SmoothFloat(x,dampingFactor,xmin,xmax);
		mZ = new SmoothFloat(x,dampingFactor,xmin,xmax);
	}

	private SmoothFloat mX;
	private SmoothFloat mY;
	private SmoothFloat mZ;
	private ZVector mV = new ZVector();
	
	public void copy(ZVector v)
	{
		mX.set(v.get(0));
		mY.set(v.get(1));
		mZ.set(v.get(2));
	}
	
	public void set(float x, float y, float z)
	{
		mX.set(x);
		mY.set(y);
		mZ.set(z);
	}
	
	public ZVector get()
	{
		mV.set(mX.get(),mY.get(),mZ.get());
		return mV;
	}
	
	public void update(int elapsedTime)
	{
		mX.update(elapsedTime);
		mY.update(elapsedTime);
		mZ.update(elapsedTime);
	}
}
