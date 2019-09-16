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

package com.zerracsoft.bck;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZVector;

class StoneCollumn
{
	float mHeight;
	float mXMin;
	float mXMax;
	public StoneCollumn(float x1, float x2, float height)
	{
		mXMin = x1;
		mXMax = x2;
		mHeight = height;
	}
	
	static ZVector v = new ZVector();
	static ZVector N = new ZVector(0,1,0);
//	static ZVector position = new ZVector();
	ZInstance mInstance;
	public void initGraphics()
	{
		resetGraphics();
		
		if (UI.get()!=null)
		{
			ZMesh m = new ZMesh();
			if (UI.get().getViewMode() == UI.ViewMode.MODE2D)
			{
				m.allocMesh(6, 4, false);
				m.setMaterial("stone");
				v.set(mXMin, 0, Level.get().getZMin()); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, 0, Level.get().getZMin()); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, 0, mHeight-0.25f); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f, 0, mHeight); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin+0.25f, 0, mHeight); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin, 0, mHeight-0.25f); m.addVertex(v, null, v.get(0)*ReleaseSettings.stoneUVscale, -v.get(2)*ReleaseSettings.stoneUVscale);
				m.addFace(0, 4, 5);
				m.addFace(0, 3, 4);
				m.addFace(0, 2, 3);
				m.addFace(0, 1, 2);
				mInstance = new ZInstance(m);
				mInstance.setTranslation(0,ReleaseSettings.layer2dStone,0);
				ZActivity.instance.mScene.add(mInstance);
			}
			if (UI.get().getViewMode() == UI.ViewMode.MODE3D)
			{
				//v.set(0.5f*(mXMax-mXMin), ReleaseSettings.STONE_COLLUMN_HALF_WIDTH,(mHeight-Level.get().getZMin())*0.5f);
				//m.createBox(v);
				
				m.allocMesh(24, (2*8)+4+(2*4)+2, true);
				m.setMaterial("stone");
				float w = ReleaseSettings.STONE_COLLUMN_HALF_WIDTH;
				float dx = mXMax-mXMin;
				float dy = w*2.f-0.5f;
				float u7 = 0;
				float u0 = (0.25f)*ReleaseSettings.stoneUVscale;
				float u1 = (dx-0.25f)*ReleaseSettings.stoneUVscale;
				float u2 = (dx)*ReleaseSettings.stoneUVscale;
				float u3 = (dx+dy)*ReleaseSettings.stoneUVscale;
				float u4 = (dx)*ReleaseSettings.stoneUVscale;
				float u5 = (dx-0.25f)*ReleaseSettings.stoneUVscale;
				float u6 = (0.25f)*ReleaseSettings.stoneUVscale;
				v.set(mXMin+0.25f, -w, Level.get().getZMin()); 	m.addVertex(v, ZVector.constYNeg, u0, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f, -w, Level.get().getZMin()); 	m.addVertex(v, ZVector.constYNeg, u1, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, -w+0.25f, Level.get().getZMin()); 	m.addVertex(v, ZVector.constX, u2, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, w-0.25f, Level.get().getZMin()); 	m.addVertex(v, ZVector.constX, u3, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f, w, Level.get().getZMin()); 	m.addVertex(v, ZVector.constY, u4, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin+0.25f, w, Level.get().getZMin()); 	m.addVertex(v, ZVector.constY, u5, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin, w-0.25f, Level.get().getZMin()); 	m.addVertex(v, ZVector.constXNeg, u6, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin, -w+0.25f, Level.get().getZMin()); 	m.addVertex(v, ZVector.constXNeg, u7, -v.get(2)*ReleaseSettings.stoneUVscale);
				
				v.set(mXMin+0.25f, -w, mHeight-0.25f); 	m.addVertex(v, ZVector.constYNeg, u0, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f, -w, mHeight-0.25f); 	m.addVertex(v, ZVector.constYNeg, u1, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, -w+0.25f, mHeight-0.25f); 	m.addVertex(v, ZVector.constX, u2, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax, w-0.25f, mHeight-0.25f); 	m.addVertex(v, ZVector.constX, u3, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f, w, mHeight-0.25f); 	m.addVertex(v, ZVector.constY, u4, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin+0.25f, w, mHeight-0.25f); 	m.addVertex(v, ZVector.constY, u5, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin, w-0.25f, mHeight-0.25f); 	m.addVertex(v, ZVector.constXNeg, u6, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin, -w+0.25f, mHeight-0.25f); 	m.addVertex(v, ZVector.constXNeg, u7, -v.get(2)*ReleaseSettings.stoneUVscale);
				
				v.set(mXMin+0.25f,-w+0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (u0+u7)*0.5f, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f,-w+0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (u1+u2)*0.5f, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f,w-0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (u3+u4)*0.5f, -v.get(2)*ReleaseSettings.stoneUVscale);
				v.set(mXMin+0.25f,w-0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (u5+u6)*0.5f, -v.get(2)*ReleaseSettings.stoneUVscale);
				
				v.set(mXMin+0.25f,-w+0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, 0, dy*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f,-w+0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (dx-0.5f)*ReleaseSettings.stoneUVscale, dy*ReleaseSettings.stoneUVscale);
				v.set(mXMax-0.25f,w-0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, (dx-0.5f)*ReleaseSettings.stoneUVscale, 0);
				v.set(mXMin+0.25f,w-0.25f,mHeight); 	m.addVertex(v, ZVector.constZ, 0, 0);
					
				m.addFace(0, 9, 8);		m.addFace(0, 1, 9);
				m.addFace(1, 10, 9);	m.addFace(1, 2, 10);
				m.addFace(2, 11, 10);	m.addFace(2, 3, 11);
				m.addFace(3, 12, 11);	m.addFace(3, 4, 12);
				m.addFace(4, 13, 12);	m.addFace(4, 5, 13);
				m.addFace(5, 14, 13);	m.addFace(5, 6, 14);
				m.addFace(6, 15, 14);	m.addFace(6, 7, 15);
				m.addFace(7, 8, 15);	m.addFace(7, 0, 8);
				
				m.addFace(15,8,16);
				m.addFace(9,10,17);
				m.addFace(11,12,18);
				m.addFace(13,14,19);
				m.addFace(8,17,16);		m.addFace(8,9,17);
				m.addFace(10,18,17);	m.addFace(10,11,18);
				m.addFace(12,19,18);	m.addFace(12,13,19);
				m.addFace(14,16,19);	m.addFace(14,15,16);
				
				m.addFace(20,21,22);	m.addFace(20,22,23);
				
				ZInstance i1 = new ZInstance(m); i1.setTranslation(0,ReleaseSettings.BRIDGE_HALF_WIDTH+ReleaseSettings.STONE_COLLUMN_HALF_WIDTH,0);
				ZInstance i2 = new ZInstance(m); i2.setTranslation(0,-ReleaseSettings.BRIDGE_HALF_WIDTH-ReleaseSettings.STONE_COLLUMN_HALF_WIDTH,0);
				mInstance = new ZInstance(i1);
				mInstance.add(i2);
				ZActivity.instance.mScene.add(mInstance);				
			}
		}
	}
	
	public void resetGraphics()
	{
		if (mInstance!=null)
			ZActivity.instance.mScene.remove(mInstance);
		mInstance = null;
	}
			
}
