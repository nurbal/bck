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

package com.zerracsoft.Lib3D;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

public class ZFont  extends org.xml.sax.helpers.DefaultHandler
{
	protected ZMaterial						mMaterial;
	protected  ArrayList<C3dCharacter>		mCharacters;
	static final int 						defaultNbChars = 128;
	protected String						mName;
	protected int							mTextureWidth;
	protected int							mTextureHeight;
	protected float							mTextureScaleWidth=1;
	protected float							mTextureScaleHeight=1;
	protected int							mHeight;	// in texels

	protected boolean 						mBMFontMode = false;
	
	public int getHeight() {return (int)((float)mHeight*mTextureScaleHeight);}
	
	public void load(ZActivity engine, String materialName, int xmlResource)
	{
		// Get Texture
		mMaterial = engine.mScene.getMaterial(materialName);
		
		if (mMaterial==null)
			Log.e("Lib3D","ZFont.loadFromResources called for uncreated material \""+materialName+"\"");
		

		// Load&Parse XML
		InputStream xmlStream = engine.getResources().openRawResource(xmlResource);
		try
		{
	        /* Get a SAXParser from the SAXPArserFactory. */ 
	        SAXParserFactory spf = SAXParserFactory.newInstance(); 
	        SAXParser sp = spf.newSAXParser(); 
	
	        /* Get the XMLReader of the SAXParser we created. */ 
	        XMLReader xr = sp.getXMLReader(); 
	        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
	        xr.setContentHandler(this); 
	         
	        /* Parse the xml-data from our URL. */ 
	        xr.parse(new InputSource(xmlStream)); 
	        /* Parsing has finished. */ 
	        // fix height, depending on texture size...
	        
		}
		catch (Exception e) 
		{
	        try {xmlStream.close();} catch(IOException ioe) {}
		}
        try {xmlStream.close();} catch(IOException ioe) {}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, qName, attributes);
		
		String s = null;
		
		if (localName.equals("font"))
		{
			mBMFontMode = true;
		}
		
		if (localName.equals("common"))
		{
			if (!mBMFontMode) throw new RuntimeException("invalid font xml");
			mBMFontMode = true;
			s = attributes.getValue("lineHeight");			if (s!=null) mHeight = Integer.decode(s);
			mTextureWidth = mMaterial.mWidth;
			
			
			mTextureHeight = mMaterial.mHeight;
			s = attributes.getValue("scaleW");			if (s!=null) mTextureWidth = Integer.decode(s);
			s = attributes.getValue("scaleH");			if (s!=null) mTextureHeight = Integer.decode(s);
			mTextureScaleWidth = (float)mMaterial.mWidth / (float)mTextureWidth;
			mTextureScaleHeight = (float)mMaterial.mHeight / (float)mTextureHeight;
			// TODO: base
		}
		
		// TODO info
		
		if (localName=="settings")
		{
			if (mBMFontMode) throw new RuntimeException("invalid font xml");
			mName = attributes.getValue("name");
			int nbChars = defaultNbChars;
			s = attributes.getValue("nb_chars");			if (s!=null) nbChars = Integer.decode(s);
			mTextureWidth = mMaterial.mWidth;
			mTextureHeight = mMaterial.mHeight;
			s = attributes.getValue("texture_width");			if (s!=null) mTextureWidth = Integer.decode(s);
			s = attributes.getValue("texture_height");			if (s!=null) mTextureHeight = Integer.decode(s);
			mTextureScaleWidth = (float)mMaterial.mWidth / (float)mTextureWidth;
			mTextureScaleHeight = (float)mMaterial.mHeight / (float)mTextureHeight;
			
			mCharacters = new ArrayList<C3dCharacter>(nbChars);	
			mHeight = 0;
		}
		
		if (localName=="chars")
		{
			if (!mBMFontMode) throw new RuntimeException("invalid font xml");
			int nbChars = defaultNbChars;
			s = attributes.getValue("count");			if (s!=null) nbChars = Integer.decode(s);
			mCharacters = new ArrayList<C3dCharacter>(nbChars);	
		}
		
