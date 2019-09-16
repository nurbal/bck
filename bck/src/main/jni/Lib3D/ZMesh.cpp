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

#include "ZMesh.h"
#include "ZVector.h"
#include "helpers.h"
#include <GLES/gl.h>
#include <math.h>
#include "log.h"

#define BUFFER_OFFSET(i) ((char*)NULL + (i))

ZMesh::ZMesh() : ZObject()
{
	mVertexBuffer = 0;
	mbNormals = false;
	mVertexSize = 5;
	mIndexBuffer = 0;
	mNbVertex = 0;
	mNbFaces = 0;
	mNbVertexMax = 0;
	mNbFacesMax = 0;

    mVertexVBOid = 0;		// VBO
    mIndexVIOid = 0;		// VBO
    mVBOsNeedRebuild = true;	// VBOs need rebuild, since they have been modified

    mBackfaceCulling  =true;
}

ZMesh::~ZMesh()
{
	LOGI("ZMesh::~ZMesh()");
	if (mVertexBuffer)	delete[] mVertexBuffer;		mVertexBuffer = 0;
	if (mIndexBuffer)	delete[] mIndexBuffer;		mIndexBuffer = 0;

	// TODO
	// free VBOs
}

void ZMesh::allocVertices(int nb,bool norms)
{
	if (mVertexBuffer)	delete[] mVertexBuffer;		mVertexBuffer = 0;
	mNbVertex = 0;
	mNbVertexMax = nb;
	mbNormals = norms;
	mVertexSize = (norms?8:5);
	mVertexBuffer = new float[mVertexSize*nb];
    mVertsOverflowWarning = true;
}

void ZMesh::allocFaces(int nb)
{
	if (mIndexBuffer)	delete[] mIndexBuffer;		mIndexBuffer = 0;
	mNbFaces = 0;
	mNbFacesMax = nb;
	mIndexBuffer = new short[3*nb];
    mFacesOverflowWarning = true;
}

void ZMesh::setVertices(int nbVerts, float* verts, float* uvs, float* norms)
{
	allocVertices(nbVerts,norms!=0);
	for (int i=0; i<nbVerts; i++)
	{
		for (int j=0; j<3; j++)
			mVertexBuffer[i*mVertexSize+2+j]=verts[i*3+j];
		for (int j=0; j<2; j++)
			mVertexBuffer[i*mVertexSize+j]=uvs[i*2+j];
		if (norms)
			for (int j=0; j<3; j++)
				mVertexBuffer[i*mVertexSize+5+j]=norms[i*3+j];
	}
	mNbVertex = nbVerts;
	mVBOsNeedRebuild = true;
}

void ZMesh::setVertices(int nbVerts,ZVector** verts, float* uvs, ZVector** norms)
{
	allocVertices(nbVerts,norms!=0);
	for (int i=0; i<nbVerts; i++)
	{
		if (norms)
			addVertex(verts[i],norms[i],uvs[i*2],uvs[i*2+1]);
		else
			addVertex(verts[i],0,uvs[i*2],uvs[i*2+1]);
	}
	mNbVertex = nbVerts;
	mVBOsNeedRebuild = true;
}

void ZMesh::setFaces(unsigned char* indices, int nbFaces)
{
	allocFaces(nbFaces);
	for (int i=0; i<nbFaces; i++) addFace(indices[i*3],indices[i*3+1],indices[i*3+2]);
	mNbFaces = nbFaces;
	mVBOsNeedRebuild = true;
}

void ZMesh::setFaces(short* indices, int nbFaces)
{
	allocFaces(nbFaces);
	for (int i=0; i<nbFaces; i++) addFace(indices[i*3],indices[i*3+1],indices[i*3+2]);
	mNbFaces = nbFaces;
	mVBOsNeedRebuild = true;
}

/*
 * Following functions will construct a mesh progressively...
 */
void ZMesh::allocMesh(int nbVerts, int nbFaces, bool norms)
{
	allocVertices(nbVerts,norms);
	allocFaces(nbFaces);
}

void ZMesh::resetMesh()
{
	mNbVertex = 0;
	mNbFaces = 0;
	mVBOsNeedRebuild = true;
    mVertsOverflowWarning = true;
    mFacesOverflowWarning = true;
}

