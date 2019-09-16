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

import android.util.Log;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZColor;
import com.zerracsoft.Lib3D.ZFont;
import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZMaterial;
import com.zerracsoft.Lib3D.ZMenu;
import com.zerracsoft.Lib3D.ZMenu.Item;
import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZRenderer;
import com.zerracsoft.Lib3D.ZVector;

public abstract class MenuText extends ZMenu
{
	@Override
	public void onItemJustSelected(Item item) {
		// TODO Auto-generated method stub
		super.onItemJustSelected(item);
		GameActivity.instance.playSnd(GameActivity.sndMenuSelect,1);
	}

	@Override
	public boolean doesHandleBackKey() {
		// TODO Auto-generated method stub
		return false;
	}

	// background: bandeaux en haut et en bas...
	private ZInstance mHeaderInstance;
	private ZInstance mFooterInstance;
	private static final float headerFooterLimit = 90.f/256.f;
	private static final float headerFooterWidthfactor = 1024.f/256.f;

	boolean mAnimate = true;
	boolean mAnimateFrame = true;
	void animate(boolean anim,boolean animFrame)
	{
		mAnimate = anim;
		mAnimateFrame = animFrame;
		if (anim)
		{
			mEnterTime = ReleaseSettings.MENU_TEXT_ENTER_TIME;
			mExitTime = ReleaseSettings.MENU_TEXT_EXIT_TIME;
		}
		else
			mEnterTime = mExitTime = 0;
			
	}
	
	public MenuText(boolean anim,boolean animFrame)
	{
		animate(anim,animFrame);

		ZMesh m;
		
		// bandeaux header/footer
		m = new ZMesh();
		m.createRectangle(-0.5f,-headerFooterLimit/headerFooterWidthfactor,0.5f,0,0,headerFooterLimit,1,0);
		m.setMaterial("menu_bg");		
		mHeaderInstance = new ZInstance(m);
		mHeaderInstance.setScale(ZRenderer.getViewportWidthF(),ZRenderer.getViewportWidthF(),1);
		mHeaderInstance.setTranslation(0,2,0);
		ZActivity.instance.mScene.add2D(mHeaderInstance);

		m = new ZMesh();
		m.createRectangle(-0.5f,0,0.5f,(1.f-headerFooterLimit)/headerFooterWidthfactor,0,1,1,headerFooterLimit);
		m.setMaterial("menu_bg");
		mFooterInstance = new ZInstance(m);
		mFooterInstance.setTranslation(0,-2,0); 
		mFooterInstance.setScale(ZRenderer.getViewportWidthF(),ZRenderer.getViewportWidthF(),1);
		ZActivity.instance.mScene.add2D(mFooterInstance); 

	}

	private static ZColor color = new ZColor();
	@Override
	public void updateExit(int timeIncrement, float progress) {
		super.updateExit(timeIncrement, progress);
		color.set(1,1,1,1.f-progress);
		if (null!=mTitleTextInstance)
			mTitleTextInstance.setColor(color);
		if (mAnimateFrame)
		{
			mHeaderInstance.setTranslation(0,1+progress,0); 
			mFooterInstance.setTranslation(0,-1-progress,0);
		}
		else
		{
			mHeaderInstance.setTranslation(0,1,0); 
			mFooterInstance.setTranslation(0,-1,0); 
		}
	}

	@Override
	public void updateIdle(int timeIncrement, int stateTime) {
		super.updateIdle(timeIncrement, stateTime);
		
		if (null!=mTitleTextInstance)
			mTitleTextInstance.setColor(ZColor.constWhite);
		mHeaderInstance.setTranslation(0,1,0); 
		mFooterInstance.setTranslation(0,-1,0); 
	}

	@Override
	public void updateEnter(int elapsedTime, float progress) {
		super.updateEnter(elapsedTime, progress);
		color.set(1,1,1,progress);
		
		if (null!=mTitleTextInstance)
			mTitleTextInstance.setColor(color);
		if (mAnimateFrame)
		{
			mHeaderInstance.setTranslation(0,2-progress,0); 
			mFooterInstance.setTranslation(0,-2+progress,0); 
		}
		else
		{
			mHeaderInstance.setTranslation(0,1,0); 
			mFooterInstance.setTranslation(0,-1,0); 
		}
	}

