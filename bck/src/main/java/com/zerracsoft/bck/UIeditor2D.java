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
import java.util.List;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZOrthoCamera;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;

public class UIeditor2D extends UI {

	ZVector camPos = new ZVector(0, -5, 0);
	SmoothFloat zoomFactor = new SmoothFloat(3,25,0.5f,10);
	SmoothFloat camPosX = new SmoothFloat(0,50,-10,10);
	SmoothFloat camPosZ = new SmoothFloat(0,50,-10,10);

	ZOrthoCamera mOrthoCamera = new ZOrthoCamera();

	ZInstance mInstanceBudgetTitle;
	ZInstance mInstanceBudgetBars;
	ZInstance mInstanceBudgetCables;
	ZInstance mInstanceBudgetHydraulics;
	
	ZInstance mInstanceCursorNormal;
	ZInstance mInstanceCursorHighlighted;
	ZInstance mInstanceNodeHighlighted;
	ZInstance mInstanceLinkHighlighted;
	
	int mNextTutorialIndex = 0;
	
	@Override
	public void init() 
	{
		// r�glage du zoom et de ses valeurs limites
		float zoomMaxX = (Level.get().getXMax()-Level.get().getXMin()) / ZRenderer.getViewportWidthF();
		float zoomMaxZ = (Level.get().getZMax()-Level.get().getEditorZMin()) / ZRenderer.getViewportHeightF();
		zoomFactor.setLimits(2,Math.min(zoomMaxX, zoomMaxZ));
		zoomFactor.setForced(Math.min(zoomMaxX, zoomMaxZ));

		// cr�ation du menu d'�dition
		ZActivity.instance.mScene.setMenu(new MenuButtonsEditor(this,true));
		
		Level.get().resetSimulation();
		Level.get().initGraphics();
		
		// cr�ation des strings de budget
		updateBudget();
		
		// cr�ation des curseurs
		initCursors();

		ReleaseSettings.suspendRendering(false);
	
		// tutorial si n�cessaire
		mNextTutorialIndex = 0;
		
	}
	
