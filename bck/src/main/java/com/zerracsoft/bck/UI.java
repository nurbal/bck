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

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.Time;
import android.widget.Toast;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMenu;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZScreenshotReceiver;

public abstract class UI implements ZScreenshotReceiver
{
	// initialization/destruction
	public abstract void init();
	public abstract void close();
	

	// touch interactions; coordinates in screen values (pixels)
	public abstract void onTouchMoveStart(float xStart, float yStart);
	public abstract void onTouchMove(float xStart, float yStart, float x, float y);
	public abstract void onTouchMoveEnd();
	public abstract void onTouchPinchStart(float xStart, float yStart);
	public abstract void onTouchPinch(float xStart, float yStart, float x, float y, float factor);
	public abstract void onTouchPinchEnd();
	
	// view mode...
	enum ViewMode
	{
		MODE2D,
		MODE3D
	}
	public abstract ViewMode getViewMode();
	public abstract boolean isEditMode();
	public abstract void updateBudget();
	
	
	public static UI get() {return ((GameActivity)ZActivity.instance).getUI();}
	
	public abstract boolean isSimulationOn();
	public abstract void shakeCamera();
	
	private boolean mScreenshotMode = false;
	private int mScreenshotTimer;
	private ZMenu mScreenshotMenu;
	private ZInstance mFlashInstance;
	private static ZColor mFlashColor = new ZColor();
	public void takeScreenshot(ZMenu returnMenu)
	{
		mScreenshotMenu = returnMenu;
		mScreenshotMode=true;
		mScreenshotTimer=0;
		ZMesh flashMesh = new ZMesh();
		flashMesh.createRectangle(-ZRenderer.getViewportWidthF()*0.5f, -ZRenderer.getViewportHeightF()*0.5f, ZRenderer.getViewportWidthF()*0.5f, ZRenderer.getViewportHeightF()*0.5f, 0,0, 1,0, 0,1, 1,1);
		if (mFlashInstance!=null) ZActivity.instance.mScene.remove(mFlashInstance);
		mFlashInstance = new ZInstance(flashMesh);
		mFlashColor.set(1,1,1, 0);
		mFlashInstance.setColor(mFlashColor);
		mFlashInstance.setTranslation(0, 0, ReleaseSettings.layer2dFlashEffect);
		ZActivity.instance.mScene.add2D(mFlashInstance);
	}
	
	// update (camera, etc)
	public void update(int elapsedTime)
	{
		if (mScreenshotMode)
		{
			mScreenshotTimer += elapsedTime;		
			if (mScreenshotTimer<500)
			{
				float alpha = 1.f - (float)mScreenshotTimer/500.f;
				mFlashColor.set(1,1,1, alpha);
				mFlashInstance.setColor(mFlashColor);
			}
			else
			{
				ZActivity.instance.mScene.remove(mFlashInstance);
				mFlashInstance = null;
				ZRenderer.takeScreenshot(this);
				ZActivity.instance.mScene.setMenu(mScreenshotMenu);
				mScreenshotMenu=null;
				mScreenshotMode=false;
			}
		}
	}

	private static String screenshotFilePath;
	@Override
	public void onScreenshotTaken(Bitmap bitmap) {
		
		
		
		try {
			Time t = new Time();
			t.setToNow();
			File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			f.mkdirs();
			screenshotFilePath = f.toString()+ReleaseSettings.SCREENSHOT_PREFIX+t.format("%Y%m%d%H%M%S")+".jpg";
		    FileOutputStream fos = new FileOutputStream(screenshotFilePath);
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
		    fos.flush();
		    fos.close();
		    GameActivity.handler.post(new Runnable() {public void run(){Toast.makeText(ZActivity.instance, ZActivity.instance.getResources().getString(R.string.screenshot_saved)+screenshotFilePath, Toast.LENGTH_LONG).show();}});
		} catch (Exception e) {
		    // handle
		}
		
	}


}
