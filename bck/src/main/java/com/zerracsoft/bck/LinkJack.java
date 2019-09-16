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

import org.xml.sax.Attributes;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZQuaternion;
import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.bck.Level.Backup;

public class LinkJack extends Link 
{
	protected native static long JNIconstructor();

	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
	// 2 instances: nez et cul de verrin
	ZInstance mInstanceParent;
		ZInstance mInstancesBottom;
		ZInstance mInstancesHead;
	
	// type: retactant ou expansif ?
	enum Type
	{
		EXTEND,
		RETRACT
	}
	Type mType = Type.EXTEND;

	public LinkJack(Node n1, Node n2, Type type)
	{
		mNativeObject = JNIconstructor();
		init(n1,n2);
		mType = type;
	}
	public LinkJack(Link l, Type type)
	{
		mNativeObject = JNIconstructor();
		JNIcopy(mNativeObject,l.getNativeObject());
		mType = type;
	}
	
	public LinkJack(Level level,Attributes attributes) throws SaveFileException
	{
		mNativeObject = JNIconstructor();
		
		String node1id=attributes.getValue("node1");
		String node2id=attributes.getValue("node2");
		String type=attributes.getValue("type");
		if (node1id==null || node2id==null) throw new SaveFileException();
		Node Node1 = level.getNode(Integer.parseInt(node1id));
		Node Node2 = level.getNode(Integer.parseInt(node2id));
		if (Node1==null || Node2==null) throw new SaveFileException();
		init (Node1,Node2);
		if (type!=null && type.equals(Type.RETRACT.toString()))
			mType = Type.RETRACT;
		else 
			mType = Type.EXTEND;
	}

	public void save(OutputStream out) throws IOException
	{
		String s = "<linkjack node1=\""+Integer.toString(getNodeSaveID(0))+"\" node2=\""+Integer.toString(getNodeSaveID(1))+"\" type=\""+mType.toString()+"\"/>\n";
		out.write(s.getBytes());
	}
			
	static ZVector v = new ZVector();
	@Override
	public void initGraphics() 
	{
		resetGraphics();
		if (UI.get().getViewMode() == UI.ViewMode.MODE2D)
		{
			ZMesh mesh = new ZMesh();
			mesh.createCylinder(8, 0.2f, 0.5f);
			if (mType==Type.RETRACT)
				mesh.setMaterial("jackBottomRetract");
			else
				mesh.setMaterial("jackBottomExpand");
			ZInstance inst = new ZInstance(mesh); inst.setTranslation(0.5f, 0, 0); inst.setRotation(ZQuaternion.constY270);
			mInstancesBottom = new ZInstance(inst);
			mesh = new ZMesh();
			mesh.createCylinder(8, 0.1f, 0.5f);
			mesh.setMaterial("verrinHead");
			inst = new ZInstance(mesh); inst.setTranslation(0.5f, 0, 0); inst.setRotation(ZQuaternion.constY270);
			mInstancesHead = new ZInstance(inst);
			mInstanceParent = new ZInstance();
			mInstanceParent.add(mInstancesHead);
			mInstanceParent.add(mInstancesBottom);
			ZActivity.instance.mScene.add(mInstanceParent);
		}
		if (UI.get().getViewMode() == UI.ViewMode.MODE3D)
		{
			ZMesh mesh = new ZMesh();
			mesh.createCylinder(8, 0.2f, 0.5f);
			if (mType==Type.RETRACT)
				mesh.setMaterial("jackBottomRetract");
			else
				mesh.setMaterial("jackBottomExpand");
			ZInstance inst = new ZInstance(mesh); inst.setTranslation(0.5f, ReleaseSettings.BRIDGE_HALF_WIDTH, 0);  inst.setRotation(ZQuaternion.constY270);
			mInstancesBottom = new ZInstance(inst);
			inst = new ZInstance(mesh); inst.setTranslation(0.5f, -ReleaseSettings.BRIDGE_HALF_WIDTH, 0);  inst.setRotation(ZQuaternion.constY270);
			mInstancesBottom.add(inst);
			mesh = new ZMesh();
			mesh.createCylinder(8, 0.1f, 0.5f);
			mesh.setMaterial("verrinHead");
			inst = new ZInstance(mesh); inst.setTranslation(0.5f, ReleaseSettings.BRIDGE_HALF_WIDTH, 0);  inst.setRotation(ZQuaternion.constY270);
			mInstancesHead = new ZInstance(inst);
			inst = new ZInstance(mesh); inst.setTranslation(0.5f, -ReleaseSettings.BRIDGE_HALF_WIDTH, 0);  inst.setRotation(ZQuaternion.constY270);
			mInstancesHead.add(inst);
			mInstanceParent = new ZInstance();
			mInstanceParent.add(mInstancesHead);
			mInstanceParent.add(mInstancesBottom);
			ZActivity.instance.mScene.add(mInstanceParent);
		}
	}

