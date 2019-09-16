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

#ifndef __ZSCENE__
#define __ZSCENE__


// Classe ZQuaternion
// en code natif, toutes les donn�es sont en virgule fixe!

#include "ZSmartPtr.h"
#include "ZList.h"
#include "ZObject.h"
#include "ZInstance.h"
#include "release_settings.h"

class ZRenderer;
class ZCamera;

class ZScene;
#define PScene ZSmartPtr<ZScene>

class ZQueuedInstance
{
public:
	enum Type
	{
		REMOVE,
		ADD,
		ADDALPHA,
		ADDSKY,
		ADDSKYALPHA,
		ADD2D
	};

	ZQueuedInstance(Type type,const PInstance& inst)
	{
		mType = type;
		mInstance = inst;
	}

	Type mType;
	PInstance mInstance;
};

class ZScene : public ZSmartObject
{
	friend class ZRenderer;
public:
	ZScene();
	~ZScene();

	void SetLight(float ambient,float diffuse,float specular, const ZVector* position);
	void Update(int elapsedTime);

	// scene containers...
	void Add(const PInstance& inst);
	void AddSky(const PInstance& inst);
	void Add2D(const PInstance& inst);
	void Remove(const PInstance& inst);
	void Clear();

protected:
	// light
	ZVector 	mLightPosition;
	float		mAmbientLight;
	float		mDiffuseLight;
	float		mSpecularLight;

	// objects and their instances to render...
//	ZSmartArray<ZInstance>	*mInstances;
//	ZSmartArray<ZInstance>	*mInstancesAlpha;
//	ZSmartArray<ZInstance>	*mInstancesSky;

	// currently inserted instances
	ZList<PInstance>	mInstances;
	ZList<PInstance>	mInstancesAlpha;
	ZList<PInstance>	mInstancesSky;
	ZList<PInstance>	mInstancesSkyAlpha;
	ZList<PInstance>	mInstances2D;

	// sort instances before render, farthest first, nearest last.
	void SortInstances(ZList<PInstance> *list);

	// insertion / remove lists (waiting for next render loop)

	ZList<ZQueuedInstance*> mWaitingInstances;

	// prepare for rendering: copy transformers, insert/remove instances, etc...
	void	PrepareForRendering(const ZCamera *camera,const ZCamera *camera2D);

};





#endif // __ZSCENE__
