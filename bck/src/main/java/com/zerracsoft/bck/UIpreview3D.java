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

import java.util.ArrayList;


import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZFrustumCamera;
import com.zerracsoft.Lib3D.ZFrustumCameraEffectManager;
import com.zerracsoft.Lib3D.ZFrustumCameraEffectShake;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;

public class UIpreview3D extends UI {

	ZFrustumCamera mFrustumCamera = new ZFrustumCamera();
	public ZFrustumCameraEffectManager mCamEffects = new ZFrustumCameraEffectManager();
	
	
	public interface CamController
	{
		public void update(int elapsedTime);
		public void onTouchMoveStart(float xStart, float yStart);
		public void onTouchMove(float xStart, float yStart, float x, float y);
		public void onTouchPinchStart(float xStart, float yStart);
		public void onTouchPinch(float xStart, float yStart, float x, float y, float factor);
		public void onTouchEnd();
		public void zoomIn();
		public void zoomOut();
	}
	
	static ZVector camFrontHori = new ZVector();
	public class CamEmbedded implements CamController
	{
		SmoothFloat camFOV = new SmoothFloat(0.5f*(float)Math.PI,25,0.05f,0.5f*(float)Math.PI);
		SmoothFloat camPosHori = new SmoothFloat(0,10,-(float)Math.PI,(float)Math.PI);
		SmoothFloat camPosVert = new SmoothFloat(0,10,-0.25f*(float)Math.PI,0.25f*(float)Math.PI);
		// syst�me de d�brayage avec retour au target 1s apr�s le lever de doigt de l'�cran
		private int freeViewTimer = 0;	// -1=free view, 0=center of interest, sinon countdown
		float camMoveStartPosHori = 0;
		float camMoveStartPosVert = 0;
		
		
		public void update(int elapsedTime)
		{
			if (freeViewTimer>0)
			{
				freeViewTimer-=elapsedTime;
				if (freeViewTimer<0) freeViewTimer=0;
			}
		
			// forced target ?
			if (mSimulationOn && freeViewTimer==0)
			{
				camPosHori.set(0);
				camPosVert.set(0);
			}
		
			TestVehicle vehicle = Level.get().getCurrentVehicle();
			ZVector vehiclepos = vehicle.getPosition();
			
			
			if (vehicle.getClass() == Train.class)
			{
				//camPosVert.setForced(0.2f);
				//camPosHori.setForced(1.f);
				
				camFrontHori.copy(vehicle.getFrontDirection());
				camFrontHori.set(2, 0); camFrontHori.normalize(); // vecteur horizontal
				// tourner mCamFront en fonction des variations angulaires....
				float cs = (float)Math.cos(camPosHori.get());
				float sn = (float)Math.sin(camPosHori.get());
				float x = camFrontHori.get(0);
				float y = camFrontHori.get(1);
				camFrontHori.set(0, x*cs + y*sn);
				camFrontHori.set(1, y*cs - x*sn);
				mCamFront.copy(camFrontHori);
				mCamFront.set(2,(float)Math.tan(camPosVert.get())); mCamFront.normalize();
				// calcul des autres vecteurs de la base
				mCamRight.cross(mCamFront,ZVector.constZ); mCamRight.normalize(); 
				mCamUp.cross(mCamRight,mCamFront);
				// position de la cam�ra
				mCamPos.set(vehiclepos.get(0), vehiclepos.get(1), Level.get().getDefaultRailsHeight()+2);
				mCamPos.addMul(camFrontHori, -4);
				
			}
			else// if (vehicle.getClass() == Boat.class)
			{
				camFrontHori.copy(vehicle.getFrontDirection());
				//camFrontHori.sub(Level.get().mCenterOfInterest.get(),vehicle.getPosition());
				camFrontHori.set(2, 0); camFrontHori.normalize(); // vecteur horizontal
				// tourner mCamFront en fonction des variations angulaires....
				float cs = (float)Math.cos(camPosHori.get());
				float sn = (float)Math.sin(camPosHori.get());
				float x = camFrontHori.get(0);
				float y = camFrontHori.get(1);
				camFrontHori.set(0, x*cs + y*sn);
				camFrontHori.set(1, y*cs - x*sn);
				mCamFront.copy(camFrontHori);
				mCamFront.set(2,(float)Math.tan(camPosVert.get())); mCamFront.normalize();
				// calcul des autres vecteurs de la base
				mCamRight.cross(mCamFront,ZVector.constZ); mCamRight.normalize(); 
				mCamUp.cross(mCamRight,mCamFront);
				// position de la cam�ra
				mCamPos.set(vehiclepos.get(0), vehiclepos.get(1), Level.get().getDefaultRailsHeight()+2);
				mCamPos.addMul(camFrontHori, -2);
			}
			
			mCamEffects.setParameters(camFOV.get(), 0.1f, 1000, mCamPos, mCamFront, mCamRight, mCamUp);	
			
			// update various timers 
			camFOV.update(elapsedTime);
			camPosHori.update(elapsedTime);
			camPosVert.update(elapsedTime);

		}

