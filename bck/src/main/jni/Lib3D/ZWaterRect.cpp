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

#include "ZWaterRect.h"
#include "helpers.h"
#include "ZVector.h"

ZWaterRect::ZWaterRect(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int resolX, int resolY, float UVamplitude, float freq, bool norms)
	: ZMesh()
{
	mX1 = x1;
	mX2 = x2;
	mY1 = y1;
	mY2 = y2;
	mU1 = u1;
	mU2 = u2;
	mV1 = v1;
	mV2 = v2;
	mResolX = resolX;
	mResolY = resolY;
	mUVamplitude = UVamplitude;
	mFrequency = freq;
	mNorms = norms;
	int nbVerts = (resolX+1)*(resolY+1);
	allocMesh(nbVerts,resolX*resolY*2,norms);
	mVertsPeriod = new float[nbVerts];
	mVertsPeriodSpeed = new float[nbVerts];
	mVertsUVAmplitude = new float[nbVerts];
	// init avec du bon gros random bien bien gras... ou pas
	for (int i=0; i<nbVerts; i++)
	{
		mVertsPeriod[i] = UNITRANDF*6.28318f;
		mVertsPeriodSpeed[i] = 6.28318f * freq * (1.f-2.f*UNITRANDF);
		mVertsUVAmplitude[i] = UNITRANDF * mUVamplitude;
	}
	// init de la topologie une premi�re fois
	UpdateTopology();
}

ZWaterRect::~ZWaterRect()
{
	delete [] mVertsPeriod;
	delete [] mVertsPeriodSpeed;
	delete [] mVertsUVAmplitude;
}

static ZVector v;
void ZWaterRect::UpdateTopology()
{
	resetMesh();
	// verts
	float stepX = (mX2-mX1)/(float)mResolX;
	float stepY = (mY2-mY1)/(float)mResolY;
	float stepU = (mU2-mU1)/(float)mResolX;
	float stepV = (mV2-mV1)/(float)mResolY;
	const ZVector *norm=mNorms?ZVector::Z():0;
	for (int y=0; y<=mResolY; y++)
		for (int x=0; x<=mResolX; x++)
		{
			int vertIndex = x+y*(mResolX+1);
			v.set(mX1 + stepX*(float)x, mY1 + stepY*(float)y, 0);
			addVertex(&v,norm,
					mU1+stepU*(float)x + mVertsUVAmplitude[vertIndex]*cosf(mVertsPeriod[vertIndex]),
					mV1+stepV*(float)y + mVertsUVAmplitude[vertIndex]*sinf(mVertsPeriod[vertIndex]));
		}
	// faces
	for (int y=0; y<mResolY; y++)
		for (int x=0; x<mResolX; x++)
		{
			int i0 = x+y*(mResolX+1);
			int i1 = i0+1;
			int i2 = i0+mResolX+1;
			int i3 = i2+1;
			addFace(i0,i1,i3);
			addFace(i0,i3,i2);
		}
}
void ZWaterRect::Update(int elapsedTime)
{
	float dt = (float)elapsedTime * 0.001f;
	for (int i=0; i<(mResolX+1)*(mResolY+1); i++)
	{
		mVertsPeriod[i] += dt*mVertsPeriodSpeed[i];
		while (mVertsPeriod[i]>6.28318f) mVertsPeriod[i]-=6.28318f;
	}
	UpdateTopology();

}