		if (localName=="char")
		{
			C3dCharacter c = new C3dCharacter();
			mCharacters.add(c);
			if (mBMFontMode)
			{
				// id
				s = attributes.getValue("id"); if (s!=null) c.mChar = (char)(int)Integer.decode(s);
				// x
				// y
				// width
				// height
				int x=0;
				int y=0;
				int w=0;
				int h=0;
				s = attributes.getValue("x"); if (s!=null) x = Integer.decode(s);
				s = attributes.getValue("y"); if (s!=null) y = Integer.decode(s);
				s = attributes.getValue("width"); if (s!=null) w = Integer.decode(s);
				s = attributes.getValue("height"); if (s!=null) h = Integer.decode(s);
				c.u1 = (x<<16) / mTextureWidth;
				c.u2 = ((x+w)<<16) / mTextureWidth;
				c.v2 = (y<<16) / mTextureHeight;
				c.v1 = ((y+h)<<16) / mTextureHeight;
				c.w = w;
				c.h = h;
				
				// xoffset
				s = attributes.getValue("xoffset"); if (s!=null) c.xOffset = Integer.decode(s);
				// yoffset
				s = attributes.getValue("yoffset"); if (s!=null) c.yOffset = Integer.decode(s);
				// xadvence
				s = attributes.getValue("xadvance"); if (s!=null) c.xAdvance = Integer.decode(s);

				// page
				// chnl
			}
			else
			{
				s = attributes.getValue("char"); if (s!=null) c.mChar = s.charAt(0);
				int x=0;
				int y=0;
				int w=0;
				int h=0;
				s = attributes.getValue("x"); if (s!=null) x = Integer.decode(s);
				s = attributes.getValue("y"); if (s!=null) y = Integer.decode(s);
				s = attributes.getValue("w"); if (s!=null) w = Integer.decode(s);
				s = attributes.getValue("h"); if (s!=null) h = Integer.decode(s);
				c.u1 = (x<<16) / mTextureWidth;
				c.u2 = ((x+w)<<16) / mTextureWidth;
				c.v2 = (y<<16) / mTextureHeight;
				c.v1 = ((y+h)<<16) / mTextureHeight;
				c.w = w;
				c.h = h;
				if (mHeight<c.h) mHeight = c.h;
				
				c.xAdvance = w;
				c.xOffset = 0;
				c.yOffset = 0;
			}
		}
	}
	
	public enum AlignV
	{
		TOP,
		CENTER,
		BOTTOM
	}
	public enum AlignH
	{
		LEFT,
		CENTER,
		RIGHT
	}
	
	public C3dCharacter getChar(char c)
	{
		if (mCharacters==null) return null;
		for (int i=0; i<mCharacters.size(); i++)
			if (c==mCharacters.get(i).mChar)
				return mCharacters.get(i);
		return mCharacters.get(0); // par défaut..
	}
	
	public int getStringWidth(String s) // in texels (int)
	{
		if (mCharacters==null) return 0;
		int width = 0;
		for (int i=0; i<s.length(); i++)
			width+=getChar(s.charAt(i)).xAdvance;
		return (int)((float)width*mTextureScaleWidth);
	}
	
	private static ZVector v11=new ZVector();
	private static ZVector v12=new ZVector();
	private static ZVector v21=new ZVector();
	private static ZVector v22=new ZVector();
	public ZMesh createStringMesh(String s, AlignH alignH, AlignV alignV)
	{
		if (mCharacters==null) return null;
		float width = (float)getStringWidth(s);
		float factorX = mTextureScaleWidth * ZRenderer.getViewportWidthF() / (float)ZRenderer.getScreenWidth();
		float factorY = mTextureScaleHeight * ZRenderer.getViewportHeightF() / (float)ZRenderer.getScreenHeight();

		float x1,y1;
		switch(alignH)
		{
		case RIGHT:
			x1 = - ZRenderer.alignScreenGrid(width*factorX/mTextureScaleWidth);
			break;
		case CENTER:
			x1 = - ZRenderer.alignScreenGrid((width*factorX/mTextureScaleWidth)*0.5f);
			break;
		default:
			x1 = 0;
			break;
		}
		
		switch(alignV)
		{
		case TOP:
			y1 = - ZRenderer.alignScreenGrid((float)mHeight*factorY);
			break;
		case CENTER:
			y1 = - ZRenderer.alignScreenGrid(((float)mHeight*factorY)*0.5f);
			break;
		default:
			y1 = 0;
			break;
		}
		
		ZMesh mesh = new ZMesh();
		mesh.setMaterial(mMaterial);
		mesh.allocMesh(4*s.length(),2*s.length(),false);

		for (int i=0; i<s.length(); i++)
		{
			C3dCharacter c = getChar(s.charAt(i));
			float top = y1-(float)c.yOffset*factorY+(float)mHeight*factorY;
			float bottom = top-(float)c.h*factorY;
			float left = x1+(float)c.xOffset*factorX;
			float right = left+(float)c.w*factorX;
				
			v11.set(left,bottom,0);
			v12.set(left,top,0);
			v21.set(right,bottom,0);
			v22.set(right,top,0);
			short index = (short)(i*4);
			mesh.addVertex(v11, null, (float)c.u1/65536.f,(float)c.v1/65536.f);
			mesh.addVertex(v21, null, (float)c.u2/65536.f,(float)c.v1/65536.f);
			mesh.addVertex(v12, null, (float)c.u1/65536.f,(float)c.v2/65536.f);
			mesh.addVertex(v22, null, (float)c.u2/65536.f,(float)c.v2/65536.f);
			mesh.addFace(index,(short)( index+1), (short)(index+3));
			mesh.addFace(index,(short)( index+3), (short)(index+2));
			
			x1 += (float)c.xAdvance*factorX;
		}
		
		return mesh;
	}