void ZMesh::addVertex(const ZVector* vert, const ZVector* norm, float u, float v)
{
	CHECK_NAN_V(vert)
	if (norm) {CHECK_NAN_V(norm)}
	CHECK_NAN(u)
	CHECK_NAN(v)

	if (mNbVertex==mNbVertexMax)
	{
		if (mVertsOverflowWarning) LOGE("addVertex overflow (max=%i)",mNbVertexMax);
		mVertsOverflowWarning = false;
		return;
	}
	mVertexBuffer[mNbVertex*mVertexSize+2] = vert->x;
	mVertexBuffer[mNbVertex*mVertexSize+3] = vert->y;
	mVertexBuffer[mNbVertex*mVertexSize+4] = vert->z;

	mVertexBuffer[mNbVertex*mVertexSize+0] = u;
	mVertexBuffer[mNbVertex*mVertexSize+1] = v;

	if (mbNormals && norm)
	{
		mVertexBuffer[mNbVertex*mVertexSize+5] = norm->x;
		mVertexBuffer[mNbVertex*mVertexSize+6] = norm->y;
		mVertexBuffer[mNbVertex*mVertexSize+7] = norm->z;
	}

	mNbVertex++;
	mVBOsNeedRebuild = true;
}

void ZMesh::addFace(int i1,int i2,int i3)
{
	if (mNbFaces==mNbFacesMax)
	{
		if (mFacesOverflowWarning) LOGE("addFace overflow (max=%i)",mNbFacesMax);
		mFacesOverflowWarning = false;
		return;
	}
	if (i1>mNbVertex || i2>mNbVertex || i3>mNbVertex)
	{
		if (mFacesOverflowWarning) LOGE("addFace(%i,%i,%i) overflow (max=%i)",i1,i2,i3,mNbVertex);
		mFacesOverflowWarning = false;
		return;
	}
	if (i1>mNbVertexMax || i2>mNbVertexMax || i3>mNbVertexMax)
	{
		if (mFacesOverflowWarning) LOGE("addFace(%i,%i,%i) vertex overflow (max=%i)",i1,i2,i3,mNbVertexMax);
		mFacesOverflowWarning = false;
		return;
	}
	mIndexBuffer[mNbFaces*3] = i1;
	mIndexBuffer[mNbFaces*3+1] = i2;
	mIndexBuffer[mNbFaces*3+2] = i3;
	mNbFaces++;
	mVBOsNeedRebuild = true;
}

void ZMesh::addFace(const ZVector* p1, const ZVector* p2, const ZVector* p3,
				const ZVector* n1, const ZVector* n2, const ZVector* n3,
				float u1, float v1, float u2, float v2, float u3, float v3)
{
	if (p1->isEqual(p2) || p1->isEqual(p3) || p2->isEqual(p3)) return;	// face d�g�n�r�e
	int i = mNbVertex;
	addVertex(p1,n1,u1,v1);
	addVertex(p2,n2,u2,v2);
	addVertex(p3,n3,u3,v3);
	addFace(i,i+1,i+2);
	mVBOsNeedRebuild = true;
}

//#include <unistd.h>

