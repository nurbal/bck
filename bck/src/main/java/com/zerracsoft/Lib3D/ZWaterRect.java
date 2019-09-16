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

// objet plan, avec les UVs anim�s de mani�re � faire un "effet d'eau"...
// cr�e un rectangle oriente en Z (comme avec ZMesh.CreateRect)

public class ZWaterRect extends ZMesh {
	
	// native NDK functions
	protected native static long JNIconstructor(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int resolX, int resolY, float UVamplitude, float freq, boolean norms);

	// NDK destructor handled by ZMesh...
	
	public ZWaterRect(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int resolX, int resolY, float UVamplitude, float freq, boolean norms)
	{
		mNativeObject = JNIconstructor(x1,y1,x2,y2,u1,v1,u2,v2,resolX,resolY,UVamplitude,freq,norms);
	}
	
}
