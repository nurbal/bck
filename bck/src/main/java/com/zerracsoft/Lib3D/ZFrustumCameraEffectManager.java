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

import java.util.ArrayList;

public class ZFrustumCameraEffectManager
{
	// just add effects to fire them instantly.
	// effects will be deleted automatically when finished
	
	// to use it, replace:
	// camera.setParameters(fov, near, far, pos, front, right, up);
	// by:
	// mgr.setParameters(fov, near, far, pos, front, right, up);
	// mgr.update(dt);
	// mgr.apply(camera);
	
	private ArrayList<ZFrustumCameraEffect> mEffects = new ArrayList<ZFrustumCameraEffect>(16);
	private ArrayList<ZFrustumCameraEffect> mEffectsToDelete = new ArrayList<ZFrustumCameraEffect>(16);
	public void addEffect(ZFrustumCameraEffect fx) {mEffects.add(fx);}
	public void reset() {mEffects.clear();}
	
	// the following vars are to be used ONLY by ZFrustumCameraEffect
	public float mFOV;
	public float mNearClippingPlane;
	public float mFarClippingPlane;
	public ZVector mPosition = new ZVector();
	public ZVector mFront = new ZVector();
	public ZVector mRight = new ZVector();
	public ZVector mUp = new ZVector();
	
	public void setParameters(float fov, float near, float far,ZVector pos,ZVector front,ZVector right,ZVector up)
	{
		mFOV = fov;
		mNearClippingPlane = near;
		mFarClippingPlane = far;
		mPosition.copy(pos);
		mFront.copy(front);
		mRight.copy(right);
		mUp.copy(up);
		
	}
	
	public void update(int elapsedTime)
	{
		for (ZFrustumCameraEffect effect:mEffects)
			if (!effect.update(this,elapsedTime))
				mEffectsToDelete.add(effect);
		for (ZFrustumCameraEffect effect:mEffectsToDelete)
			mEffects.remove(effect);
		mEffectsToDelete.clear();
	}

	public void apply(ZFrustumCamera camera)
	{
		camera.setParameters(mFOV, mNearClippingPlane, mFarClippingPlane, mPosition, mFront, mRight, mUp);
	}
	
}
