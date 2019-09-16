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


#include <jni.h>
#include <math.h>
#include <GLES/gl.h>
#include "ZRenderer.h"
#include "helpers.h"
#include "log.h"
#include <string.h>
#include <stdlib.h>
#include "lib3d_mutex.h"
#include "../BCK/jni_Misc.h"
#include "release_settings.h"

ZRenderer gRenderer;


ZRenderer::ZRenderer()
{
	mNativeScreenWidth = 800;
	mNativeScreenHeight = 480;
	mEmulatedScreenWidth = 0;
	mEmulatedScreenHeight = 0;
	mNativeEmulatedResolution = false;

	mPointSpriteSupported = false;

	mFadeOutProgress = 0.f;

}

ZRenderer::~ZRenderer() {

}

static pthread_t sRenderThread = 0;
void ZRenderer::DBG_CheckThread()	// DEBUG: check the thread and compare it to the render thread. If not equal, log a warning and a the java call stack
{
	if (sRenderThread!=0 && sRenderThread!=pthread_self())
	{
		LOGW("ZRenderer::DBG_CheckThread WARNING renderThreadID=%d currentThreadID=%d",(int)sRenderThread,(int)pthread_self());
		LogJavaCallStack();
	}
}




float ZRenderer::screenToViewportX(int x) {
	return (float)x/(float)mNativeScreenWidth*getViewportWidth() - getViewportWidth()*0.5f;
}
float ZRenderer::screenToViewportY(int y) {
	return -(float)y/(float)mNativeScreenHeight*getViewportHeight() + getViewportHeight()*0.5f;
}

void ZRenderer::SetFadeOut(float progress) { mFadeOutProgress = progress; }

/*
 private ArrayList<ZInstance>	m3dInstances;	// objects instances to render...
 private ArrayList<ZInstance>	m3dInstancesAlpha;	// objects instances to render...

 private ArrayList<ZInstance>	m3dSkyInstances;	// objects instances to render...

 */
/*
 public void setInstances(ArrayList<ZInstance> Instances,ArrayList<ZInstance> InstancesAlpha,ArrayList<ZInstance> SkyInstances)
 {
 m3dInstances = Instances;
 m3dInstancesAlpha = InstancesAlpha;
 m3dSkyInstances = SkyInstances;
 }
 */


void ZRenderer::PreRender(ZScene* scene)
{
	LOGV("ZRenderer::PreRender 1 scene=%08X",scene);
	//PRERENDER MUTEX (done in scene ?)
	scene->PrepareForRendering(mCamera,&mCamera2D);

	LOGV("ZRenderer::PreRender 2");
}

