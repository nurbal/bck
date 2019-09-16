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

class LinkJack : public Link
{
public:
	LinkJack();

	virtual bool Is(Type t) {return t==jack;}

	virtual float GetWeight() { return JACK_DENSITY*mNominalLength; }

	virtual void StartSimulationFrame(float dt);
	virtual bool UpdateSimulation(float dt);

	virtual bool IsBroken();
	virtual void SetBroken();
	virtual float GetStressFactor();

public:
	float mDynamicNominalLengthFactor;
	float GetDynamicNominalLength() { return mNominalLength*mDynamicNominalLengthFactor; }
	float GetNominalLengthFactor() { return GetLength()/GetDynamicNominalLength(); }

	// graphics
public:
	virtual void InitGraphics(ZScene *scene,bool is3D);
	virtual void ResetGraphics(ZScene *scene);
	virtual void UpdateGraphics(int elpaseTime);
};
