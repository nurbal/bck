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

#ifndef __ZPOINTSMESH__
#define __ZPOINTSMESH__

#include "ZObject.h"
#include "release_settings.h"
#include <GLES/gl.h>

class ZVector;
class ZColor;

class ZPointsMesh : public ZObject
{
protected:
	bool 	mbSizes;
	bool 	mbColors;
	float*	mVertexBuffer;	// format: VT(N)VT(N)
	int		mVertexSize;	// depends on normals: 3 to 8 (V=3, S=1, C=4)

	int 	mVertexBufferIndexVertex;
	int 	mVertexBufferIndexColor;
	int 	mVertexBufferIndexSize;

	int		mNbVertex;
    int		mNbVertexMax;

public:
    ZPointsMesh();
    virtual ~ZPointsMesh();

	virtual bool GetType() {return ZOBJECT_TYPE_POINTSMESH;}
	virtual bool IsKindOf(int type)	{return (type==ZOBJECT_TYPE_POINTSMESH);}

//    void setVertices(int nbVerts, float* verts, float* uvs, float* norms);
//	void setVertices(int nbVerts,ZVector** verts, float* uvs, ZVector** norms);

	/*
	 * Following functions will construct a mesh progressively...
	 */
	void allocMesh(int nbVerts, bool sizes, bool colors);
	void resetMesh();

	/*
	 * returns the index of the new vertex
	 */
	int getNbVerts()	{return mNbVertex;}

	void addVertex(const ZVector* vert, float size, const ZColor* color);

	virtual void Render(SceneContext context, bool parentIsColored, const ZColor* parentColor);

	virtual bool IsColored();

};

#endif // __ZPOINTSMESH__