void ZMesh::Render(SceneContext context, bool parentIsColored, const ZColor* parentColor)
{
	if (!mVertexBuffer || !mIndexBuffer)
	{
		return;
	}

	// use GL11 functions ?
	bool GL11=false;
	//int vboUsage = GL_STATIC_DRAW;
	int vboUsage = GL_DYNAMIC_DRAW;

	// clear cache!
//	cacheflush((long)(void*)mVertexBuffer,(long)(void*)(mVertexBuffer+mVertexSize*4*mNbVertex),0);
//	cacheflush((long)(void*)mIndexBuffer,(long)(void*)(mIndexBuffer+2*mNbFaces*3),0);

    if (GL11)
    {
    	// create VBO id necessary
    	if (0==mVertexVBOid)
    		glGenBuffers(1, &mVertexVBOid);
    	if (0==mIndexVIOid)
    		glGenBuffers(1, &mIndexVIOid);
    	// bind buffers
    	glBindBuffer(GL_ARRAY_BUFFER, mVertexVBOid);
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mIndexVIOid);
    	// fill buffers if necessary
    	if (mVBOsNeedRebuild)
    	{
    		glBufferData(GL_ARRAY_BUFFER, mVertexSize*4*mNbVertex, mVertexBuffer, vboUsage);
    		glBufferData(GL_ELEMENT_ARRAY_BUFFER, 2*mNbFaces*3, mIndexBuffer, vboUsage);
    		mVBOsNeedRebuild = false;
    	}
    }


	// vertices
	glEnableClientState(GL_VERTEX_ARRAY);
    if (GL11)
        glVertexPointer(3, GL_FLOAT, 4*mVertexSize, BUFFER_OFFSET(8));
    else
    	glVertexPointer(3, GL_FLOAT, 4*mVertexSize, mVertexBuffer+2);

    // normals
	 if (mbNormals)
	{
		glEnableClientState(GL_NORMAL_ARRAY);
	//	glEnable(GL_LIGHTING);
	    if (GL11)
	    	glNormalPointer(GL_FLOAT, 4*mVertexSize, BUFFER_OFFSET(20));
	    else
	    	glNormalPointer(GL_FLOAT, 4*mVertexSize, mVertexBuffer+5);

		float matAmbient[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		float matDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.f };
		float matSpecular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		glMaterialfv(GL_FRONT, GL_AMBIENT, matAmbient);
		glMaterialfv(GL_FRONT, GL_DIFFUSE, matDiffuse);
		glMaterialfv(GL_FRONT, GL_SPECULAR, matSpecular);
		glMaterialf(GL_FRONT, GL_SHININESS, 100.0f);
	 }
	else
	{
	//	glDisable(GL_LIGHTING);
		glDisableClientState(GL_NORMAL_ARRAY);
	}


	// UVs
	if (mMaterial)
	{
		//LOGI("ZMesh::draw 5");
	    glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	    glEnable(GL_TEXTURE_2D);
	    if (GL11)
	    	glTexCoordPointer(2,GL_FLOAT, 4*mVertexSize, BUFFER_OFFSET(0));
	    else
	    	glTexCoordPointer(2,GL_FLOAT, 4*mVertexSize, mVertexBuffer+0);

	   // TODO
	   mMaterial->bind();

	   // filtering
	   glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	   glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}
	else
	{
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glDisable(GL_TEXTURE_2D);
	}

	// vertex colors:
	glDisableClientState(GL_COLOR_ARRAY);

	// disable everything relative to lighting & textures
