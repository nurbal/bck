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

#ifndef __P6POOL__
#define __P6POOL__


#include "../Lib3D/Lib3D.h"

class ZScene;

class P6Pool
{
public:
	P6Pool();
	virtual ~P6Pool();

	virtual ZParticleSystem* P6Factory() = 0;

private:
	PParticleSystem* mP6;
	PInstance* mP6Instance;
	int mIndex;
	int mNbP6;

public:
	void Init(ZScene *scene,int nbParticles, int nbSystems);
	void Destroy(ZScene *scene);
	void SetVisible(bool visible);
	ZParticleSystem* GetNext();

};

#endif // __P6POOL__
