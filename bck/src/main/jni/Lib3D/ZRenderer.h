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

#ifndef __ZRENDERER__
#define __ZRENDERER__


#include "ZSmartPtr.h"
#include "ZVector.h"
#include "ZQuaternion.h"
#include "ZMatrix.h"
#include "ZMaterial.h"
#include "ZScene.h"
#include "ZInstance.h"
//#include "helpers.h"
#include "ZList.h"
#include "ZCamera.h"
#include "ZOrthoCamera.h"
#include "release_settings.h"

class ZRenderer
{

public:
	ZRenderer();
	virtual ~ZRenderer();

	// point-sprites (pour les particle systems)
	bool isPointSpriteSupported() {return  mPointSpriteSupported;}

	void emulateScreenSize(int width, int height);

	int getScreenWidth() {return mEmulatedScreenWidth==0?mNativeScreenWidth:mEmulatedScreenWidth;}
	int getScreenHeight() {return mEmulatedScreenHeight==0?mNativeScreenHeight:mEmulatedScreenHeight;}

	float getViewportWidth() {return 2.f*getScreenRatio();}
	float getViewportHeight() {return 2.f;}
	float getViewportPixelSize() {return 2.f/(float)getScreenHeight();}

	float screenToViewportX(int x);
	float screenToViewportY(int y);

	float getScreenRatio() {return ((float)getScreenWidth()/(float)getScreenHeight());}

	void SetFadeOut(float progress);

//	void setInstances(ArrayList<ZInstance> Instances,ArrayList<ZInstance> InstancesAlpha,ArrayList<ZInstance> SkyInstances);

	void setCamera(const PCamera& cam) {mCamera = cam;}
	const PCamera getCamera() const {return mCamera;}

protected:
	float 	mFadeOutProgress;

	PCamera mCamera;
	ZOrthoCamera mCamera2D;	// cam toujours pr�sente!

	int	mEmulatedScreenWidth,mEmulatedScreenHeight;
	int	mNativeScreenWidth,mNativeScreenHeight;
	bool mNativeEmulatedResolution;


	// point-sprites (pour les particle systems)
	bool mPointSpriteSupported;

public:
	void Init();
	void Resize(int w, int h);
	void PreRender(ZScene* scene);		// called before update of game is launched on another thread...
	void Render(ZScene* scene);			// called after game update launched in another thread...

	void DBG_CheckThread();	// DEBUG: check the thread and compare it to the render thread. If not equal, log a warning and a the java call stack

// check if wanted extensions are supported...
protected:
	void setUpSupportedExtensions();


};


extern ZRenderer	gRenderer;


#endif // __ZRENDERER__
