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

#include "../Lib3D/Lib3D.h"

#include "release_settings.h"
#include "Link.h"

class LinkBar : public Link
{
public:
	virtual void RecomputeInitialState();

	virtual bool Is(Type t) {return t==bar;}

	virtual float GetWeight() { return BAR_DENSITY*mNominalLength; }
	void AddDynamicWeight(float x, float w);

	virtual void StartSimulationFrame(float dt);
	virtual bool UpdateSimulation(float dt);

	virtual bool IsBroken();
	virtual void SetBroken();
	virtual float GetStressFactor();

	void SetIsRails(bool rails) {mIsRails = rails;}
	bool IsRails() {return mIsRails;}
private:
	float GetNominalLengthFactor() { return GetLength()/mNominalLength; }
	bool mIsRails;
	bool mIs3D;
	bool mIsRailsHeight;

	ZMesh *mBarsMesh;	// mesh dynamique de toutes les barres, optim de fou

	// graphics
public:
	virtual void InitGraphics(ZScene *scene,bool is3D);
	virtual void ResetGraphics(ZScene *scene);
	virtual void UpdateGraphics(int elpaseTime);

};
