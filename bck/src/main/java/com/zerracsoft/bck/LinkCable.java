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
import java.io.OutputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.zerracsoft.bck.Level.Backup;

public class LinkCable extends Link 
{
	protected native static long JNIconstructor();
	
	// native object pointer
	protected long mNativeObject;
	public long getNativeObject() {return mNativeObject;}
		
	public LinkCable(Node n1, Node n2)
	{
		mNativeObject = JNIconstructor();
		init(n1,n2);
	}
	public LinkCable(Link l)
	{
		mNativeObject = JNIconstructor();
		JNIcopy(mNativeObject,l.getNativeObject());
	}
	
	public LinkCable(Level level,Attributes attributes) throws SaveFileException
	{
		mNativeObject = JNIconstructor();
		
		String node1id=attributes.getValue("node1");
		String node2id=attributes.getValue("node2");
		if (node1id==null || node2id==null) throw new SaveFileException();
		Node Node1 = level.getNode(Integer.parseInt(node1id));
		Node Node2 = level.getNode(Integer.parseInt(node2id));
		if (Node1==null || Node2==null) throw new SaveFileException();
		init (Node1,Node2);
	}

	
	public void save(OutputStream out) throws IOException
	{
		String s = "<linkcable node1=\""+Integer.toString(getNodeSaveID(0))+"\" node2=\""+Integer.toString(getNodeSaveID(1))+"\"/>\n";
		out.write(s.getBytes());
	}
			
	@Override
	public void initGraphics() {}
	
	@Override
	public void resetGraphics() {}

	@Override
	public void updateGraphics(int elapsedTime) {}
	
	@Override
	public float getEditMaxLength() {return ReleaseSettings.CABLE_MAX_LENGTH;}


	@Override
	public void listRails(ArrayList<Rails> list) {}

	@Override
	public void setHydraulicLiftFactor(float f) {}
	

	@Override
	public Link createBackup(Backup b) { return new LinkCable(b.getNode(getNodeSaveID(0)),b.getNode(getNodeSaveID(1))); }

	@Override
	public boolean isEditable() { return true; }
	
	@Override
	public void updateBudget(StatusLevel s) { s.mSpentCable++; }
}
