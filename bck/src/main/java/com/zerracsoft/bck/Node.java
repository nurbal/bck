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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;

public class Node implements GraphicsObject
{
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	protected native static void JNIcopy(long obj, long other);
	protected native static void JNIfusion(long obj, long target);
	
	protected native static void JNIsetPosition(long obj,float x, float y);
	
	protected native static float JNIgetPositionX(long obj);
	protected native static float JNIgetPositionZ(long obj);
	protected native static float JNIgetResetPositionX(long obj);
	protected native static float JNIgetResetPositionZ(long obj);
	
	protected native static void JNIremoveLink(long obj, long link);

	protected native static void JNIresetDynamicWeight(long obj);
	
	protected native static void JNIresetSimulation(long obj);
	
	protected native static void JNIsetFixed(long obj,boolean fixed);
	protected native static boolean JNIisFixed(long obj);
		
	protected native static void JNIseparateNode(long mNativeObject, boolean separate);
	protected native static boolean JNIisSeparator(long mNativeObject);
	protected native static float JNIgetSeparatorAngle(long mNativeObject);
	protected native static void JNIsetSeparator(long mNativeObject, float angle);
	protected native static void JNIunsetSeparator(long mNativeObject);

	private native static void JNIsetSaveID(long obj, int saveID);
	private native static int JNIgetSaveID(long obj);
	
	private native static int JNIgetNbLinks(long obj);
	private static native long JNIgetLinkNativeObject(long obj, int index);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	//public int mSaveID; 
	public void setSaveID(int saveID) {JNIsetSaveID(mNativeObject,saveID);}
	public int getSaveID() {return JNIgetSaveID(mNativeObject);}
	
	//private ArrayList<Link> mLinks = new ArrayList<Link>(ReleaseSettings.MAX_LINKS);
	public int getNbLinks() {return JNIgetNbLinks(mNativeObject);}

	private Link getLink(int index) 
	{ 
		long nativeObject = JNIgetLinkNativeObject(mNativeObject,index);
		Link l = Level.get().getLinkFromNativeObject(nativeObject);
		if( l==null)
			BCKLog.e("Node.getLink","Link #"+nativeObject+"not found");
		return l; 
	}

	public void setFixed(boolean fixed) {JNIsetFixed(mNativeObject,fixed);}
	public boolean isFixed() {return JNIisFixed(mNativeObject);}
	
	public void setPosition(float x, float z) {JNIsetPosition(mNativeObject,x,z);}
	
	public float getPositionX() {return JNIgetPositionX(mNativeObject);}
	public float getPositionZ() {return JNIgetPositionZ(mNativeObject);}
	public float getResetPositionX() {return JNIgetResetPositionX(mNativeObject);}
	public float getResetPositionZ() {return JNIgetResetPositionZ(mNativeObject);}
	
