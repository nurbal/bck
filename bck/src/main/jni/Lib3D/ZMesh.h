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

#ifndef __ZMESH__
#define __ZMESH__

#include "ZObject.h"
#include "ZMaterial.h"
#include "release_settings.h"
#include <GLES/gl.h>

class ZVector;
class ZColor;

class ZMesh : public ZObject
{
protected:
	bool 	mbNormals;
	float*	mVertexBuffer;	// format: VT(N)VT(N)
	int		mVertexSize;	// depends on normals: 5 or 8 (V=3, T=2, N=3)
    short*	mIndexBuffer;
    // the same buffers, but in VBO (hardware side)
    // if needed, they will be re-uploaded in next draw() call (see mVBOsNeedRebuild)
    // mVBOsNeedRebuild is set to true on each resetMesh/addFace/addVertex call.
    GLuint	mVertexVBOid;		// Vertex Buffer Object
    GLuint	mIndexVIOid;		// Vertex Index Oobject
    bool	mVBOsNeedRebuild;	// VBOs need rebuild, since they have been modified

    bool 	mBackfaceCulling;

    bool	mVertsOverflowWarning;
    bool	mFacesOverflowWarning;
    int		mNbVertex;
    int		mNbFaces;
    int		mNbVertexMax;
    int		mNbFacesMax;
public:
    PMaterial mMaterial;
public:
    ZMesh();
    virtual ~ZMesh();

	virtual bool GetType() {return ZOBJECT_TYPE_MESH;}
	virtual bool IsKindOf(int type)	{return (type==ZOBJECT_TYPE_MESH);}

    void allocVertices(int nb,bool norms);
    void allocFaces(int nb);

    void setVertices(int nbVerts, float* verts, float* uvs, float* norms);
	void setVertices(int nbVerts,ZVector** verts, float* uvs, ZVector** norms);

 	void setFaces(unsigned char* indices, int nbFaces);
	void setFaces(short* indices, int nbFaces);

	void enableBackfaceCulling(bool culling) { mBackfaceCulling = culling; }

	/*
	 * Following functions will construct a mesh progressively...
	 */
	void allocMesh(int nbVerts, int nbFaces, bool norms);
	void resetMesh();

	/*
	 * returns the index of the new vertex
	 */
	int getNbVerts()	{return mNbVertex;}
	int getNbFaces()	{return mNbFaces;}

	void addVertex(const ZVector* vert, const ZVector* norm, float u, float v);

	void addFace(int i1,int i2,int i3);

	void addFace(const ZVector* p1, const ZVector* p2, const ZVector* p3,
					const ZVector* n1, const ZVector* n2, const ZVector* n3,
					float u1, float v1, float u2, float v2, float u3, float v3);


	virtual void Render(SceneContext context, bool parentIsColored, const ZColor* parentColor);

	virtual bool IsColored();

	void createBox(ZVector* halfsize);
	void createSphere(int resolX,int resolY,float ray);
	// create a negative sphere, with no normal, ray=1
	void createSkySphere(int resolX,int resolY);
	void createCylinder(int resol,float ray, float halfheight);
	void createTube(int resol,float rayIn,float rayOut, float halfheight);

};

#endif // __ZMESH__
