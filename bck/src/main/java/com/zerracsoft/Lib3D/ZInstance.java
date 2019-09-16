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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ZInstance implements ZObject
{
	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	protected native static void JNIsetVisible(long obj,boolean visible);
	protected native static boolean JNIisVisible(long obj);
	protected native static void JNIsetColor(long obj,long color);
	protected native static float JNIgetAlpha(long obj);
	protected native static void JNIsetColored(long obj,boolean colored);
	protected native static boolean JNIisColored(long obj);
	protected native static void JNIaddObject(long inst,long obj);
	protected native static void JNIresetObjects(long inst);
	protected native static void JNIsetTranslation(long inst,long v);
	protected native static void JNIsetTranslation2(long inst,float x,float y,float z);
	protected native static void JNIsetScale(long inst,long v);
	protected native static void JNIsetScale2(long inst,float x,float y,float z);
	protected native static void JNIsetRotation(long inst,long q);
	protected native static void JNItranslate(long inst,long v);
	protected native static void JNItranslate2(long inst,float x,float y,float z);
	protected native static void JNIrotate(long inst,long q);
	protected native static void JNIrotate2(long inst,long v,float angle);
	protected native static void JNIrotate3(long inst,long v);
	
	public String mName;
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject()
	{
		return mNativeObject;
	}

	public ZInstance()				{ mNativeObject = JNIconstructor(); }   
	public ZInstance(ZObject obj)	{ mNativeObject = JNIconstructor(); add(obj); }
	
	public void add(ZObject obj)	{ JNIaddObject(mNativeObject,obj.getNativeObject()); }
	public void set(ZObject obj)	{ JNIresetObjects(mNativeObject); JNIaddObject(mNativeObject,obj.getNativeObject()); }
	
	@Override
	protected void finalize() throws Throwable {
		JNIdestructor(mNativeObject); 
		super.finalize();
	}  

	public void setVisible(boolean visible)	{JNIsetVisible(mNativeObject,visible);}
	public boolean isVisible()	{return JNIisVisible(mNativeObject);}
	
	public void setColor(ZColor color)	{JNIsetColor(mNativeObject,color.getNativeObject());}
	public float getAlpha()	{return JNIgetAlpha(mNativeObject);}
	public void setColored(boolean colored)	{JNIsetColored(mNativeObject,colored);}
	public boolean isColored()	{return JNIisColored(mNativeObject);}
	
	public void setTranslation(ZVector v)	{JNIsetTranslation(mNativeObject,v.getNativeObject());}
	public void setTranslation(float x,float y,float z)	{JNIsetTranslation2(mNativeObject,x,y,z);}
	public void setScale(ZVector v)			{JNIsetScale(mNativeObject,v.getNativeObject());}
	public void setScale(float x,float y,float z)	{JNIsetScale2(mNativeObject,x,y,z);}
	public void setRotation(ZQuaternion q)	{JNIsetRotation(mNativeObject,q.getNativeObject());}
	
	public void translate(ZVector v)				{JNItranslate(mNativeObject,v.getNativeObject());}
	public void translate(float x,float y,float z)	{JNItranslate2(mNativeObject,x,y,z);}
	public void rotate(ZQuaternion q)				{JNIrotate(mNativeObject,q.getNativeObject());}
	public void rotate(ZVector v,float angle)		{JNIrotate2(mNativeObject,v.getNativeObject(),angle);}
	public void rotate(ZVector v)					{JNIrotate3(mNativeObject,v.getNativeObject());}
	
	
	
	// chargement d'un mesh a partir d'un fichier
	public static final int LOADFLAG_DEFAUT		= 0;
	public static final int LOADFLAG_NO_NORMAL	= 1;
	
	public void load(InputStream input, int loadFlags) throws IOException
	{
		// file format:
		
		// nb geometries (int)
		// geometries[]
		
			// for each geometry/mesh:
			// name (UTF)
			// nb meshes
		
				// for each mesh:
				// flags (int)
				// material (UTF)
				// nb verts
				// verts[] (3*FLOATS)
				// normals[] (if flag is set) (3*FLOATS)
				// UVs[] (if flag is set) (2*FLOATS)
				// nb faces
				// faces[] (3*int)
		
		// nb instances
		// instances[]
		
			// for each instance:
			// instance name (String)
			// nb meshes
			// meshes names
			// rotation (4*float) (quaternion)
			// scale (3*float)
			// translation (3*float)
			// nb sub-instances
			// sub-instances[]
				// ..
		
		DataInputStream dis = new DataInputStream(input);
		int nbGeoms = dis.readInt();
		ZInstance[] geoms = new ZInstance[nbGeoms];
		for (int g=0; g<nbGeoms; g++)
		{
			// create mesh
			geoms[g] = new ZInstance();
			geoms[g].mName = dis.readUTF();
			
			int nbMeshes = dis.readInt();
			for (int m=0; m<nbMeshes; m++)
			{			
				// load mesh
				int flags = dis.readInt();
				String material = dis.readUTF();
				boolean bNormals = (flags&1)!=0;
				boolean bUVs = (flags&2)!=0;
				int nbVerts = dis.readInt();
				ZVector[] verts = new ZVector[nbVerts];
				ZVector[] norms = new ZVector[nbVerts];
				float[] UVs = new float[nbVerts*2];
				for (int v=0; v<nbVerts; v++)
					verts[v] = new ZVector(dis.readFloat(),dis.readFloat(),dis.readFloat());
				if (bNormals)
					for (int v=0; v<nbVerts; v++)
						norms[v] = new ZVector(dis.readFloat(),dis.readFloat(),dis.readFloat());
				if (bUVs)
					for (int v=0; v<nbVerts; v++)
					{
						UVs[v*2] = dis.readFloat();
						UVs[v*2+1] = dis.readFloat();
					}
				
				int nbFaces = dis.readInt();
				int[] faces = new int[nbFaces*3];
				for (int f=0; f<nbFaces*3; f++) faces[f] = dis.readInt();
				
				ZMesh mesh = new ZMesh();
				geoms[g].add(mesh);
				
				// flags override
				if (0!=(loadFlags&LOADFLAG_NO_NORMAL))
					bNormals = false;
				
				mesh.allocMesh(nbVerts, nbFaces, bNormals);
				
				for (int v=0; v<nbVerts; v++)
				{
					ZVector norm=null; if (bNormals) norm=norms[v];
					float U=0.f; if (bUVs) U=UVs[v*2];
					float V=0.f; if (bUVs) V=UVs[v*2+1];
					mesh.addVertex(verts[v], norm, U, V);
				}
				for (int f=0; f<nbFaces; f++)
					mesh.addFace(faces[f*3], faces[f*3+1], faces[f*3+2]); 
				
				mesh.setMaterial(material);

			}
			
			// add the loaded mesh
			//add(mesh);
//				mesh.setMaterial("chest", ZActivity.instance); // TEMP! CACA!
			
			
		}
		
		// chargement des instances
		int nbInstances = dis.readInt();
		for (int i=0; i<nbInstances; i++)
			add(loadSubInstance(dis,geoms));		
		// no mesh to link to the root load instance
	}
	
	private static ZInstance loadSubInstance(DataInputStream dis,ZInstance[] geoms) throws IOException
	{
		ZInstance inst = new ZInstance();
		// TODO
		inst.mName = dis.readUTF();
		int nbMeshes = dis.readInt();
		for (int i=0; i<nbMeshes; i++)
		{
			String meshName = dis.readUTF();
			for (ZInstance geom : geoms)
				if (meshName.equals(geom.mName))
				{
					inst.add(geom);
					break;
				}
		}
		ZQuaternion r = new ZQuaternion(dis.readFloat(),dis.readFloat(),dis.readFloat(),dis.readFloat());
		ZVector s = new ZVector(dis.readFloat(),dis.readFloat(),dis.readFloat());
		ZVector t = new ZVector(dis.readFloat(),dis.readFloat(),dis.readFloat());
		inst.setRotation(r);
		inst.setScale(s);
		inst.setTranslation(t);
		
		// sub-instances
		int nbSubINstances = dis.readInt();
		for (int i=0; i<nbSubINstances; i++)
			inst.add(loadSubInstance(dis,geoms));
		
		return inst;
	}

}
