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

#include "P6PoolLinkBreak.h"
#include "Link.h"
#include "../Lib3D/ZParticleSystemNewton.h"


P6PoolLinkBreak::P6PoolLinkBreak(ZScene *scene, PMaterial material, int matVariantsX, int matVariantsY, int nbParticles, int poolSize)
{
	mMaterial = material;
	mMatVariantsX = matVariantsX;
	mMatVariantsY = matVariantsY;
	Init(scene,nbParticles,poolSize);
}

ZParticleSystem* P6PoolLinkBreak::P6Factory()
{
	ZParticleSystemNewton* p6 = new ZParticleSystemNewton();
	p6->mMaterial = mMaterial;
	p6->mMaterialVariantsX = mMatVariantsX;
	p6->mMaterialVariantsY = mMatVariantsY;
	return p6;
}

void P6PoolLinkBreak::Fire(Link* l)
{
	ZVector emmiterCoefs,emmiterSpeed;
	emmiterCoefs.set(l->GetLength(),1,0);
	emmiterSpeed.set(2,2,2);

	ZParticleSystemNewton *p6 = (ZParticleSystemNewton*)GetNext();

	ZVector dir, pos;
	l->GetDirection(&dir);
	l->GetPosition(&pos);

	p6->SetParameters(&dir,ZVector::Y(),ZVector::Null(),&emmiterCoefs, ZVector::Null(), &emmiterSpeed, ZVector::Null(), ZVector::Null(), 2000, 1500, 0, 0.2f, 0.1f, 1.f, 2.f, true);
	p6->SetEmmiterPos(&pos);
	p6->Fire();
}
