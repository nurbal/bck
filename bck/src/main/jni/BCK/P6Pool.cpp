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

#include "P6Pool.h"
#include "log.h"

#include "../Lib3D/ZParticleSystem.h"
#include "../Lib3D/ZInstance.h"
#include "../Lib3D/ZScene.h"

P6Pool::P6Pool()
{
	mIndex = 0;
	mNbP6 = 0;
	mP6 = 0;
	mP6Instance = 0;
}

P6Pool::~P6Pool()
{
}


void P6Pool::Init(ZScene *scene,int nbParticles, int nbSystems)
{
	mNbP6 = nbSystems;
	mP6 = new PParticleSystem[mNbP6];
	mP6Instance = new PInstance[mNbP6];
	for (int i=0; i<mNbP6; i++)
	{
		mP6[i] = P6Factory();
		mP6[i]->Init(nbParticles);
		mP6Instance[i] = new ZInstance();
		mP6Instance[i]->Add(mP6[i]);
		scene->Add(mP6Instance[i]);
	}
}

void P6Pool::Destroy(ZScene *scene)
{
	LOGD("P6Pool::Destroy");
	for (int i=0; i<mNbP6; i++)
	{
		scene->Remove(mP6Instance[i]);
	}
	LOGD("P6Pool::Destroy 5");
	delete[] mP6Instance;
	LOGD("P6Pool::Destroy 6");
	delete[] mP6;
}

void P6Pool::SetVisible(bool visible)
{
	for (int i=0; i<mNbP6; i++)
	{
		mP6Instance[i]->SetVisible(visible);
	}
}

ZParticleSystem* P6Pool::GetNext()
{
	ZParticleSystem* result = mP6[mIndex];
	mIndex  = (mIndex+1)%mNbP6;
	return result;
}
