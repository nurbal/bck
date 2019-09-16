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

import com.zerracsoft.Lib3D.ZVector;
import com.zerracsoft.bck.Level.SimulationState;

public abstract class Link implements GraphicsObject
{
	protected native static void JNIdestructor(long obj);
	
	private static native void JNIinit(long obj, long node1, long node2);
	protected static native void JNIcopy(long obj, long src);
	
	public abstract long getNativeObject();
	
	public abstract void updateGraphics(int timeIncrement);  
	
	private boolean mUnderwater = false;
	private boolean mBroken = false;
	private static native void JNIresetSimulation(long obj);
	public void resetSimulation() 
	{
		JNIresetSimulation(getNativeObject());
		mUnderwater = JNIisUnderwater(getNativeObject());
	}

	private static native boolean JNIisBroken(long obj);
	public boolean isBroken() {return JNIisBroken(getNativeObject());}
	
	private static native boolean JNIisUnderwater(long obj);
	public boolean hasJustSplashed() 
	{
		if (!isBroken() && !mUnderwater && JNIisUnderwater(getNativeObject()))
		{
			mUnderwater = true;
			return true;
		}
		//mUnderwater = JNIisUnderwater(getNativeObject());
		return false;
	}
	
	private boolean alreadyBroken = false;
	public boolean hasJustBroken()
	{
		if (!alreadyBroken && isBroken())
		{
			alreadyBroken = true;
			return true;
		}
		alreadyBroken = isBroken();
		return false;
	}
	
	private static native float JNIgetStressFactor(long obj);
	public float getStressFactor() {return JNIgetStressFactor(getNativeObject());}

	private static native void JNIsetBroken(long obj);
	public void setBroken() {JNIsetBroken(getNativeObject()); UI.get().shakeCamera();}
	
	private static native float JNIgetWeight(long obj);
	public float getWeight() {return JNIgetWeight(getNativeObject());}
	
//	private static native void JNIstartSimulationFrame(long obj, float dt);
//	private static native boolean JNIupdateSimulation(long obj, float dt);
//	public void startSimulationFrame(float dt) {JNIstartSimulationFrame(getNativeObject(),dt);}	
/*	public boolean updateSimulation(float dt) 
	{
		boolean result = JNIupdateSimulation(getNativeObject(),dt);
		if (!mUnderwater && isUnderwater())
		{
			mUnderwater = true;
			GameActivity.instance.playSnd(GameActivity.sndSplash);
		}
		return result;
	}	// dt = total frame time. returns true if more updates necessary (stiffness-dependant)
*/	
	
	
	public abstract Link createBackup(Level.Backup b);
	
	public abstract void updateBudget(StatusLevel s);

	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(getNativeObject()); 
		super.finalize();
	}  
	
