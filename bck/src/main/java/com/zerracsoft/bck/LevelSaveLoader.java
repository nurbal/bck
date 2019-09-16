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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class LevelSaveLoader extends DefaultHandler
{
	protected Level mLevel;
	
	public boolean load(InputStream in, Level level)
	{

		mLevel = level;
		mLevel.resetGameplay(false);
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		
		mLevel.fixIntegrity();
		mLevel.initGraphics(); // ICI ca freeze..... PUTAIN
		
		return true;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equals("node"))
		{
			// create a node
			Node n = new Node(attributes);
			if (n.isFixed())
			{
				// recherche du noeud en question et application de l'id
				Node fixedNode = mLevel.getNearestFixedNode(n.getPositionX(), n.getPositionZ());
				if (fixedNode!=null)
					fixedNode.setSaveID(n.getSaveID());
			}
			else
				mLevel.add(n);
		}
		
		if (localName.equals("linkelastic") || localName.equals("linkbar"))
		{
			// create a node
			try {
				LinkBar l = new LinkBar(mLevel,attributes);
				mLevel.add((Link)l);
			} catch (SaveFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (localName.equals("linkverrin") || localName.equals("linkjack"))
		{
			// create a node
			try {
				LinkJack l = new LinkJack(mLevel,attributes);
				mLevel.add((Link)l);
			} catch (SaveFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (localName.equals("linkcable"))
		{
			// create a node
			try {
				LinkCable l = new LinkCable(mLevel,attributes);
				mLevel.add((Link)l);
			} catch (SaveFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