//	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
//	glDisable(GL_TEXTURE_2D);
//	glDisableClientState(GL_NORMAL_ARRAY);
//	glDisable(GL_LIGHTING);

	// backface culing
	if (mBackfaceCulling)
	{
		glEnable( GL_CULL_FACE);
		glFrontFace( GL_CCW);
	}
	else
	{
		glDisable( GL_CULL_FACE);
	}

    if (GL11)
    {
    	glDrawElements(GL_TRIANGLES, mNbFaces*3, GL_UNSIGNED_SHORT, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    else
		glDrawElements(GL_TRIANGLES, mNbFaces*3, GL_UNSIGNED_SHORT, mIndexBuffer);

	glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_NORMAL_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
}

void ZMesh::createBox(ZVector* halfsize)
{
	ZVector** verts = new ZVector*[24];	// vertices
	ZVector** norms = new ZVector*[24];	// normals
	float* uvs = new float[48];		// text coords
	short* faces = new short[3*12];
	int i;

	// verts
	verts[0] = new ZVector(-halfsize->x,halfsize->y,halfsize->z);
	verts[13] = new ZVector(-halfsize->x,halfsize->y,halfsize->z);
	verts[22] = new ZVector(-halfsize->x,halfsize->y,halfsize->z);
	verts[1] = new ZVector(halfsize->x,halfsize->y,halfsize->z);
	verts[4] = new ZVector(halfsize->x,halfsize->y,halfsize->z);
	verts[23] = new ZVector(halfsize->x,halfsize->y,halfsize->z);
	verts[2] = new ZVector(-halfsize->x,-halfsize->y,halfsize->z);
	verts[15] = new ZVector(-halfsize->x,-halfsize->y,halfsize->z);
	verts[16] = new ZVector(-halfsize->x,-halfsize->y,halfsize->z);
	verts[3] = new ZVector(halfsize->x,-halfsize->y,halfsize->z);
	verts[6] = new ZVector(halfsize->x,-halfsize->y,halfsize->z);
	verts[17] = new ZVector(halfsize->x,-halfsize->y,halfsize->z);

	verts[5] = new ZVector(halfsize->x,halfsize->y,-halfsize->z);
	verts[8] = new ZVector(halfsize->x,halfsize->y,-halfsize->z);
	verts[21] = new ZVector(halfsize->x,halfsize->y,-halfsize->z);
	verts[9] = new ZVector(-halfsize->x,halfsize->y,-halfsize->z);
	verts[12] = new ZVector(-halfsize->x,halfsize->y,-halfsize->z);
	verts[20] = new ZVector(-halfsize->x,halfsize->y,-halfsize->z);
	verts[7] = new ZVector(halfsize->x,-halfsize->y,-halfsize->z);
	verts[10] = new ZVector(halfsize->x,-halfsize->y,-halfsize->z);
	verts[19] = new ZVector(halfsize->x,-halfsize->y,-halfsize->z);
	verts[11] = new ZVector(-halfsize->x,-halfsize->y,-halfsize->z);
	verts[14] = new ZVector(-halfsize->x,-halfsize->y,-halfsize->z);
	verts[18] = new ZVector(-halfsize->x,-halfsize->y,-halfsize->z);

	// normales
	for (i=0; i<4; i++)  	{ norms[i]=new ZVector(0,0,1.f); }
	for (i=4; i<8; i++)  	{ norms[i]=new ZVector(1.f,0,0); }
	for (i=8; i<12; i++)  	{ norms[i]=new ZVector(0,0,-1.f); }
	for (i=12; i<16; i++)  	{ norms[i]=new ZVector(-1.f,0,0); }
	for (i=16; i<20; i++)  	{ norms[i]=new ZVector(0,-1.f,0); }
	for (i=20; i<24; i++)  	{ norms[i]=new ZVector(0,1.f,0); }

	// uvs
	i=0;
	uvs[i*8+0] = 0;	uvs[i*8+1] = 0;
	uvs[i*8+2] = 1.f;	uvs[i*8+3] = 0;
	uvs[i*8+4] = 0;	uvs[i*8+5] = 1.f;
	uvs[i*8+6] = 1.f;	uvs[i*8+7] = 1.f;
	i=1;
	uvs[i*8+0] = 1.f;	uvs[i*8+1] = 0;
	uvs[i*8+2] = 1.f;	uvs[i*8+3] = 1.f;
	uvs[i*8+4] = 0;	uvs[i*8+5] = 0;
	uvs[i*8+6] = 0;	uvs[i*8+7] = 1.f;
	i=2;
	uvs[i*8+0] = 0;	uvs[i*8+1] = 0;
	uvs[i*8+2] = 1.f;	uvs[i*8+3] = 0;
	uvs[i*8+4] = 0;	uvs[i*8+5] = 1.f;
	uvs[i*8+6] = 1.f;	uvs[i*8+7] = 1.f;
	i=3;
	uvs[i*8+0] = 0;	uvs[i*8+1] = 1.f;
	uvs[i*8+2] = 0;	uvs[i*8+3] = 0;
	uvs[i*8+4] = 1.f;	uvs[i*8+5] = 1.f;
	uvs[i*8+6] = 1.f;	uvs[i*8+7] = 0;
	i=4;
	uvs[i*8+0] = 0;	uvs[i*8+1] = 0;
	uvs[i*8+2] = 1.f;	uvs[i*8+3] = 0;
	uvs[i*8+4] = 0;	uvs[i*8+5] = 1.f;
	uvs[i*8+6] = 1.f;	uvs[i*8+7] = 1.f;
	i=5;
	uvs[i*8+0] = 1.f;	uvs[i*8+1] = 1.f;
	uvs[i*8+2] = 0;	uvs[i*8+3] = 1.f;
	uvs[i*8+4] = 1.f;	uvs[i*8+5] = 0;
	uvs[i*8+6] = 0;	uvs[i*8+7] = 0;

	// faces
	for (i=0; i<6; i++)
	{
		faces[i*6+0]=(short)(i*4);
		faces[i*6+1]=(short)(i*4+2);
		faces[i*6+2]=(short)(i*4+1);

		faces[i*6+3]=(short)(i*4+1);
		faces[i*6+4]=(short)(i*4+2);
		faces[i*6+5]=(short)(i*4+3);
	}

	setVertices(24,verts,uvs,norms);
	setFaces(faces,12);
}

void ZMesh::createSphere(int resolX,int resolY,float ray)
{
 	// cr�ation des vertexs et des faces
	// resolX = nb de m�ridiens (+1 pour fermer)
	// resolY = nb de parall�les (+2 pour les poles)
	int xStepFace = resolY*2; // incr�ment entre deux m�ridiens
	int xStepVert = resolY+2;

	int nbVerts = (resolX+1) * xStepVert;
	int nbFaces = (resolX+1) * xStepFace;

	ZVector** verts = new ZVector*[nbVerts];	// vertices
	ZVector** norms = new ZVector*[nbVerts];	// normals
	float* uvs = new float[2*nbVerts];		// text coords
	short* faces = new short[3*nbFaces];

	// vertices, normals, uvs
	for (int x=0; x<resolX+1; x++)
	{
		float angleX = (float)x*6.2832f/(float)resolX;
		if (x==resolX) angleX=0;
		float cosX = cosf(angleX);
		float sinX = sinf(angleX);
		for (int y=0; y<xStepVert; y++)
		{
			float cosY,sinY;
			float angleY = (float)y*3.1416f/(float)(xStepVert-1);
			if (y==0)
			{
				cosY = 1.f;
				sinY = 0;
			}
			else if (y==xStepVert-1)
			{
				cosY = -1.f;
				sinY = 0;
			}
			else
			{
				cosY = cosf(angleY);
				sinY = sinf(angleY);
			}
			int vertIndex = x*xStepVert + y;
			norms[vertIndex] = new ZVector(sinY*cosX,sinY*sinX,cosY);
			verts[vertIndex] = new ZVector();   verts[vertIndex]->copy(norms[vertIndex]);  	verts[vertIndex]->mul(ray);
			if (x==resolX)
				uvs[vertIndex*2] = 1.f;
			else
			{
				if (y==0 || y==xStepVert-1)
					uvs[vertIndex*2] = (float)x/(float)resolX + 1.f/(2.f*(float)resolX);
				else
					uvs[vertIndex*2] = angleX/6.2832f;
			}
			uvs[vertIndex*2+1] = angleY/3.1416f;
		}
	}
	setVertices(nbVerts,verts,uvs,norms);

	// faces
	allocFaces(nbFaces);
//	for (int i=0; i<nbFaces*3; i++) faces[i]=0; //TEMP
	for (int x=0; x<resolX; x++)
	{
		int indexVert = xStepVert*x;
		// north
		addFace(indexVert,indexVert+1,indexVert+1+xStepVert);
		// south
		addFace(indexVert+xStepVert-1, indexVert+xStepVert-2+xStepVert, indexVert+xStepVert-2);

		// surface
		for (int y=1; y<resolY; y++)
		{
			// 2 faces...
			addFace(indexVert+y, indexVert+y+1, indexVert+y+1+xStepVert);

			addFace(indexVert+y+xStepVert, indexVert+y, indexVert+y+1+xStepVert);

		}
	}
}

// create a cylinder;
// half top texture is used as top/bottom caps, haf bottom is used as side
void ZMesh::createCylinder(int resol,float ray, float halfheight)
{
	int nbVerts = resol * 4 +2; // side vert is dedoubled
	int nbFaces = (resol*2) + 2*(resol-2);

	allocMesh(nbVerts,nbFaces,true);

	// verts (4 blocs dans cet ordre: top, bottom, side up, side down) & norms
	ZVector v,n;

	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);

		v.set(cs*ray,sn*ray,halfheight);
		n.set(0.f,0.f,1.f);
		addVertex(&v,&n,0.5f+(cs*0.5f),0.25f-(sn*0.25f));
	}

	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*ray),(sn*ray),-halfheight);
		n.set(0.f,0.f,-1.f);
		addVertex(&v,&n,0.5f+(cs*0.5f),0.25f-(sn*0.25f));
	}

	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*ray),(sn*ray),halfheight);
		n.set(cs,sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,0.5f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(ray,0.f,halfheight);
	n.set(1.f,0.f,0.f);
	addVertex(&v,&n,1.f,0.5f);

	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*ray),(sn*ray),-halfheight);
		n.set(cs,sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,1.f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(ray,0.f,-halfheight);
	n.set(1.f,0.f,0.f);
	addVertex(&v,&n,1.f,1.f);

	// faces
	//caps
	for (int i=0; i<resol-2; i++)
	{
		addFace(0,i+1,i+2); //top
		addFace(resol,resol+i+2,resol+i+1); //bottom
	}

	// side
	for (int i=0; i<resol; i++)
	{
		addFace(i+2*resol,i+3*resol+1,i+2*resol+1);
		addFace(i+3*resol+1,i+3*resol+2,i+2*resol+1);
	}

}

