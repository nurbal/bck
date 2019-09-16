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
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public abstract class TrainElement {
	static ZVector v = new ZVector();
	static ZVector T = new ZVector();
	static ZVector N = new ZVector();
	static ZQuaternion q = new ZQuaternion();

	protected ZInstance mInstance;
	protected ZInstance mWheelsSubInstances[] = new ZInstance[4]; // 0 et 1 = avant, 2 et 3 = arriere
	
	public TrainElement mPrevious;
	
	boolean mBroken = false;
	
	public void setBroken(boolean b)
	{
		mBroken=b;
	}
	
	protected boolean mSunk=false;

	public void plouf()
	{
		if (mSunk) return;
		Train.get().setSunk(true);
		mSunk = true;
		GameActivity.instance.playSnd(GameActivity.sndSplash,1);
	}
	
	public abstract void setPaused(boolean paused);

	private static Rails.Proximity railsProximity = new Rails.Proximity();

	class TrainElementWheel
	{
		public ZVector mPosition = new ZVector();
		public ZVector mSpeed = new ZVector();
		ZVector mPrevPosition = new ZVector();
		
		TrainElementWheel mLinkedWheel; // linked wheel, for wagons
		
		Rails mRailContact = null;
			
		public void updateSimulation(float dt)
		{
			// recalcul de la vitesse
			v.sub(mPosition,mPrevPosition);
			mSpeed.mul(v,1/dt);
			
			mPrevPosition.copy(mPosition);
			
			// gravite et vitesse
			if (Level.get().hasWater() && mPosition.get(2)<Level.get().getWaterHeight())
				mSpeed.addMul(ZVector.constZ, -ReleaseSettings.G_WATER*dt);
			else
				mSpeed.addMul(ZVector.constZ, -ReleaseSettings.G*dt);
			
			// motricite:
			if (mRailContact!=null && !mBroken) 
			{
				ZVector direction = mRailContact.getRailsDirection();
				float s = mSpeed.dot(direction);
				mSpeed.addMul(direction,ReleaseSettings.TRAIN_SPEED-s);
			}

			mPosition.addMul(mSpeed, dt); // c'est l� qu'est l'os: si la position a deja ete modifie par la linkedwheel !
			
			// linked wheel
			if (mLinkedWheel!=null)
			{
				D.sub(mLinkedWheel.mPosition,mPosition);
				float d = D.norme();
				if (Math.abs(d)>0.001f)
				{
					// on force la distance � 2*ray
					D.mul(0.5f*(d-2.f*ReleaseSettings.TRAIN_WHEEL_RAY)/d);
					mPosition.add(D);
					mLinkedWheel.mPosition.sub(D);
					
				}				
			}
			
			float x = mPosition.get(0);
			
			// collision avec les rails
			mRailContact = null;
			if (Level.get().getRailsNearestPoint(mPosition, ReleaseSettings.TRAIN_WHEEL_RAY, railsProximity))
				if (railsProximity.distance<ReleaseSettings.TRAIN_WHEEL_RAY)
				{
					// contact avec le rail & application du poids du train
					mRailContact = railsProximity.rails;
					mRailContact.addWeight(x, ReleaseSettings.TRAIN_WEIGHT/2);
					// correction de la penetration
					mPosition.addMul(railsProximity.normal,(ReleaseSettings.TRAIN_WHEEL_RAY-railsProximity.distance));
					// annulation de la vitesse normale
					zeroNormalSpeed(railsProximity.normal);
				}
			
			// collision avec le sol
			//if (mRailContact==null)
			{
				float dist = Level.get().getFloorNearestPoint(mPosition, ReleaseSettings.TRAIN_WHEEL_RAY, v, N, T);
				if (dist<ReleaseSettings.TRAIN_WHEEL_RAY)
				{
					// train casse
					if (mRailContact==null)
						Train.get().setBroken(true);
					// correction de la penetration
					mPosition.addMul(N,ReleaseSettings.TRAIN_WHEEL_RAY-dist);
					// r�duction de la vitesse tangentielle
					if (mRailContact==null)
					{
						float frictionCoef = 1.f-(float)Math.exp(-dt*ReleaseSettings.FRICTION_FACTOR);
						frictionCoef *= Math.abs(N.get(2));
						D.sub(mPosition,mPrevPosition);
						mPosition.addMul(T,-T.dot(D)*frictionCoef);
					}
					// annulation de la vitesse normale
					zeroNormalSpeed(N);
					
				}			
			}
			
			
			// speed cap (in water)
			if (Level.get().hasWater() && mPosition.get(2)<Level.get().getWaterHeight())
			{
				D.sub(mPosition, mPrevPosition);
				float d = D.norme();
				if (d>ReleaseSettings.waterMaxSpeedTrain*dt)
					mPrevPosition.mulAddMul(mPosition,1,D, -ReleaseSettings.waterMaxSpeedTrain*dt/d);
				
				plouf();
			}
			
			
		}
		
		void collide(TrainElementWheel other)
		{
			D.sub(other.mPosition,mPosition);
			float d = D.norme();
			if (d>0.001f && d<ReleaseSettings.TRAIN_WHEEL_RAY*2.f)
			{
				// on force la distance a 2*ray
				D.mul(0.5f*(d-2.f*ReleaseSettings.TRAIN_WHEEL_RAY)/d);
				mPosition.add(D);
				other.mPosition.sub(D);
			}				
		}
		
		// set normal speed to 0
		private void zeroNormalSpeed(ZVector normal)
		{
			v.sub(mPosition,mPrevPosition);
			float vn = normal.dot(v);
			mPrevPosition.addMul(normal,vn);
		}
		
	}
	
	TrainElementWheel [] mWheels;
	public TrainElement()
	{
		mWheels = new TrainElementWheel[2];
		mWheels[0] = new TrainElementWheel(); 	
		mWheels[1] = new TrainElementWheel(); 	
		setPosition(0);
	}
	
	public void collide(TrainElement other)
	{
		if (other==this) return;
		mWheels[0].collide(other.mWheels[0]);
		mWheels[0].collide(other.mWheels[1]);
		mWheels[1].collide(other.mWheels[0]);
		mWheels[1].collide(other.mWheels[1]);
	}
	
	public ZVector getPosition()
	{
		D.mulAddMul(mWheels[0].mPosition, 0.5f, mWheels[1].mPosition, 0.5f);
		return D;
	}
	public void setPosition(float x)
	{
		float railsHeight = 0;
		if (Level.get()!=null)
			railsHeight=Level.get().getDefaultRailsHeight();
		mWheels[0].mPosition.set(x+ReleaseSettings.TRAIN_WHEEL_HALF_DISTANCE,0,railsHeight+ReleaseSettings.TRAIN_WHEEL_RAY);
		mWheels[1].mPosition.set(x-ReleaseSettings.TRAIN_WHEEL_HALF_DISTANCE,0,railsHeight+ReleaseSettings.TRAIN_WHEEL_RAY);
	}
	
	private ZVector direction = new ZVector();
	public ZVector getDirection()
	{
		direction.sub(mWheels[0].mPosition, mWheels[1].mPosition);
		direction.normalize();
		return direction;
	}
	
	static ZVector D = new ZVector();
	public void updateSimulation(float dt)
	{
		// element pr�c�dent
		if (mPrevious!=null)
		{
			// doit-on se detacher ?
			if (getDirection().dot(mPrevious.getDirection())<0.8f)
			{
				Train.get().detachElements(this, mPrevious);
				mWheels[0].mLinkedWheel = null;
			}
			else
				mWheels[0].mLinkedWheel = mPrevious.mWheels[1];
		}
		else
			mWheels[0].mLinkedWheel = null;
		
		// update independant des roues
		for (TrainElementWheel w:mWheels)
			w.updateSimulation(dt);
		
		// update des MESHS de roues
		if (mWheels[0].mRailContact!=null)
			for (int i=0; i<2; i++)
				if (mWheelsSubInstances[i]!=null) mWheelsSubInstances[i].rotate(ZVector.constY,mWheels[0].mSpeed.norme()*dt/0.25f);
		if (mWheels[1].mRailContact!=null)
			for (int i=2; i<4; i++)
				if (mWheelsSubInstances[i]!=null) mWheelsSubInstances[i].rotate(ZVector.constY,mWheels[1].mSpeed.norme()*dt/0.25f);
		
		// recalcul de la distance des roues
		D.sub(mWheels[0].mPosition,mWheels[1].mPosition); // 1->0, vecteur oriente vers l'avant
		float d = D.norme();
		D.mul(1/d);
		mWheels[0].mPosition.addMul(D,ReleaseSettings.TRAIN_WHEEL_HALF_DISTANCE-d/2);
		mWheels[1].mPosition.addMul(D,d/2-ReleaseSettings.TRAIN_WHEEL_HALF_DISTANCE);
		
	}

	public void initGraphics()
	{
		if (UI.get().getViewMode() != UI.ViewMode.MODE3D)
			return;
	}
	
	public void resetSimulation() 
	{
		mSunk=false;
		for (TrainElementWheel w:mWheels)
		{
			w.mSpeed.set(ReleaseSettings.TRAIN_SPEED,0,0);
			w.mPosition.set(2,Level.get().getDefaultRailsHeight()+ReleaseSettings.TRAIN_WHEEL_RAY);
			w.mPrevPosition.copy(w.mPosition);
		}
		for (ZInstance i:mWheelsSubInstances)
			if (i!=null)
				i.rotate(ZVector.constY,10.f*(float)Math.random());
	}

	public void updateGraphics(int elapsedTime)
	{
		if (UI.get().getViewMode() != UI.ViewMode.MODE3D)
			return;
		
		if (mInstance==null) 
			return;

		// calcul de v et q, position et rotation correspondant aux roues
		D.sub(mWheels[0].mPosition,mWheels[1].mPosition); // 1->0, vecteur oriente vers l'avant
		D.normalize();
		float cs = D.get(0);
		float sn = D.get(2);
		float angle;
		if (cs>=1) angle=0;
		else if (cs<=-1) angle=(float)Math.PI;
		else
		{
			angle = (float)Math.acos(cs);
			if (sn<0) angle = -angle;
		}
		q.set(ZVector.constY, -angle); 
		 
		v.mulAddMul(mWheels[0].mPosition,0.5f,mWheels[1].mPosition,0.5f);
		
		
		mInstance.setTranslation(v);	
		mInstance.setRotation(q);
	}

	public void resetGraphics()
	{
		// destroy graphics 
		ZActivity.instance.mScene.remove(mInstance);
		mInstance = null;
	}
	
	public void setVisible(boolean visible) 
	{
		if (mInstance!=null)  
			mInstance.setVisible(visible);
	}
	
	public boolean isVisible()
	{
		if (mInstance==null) return false;
		return mInstance.isVisible();
	}
	
}
