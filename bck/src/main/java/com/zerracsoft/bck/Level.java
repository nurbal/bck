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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream; 
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMaterial;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZWaterRect;
import com.zerracsoft.Lib3D.ZPointsMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.bck.Rails.Proximity;

public class Level implements GraphicsObject
{
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	
	protected native static void JNIsetWaterHeight(long obj,float height);
	protected native static void JNIunsetWater(long obj);
	protected native static boolean JNIhasWater(long obj);
	protected native static float JNIgetWaterHeight(long obj);
	
	protected native static void JNIsetWind(long obj,float wind);
	protected native static float JNIgetWind(long obj);
	
	protected native static void JNIsetRailsHeight(long obj,float height);
	protected native static float JNIgetRailsHeight(long obj);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	// points fixes
	private ArrayList<Node> mFixedNodes = new ArrayList<Node>(ReleaseSettings.MAX_FIXED_NODES);
	// nodes
	private ArrayList<Node> mMobileNodes = new ArrayList<Node>(ReleaseSettings.MAX_MOBILE_NODES);
	// links
	private ArrayList<Link> mLinks = new ArrayList<Link>(ReleaseSettings.MAX_LINKS);
	private ArrayList<Link> mLinksToExplode = new ArrayList<Link>(ReleaseSettings.MAX_LINKS);
	// rails
	private ArrayList<FixedRails> mFixedRails = new ArrayList<FixedRails>(ReleaseSettings.MAX_FIXED_RAILS);
	private ArrayList<Rails> mRails = new ArrayList<Rails>(ReleaseSettings.MAX_RAILS);
	// stone collumns
	private ArrayList<StoneCollumn> mStoneCollumns = new ArrayList<StoneCollumn>(ReleaseSettings.MAX_STONE_COLLUMNS);
	
	// Train
	public TestVehicle mTrain = new Train();
	// Boat
	public ArrayList<TestVehicle> mBoats = new ArrayList<TestVehicle>(ReleaseSettings.MAX_NB_BOATS);
	
	// tower mode?
	protected static native void JNIsetTowerMode(long obj,boolean towerMode);
	protected static native boolean JNIisTowerMode(long obj);
	protected static native void JNIsetTowerGoal(long obj,float goal);
	protected static native float JNIgetTowerGoal(long obj);
	public void setTowerMode(boolean towerMode) {JNIsetTowerMode(mNativeObject,towerMode);}
	public boolean isTowerMode() {return JNIisTowerMode(mNativeObject);}
	public void setTowerGoal(float goal) {JNIsetTowerGoal(mNativeObject,goal);}
	public float getTowerGoal() {return JNIgetTowerGoal(mNativeObject);}
	
	
	// tutorial?
	public ArrayList<String> mTutorialsID = new ArrayList<String>(5);
	
	// mesh bank
	public MeshBank mMeshBank;
	
	// budget
	public void updateBudget() 
	{ 
		mStatus.mSpentBar = 0;
		mStatus.mSpentCable = 0;
		mStatus.mSpentHydraulic = 0;
		for (Link l:mLinks) l.updateBudget(mStatus);
		mStatus.mWorld.updateBudget();
		
		UI ui = UI.get();
		if (ui!=null)
			ui.updateBudget();

	}
	
	// extents 
	protected static native int JNIgetXMin(long obj);
	protected static native int JNIgetXMax(long obj);
	protected static native float JNIgetXCenter(long obj);
	protected static native float JNIgetXSize(long obj);
	
	public int getXMin() { return JNIgetXMin(mNativeObject); }
	public int getXMax() { return JNIgetXMax(mNativeObject); }
	public float getXCenter() { return JNIgetXCenter(mNativeObject); }
	public float getXSize() { return JNIgetXSize(mNativeObject); }
	
	protected static native int JNIgetZMin(long obj);
	protected static native int JNIgetZMax(long obj);
	protected static native float JNIgetZCenter(long obj);
	protected static native float JNIgetZSize(long obj);
	protected static native void JNIsetZExtents(long obj, int zMin, int zMax);
	
	public int getZMin() { return JNIgetZMin(mNativeObject); }
	public int getZMax() { return JNIgetZMax(mNativeObject); }
	public float getZCenter() { return JNIgetZCenter(mNativeObject); }
//	public float getZSize() { return JNIgetZSize(mNativeObject); }
	public void setZExtents(int zMin, int zMax) { JNIsetZExtents(mNativeObject, zMin, zMax); }
	
	public int getEditorZMin()
	{
		// version "speciale" destin�e � ne pas ramasser le niveau en bas de l'�cran en mode "d�zoom�"
		int editorZMin = (int)(-0.5f*(float)getXSize() / ZRenderer.getScreenRatioF()); 
		return Math.min(editorZMin, getZMin());
	}
	
	
	// floor
	protected static native float JNIgetFloorHeight(long obj, int x);
	protected static native float JNIgetNearHeight(long obj, int x);
	protected static native float JNIgetFarHeight(long obj, int x);
	
	protected static native void JNIsetNearHeight(long obj, int x, float height);
	protected static native void JNIsetFarHeight(long obj, int x, float height);
	protected static native void JNIsetFloorHeight(long obj, int x, float height);
	
	protected static native void JNIallocFloor(long obj, int xMin, int xMax);
	protected static native void JNIcompleteFloor(long obj);

	public float getFloorHeight(int x) { return JNIgetFloorHeight(mNativeObject,x); }
	public void setFloorHeight(int x, float height) { JNIsetFloorHeight(mNativeObject,x,height); }
	public float getNearHeight(int x) { return JNIgetNearHeight(mNativeObject,x); }
	public void setNearHeight(int x, float height) { JNIsetNearHeight(mNativeObject,x,height); }
	public float getFarHeight(int x) { return JNIgetFarHeight(mNativeObject,x); }
	public void setFarHeight(int x, float height) { JNIsetFarHeight(mNativeObject,x,height); }
	public void allocFloor(int xMin, int xMax) {JNIallocFloor(mNativeObject, xMin, xMax);}
	public void completeFloor() {JNIcompleteFloor(mNativeObject);}
	
	// water
	public boolean hasWater() {return JNIhasWater(mNativeObject);}
	public float getWaterHeight() {return JNIgetWaterHeight(mNativeObject);}
	public void setWaterHeight(float height) { JNIsetWaterHeight(mNativeObject,height); }

	
	// simulation states
	enum SimulationState
	{
		UNKNOWN,
		TOWER_WINDTEST,			// �preuve au vent de la tour
		TOWER_COUNTDOWN,		// compte � rebours final pour l'�preuve de la tour
		START,					// �tat initial, encore aucun v�hicule
		RUNNING_TRAIN,			// premier train
		RUNNING_LIFTBRIDGE,		// pont se l�ve
		RUNNING_BOAT,			// bateau passe sous le pont
		RUNNING_LOWERBRIDGE,	// pont s'abaisse
		RUNNING_TRAIN2,			// second train
		SUCCEED,				
		FAILING_TRAIN,					
		FAILING_BOAT,					
		FAILING_TRAIN2,					
		FAILED_TRAIN,				
		FAILED_BOAT,				
		FAILED_TRAIN2,
		FAILED_TOWER
	}
	private float mFailedLinkDestroyTimer;
	private SimulationState mSimulationState = SimulationState.UNKNOWN;
	private int mSimulationStateTimer = 0;
	public SimulationState getSimulationState() {return mSimulationState;}
	public int getSimulationStateTimer() {return mSimulationStateTimer;}
	
	private static native float JNIgetFloorNearestPoint(long obj, long inPoint,float inMaxRay,long outPoint,long outNormal,long outTangent);
	public float getFloorNearestPoint(ZVector inPoint,float inMaxRay,ZVector outPoint,ZVector outNormal,ZVector outTangent) // can return negative values!! (if underground)
	{
		return JNIgetFloorNearestPoint(getNativeObject(),inPoint.getNativeObject(),inMaxRay,outPoint.getNativeObject(),outNormal.getNativeObject(),outTangent.getNativeObject());
	}
	private static native boolean JNIcorrectFloorPosition(long obj, long point, long N, long T);
	public boolean correctFloorPosition(ZVector point, ZVector N, ZVector T) // returns true if collision
	{
		return JNIcorrectFloorPosition(mNativeObject,point.getNativeObject(),N.getNativeObject(),T.getNativeObject());
	}
	
	
	// rails height, start & goal
	public float getDefaultRailsHeight() { return JNIgetRailsHeight(mNativeObject); }

	static Rails.Proximity railsProximity = new Rails.Proximity();
	public boolean getRailsNearestPoint(ZVector inPoint,float inMaxRay,Proximity out)
	{
		out.distance=-1;
		for (Rails r:mRails)
		{   
			if (r.getRailsNearestPoint(inPoint, inMaxRay, railsProximity)) 
				if (out.distance<0 || railsProximity.distance<out.distance)
					out.copy(railsProximity);
		}
		return out.distance>=0;
	}
	
