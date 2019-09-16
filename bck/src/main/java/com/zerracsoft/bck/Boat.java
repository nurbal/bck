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
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class Boat implements TestVehicle {

	@SuppressWarnings("unused")
	private Boat() {}
	
	public Boat(String meshID,float halfLength, float halfWidth, float halfHeight, float centerHeight, float speed, float x, boolean direction)
	{
		mX = x;
		mHalfLength = halfLength;
		mHalfWidth = halfWidth;
		mHalfHeight = halfHeight;
		mCenterHeight = centerHeight;
		mSpeed = speed;
		mMeshID = meshID;
		mDirection = direction;
	}
	
	protected float mHalfLength;
	protected float mHalfWidth;
	protected float mHalfHeight;
	protected float mCenterHeight;
	protected float mSpeed;
	protected String mMeshID;
	protected boolean mDirection;
	
	
	private float mX;
	private float mY;//=-ReleaseSettings.waterExtrudeHalfWidth+ReleaseSettings.BOAT_HALF_LENGTH;

	private ZInstance mInstance;
	
	@Override
	public void initGraphics() 
	{
		resetGraphics();
		if (UI.get().getViewMode() == UI.ViewMode.MODE2D)
		{
			ZMesh mMesh = new ZMesh();
			mMesh.createRectangle(-mHalfWidth, -mHalfHeight, mHalfWidth, mHalfHeight, 0, 1, 1, 0);
			mInstance = new ZInstance(mMesh);
			mInstance.setColor(new ZColor(1,0,0,0.5f));
			mInstance.setTranslation(mX,ReleaseSettings.layer2dBoat,Level.get().getWaterHeight()+mCenterHeight);
			mInstance.setRotation(ZQuaternion.constX90);
			ZActivity.instance.mScene.add(mInstance);
		}
		if (UI.get().getViewMode() == UI.ViewMode.MODE3D)
		{
			/*
			ZMesh mMesh = new ZMesh();
			mMesh.createBox(new ZVector(mHalfWidth, mHalfLength, mHalfHeight));
			mInstance = new ZInstance(mMesh);
			*/
			mInstance = MeshBank.get().instanciate(mMeshID);
			
			mInstance.setTranslation(mX,mY,Level.get().getWaterHeight()+mCenterHeight);
			mInstance.setVisible(false);
			if (mDirection) mInstance.setRotation(ZQuaternion.constZ180);
			ZActivity.instance.mScene.add(mInstance);
		}
		
	}

	@Override
	public void resetGraphics()
	{
		if (mInstance!=null) ZActivity.instance.mScene.remove(mInstance); mInstance = null;
	}

	@Override
	public void updateGraphics(int elapsedTime) 
	{
		if (UI.get().getViewMode() == UI.ViewMode.MODE3D && mInstance!=null)
		{
			mInstance.setTranslation(mX,mY,Level.get().getWaterHeight()+mCenterHeight);
		}
	}

	@Override
	public void resetSimulation() 
	{
		if (mDirection)
			mY = +ReleaseSettings.waterExtrudeHalfWidth-mHalfLength;
		else
			mY = -ReleaseSettings.waterExtrudeHalfWidth+mHalfLength;
	}

	@Override
	public void updateSimulation(float dt) 
	{
		if (Level.get().getSimulationState()!=Level.SimulationState.RUNNING_BOAT) 
			return;
		if (mDirection)
			mY -= mSpeed * dt;			
		else
			mY += mSpeed * dt;
		// test de collision
		if (Math.abs(mY)<ReleaseSettings.RAILS_HALF_WIDTH+mHalfLength)
			Level.get().boatBoxCollision(mX, Level.get().getWaterHeight()+mCenterHeight, mHalfWidth, mHalfHeight);
	}

	static ZVector v = new ZVector();
	@Override
	public ZVector getPosition() 
	{
		v.set(mX,mY,Level.get().getWaterHeight()+mCenterHeight);
		return v;
	}

	@Override
	public void setVisible(boolean visible) { if (mInstance!=null) mInstance.setVisible(visible); }

	@Override
	public boolean isSuccess()
	{
		return ((!mDirection && (mY >= ReleaseSettings.waterExtrudeHalfWidth-mHalfLength))
				|| (mDirection && (mY <= -ReleaseSettings.waterExtrudeHalfWidth+mHalfLength)));
	}

	@Override
	public ZVector getFrontDirection() {
		if (mDirection) return ZVector.constYNeg;
		return ZVector.constY;
	}

	@Override
	public void setPaused(boolean paused) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isVisible() 
	{ 
		if (mInstance!=null) 
			return mInstance.isVisible();
		return true;
	}

}