		public void onTouchMoveStart(float xStart, float yStart) 
		{
			camMoveStartPosHori = camPosHori.get();
			camMoveStartPosVert = camPosVert.get();
			freeViewTimer = -1;
		}

		public void onTouchMove(float xStart, float yStart, float x, float y) {
			float deltaX = ZRenderer.screenToViewportXF(x)-ZRenderer.screenToViewportXF(xStart);
			float deltaY = ZRenderer.screenToViewportYF(y)-ZRenderer.screenToViewportYF(yStart);
			camPosHori.set(camMoveStartPosHori+deltaX*camFOV.get()*0.5f/ZRenderer.getScreenRatioF());
			camPosVert.set(camMoveStartPosVert+deltaY*camFOV.get()*0.5f/ZRenderer.getScreenRatioF());
		}
	
		public void onTouchEnd()
		{
			freeViewTimer=1000;
		}
		
		public void onTouchPinchStart(float xStart, float yStart) {}
		public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) {}
		@Override
		public void zoomIn() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void zoomOut() {
			// TODO Auto-generated method stub
			
		}

	}
	public class CamBridge implements CamController
	{
		SmoothFloat camFOV = new SmoothFloat(0.5f*(float)Math.PI,25,0.05f,0.5f*(float)Math.PI);
		SmoothFloat camTarget = new SmoothFloat(0,50,-0.5f*(float)Math.PI,0.5f*(float)Math.PI);
		SmoothFloat camPosLatitude = new SmoothFloat(0.1f,50,0,0.25f*(float)Math.PI);
		float camDistance;
		float camFOVPinchStart = 1;	// zoom factor at the beginning of the pinch...
		float camMoveStartTarget = 0;
		float camMoveStartLat = 0;
		
		public CamBridge()
		{
			camTarget.setForced(Level.get().getXCenter());
			camDistance = Math.max(Level.get().getXSize()*0.5f,8+ReleaseSettings.floorExtrudeHalfWidth);
			camFOV.setForced(2.0f*(float)Math.atan(Level.get().getXSize()*0.5f/100)); 
			
		}
		
		// syst�me de d�brayage avec retour au target 1s apr�s le lever de doigt de l'�cran
		private int freeViewTimer = 0;	// -1=free view, 0=center of interest, sinon countdown
		
		public void update(int elapsedTime)
		{
			if (freeViewTimer>0)
			{
				freeViewTimer-=elapsedTime;
				if (freeViewTimer<0) freeViewTimer=0;
			}
			
			// forced target ?
			if (mSimulationOn && freeViewTimer==0)
				camTarget.set(Level.get().mCenterOfInterest.get().get(0));
			
			// limites de la cible en x
			float xExtents = Level.get().getXSize() * (1.f-(float)Math.tan(0.5f*camFOV.get())/(float)Math.tan(0.25f*(float)Math.PI));
			camTarget.setLimits(Level.get().getXCenter()-xExtents*0.5f, Level.get().getXCenter()+xExtents*0.5f);

			// calcul de la cam�ra
			
			// la pi�ce fait 5*50 = 125 de rayon
			camDistance = 100; // hardcod�, bouhouhou
			
			float cosLat = (float)Math.cos(camPosLatitude.get());
			float sinLat = (float)Math.sin(camPosLatitude.get());
			
			mCamPos.set(Level.get().getXCenter(), -camDistance*cosLat, Level.get().getDefaultRailsHeight()+camDistance*sinLat);
			mCamFront.mul(mCamPos,-1); mCamFront.add(camTarget.get(),0,Level.get().getDefaultRailsHeight()); mCamFront.normalize();
			mCamRight.cross(mCamFront, ZVector.constZ); mCamRight.normalize();
			mCamUp.cross(mCamRight,mCamFront);
			mCamEffects.setParameters(camFOV.get(), 5, 1000, mCamPos, mCamFront, mCamRight, mCamUp);		

			// update various timers 
			camFOV.update(elapsedTime);
			camTarget.update(elapsedTime);
			camPosLatitude.update(elapsedTime);
		}

		public void onTouchMoveStart(float xStart, float yStart) 
		{
			camMoveStartTarget = camTarget.get();
			camMoveStartLat = camPosLatitude.get();
			freeViewTimer = -1;
		}

		public void onTouchMove(float xStart, float yStart, float x, float y) {
			float deltaX = ZRenderer.screenToViewportXF(x)-ZRenderer.screenToViewportXF(xStart);
			float coefX = camDistance*(float)Math.tan(0.5f*camFOV.get())/ZRenderer.getScreenRatioF();
			float deltaY = ZRenderer.screenToViewportYF(y)-ZRenderer.screenToViewportYF(yStart);
			camTarget.set(camMoveStartTarget-deltaX*coefX);
			camPosLatitude.set(camMoveStartLat-deltaY*0.5f);
		}

		public void onTouchPinchStart(float xStart, float yStart) 
		{ 
			camFOVPinchStart = camFOV.get(); 
			camMoveStartTarget = camTarget.get();
			camMoveStartLat = camPosLatitude.get();
			freeViewTimer=-1;
		}

		public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) 
		{ 
			// on set le zoomfactor
			camFOV.set(camFOVPinchStart/factor); 
			
			// on modifie aussi la position de la cam pour que le centre du pich soit toujours le m�me point du monde
			float xStartViewport = ZRenderer.screenToViewportXF(xStart);
			float yStartViewport = ZRenderer.screenToViewportYF(yStart);
			float coefXStart = camDistance*(float)Math.tan(0.5f*camFOVPinchStart)/ZRenderer.getScreenRatioF();
			float xViewport = ZRenderer.screenToViewportXF(x);
			float yViewport = ZRenderer.screenToViewportYF(y);
			float coefX = camDistance*(float)Math.tan(0.5f*camFOV.get())/ZRenderer.getScreenRatioF();
			// calcul de la position de ce fameux point....
			float xWorld = xStartViewport*coefXStart+camMoveStartTarget;
			float zWorld = yStartViewport*0.5f+camMoveStartLat;
			
			camTarget.set(xWorld-xViewport*coefX);
			camPosLatitude.set(zWorld-yViewport*0.5f);
		}
		
		public void onTouchEnd()
		{
			freeViewTimer=1000;
		}
		
		

		public void zoomIn() { camFOV.set(camFOV.get()*0.5f); }
		public void zoomOut() { camFOV.set(camFOV.get()*2.f); }

	}
	
	public class CamTower implements CamController
	{
		SmoothFloat camFOV = new SmoothFloat(0.5f*(float)Math.PI,25,0.05f,0.5f*(float)Math.PI);
		SmoothFloat camTarget = new SmoothFloat(0,50,-0.5f*(float)Math.PI,0.5f*(float)Math.PI);
		float camDistance;
		float camFOVPinchStart = 1;	// zoom factor at the beginning of the pinch...
		float camMoveStartTarget = 0;
		float camMoveStartLat = 0;
		
		public CamTower()
		{
			camTarget.setForced(Level.get().getDefaultRailsHeight()*0.5f+Level.get().getTowerHeight()*0.5f);
			camTarget.setLimits(Level.get().getDefaultRailsHeight(), Level.get().getTowerHeight());
		//	camDistance = Math.max(Level.get().getZSize()*0.5f,8+ReleaseSettings.floorExtrudeHalfWidth);
			float maxFOV = Math.max(4.0f*ZRenderer.getScreenRatioF()*(float)Math.atan(Level.get().getZSize()*0.5f/100), 1);
			camFOV.setLimits(0.05f,maxFOV); 
			camFOV.setForced(maxFOV*0.75f); 
			
		}
		
		public void update(int elapsedTime)
		{
			// calcul de la cam�ra
			
			// la pi�ce fait 5*50 = 125 de rayon
			camDistance = 100; // hardcod�, bouhouhou
			
			
			float cosLat = (float)Math.cos(0.15f*Math.PI);
			float sinLat = (float)Math.sin(0.15f*Math.PI);
			
			mCamPos.set(Level.get().getXCenter(), -camDistance*cosLat, Level.get().getDefaultRailsHeight()+camDistance*sinLat);
			mCamFront.mul(mCamPos,-1); mCamFront.add(Level.get().getXCenter(),0,camTarget.get()); mCamFront.normalize();
			mCamRight.cross(mCamFront, ZVector.constZ); mCamRight.normalize();
			mCamUp.cross(mCamRight,mCamFront);
			mCamEffects.setParameters(camFOV.get(), 5, 1000, mCamPos, mCamFront, mCamRight, mCamUp);		

			// update various timers 
			camFOV.update(elapsedTime);
			camTarget.update(elapsedTime);
		}

		public void onTouchMoveStart(float xStart, float yStart) 
		{
			camMoveStartTarget = camTarget.get(); 
		}

		public void onTouchMove(float xStart, float yStart, float x, float y) {
			float deltaY = ZRenderer.screenToViewportYF(y)-ZRenderer.screenToViewportYF(yStart);
			float coefY = camDistance*(float)Math.tan(0.5f*camFOV.get())/ZRenderer.getScreenRatioF();
			camTarget.set(camMoveStartTarget-deltaY*coefY);
		}

		public void onTouchPinchStart(float xStart, float yStart) 
		{ 
			camFOVPinchStart = camFOV.get(); 
			camMoveStartTarget = camTarget.get();
		}

		public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) 
		{ 
			// on set le zoomfactor
			camFOV.set(camFOVPinchStart/factor); 
			
			// on modifie aussi la position de la cam pour que le centre du pich soit toujours le m�me point du monde
			float yStartViewport = ZRenderer.screenToViewportYF(yStart);
			float coefYStart = camDistance*(float)Math.tan(0.5f*camFOVPinchStart)/ZRenderer.getScreenRatioF();
			float yViewport = ZRenderer.screenToViewportYF(y);
			float coefY = camDistance*(float)Math.tan(0.5f*camFOV.get())/ZRenderer.getScreenRatioF();
			// calcul de la position de ce fameux point....
			float zWorld = yStartViewport*coefYStart+camMoveStartLat;
			
			camTarget.set(zWorld-yViewport*coefY);
		}
		
		public void onTouchEnd()
		{
		}
		
		

		public void zoomIn() { camFOV.set(camFOV.get()*0.5f); }
		public void zoomOut() { camFOV.set(camFOV.get()*2.f); }

	}
	
	public CamBridge	mCamBridge 		= new CamBridge();
	public CamTower 	mCamTower 		= new CamTower();
	public CamEmbedded 	mCamEmbedded	= new CamEmbedded();
	
	public CamController mCamController;
	
	public void switchCamera()
	{
		if (mCamController==mCamBridge || mCamController==mCamTower) mCamController = mCamEmbedded;
		else if (mCamController == mCamEmbedded) mCamController = Level.get().isTowerMode()?mCamTower:mCamBridge;
	}
	
	public boolean mShowConstraints = false;

	private ZInstance[] mTowerCountDownInstances = new ZInstance[(ReleaseSettings.TOWER_COUNTDOWN_LENGTH+999)/1000];
	private ZInstance mWindGaugeInstance;
	private ZInstance mWindValueInstance;
	private ZMesh mWindValueMesh;
	private float mWindGaugeAnimPeriod;
	private ZInstance[] mStressedLinksInstances = new ZInstance[ReleaseSettings.STRESS_NB_LINKS];
	private static ZVector v = new ZVector();
	private static ZQuaternion q = new ZQuaternion();
	private void updateWindGauge(int elapsedTime)
	{
		mWindGaugeAnimPeriod = (mWindGaugeAnimPeriod+elapsedTime)%ReleaseSettings.WINDGAUGE_ANIM_PERIOD;
		float gaugeAnimPeriod = (float)mWindGaugeAnimPeriod / (float)ReleaseSettings.WINDGAUGE_ANIM_PERIOD;
		
		float x = Level.get().getWind();
		float u1 = -gaugeAnimPeriod;
		float u2 = u1+Math.abs(x*8.f);
		
		mWindValueMesh.resetMesh();
		v.set(0,-0.0625f,0);			mWindValueMesh.addVertex(v,null, u1, 1);
		v.set(x,-0.0625f,0);			mWindValueMesh.addVertex(v,null, u2, 1);
		v.set(0,0.0625f,0);			mWindValueMesh.addVertex(v,null, u1, 0);
		v.set(x,0.0625f,0);			mWindValueMesh.addVertex(v,null, u2, 0);
		if (x>0)
		{
			mWindValueMesh.addFace(0, 1, 3);
			mWindValueMesh.addFace(0, 3, 2);
		}
		else
		{
			mWindValueMesh.addFace(1, 0, 2);
			mWindValueMesh.addFace(1, 2, 3);
		}
		
	}
	
	@Override
	public void init() 
	{
		mSimulationOn = true;

		// cr�ation du menu d'�dition
		ZActivity.instance.mScene.setMenu(new MenuButtonsPreview(this,true));
				
		Level.get().resetSimulation();
		Level.get().initGraphics();
		
		mCamController = Level.get().isTowerMode()?mCamTower:mCamBridge;
		
		for (int i=0; i<mTowerCountDownInstances.length; i++)
		{
			mTowerCountDownInstances[i] = new ZInstance(GameActivity.mFontBig.createStringMesh(Integer.toString(i+1), ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
			mTowerCountDownInstances[i].setVisible(false);
			ZActivity.instance.mScene.add2D(mTowerCountDownInstances[i]); 
		}

		// instances transparentes de stress (des boites blanches rendues en alpha vert>rouge)
		col.set(1,0,0,0);
		ZMesh boxMesh = new ZMesh();
		boxMesh.createBox(new ZVector(0.5f,1.1f,0.25f)); // yeah right, all instances have the very same geometry. FUCK YEAH!
		for (int i=0; i<mStressedLinksInstances.length; i++)
		{
			mStressedLinksInstances[i] = new ZInstance(boxMesh);
			mStressedLinksInstances[i].setVisible(false);
			mStressedLinksInstances[i].setColor(col); // rouge transparent! yeah....
			
			ZActivity.instance.mScene.add(mStressedLinksInstances[i]); 
		}
		
		
		if (Level.get().isTowerMode())
		{
			// jauge de vent
			ZMesh windGauge = new ZMesh();
			windGauge.createRectangle(-1, -0.0625f, 1, 0.0625f, 0, 1, 1, 0);
			windGauge.setMaterial("wind_gauge");
			mWindGaugeInstance = new ZInstance(windGauge);
			mWindValueMesh = new ZMesh();
			mWindValueMesh.allocMesh(4, 2, false);
			mWindValueMesh.setMaterial("wind_value");
			mWindValueInstance = new ZInstance(mWindValueMesh);
			
			mWindGaugeInstance.setTranslation(0,0,0);		
			mWindValueInstance.setTranslation(0,0,-1);	
			
			mWindGaugeInstance.setVisible(false);
			mWindValueInstance.setVisible(false);
			
			ZActivity.instance.mScene.add2D(mWindGaugeInstance);
			ZActivity.instance.mScene.add2D(mWindValueInstance);
		}

		ReleaseSettings.suspendRendering(false);

	}

	protected ZVector mCamFront = new ZVector();
	protected ZVector mCamFrontHori = new ZVector();	// vecteur horizontal dirig vers l'avant de la cam�ra. (interm�diaire)
	protected ZVector mCamRight = new ZVector();
	protected ZVector mCamUp = new ZVector();
	protected ZVector mCamPos = new ZVector();

	static ZColor col = new ZColor();
	private int mColorTimer = 0;
	@Override
	public void update(int elapsedTime) 
	{
		super.update(elapsedTime);
		
		mColorTimer += elapsedTime;
		
		if (Level.get().isTowerMode())
		{
			if (Level.get().getSimulationState() == Level.SimulationState.TOWER_WINDTEST)
			{
				mWindGaugeInstance.setVisible(true);
				mWindValueInstance.setVisible(true);
				updateWindGauge(elapsedTime);
			}
			else
			{
				mWindGaugeInstance.setVisible(false);
				mWindValueInstance.setVisible(false);			
			}
			
			if (Level.get().getSimulationState() == Level.SimulationState.TOWER_COUNTDOWN)
			{
				int timer = ReleaseSettings.TOWER_COUNTDOWN_LENGTH - Level.get().getSimulationStateTimer();
				int activeNumber = (timer-1)/1000;
				if (activeNumber<0) activeNumber=0;
				if (activeNumber>mTowerCountDownInstances.length) activeNumber=mTowerCountDownInstances.length;
				float alpha = 0.001f*(float)(timer - activeNumber*1000);
				for (int i=0; i<mTowerCountDownInstances.length; i++)
				{
					if (i==activeNumber)
					{
						mTowerCountDownInstances[i].setVisible(true);
						float s = 4.f-alpha*3.f;
						mTowerCountDownInstances[i].setScale(s,s,s);
						col.set(1,1,1,alpha);
						mTowerCountDownInstances[i].setColor(col);
					}
					else
						mTowerCountDownInstances[i].setVisible(false);
				}
			}
			else
			{
				for (int i=0; i<mTowerCountDownInstances.length; i++)
					mTowerCountDownInstances[i].setVisible(false);
			}
		}
	
		mCamController.update(elapsedTime);

		// stress indicator
		for (ZInstance inst:mStressedLinksInstances) inst.setVisible(false);
		if (mShowConstraints)
		{
			ArrayList<Link> stressedLinks = Level.get().getMostStressedLink(ReleaseSettings.STRESS_NB_LINKS);
			for (int i=0; i<stressedLinks.size(); i++)
			{
				Link link = stressedLinks.get(i);
				ZInstance inst = mStressedLinksInstances[i];
				inst.setScale(link.getLength(),1,1);
				inst.setTranslation(link.getPosition());
				q.set(ZVector.constYNeg, link.getAngle());
				inst.setRotation(q);
				float stressAlpha = Math.max(0,2.f*(link.getStressFactor()-0.5f));
				if (stressAlpha<1 )
					col.set(1,0,0,stressAlpha);
				else
				{
					if ((mColorTimer&0x80)==0)
						col.set(1,0,0,1.f);
					else
						col.set(0,1,0,1.f);
				}
				inst.setColor(col);
				inst.setVisible(true);
			}
		}

		// camera effects
		mCamEffects.update(elapsedTime);
		mCamEffects.apply(mFrustumCamera);
		
		ZRenderer.setCamera(mFrustumCamera);  
		

	}


	
	@Override
	public void onTouchMoveStart(float xStart, float yStart) 
	{
		mCamController.onTouchMoveStart(xStart, yStart); 
	}

	@Override
	public void onTouchMove(float xStart, float yStart, float x, float y) 
	{
		mCamController.onTouchMove(xStart, yStart, x, y); 
	}

	@Override
	public void onTouchMoveEnd() {
		mCamController.onTouchEnd(); 
		
	}

	@Override
	public void onTouchPinchStart(float xStart, float yStart) 
	{ 
		mCamController.onTouchPinchStart(xStart, yStart); 
	} 

	@Override
	public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) 
	{ 
		mCamController.onTouchPinch(xStart, yStart, x, y, factor); 
	}

	@Override
	public void onTouchPinchEnd() {
		mCamController.onTouchEnd(); 

	}

	@Override
	public ViewMode getViewMode() { return ViewMode.MODE3D;	}

	
	private boolean mSimulationOn  = false;
	public void setPaused(boolean paused) 
	{ 
		mSimulationOn = !paused;
		Level.get().setPaused(paused);
	}
	
	@Override
	public boolean isSimulationOn() { return mSimulationOn; }

	@Override
	public boolean isEditMode() { return false; }

	@Override
	public void close() {
		ReleaseSettings.suspendRendering(true);
		Level.get().resetGraphics();

		for (ZInstance inst:mTowerCountDownInstances)
			ZActivity.instance.mScene.remove(inst); 
		ZActivity.instance.mScene.remove(mWindGaugeInstance);
		ZActivity.instance.mScene.remove(mWindValueInstance);
		for (ZInstance inst:mStressedLinksInstances)
			ZActivity.instance.mScene.remove(inst); 

		
	}
	
	@Override
	public void updateBudget() {}

	@Override
	public void shakeCamera() 
	{
		float amplitude = 1 * (float)Math.tan(mCamEffects.mFOV*0.5f);
		mCamEffects.addEffect(new ZFrustumCameraEffectShake(500,20,24,32,amplitude,amplitude,amplitude));
	}

}