/*
	public void drawStringF(ZRenderer renderer, GL10 gl, String s, float x, float y, float scaleX, float scaleY, AlignH alignH, AlignV alignV, float alpha)
	{
		if (mCharacters==null) return;
		//if (x<0.9f || y> 0.1f) return;	// on n'affiche que le fps
		
		int width = getStringWidth(s);
		int factorX = (int)(renderer.getViewportWidthF()*scaleX*65536.f)/ZRenderer.getScreenWidth();
		int factorY = (int)(renderer.getViewportWidthF()*scaleY*65536.f)/ZRenderer.getScreenHeight();
		
		int xi = (int)(x*65536.f);
		int yi = (int)(y*65536.f);
		int x1,y1;
		switch(alignH)
		{
		case RIGHT:
			x1 = xi - width*factorX;
			break;
		case CENTER:
			x1 = xi - ((width*factorX)>>1);
			break;
		default:
			x1 = xi;
			break;
		}
		switch(alignV)
		{
		case TOP:
			y1 = yi - mHeight*factorY;
			break;
		case CENTER:
			y1 = yi - ((mHeight*factorY)>>1);
			break;
		default:
			y1 = yi;
			break;
		}
		
		int x2,y2=y1+mHeight*factorY;
		
		renderer.draw2dBegin();
		
		for (int i=0; i<s.length(); i++)
		{
			C3dCharacter c = getChar(s.charAt(i));
			x2 = x1 + c.w*factorX; 
			//renderer.draw2dRectangle(gl, x1, y1, x2, y2, mMaterial, c.u1, c.v1, c.u2, c.v2,alpha);
			short index = (short)(i*4);
			renderer.draw2dAddVertexFP(x1,y2,c.u1,c.v2);
			renderer.draw2dAddVertexFP(x1,y1,c.u1,c.v1);
			renderer.draw2dAddVertexFP(x2,y2,c.u2,c.v2);
			renderer.draw2dAddVertexFP(x2,y1,c.u2,c.v1);
//			renderer.draw2dAddVertexIndex(index);						renderer.draw2dAddVertexIndex((short)(index+1));	renderer.draw2dAddVertexIndex((short)(index+2));	
//			renderer.draw2dAddVertexIndex((short)(index+1));	renderer.draw2dAddVertexIndex((short)(index+3));	renderer.draw2dAddVertexIndex((short)(index+2));	
			renderer.draw2dAddVertexIndex(index);						renderer.draw2dAddVertexIndex((short)(index+1));	renderer.draw2dAddVertexIndex((short)(index+2));	renderer.draw2dAddVertexIndex((short)(index+3));				
			x1 = x2;
		}
		
		renderer.draw2dEndTriangleStrip(gl, mMaterial, alpha);	
	}
	*/
}

class C3dCharacter
{
	public char mChar;
	public int u1;		// FP
	public int u2;		// FP
	public int v1;		// FP
	public int v2;		// FP
	public int w;		// in texels
	public int h;		// in texels
	
	public int xOffset;
	public int yOffset;
	public int xAdvance;
}