	// possible editing moves (depending on links)
	public List<ZVector> buildEditPossiblePositions()
	{
		// compute extents to explore
		int xMin = Math.round(JNIgetPositionX(mNativeObject));
		int xMax = Math.round(JNIgetPositionX(mNativeObject));
		int zMin = Math.round(JNIgetPositionZ(mNativeObject));
		int zMax = Math.round(JNIgetPositionZ(mNativeObject));
		for (int i=0; i<getNbLinks(); i++)
		{
			Link l = getLink(i);
			if (l.isEditable())
			{
				Node otherNode = l.getOtherNode(this);
				BCKLog.d("Link.getOtherNode returned","#"+otherNode.mNativeObject);

				int otherX = Math.round(JNIgetPositionX(otherNode.mNativeObject));
				int otherZ = Math.round(JNIgetPositionZ(otherNode.mNativeObject));
				int maxLen = Math.round((float)Math.floor(l.getEditMaxLength()));
				if (maxLen>0)
				{
					xMin = Math.min(xMin, otherX-maxLen);
					xMax = Math.max(xMax, otherX+maxLen);
					zMin = Math.min(zMin, otherZ-maxLen);
					zMax = Math.max(zMax, otherZ+maxLen);
				}
			}
		}
		
		// allocation de la liste, a la taille maxi possible
		ArrayList<ZVector> list = new ArrayList<ZVector>((xMax-xMin+1)*(zMax-zMin+1));	 // temp ! a remplacer par un arrayList statique
		
		// parcours et test de toutes les positions
		for (int x=xMin; x<=xMax; x++)
			for (int z=zMin; z<=zMax; z++)
			{
				ZVector pos= new ZVector(x,0,z);
				boolean possible = true;
				for (int i=0; i<getNbLinks(); i++)
				{
					Link l = getLink(i);
					if (l.isEditable())
						possible &= l.testPossibleNodePosition(this, pos);
				}
				if (possible)
					list.add(pos);
			}
		
		return list;
	}

	
	public Node(float x, float z, boolean fixed)
	{
		mNativeObject = JNIconstructor();

		setFixed(fixed);
		setPosition(x,z);
	}
	public Node(Attributes attributes)
	{
		mNativeObject = JNIconstructor();

		String fixed=attributes.getValue("fixed");
		String id=attributes.getValue("id");
		String x=attributes.getValue("x");
		String z=attributes.getValue("z");
		String separatorAngle=attributes.getValue("separatorAngle");
		if (fixed!=null) setFixed(Boolean.parseBoolean(fixed));
		if (x!=null && z!=null) setPosition(Float.parseFloat(x),Float.parseFloat(z));
		if (id!=null) setSaveID(Integer.parseInt(id));
		if (separatorAngle!=null) setSeparator(Float.parseFloat(separatorAngle));
	}
	
	public Node(Node n)	// constructeur de recopie
	{
		mNativeObject = JNIconstructor();
		JNIcopy(mNativeObject,n.mNativeObject);		
	}
	
	protected Node() {} // prevents use; do not create separation node

	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}  
		
	// fusion de nodes: transfert de tous les links vers un autre
	public void fusion(Node target)
	{
		BCKLog.d("Node.fusion this="+Long.toString(mNativeObject)+" target="+Long.toString(target.mNativeObject),"JNI FUSION");
		JNIfusion(mNativeObject,target.mNativeObject);
	}
	
	public void save(OutputStream out) throws IOException
	{
		String separatorString="";
		if (JNIisSeparator(mNativeObject))
		{
			separatorString = " separatorAngle=\""+Float.toString(JNIgetSeparatorAngle(mNativeObject))+"\"";
		}
		String s = "<node id=\""+Integer.toString(getSaveID())+"\" fixed=\""+Boolean.toString(isFixed())+"\" x=\""+Float.toString(JNIgetPositionX(mNativeObject))+"\" z=\""+Float.toString(JNIgetPositionZ(mNativeObject))+"\""+separatorString+"/>\n";
		out.write(s.getBytes());
	}

	public void removeLink(Link l) { JNIremoveLink(mNativeObject,l.getNativeObject()); }

	public boolean isEmpty()
	{
		return (0==getNbLinks());
	}
/*	
	public float getWeight()
	{
		if (isFixed()) return 0;
		return 0.5f*JNIgetLinksWeight(mNativeObject) + JNIgetDynamicWeight(mNativeObject); // on prend la moitie du poids des barres
	}*/
	
//	public void addWeight(float w) {JNIaddDynamicWeight(mNativeObject,w);}
	public void resetDynamicWeight() { JNIresetDynamicWeight(mNativeObject); }
	
	static ZVector D = new ZVector();
	static ZVector N = new ZVector();
	static ZVector T = new ZVector();
	
//	public void updateSimulation(float dt) {JNIupdateSimulation(mNativeObject,dt);}

//	public boolean isMobile() { return !mFixed; }

//	public void setPosition(ZVector pos) { mPosition.copy(pos); }

//	public ZVector getPosition() { return mPosition; }
//	public ZVector getResetPosition() { return mResetPosition; }

