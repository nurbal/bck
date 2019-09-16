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

#include "ZParticleSystem.h"
#include "ZRenderer.h"
#include "helpers.h"
#include "log.h"
#include <GLES/gl.h>


ZParticleSystem::ZParticleSystem() : ZObject()
{
	m_NbParticles = 0;
	m_Particles = 0;
	m_NbPreparedParticles = 0;
	m_VertexBuffer = 0;
	m_ColorBuffer = 0;
	m_UVBuffer = 0;
	m_IndexBuffer = 0;
	m_Paused = false;
}

ZParticleSystem::~ZParticleSystem()
{
//	LOGD("ZParticleSystem::~ZParticleSystem()");
	if (m_NbParticles)
	{
//		LOGD("ZParticleSystem::~ZParticleSystem() 1");
		delete[] m_VertexBuffer;
//		LOGD("ZParticleSystem::~ZParticleSystem() 2");
		delete[] m_ColorBuffer;
//		LOGD("ZParticleSystem::~ZParticleSystem() 3");
		delete[] m_UVBuffer;
//		LOGD("ZParticleSystem::~ZParticleSystem() 4");
		delete[] m_IndexBuffer;
//		LOGD("ZParticleSystem::~ZParticleSystem() 5");
		for (int i=0; i<m_NbParticles; i++) delete m_Particles[i];
//		LOGD("ZParticleSystem::~ZParticleSystem() 6");
		delete[] m_Particles;
//		LOGD("ZParticleSystem::~ZParticleSystem() 7");
	}
}


ZParticle* ZParticleSystem::ParticleFactory()	// to be overimplemented, so that we can have new particles types
{
	return new ZParticle(rand()%mMaterialVariantsX,rand()%mMaterialVariantsY,mMaterialVariantsX,mMaterialVariantsY);
}

void ZParticleSystem::Init(int nbParticles)
{
	if (m_NbParticles)
	{
		for (int i=0; i<m_NbParticles; i++)
			if (m_Particles[i])
				delete m_Particles[i];
		delete[] m_Particles;
	}

	// allocate particles
	m_NbParticles = nbParticles;
	m_Particles = new ZParticle*[nbParticles];
	for (int i=0; i<nbParticles; i++)
	{
		m_Particles[i] = ParticleFactory();
	}
	// quads : we need 4 verts, 4 color, 4 UVs pairs, and 2 triangles per particle
	m_VertexBuffer = new float[nbParticles*12];
	m_ColorBuffer = new float[nbParticles*16];
	m_UVBuffer = new float[nbParticles*8];
	m_IndexBuffer = new short[nbParticles*6];
}

void ZParticleSystem::ClearParticles()
{
	for (int i=0; i<m_NbParticles; i++)
		m_Particles[i]->lifeLength=m_Particles[i]->lifeTime=0;
}



void ZParticleSystem::Update(int elapsedTime)
{
	if (m_Paused) return;
	// to be overimplemented
	for (int i=0; i<m_NbParticles; i++)
		if (m_Particles[i]->lifeLength!=0)
			m_Particles[i]->Update(this,elapsedTime);
}