	protected ZInstance mTitleTextInstance;	// texte du titre du menu
	
	
	@Override
	public void setState(State state) {
		super.setState(state);
		if (state==State.EXIT)
		{
//			GameActivity.instance.playSnd(GameActivity.sndMenuSelect);
		}
	}

	public void setTitle (int resourceID,ZVector positionCenter,ZFont.AlignH pivotPosition)
	{
		setTitle(ZActivity.instance.getResources().getString(resourceID),positionCenter,pivotPosition);
	}
	public void setTitle (String s,ZVector positionCenter,ZFont.AlignH pivotPosition)
	{
		float w = GameActivity.mFontBig.getStringWidth(s)*0.5f*ZRenderer.getViewportWidthF() / (float)ZRenderer.getScreenWidth();
		ZInstance textInstance = new ZInstance(GameActivity.mFontBig.createStringMesh(s, ZFont.AlignH.CENTER, ZFont.AlignV.CENTER));
		if (positionCenter==null) positionCenter=new ZVector(0,ZRenderer.alignScreenGrid(0.8f),0);
		switch(pivotPosition)
		{
		case LEFT:
			textInstance.setTranslation(ZRenderer.alignScreenGrid(w), 0, 0);
			mTitleTextInstance = new ZInstance(textInstance);
			mTitleTextInstance.setTranslation(positionCenter);
			mTitleTextInstance.translate(ZRenderer.alignScreenGrid(-w), 0, 0);
			break;
		case CENTER:
			textInstance.setTranslation(positionCenter);
			mTitleTextInstance = textInstance;
			break;
		case RIGHT:
			textInstance.setTranslation(-ZRenderer.alignScreenGrid(w), 0, 0);
			mTitleTextInstance = new ZInstance(textInstance);
			mTitleTextInstance.setTranslation(positionCenter);
			mTitleTextInstance.translate(ZRenderer.alignScreenGrid(w), 0, 0);
			break;
		}
		
		color.set(1,1,1,0);
		mTitleTextInstance.setColor(color);
		ZActivity.instance.mScene.add2D(mTitleTextInstance);		
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		ZActivity.instance.mScene.remove(mTitleTextInstance); 
		mTitleTextInstance=null;
		ZActivity.instance.mScene.remove(mHeaderInstance);
		ZActivity.instance.mScene.remove(mFooterInstance);
	}
	
	
	float animIterator = 0.3f;
	
	static ZVector v = new ZVector();
	public class TextItem extends Item
	{
		private String text;
		private float textY;
		private float textX;
		
		//private static final float btnMarginX = 0.12f;
		//private static final float btnMarginY = 0.02f;
		private static final float btnsizeFactorY = 2.5f;
		
		protected float animDirection = 1.f;
		
		
//		protected ZMesh mTextMesh;
		protected ZInstance mTextInstance;
		
//		protected ZMesh mButtonMesh;
		protected ZInstance mFrameInstance;
		
		static final float textScale = 1;
		
		public TextItem(int id,String txt,float x,float y)
		{
			mID = id;
			text = txt;
			textX = ZRenderer.alignScreenGrid(x);
			textY = ZRenderer.alignScreenGrid(y);
						
			animDirection = animIterator; 
			animIterator*=-1;
		}
		
		public TextItem(int id,int textResource,float x,float y)
		{
			mID = id;
			text = ZActivity.instance.getResources().getString(textResource);
			textX = ZRenderer.alignScreenGrid(x);
			textY = ZRenderer.alignScreenGrid(y);
						
			animDirection = animIterator; 
			animIterator*=-1;
		}
		
