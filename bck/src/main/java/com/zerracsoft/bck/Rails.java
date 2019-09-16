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

import com.zerracsoft.Lib3D.ZMesh;
import com.zerracsoft.Lib3D.ZVector;

// interface commune a tousles troncons de rails.
// utilise par les trains pour... bin pour rouler, quoi!

public interface Rails 
{
	
	public class Proximity
	{
		public Rails rails;
		public ZVector normal = new ZVector();
		public ZVector tangent = new ZVector();
		public ZVector point = new ZVector();
		public float distance;
		
		public void copy(Proximity from)
		{
			rails = from.rails;
			normal.copy(from.normal);
			tangent.copy(from.tangent);
			point.copy(from.point);
			distance = from.distance;
		}
	}
	
	public static class Helper
	{
		static ZVector V = new ZVector();

		public static ZMesh createMesh(float width)
		{
			ZMesh meshRails = new ZMesh();	
			meshRails.allocMesh(48, 28, true);
			float w = width*0.5f;
			float u = Math.max(1,Math.round(width*ReleaseSettings.RAILS_U_FACTOR));
			
			// dessus
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constYNeg, 0, 0);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constYNeg, 0, 0.125f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, 0, 0);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, 0, 0.25f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constYNeg, 0, 0.125f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constYNeg, 0, 0.25f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, 0, 0.25f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, 0, 0.75f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constY, 0, 0.25f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constY, 0, 0.125f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, 0, 0.75f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, 0, 1);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constY, 0, 0.125f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,0);		meshRails.addVertex(V,ZVector.constY, 0, 0);
	
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constYNeg, u, 0);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constYNeg, u, 0.125f);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, u, 0);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, u, 0.25f);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constYNeg, u, 0.125f);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constYNeg, u, 0.25f);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, u, 0.25f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, u, 0.75f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constY, u, 0.25f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constY, u, 0.125f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, u, 0.75f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, u, 1);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constY, u, 0.125f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,0);		meshRails.addVertex(V,ZVector.constY, u, 0);
	
			meshRails.addFace(0,14,15);
			meshRails.addFace(0,15,1);
			meshRails.addFace(2,16,17);
			meshRails.addFace(2,17,3);
			meshRails.addFace(4,18,19);
			meshRails.addFace(4,19,5);
			meshRails.addFace(6,20,21);
			meshRails.addFace(6,21,7);
			meshRails.addFace(8,22,23);
			meshRails.addFace(8,23,9);
			meshRails.addFace(10,24,25);
			meshRails.addFace(10,25,11);
			meshRails.addFace(12,26,27);
			meshRails.addFace(12,27,13);
			
			// dessous
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, 0, 0);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, 0, 1);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, u, 0);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, u, 1);
			meshRails.addFace(28,29,30);
			meshRails.addFace(30,29,31);
			
			// cotes gauche et droit
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constX, 0, 0);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constX, 0, 1);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constX, 0, 0);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constX, 0, 1);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constX, 0, 0.25f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constX, 0, 0.75f);
			V.set(w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constX, 0, 0.25f);
			V.set(w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constX, 0, 0.75f);
			meshRails.addFace(32,33,35);
			meshRails.addFace(32,35,37);
			meshRails.addFace(32,37,36);
			meshRails.addFace(32,36,34);
			meshRails.addFace(36,37,39);
			meshRails.addFace(36,39,38);
			
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constXNeg, 0, 1);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constXNeg, 0, 1);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH,ReleaseSettings.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constXNeg, 0, 0);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.75f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.25f);
			V.set(-w,ReleaseSettings.RAILS_HALF_WIDTH-ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.75f);
			V.set(-w,-ReleaseSettings.RAILS_HALF_WIDTH+ReleaseSettings.RAILS_WHEEL_WIDTH,ReleaseSettings.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.25f);
			meshRails.addFace(40,41,43);
			meshRails.addFace(40,43,45);
			meshRails.addFace(40,45,44);
			meshRails.addFace(40,44,42);
			meshRails.addFace(40,45,47);
			meshRails.addFace(40,47,46);
			
			meshRails.setMaterial("rails");
			meshRails.enableBackfaceCulling(true);
			
			return meshRails;
		}
	
		public static ZMesh createMesh2D(float width)
		{
			ZMesh meshRails = new ZMesh();	
			float w = width*0.5f;
			float u = Math.max(1,Math.round(width*ReleaseSettings.RAILS_U_FACTOR));
			
			meshRails.allocMesh(4, 2, false);
			V.set(-w,0,0);												meshRails.addVertex(V,null, 0, 0);
			V.set(w,0,0);												meshRails.addVertex(V,null, u, 0);
			V.set(-w,0,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,null, 0, 1);
			V.set(w,0,ReleaseSettings.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,null, u, 1);
			meshRails.addFace(0,1,3);
			meshRails.addFace(0,3,2);
			
			meshRails.setMaterial("rails");
			meshRails.enableBackfaceCulling(false);
			
			return meshRails;
		}
	}
	
	public boolean getRailsNearestPoint(ZVector inPoint,float inMaxRay,Proximity out); // returns false if none
	public ZVector getRailsDirection(); // ATTENTION ! check isRailunder before calling this function
	public void addWeight(float x, float w); // ATTENTION ! check isRailunder before calling this function
}
