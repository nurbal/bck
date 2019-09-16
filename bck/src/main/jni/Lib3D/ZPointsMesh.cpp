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

#include "ZPointsMesh.h"
#include "ZVector.h"
#include "ZColor.h"
#include "helpers.h"
#include <GLES/gl.h>
//#include <math.h>
#include "log.h"

#define BUFFER_OFFSET(i) ((char*)NULL + (i))

ZPointsMesh::ZPointsMesh() : ZObject()
{
	mVertexBuffer = 0;
	mbSizes = false;
	mbColors = false;
	mVertexSize = 3;
	mNbVertex = 0;
	mNbVertexMax = 0;
}

ZPointsMesh::~ZPointsMesh()
{
	LOGI("ZPointsMesh::~ZPointsMesh()");
	if (mVertexBuffer)	delete[] mVertexBuffer;		mVertexBuffer = 0;
}

/*
 * Following functions will construct a mesh progressively...
 */
void ZPointsMesh::allocMesh(int nbVerts, bool sizes, bool colors)
{
	if (mVertexBuffer)	delete[] mVertexBuffer;		mVertexBuffer = 0;
	mNbVertex = 0;
	mNbVertexMax = nbVerts;
	mbSizes = sizes;
	mbColors = colors;
	mVertexSize = 3; mVertexBufferIndexVertex = 0;
	if (mbSizes) {mVertexBufferIndexSize=mVertexSize; mVertexSize+=1;}
	if (mbColors) {mVertexBufferIndexColor=mVertexSize; mVertexSize+=4;}
	mVertexBuffer = new float[mVertexSize*nbVerts];
}

void ZPointsMesh::resetMesh()
{
	mNbVertex = 0;
}
int 	mVertexBufferIndexVertex;
int 	mVertexBufferIndexColor;
int 	mVertexBufferIndexSize;

void ZPointsMesh::addVertex(const ZVector* vert, float size, const ZColor* color)
{
	if (mNbVertex==mNbVertexMax)
	{
		LOGE("ZPointsMesh::addVertex overflow (max=%i)",mNbVertexMax);
		return;
	}
	if (mbColors && !color)
	{
		LOGW("ZPointsMesh::addVertex no color specified; using white");
		color = ZColor::WHITE();
	}

	mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexVertex+0] = vert->x;
	mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexVertex+1] = vert->y;
	mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexVertex+2] = vert->z;

	if (mbSizes)
		mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexSize] = size;

	if (mbColors)
	{
		mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexColor+0] = color->r;
		mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexColor+1] = color->g;
		mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexColor+2] = color->b;
		mVertexBuffer[mNbVertex*mVertexSize+mVertexBufferIndexColor+3] = color->a;
	}
	mNbVertex++;
}

void ZPointsMesh::Render(SceneContext context, bool parentIsColored, const ZColor* parentColor)
{
	if (!mVertexBuffer)
	{
		return;
	}

	// vertices
	glEnableClientState(GL_VERTEX_ARRAY);
   	glVertexPointer(3, GL_FLOAT, 4*mVertexSize, mVertexBuffer+mVertexBufferIndexVertex);

   	// colors
   	if (mbColors)
   	{
   		glEnableClientState(GL_COLOR_ARRAY);
   		glColorPointer(4,GL_FLOAT,4*mVertexSize,mVertexBuffer+mVertexBufferIndexColor);
   	}
   	else
   	{
   		glDisableClientState(GL_COLOR_ARRAY);
   		glColor4f(1.f,1.f,1.f,1.f);
   	}

    // normals
	glDisableClientState(GL_NORMAL_ARRAY);

	// UVs
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	glDisable(GL_TEXTURE_2D);

	// point size

	if (mbSizes)
	{
		glEnableClientState(GL_POINT_SIZE_ARRAY_OES);
		glPointSizePointerOES(GL_FLOAT,4*mVertexSize,mVertexBuffer+mVertexBufferIndexSize);
	}
	else
		glDisableClientState(GL_POINT_SIZE_ARRAY_OES);

	//glPointSize(9);

	glDrawArrays(GL_POINTS, 0, mNbVertex);

	glDisableClientState(GL_POINT_SIZE_ARRAY_OES);
	glDisableClientState(GL_COLOR_ARRAY);

}

bool ZPointsMesh::IsColored()
{
	return true;	// ALWAYS colored; if not per-point colored, draw it in 100% white
}
