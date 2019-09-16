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

#ifndef __ZPARTICLESYSTEM__
#define __ZPARTICLESYSTEM__

#include "ZObject.h"
#include "ZMaterial.h"
#include "ZVector.h"
#include "release_settings.h"
class ZParticleSystem;
#define PParticleSystem ZSmartPtr<ZParticleSystem>

class ZParticle
{
public:
	ZVector position;
	float 	UV[8];	// 0=bottom left, 1= bottom right, 2=top left, 3=top right
	float	size;
	float 	alpha;

	int 	lifeLength;			// -1 == infinite life!
	int 	lifeTime;			// remaining time

	ZParticle(int variantX, int variantY, int nbVarsX, int nbVarsY)
	{
		float Usize = 1.f / (float)nbVarsX;
		float Vsize = 1.f / (float)nbVarsY;
		float U = (float)variantX / (float)nbVarsX;
		float V = (float)variantY / (float)nbVarsY;
		UV[0]=U;		UV[1]=V+Vsize;
		UV[2]=U+Usize;	UV[3]=V+Vsize;
		UV[4]=U;		UV[5]=V;
		UV[6]=U+Usize;	UV[7]=V;
		position.zero();
		size = 1.f;
		alpha = 1.f;

		lifeLength = 0;			// -1 == infinite life!
		lifeTime = 0;			// remaining time
	}
protected:
	ZParticle() {}

public:

	virtual void Update(ZParticleSystem *system, int elapsedTime)
	{
		if (lifeTime>0)
		{
			lifeTime -= elapsedTime;
			if (lifeTime<0) lifeTime = 0;
		}

	}
};



class ZParticleSystem : public ZObject
{
public:
	ZParticleSystem();
	virtual ~ZParticleSystem();

	virtual bool GetType() {return ZOBJECT_TYPE_PARTICLESYSTEM;}
	virtual bool IsKindOf(int type)	{return (type==ZOBJECT_TYPE_PARTICLESYSTEM);}

	void Init(int nbParticles);
	virtual void ClearParticles();
	void SetEmmiterPos(ZVector *pos)	{ m_EmmiterPos.copy(pos); }
	void Pause(bool paused)				{ m_Paused = paused; }

	virtual void Update(int elapsedTime);
	virtual void PrepareForRendering(const ZCamera* camera);		// prepare render lists

	virtual void Render(SceneContext context, bool parentIsColored, const ZColor* parentColor);
	virtual bool IsColored() {return true;}

public:
    PMaterial mMaterial;
    int 	mMaterialVariantsX;
    int 	mMaterialVariantsY;
    virtual ZParticle* ParticleFactory();	// to be overimplemented, so that we can have new particles types

protected:
    ZVector	m_EmmiterPos;

	ZParticle**	m_Particles;
	int		m_NbParticles;
	int		m_NbPreparedParticles;

	float*	m_VertexBuffer;
	float*	m_ColorBuffer;
	float*	m_UVBuffer;
	short*	m_IndexBuffer;

	bool	m_Paused;

};

#endif // __ZPARTICLESYSTEM__