void ZParticleSystem::PrepareForRendering(const ZCamera* camera)
{
	if (!m_NbParticles)
	{
		return;	// should not happen
	}

	// degraded mode: very, _VERY_ simple quads...

	// update particles vertex, uv, color and index buffers
	m_NbPreparedParticles = 0;
	for (int i=0; i<m_NbParticles; i++)
	{
		if (m_Particles[i]->lifeTime!=0 && m_Particles[i]->alpha>0.f)
		{
			static ZVector particleWidth;
			static ZVector particleHeigth;
			// first, compute particle half widht/height
			particleWidth.mul(camera->getCameraRight(), m_Particles[i]->size/2);
			particleHeigth.mul(camera->getCameraUp(), m_Particles[i]->size/2);

			// 2-3
			// !\!
			// 0-1

			// 0
			m_VertexBuffer[m_NbPreparedParticles*12+0] = (m_Particles[i]->position.x-particleWidth.x-particleHeigth.x);
			m_VertexBuffer[m_NbPreparedParticles*12+1] = (m_Particles[i]->position.y-particleWidth.y-particleHeigth.y);
			m_VertexBuffer[m_NbPreparedParticles*12+2] = (m_Particles[i]->position.z-particleWidth.z-particleHeigth.z);
			// 1
			m_VertexBuffer[m_NbPreparedParticles*12+3] = (m_Particles[i]->position.x+particleWidth.x-particleHeigth.x);
			m_VertexBuffer[m_NbPreparedParticles*12+4] = (m_Particles[i]->position.y+particleWidth.y-particleHeigth.y);
			m_VertexBuffer[m_NbPreparedParticles*12+5] = (m_Particles[i]->position.z+particleWidth.z-particleHeigth.z);
			// 2
			m_VertexBuffer[m_NbPreparedParticles*12+6] = (m_Particles[i]->position.x-particleWidth.x+particleHeigth.x);
			m_VertexBuffer[m_NbPreparedParticles*12+7] = (m_Particles[i]->position.y-particleWidth.y+particleHeigth.y);
			m_VertexBuffer[m_NbPreparedParticles*12+8] = (m_Particles[i]->position.z-particleWidth.z+particleHeigth.z);
			// 3
			m_VertexBuffer[m_NbPreparedParticles*12+9] = (m_Particles[i]->position.x+particleWidth.x+particleHeigth.x);
			m_VertexBuffer[m_NbPreparedParticles*12+10] = (m_Particles[i]->position.y+particleWidth.y+particleHeigth.y);
			m_VertexBuffer[m_NbPreparedParticles*12+11] = (m_Particles[i]->position.z+particleWidth.z+particleHeigth.z);

			for (int j=0; j<4; j++)
			{
				m_ColorBuffer[m_NbPreparedParticles*16+j*4+0] = 1.f;
				m_ColorBuffer[m_NbPreparedParticles*16+j*4+1] = 1.f;
				m_ColorBuffer[m_NbPreparedParticles*16+j*4+2] = 1.f;
				m_ColorBuffer[m_NbPreparedParticles*16+j*4+3] = m_Particles[i]->alpha;
			}

			for (int j=0; j<8; j++)
				m_UVBuffer[m_NbPreparedParticles*8+j] = m_Particles[i]->UV[j];

			m_IndexBuffer[m_NbPreparedParticles*6+0] = m_NbPreparedParticles*4;
			m_IndexBuffer[m_NbPreparedParticles*6+1] = m_NbPreparedParticles*4+1;
			m_IndexBuffer[m_NbPreparedParticles*6+2] = m_NbPreparedParticles*4+2;

			m_IndexBuffer[m_NbPreparedParticles*6+3] = m_NbPreparedParticles*4+1;
			m_IndexBuffer[m_NbPreparedParticles*6+4] = m_NbPreparedParticles*4+3;
			m_IndexBuffer[m_NbPreparedParticles*6+5] = m_NbPreparedParticles*4+2;

			m_NbPreparedParticles++;
		}
	}
}


void ZParticleSystem::Render(SceneContext context, bool parentIsColored, const ZColor* parentColor)
{
	if (!m_NbPreparedParticles)
	{
		return;	// should not happen
	}
	// let's draw!

	glEnableClientState(GL_VERTEX_ARRAY);
    glVertexPointer(3, GL_FLOAT, 0, m_VertexBuffer);

	glDisableClientState(GL_NORMAL_ARRAY);

	if (mMaterial)
	{
		mMaterial->bind();
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnable(GL_TEXTURE_2D);
        glTexCoordPointer(2, GL_FLOAT, 0, m_UVBuffer);
	}
	else
	{
        glDisable(GL_TEXTURE_2D);
	}

    glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

//    glDisableClientState(GL_COLOR_ARRAY);
    glEnableClientState(GL_COLOR_ARRAY);
    glColorPointer(4,GL_FLOAT,0,m_ColorBuffer);

    glDisable( GL_CULL_FACE);

    glDrawElements(GL_TRIANGLES, m_NbPreparedParticles*6, GL_UNSIGNED_SHORT, m_IndexBuffer);

    glDisableClientState(GL_VERTEX_ARRAY);
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    glDisableClientState(GL_COLOR_ARRAY);

}