// create a cylinder;
// half top texture is used as top/bottom caps, haf bottom is used as side
void ZMesh::createTube(int resol,float rayIn,float rayOut, float halfheight)
{
	int nbVerts = resol * 8 +4; // side vert is dedoubled
	int nbFaces = resol*8;

	allocMesh(nbVerts,nbFaces,true);

	// verts (8 blocs dans cet ordre: inner top, inner bottom, inner side up, inner side down, outer top, outer bottom, outer side up, outer side down) & norms
	ZVector v,n;

	// inner top
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		float inOutFactor = rayIn/rayOut;
		v.set((cs*rayIn),(sn*rayIn),halfheight);
		n.set(0.f,0.f,1.f);
		addVertex(&v,&n,0.5f+(cs*inOutFactor*0.5f),0.25f-(sn*inOutFactor*0.25f));
	}
	// inner bottom
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		float inOutFactor = (rayIn/rayOut);
		v.set((cs*rayIn),(sn*rayIn),-halfheight);
		n.set(0.f,0.f,-1.f);
		addVertex(&v,&n,0.5f+(cs*inOutFactor*0.5f),0.25f-(sn*inOutFactor*0.25f));
	}
	// inner side up
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*rayIn),(sn*rayIn),halfheight);
		n.set(-cs,-sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,0.5f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(rayIn,0.f,halfheight);
	n.set(-1.f,0.f,0.f);
	addVertex(&v,&n,1.f,0.5f);

	// inner side down
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*rayIn),(sn*rayIn),-halfheight);
		n.set(-cs,-sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,1.f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(rayIn,0.f,-halfheight);
	n.set(-1.f,0.f,0.f);
	addVertex(&v,&n,1.f,1.f);


	// outer top
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);

		v.set((cs*rayOut),(sn*rayOut),halfheight);
		n.set(0.f,0.f,1.f);
		addVertex(&v,&n,0.5f+(cs*0.5f),0.25f-(sn*0.25f));
	}
	// outer bottom
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*rayOut),(sn*rayOut),-halfheight);
		n.set(0.f,0.f,-1.f);
		addVertex(&v,&n,0.5f+(cs*0.5f),0.25f-(sn*0.25f));
	}
	// outer side up
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set((cs*rayOut),(sn*rayOut),halfheight);
		n.set(cs,sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,0.5f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(rayOut,0.f,halfheight);
	n.set(1.f,0.f,0.f);
	addVertex(&v,&n,1.f,0.5f);

	// outer side down
	for (int i=0; i<resol; i++)
	{
		float angle = 6.2832f*((float)i)/((float)resol);
		float cs = cosf(angle);
		float sn = sinf(angle);
		v.set(cs*rayOut,sn*rayOut,-halfheight);
		n.set(cs,sn,0.f);
		addVertex(&v,&n,(float)i/(float)resol,1.f);
	}

	// vertex d'enveloppe d�doubl�
	v.set(rayOut,0.f,-halfheight);
	n.set(1.f,0.f,0.f);
	addVertex(&v,&n,1.f,1.f);




	// verts (8 blocs dans cet ordre: inner top, inner bottom, inner side up, inner side down, outer top, outer bottom, outer side up, outer side down) & norms
	// blocs indexes:
	// inner top = 0
	// inner bottom = resol
	// inner side up = resol*2
	// inner side down = resol*3+1
	// outer top = resol*4+2
	// outer bottom = resol*5+2
	// outer side up = resol*6+2
	// outer side down = resol*7+3
	int inTop = 0;
	int inBottom = resol;
	int inSideUp = resol*2;
	int inSideDown = resol*3+1;
	int outTop = resol*4+2;
	int outBottom = resol*5+2;
	int outSideUp = resol*6+2;
	int outSideDown = resol*7+3;

	// faces
	//caps
	for (int i=0; i<resol; i++)
	{
		// top
		addFace(inTop+i,outTop+((i+1)%resol),inTop+((i+1)%resol));
		addFace(inTop+i,outTop+i,outTop+((i+1)%resol));
		// bottom
		addFace(inBottom+i,outBottom+((i+1)%resol),inBottom+((i+1)%resol));
		addFace(inBottom+i,outBottom+i,outBottom+((i+1)%resol));
	}

	// inner side
	for (int i=0; i<resol; i++)
	{
		addFace(inSideUp+i,inSideUp+i+1,inSideDown+i+1);
		addFace(inSideUp+i,inSideDown+i+1,inSideDown+i);
	}

	// outer side
	for (int i=0; i<resol; i++)
	{
		addFace(outSideUp+i,outSideDown+i+1,outSideUp+i+1);
		addFace(outSideUp+i,outSideDown+i,outSideDown+i+1);
	}


}