		public boolean checkTextInstance()
		{
			// deffered text instance creation, for deffered material loading...
			if (mTextInstance!=null) 
				return true;
			ZMaterial mat = ZActivity.instance.mScene.getMaterial("button");
			if (mat==null)
				return false;
			if (GameActivity.mFontBig.getHeight()==0)
				return false;
			// text mesh
			ZMesh mesh = GameActivity.mFontBig.createStringMesh(text, ZFont.AlignH.CENTER, ZFont.AlignV.CENTER);
			if (mesh==null)
				return false;
			mTextInstance = new ZInstance(mesh);
			mTextInstance.setColor(color);
			//mTextInstance.setVisible(false);
			v.set(textX,textY,1); mTextInstance.setTranslation(v);
			ZActivity.instance.mScene.add2D(mTextInstance);
			
			//textSizeX = 0.5f*textScale*(float)(MainScreen.mFontBig.getStringWidth(text)*ZRenderer.getViewportWidthF()/(float)ZRenderer.getScreenWidth());
			float textSizeY = 0.5f*textScale*(float)(GameActivity.mFontBig.getHeight()*ZRenderer.getViewportHeightF()/(float)ZRenderer.getScreenHeight());
			// button frame (for user input)
			float matRatio = (float)mat.mWidth / (float)mat.mHeight;
			float halfWidth = textSizeY*btnsizeFactorY*matRatio;
			float halfHeight = textSizeY*btnsizeFactorY;
			xMin = textX-halfWidth;
			xMax = textX+halfWidth;
			yMin = textY-halfHeight;
			yMax = textY+halfHeight;
			// frame mesh
			mesh = new ZMesh();
			mesh.createRectangle(-halfWidth, -halfHeight, halfWidth, halfHeight, 0,1,1,0);
			mesh.setMaterial(mat);
			mFrameInstance = new ZInstance(mesh); 
			mFrameInstance.setTranslation(textX,textY,0); 
			color.set(1,1,1,0); 
			mFrameInstance.setColor(color); 
			ZActivity.instance.mScene.add2D(mFrameInstance);

			return true;

		}
		
		public void destroy()
		{
			super.destroy();
			ZActivity.instance.mScene.remove(mTextInstance);
			ZActivity.instance.mScene.remove(mFrameInstance);
		}
	
		protected void updateButton(float xOffset,float yOffset,float sizeCoef,float alpha)
		{
			
			// translation
			v.set(textX+xOffset,textY+yOffset,1); mTextInstance.setTranslation(v);
			v.set(textX+xOffset,textY+yOffset,0); 
			mFrameInstance.setTranslation(v);
			//scale
			v.set(sizeCoef,sizeCoef,sizeCoef);
			mTextInstance.setScale(v);
			mFrameInstance.setScale(v);
			// alpha
			color.set(1,1,1,alpha);
			mTextInstance.setColor(color);
			mFrameInstance.setColor(color);
		}
		
		@Override
		public void updateEnter(int timeIncrement, float progress, int time) {
			// TODO Auto-generated method stub
			super.updateEnter(timeIncrement, progress, time);
			
			if (!checkTextInstance()) return;
			
			// decallage du a l'anim
			float y = 0;
			float x = animDirection*(1-progress);
			
			updateButton(x,y,1.f,progress);
			
		}

		@Override
		public void updateExit(int elapsedTime, float progress, int time,
				boolean selected) {
			// TODO Auto-generated method stub
			super.updateExit(elapsedTime,progress, time, selected);

			if (!checkTextInstance()) return;
			
			float sizeCoef = 1.f;
			float alpha = 1.f;
			if (selected)
			{
				sizeCoef = 1.f+ progress*0.2f;
				alpha = (1.f-progress)*2.f; if (alpha>1) alpha = 1;
			}
			else
			{
				sizeCoef = 1.f - progress*0.5f;
				alpha = 1.f-progress;
			}

			updateButton(0,0,sizeCoef,alpha);
		}

		@Override
		public void updateIdle(int elapsedTime, int time) {
			// TODO Auto-generated method stub
			super.updateIdle(elapsedTime,time);

			if (!checkTextInstance()) return;

			updateButton(0,0,1,1);
		}

		
		
	}

}
