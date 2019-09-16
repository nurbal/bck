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

#ifndef __P6POOLLINKBREAK__
#define __P6POOLLINKBREAK__

#include "../Lib3D/Lib3D.h"

#include "P6Pool.h"

class ZMaterial;
class ZScene;
class ZParticleSystem;
class Link;

class P6PoolLinkBreak : public P6Pool
{
public:
	P6PoolLinkBreak(ZScene *scene, PMaterial material, int matVariantsX, int matVariantsY, int nbParticles, int poolSize);

	ZParticleSystem* P6Factory();
	void Fire(Link* l);

private:
	PMaterial mMaterial;
	int mMatVariantsX,mMatVariantsY;
};

#endif // __P6POOLLINKBREAK__
