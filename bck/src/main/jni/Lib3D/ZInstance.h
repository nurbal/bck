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

#ifndef __ZINSTANCE__
#define __ZINSTANCE__

#include "ZList.h"
#include "ZObject.h"
#include "ZColor.h"
#include "ZSRT.h"
#include "ZSmartPtr.h"
#include "release_settings.h"

class ZInstance;
#define PInstance ZSmartPtr<ZInstance>

class ZCamera;

class ZInstance : public ZObject
{
	friend class ZRenderer;
	friend class ZScene;
protected:
	ZList<PObject>	mObjects;

public:
	ZInstance();
	//ZInstance(ZObject* obj);
	void Add(ZObject* obj);
	void ResetObjects();

	virtual bool GetType() {return ZOBJECT_TYPE_INSTANCE;}
	virtual bool IsKindOf(int type)	{return type==ZOBJECT_TYPE_INSTANCE;}
	virtual void Update(int elapsedTime);


protected:
	void PrepareForRendering(const ZCamera *camera);
	void Render(SceneContext context, bool parentIsColored, const ZColor* parentColor);

	// position & visibility data
	ZSRT mTransformer;

	bool mVisible;
	ZColor mColor;
	bool mIsColored;

	// replicated data for rendering...
	ZMatrix mRenderMatrix;
	bool mRenderVisible;
	ZColor mRenderColor;
	bool mRenderIsColored;

	// sort value... (for alpha instances)
	float	mRenderSortValue;


public:
	void SetVisible(bool visible);
	bool IsVisible() {return mVisible;}
	void SetColor(ZColor *color);
	float GetAlpha() {return mColor.a;}
	void SetIsColored(bool isColored);
	virtual bool IsColored() {return mIsColored;}

	void SetTranslation (ZVector *translation);
	void SetTranslation (float x, float y, float z);
	void SetScale (ZVector *scale);
	void SetScale (float x, float y, float z);
	void SetRotation (ZQuaternion *rotation);

	void Translate(const ZVector* v);
	void Translate(float x,float y,float z);
	void Rotate(const ZQuaternion* q);
	void Rotate(const ZVector* v,float angle);
	void Rotate(const ZVector* v);

};

#endif //__ZINSTANCE__
