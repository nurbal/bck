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
#include "release_settings.h"
#include "log.h"
#include "Level.h"
#include "../Lib3D/Lib3D.h"
#include "../Lib3D/jni_Watchdogs.h"

extern "C" jlong Java_com_zerracsoft_bck_Level_JNIconstructor(JNIEnv* env, jobject thiz)
{
	JNI_IN
	PLevel* pl = new PLevel();
	*pl = new Level();
	JNI_OUT
	return (jlong)(unsigned long)(pl);
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIdestructor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_DESTRUCTOR_IN
	//LOGD("JNI enter obj=%d",obj);
	LOGD("Level_JNIdestructor");
	CHECK_SPTR((PLevel*)obj);
	delete ((PLevel*)obj);
	JNI_DESTRUCTOR_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetWaterHeight(JNIEnv* env, jobject thiz, jlong obj, jfloat height)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	level->mHasWater = true;
	level->mWaterHeight = height;
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIsetWind(JNIEnv* env, jobject thiz, jlong obj, jfloat wind)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	level->mWindValue = wind;
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIsetRailsHeight(JNIEnv* env, jobject thiz, jlong obj, jfloat height)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	level->mRailsHeight = height;
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIunsetWater(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	level->mHasWater = false;
	JNI_OUT
}
extern "C" jboolean Java_com_zerracsoft_bck_Level_JNIhasWater(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	bool res = level->mHasWater;
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetWaterHeight(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->mWaterHeight;
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetWind(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->mWindValue;
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetRailsHeight(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->mRailsHeight;
	JNI_OUT
	return res;
}

extern "C" jint Java_com_zerracsoft_bck_Level_JNIgetXMin(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	int res = level->mXMin;
	JNI_OUT
	return res;
}
extern "C" jint Java_com_zerracsoft_bck_Level_JNIgetXMax(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	int res = level->mXMax;
	JNI_OUT
	return res;
}
//*
extern "C" jint Java_com_zerracsoft_bck_Level_JNIgetZMin(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	int res = level->mZMin;
	JNI_OUT
	return res;
}
extern "C" jint Java_com_zerracsoft_bck_Level_JNIgetZMax(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	int res = level->mZMax;
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetZCenter(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->GetZCenter();
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetZExtents(JNIEnv* env, jobject thiz, jlong obj, jint zMin, jint zMax)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->SetZExtents(zMin, zMax);
	JNI_OUT
}
//*/
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetXCenter(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->GetXCenter();
	JNI_OUT
	return res;
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetXSize(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	//LOGD("JNI enter xsize=%f",level->GetXSize());
	float res = level->GetXSize();
	JNI_OUT
	return res;
}

extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetFloorHeight(JNIEnv* env, jobject thiz, jlong obj, jint x)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->GetFloorHeight(x);
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIsetFloorHeight(JNIEnv* env, jobject thiz, jlong obj, jint x, jfloat height)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->SetFloorHeight(x,height);
	JNI_OUT
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetNearHeight(JNIEnv* env, jobject thiz, jlong obj, jint x)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->GetNearHeight(x);
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIsetNearHeight(JNIEnv* env, jobject thiz, jlong obj, jint x, jfloat height)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->SetNearHeight(x,height);
	JNI_OUT
}
extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetFarHeight(JNIEnv* env, jobject thiz, jlong obj, jint x)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter obj=%d",obj);
	PLevel level = *((PLevel*)obj);
	float res = level->GetFarHeight(x);
	JNI_OUT
	return res;
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIsetFarHeight(JNIEnv* env, jobject thiz, jlong obj, jint x, jfloat height)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->SetFarHeight(x,height);
	JNI_OUT
}
extern "C" void Java_com_zerracsoft_bck_Level_JNIallocFloor(JNIEnv* env, jobject thiz, jlong obj, jint xMin, jint xMax)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->AllocFloor(xMin, xMax);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIcompleteFloor(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->CompleteFloor();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIcorrectFloorPosition(JNIEnv* env, jobject thiz, jlong obj, jlong point, jlong n, jlong t)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PVector*)point);
	CHECK_SPTR((PVector*)n);
	CHECK_SPTR((PVector*)t);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PVector P = *((PVector*)point);
	PVector N = *((PVector*)n);
	PVector T = *((PVector*)t);
	level->CorrectFloorPosition(P,N,T);
	JNI_OUT
}

extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetFloorNearestPoint(JNIEnv* env, jobject thiz, jlong obj, jlong inpoint,jfloat inMaxRay,jlong outpoint,jlong outnormal,jlong outtangent)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PVector*)inpoint);
	CHECK_SPTR((PVector*)outpoint);
	CHECK_SPTR((PVector*)outnormal);
	CHECK_SPTR((PVector*)outtangent);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PVector inPoint = *((PVector*)inpoint);
	PVector outPoint = *((PVector*)outpoint);
	PVector outNormal = *((PVector*)outnormal);
	PVector outTangent = *((PVector*)outtangent);
	float res = level->GetFloorNearestPoint(inPoint,inMaxRay,outPoint,outNormal,outTangent);
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIupdateSimulation(JNIEnv* env, jobject thiz, jlong obj, jfloat dt)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->UpdateSimulation(dt);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIaddMobileNode(JNIEnv* env, jobject thiz, jlong obj, jlong n)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PNode*)n);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PNode node = *((PNode*)n);
	level->AddMobileNode(node);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIremoveMobileNode(JNIEnv* env, jobject thiz, jlong obj, jlong n)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PNode*)n);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PNode node = *((PNode*)n);
	level->RemoveMobileNode(node);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIclearMobileNodes(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("Level_JNIclearMobileNodes");
	PLevel level = *((PLevel*)obj);
	level->ClearMobileNodes();
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIaddLink(JNIEnv* env, jobject thiz, jlong obj, jlong l)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PLink*)l);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PLink link = *((PLink*)l);
	level->AddLink(link);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIremoveLink(JNIEnv* env, jobject thiz, jlong obj, jlong l)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PLink*)l);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PLink link = *((PLink*)l);
	level->RemoveLink(link);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIclearLinks(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("Level_JNIclearLinks");
	PLevel level = *((PLevel*)obj);
	level->ClearLinks();
	JNI_OUT

}

