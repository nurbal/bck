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

#ifndef __ZPARTICLESYSTEMNEWTON__
#define __ZPARTICLESYSTEMNEWTON__

#include "ZObject.h"
#include "ZMaterial.h"
#include "ZVector.h"
#include "ZParticleSystem.h"
#include "release_settings.h"



class ZParticleNewton : public ZParticle
{
public:
	ZVector speed;
	ZVector gravity;
	float originalSize;

	ZParticleNewton(int variantX, int variantY, int nbVarsX, int nbVarsY) : ZParticle(variantX, variantY, nbVarsX, nbVarsY) {}
protected:
	ZParticleNewton() {}

public:
	void Update(ZParticleSystem *system, int elapsedTime);

};



class ZParticleSystemNewton : public ZParticleSystem
{
	friend class ZParticleNewton;
public:
	ZParticleSystemNewton();

	virtual bool GetType() {return ZOBJECT_TYPE_PARTICLESYSTEMNEWTON;}
	virtual bool IsKindOf(int type)	{return (type==ZOBJECT_TYPE_PARTICLESYSTEMNEWTON);}

	virtual void Update(int elapsedTime);
    virtual ZParticle* ParticleFactory();	// to be overimplemented, so that we can have new particles types

    void 		SetParameters(const ZVector *emmiterrandomX,const ZVector *emmiterrandomY,const ZVector *emmiterrandomZ,const ZVector *emmiterrandomcoefs, const ZVector *speed, const ZVector *speedrandom, const ZVector *gravity, const ZVector *gravityrandom, int lifetime, int lifetimerandom, int initialLaunchTime, float size, float sizerandom, float sizefactorbirth, float sizefactordeath, bool oneshot);
    void Fire();
	virtual void ClearParticles();

protected:
    ZVector 	m_EmmiterRandomX;
    ZVector 	m_EmmiterRandomY;
    ZVector 	m_EmmiterRandomZ;
    ZVector 	m_Speed;
    ZVector		m_SpeedRandom;
    ZVector 	m_Gravity;
    ZVector		m_GravityRandom;
    int			m_ParticleLifeTime;
    int			m_ParticleLifeTimeRandom;
    float		m_ParticleSize;
    float		m_ParticleSizeRandom;
    float 		m_ParticleSizeFactorBirth;
    float 		m_ParticleSizeFactorDeath;
    bool		m_OneShot;
    bool 		m_Fired;
    int			m_InitialLaunchParticleDelay;	// delay between 2 particles
    int 		m_InitialLaunchParticleTimer;	// timer between 2 particles
    int			m_InitialLaunchParticleCount;	// remaining particles to launch
};

#endif // __ZPARTICLESYSTEMNEWTON__