// Nodes
// references en double: en jave ET en C++
// double reference tres utile pour les fonctions d'edition en java...
//	private Node[] mNodes = new Node[2];
	protected void init(Node n1, Node n2)
	{
		JNIinit(getNativeObject(), n1.getNativeObject(), n2.getNativeObject()); // init cote C++ !
	}
	
	private static native long JNIgetNodeNativeObject(long obj, int index);
	private Node getNode(int index) 
	{ 
		long nativeObject = JNIgetNodeNativeObject(getNativeObject(),index);
		Node n = Level.get().getNodeFromNativeObject(nativeObject);
		if( n==null)
			BCKLog.e("Link.getNode","Node #"+nativeObject+"not found");
		return n; 
	}
	
	private static native float JNIgetNodeX(long obj, int index);
	private static native float JNIgetNodeZ(long obj, int index);
	public float getNodeX(int index) {return JNIgetNodeX(getNativeObject(),index);}
	public float getNodeZ(int index) {return JNIgetNodeZ(getNativeObject(),index);}

	private static native int JNIgetNodeSaveID(long obj, int index);
	public int getNodeSaveID(int index) {return JNIgetNodeSaveID(getNativeObject(),index);}
	
	public Node getOtherNode(Node n) 
	{
		BCKLog.d("Link.getOtherNode","Node #"+n.mNativeObject);

		if (n==getNode(0))
			return getNode(1);
		return getNode(0);
	}
	public boolean isDegenerated() { return (getNode(0)==getNode(1)); }

	private static native float JNIgetLength(long obj);
	public float getLength() { return JNIgetLength(getNativeObject()); }

	private static native float JNIgetNominalLength(long obj);
	public float getNominalLength() { return JNIgetNominalLength(getNativeObject()); }

	
	
	public void getD(ZVector d) 
	{
		d.set(getNodeX(1)-getNodeX(0), 0, getNodeZ(1)-getNodeZ(0));
	}
	
	public float getAngle() // returns angle between node 0 and node 1 
	{
		//D.sub(mNodes[1].getPosition(),mNodes[0].getPosition());
		getD(D);
		D.normalize();
		float x=D.get(0);
		float z=D.get(2);
		if (Math.abs(z)<0.00001f)
		{
			if (x>0) 
				return 0;
			else
				return (float)Math.PI;
		}
		float angle = (float)Math.acos(x);
		if (z>0) 
			return angle;
		else
			return -angle;
	}
	
	public float getDistance(float x, float z) 
	{
		// calcul du point le plus proche sur le segment
		// A = premier point
		// B = premier point
		// ab = norme
		// U = vecteur unitaire
		// p = coordonnee du point teste sur AB
		// P = point du segment le plus proche
		float Ax = getNode(0).getPositionX();
		float Az = getNode(0).getPositionZ();
		float Bx = getNode(1).getPositionX();
		float Bz = getNode(1).getPositionZ();
		float ABx = Bx-Ax;
		float ABz = Bz-Az;
		float ab = (float)Math.sqrt((Bx-Ax)*(Bx-Ax) + (Bz-Az)*(Bz-Az));
		float Ux = ABx/ab;
		float Uz = ABz/ab;
		
		float p = Ux*(x-Ax) + Uz*(z-Az);
		if (p<0) p=0; else if (p>ab) p=ab;
		float Px = Ax+p*Ux;
		float Pz = Az+p*Uz;
		
		return (float)Math.sqrt((x-Px)*(x-Px)+(z-Pz)*(z-Pz));
	}
	public float getMiddleDistance(float x, float z) 
	{
		// distance avec le centre du segment
		ZVector center = getPosition();
		float dx = x-center.get(0);
		float dz = z-center.get(2);
		return (float)Math.sqrt(dx*dx+dz*dz);

	}	
	public abstract void listRails(ArrayList<Rails> list);
	public abstract void setHydraulicLiftFactor(float f);	// 0=normal, 1=lift bridge

	private static ZVector D = new ZVector(); 
	public ZVector getDirection() 
	{
		getD(D);
		D.normalize();
		if (D.get(0)<0) D.mul(-1);
		return D;
	}	
	
	public abstract float getEditMaxLength();
	public abstract boolean isEditable();
	
	static ZVector tempV = new ZVector();
	public boolean testPossibleNodePosition(Node n, ZVector position) 
	{
		tempV.set(getOtherNode(n).getPositionX()-position.get(0),0,getOtherNode(n).getPositionZ()-position.get(2));
		//tempV.sub(getOtherNode(n).getPosition(),position);
		return (tempV.norme()<getEditMaxLength());
	}
	
	static ZVector pos = new ZVector();
	public ZVector getPosition() 
	{
		pos.set((getNodeX(0)+getNodeX(1))*0.5f,0,(getNodeZ(0)+getNodeZ(1))*0.5f);
		//pos.mulAddMul(getNode(0).getPosition(), 0.5f, getNode(1).getPosition(), 0.5f);
		return pos;
	}
	
	public void boatBoxCollision(float x, float z, float halfWidth, float halfHeight) 
	{
		float x1 = getNodeX(0);
		float x2 = getNodeX(1);
		float z1 = getNodeZ(0);
		float z2 = getNodeZ(1);
		if (x2<x1)
		{
			float f = x2; x2=x1; x1=f;
			f = z2; z2=z1; z1=f;
		}
		float dx = x2-x1;
		float dz = z2-z1;
		// cas 1: tout au-dessus ou en dessous
		if (Math.min(z1,z2)>z+halfHeight) return;
		if (Math.max(z1,z2)<z-halfHeight) return;
		// cas 2: tout a droite ou a gauche
		if (x1>x+halfWidth) return;
		if (x2<x-halfWidth) return;
		// cas 3: un point a l'interieur
		boolean collision = false;
		if (Math.abs(x1-x)<halfWidth && Math.abs(z1-z)<halfHeight) collision = true;
		if (Math.abs(x2-x)<halfWidth && Math.abs(z2-z)<halfHeight) collision = true;
		// cas 4: calcul des intersections
		// on part de (x1,z1) et on calcule alpha tel que x = x1+alpha*dx et z = z1+alpha*dz
		// si alpha est compris entre 0 et 1, c'est sur le segment
		float alpha;
				
		if (!collision && Math.abs(dx)>0.001f)
		{
			// collision a gauche
			alpha = ((x-halfWidth)-x1) / dx;
			if (0<alpha && alpha<1 && Math.abs(z- (z1+alpha*dz))<halfHeight) collision = true;
			// collision a droite
			alpha = ((x+halfWidth)-x1) / dx;
			if (0<alpha && alpha<1 && Math.abs(z- (z1+alpha*dz))<halfHeight) collision = true;
		}
		if (!collision && Math.abs(dz)>0.001f)
		{
			// collision en bas
			alpha = ((z-halfHeight)-z1) / dz;
			if (0<alpha && alpha<1 && Math.abs(x- (x1+alpha*dx))<halfWidth) collision = true;
			// collision en haut
			alpha = ((z+halfHeight)-z1) / dz;
			if (0<alpha && alpha<1 && Math.abs(x- (x1+alpha*dx))<halfWidth) collision = true;
		}
	
		// collision?
		if (collision)
		{
			setBroken();
			Level.get().setSimulationState(SimulationState.FAILING_BOAT);
		}
	}	
	
	private static native void JNIreplaceNode(long obj, long from, long to, boolean recomputeInitState);
	public void replaceNode(Node from, Node to, boolean recomputeInitState) { JNIreplaceNode(getNativeObject(), from.getNativeObject(), to.getNativeObject(),recomputeInitState); }	

	// unlinks from nodes, destroy graphics, etc.
	public void destroy() {
		if (getNode(0)!=null)
			getNode(0).removeLink(this);
		if (getNode(1)!=null)
			getNode(1).removeLink(this);
		resetGraphics();
	}

	public abstract void save(OutputStream out) throws IOException;
}
