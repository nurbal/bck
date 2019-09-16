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

#include "LinkJack.h"
#include <GLES/gl.h>
#include <math.h>
#include "Level.h"

LinkJack::LinkJack() : Link()
{
	mDynamicNominalLengthFactor = 1;
}

bool LinkJack::IsBroken()
{
	if (mBroken) return true;

	// calcul de la rupture
	float lengthFactor = GetNominalLengthFactor();
	mBreaking = (lengthFactor>JACK_MAXLENGTHFACTOR || lengthFactor<JACK_MINLENGTHFACTOR);

	if (mBreaking && mBreakTimer>BREAK_TIME_THRESHOLD)
		SetBroken();

	return mBroken;
}

float LinkJack::GetStressFactor()
{
	float lengthFactor = GetNominalLengthFactor();
	if (lengthFactor<1.f)
		return (1.f-lengthFactor)/(1.f-JACK_MINLENGTHFACTOR);
	else
		return (lengthFactor-1.f)/(JACK_MAXLENGTHFACTOR-1.f);
}

void LinkJack::SetBroken()
{
	mBroken = true;
	Level::instance->FireParticleSystemLinkJackBreak(this);
}

void LinkJack::StartSimulationFrame(float dt)
{
	if (!IsBroken())
	{
		if (mBreaking)
			mBreakTimer += dt;
		else
			mBreakTimer=0;
	}
	mSimulationFrameCounter = JACK_UPDATE_ITERATIONS;
}

bool LinkJack::UpdateSimulation(float dt)
{
	if (0==mSimulationFrameCounter) return false;

	ZVector D,D1,D2;
	D.sub(mNodes[1]->GetPosition(this),mNodes[0]->GetPosition(this));
	float d = D.norme();

	if (!IsBroken())
	{
		if (d>0.00001f)	// avoids divide by 0!
		{
			D.mul((d-GetDynamicNominalLength())/d);

			// corrects the distance between the two nodes
			if (!mNodes[0]->mFixed && !mNodes[1]->mFixed)
			{
				// move both nodes
				float w1 = mNodes[0]->GetWeight(this);
				float w2 = mNodes[1]->GetWeight(this);
				D1.mul(&D,w2/(w1+w2));
				D2.sub(&D1,&D); //D2.mul(D,-w1/(w1+w2));
				D1.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*w1));
				D2.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*w2));
				mNodes[0]->Move(&D1,this);
				mNodes[1]->Move(&D2,this);
			}
			else if (!mNodes[0]->mFixed)
			{
				// move first node only
				D.mul(expf(-dt*LINK_WEIGHT_TIME_FACTOR*mNodes[0]->GetWeight(this)));
				mNodes[0]->Move(&D,this);
			}
			else if (!mNodes[1]->mFixed)
			{
				// move second node only
				D.mul(-expf(-dt*LINK_WEIGHT_TIME_FACTOR*mNodes[1]->GetWeight(this)));
				mNodes[1]->Move(&D,this);
			}
		}
	}

	mSimulationFrameCounter--;
	return (mSimulationFrameCounter>0);
}

void LinkJack::InitGraphics(ZScene *scene,bool is3D)
{
	//TODO
}

void LinkJack::ResetGraphics(ZScene *scene)
{
	//TODO
}

void LinkJack::UpdateGraphics(int elsapedTime)
{
	//TODO
}
