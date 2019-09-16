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

#ifndef __ZWATERRECT__
#define __ZWATERRECT__

#include "ZMesh.h"

class ZWaterRect : public ZMesh
{
protected:

	float mX1,mY1,mX2,mY2,mU1,mU2,mV1,mV2;
	int mResolX, mResolY;
	float mUVamplitude, mFrequency;
	bool mNorms;

	float *mVertsPeriod;
	float *mVertsPeriodSpeed;
	float *mVertsUVAmplitude;


public:
	ZWaterRect(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2, int resolX, int resolY, float UVamplitude, float freq, bool norms);
	virtual ~ZWaterRect();

	virtual void Update(int elapsedTime);

protected:
	void UpdateTopology();
};

#endif // __ZWATERRECT__