void ZRenderer::Render(ZScene* scene)
{
	/*
	if (sRenderThread!=pthread_self())
	{
		LOGW("ZRenderer::Render WARNING renderThreadID=%d currentThreadID=%d",(int)sRenderThread,(int)pthread_self());
		LogJavaCallStack();
		sRenderThread=pthread_self();
	}
*/

	if (sRenderThread==0)
		sRenderThread=pthread_self();
	else
		DBG_CheckThread();
	sRenderThread=pthread_self();

	LOGI("ZRenderer::Render 1 scene=%08X",scene);

	//   gl.glDisable(GL10.GL_DITHER); // this disturbs alpha+lighting
	//   gl.glEnable(GL10.GL_DITHER);

	if (!mCamera) return;

	glTexEnvx(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

	glDepthMask(true);

	// clear frame buffer
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	 // 2d background
	 glMatrixMode(GL_MODELVIEW);        glLoadIdentity();	// flat modelview matrix
	 glMatrixMode(GL_PROJECTION);        glLoadIdentity();	// flat projection matrix
	 glDisableClientState(GL_NORMAL_ARRAY);
	 glDisable(GL_LIGHTING);
	 glDisable(GL_DEPTH_TEST);
	/*
	 if (mActivity.render2dBackground(gl))
	 gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);	// clear zbuffer only if necessary
	 */

	// Projection matrix setup
	mCamera->computeProjection();

	// light setup
	LIGHT_MUTEX_LOCK

		glMatrixMode( GL_PROJECTION);
		// lighting
		float lightAmbient[] = { scene->mAmbientLight, scene->mAmbientLight, scene->mAmbientLight, 1.0f };
		float lightDiffuse[] = { scene->mDiffuseLight, scene->mDiffuseLight, scene->mDiffuseLight, 1.0f };
		float lightSpecular[] = { scene->mSpecularLight, scene->mSpecularLight, scene->mSpecularLight, 1.0f };
		float lightPosition[] = { scene->mLightPosition.x, scene->mLightPosition.y, scene->mLightPosition.z,0.0f }; //x,y,z=direction, w=0 means directional light
		// ambiant + directionnelle
		glEnable( GL_LIGHTING);
		glEnable( GL_LIGHT0);
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular);
		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		glShadeModel( GL_SMOOTH); //GL_FLAT/GL_SMOOTH

	LIGHT_MUTEX_UNLOCK

	// Camera setup
	mCamera->computePosition();
	// la cam�ra 2D est une cam ortho X � droite, Y � gauche, Z sortant de l'�cran
	// X = {-getScreenRatio() , +getScreenRatio()}
	// Y = {-1 , +1}
	// Z = {-100 , +100}
	static ZVector cam2DPos(0.f,0.f,101.f);
	mCamera2D.setParameters(1.f,201.f,getScreenRatio(), 1.f, &cam2DPos,ZVector::Zneg(),ZVector::X(),ZVector::Y());

	// setup textured 3d objects
	glActiveTexture( GL_TEXTURE0);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
	glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glDisable( GL_BLEND);

	// disable depth test
	glDisable( GL_DEPTH_TEST);


	INSTANCES_MUTEX_LOCK
	INSTANCES_TRANSFORMER_MUTEX_LOCK
		PInstance inst;
		ZListIterator<PInstance> it;
		int i;
		LOGI("ZRenderer::Render 3 scene=%X sky=%i+%i",scene,scene->mInstancesSky.GetNbElements(),scene->mInstancesSkyAlpha.GetNbElements());
	//*
		 // render sky
		 //for (i=0; i<scene->mInstancesSky->GetCount(); i++)
		 it = scene->mInstancesSky.Begin();
		while (!it.IsEnd())
		 {
			 //inst=scene->mInstancesSky->Get(i);
			inst = it.Get();
			it.Next();
			 if ((ZInstance*)inst)
			 {
				 if (inst->mRenderVisible)
				 {
					 inst->mTransformer.setTranslation(mCamera->getCameraEye());
					 inst->PrepareForRendering(mCamera);
					 inst->Render(ZInstance::SKY,false,ZColor::WHITE());
				 }
			 }
		 }
		 it = scene->mInstancesSkyAlpha.Begin();
		while (!it.IsEnd())
		 {
			glDisable(GL_LIGHTING);
			 //inst=scene->mInstancesSky->Get(i);
			inst = it.Get();
			it.Next();
			 if ((ZInstance*)inst)
			 {
				 if (inst->mRenderVisible)
				 {
					 inst->mTransformer.setTranslation(mCamera->getCameraEye());
					 inst->PrepareForRendering(mCamera);
					 inst->Render(ZInstance::SKY,false,ZColor::WHITE());
				 }
			 }
		 }
	//*/
		 // enable depth test
		 glEnable(GL_DEPTH_TEST);
			LOGI("ZRenderer::Render 4 scene=%X inst=%i+%i",scene,scene->mInstances.GetNbElements(),scene->mInstancesAlpha.GetNbElements());

		 // render objects
		 glMatrixMode(GL_MODELVIEW);
		 //for (i=0; i<scene->mInstances->GetCount(); i++)
		 it = scene->mInstances.Begin();
		while (!it.IsEnd())
		 {
			glEnable(GL_LIGHTING);
			 //inst=scene->mInstances->Get(i);
			inst = it.Get();
			 it.Next();
			 if (inst->mRenderVisible)
				 inst->Render(ZInstance::NORMAL,false,ZColor::WHITE());
		 }

		 glEnable(GL_BLEND);
		LOGI("ZRenderer::Render 5");

		 //for (i=0; i<scene->mInstancesAlpha->GetCount(); i++)
		 it = scene->mInstancesAlpha.Begin();
		while (!it.IsEnd())
		 {
			 //inst=scene->mInstancesAlpha->Get(i);
			inst = it.Get();
			it.Next();
			 if (inst->mRenderVisible && inst->mRenderColor.a>0)
			 {
				 glEnable(GL_LIGHTING);
				 //        		lightAmbient[3] = lightDiffuse[3] = lightSpecular[3] = inst.mAlpha;
				 //    	   		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,0);
				 //    	    	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,0);
				 //    	    	gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular,0);
				 inst->Render(ZInstance::NORMAL,false,ZColor::WHITE());
			 }
		 }
		LOGI("ZRenderer::Render 6");

		// 2D interface
		// draw 2D foreground

	//	glDisable(GL_DEPTH_TEST);
		mCamera2D.computeProjection();
		mCamera2D.computePosition();
		if (scene->mInstances2D.GetNbElements())
		{

			glDisable(GL_LIGHTING);
			glDepthMask(true);
			glClear(GL_DEPTH_BUFFER_BIT); // eh ouais... interface 2D, mais en relief ;) en plusieurs layers, quoi
			glDisableClientState(GL_NORMAL_ARRAY);

			it = scene->mInstances2D.Begin();
			while (!it.IsEnd())
			 {
				 //inst=scene->mInstancesSky->Get(i);
				inst = it.Get();
				it.Next();
				 if ((ZInstance*)inst)
				 {
					 if (inst->mRenderVisible)
						 inst->Render(ZInstance::UI,false,ZColor::WHITE());
				 }
			 }
			//glDisableClientState( GL_NORMAL_ARRAY);
		}

		// fadeout, par dessus le tout
		static float verts[12] = { -getViewportWidth()/2, -getViewportHeight()/2, 0,
							getViewportWidth()/2, -getViewportHeight()/2, 0,
							-getViewportWidth()/2, getViewportHeight()/2, 0,
							getViewportWidth()/2, getViewportHeight()/2, 0 };
		static short faces[4] = { 1,3,0,2 };

		glDisable(GL_LIGHTING);
		glDisableClientState(GL_NORMAL_ARRAY);
		glEnableClientState( GL_VERTEX_ARRAY);
		glVertexPointer(3, GL_FLOAT, 0, verts);
		glDisableClientState( GL_TEXTURE_COORD_ARRAY);
		glDisable( GL_TEXTURE_2D);
		// alpha
		glEnable( GL_BLEND);
		glDisable( GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glColor4f(0.f,0.f,0.f,mFadeOutProgress);
		glDrawElements(GL_TRIANGLE_STRIP, 4, GL_UNSIGNED_SHORT,
				faces);

		/*
		 // fin du rendu
		 mRenderingInProgress = false;
		 */
	INSTANCES_TRANSFORMER_MUTEX_UNLOCK
	INSTANCES_MUTEX_UNLOCK

	LOGI("ZRenderer::Render end");

}


void ZRenderer::emulateScreenSize(int width, int height)
{
	if (width==0) height = 0;
	if (height==0) width = 0;
	mEmulatedScreenWidth = width;
	mEmulatedScreenHeight = height;
}

void ZRenderer::Resize(int width, int height)
{

	glViewport(0, 0, width, height);
	/*
	 * Set our projection matrix. This doesn't have to be done
	 * each time we draw, but usually a new projection needs to
	 * be set when the viewport is resized.
	 */
	mNativeScreenWidth = width;
	mNativeScreenHeight = height;
	//mCamera->computeProjection();
}

void ZRenderer::Init() {
	glDisable( GL_DITHER);

	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	glClearColor(0, 0, 0, 1);
	glEnable( GL_CULL_FACE);
	glShadeModel( GL_SMOOTH);
	//         gl.glEnable(GL10.GL_DEPTH_TEST);
	glEnable( GL_TEXTURE_2D);

	glEnable( GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	// check if GL_ARB_multitexture is supported
	// check if GL_OES_point_sprite is supported
	setUpSupportedExtensions();

}


// check if wanted extensions are supported...
void ZRenderer::setUpSupportedExtensions() {
	//Get the extensions string, and look through it for "GL_ARB_multitexture". If it is found, set "ARB_multitexture_supported" to true.
	char* s = (char*) glGetString(GL_EXTENSIONS);

//	LOGE(s);

	//if (strstr(s, "GL_ARB_multitexture")
	//		&& strstr(s, "GL_ARB_texture_cube_map") && strstr(s,
	//		"GL_ARB_texture_env_combine") && strstr(s,
	//		"GL_ARB_texture_env_dot3")) {
	//	mBumpMappingSupported = true;
		// bump mapping setup...
		// unfortunately, only 2 of the 4 extensions necessary are present on the HTC Dream.
		// hopefully they will be present on the Nexus One ?
	//}

	// check if the point sprites are supported...
	//if ((s.startsWith("GL_OES_point_sprite ") || s.contains(" GL_OES_point_sprite ") || s.endsWith(" GL_OES_point_sprite"))
	//		&& (s.startsWith("GL_OES_point_size_array ") || s.contains(" GL_OES_point_size_array ") || s.endsWith(" GL_OES_point_size_array"))
	//		)
	//	mPointSpriteSupported = true;

	// todo: draw_texture/draw_
}

