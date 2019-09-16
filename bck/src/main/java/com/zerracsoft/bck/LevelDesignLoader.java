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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.zerracsoft.Lib3D.ZInstance;
import com.zerracsoft.Lib3D.ZVector;

public class LevelDesignLoader extends DefaultHandler
{
	protected Level mLevel;
	char[] mLineChars;
	int mLineIndex;
	
	class SceneDecoration
	{
		public char mType;
		public int mX;
		public SceneDecoration(char type, int x) {mType=type; mX=x;}
	}
	ArrayList<SceneDecoration> mSceneDecorations = new ArrayList<SceneDecoration>(10);
	
	boolean mBoatDirection;
	
	public boolean load(InputStream in, Level level)
	{
		mLevel = level;
		mLevel.resetAll();
		
		mLineChars = null;
		mLineIndex = 0;
		
		mBoatDirection = Math.random()>0.5;
		
		try 
		{
			
	        /* Get a SAXParser from the SAXPArserFactory. */ 
	        SAXParserFactory spf = SAXParserFactory.newInstance(); 
	        SAXParser sp;
			sp = spf.newSAXParser();

	        /* Get the XMLReader of the SAXParser we created. */ 
	        XMLReader xr = sp.getXMLReader(); 
	        /* Create a new ContentHandler and apply it to the XML-Reader*/ 
	        xr.setContentHandler(this); 
	         
	        /* Parse the xml-data from our URL. */ 
			xr.parse(new InputSource(in));
	        /* Parsing has finished. */ 

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		mLevel.completeFloor();	// complete les trous, en particulier sur les bords...
		
		instanciateDecorations();
		
		int w = mLineChars.length; // par defaut, la hauteur est egale a la largeur du niveau...
		if (mLevel.isTowerMode()) w = (int)mLevel.getTowerGoal() + 16;
		mLevel.setZExtents(mLineIndex, w);
		mLevel.mCenterOfInterest = new SmoothVector(0,0,0,2,mLevel.getXMin(),mLevel.getXMax(),0,0,mLevel.getZMin(),mLevel.getZMax());
		
		return true;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equals("save"))
		{
			// save filename
			mLevel.mSaveFilename = attributes.getValue("filename");
		}
		
		if (localName.equals("towermode"))
		{
			// this level is a tower mode level
			String goal = attributes.getValue("goal");
			if (goal!=null)
			{
				mLevel.setTowerMode(true);
				mLevel.setTowerGoal(Float.parseFloat(goal));
			}
		}

		if (localName.equals("tutorial"))
		{
			String id = attributes.getValue("id");
			if (id!=null)
			{
				mLevel.mTutorialsID.add(id);
			}
		}
		
		if (localName.equals("line"))
		{
			// a line of design
			String line = attributes.getValue("value");
			if (line!=null)
			{
				int len = line.length();
				if (mLineChars==null)
				{
					// INITIALISATION DU NIVEAU
					// on a notre longueur "officielle", celle de la PREMIERE ligne
					mLineChars = new char[len];
					line.getChars(0, len, mLineChars, 0);
					//mLevel.allocFloor(-ReleaseSettings.MAX_TRAIN_LENGTH, len+ReleaseSettings.MAX_TRAIN_LENGTH);
					mLevel.allocFloor(0, len-1);
					mSceneDecorations.clear();
				}
				else
				{
					len = Math.min(len, mLineChars.length);
					line.getChars(0, len, mLineChars, 0);
				}
				
				
				boolean isCreatingRail = false;
				boolean isCreatingStone = false;
				int railStart = 0;
				int stoneStart = 0;
				// decodage de la ligne
				for(int x=0; x<len; x++)
				{
					//R = rail+sol (rail x a x+1) (le premier indique la hauteur des rails par defaut)
					//n = node
					//N = node+sol
					//O = node+sol+rail
					//G = ground level
					//W = water level (un seul suffit)
					//B = boat (au meme niveau que l'eau)
					boolean nearHeight = false;
					boolean floorHeight = false;
					boolean farHeight = false;
					boolean rail = false;
					boolean node = false;
					boolean water = false;
					boolean boatBig = false;
					boolean boatSmall = false;
					boolean stone = false;
					switch (mLineChars[x])
					{
					case '.':	nearHeight = true; break;
					case '-':	floorHeight = true; break;
					case '_':	farHeight = true; break;
					case '+':	floorHeight = node = true; break;
					case '*':	floorHeight = node = rail = true; break;
					case '=':	floorHeight = rail = true; break;
					case '#':	stone = true; break;
					case '@':	node = stone = true; break;
					case 'o':	node = true; break;
					case 'W':	water = true; break;
					case 'B':	water = boatBig = true; break;
					case 'b':	water = boatSmall = true; break;
					
					// decorations...
					case 'L':
					case 'l':
					case 'C':
					case 'c':
					case 'S':
					case 'P':
					case 'p':
						mSceneDecorations.add(new SceneDecoration(mLineChars[x],x));
						break;
						
					default:	break;
					}
					
					if (mLevel.isTowerMode())
					{
						rail = false;
						boatBig = false;
						boatSmall = false;
					}
					
					//if (mLevel.getFloorHeight(x)<mLineIndex) 
						if (floorHeight)
							mLevel.setFloorHeight(x, mLineIndex);
					//if (mLevel.getNearHeight(x)<mLineIndex) 
						if (nearHeight)
							mLevel.setNearHeight(x, mLineIndex);
					//if (mLevel.getFarHeight(x)<mLineIndex) 
						if (farHeight)
							mLevel.setFarHeight(x, mLineIndex);
					
					if (node)
						mLevel.add(new Node(x,mLineIndex,true));
	
					if (rail)
					{
						if (!isCreatingRail)
						{
							isCreatingRail = true;
							railStart = x;
						}
					}
					else
					{
						if (isCreatingRail)
						{
							isCreatingRail = false;
							//if (railStart==0) railStart=-ReleaseSettings.MAX_TRAIN_LENGTH;
							FixedRails r = new FixedRails(railStart,x,mLineIndex);
							mLevel.add(r);
						}
					}
					
					if (stone)
					{
						if (!isCreatingStone)
						{
							isCreatingStone = true;
							stoneStart = x;
						}
					}
					else
					{
						if (isCreatingStone)
						{
							isCreatingStone = false;
							StoneCollumn s = new StoneCollumn((float)stoneStart-0.5f,(float)x-0.5f,mLineIndex);
							mLevel.add(s);
						}
					}
					
					if (water)
					{
						if (!mLevel.hasWater()) mLevel.setWaterHeight(mLineIndex);
					}
					if (boatBig)
					{
						Boat b = new BoatBig(x,mBoatDirection);
						mBoatDirection = !mBoatDirection;
						mLevel.add(b);
					}
					if (boatSmall)
					{
						Boat b = new BoatSmall(x,mBoatDirection);
						mBoatDirection = !mBoatDirection;
						mLevel.add(b);
					}
				}
				
				if (isCreatingRail)
				{
					//if (railStart==0) railStart=-ReleaseSettings.MAX_TRAIN_LENGTH;
					FixedRails r = new FixedRails(railStart,mLineChars.length-1/*+ReleaseSettings.MAX_TRAIN_LENGTH*/,mLineIndex);
					mLevel.add(r);
				}
				if (isCreatingStone)
				{
					StoneCollumn s = new StoneCollumn((float)stoneStart-0.5f,(float)(mLineChars.length)-0.5f,mLineIndex);
					mLevel.add(s);
				}
				/*
				for (int x=-ReleaseSettings.MAX_TRAIN_LENGTH; x<0; x++)
					mLevel.setFloorHeight(x,mLevel.getFloorHeight(0));
				for (int x=mLineChars.length; x<=mLineChars.length+ReleaseSettings.MAX_TRAIN_LENGTH; x++)
					mLevel.setFloorHeight(x,mLevel.getFloorHeight(mLineChars.length-1));
				*/
			}
			
			
			mLineIndex--;
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
	}
	
	protected void instanciateDecorations()
	{
			// creation et placement des decorations
			while (!mSceneDecorations.isEmpty())
			{
				SceneDecoration deco = mSceneDecorations.remove(0);
				String meshName = null;
				float x = deco.mX;
				float y=0,z=0;
				
				switch(deco.mType)
				{
				case 'l':	meshName = "lego";		y=-ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getNearHeight(deco.mX);		break;
				case 'L':	meshName = "lego";		y=ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getFarHeight(deco.mX);			break;
				
				case 'p':	meshName = "pen";		y=-ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getNearHeight(deco.mX);		break;
				case 'P':	meshName = "pen";		y=ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getFarHeight(deco.mX);			break;
				
				case 'c':	meshName = "cube";		y=-ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getNearHeight(deco.mX);		break;
				case 'C':	meshName = "cube";		y=ReleaseSettings.floorExtrudeHalfWidth*0.5f; 		z=mLevel.getFarHeight(deco.mX);			break;
				
				case 'S':	meshName = "gare";		y=0; 		z=mLevel.getFloorHeight(deco.mX);		break;
					default: break;
				}
				
				if (meshName!=null) mLevel.addDecorObject(meshName,new ZVector(x,y,z));

			}
		
	}
	
}
