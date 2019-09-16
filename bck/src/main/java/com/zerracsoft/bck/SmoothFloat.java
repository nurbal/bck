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

public class SmoothFloat 
{
	@SuppressWarnings("unused")
	private SmoothFloat() {}
	
	public SmoothFloat(float value, float dampingFactor,float min, float max) 
	{
		setForced(value); 
		setDampingFactor(dampingFactor);
		setLimits(min,max);
	}

	private float mValue,mTarget;
	
	private float mMin, mMax;
	public void setLimits(float min, float max)
	{
		mMin = min;
		mMax = max;
		if (mTarget>mMax) mTarget = mMax;
		if (mTarget<mMin) mTarget = mMin;
		if (mValue>mMax) mValue = mMax;
		if (mValue<mMin) mValue = mMin;
	}
	
	private float mDampingFactor = 0.1f;
	public void setDampingFactor(float factor)
	{
		mDampingFactor = factor;
	}
	
	public void update(int elapsedTime)
	{
		float dt = (float)elapsedTime * 0.001f;
		float factor = 1.f - (float)Math.exp(-(double)(dt*mDampingFactor));
		mValue += (mTarget-mValue)*factor;
	}
	
	public float get() {return mValue;}
	public void set(float value) 
	{
		mTarget = value;
		if (mTarget>mMax) mTarget = mMax;
		if (mTarget<mMin) mTarget = mMin;
	}
	public void setForced(float value) {mTarget = mValue = value;}
	
}
