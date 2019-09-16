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

package com.zerracsoft.Lib3D;

public class ZFrustumCameraEffectShake implements ZFrustumCameraEffect {

	private int mLength;
	private int mTimer = 0;
	private double mPeriodX;
	private double mPeriodY;
	private double mPeriodZ;
	private float mAmplitudeX;
	private float mAmplitudeY;
	private float mAmplitudeZ;
	
	private double mAngleX = 0;
	private double mAngleY = 0;
	private double mAngleZ = 0;
	private ZVector mAmplitude =  new ZVector();
	
	public ZFrustumCameraEffectShake(int length,float periodX,float periodY, float periodZ,float amplitudeX,float amplitudeY, float amplitudeZ)
	{
		mLength = length;
		mPeriodX = periodX;
		mPeriodY = periodY;
		mPeriodZ = periodZ;
		mAmplitudeX = amplitudeX;
		mAmplitudeY = amplitudeY;
		mAmplitudeZ = amplitudeZ;
		mAngleX = Math.random()*Math.PI*2.0;
		mAngleY = Math.random()*Math.PI*2.0;
		mAngleZ = Math.random()*Math.PI*2.0;
	}
	
	public boolean update(ZFrustumCameraEffectManager mgr,int timeIncrement)
	{
		mTimer += timeIncrement;
		if (mTimer>mLength)
			return false;
		double dt = 0.001*(double)timeIncrement;
		mAngleX += dt*mPeriodX; if (mAngleX>2.0*Math.PI) mAngleX -= 2.0*Math.PI;
		mAngleY += dt*mPeriodY; if (mAngleY>2.0*Math.PI) mAngleY -= 2.0*Math.PI;
		mAngleZ += dt*mPeriodZ; if (mAngleZ>2.0*Math.PI) mAngleZ -= 2.0*Math.PI;
		float coef = 1.f - (float)mTimer/(float)mLength;		
		mAmplitude.set(coef*mAmplitudeX*(float)Math.sin(mAngleX), coef*mAmplitudeY*(float)Math.sin(mAngleY), coef*mAmplitudeZ*(float)Math.sin(mAngleZ));
		mgr.mPosition.add(mAmplitude);
		return true;
	}

}