	@Override
	public void resetGraphics() 
	{
		if (null!=mInstanceParent) ZActivity.instance.mScene.remove(mInstanceParent); mInstanceParent=null;
		mInstancesHead=null;
		mInstancesBottom=null;
	}

	static ZVector D = new ZVector();
	static ZVector D1 = new ZVector();
	static ZVector D2 = new ZVector();
	
	private static native void JNIsetDynamicLengthFactor(long obj, float factor);
	@Override
	public void setHydraulicLiftFactor(float f)
	{
		switch (mType)
		{
		case EXTEND:
			JNIsetDynamicLengthFactor(getNativeObject(), 1 + ReleaseSettings.HYDRAULIC_EXTEND_FACTOR * f);
			break;
		case RETRACT:
			JNIsetDynamicLengthFactor(getNativeObject(), 1 - ReleaseSettings.HYDRAULIC_RETRACT_FACTOR * f);
			break;
		}
	}




	static ZQuaternion q = new ZQuaternion();
	static ZColor col = new ZColor();
	@Override
	public void updateGraphics(int elapsedTime) 
	{
		if (UI.get().getViewMode() == UI.ViewMode.MODE2D && mInstanceParent!=null && mInstancesHead!=null && mInstancesBottom!=null)
		{
			mInstanceParent.setRotation(ZQuaternion.constZero);
			mInstanceParent.rotate(ZVector.constY,-getAngle());
			mInstanceParent.setTranslation(getNodeX(0),0,getNodeZ(0));
			mInstanceParent.translate(0, ReleaseSettings.layer2dLink, 0);
			mInstancesHead.setScale(getLength(),1,1); 
			if (mType==Type.EXTEND)
				mInstancesBottom.setScale(getNominalLength()*0.8f,1,1); 
			else
				mInstancesBottom.setScale(getNominalLength()*0.5f,1,1); 
			
			mInstanceParent.setVisible(!isBroken());
		}
		if (UI.get().getViewMode() == UI.ViewMode.MODE3D && mInstanceParent!=null && mInstancesHead!=null && mInstancesBottom!=null)
		{
			mInstanceParent.setRotation(ZQuaternion.constZero);
			mInstanceParent.rotate(ZVector.constY,-getAngle());
			mInstanceParent.setTranslation(getNodeX(0),0,getNodeZ(0));
			mInstancesHead.setScale(getLength(),1,1); 
			if (mType==Type.EXTEND)
				mInstancesBottom.setScale(getNominalLength()*0.8f,1,1); 
			else
				mInstancesBottom.setScale(getNominalLength()*0.5f,1,1); 
			
			mInstanceParent.setVisible(!isBroken());
		}
	}

	@Override
	public void resetSimulation()
	{
		super.resetSimulation();
		JNIsetDynamicLengthFactor(getNativeObject(), 1);
	}
	
	@Override
	public float getEditMaxLength() 
	{
		return ReleaseSettings.HYDRAULIC_MAX_LENGTH;
	}
	

	@Override
	public void listRails(ArrayList<Rails> list) {}


	@Override
	public Link createBackup(Backup b) { return new LinkJack(b.getNode(getNodeSaveID(0)),b.getNode(getNodeSaveID(1)),mType); }

	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void updateBudget(StatusLevel s) { s.mSpentHydraulic++; }
}
