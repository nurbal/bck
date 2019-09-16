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

#ifndef __ZOBJECT__
#define __ZOBJECT__

#include "ZSmartPtr.h"
#include "release_settings.h"

#define ZOBJECT_TYPE_UNDEFINED				0
#define ZOBJECT_TYPE_INSTANCE				1
#define ZOBJECT_TYPE_MESH					2
#define ZOBJECT_TYPE_PARTICLESYSTEM			3
#define ZOBJECT_TYPE_PARTICLESYSTEMNEWTON	4
#define ZOBJECT_TYPE_POINTSMESH				5

class ZObject;
#define PObject ZSmartPtr<ZObject>

class ZColor;
class ZCamera;

class ZObject : public ZSmartObject
{
public:
	ZObject();

	virtual bool GetType() {return ZOBJECT_TYPE_UNDEFINED;}
	virtual bool IsKindOf(int type) {return false;}

	enum SceneContext
	{
		NORMAL,
		SKY,
		UI
	};

	virtual void PrepareForRendering(const ZCamera* camera) {};
	virtual void Render(SceneContext context, bool parentIsColored, const ZColor* parentColor) = 0;

	virtual bool IsColored() {return false;}

	enum LightMode
	{
		VERTEXCOLORS,	// unlightable (done internlly by vertex colors)
		LIGHTABLE		// lightable
	};
	virtual LightMode GetLightMode() {return LIGHTABLE;}

	virtual void Update(int elapsedTime) {};	// only used by some objects (p6, water rects) & instances

};

#endif // __ZOBJECT__
