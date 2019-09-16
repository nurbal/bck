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
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ShopManagerLoader extends DefaultHandler 
{

	public boolean load(InputStream in)
	{
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
			BCKLog.e("ShopManagerLoader", e.getMessage());
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			BCKLog.e("ShopManagerLoader", e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			BCKLog.e("ShopManagerLoader", e.getMessage());
			e.printStackTrace();
			return false;
		} 
				
		return true;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equals("shop"))
		{
			String virtualgoodstransfered = attributes.getValue("virtualgoodstransfered");
			if (virtualgoodstransfered!=null)
				ShopManager.instance.mSkipWorldVirtualGoodsTransfered = Boolean.parseBoolean(virtualgoodstransfered);
			String bugrewarded = attributes.getValue("bugrewarded");
			if (bugrewarded!=null)
				ShopManager.instance.mBugRewarded = Boolean.parseBoolean(bugrewarded);
			
		}
		
		if (localName.equals("purchased"))
		{
			String id = attributes.getValue("id");
			if (id!=null)
			{
				ShopManager.instance.setPurchased(ShopManager.AddonType.valueOf(id));
				BCKLog.d("ShopManagerLoader", "Loading "+id);
				
			}
		}
	
	}

	
}
