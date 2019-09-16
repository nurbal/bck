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


public class ZMesh implements ZObject {

	// native NDK functions
	protected native static long JNIconstructor();
	protected native static void JNIdestructor(long obj);
	
	protected native static void JNIsetMaterial(long obj, long mat);
	protected native static void JNIenableBackfaceCulling(long obj, boolean culling);
	protected native static void JNIallocMesh(long obj, int nbVerts, int nbFaces, boolean norms);
	protected native static void JNIresetMesh(long obj);
	protected native static void JNIaddFace(long obj, int i1, int i2, int i3);
	protected native static void JNIaddFace2(long obj, long p1, long p2, long p3, long n1, long n2, long n3, float u1, float v1, float u2, float v2, float u3, float v3);
	protected native static int JNIgetNbVerts(long obj);
	protected native static int JNIgetNbFaces(long obj);
	protected native static void JNIaddVertex(long obj, long vert, long norm, float  u, float v);
	protected native static void JNIcreateBox(long obj, long halfsize);
	protected native static void JNIcreateSphere(long obj, int resolX, int resolY, float ray);
	protected native static void JNIcreateSkySphere(long obj, int resolX, int resolY);
	protected native static void JNIcreateCylinder(long obj, int resolX, float ray, float halfheight);
	protected native static void JNIcreateTube(long obj, int resolX, float rayIn, float rayOut, float halfheight);

	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}

	public ZMesh()			{ mNativeObject = JNIconstructor(); }   
	@Override
	protected void finalize() throws Throwable { 
		JNIdestructor(mNativeObject); 
		super.finalize();
	}   
	
    private ZMaterial 	mMaterial = null;	// passé en private pour empecher d'y accéder directement (très important pour le lifecycle de l'application!)
    
    public String		mName;
    
	// helper
	public void setMaterial(String materialName)
	{
  		setMaterial(ZActivity.instance.mScene.getMaterial(materialName));
 	}
	
	public void setMaterial(ZMaterial material)
	{
		mMaterial = material;
		if (null==material) return;
		JNIsetMaterial(mNativeObject, mMaterial.getNativeObject());
	}

	public void allocMesh(int nbVerts, int nbFaces, boolean norms)	{JNIallocMesh(mNativeObject,nbVerts,nbFaces,norms);}
	public void resetMesh() {JNIresetMesh(mNativeObject);}	// removes all faces, but keeps allocations

	public void enableBackfaceCulling(boolean culling) { JNIenableBackfaceCulling(mNativeObject,culling); }
	
	
	public int getNbVerts()		{ return JNIgetNbVerts(mNativeObject); }
	public void addVertex(ZVector vert, ZVector norm, float  u, float v)
	{
		if (norm!=null)
			JNIaddVertex(mNativeObject,vert.getNativeObject(),norm.getNativeObject(),u,v);
		else
			JNIaddVertex(mNativeObject,vert.getNativeObject(),0,u,v);
	}
	
	public int getNbFaces()		{ return JNIgetNbFaces(mNativeObject); }
	public void addFace(int i1,int i2,int i3)	{JNIaddFace(mNativeObject,i1,i2,i3);}
	public void addFace(ZVector p1, ZVector p2, ZVector p3, 
						 					ZVector n1, ZVector n2, ZVector n3, 
											float u1, float v1, float u2, float v2, float u3, float v3)
	{
		JNIaddFace2(mNativeObject,p1.getNativeObject(),p2.getNativeObject(),p3.getNativeObject(),
				n1.getNativeObject(),n2.getNativeObject(),n3.getNativeObject(),
				u1,v1,u2,v2,u3,v3);
	}
	
	
	/*
	@Override
	public void draw(GL10 gl) {
        // vertices
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        mVertexBuffer.position(0);
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);
        
        // normals
         if (mNormalBuffer!=null && mNormalBuffer.capacity()>=mVertexBuffer.capacity())
        {
        	gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        	mNormalBuffer.position(0);
        	gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);

        	float matAmbient[] = { 1.0f, 1.0f, 1.0f, 1.0f };
            float matDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.f };
            float matSpecular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        	gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT, matAmbient,0);
        	gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_DIFFUSE, matDiffuse,0);
        	gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, matSpecular,0);
        	gl.glMaterialf(GL10.GL_FRONT, GL10.GL_SHININESS, 100.0f);
         }
        else
        {
        	gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        }
        	
        // UVs
        if (mTexBuffer!=null && mMaterial!=null && mTexBuffer.capacity()*3>=mVertexBuffer.capacity()*2)
        {
           gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
           gl.glEnable(GL10.GL_TEXTURE_2D);
           mTexBuffer.position(0);
           gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, mTexBuffer); 
           
           mMaterial.bind(gl);
           
           // filtering
           gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
           gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        }
        else
        {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisable(GL10.GL_TEXTURE_2D);        	
        } 
        
        mIndexBuffer.position(0);
       
		gl.glDrawElements(GL10.GL_TRIANGLES, mNbFaces*3, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}
	
	*/
	public void createBox(ZVector halfsize)						{JNIcreateBox(mNativeObject,halfsize.getNativeObject());}
	public void createSphere(int resolX,int resolY,float ray)	{JNIcreateSphere(mNativeObject,resolX,resolY,ray);}
	// create a negative sphere, with no normal, ray1
	public void createSkySphere(int resolX,int resolY)			{JNIcreateSkySphere(mNativeObject,resolX,resolY);}
	public void createCylinder(int resol,float ray,float halfheight)	{JNIcreateCylinder(mNativeObject,resol,ray,halfheight);}
	public void createTube(int resol,float rayIn,float rayOut,float halfheight)	{JNIcreateTube(mNativeObject,resol,rayIn,rayOut,halfheight);}
	
	private static ZVector v11 = new ZVector(); 
	private static ZVector v12 = new ZVector(); 
	private static ZVector v21 = new ZVector(); 
	private static ZVector v22 = new ZVector(); 
	public void createRectangle(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2)
	{
		createRectangle(x1,y1,x2,y2,u1,v1,u2,v1,u1,v2,u2,v2);
	}
	public void createRectangle(float x1, float y1, float x2, float y2, float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3)
	{
		allocMesh(4,2,false);
		v11.set(x1,y1,0);
		v12.set(x1,y2,0);
		v21.set(x2,y1,0);
		v22.set(x2,y2,0);
		addVertex(v11,null,u0,v0);
		addVertex(v21,null,u1,v1);
		addVertex(v12,null,u2,v2);
		addVertex(v22,null,u3,v3);
		addFace(0,1,2);	
		addFace(2,1,3);
	}
	
	
	

	
}