//	public void move(ZVector v) { JNImove(mNativeObject,v.getNativeObject()); }
	
	ZInstance mNodeInstance;
	ZInstance mSeparatorInstance;
	
	@Override
	public void resetGraphics() 
	{
		if (null!=mNodeInstance) ZActivity.instance.mScene.remove(mNodeInstance); mNodeInstance=null;
		if (null!=mSeparatorInstance) ZActivity.instance.mScene.remove(mSeparatorInstance); mSeparatorInstance=null;
	}
	
	@Override
	public void initGraphics() 
	{
		if (UI.get().getViewMode()==UI.ViewMode.MODE2D)
		{
			ZMesh m = new ZMesh();
			m.createRectangle(-0.2f, -0.2f, 0.2f, 0.2f, 0, 0, 1, 1);
			m.setMaterial("node2d");
			mNodeInstance = new ZInstance(m);
			ZActivity.instance.mScene.add(mNodeInstance);
			m = new ZMesh();
			m.createRectangle(-0.8f, -0.4f, 0.8f, 0.4f, 0, 0, 1, 1);
			m.setMaterial("separator");
			mSeparatorInstance = new ZInstance(m);
			ZActivity.instance.mScene.add(mSeparatorInstance);
		}
	}

	public float getDistance(float x, float z) 
	{
		float dx = x-getPositionX();
		float dz = z-getPositionZ();
		return (float)Math.sqrt(dx*dx+dz*dz);
	}
	
	public boolean isOnRailsHeight()
	{
		return (0.1f>Math.abs(getPositionZ()-Level.get().getDefaultRailsHeight()));
	}
	
	static ZColor col = new ZColor();
	@Override
	public void updateGraphics(int elapsedTime) 
	{
		if (mNodeInstance!=null)
		{
			mNodeInstance.setRotation(ZQuaternion.constX90);
			mNodeInstance.setTranslation(getPositionX(),0,getPositionZ());
			mNodeInstance.translate(0, ReleaseSettings.layer2dNode, 0);
		}
		
		if (mSeparatorInstance!=null)
		{
			if (!JNIisSeparator(mNativeObject))
				mSeparatorInstance.setVisible(false);
			else
			{
				mSeparatorInstance.setVisible(true);
				mSeparatorInstance.setRotation(ZQuaternion.constX90);
				mSeparatorInstance.rotate(ZVector.constY, -JNIgetSeparatorAngle(mNativeObject));
				mSeparatorInstance.setTranslation(getPositionX(),0,getPositionZ());
				mSeparatorInstance.translate(0, ReleaseSettings.layer2dNode, 0);
				
			}
		}
		
	}
	
	public void destroy()
	{
		resetGraphics();
		while (!isEmpty())
		{
			Link l = getLink(0);
			l.destroy();
			Level.get().remove(l);
		}
	}
	

	// separation nodes...
	public void setSeparator(float angle) {JNIsetSeparator(mNativeObject,angle);}
	public void unsetSeparator() {JNIunsetSeparator(mNativeObject);}
	
	public boolean isSeparator() { return JNIisSeparator(mNativeObject); }
	public boolean isSeparator0() { return JNIisSeparator(mNativeObject) && Math.abs(JNIgetSeparatorAngle(mNativeObject)-0)<0.001f; }
	public boolean isSeparator45() { return JNIisSeparator(mNativeObject) && Math.abs(JNIgetSeparatorAngle(mNativeObject)-0.25f*(float)Math.PI)<0.001f; }
	public boolean isSeparator90() { return JNIisSeparator(mNativeObject) && Math.abs(JNIgetSeparatorAngle(mNativeObject)-0.5f*(float)Math.PI)<0.001f; }
	public boolean isSeparator135() { return JNIisSeparator(mNativeObject) && Math.abs(JNIgetSeparatorAngle(mNativeObject)-0.75f*(float)Math.PI)<0.001f; }
	
	public void separateNode(boolean separate) {JNIseparateNode(mNativeObject,separate);}
	
	public void resetSimulation() {JNIresetSimulation(mNativeObject);}	
	

}