	@Override
	public void updateBudget()
	{
		StatusWorld world = Level.get().mStatus.mWorld;
		
		if (world.mInfiniteBudget) return;

		if (mInstanceBudgetTitle!=null) ZActivity.instance.mScene.remove(mInstanceBudgetTitle);
		if (mInstanceBudgetBars!=null) ZActivity.instance.mScene.remove(mInstanceBudgetBars);
		if (mInstanceBudgetCables!=null) ZActivity.instance.mScene.remove(mInstanceBudgetCables);
		if (mInstanceBudgetHydraulics!=null) ZActivity.instance.mScene.remove(mInstanceBudgetHydraulics);
		
		float dy = ZRenderer.getViewportHeightF()/(float)ZRenderer.getScreenHeight()*GameActivity.mFont.getHeight();
		
		String s = ZActivity.instance.getResources().getString(R.string.budget);
		mInstanceBudgetTitle = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.RIGHT, ZFont.AlignV.TOP));
		mInstanceBudgetTitle.setTranslation(ZRenderer.alignScreenGrid(ZRenderer.getViewportWidthF()*0.5f),ZRenderer.alignScreenGrid(ZRenderer.getViewportHeightF()*0.5f),1);
		ZActivity.instance.mScene.add2D(mInstanceBudgetTitle);
		
		if (world.mBudgetBar>0)
		{	
			s = ZActivity.instance.getResources().getString(R.string.budget_bar) + " " + Integer.toString(world.mBudgetBar-world.mSpentBar) + " / "+Integer.toString(world.mBudgetBar);
			mInstanceBudgetBars = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.RIGHT, ZFont.AlignV.TOP));
			mInstanceBudgetBars.setTranslation(ZRenderer.alignScreenGrid(ZRenderer.getViewportWidthF()*0.5f),ZRenderer.alignScreenGrid(ZRenderer.getViewportHeightF()*0.5f-dy),1);
			ZActivity.instance.mScene.add2D(mInstanceBudgetBars);
		}
		
		if (world.mBudgetCable>0)
		{	
			s = ZActivity.instance.getResources().getString(R.string.budget_cable) + " " + Integer.toString(world.mBudgetCable-world.mSpentCable) + " / "+Integer.toString(world.mBudgetCable);
			mInstanceBudgetCables = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.RIGHT, ZFont.AlignV.TOP));
			mInstanceBudgetCables.setTranslation(ZRenderer.alignScreenGrid(ZRenderer.getViewportWidthF()*0.5f),ZRenderer.alignScreenGrid(ZRenderer.getViewportHeightF()*0.5f-dy*2.f),1);
			ZActivity.instance.mScene.add2D(mInstanceBudgetCables);
		}
		
		if (world.mBudgetJack>0)
		{	
			s = ZActivity.instance.getResources().getString(R.string.budget_hydraulic) + " " + Integer.toString(world.mBudgetJack-world.mSpentJack) + " / "+Integer.toString(world.mBudgetJack);
			mInstanceBudgetHydraulics = new ZInstance(GameActivity.mFont.createStringMesh(s, ZFont.AlignH.RIGHT, ZFont.AlignV.TOP));
			mInstanceBudgetHydraulics.setTranslation(ZRenderer.alignScreenGrid(ZRenderer.getViewportWidthF()*0.5f),ZRenderer.alignScreenGrid(ZRenderer.getViewportHeightF()*0.5f-dy*3.f),1);
			ZActivity.instance.mScene.add2D(mInstanceBudgetHydraulics);
		}
	}
	
	static ZVector v = new ZVector();
	static ZColor col = new ZColor();
	public void initCursors()
	{
		
		float w = ZRenderer.getViewportWidthF();
		float h = ZRenderer.getViewportHeightF();
		float t = 2.f*ZRenderer.getViewportPixelSize();
		
		// mesh de s�lection ormal: une croix toute conne
		ZMesh m = new ZMesh();
		m.allocMesh(8, 4, false);
		col.set(1,1,1,0.5f);
		v.set(-w,-t, 0);	m.addVertex(v, null, 0, 1);
		v.set(w,-t, 0);		m.addVertex(v, null, 1, 1);
		v.set(-w,t, 0);		m.addVertex(v, null, 0, 0);
		v.set(w,t, 0);		m.addVertex(v, null, 1, 0);
		m.addFace(0,1,3);
		m.addFace(0,3,2);
		v.set(-t,-h, 0);	m.addVertex(v, null, 0, 1);
		v.set(t,-h, 0);		m.addVertex(v, null, 1, 1);
		v.set(-t,h, 0);		m.addVertex(v, null, 0, 0);
		v.set(t,h, 0);		m.addVertex(v, null, 1, 0);
		m.addFace(4,5,7);
		m.addFace(4,7,6);
		mInstanceCursorNormal = new ZInstance(m);
		mInstanceCursorNormal.setColor(col);
		mInstanceCursorNormal.setVisible(false);
		ZActivity.instance.mScene.add2D(mInstanceCursorNormal);
		
		// curseur de s�lection "highlight"
		m = new ZMesh();
		w = h = 0.2f;
		m.createRectangle(-w,-h,w,h,0,1,1,0);
		m.setMaterial("select_cursor");
		mInstanceCursorHighlighted = new ZInstance(m);
		mInstanceCursorHighlighted.setVisible(false);
		ZActivity.instance.mScene.add2D(mInstanceCursorHighlighted);
		
		// curseurs "highlight" node & bar
		m = new ZMesh();
		w = h = 0.75f;
		m.createRectangle(-w,-h,w,h,0,1,1,0);
		m.setMaterial("node_highlight");
		mInstanceNodeHighlighted = new ZInstance(m);
		mInstanceNodeHighlighted.setVisible(false);
		ZActivity.instance.mScene.add(mInstanceNodeHighlighted);
		
		m = new ZMesh();
		w = 0.7f; h = 0.5f;
		m.createRectangle(-w,-h,w,h,0,1,1,0);
		//m.setMaterial("bar_highlight");
		m.setMaterial("node_highlight");
		mInstanceLinkHighlighted = new ZInstance(m);
		mInstanceLinkHighlighted.setVisible(false);
		ZActivity.instance.mScene.add(mInstanceLinkHighlighted);
		
	}


	private int mCursorAnimPeriod = 0;
	static private ZColor alphaBlink = new ZColor();
	@Override
	public void update(int elapsedTime) 
	{
		super.update(elapsedTime);
		// update camera limits
		Level level = Level.get();
		float xMin = level.getXMin();
		float xMax = level.getXMax(); 
		float zMin = level.getEditorZMin();
		float zMax = level.getZMax(); 
		
		camPosX.setLimits(xMin+0.5f*ZRenderer.getViewportWidthF()*zoomFactor.get(),
							xMax-0.5f*ZRenderer.getViewportWidthF()*zoomFactor.get());
		camPosZ.setLimits(zMin+0.5f*ZRenderer.getViewportHeightF()*zoomFactor.get(),
							zMax-0.5f*ZRenderer.getViewportHeightF()*zoomFactor.get());
		
		// update camera

		// fov de 90� 
//		float fov = (float) Math.PI * 0.5f;

		// attention, le fov est horizontal... on va donc s'arranger pour que la
		// distance fasse que
		// (coord viewport(X,Y)*zoomfactor = coord world(X,Z)

		// update cam distance
		camPos.set(camPosX.get(), -zoomFactor.get() * ZRenderer.getViewportWidthF() * 0.5f, camPosZ.get());

		mOrthoCamera.setParameters(0.5f, 100, ZRenderer.getScreenRatioF()*zoomFactor.get(), zoomFactor.get(), 
				camPos, ZVector.constY, ZVector.constX, ZVector.constZ);
//		ZRenderer.setCamera(mFrustumCamera);  
		ZRenderer.setCamera(mOrthoCamera);  
		
		
		// update cursors meshs
		boolean cursorActive = false;
		boolean cursorHighlighted = false;
		ZVector cursorPosWorld = null;
		switch (mTouchState)
		{
		case SELECTNODE:
			if (mSelectedNode==null)
			{
				setTouchState(TouchState.NONE);
			}
			else
			{
				cursorPosWorld = new ZVector(mSelectedNode.getPositionX(),0,mSelectedNode.getPositionZ());
				cursorActive = true;
				//cursorHighlighted = true;
			}
			break;
		case SELECTLINK:
			if (mSelectedLink==null)
			{
				setTouchState(TouchState.NONE);
			}
			else
			{
				cursorPosWorld = mSelectedLink.getPosition();
				cursorActive = true;
				//cursorHighlighted = true;
			}
			break;
		case EDITNODE:
			cursorPosWorld = new ZVector(mSelectedNode.getPositionX(),0,mSelectedNode.getPositionZ());
			cursorActive = true;
			//cursorHighlighted = true;
			break;
		case EDITLINK:
			cursorPosWorld = mSelectedLink.getPosition();
			cursorActive = true;
			//cursorHighlighted = true;
			break;
		case CREATELINK:
			if (mCreatingNode!=null)
			{
				cursorPosWorld = new ZVector(mCreatingNode.getPositionX(),0,mCreatingNode.getPositionZ());
				cursorActive = true;
				cursorHighlighted = (null!=level.getTwinNode(mCreatingNode));
			}
			break;	
		case MOVENODE:
			cursorPosWorld = new ZVector(mSelectedNode.getPositionX(),0,mSelectedNode.getPositionZ());
			cursorActive = true;
			cursorHighlighted = (null!=level.getTwinNode(mSelectedNode));
			break;	
		}
		
		// curseur de s�l�ection
		if (cursorActive && cursorPosWorld!=null)
		{
			mCursorAnimPeriod = (mCursorAnimPeriod+elapsedTime%1000);
			float scale = 1.f+0.5f*(float)Math.sin((float)mCursorAnimPeriod*0.001 * (Math.PI*2.0));
			float cursorX = xWorld2xViewport(cursorPosWorld.get(0));
			float cursorY = zWorld2yViewport(cursorPosWorld.get(2));
			mInstanceCursorNormal.setTranslation(cursorX, cursorY,ReleaseSettings.layer2dCursor);
			mInstanceCursorHighlighted.setTranslation(cursorX, cursorY,ReleaseSettings.layer2dCursorHighlighted);
			mInstanceCursorHighlighted.setScale(scale,scale,1);
			mInstanceCursorNormal.setVisible(true);
			mInstanceCursorHighlighted.setVisible(cursorHighlighted);  
			
			alphaBlink.set(1,1,1,0.75f+0.25f*(float)Math.sin((float)mCursorAnimPeriod*0.001 * (Math.PI*2.0)));
			
			mInstanceNodeHighlighted.setVisible(false);
			mInstanceLinkHighlighted.setVisible(false);
			Link link = mSelectedLink;
			if (link==null) link = mCreatingLink;
			if (link!=null)
			{
				mInstanceLinkHighlighted.setRotation(ZQuaternion.constX90);
				mInstanceLinkHighlighted.rotate(ZVector.constYNeg, link.getAngle());
				mInstanceLinkHighlighted.setTranslation(link.getPosition());
				mInstanceLinkHighlighted.setScale(link.getLength(), 1, 1);
				mInstanceLinkHighlighted.translate(0,1,0);
				mInstanceLinkHighlighted.setVisible(true);
				mInstanceLinkHighlighted.setColor(alphaBlink);

			}
			else
			{
				// si pas de lien, alors un node?
				if (mSelectedNode!=null)
				{
					mInstanceNodeHighlighted.setRotation(ZQuaternion.constX90);
					mInstanceNodeHighlighted.setTranslation(mSelectedNode.getPositionX(),1,mSelectedNode.getPositionZ());
					mInstanceNodeHighlighted.setVisible(true);
					mInstanceNodeHighlighted.setColor(alphaBlink);
				}
			}	
			
		}
		else
		{
			mInstanceCursorNormal.setVisible(false);
			mInstanceCursorHighlighted.setVisible(false);			
			mInstanceNodeHighlighted.setVisible(false);			
			mInstanceLinkHighlighted.setVisible(false);			
		}
		
		// update various timers
		updateTouchState(elapsedTime);
		zoomFactor.update(elapsedTime);
		camPosX.update(elapsedTime);
		camPosZ.update(elapsedTime);
		
		// tutorial si n�cessaire
		if (Level.get().mTutorialsID.size()!=0 && (ReleaseSettings.ALWAYS_SHOW_TUTO || Level.get().mStatus.mTutorialPlayed==false))
		{
			if (!TutorialDialog.isActive())
			{
				if (mNextTutorialIndex<Level.get().mTutorialsID.size())
					TutorialDialog.showTutorial(Level.get().mTutorialsID.get(mNextTutorialIndex++));
				else
					Level.get().mStatus.mTutorialPlayed=true; // ne plus le montrer
			}
		}

	}
	
	public void replayTuto()
	{
		mNextTutorialIndex = 0;
		Level.get().mStatus.mTutorialPlayed=false;
	}

	enum EditMode
	{
		MOVENODE,
		CREATEBAR,
		CREATEHYDRAULICEXTEND,
		CREATEHYDRAULICRETRACT,
		CREATECABLE
	}
	public EditMode mEditMode = EditMode.CREATEBAR;
	
	public enum TouchState 
	{
		NONE, // no touch actually
		UNKNOWN, // one finger down, intention not yet known
		CAMERAMOVE, 
		CAMERAZOOM,
		SELECTNODE,
		SELECTLINK,
		EDITNODE,	// popup menu 
		EDITLINK,	// popup menu 
		CREATELINK,
		MOVENODE
	}
	private TouchState mTouchState = TouchState.NONE;
	public TouchState getTouchState() { return mTouchState; }
	private int mTouchStateTimer = 0;
	private int mTouchStateFrame = 0;
	private Node mSelectedNode;
	private Link mSelectedLink;

	private Link mCreatingLink;
	private Node mCreatingNode;
	
	
	public void setTouchState(TouchState newState)
	{
		if (newState==mTouchState) 
		{
			BCKLog.i("UIeditor2D","setTouchState ignored: "+newState.toString());
			return;
		}
		//TouchState oldState = mTouchState;
		mTouchState = newState;
		mTouchStateTimer = 0;
		mTouchStateFrame = 0;
		switch (newState)
		{
		case NONE:
			// d�selection
			mSelectedNode=null;
			mSelectedLink=null;
			break;
		case CAMERAZOOM:
			// d�selection
			mSelectedNode=null;
			mSelectedLink=null;
			// effacement du node et du link en cours de cr�ation...
			if (mCreatingLink!=null)
			{
				mCreatingLink.destroy();
				Level.get().remove(mCreatingLink);
				mCreatingLink=null;
			}
			if (mCreatingNode!=null)
			{
				mCreatingNode.resetGraphics();
				Level.get().remove(mCreatingNode);
				mCreatingNode=null;
			}
			break;
		}
	}
	
	public void updateTouchState(int timeIncrement)
	{
		mTouchStateTimer += timeIncrement;
		mTouchStateFrame++;
		switch(mTouchState)
		{
		case UNKNOWN:
			if (mTouchStateFrame>1 && mTouchStateTimer>=ReleaseSettings.TOUCH_DRAG_TIME)
			{

				if (mSelectedNode!=null)
				{
					setTouchState(TouchState.SELECTNODE);
				}

				if (mSelectedLink!=null)
				{
					setTouchState(TouchState.SELECTLINK);
				}

			}
			break;
			
		case SELECTNODE:
			if (mTouchStateTimer>=ReleaseSettings.TOUCH_POPUP_TIME && !mSelectedNode.isFixed())
			{
				ZActivity.instance.mScene.setMenuWithTransition(new MenuPopupNode(this,Level.get(),mSelectedNode));
				setTouchState(TouchState.EDITNODE);
			}
			break;
			
		case SELECTLINK:
			if (mTouchStateTimer>=ReleaseSettings.TOUCH_POPUP_TIME)
			{
				ZActivity.instance.mScene.setMenuWithTransition(new MenuPopupLink(this,Level.get(),mSelectedLink));
				setTouchState(TouchState.EDITLINK);
			}
			break;
		}
	}
	
	
	@Override
	public void onTouchMoveStart(float xStart, float yStart) {
		if (mTouchState!=TouchState.EDITLINK && mTouchState!=TouchState.EDITNODE)
		{
			setTouchState(TouchState.UNKNOWN);

			float selectionRay = zoomFactor.get()*0.25f;
			float xStartViewport = ZRenderer.screenToViewportXF(xStart);
			float yStartViewport = ZRenderer.screenToViewportYF(yStart);
			float xStartWorld = xViewport2xWorld(xStartViewport);
			float zStartWorld = yViewport2zWorld(yStartViewport);
			
			// s�lection d'un node ou d'un link
			mSelectedNode = Level.get().getNearestNode(xStartWorld, zStartWorld, selectionRay);
			mSelectedLink = Level.get().getNearestLink(xStartWorld, zStartWorld, selectionRay);
			
			if (mSelectedNode!=null && mSelectedLink!=null)
			{
				// choix entre le node et le link...
				float dNode = mSelectedNode.getDistance(xStartWorld, zStartWorld);
				float dLink = mSelectedLink.getMiddleDistance(xStartWorld, zStartWorld);
				if (dNode<dLink)
					mSelectedLink = null;
				else
					mSelectedNode = null;
			}
		}
	}

	@Override
	public void onTouchMove(float xStart, float yStart, float x, float y) 
	{
		Level level = ((GameActivity)ZActivity.instance).mLevel;

		if (mTouchState==TouchState.EDITLINK || mTouchState==TouchState.EDITNODE) return;

		
		float xStartViewport = ZRenderer.screenToViewportXF(xStart);
		float yStartViewport = ZRenderer.screenToViewportYF(yStart);
		float xViewport = ZRenderer.screenToViewportXF(x);
		float yViewport = ZRenderer.screenToViewportYF(y);
		float xStartWorld = xViewport2xWorld(xStartViewport);
		float zStartWorld = yViewport2zWorld(yStartViewport);
		float xWorld = xViewport2xWorld(xViewport);
		float zWorld = yViewport2zWorld(yViewport);
		
		float dxViewport = xViewport-xStartViewport;
		float dyViewport = yViewport-yStartViewport;
		
		
		switch (mTouchState)
		{
		case UNKNOWN:
			{
				// passage � CAMERAMOVE ou � SELECT ?
				if (Math.abs(dxViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD
						|| Math.abs(dyViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD)
				{
					setTouchState(TouchState.CAMERAMOVE);
					camMoveStartX = camPosX.get();
					camMoveStartZ = camPosZ.get();
				}
		
			}
			break;
			
		case SELECTNODE:
			{
				switch (mEditMode)
				{
					case CREATEBAR:
					case CREATECABLE:
					case CREATEHYDRAULICEXTEND:
					case CREATEHYDRAULICRETRACT:
						{
							// passage � DRAG ?
							if (Math.abs(dxViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD
									|| Math.abs(dyViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD)
							{
								// budget compliant ?
								if (level.mStatus.mWorld.mInfiniteBudget
									||	(mEditMode==EditMode.CREATEBAR && level.mStatus.mWorld.mSpentBar<level.mStatus.mWorld.mBudgetBar)
									|| (mEditMode==EditMode.CREATECABLE && level.mStatus.mWorld.mSpentCable<level.mStatus.mWorld.mBudgetCable)
									|| (mEditMode==EditMode.CREATEHYDRAULICEXTEND && level.mStatus.mWorld.mSpentJack<level.mStatus.mWorld.mBudgetJack)
									|| (mEditMode==EditMode.CREATEHYDRAULICRETRACT && level.mStatus.mWorld.mSpentJack<level.mStatus.mWorld.mBudgetJack)
										)
								{
									backupLevel();
									setTouchState(TouchState.CREATELINK);
									
									// cr�ation d'un nouveau node et d'un nouveau link
									float gridX = Math.round(xStartWorld);
									float gridZ = Math.round(zStartWorld);
									mCreatingNode = new Node(gridX,gridZ,false);
									
									level.add(mCreatingNode);
									if (mEditMode==EditMode.CREATEBAR)
										mCreatingLink = new LinkBar(mSelectedNode,mCreatingNode);
									else if (mEditMode==EditMode.CREATEHYDRAULICEXTEND)
										mCreatingLink = new LinkJack(mSelectedNode,mCreatingNode,LinkJack.Type.EXTEND);
									else if (mEditMode==EditMode.CREATEHYDRAULICRETRACT)
										mCreatingLink = new LinkJack(mSelectedNode,mCreatingNode,LinkJack.Type.RETRACT);
									else if (mEditMode==EditMode.CREATECABLE)
										mCreatingLink = new LinkCable(mSelectedNode,mCreatingNode);
									level.add(mCreatingLink);
									mCreatingNode.initGraphics();
									mCreatingLink.initGraphics();
									// calcul des mouvements possibles
								mSelectedNodePossibleMoves = mCreatingNode.buildEditPossiblePositions();
								}
							}
						}
						break;
					case MOVENODE:
						{
							// passage � DRAG ?
							if (!mSelectedNode.isFixed() 
									&& (Math.abs(dxViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD
									|| Math.abs(dyViewport)>ReleaseSettings.TOUCH_MOVE_THRESHOLD) )
							{
								backupLevel();
								setTouchState(TouchState.MOVENODE);
								// calcul des mouvements possibles
								mSelectedNodePossibleMoves = mSelectedNode.buildEditPossiblePositions();
							}
						}
						break;

				}
				
			}
			break;

		case SELECTLINK:
			{
				// NOTHING
			}
			break;
			
		case CAMERAMOVE:
			camPosX.set(camMoveStartX-dxViewport*zoomFactor.get());
			camPosZ.set(camMoveStartZ-dyViewport*zoomFactor.get());	
			break;
			
		case CREATELINK:
			// move creating node
			if (mCreatingNode!=null)
			{
				ZVector newPos = getNearestNodePossiblePosition(xWorld,zWorld);
				mCreatingNode.setPosition(newPos.get(0), newPos.get(2));
			
			}
			break;
			
		case MOVENODE:
			// move node
			if (mSelectedNode!=null)
			{
				ZVector newPos = getNearestNodePossiblePosition(xWorld,zWorld);
				mSelectedNode.setPosition(newPos.get(0), newPos.get(2));
				
			}
			break;
			
		}
		
	}

	@Override
	public void onTouchMoveEnd() {
		Level level = ((GameActivity)ZActivity.instance).mLevel;
		if (mTouchState!=TouchState.EDITLINK && mTouchState!=TouchState.EDITNODE)
		{
			// laisser dans le level les objets cr��s, fusionner les nodes si n�cessaire, 
			// d�selectionner si n�cessaire, etc....
			mCreatingNode = null;
			mCreatingLink = null;
			level.fixIntegrity();
			
			cacheBackupLevel(); // acc�l�re le d�but de la prochaine op�ration (le lag a lieu quand l'utilisateur l�ve la main)
			
			setTouchState(TouchState.NONE);
		}
		

	}

	float zoomFactorPinchStart = 1;	// zoom factor at the beginning of the pinch...
	float camMoveStartX = 0;
	float camMoveStartZ = 0;
	
	@Override
	public void onTouchPinchStart(float xStart, float yStart) {
		zoomFactorPinchStart = zoomFactor.get();
		camMoveStartX = camPosX.get();
		camMoveStartZ = camPosZ.get();
		
		setTouchState(TouchState.CAMERAZOOM);	
	}

	@Override
	public void onTouchPinch(float xStart, float yStart, float x, float y, float factor) {
		// on set le zoomfactor
		zoomFactor.set(zoomFactorPinchStart/factor);
		
		// on modifie aussi la position de la cam pour que le centre du pich soit toujours le m�me point du monde
		float xStartViewport = ZRenderer.screenToViewportXF(xStart);
		float yStartViewport = ZRenderer.screenToViewportYF(yStart);
		float xViewport = ZRenderer.screenToViewportXF(x);
		float yViewport = ZRenderer.screenToViewportYF(y);
		// calcul de la position de ce fameux point....
		float xWorld = xStartViewport*zoomFactorPinchStart+camMoveStartX;
		float zWorld = yStartViewport*zoomFactorPinchStart+camMoveStartZ;
		
		camPosX.set(xWorld-xViewport*zoomFactor.get());
		camPosZ.set(zWorld-yViewport*zoomFactor.get());
	}

	@Override
	public void onTouchPinchEnd() {
		setTouchState(TouchState.NONE);
	}
	
	
	
	
	
	
	
	
	

	public float xViewport2xWorld(float xViewport) {return xViewport*zoomFactor.get()+camPosX.get();}
	public float yViewport2zWorld(float yViewport) {return yViewport*zoomFactor.get()+camPosZ.get();}

	public float xWorld2xViewport(float xWorld) {return (xWorld-camPosX.get())/zoomFactor.get();}
	public float zWorld2yViewport(float zWorld) {return (zWorld-camPosZ.get())/zoomFactor.get();}
	
	private List<ZVector> mSelectedNodePossibleMoves; // possible moves of the selected node....
	private ZVector getNearestNodePossiblePosition(float x, float z)
	{
		float bestDist = 0;
		ZVector bestPosition = null;
		for (ZVector vect:mSelectedNodePossibleMoves)
		{
			float dx = vect.get(0)-x;
			float dz = vect.get(2)-z;
			float d = (float)Math.sqrt(dx*dx+dz*dz);
			if (null==bestPosition || d<bestDist)
			{
				bestPosition = vect;
				bestDist=d;
			}
		}
		return bestPosition;
	}

	@Override
	public ViewMode getViewMode() 
	{
		return ViewMode.MODE2D;
	}


	@Override
	public boolean isSimulationOn() { return false; }

	@Override
	public boolean isEditMode() { return true; }

	
	
	
	
	// undo feature
	private ArrayList<Level.Backup> mUndoBackups = new ArrayList<Level.Backup>(ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE);
	private ArrayList<Level.Backup> mRedoBackups = new ArrayList<Level.Backup>(ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE);
	private Level.Backup mCachedBackup;
	public boolean isUndoable() {return !mUndoBackups.isEmpty();}
	public boolean isRedoable() {return !mRedoBackups.isEmpty();}
	
	public void undo()
	{
		if (!mUndoBackups.isEmpty())
		{
			if (mRedoBackups.size()==ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE)
				mRedoBackups.remove(ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE-1);
			mRedoBackups.add(0, Level.get().createBackup());
			Level.get().restore(mUndoBackups.get(0));
			mUndoBackups.remove(0);
			mCachedBackup=null;
		}
	}
	
	public void redo() 
	{
		if (!mRedoBackups.isEmpty())
		{
			if (mUndoBackups.size()==ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE)
				mUndoBackups.remove(ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE-1);
			mUndoBackups.add(0, Level.get().createBackup());
			Level.get().restore(mRedoBackups.get(0));
			mRedoBackups.remove(0);
			mCachedBackup=null;
		}
	}
	
	
	public void cacheBackupLevel()
	{
		mCachedBackup = Level.get().createBackup();
	}
	
	public void backupLevel()
	{
		if (mCachedBackup==null) cacheBackupLevel();
		if (mUndoBackups.size()==ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE)
			mUndoBackups.remove(ReleaseSettings.EDITOR_UNDO_QUEUE_QIZE-1);
		//mBackups.add(0, Level.get().createBackup());
		mUndoBackups.add(0, mCachedBackup);
		mCachedBackup=null;
		mRedoBackups.clear();
		
		Level.get().mStatus.mState = StatusLevel.State.NONE;		// on repasse en �tat "non valid�"
	}


	@Override
	public void close()
	{
		ReleaseSettings.suspendRendering(true);
		Level.get().resetGraphics();
		Level.get().saveGame();
		if (mInstanceBudgetTitle!=null) ZActivity.instance.mScene.remove(mInstanceBudgetTitle);
		if (mInstanceBudgetBars!=null) ZActivity.instance.mScene.remove(mInstanceBudgetBars);
		if (mInstanceBudgetCables!=null) ZActivity.instance.mScene.remove(mInstanceBudgetCables);
		if (mInstanceBudgetHydraulics!=null) ZActivity.instance.mScene.remove(mInstanceBudgetHydraulics);
		if (mInstanceCursorNormal!=null) ZActivity.instance.mScene.remove(mInstanceCursorNormal);
		if (mInstanceCursorHighlighted!=null) ZActivity.instance.mScene.remove(mInstanceCursorHighlighted);
	}

	
	@Override
	public void shakeCamera() {}
	
	
}
