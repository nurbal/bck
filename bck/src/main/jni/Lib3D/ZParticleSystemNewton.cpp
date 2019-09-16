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

#include "ZParticleSystemNewton.h"
#include "ZRenderer.h"
#include "helpers.h"
#include "log.h"
#include <GLES/gl.h>


void ZParticleNewton::Update(ZParticleSystem *system, int elapsedTime)
{
	ZParticleSystemNewton *sys = (ZParticleSystemNewton*)system;
	float dt = (float)elapsedTime*0.001f;
	speed.addMul(&gravity, dt);
	position.addMul(&speed, dt);
	alpha = (float)lifeTime / (float)lifeLength;
	size = originalSize * (sys->m_ParticleSizeFactorBirth * alpha + sys->m_ParticleSizeFactorDeath*(1.f-alpha));
	ZParticle::Update(system, elapsedTime);
}

ZParticle* ZParticleSystemNewton::ParticleFactory()	// to be overimplemented, so that we can have new particles types
{
	return new ZParticleNewton(rand()%mMaterialVariantsX,rand()%mMaterialVariantsY,mMaterialVariantsX,mMaterialVariantsY);
}

ZParticleSystemNewton::ZParticleSystemNewton() : ZParticleSystem()
{
	m_EmmiterRandomX.zero();
	m_EmmiterRandomY.zero();
	m_EmmiterRandomZ.zero();
	m_Speed.zero();
	m_SpeedRandom.zero();
	m_Gravity.zero();
	m_GravityRandom.zero();
	m_ParticleLifeTime = 0;
	m_ParticleLifeTimeRandom = 0;
	m_ParticleSize = 0.f;
	m_ParticleSizeRandom = 0.f;
	m_InitialLaunchParticleTimer = 0;
	m_InitialLaunchParticleCount = 0;
	m_Fired = false;
}

void ZParticleSystemNewton::Update(int elapsedTime)
{
	if (m_Paused) return;
	ZParticleSystem::Update(elapsedTime);
	// initial launch timer...
	if (m_InitialLaunchParticleCount)
	{
		//LOGE("ZParticleSystemNewton::Update dt=%d timer=%d/%d count=%d",elapsedTime,m_InitialLaunchParticleTimer,m_InitialLaunchParticleDelay,m_InitialLaunchParticleCount);
		m_InitialLaunchParticleTimer -= elapsedTime;
	}
	// re-emit dead particles
	for (int i=0; i<m_NbParticles; i++)
	{
		if ((!m_OneShot || m_Fired) && m_InitialLaunchParticleTimer<=0) // on ne r�emet que si on est en jet continu, ou si on est en cours de "tir" initial
		{
			ZParticleNewton *p = (ZParticleNewton*)m_Particles[i];
			if (p->lifeTime==0)
			{
				if (m_InitialLaunchParticleCount)
				{
					//LOGE("ZParticleSystemNewton::Update launch initial count=%d",m_InitialLaunchParticleCount);
					m_InitialLaunchParticleTimer += m_InitialLaunchParticleDelay;
					m_InitialLaunchParticleCount--;
					if (!m_InitialLaunchParticleCount)
					{
						m_Fired=false;
						m_InitialLaunchParticleTimer=0;
					}
				}

				float emitterRandX = UNITRAND - 0.5f;
				float emitterRandY = UNITRAND - 0.5f;
				float emitterRandZ = UNITRAND - 0.5f;
				p->position.set(m_EmmiterPos.x+m_EmmiterRandomX.x*emitterRandX+m_EmmiterRandomY.x*emitterRandY+m_EmmiterRandomZ.x*emitterRandZ,
						m_EmmiterPos.y+m_EmmiterRandomX.y*emitterRandX+m_EmmiterRandomY.y*emitterRandY+m_EmmiterRandomZ.y*emitterRandZ,
						m_EmmiterPos.z+m_EmmiterRandomX.z*emitterRandX+m_EmmiterRandomY.z*emitterRandY+m_EmmiterRandomZ.z*emitterRandZ);
				p->speed.set(RANDOMIZED(m_Speed.x,m_SpeedRandom.x),
						RANDOMIZED(m_Speed.y,m_SpeedRandom.y),
						RANDOMIZED(m_Speed.z,m_SpeedRandom.z));
				p->gravity.set(RANDOMIZED(m_Gravity.x,m_GravityRandom.x),
						RANDOMIZED(m_Gravity.y,m_GravityRandom.y),
						RANDOMIZED(m_Gravity.z,m_GravityRandom.z));
				p->lifeLength = p->lifeTime = RANDOMIZEDI(m_ParticleLifeTime,m_ParticleLifeTimeRandom);
				p->originalSize = RANDOMIZED(m_ParticleSize,m_ParticleSizeRandom);

				p->Update(this,0);
			}
		}
	}
}

void  ZParticleSystemNewton::SetParameters(const ZVector *emmiterrandomX,const ZVector *emmiterrandomY,const ZVector *emmiterrandomZ,const ZVector *emmiterrandomcoefs, const ZVector *speed, const ZVector *speedrandom, const ZVector *gravity, const ZVector *gravityrandom, int lifetime, int lifetimerandom, int initialLaunchTime, float size, float sizerandom, float sizefactorbirth, float sizefactordeath, bool oneshot)
{
	m_EmmiterRandomX.mul(emmiterrandomX,emmiterrandomcoefs->x);
	m_EmmiterRandomY.mul(emmiterrandomY,emmiterrandomcoefs->y);
	m_EmmiterRandomZ.mul(emmiterrandomZ,emmiterrandomcoefs->z);
	m_Speed.copy(speed);
	m_SpeedRandom.copy(speedrandom);
	m_Gravity.copy(gravity);
	m_GravityRandom.copy(gravityrandom);
	m_ParticleLifeTime = lifetime;
	m_ParticleLifeTimeRandom = lifetimerandom;
	m_ParticleSize = size;
	m_ParticleSizeRandom = sizerandom;
	m_ParticleSizeFactorBirth = sizefactorbirth;
	m_ParticleSizeFactorDeath = sizefactordeath;
	m_OneShot = oneshot;
	//m_Fired = false;
	m_InitialLaunchParticleCount = m_NbParticles;
	m_InitialLaunchParticleTimer = m_InitialLaunchParticleDelay = initialLaunchTime / m_NbParticles;
//LOGE("ZParticleSystemNewton::SetParameters launchtime=%d count=%d timer=%d/%d",initialLaunchTime,m_InitialLaunchParticleCount,m_InitialLaunchParticleTimer,m_InitialLaunchParticleDelay);

}


void ZParticleSystemNewton::ClearParticles()
{
	ZParticleSystem::ClearParticles();
	// reset initial launch timer
	m_InitialLaunchParticleCount = m_NbParticles;
	m_InitialLaunchParticleTimer = m_InitialLaunchParticleDelay;

	m_Fired = false;
}



void ZParticleSystemNewton::Fire()
{
	m_Fired = true;
}
