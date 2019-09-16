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

import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.bck.Level.Backup;

public class LinkBar extends Link implements Rails  
{
	protected native static long JNIconstructor();
	private native static boolean JNIisRails(long obj);
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
	
//	protected boolean mRails = false;
	
	public LinkBar(Node n1, Node n2)
	{
		mNativeObject = JNIconstructor();
		init(n1,n2);
	}
	
	public LinkBar(Link l)
	{
		mNativeObject = JNIconstructor();
		JNIcopy(mNativeObject,l.getNativeObject());
	}
	
	public LinkBar(Level level,Attributes attributes) throws SaveFileException
	{
		mNativeObject = JNIconstructor();
		
		String node1id=attributes.getValue("node1");
		String node2id=attributes.getValue("node2");
		if (node1id==null || node2id==null) throw new SaveFileException();
		Node Node1 = level.getNode(Integer.parseInt(node1id));
		Node Node2 = level.getNode(Integer.parseInt(node2id));
		if (Node1==null || Node2==null) throw new SaveFileException();
		init (Node1,Node2);

	}
	
	protected LinkBar() {}
	

	public void save(OutputStream out) throws IOException
	{
		String s = "<linkbar node1=\""+Integer.toString(getNodeSaveID(0))+"\" node2=\""+Integer.toString(getNodeSaveID(1))+"\"/>\n";
		out.write(s.getBytes());
	}
		
	
	private static native void JNIaddDynamicWeight(long obj, float x, float w);
	@Override
	public void addWeight(float x, float w) {JNIaddDynamicWeight(getNativeObject(),x,w);}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// DEMARCATION LINE ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	static ZVector D = new ZVector();


	@Override
	public void resetGraphics() {}
	
	@Override
	public void initGraphics() {}

	@Override
	public void updateGraphics(int elapsedTime) {}
	
	@Override
	public void destroy() {
		super.destroy();
		if (JNIisRails(mNativeObject))
			Level.get().remove((Rails)this);
	}

	@Override
	public float getEditMaxLength() { return ReleaseSettings.BAR_MAX_LENGTH; }
	
	static ZVector P = new ZVector();
	static ZVector T = new ZVector();
	public boolean getRailsNearestPoint(ZVector inPoint,float inMaxRay,Proximity out)
	{
		if (isBroken()) return false;
		if (inPoint.get(0)+inMaxRay<Math.min(getNodeX(0), getNodeX(1))) return false;
		if (inPoint.get(0)-inMaxRay>Math.max(getNodeX(0), getNodeX(1))) return false;
		
		//T.sub(mNodes[1].getPosition(),mNodes[0].getPosition()); // T = p0->p1 (non normalise encore)
		getD(T);
		float l = T.norme(); // l=longueur du segment
		T.mul(1.f/l);	// T normalise
		// vecteur D distance p0->inPoint, abscisse x sur outTangent [0..l] 
		D.set(inPoint.get(0)-getNodeX(0),inPoint.get(1),inPoint.get(2)-getNodeZ(0));
		float x = Math.max(0,Math.min(l, D.dot(T)));
		// vecteur P point le plus proche, D distance P->inPoint, d norme de D
		//P.mulAddMul(mNodes[0].getPosition(),1,T,x);
		P.set(getNodeX(0) + T.get(0)*x, T.get(1)*x, getNodeZ(0) + T.get(2)*x);
		D.sub(inPoint,P);
		
		out.distance = D.norme();
		out.normal.mul(D,1.f/out.distance);
		out.tangent.copy(T);
		out.point.copy(P);
		out.rails = this;
		return true;
	}


	
	
	@Override 
	public void listRails(ArrayList<Rails> list) 
	{
		if (JNIisRails(mNativeObject))
			list.add(this);
	}

	@Override
	public ZVector getRailsDirection() 
	{
		return getDirection();
	}

	@Override
	public void setHydraulicLiftFactor(float f) { }


	@Override
	public Link createBackup(Backup b) { return new LinkBar(b.getNode(getNodeSaveID(0)),b.getNode(getNodeSaveID(1))); }

	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void updateBudget(StatusLevel s) { s.mSpentBar++; }

}