	public TestVehicle getCurrentVehicle()
	{
		switch (mSimulationState)
		{
		case RUNNING_LIFTBRIDGE:
		case RUNNING_LOWERBRIDGE:
		case RUNNING_BOAT:
		case FAILED_BOAT:
			return mBoats.get(0);
		default:
			return mTrain;
		}
	}

	
	// helper
	public static Level get() {return ((GameActivity)ZActivity.instance).mLevel;}

	
//	private ZInstance mSucceedInstance;
//	private ZInstance mFailedInstance;
	// temp: init d'un level � la con
	public Level()
	{
		// appel du constructeur JNI avec les param�tres qui vont bien
		mNativeObject = JNIconstructor();
	}
	
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}  
	
	
	public void loadGame()
	{
		String filename = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX+mSaveFilename;
		if (!ReleaseSettings.DEBUG_SAVEGAME_SDCARD)
			filename = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX+mSaveFilename;	
		File f = new File(filename);
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(f),8192);
			LevelSaveLoader ldr = new LevelSaveLoader();
			ldr.load(in, this);	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (mStatus!=null)
			updateBudget();
	}
	
	public void saveGame()
	{
		String dirname = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX;
		if (!ReleaseSettings.DEBUG_SAVEGAME_SDCARD)
			dirname = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX;	
		File dir = new File(dirname);
		dir.mkdirs();
		String filename = dirname+mSaveFilename;
		File f = new File(filename);
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			save(out);
			out.flush();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static LevelDesignLoader mLevelDesignLoader = new LevelDesignLoader();
	StatusLevel mStatus = null;
	public void init(StatusLevel status)
	{
		mStatus = status;
		mLevelDesignLoader.load(status.getLevelDesignInputStream(),this);
	}
	
	public String mSaveFilename;

	public void add(FixedRails r)
	{
		mFixedRails.add(r);
		JNIsetRailsHeight(mNativeObject,r.mZ);
	}
	
	public void add(Boat b)
	{
		mBoats.add(b);
	}
	
	public void add(StoneCollumn s)
	{
		mStoneCollumns.add(s);
	}
	
	private static native void JNIaddMobileNode(long obj, long node);
	private static native void JNIremoveMobileNode(long obj, long node);
	private static native void JNIclearMobileNodes(long obj);
	private static native void JNIaddLink(long obj, long link);
	private static native void JNIremoveLink(long obj, long link);
	private static native void JNIclearLinks(long obj);
	
	public void add(Node n)
	{
		//Log.d("Level.add","node "+n.getNativeObject());
		if (n.isFixed())
			mFixedNodes.add(n);
		else
		{
			mMobileNodes.add(n);
			JNIaddMobileNode(mNativeObject,n.mNativeObject);
		}
	}
	
	public void add(Link l)
	{
		mLinks.add(l);
		JNIaddLink(mNativeObject,l.getNativeObject());
		updateBudget();
	}
	
	public boolean hasJacks()
	{
		for (Link l: mLinks)
			if (l.getClass() == LinkJack.class)
				return true;
		return false;
	}

	public void remove(Node n)
	{
		//Log.d("Level.remove","node "+n.getNativeObject());
		if (n.isFixed())
			mFixedNodes.remove(n);
		else
		{
			mMobileNodes.remove(n);
			JNIremoveMobileNode(mNativeObject,n.mNativeObject);
		}
	}
	
	public void remove(Link l)
	{
		mLinks.remove(l);
		JNIremoveLink(mNativeObject,l.getNativeObject());
		updateBudget();
	}

	public void remove(Rails r)
	{
		mRails.remove(r);
	}
	
	public int getNbMobileNodes() { return mMobileNodes.size(); }
	public int getNbFixedNodes() { return mFixedNodes.size(); }
	public int getNbLinks() { return mLinks.size(); }
	public int getNbRails() { return mRails.size(); }
	
	public Node getNode(int nodeID)
	{
		if (nodeID==0) return null;
		for (Node n:mFixedNodes)
			if (n.getSaveID()==nodeID) 
				return n;
		for (Node n:mMobileNodes)
			if (n.getSaveID()==nodeID) 
				return n;
		return null;
	}
	
	public Node getNodeFromNativeObject(long nativeObject)
	{
		//Log.d("Level.getNodeFromNativeObject","looking for node "+nativeObject);
		for (Node n:mFixedNodes)
		{
			Log.d("Level.getNodeFromNativeObject","testing fixed node "+n.getNativeObject());
			if (n.getNativeObject()==nativeObject) 
				return n;
		}
		for (Node n:mMobileNodes)
		{
			Log.d("Level.getNodeFromNativeObject","testing mobile node "+n.getNativeObject());
			if (n.getNativeObject()==nativeObject) 
				return n;
		}
		BCKLog.e("Level.getNodeFromNativeObject","did not find node "+nativeObject);
		return null;
	}
	
	public Link getLinkFromNativeObject(long nativeObject)
	{
		//Log.d("Level.getLinkFromNativeObject","looking for link "+nativeObject);
		for (Link n:mLinks)
		{
			Log.d("Level.getLinkFromNativeObject","testing link "+n.getNativeObject());
			if (n.getNativeObject()==nativeObject) 
				return n;
		}
		BCKLog.e("Level.getLinkFromNativeObject","did not find link "+nativeObject);
		return null;
	}
	
	
	public void resetAll()
	{
		//Log.d("Level.resetAll","Level.resetAll");
		resetGraphics();
		
		// deletes all the fixed points and all the links...
		for (Link l:mLinks)
			l.destroy();
		mLinks.clear();
		mMobileNodes.clear();	
		JNIclearMobileNodes(mNativeObject);
		JNIclearLinks(mNativeObject);
		mFixedNodes.clear();	
		
		JNIunsetWater(mNativeObject);
		mFixedRails.clear();
		mBoats.clear();
		mStoneCollumns.clear();
		setTowerMode(false);
		
		mTutorialsID.clear();
		
		mDecorObjects.clear();
	}
	
	public void resetGameplay(boolean deleteFixedNodes)
	{
		//Log.d("Level.resetGameplay","Level.resetGameplay");
		// deletes all the not fixed points and all the links...
		for (Link l:mLinks)
			l.destroy();
		mLinks.clear();
		if (deleteFixedNodes)
		{
			for (Node n:mFixedNodes)
				n.resetGraphics();
			mFixedNodes.clear();
		}
		for (Node n:mMobileNodes)
			n.resetGraphics();
		mMobileNodes.clear();	
		JNIclearMobileNodes(mNativeObject);
		JNIclearLinks(mNativeObject);
		
		updateBudget();
	}
	
	public void save(OutputStream out) throws IOException
	{
		// num�rotation de tous les nodes
		int id = 1;
		for (Node n:mFixedNodes) n.setSaveID(id++);
		for (Node n:mMobileNodes) n.setSaveID(id++);
		
		// header
		String s;
		s = "<StructureBuilder>\n"; 
		out.write(s.getBytes());
		
		// save fixed nodes
		for (Node n:mFixedNodes) n.save(out);
		
		// save mobile nodes
		for (Node n:mMobileNodes) n.save(out);
		
		// save links
		for (Link l:mLinks) l.save(out);
		
		// footer
		s = "</StructureBuilder>\n"; 
		out.write(s.getBytes());

	}
	
	public class Backup 
	{
		private ArrayList<Node> mBackupFixedNodes = new ArrayList<Node>(ReleaseSettings.MAX_MOBILE_NODES);
		private ArrayList<Node> mBackupMobileNodes = new ArrayList<Node>(ReleaseSettings.MAX_MOBILE_NODES);
		private ArrayList<Link> mBackupLinks = new ArrayList<Link>(ReleaseSettings.MAX_LINKS);
		public Node getNode(int nodeID)
		{
			if (nodeID==0) return null;
			for (Node n:mBackupFixedNodes)
				if (n.getSaveID()==nodeID) 
					return n;
			for (Node n:mBackupMobileNodes)
				if (n.getSaveID()==nodeID) 
					return n;
			return null;
		}
	}	

	public Backup createBackup()
	{
		// num�rotation de tous les nodes
		//long start = System.currentTimeMillis();
		int id = 1;
		for (Node n:mFixedNodes) n.setSaveID(id++);
		for (Node n:mMobileNodes) n.setSaveID(id++);
		
		Backup b = new Backup();
		
		// save all nodes
		//b.mBackupFixedNodes = mFixedNodes; // just copy the reference to the array.... do not duplicate nodes!
		for (Node n:mFixedNodes) 
			b.mBackupFixedNodes.add(new Node(n));
		for (Node n:mMobileNodes) 
			b.mBackupMobileNodes.add(new Node(n));
		
		// save links
		for (Link l:mLinks)
			b.mBackupLinks.add(l.createBackup(b));
		
		//long len = System.currentTimeMillis() - start;
		//Log.d("CreateBackup","time elapsed="+len);
		return b;
	}
	
	public void restore(Backup backup)
	{
		//Log.d("Level.restore","Level.restore");
		resetGraphics();
		
		resetGameplay(true);
		
		
		for (Node n:backup.mBackupFixedNodes)
			add(n);
		for (Node n:backup.mBackupMobileNodes)
			add(n);
		for (Link l:backup.mBackupLinks)
			add(l);

		initGraphics();
		
		updateBudget();
	}
	
	
	
	ZInstance mInstGrid;
	ZInstance mInstFloor;
	ZInstance mInstMud;
	ZInstance mInstSky;
	ZInstance mInstWater;
	ZInstance mInstPlexi;
	/*
	P6PoolLinkBreak mP6PoolBars;
	P6PoolLinkBreak mP6PoolHydraulics;
	P6PoolLinkBreak mP6PoolCables;
	*/
	
	// gare
	public ZInstance mInstanceDecor;
	public ZInstance mInstanceDecorAlpha;
	
	protected class DecorObject
	{
		public DecorObject(String name,ZVector pos)
		{
			meshName = name;
			position = pos;
		}
		public String meshName;
		public ZVector position;
	}
	protected ArrayList<DecorObject> mDecorObjects = new ArrayList<DecorObject>(10);
	public void addDecorObject(String name,ZVector pos)
	{
		mDecorObjects.add(new DecorObject(name,pos));
	}

	private static native void JNIresetGraphics(long obj, long scne);
	public void resetGraphics()
	{
		// effacement des pr�c�dents meshs
		if (null!=mInstGrid) ZActivity.instance.mScene.remove(mInstGrid); mInstGrid=null;
		if (null!=mInstFloor) ZActivity.instance.mScene.remove(mInstFloor); mInstFloor=null;
		if (null!=mInstMud) ZActivity.instance.mScene.remove(mInstMud); mInstMud=null;	
		if (null!=mInstSky) ZActivity.instance.mScene.remove(mInstSky); mInstSky=null;	
		if (null!=mInstWater) ZActivity.instance.mScene.remove(mInstWater); mInstWater=null;	
		if (null!=mInstWater) ZActivity.instance.mScene.remove(mInstWater); mInstWater=null;	
		for (FixedRails r:mFixedRails) r.resetGraphics();	
		for (Node n:mMobileNodes) n.resetGraphics();
		for (Node n:mFixedNodes) n.resetGraphics();
		for (Link l:mLinks) l.resetGraphics();
		if (mTrain!=null) mTrain.resetGraphics();
		for (TestVehicle boat:mBoats)
			boat.resetGraphics();
		// stone
		for (StoneCollumn s:mStoneCollumns)
			s.resetGraphics();
		if (null!=mInstanceDecor) ZActivity.instance.mScene.remove(mInstanceDecor); mInstanceDecor=null;	
		if (null!=mInstanceDecorAlpha) ZActivity.instance.mScene.remove(mInstanceDecorAlpha); mInstanceDecorAlpha=null;	
		if (null!=mInstPlexi) ZActivity.instance.mScene.remove(mInstPlexi); mInstPlexi=null;	
		
		JNIresetGraphics(mNativeObject,ZActivity.instance.mScene.getNativeObject());
		
	}
	
	
	private static final int GRIDSIZE=16;
	private void createGrid()
	{
		// temp: cr�ation d'une grille 
		ZColor col1 = new ZColor(0.5f,0.5f,0.5f,1.f);
		//ZColor col41 = new ZColor(1.f,1.f,1.f,1.f);
		//ZColor col44 = new ZColor(0.f,0.f,1.f,1.f);
		ZPointsMesh pointsMesh = new ZPointsMesh();
		pointsMesh.allocMesh((GRIDSIZE*2+1)*(GRIDSIZE*2+1), true, true);
		for (int x=-GRIDSIZE; x<=GRIDSIZE; x++)
			for (int z=-GRIDSIZE; z<=GRIDSIZE; z++)
			{
				/*if (x%4==0 && z%4==0)
					pointsMesh.addVertex(new ZVector(x,0,z), 3, col44);
				else if (x%4==0 || z%4==0)
					pointsMesh.addVertex(new ZVector(x,0,z), 3, col41);
				else*/
					pointsMesh.addVertex(new ZVector(x,0,z), 1, col1);
			}
		
		mInstGrid = new ZInstance(pointsMesh);
		mInstGrid.setTranslation(0,ReleaseSettings.layer2dGrid,0);
		ZActivity.instance.mScene.add(mInstGrid);		
	}
	
	private static final float GRIDSTEP=2.f;
	private void updateGrid()
	{ 
		// on zoome et on d�place la grille pour coller � la position de cam�ra
		if (mInstGrid==null) return;
		UIeditor2D ui = (UIeditor2D)UI.get();
		float zoom = ui.zoomFactor.get();
		float camPosX = ui.camPosX.get();
		float camPosZ = ui.camPosZ.get();
		float gridZoomFactor = 1.f;
		while (zoom*ZRenderer.getScreenRatioF() > gridZoomFactor*GRIDSIZE) gridZoomFactor *= GRIDSTEP;
		mInstGrid.setScale(gridZoomFactor, 1, gridZoomFactor);
		float x = GRIDSTEP*gridZoomFactor*Math.round(camPosX/(GRIDSTEP*gridZoomFactor));
		float z = GRIDSTEP*gridZoomFactor*Math.round(camPosZ/(GRIDSTEP*gridZoomFactor));
		mInstGrid.setTranslation(x, 0, z);
		
	}

	private static ZVector floorN = new ZVector();
	
	private static native void JNIinitGraphics(long obj,long scene,boolean is3D);
	private static native void JNIsetP6LinkBreakMaterials(long obj, long bars, long jacks, long cables);
	private static native void JNIsetLinksMaterials(long obj, long bars, long jackHead, long jackRetract, long jackExpand, long cables);
	public void initGraphics()
	{
		ReleaseSettings.suspendRendering(true);
		resetGraphics();
		
		if (UI.get()!=null)
		{ 
			if (UI.get().getViewMode() == UI.ViewMode.MODE2D)
			{
				JNIinitGraphics(mNativeObject,ZActivity.instance.mScene.getNativeObject(),false); 
				ZMaterial matBars = ZActivity.instance.mScene.getMaterial("linkbar");
				ZMaterial matJackHead = ZActivity.instance.mScene.getMaterial("jackHead");
				ZMaterial matJackRetract = ZActivity.instance.mScene.getMaterial("jackBottomRetract");
				ZMaterial matJackExpand = ZActivity.instance.mScene.getMaterial("jackBottomExpand");
				ZMaterial matCables = ZActivity.instance.mScene.getMaterial("cable");
				JNIsetLinksMaterials(mNativeObject,matBars.getNativeObject(),matJackHead.getNativeObject(),matJackRetract.getNativeObject(),matJackExpand.getNativeObject(),matCables.getNativeObject());
				if (mTrain!=null) mTrain.initGraphics();
				for (TestVehicle boat:mBoats)
					boat.initGraphics();
				for (Node n:mFixedNodes) n.initGraphics();
				for (Node n:mMobileNodes) n.initGraphics();
				for (Link l:mLinks) l.initGraphics();
				for (FixedRails r:mFixedRails) r.initGraphics(); 
				
				if (UI.get().isEditMode())
					createGrid();
				
				// cr�ation du mesh du dessus (herbe) et du dessous (terre)
				int nbTranches = (int)getXMax()-(int)getXMin()+1;
				ZVector V = new ZVector();
				
				// terre
				ZMesh meshMud = new ZMesh();
				meshMud.allocMesh(nbTranches*2, 2*(nbTranches-1), false);	
				// verts
				for (int i=0; i<nbTranches; i++)
				{
					//front
					V.set(getXMin()+i,ReleaseSettings.layer2dMud,getEditorZMin());
					meshMud.addVertex(V, null, V.get(0)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
					V.set(getXMin()+i,ReleaseSettings.layer2dMud,getFloorHeight(i+getXMin()));
					meshMud.addVertex(V, null, V.get(0)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				}
				// faces
				for (int i=0; i<nbTranches-1; i++)
				{
					//front
					meshMud.addFace(i*2+0,i*2+2,i*2+3);	// 0-2-3 
					meshMud.addFace(i*2+0,i*2+3,i*2+1);	// 0-3-1
				}
				
				meshMud.setMaterial("mud");
				mInstMud = new ZInstance(meshMud);
				ZActivity.instance.mScene.add(mInstMud);
				
				// herbe
				ZMesh meshGrass = new ZMesh();
				meshGrass.allocMesh(nbTranches*2, 2*(nbTranches-1), false);	
				// verts
				for (int i=0; i<nbTranches; i++)
				{
					// vecteur direction du sol= moyenne des deux adjacents...
					if (i==0 || i==nbTranches-1)
						floorN.set(0,0,-1);
					else
						floorN.set(0.5f*(getFloorHeight(i+1+getXMin())-getFloorHeight(i-1+getXMin())),0,-1);
					floorN.normalize();
					
					//front
					V.set(getXMin()+i,ReleaseSettings.layer2dGrass,getFloorHeight(i+getXMin()));
					V.addMul(floorN, ReleaseSettings.floor2Dratio/ReleaseSettings.floorUVscale);
					meshGrass.addVertex(V, null, (float)(getXMin()+i)*ReleaseSettings.floorUVscale, 0.95f);
					V.set(getXMin()+i,ReleaseSettings.layer2dGrass,getFloorHeight(i+getXMin()));
					meshGrass.addVertex(V, null, (float)(getXMin()+i)*ReleaseSettings.floorUVscale, 0);
				}
				// faces
				for (int i=0; i<nbTranches-1; i++)
				{
					//front
					meshGrass.addFace(i*2+0,i*2+2,i*2+3);	// 0-2-3 
					meshGrass.addFace(i*2+0,i*2+3,i*2+1);	// 0-3-1
				}
				
				meshGrass.setMaterial("grass2d");
				mInstFloor = new ZInstance(meshGrass);
				ZActivity.instance.mScene.add(mInstFloor);
				
				// stone
				for (StoneCollumn s:mStoneCollumns)
					s.initGraphics();
				
				// water
				if (hasWater())
				{
					//ZMesh meshWater = new ZMesh();
					//meshWater.createRectangle(getXMin(), getZMin(), getXMax(), getWaterHeight(), getXMin()*ReleaseSettings.waterUVscale, getZMin()*ReleaseSettings.waterUVscale, getXMax()*ReleaseSettings.waterUVscale, -getWaterHeight()*ReleaseSettings.waterUVscale);
					ZMesh meshWater = new ZWaterRect(getXMin(), getZMin(), getXMax(), getWaterHeight(), getXMin()*ReleaseSettings.waterUVscale, getZMin()*ReleaseSettings.waterUVscale, getXMax()*ReleaseSettings.waterUVscale, -getWaterHeight()*ReleaseSettings.waterUVscale, ReleaseSettings.waterResolX, ReleaseSettings.waterResolZ, ReleaseSettings.waterUVamplitude, ReleaseSettings.waterUVfrequency, false);
					meshWater.setMaterial("water");
					mInstWater = new ZInstance(meshWater);
					mInstWater.setRotation(ZQuaternion.constX90);
					mInstWater.setTranslation(0,ReleaseSettings.layer2dWater,0);
					//mInstWater.setColor(new ZColor(0,0.5f,1,1));
					ZActivity.instance.mScene.add(mInstWater);
				}
				
				
				// tower goal
				if (isTowerMode())
				{
					mInstSky = new ZInstance();
					ZMesh m = new ZMesh();
					m.createRectangle(getXMin(), getTowerGoal(), getXMax(), getZMax(), getXMin()*ReleaseSettings.TOWER_SKY_UV_FACTOR, -getTowerGoal()*ReleaseSettings.TOWER_SKY_UV_FACTOR, getXMax()*ReleaseSettings.TOWER_SKY_UV_FACTOR, -getZMax()*ReleaseSettings.TOWER_SKY_UV_FACTOR);
					m.setMaterial("tower_goal_green");
					mInstSky.add(new ZInstance(m));
					m = new ZMesh();
					m.createRectangle(getXMin(), getZMin(), getXMax(), getTowerGoal(), getXMin()*ReleaseSettings.TOWER_SKY_UV_FACTOR, -getZMin()*ReleaseSettings.TOWER_SKY_UV_FACTOR, getXMax()*ReleaseSettings.TOWER_SKY_UV_FACTOR, -getTowerGoal()*ReleaseSettings.TOWER_SKY_UV_FACTOR);
					m.setMaterial("tower_goal_red");
					mInstSky.add(new ZInstance(m));
					
					
					mInstSky.setRotation(ZQuaternion.constX90);
					mInstSky.setTranslation(0, ReleaseSettings.layer2dSky, 0);
					ZActivity.instance.mScene.add(mInstSky);
					
				}

				
			}
			
			if (UI.get().getViewMode()==UI.ViewMode.MODE3D)
			{
				ZMaterial matBarsP6 = ZActivity.instance.mScene.getMaterial("p6_bar");
				ZMaterial matJacksP6 = ZActivity.instance.mScene.getMaterial("p6_jack");
				ZMaterial matCablesP6 = ZActivity.instance.mScene.getMaterial("p6_cable");
				JNIsetP6LinkBreakMaterials(mNativeObject,matBarsP6.getNativeObject(),matJacksP6.getNativeObject(),matCablesP6.getNativeObject());
				
				ZMaterial matBars = ZActivity.instance.mScene.getMaterial("linkbar");
				ZMaterial matJackHead = ZActivity.instance.mScene.getMaterial("jackHead");
				ZMaterial matJackRetract = ZActivity.instance.mScene.getMaterial("jackBottomRetract");
				ZMaterial matJackExpand = ZActivity.instance.mScene.getMaterial("jackBottomExpand");
				ZMaterial matCables = ZActivity.instance.mScene.getMaterial("cable");
				JNIsetLinksMaterials(mNativeObject,matBars.getNativeObject(),matJackHead.getNativeObject(),matJackRetract.getNativeObject(),matJackExpand.getNativeObject(),matCables.getNativeObject());
				JNIinitGraphics(mNativeObject,ZActivity.instance.mScene.getNativeObject(),true);
				if (mTrain!=null) mTrain.initGraphics();
				for (TestVehicle boat:mBoats)
					boat.initGraphics();
				for (Node n:mMobileNodes) n.initGraphics();
				for (Node n:mFixedNodes) n.initGraphics();
				for (Link l:mLinks) l.initGraphics();
				for (FixedRails r:mFixedRails) r.initGraphics();
				
				// cr�ation du mesh du dessus (herbe) et du dessous (terre)
				int nbTranches = (int)getXMax()-(int)getXMin()+1;
				ZVector V = new ZVector();
				ZVector N = new ZVector(0,0,1); 
				ZVector N0 = new ZVector(0,0,1); 
				ZVector N1 = new ZVector(0,0,1); 
				ZVector N2 = new ZVector(0,0,1); 
				ZVector N3 = new ZVector(0,0,1);
				ZVector N4 = new ZVector(0,0,1); 
				ZVector N5 = new ZVector(0,0,1); 
			
				// herbe
				ZMesh meshFloor = new ZMesh();
				meshFloor.allocMesh(nbTranches*6, 10*(nbTranches-1), true);
				// verts
				for (int i=0; i<nbTranches; i++)
				{ 
					// calcul de la normale
					float dx;
					float dzn; // near
					float dzf; // far
					float dzm; // middle
					float dyn, dyf; // near/middle, middle/far
					
					if (getFloorHeight(i+getXMin())==getNearHeight(i+getXMin())) dyn=0; 
					else if (getFloorHeight(i+getXMin())>getNearHeight(i+getXMin())) dyn=-0.5f; else dyn=0.5f;
					
					if (getFloorHeight(i+getXMin())==getFarHeight(i+getXMin())) dyf=0; 
					else if (getFloorHeight(i+getXMin())>getFarHeight(i+getXMin())) dyf=0.5f; else dyf=-0.5f;
					
					if (i==0)
					{
						dx = 1;
						dzn = getNearHeight(1+getXMin())-getNearHeight(getXMin());
						dzf = getFarHeight(1+getXMin())-getFarHeight(getXMin());
						dzm = getFloorHeight(1+getXMin())-getFloorHeight(getXMin());
					} 
					else if (i==nbTranches-1)
					{
						dx = 1;
						dzn = getNearHeight(i+getXMin())-getNearHeight(i-1+getXMin());
						dzf = getFarHeight(i+getXMin())-getFarHeight(i-1+getXMin());
						dzm = getFloorHeight(i+getXMin())-getFloorHeight(i-1+getXMin());
					}
					else
					{
						dx = 2;
						dzn = getNearHeight(i+1+getXMin())-getNearHeight(i-1+getXMin());
						dzf = getFarHeight(i+1+getXMin())-getFarHeight(i-1+getXMin());
						dzm = getFloorHeight(i+1+getXMin())-getFloorHeight(i-1+getXMin());
						dyn*=2;
						dyf*=2;
					}
					N0.set(-dzn,0,dx);		N0.normalize(); // near (border)
					N1.set(-dzn,dyn,dx);	N1.normalize(); // near
					N2.set(-dzm,dyn,dx);	N2.normalize(); // near/middle
					N3.set(-dzm,dyf,dx);	N3.normalize(); // middle/far
					N4.set(-dzf,dyf,dx);	N4.normalize(); // far
					N5.set(-dzf,0,dx);		N5.normalize(); // far (border)
				
					// y0 = ordonn�e du haut de la bosse
					// y1 = ordonn�e du bas de la bosse (haut=1)
					// y2 = ordonn�e du bord
					float y2n = ReleaseSettings.floorExtrudeHalfWidth;
					float y0n = 2.f*ReleaseSettings.STONE_COLLUMN_HALF_WIDTH+ReleaseSettings.BRIDGE_HALF_WIDTH;
					float y1n = Math.max(y0n+1, Math.min(y2n,y0n + getFloorHeight(i+getXMin())-getNearHeight(i+getXMin())));
					float y2f = ReleaseSettings.floorExtrudeHalfWidth;
					float y0f = 2.f*ReleaseSettings.STONE_COLLUMN_HALF_WIDTH+ReleaseSettings.BRIDGE_HALF_WIDTH;
					float y1f = Math.max(y0f+1, Math.min(y2f,y0f + getFloorHeight(i+getXMin())-getFarHeight(i+getXMin())));
					
					
					V.set(getXMin()+i,-y2n,getNearHeight(i+getXMin()));	meshFloor.addVertex(V, N0, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,-y1n,getNearHeight(i+getXMin()));	meshFloor.addVertex(V, N1, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,-y0n,getFloorHeight(i+getXMin()));	meshFloor.addVertex(V, N2, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,y0f,getFloorHeight(i+getXMin()));		meshFloor.addVertex(V, N3, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,y1f,getFarHeight(i+getXMin()));		meshFloor.addVertex(V, N4, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,y2f,getFarHeight(i+getXMin()));		meshFloor.addVertex(V, N5, V.get(0)*ReleaseSettings.floorUVscale, V.get(1)*ReleaseSettings.floorUVscale);
				}
				// faces
				for (int i=0; i<nbTranches-1; i++)
				{
					for (int j=0; j<5; j++)
					{
						meshFloor.addFace(6*i+j,6*i+j+6,6*i+j+7);
						meshFloor.addFace(6*i+j,6*i+j+7,6*i+j+1);
					}
				}
				meshFloor.setMaterial("grass");
				mInstFloor = new ZInstance(meshFloor);
				ZActivity.instance.mScene.add(mInstFloor);
				
				// terre
				ZMesh meshMud = new ZMesh();
				meshMud.allocMesh(nbTranches*4+8, 4+4*(nbTranches-1), true);
				// cot� gauche
				N.set(-1,0,0);
				V.set(getXMin(),ReleaseSettings.floorExtrudeHalfWidth, getZMin()); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMin(),-ReleaseSettings.floorExtrudeHalfWidth, getZMin()); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMin(),ReleaseSettings.floorExtrudeHalfWidth, getNearHeight(getXMin())); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMin(),-ReleaseSettings.floorExtrudeHalfWidth, getNearHeight(getXMin())); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				meshMud.addFace(0,1,3);
				meshMud.addFace(0,3,2);
				// cot� droit
				N.set(1,0,0);
				V.set(getXMax(),ReleaseSettings.floorExtrudeHalfWidth, getZMin()); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMax(),-ReleaseSettings.floorExtrudeHalfWidth, getZMin()); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMax(),ReleaseSettings.floorExtrudeHalfWidth, getNearHeight(getXMax())); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				V.set(getXMax(),-ReleaseSettings.floorExtrudeHalfWidth, getNearHeight(getXMax())); meshMud.addVertex(V, N, V.get(1)*ReleaseSettings.mudUVscale, V.get(2)*ReleaseSettings.mudUVscale);
				meshMud.addFace(4,7,5);
				meshMud.addFace(4,6,7);
				
				// verts
				for (int i=0; i<nbTranches; i++)
				{
					//front
					N.set(0,-1,0);
					V.set(getXMin()+i,-ReleaseSettings.floorExtrudeHalfWidth,getZMin());
					meshMud.addVertex(V, N, V.get(0)*ReleaseSettings.floorUVscale, V.get(2)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,-ReleaseSettings.floorExtrudeHalfWidth,getNearHeight(i+getXMin()));
					meshMud.addVertex(V, N, V.get(0)*ReleaseSettings.floorUVscale, V.get(2)*ReleaseSettings.floorUVscale);
					//rear
					N.set(0,1,0);
					V.set(getXMin()+i,ReleaseSettings.floorExtrudeHalfWidth,getZMin());
					meshMud.addVertex(V, N, V.get(0)*ReleaseSettings.floorUVscale, V.get(2)*ReleaseSettings.floorUVscale);
					V.set(getXMin()+i,ReleaseSettings.floorExtrudeHalfWidth,getFarHeight(i+getXMin()));
					meshMud.addVertex(V, N, V.get(0)*ReleaseSettings.floorUVscale, V.get(2)*ReleaseSettings.floorUVscale);
				}
				// faces
				for (int i=0; i<nbTranches-1; i++)
				{
					//front
					meshMud.addFace(i*4+8,i*4+12,i*4+13);	// 8-12-13
					meshMud.addFace(i*4+8,i*4+13,i*4+9);	// 8-13-9
					//rear
					meshMud.addFace(i*4+14,i*4+10,i*4+15);	// 10-14-15
					meshMud.addFace(i*4+15,i*4+10,i*4+11);	// 10-15-11
				}
				
				meshMud.setMaterial("mud");
				mInstMud = new ZInstance(meshMud);
				ZActivity.instance.mScene.add(mInstMud);

				// stone
				for (StoneCollumn s:mStoneCollumns)
					s.initGraphics();
								
				// water
				if (hasWater())
				{
					/*
					ZMesh meshWater = new ZMesh();
					
					//meshWater.createRectangle(-getXSize()*0.5f, -ReleaseSettings.floorExtrudeHalfWidth, getXSize()*0.5f, ReleaseSettings.floorExtrudeHalfWidth, 0,1,1,0);
					meshWater.allocMesh(6, 4, false);
					V.set(getXMin(),-ReleaseSettings.waterExtrudeHalfWidth,getZMin());			meshWater.addVertex(V, null, getXMin()*ReleaseSettings.waterUVscale, 0);
					V.set(getXMax(),-ReleaseSettings.waterExtrudeHalfWidth,getZMin());			meshWater.addVertex(V, null, getXMax()*ReleaseSettings.waterUVscale, 0);
					V.set(getXMin(),-ReleaseSettings.waterExtrudeHalfWidth,JNIgetWaterHeight(mNativeObject));	meshWater.addVertex(V, null, getXMin()*ReleaseSettings.waterUVscale, (getWaterHeight()-getZMin())*ReleaseSettings.waterUVscale);
					V.set(getXMax(),-ReleaseSettings.waterExtrudeHalfWidth,JNIgetWaterHeight(mNativeObject));	meshWater.addVertex(V, null, getXMax()*ReleaseSettings.waterUVscale, (getWaterHeight()-getZMin())*ReleaseSettings.waterUVscale);
					V.set(getXMin(),ReleaseSettings.waterExtrudeHalfWidth,JNIgetWaterHeight(mNativeObject));	meshWater.addVertex(V, null, getXMin()*ReleaseSettings.waterUVscale, (getWaterHeight()-getZMin()+2.f*ReleaseSettings.waterExtrudeHalfWidth)*ReleaseSettings.waterUVscale);
					V.set(getXMax(),ReleaseSettings.waterExtrudeHalfWidth,JNIgetWaterHeight(mNativeObject));	meshWater.addVertex(V, null, getXMax()*ReleaseSettings.waterUVscale, (getWaterHeight()-getZMin()+2.f*ReleaseSettings.waterExtrudeHalfWidth)*ReleaseSettings.waterUVscale);
					meshWater.addFace(0, 1, 3);
					meshWater.addFace(0, 3, 2);
					meshWater.addFace(2, 3, 5);
					meshWater.addFace(2, 5, 4);
					
					
					
					meshWater.setMaterial("water");
					mInstWater = new ZInstance(meshWater);
					*/
					
					ZMesh meshWaterFront = new ZWaterRect(getXMin(), getZMin(), getXMax(), getWaterHeight()-0.1f, getXMin()*ReleaseSettings.waterUVscale, getZMin()*ReleaseSettings.waterUVscale, getXMax()*ReleaseSettings.waterUVscale, -getWaterHeight()*ReleaseSettings.waterUVscale, ReleaseSettings.waterResolX, ReleaseSettings.waterResolZ, ReleaseSettings.waterUVamplitude, ReleaseSettings.waterUVfrequency, false);
					meshWaterFront.setMaterial("water");
					ZInstance instWaterFront = new ZInstance(meshWaterFront);
					instWaterFront.setRotation(ZQuaternion.constX90);
					instWaterFront.setTranslation(0,-ReleaseSettings.waterExtrudeHalfWidth,0);
					ZMesh meshWaterTop = new ZWaterRect(getXMin(), -ReleaseSettings.waterExtrudeHalfWidth, getXMax(), ReleaseSettings.waterExtrudeHalfWidth, getXMin()*ReleaseSettings.waterUVscale, -ReleaseSettings.waterExtrudeHalfWidth*ReleaseSettings.waterUVscale, getXMax()*ReleaseSettings.waterUVscale, ReleaseSettings.waterExtrudeHalfWidth*ReleaseSettings.waterUVscale, ReleaseSettings.waterResolX, ReleaseSettings.waterResolY, ReleaseSettings.waterUVamplitude, ReleaseSettings.waterUVfrequency, false);
					meshWaterTop.setMaterial("water");
					ZInstance instWaterTop = new ZInstance(meshWaterTop);
					instWaterTop.setTranslation(0,0,getWaterHeight()-0.1f);

					mInstWater = new ZInstance();
					mInstWater.add(instWaterFront);
					mInstWater.add(instWaterTop);
					
					//mInstWater.setColor(new ZColor(0,0.5f,1,0.5f));
					ZActivity.instance.mScene.add(mInstWater);
				}
				
				// TEMP test chargement de meshs
				mInstanceDecor = new ZInstance();
				ZActivity.instance.mScene.add(mInstanceDecor);
				mInstanceDecorAlpha = new ZInstance();
				mInstanceDecorAlpha.setColored(true);	// alpha
				ZActivity.instance.mScene.add(mInstanceDecorAlpha);
				ZInstance i;
				/*
				// wizard tower
				i=MeshBank.get().instanciate("wizard_tower");
				i.setTranslation(-ReleaseSettings.MAX_TRAIN_LENGTH*0.3f,ReleaseSettings.floorExtrudeHalfWidth*0.33f,getDefaultRailsHeight());
				mInstanceDecor.add(i);
				*/
				/*
				// gares
				if (!isTowerMode())
				{
					i=MeshBank.get().instanciate("gare");
					i.setTranslation(getXMin()+ReleaseSettings.MAX_TRAIN_LENGTH*0.5f,0,getDefaultRailsHeight());
					mInstanceDecor.add(i);
						i=MeshBank.get().instanciate("lego");
						i.setTranslation(getXMin()+ReleaseSettings.MAX_TRAIN_LENGTH*0.5f,-ReleaseSettings.floorExtrudeHalfWidth*0.5f,getDefaultRailsHeight());
						mInstanceDecor.add(i);
					i=MeshBank.get().instanciate("gare");
					i.setTranslation(getXMax()-ReleaseSettings.MAX_TRAIN_LENGTH*0.5f,0,getDefaultRailsHeight());
					mInstanceDecor.add(i);
						i=MeshBank.get().instanciate("pen");
						i.setTranslation(getXMax()-ReleaseSettings.MAX_TRAIN_LENGTH*0.5f,-ReleaseSettings.floorExtrudeHalfWidth*0.5f,getDefaultRailsHeight());
						mInstanceDecor.add(i);
				}
				*/
				
				// objets d�coratifs de la maquette
				for (DecorObject obj : mDecorObjects)
				{
					i=MeshBank.get().instanciate(obj.meshName);
					if (i!=null)
					{
						i.setTranslation(obj.position);
						mInstanceDecor.add(i);
					}					
				}
				
				// room
				i=MeshBank.get().instanciate("room");
				i.setTranslation(getXCenter(),0,getZMin());
				mInstanceDecor.add(i);
				i=MeshBank.get().instanciate("room_alpha");
				i.setTranslation(getXCenter(),0,getZMin());
				mInstanceDecorAlpha.add(i);
				i=MeshBank.get().instanciate("room_no_light");
				i.setTranslation(getXCenter(),0,getZMin());
				mInstanceDecor.add(i);
				
				// matte painting
				float vMattePainting = 1;							// v du bas du matte painting
				float zMattePainting = getDefaultRailsHeight()+20;		// haut du matte painting
				float uMattePainting = 2.f*ReleaseSettings.floorExtrudeHalfWidth/(getXMax()-getXMin()+4.f*ReleaseSettings.floorExtrudeHalfWidth);
				ZMesh m = new ZMesh();
				m.allocMesh(12, 6, true);
				// left
				V.set(getXMin(), -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constX, 0, vMattePainting);
				V.set(getXMin(), ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constX, uMattePainting, vMattePainting);
				V.set(getXMin(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constX, 0, 0);
				V.set(getXMin(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constX, uMattePainting, 0);
				m.addFace(0,1,3);
				m.addFace(0,3,2);
				// front
				V.set(getXMin(), ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, uMattePainting, vMattePainting);
				V.set(getXMax(), ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, 1-uMattePainting, vMattePainting);
				V.set(getXMin(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, uMattePainting, 0);
				V.set(getXMax(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, 1-uMattePainting, 0);
				m.addFace(4,5,7);
				m.addFace(4,7,6);
				// right
				V.set(getXMax(), ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constXNeg, 1-uMattePainting, vMattePainting);
				V.set(getXMax(), -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constXNeg, 1, vMattePainting);
				V.set(getXMax(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constXNeg, 1-uMattePainting, 0);
				V.set(getXMax(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constXNeg, 1, 0);
				m.addFace(8,9,11);
				m.addFace(8,11,10);
				m.setMaterial("matte_painting_water");
				mInstanceDecor.add(m);
				// dessus et arri�re du matte painting, dans un autre mat�riau
				float w = 0.5f; // �paisseur du matte-painting.
				float uw = w/(2*w+getXSize());
				float vw = w/(w+2*ReleaseSettings.floorExtrudeHalfWidth);
				m = new ZMesh();
				m.allocMesh(28, 16, true);
				// left front
				V.set(getXMin()-w, -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, 0, vMattePainting);
				V.set(getXMin(), -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, uw, vMattePainting);
				V.set(getXMin()-w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, 0, 0);
				V.set(getXMin(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, uw, 0);
				m.addFace(0,1,3);
				m.addFace(0,3,2);
				// right front
				V.set(getXMax(), -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, 1-uw, vMattePainting);
				V.set(getXMax()+w, -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constYNeg, 1, vMattePainting);
				V.set(getXMax(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, 1-uw, 0);
				V.set(getXMax()+w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constYNeg, 1, 0);
				m.addFace(4,5,7);
				m.addFace(4,7,6);
				// top
				V.set(getXMin()-w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, 0, 1);
				V.set(getXMin(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, uw, 1);
				V.set(getXMin()-w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constZ, 0, 0);
				V.set(getXMin(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, uw, vw);
				V.set(getXMax()+w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constZ, 1, 0);
				V.set(getXMax(), ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, 1-uw, vw);
				V.set(getXMax(), -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, 1-uw, 1);
				V.set(getXMax()+w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constZ, 1, 1);
				m.addFace(8,9,11);
				m.addFace(8,11,10);
				m.addFace(10,11,13);
				m.addFace(10,13,12);
				m.addFace(14,15,12);
				m.addFace(14,12,13);
				// left
				V.set(getXMin()-w, ReleaseSettings.floorExtrudeHalfWidth+w, getZMin());	m.addVertex(V, ZVector.constXNeg, 0, 1);
				V.set(getXMin()-w, -ReleaseSettings.floorExtrudeHalfWidth, getZMin());		m.addVertex(V, ZVector.constXNeg, 1, 1);
				V.set(getXMin()-w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constXNeg, 0, 0);
				V.set(getXMin()-w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constXNeg, 1, 0);
				m.addFace(16,17,19);
				m.addFace(16,19,18);
				// back
				V.set(getXMax()+w, ReleaseSettings.floorExtrudeHalfWidth+w, getZMin());	m.addVertex(V, ZVector.constY, 0,1);
				V.set(getXMin()-w, ReleaseSettings.floorExtrudeHalfWidth+w, getZMin());	m.addVertex(V, ZVector.constY, 1,1);
				V.set(getXMax()+w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constY, 0, 0);
				V.set(getXMin()-w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constY, 1, 0);
				m.addFace(20,21,23);
				m.addFace(20,23,22);
				// right
				V.set(getXMax()+w, -ReleaseSettings.floorExtrudeHalfWidth, getZMin());	m.addVertex(V, ZVector.constX, 0,1);
				V.set(getXMax()+w, ReleaseSettings.floorExtrudeHalfWidth+w, getZMin());	m.addVertex(V, ZVector.constX, 1, 1);
				V.set(getXMax()+w, -ReleaseSettings.floorExtrudeHalfWidth, zMattePainting);	m.addVertex(V, ZVector.constX, 0, 0);
				V.set(getXMax()+w, ReleaseSettings.floorExtrudeHalfWidth+w, zMattePainting);	m.addVertex(V, ZVector.constX, 1, 0);
				m.addFace(24,25,27);
				m.addFace(24,27,26);

				m.setMaterial("matte_painting_back");
				mInstanceDecor.add(m);
				
				// plexi glass in front of the model
				if (ReleaseSettings.SHOW_PLEXYGLASS)
				{
					m = new ZMesh();
					m.setMaterial("plexi");
					m.createBox(new ZVector(w+getXSize()*0.5f,w*0.5f,(getDefaultRailsHeight()+1-getZMin())*0.5f));
					mInstPlexi = new ZInstance(m);
					mInstPlexi.setTranslation(getXCenter(),-ReleaseSettings.floorExtrudeHalfWidth-w*0.5f,getZMin()+(getDefaultRailsHeight()+1-getZMin())*0.5f);
					ZActivity.instance.mScene.add(mInstPlexi);
				}

			}
		}
		
		ReleaseSettings.suspendRendering(false);
			
	}
	
	public void resetSimulation()
	{
		for (Link l:mLinks)
			l.resetSimulation(); // important de faire les links avant les nodes (calcul du poids des nodes)
		for (Node n:mMobileNodes)
			n.resetSimulation();
		if (mTrain!=null)
			mTrain.resetSimulation();
		for (TestVehicle boat:mBoats)
			boat.resetSimulation();
		
		// init de la liste des rails
		mRails.clear();
		for (Rails r:mFixedRails) mRails.add(r);
		for (Link l:mLinks)
			l.listRails(mRails); // important de faire les links avant les nodes (calcul du poids des nodes)
		
		mSimulateTimer = 0;	
		
		setSimulationState(SimulationState.START);
	}
	
	public SmoothVector mCenterOfInterest;
	private float mVerrinAnimPeriod = 0;
	private int mSimulateTimer = 0;
	
	public void update(int elapsedTime,boolean simulate)
	{
		float dt = (float)elapsedTime * 0.001f;
		
		mVerrinAnimPeriod += dt;
		if (mVerrinAnimPeriod>Math.PI)
			mVerrinAnimPeriod -= 2*(float)Math.PI;
		
		if (simulate)
		{
			mSimulateTimer += elapsedTime;
			mSimulationStateTimer += elapsedTime;
			
		//	if (mSimulationState!=SimulationState.FAILED_TRAIN
		//			&& mSimulationState!=SimulationState.FAILED_TRAIN2
		//			&& mSimulationState!=SimulationState.FAILED_BOAT
		//			)
			{
				// update des verrins
				float liftFactor = 0;
				if (mSimulationState==SimulationState.RUNNING_LIFTBRIDGE)
				{
					if (mSimulationStateTimer<ReleaseSettings.HYDRAULIC_PAUSETIMER)
						liftFactor = 0;
					else if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_PAUSETIMER+ReleaseSettings.HYDRAULIC_LIFTTIMER)
						liftFactor = 1;
					else 
						liftFactor = 0.5f-0.5f*(float)Math.cos(Math.PI*(double)(mSimulationStateTimer-ReleaseSettings.HYDRAULIC_PAUSETIMER)/(double)ReleaseSettings.HYDRAULIC_LIFTTIMER);

				}
				if (mSimulationState==SimulationState.RUNNING_LOWERBRIDGE)
				{
					if (mSimulationStateTimer<ReleaseSettings.HYDRAULIC_PAUSETIMER)
						liftFactor = 1;
					else if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_PAUSETIMER+ReleaseSettings.HYDRAULIC_LIFTTIMER)
						liftFactor = 0;
					else 
						liftFactor = 0.5f+0.5f*(float)Math.cos(Math.PI*(double)(mSimulationStateTimer-ReleaseSettings.HYDRAULIC_PAUSETIMER)/(double)ReleaseSettings.HYDRAULIC_LIFTTIMER);
				}
				if (mSimulationState==SimulationState.RUNNING_BOAT
						|| mSimulationState==SimulationState.FAILING_BOAT
						|| mSimulationState==SimulationState.FAILED_BOAT)
					liftFactor = 1;
				for (Link l:mLinks)
					l.setHydraulicLiftFactor(liftFactor);
	
													
				// on update en petits incr�ments de temps
				float subFrameTime = ReleaseSettings.SUBFRAME_TIME_NODE;
				int nbSubFrames = (int)(float)(dt/subFrameTime);
				if (nbSubFrames==0) nbSubFrames = 1;
				
//				for (Node n:mMobileNodes)
//					n.updateSimulation(dt);
				
				for (int i=0; i<nbSubFrames; i++)
				{
					// update du train
					for (Node n:mMobileNodes)
						n.resetDynamicWeight(); // remove previous train weight
					if (mSimulationState==SimulationState.RUNNING_TRAIN 
							|| mSimulationState==SimulationState.RUNNING_TRAIN2
							|| mSimulationState==SimulationState.FAILING_TRAIN
							|| mSimulationState==SimulationState.FAILING_TRAIN2
							|| mSimulationState==SimulationState.FAILED_TRAIN
							|| mSimulationState==SimulationState.FAILED_TRAIN2)
						if (mTrain!=null)
							mTrain.updateSimulation(subFrameTime);
					// update des bateaux
					if (mSimulationState==SimulationState.RUNNING_BOAT
							|| mSimulationState==SimulationState.FAILING_BOAT)
						for (TestVehicle boat:mBoats)
							boat.updateSimulation(subFrameTime);
					
					updateSimulation(subFrameTime);
					
				}
			
			}
			
			
			// update du simulationState
			updateSimulationState(elapsedTime);

			// update du vent
			updateWind();

			
		}
		else
		{
			mCenterOfInterest.set(getXCenter(),0,getDefaultRailsHeight());
		}
		
		mCenterOfInterest.update(elapsedTime);
		
		// graphics update
		updateGraphics(elapsedTime);
	}
	
	private static native void JNIupdateSimulation(long obj, float dt);
	public void updateSimulation(float dt)
	{
		JNIupdateSimulation(mNativeObject,dt);
		/*
		// update des nodes (calcul des vitesses et des positions)
		for (Node n:mMobileNodes)
			n.updateSimulation(dt);
		// update des links (correction des positions)
		for (Link l:mLinks)
			l.startSimulationFrame(dt);
		boolean moreUpdateNeeded = false;
		do
		{
			moreUpdateNeeded = false;
			for (Link l:mLinks)
				moreUpdateNeeded |= l.updateSimulation(dt);
		} while (moreUpdateNeeded);
		 */
		
		// destroy links
		switch (mSimulationState)
		{
		case FAILED_TRAIN:
		case FAILED_TRAIN2:
		case FAILED_BOAT:
		case FAILED_TOWER:
			/*
			mFailedLinkDestroyTimer -= dt;
			if (mFailedLinkDestroyTimer<0.f)
			{
				// explode one link
				// first, list non-broken links
				mLinksToExplode.clear();
				for (Link l:mLinks)
					if (!l.isBroken())
						mLinksToExplode.add(l);
				if (!mLinksToExplode.isEmpty())
				{
					int indexToExplode = (int)((float)mLinksToExplode.size()*(float)Math.random());
					mLinksToExplode.get(indexToExplode).setBroken();
				}
				mFailedLinkDestroyTimer = 0.5f*(float)Math.random(); // d�lai avant la prochaine explosion
			}
			*/
			break;
		}
		
		// splash ?
		for (Link l:mLinks)
			if (l.hasJustSplashed())
				ZActivity.instance.playSnd(GameActivity.sndSplash,1);
		
		// crack ?
		for (Link l:mLinks)
			if (l.hasJustBroken())
				ZActivity.instance.playSnd(GameActivity.sndBreak,0.5f);
		

	}

	public void setPaused(boolean paused)
	{
		mTrain.setPaused(paused || !mTrain.isVisible());
		for (TestVehicle boat:mBoats)
			boat.setPaused(paused || !boat.isVisible());
	}
	
	public void setSimulationState(SimulationState newState)
	{
		if (newState == mSimulationState)
			return;
		mSimulationStateTimer = 0;
		mSimulationState = newState;
		
		BCKLog.i("Level.setSimulationState",newState.toString());
		
		GameActivity.setTrainSoundOn(false);

		switch (newState)
		{
		case START:
			mTrain.setVisible(false);
			mTrain.setPaused(true);
			for (TestVehicle boat:mBoats)
				boat.setVisible(false);
			break;
		case RUNNING_TRAIN:
		case RUNNING_TRAIN2:
			{
				GameActivity.setTrainSoundOn(true);
				mTrain.resetSimulation();
				mTrain.setVisible(true);
				mTrain.setPaused(false);
			}
			break;
		case SUCCEED:
			mTrain.setVisible(false);
			mTrain.setPaused(true);
			for (TestVehicle boat:mBoats)
				boat.setVisible(false);
			ZActivity.instance.mScene.setMenu(new MenuLevelResult(true));
			if (mStatus!=null)
				mStatus.setDone();
			break;
		case FAILED_TRAIN:
		case FAILED_TRAIN2:
		case FAILED_BOAT:
		case FAILED_TOWER:
			mFailedLinkDestroyTimer = 1.f; // d�lai avant la prochaine explosion
			ZActivity.instance.mScene.setMenu(new MenuLevelResult(false));
			break;
		case RUNNING_LIFTBRIDGE:
			{
				mTrain.setVisible(false);
				mTrain.setPaused(true);
				if (hasJacks())
					GameActivity.instance.playSnd(GameActivity.sndHydraulics,1);
			}
			break;
		case RUNNING_BOAT: 
			{
				boolean sndBig = false;
				boolean sndSmall = false;
				for (TestVehicle boat:mBoats)
				{
					boat.setVisible(true);
					if (boat.getClass() == BoatBig.class) sndBig = true;
					if (boat.getClass() == BoatSmall.class) sndSmall = true;
				}
				if (sndBig) GameActivity.instance.playSnd(GameActivity.sndBoatBig,1);
				if (sndSmall) GameActivity.instance.playSnd(GameActivity.sndBoatSmall,1);
			}
			break;
		case RUNNING_LOWERBRIDGE:
			if (hasJacks())
				GameActivity.instance.playSnd(GameActivity.sndHydraulics,1);
			for (TestVehicle boat:mBoats)
				boat.setVisible(false);
			break;

		}

	} 
	
	public void vehicleFailed()	// le v�hicule a �chou� dans sa travers�e !
	{
		switch (mSimulationState)
		{ 
		case RUNNING_TRAIN:
			setSimulationState(SimulationState.FAILING_TRAIN);
			break;
		case RUNNING_TRAIN2:
			setSimulationState(SimulationState.FAILING_TRAIN2);
			break;
		case RUNNING_BOAT:
			setSimulationState(SimulationState.FAILING_BOAT);
			break;
		}
	}
	
	private boolean isTowerObjectiveFailed()
	{
		for (Node n:mMobileNodes)
			if (n.getPositionZ() > getTowerGoal())
				return false;
		return true;
	}
	
	public float getTowerHeight()
	{
		float h = getDefaultRailsHeight();
		for (Node n:mMobileNodes)
			if (n.getPositionZ() > h) 
				h = n.getPositionZ();
		return h;
	}
	
	public float getZSize() 	{ return getTowerGoal()-getDefaultRailsHeight(); }
	
	
	public void setWind(float wind) {JNIsetWind(mNativeObject,wind);}
	public float getWind() {return JNIgetWind(mNativeObject);}
	
	private void updateWind()
	{
		setWind(0);	// par defaut
		if (mSimulationState==SimulationState.TOWER_WINDTEST)
		{
			// valeur de temps comprise entre -1 et +1
			float x = (mSimulationStateTimer*2.f-ReleaseSettings.TOWER_WINDTEST_LENGTH) / ReleaseSettings.TOWER_WINDTEST_LENGTH;
			// amplitude= 0 en -1, 0 et 1, -1 en -0,5 et +1 en +0,5 : un sinus!
			float amplitude = (float)Math.sin(x*Math.PI);
			// ensuite, des oscillations entre 0 et 1
			float oscillations = 0.7f + 0.3f*(float)Math.cos(x*Math.PI*8.f);
			
			setWind(oscillations*amplitude);
		}
	}
	
	private void updateSimulationState(int elapsedTime)
	{
		switch (mSimulationState)
		{
		case START:					// �tat initial, encore aucun v�hicule
			if (isTowerMode()) 
				setSimulationState(SimulationState.TOWER_WINDTEST);
			else if (mSimulationStateTimer>ReleaseSettings.START_PAUSETIMER)
				setSimulationState(SimulationState.RUNNING_TRAIN);
			updateWind();
			break;
		case TOWER_WINDTEST:
			if (isTowerObjectiveFailed())
				setSimulationState(SimulationState.FAILED_TOWER);
			else if (mSimulationStateTimer>ReleaseSettings.TOWER_WINDTEST_LENGTH)
				setSimulationState(SimulationState.TOWER_COUNTDOWN);
			break;
		case TOWER_COUNTDOWN:					// �tat initial, encore aucun v�hicule
			if (isTowerObjectiveFailed())
				setSimulationState(SimulationState.FAILED_TOWER);
			else if (mSimulationStateTimer>ReleaseSettings.TOWER_COUNTDOWN_LENGTH)
				setSimulationState(SimulationState.SUCCEED);
			break;
		case RUNNING_TRAIN:			// premier train
			mCenterOfInterest.copy(mTrain.getPosition());
			if (mTrain.isSuccess())
			{
				if (mBoats.isEmpty())
					setSimulationState(SimulationState.SUCCEED);
				else
					setSimulationState(SimulationState.RUNNING_LIFTBRIDGE);
			}
			break;
		case RUNNING_LIFTBRIDGE:		// pont se l�ve
			if (!hasJacks())
			{
				if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_PAUSETIMER)
					setSimulationState(SimulationState.RUNNING_BOAT);
			}
			else
			{
				float x = 0; 
				for (TestVehicle boat:mBoats)
				{
					x += boat.getPosition().get(0) / (float)mBoats.size();
				}
				mCenterOfInterest.set(x,0,getDefaultRailsHeight());
				// d�verrouillage ?
				{
					int LockTime = mSimulationStateTimer-(int)(0.5f*(float)ReleaseSettings.HYDRAULIC_PAUSETIMER);
					if (LockTime>=0 && LockTime<elapsedTime)
					{
						boolean sndLock = false;
						for (Node n:mMobileNodes)
						{
							n.separateNode(true);
							sndLock |= n.isSeparator();
						}
						if (sndLock) GameActivity.instance.playSnd(GameActivity.sndLock,1);
					}
				}
				
				if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_LIFTTIMER+2*ReleaseSettings.HYDRAULIC_PAUSETIMER)
					setSimulationState(SimulationState.RUNNING_BOAT);
			}
			break;
		case RUNNING_BOAT:			// bateau passe sous le pont
			{
				boolean success = true;
				float x = 0;
				for (TestVehicle boat:mBoats)
				{
					x += boat.getPosition().get(0) / (float)mBoats.size();
					if (!boat.isSuccess())
						success = false;
				}
				mCenterOfInterest.set(x,0,getDefaultRailsHeight());
				
				if (success)
					setSimulationState(SimulationState.RUNNING_LOWERBRIDGE); // temp
			}
			break;
		case RUNNING_LOWERBRIDGE:	// pont s'abaisse
			if (!hasJacks())
			{
				if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_PAUSETIMER)
					setSimulationState(SimulationState.RUNNING_BOAT);
			}
			else
			{
				float x = 0;
				for (TestVehicle boat:mBoats)
				{
					x += boat.getPosition().get(0) / (float)mBoats.size();
				}
	
				// verrouillage ?
				{
					int LockTime = mSimulationStateTimer-(ReleaseSettings.HYDRAULIC_LIFTTIMER+(int)(1.5f*(float)ReleaseSettings.HYDRAULIC_PAUSETIMER));
					if (LockTime>=0 && LockTime<elapsedTime)
					{
						boolean sndLock = false;
						for (Node n:mMobileNodes)
						{
							n.separateNode(false);
							sndLock |= n.isSeparator();
						}
						if (sndLock) GameActivity.instance.playSnd(GameActivity.sndLock,1);
					}
				}
				
				mCenterOfInterest.set(x,0,getDefaultRailsHeight());
				if (mSimulationStateTimer>ReleaseSettings.HYDRAULIC_LIFTTIMER+2*ReleaseSettings.HYDRAULIC_PAUSETIMER)
					setSimulationState(SimulationState.RUNNING_TRAIN2);
			}
			break;
		case RUNNING_TRAIN2:			// second train
			mCenterOfInterest.copy(mTrain.getPosition());
			if (mTrain.isSuccess()) 
				setSimulationState(SimulationState.SUCCEED);
			break;
		case SUCCEED:					// gagn�!
			break;
		case FAILING_TRAIN:
			mCenterOfInterest.copy(mTrain.getPosition());
			if (mSimulationStateTimer > ReleaseSettings.FAILING_TRAIN_TIMER)
				setSimulationState(SimulationState.FAILED_TRAIN);
			break;
		case FAILING_TRAIN2:		
			mCenterOfInterest.copy(mTrain.getPosition());
			if (mSimulationStateTimer > ReleaseSettings.FAILING_TRAIN_TIMER)
				setSimulationState(SimulationState.FAILED_TRAIN2);
			break;
		case FAILING_BOAT:					
			if (mSimulationStateTimer > ReleaseSettings.FAILING_BOAT_TIMER)
				setSimulationState(SimulationState.FAILED_BOAT);
			break;
		case FAILED_TRAIN:					
			break;
		case FAILED_BOAT:					
			break;
		case FAILED_TRAIN2:					
			break;
		}
	}
	
	
	// quel est le node de cet endroit ?
	public Node getNearestNode(float x, float z, float dMax)
	{
		Node nearestNode = null;
		float nearestD = 0; // norme au carr� de la distance du node le plus proche
		for (Node n:mFixedNodes)
		{
			float d = n.getDistance(x, z);
			if (d < nearestD || nearestNode==null)
			{
				nearestNode = n;
				nearestD = d;
			}
		}
		for (Node n:mMobileNodes)
		{
			float d = n.getDistance(x, z);
			if (d < nearestD || nearestNode==null)
			{
				nearestNode = n;
				nearestD = d;
			}
		}
		if (nearestNode!=null && nearestD<dMax)
			return nearestNode;
		return null;
	}
	
	// quel est le node de cet endroit ?
	public Link getNearestLink(float x, float z, float dMax)
	{
		Link nearestLink = null;
		float nearestD = 0; // norme au carr� de la distance du node le plus proche
		for (Link l:mLinks)
		{
			float d = l.getDistance(x,z);
			if (d < nearestD || nearestLink==null)
			{
				nearestLink = l;
				nearestD = d;
			}
		}

		if (nearestLink!=null && nearestD<dMax)
			return nearestLink;
		return null;
	}
	
	public Node getNearestFixedNode(float x, float z)
	{
		Node nearestNode = null;
		float nearestD2 = 0; // norme au carr� de la distance du node le plus proche
		for (Node n:mFixedNodes)
		{
			float xNode = n.getPositionX();
			float zNode = n.getPositionZ();
			float dx = x-xNode;
			float dz = z-zNode;
			float d2 = dx*dx+dz*dz;
			if (d2 < nearestD2 || nearestNode==null)
			{
				nearestNode = n;
				nearestD2 = d2;
			}
		}

		if (nearestNode!=null && nearestD2<0.001f)
			return nearestNode;
		return null;
	}
	
	// noeuds confondus?
	public Node getTwinNode(Node testNode)
	{
		Node nearestNode = null;
		float x = testNode.getPositionX();
		float z = testNode.getPositionZ();
		float nearestD2 = 0; // norme au carr� de la distance du node le plus proche
		for (Node n:mFixedNodes)
			if (n!=testNode)
			{
				float xNode = n.getPositionX();
				float zNode = n.getPositionZ();
				float dx = x-xNode;
				float dz = z-zNode;
				float d2 = dx*dx+dz*dz;
				if (d2 < nearestD2 || nearestNode==null)
				{
					nearestNode = n;
					nearestD2 = d2;
				}
			}
		for (Node n:mMobileNodes)
			if (n!=testNode && !n.isEmpty())
			{
				float xNode = n.getPositionX();
				float zNode = n.getPositionZ();
				float dx = x-xNode;
				float dz = z-zNode;
				float d2 = dx*dx+dz*dz;
				if (d2 < nearestD2 || nearestNode==null)
				{
					nearestNode = n;
					nearestD2 = d2;
				}
			}
		if (nearestNode!=null && nearestD2<=0.0001f)
			return nearestNode;
		return null;
	}
	
	private static native void JNIupdateGraphics(long obj, int elapsedTime);
	@Override
	public void updateGraphics(int elapsedTime) 
	{
		JNIupdateGraphics(mNativeObject,elapsedTime);
		for (Link l:mLinks)	l.updateGraphics(elapsedTime);
		for (Node n:mFixedNodes) n.updateGraphics(elapsedTime);
		for (Node n:mMobileNodes) n.updateGraphics(elapsedTime);
		if (mTrain!=null) mTrain.updateGraphics(elapsedTime);
		for (TestVehicle boat:mBoats) boat.updateGraphics(elapsedTime);
		
		if (UI.get().isEditMode()) updateGrid();
	}
	
	
	// efface les links impossibles, efface les nodes vides, fusione les nodes entre eux...
	// nodes
	private ArrayList<Node> mTempNodesList = new ArrayList<Node>(ReleaseSettings.MAX_MOBILE_NODES);
	// links
	private ArrayList<Link> mTempLinksList = new ArrayList<Link>(ReleaseSettings.MAX_LINKS);
	
	public void fixIntegrity()
	{
		
		// fusion mobile nodes
		for (Node n:mMobileNodes)
			if (!n.isEmpty())
			{
				Node twinNode = getTwinNode(n);
				if (twinNode!=null)
				{
					// on transf�re tout dans twin Node!
					n.fusion(twinNode);
				}
			}		
/*		
		// fusion fixed nodes
		for (Node n:mFixedNodes)
			//if (!n.isEmpty())
			{
				Node twinNode = getTwinNode(n);
				if (twinNode!=null)
				{
					// on transf�re tout dans twin Node!
					n.fusion(twinNode);
					mTempNodesList.add(n);
				}
			}
*/			
		for (Node n:mTempNodesList)
		{
			n.resetGraphics();
			remove(n);
		}
		mTempNodesList.clear();	

		// delete degenerated links (the two nodes are the same)
		for (Link l:mLinks)
			if (l.isDegenerated())
				mTempLinksList.add(l);
		for (Link l:mTempLinksList)
		{
			l.destroy();
			remove(l);
		}
		mTempLinksList.clear();		
		
		// delete twin links
		
		
		// delete empty mobile nodes
		for (Node n:mMobileNodes)
			if (n.isEmpty())
				mTempNodesList.add(n);
		for (Node n:mTempNodesList)
		{
			n.resetGraphics();
			remove(n);
		}
		mTempNodesList.clear();	
		
		updateBudget();
		
	}
	
	public void boatBoxCollision(float x,float z,float hlafWidth,float halfHeight)
	{
		for (Link l:mLinks)
			l.boatBoxCollision(x, z, hlafWidth, halfHeight);
	}
	
	// retourne la liste des "n" liens les plus stress�s (�tir�e ou compress�s)
	public ArrayList<Link> getMostStressedLink(int size)
	{
		ArrayList<Link> list = new ArrayList<Link>(size);
		for (Link l:mLinks)
		{
			if (!l.isBroken())
			{
				float stress = l.getStressFactor();
				if (list.size()==size && stress>list.get(0).getStressFactor()) list.remove(0);	// on vire le lien le moins stress� pour ins�rer le nouveau
				if (list.size()<size)
				{
					boolean inserted = false;
					// on parcourt la liste pour ins�rer le nouveau lien
					for (int i=0; i<list.size() && !inserted; i++)
						if (list.get(i).getStressFactor()>stress)
						{
							list.add(i,l);	// insertion du lien
							inserted=true;
						}
					if (!inserted)
						list.add(l);	// ajout � la fin (lien le plus stress� de la liste!
					
				}
			}
		}
		return list;
	}
	
	
	
}