extern "C" void Java_com_zerracsoft_bck_Level_JNIinitGraphics(JNIEnv* env, jobject thiz, jlong obj, jlong scene, jboolean is3D)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PScene*)scene);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PScene Scene = *((PScene*)scene);
	level->InitGraphics(Scene,is3D);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIresetGraphics(JNIEnv* env, jobject thiz, jlong obj, jlong scene)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PScene*)scene);
	//LOGD("JNI enter (scene=%d)",scene);
	PLevel level = *((PLevel*)obj);
	PScene Scene = *((PScene*)scene);
	level->ResetGraphics(Scene);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIupdateGraphics(JNIEnv* env, jobject thiz, jlong obj, jint elapsedTime)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->UpdateGraphics(elapsedTime);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetTowerMode(JNIEnv* env, jobject thiz, jlong obj, jboolean towerMode)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->mTowerMode = towerMode;
	JNI_OUT
}

extern "C" jboolean Java_com_zerracsoft_bck_Level_JNIisTowerMode(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	bool res = level->mTowerMode;
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetTowerGoal(JNIEnv* env, jobject thiz, jlong obj, jfloat towerGoal)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	level->mTowerGoal = towerGoal;
	JNI_OUT
}

extern "C" jfloat Java_com_zerracsoft_bck_Level_JNIgetTowerGoal(JNIEnv* env, jobject thiz, jlong obj)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	float res = level->mTowerGoal;
	JNI_OUT
	return res;
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetP6LinkBreakMaterials(JNIEnv* env, jobject thiz, jlong obj, jlong bars, jlong jacks, jlong cables)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PMaterial*)bars);
	CHECK_SPTR((PMaterial*)jacks);
	CHECK_SPTR((PMaterial*)cables);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PMaterial matBars = *((PMaterial*)bars);
	PMaterial matJacks = *((PMaterial*)jacks);
	PMaterial matCables = *((PMaterial*)cables);
	level->SetP6LinkBreakMaterials(matBars,matJacks,matCables);
	JNI_OUT
}

extern "C" void Java_com_zerracsoft_bck_Level_JNIsetLinksMaterials(JNIEnv* env, jobject thiz, jlong obj, jlong bars, jlong jackHead, jlong jackRetract, jlong jackExpand, jlong cables)
{
	JNI_IN
	CHECK_SPTR((PLevel*)obj);
	CHECK_SPTR((PMaterial*)bars);
	CHECK_SPTR((PMaterial*)jackHead);
	CHECK_SPTR((PMaterial*)jackRetract);
	CHECK_SPTR((PMaterial*)jackExpand);
	CHECK_SPTR((PMaterial*)cables);
	//LOGD("JNI enter");
	PLevel level = *((PLevel*)obj);
	PMaterial matBars = *((PMaterial*)bars);
	PMaterial matJackHead = *((PMaterial*)jackHead);
	PMaterial matJackRetract = *((PMaterial*)jackRetract);
	PMaterial matJackExpand = *((PMaterial*)jackExpand);
	PMaterial matCables = *((PMaterial*)cables);
	level->SetLinksMaterials(matBars,matJackHead,matJackRetract,matJackExpand,matCables);
	JNI_OUT
}
