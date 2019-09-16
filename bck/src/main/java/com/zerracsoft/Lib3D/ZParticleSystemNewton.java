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

// a VERY generic particle system, with initial velocity, random range, gravity, 
// lifelength and alha decreasing until death

public class ZParticleSystemNewton extends ZParticleSystem 
{
	// native NDK functions
	protected native static long JNIconstructor();
	//protected native static void JNIdestructor(long obj);
	
	protected native static void JNIsetParameters(long obj, long emmiterrandomX, long emmiterrandomY, long emmiterrandomZ, long emmiterrandomCoefs, long speed, long speedrandom, long gravity, long gravityrandom, int lifetime, int lifetimerandom, float size, float sizerandom, float sizefactorbirth, float sizefactordeath, boolean oneshot);
	protected native static void JNIfire(long obj);	// launch one-shot particles
	
	public ZParticleSystemNewton()			{ mNativeObject = JNIconstructor(); }   

	//public void fire() { JNIfire(mNativeObject); } 
	
	public void setParameters(ZVector emmiterrandom, ZVector speed, ZVector speedrandom, ZVector gravity, ZVector gravityrandom, int lifetime, int lifetimerandom, float size, float sizerandom, float sizefactorbirth, float sizefactordeath, boolean oneshot)
	{
		JNIsetParameters(mNativeObject, ZVector.constX.getNativeObject(), ZVector.constY.getNativeObject(), ZVector.constZ.getNativeObject(), emmiterrandom.getNativeObject(), speed.getNativeObject(), speedrandom.getNativeObject(), gravity.getNativeObject(), gravityrandom.getNativeObject(), lifetime, lifetimerandom, size, sizerandom, sizefactorbirth, sizefactordeath, oneshot);		
	}
	public void setParameters(ZVector emmiterrandomX,ZVector emmiterrandomY,ZVector emmiterrandomZ,ZVector emmiterrandomCoefs, ZVector speed, ZVector speedrandom, ZVector gravity, ZVector gravityrandom, int lifetime, int lifetimerandom, float size, float sizerandom, float sizefactorbirth, float sizefactordeath, boolean oneshot)
	{
		JNIsetParameters(mNativeObject, emmiterrandomX.getNativeObject(), emmiterrandomY.getNativeObject(), emmiterrandomZ.getNativeObject(), emmiterrandomCoefs.getNativeObject(), speed.getNativeObject(), speedrandom.getNativeObject(), gravity.getNativeObject(), gravityrandom.getNativeObject(), lifetime, lifetimerandom, size, sizerandom, sizefactorbirth, sizefactordeath, oneshot);		
	}
}