// create a negative sphere, with no normal, ray=1
void ZMesh::createSkySphere(int resolX,int resolY)
{
	// cr�ation des vertexs et des faces
	// resolX = nb de m�ridiens (+1 pour fermer)
	// resolY = nb de parall�les (+2 pour les poles)
	int xStepFace = resolY*2; // incr�ment entre deux m�ridiens
	int xStepVert = resolY+2;

	int nbVerts = (resolX+1) * xStepVert;
	int nbFaces = (resolX+1) * xStepFace;

	ZVector** verts = new ZVector*[nbVerts];	// vertices
	float* uvs = new float[2*nbVerts];		// text coords
	short* faces = new short[3*nbFaces];

	// vertices, normals, uvs
	for (int x=0; x<resolX+1; x++)
	{
		float angleX = (float)x*6.2832f/(float)resolX;
		if (x==resolX) angleX=0;
		float cosX = cosf(angleX);
		float sinX = sinf(angleX);
		for (int y=0; y<xStepVert; y++)
		{
			float cosY,sinY;
			float angleY = (float)y*3.1416f/(float)(xStepVert-1);
			if (y==0)
			{
				cosY = 1.f;
				sinY = 0;
			}
			else if (y==xStepVert-1)
			{
				cosY = -1.f;
				sinY = 0;
			}
			else
			{
				cosY = cosf(angleY);
				sinY = sinf(angleY);
			}
			int vertIndex = x*xStepVert + y;
			verts[vertIndex] = new ZVector(sinY*cosX,sinY*sinX,cosY);
			if (x==resolX)
				uvs[vertIndex*2] = 1.f;
			else
			{
				if (y==0 || y==xStepVert-1)
					uvs[vertIndex*2] = (float)x/(float)resolX + 1.f/(2.f*(float)resolX);
				else
					uvs[vertIndex*2] = angleX/6.2832f;
			}
			uvs[vertIndex*2+1] = angleY/3.1416f;
		}
	}
	setVertices(nbVerts,verts,uvs,0);


	// faces
//	for (int i=0; i<nbFaces*3; i++) faces[i]=0; //TEMP
	allocFaces(nbFaces);
	for (int x=0; x<resolX; x++)
	{
		int indexVert = xStepVert*x;
		// north
		addFace(indexVert+1,indexVert,indexVert+1+xStepVert);
		// south
		addFace(indexVert+xStepVert-2+xStepVert, indexVert+xStepVert-1, indexVert+xStepVert-2);
		// surface
		for (int y=1; y<resolY; y++)
		{
			// 2 faces...
			addFace(indexVert+y+1, indexVert+y, indexVert+y+1+xStepVert);
			addFace(indexVert+y, indexVert+y+xStepVert, indexVert+y+1+xStepVert);
		}
	}
}

bool ZMesh::IsColored()
{
	if (mMaterial) return mMaterial->mHasAlpha;
	return false;
}
