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

#include "Rails.h"
#include "release_settings.h"
#include "../Lib3D/Lib3D.h"

int RailsHelper::GetRailsMeshNbVerts(bool is3D)
{
	if (is3D)
		return 48;
	else
		return 4;
}

int RailsHelper::GetRailsMeshNbFaces(bool is3D)
{
	if (is3D)
		return 28;
	else
		return 2;
}

void  RailsHelper::CreateRailsMesh(bool is3D,ZMesh *mesh,const ZVector *P, const ZVector *X, const ZVector *Z,float vbottom, float vheight)
{
	int index = mesh->getNbVerts();
	static ZVector Y(0,1,0);
	static ZVector v;
	static ZVector Zneg; Zneg.mul(Z,-1);
	static ZVector R; R.copy(X); R.normalize();
	static ZVector L; L.mul(&R,-1);
	float u = (float)(int)(X->norme()*RAILS_U_FACTOR); if (u<1) u=1;

	if (is3D)
	{
		// dessus
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), 0, vbottom+vheight*0);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), 0, vbottom+vheight*0.125f);
		mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*0);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*0.25f);
		mesh->addVertex(&v,ZVector::Yneg(), 0, vbottom+vheight*0.125f);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), 0, vbottom+vheight*0.25f);
		mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*0.25f);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*0.75f);
		mesh->addVertex(&v,ZVector::Y(), 0, vbottom+vheight*0.25f);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Y(), 0, vbottom+vheight*0.125f);
		mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*0.75f);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), 0, vbottom+vheight*1);
		mesh->addVertex(&v,ZVector::Y(), 0, vbottom+vheight*0.125f);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,ZVector::Y(), 0, vbottom+vheight*0);

		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), u, vbottom+vheight*0);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), u, vbottom+vheight*0.125f);
		mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*0);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*0.25f);
		mesh->addVertex(&v,ZVector::Yneg(), u, vbottom+vheight*0.125f);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,ZVector::Yneg(), u, vbottom+vheight*0.25f);
		mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*0.25f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*0.75f);
		mesh->addVertex(&v,ZVector::Y(), u, vbottom+vheight*0.25f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Y(), u, vbottom+vheight*0.125f);
		mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*0.75f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,ZVector::Z(), u, vbottom+vheight*1);
		mesh->addVertex(&v,ZVector::Y(), u, vbottom+vheight*0.125f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,ZVector::Y(), u, vbottom+vheight*0);

		mesh->addFace(index+0,index+14,index+15);
		mesh->addFace(index+0,index+15,index+1);
		mesh->addFace(index+2,index+16,index+17);
		mesh->addFace(index+2,index+17,index+3);
		mesh->addFace(index+4,index+18,index+19);
		mesh->addFace(index+4,index+19,index+5);
		mesh->addFace(index+6,index+20,index+21);
		mesh->addFace(index+6,index+21,index+7);
		mesh->addFace(index+8,index+22,index+23);
		mesh->addFace(index+8,index+23,index+9);
		mesh->addFace(index+10,index+24,index+25);
		mesh->addFace(index+10,index+25,index+11);
		mesh->addFace(index+12,index+26,index+27);
		mesh->addFace(index+12,index+27,index+13);

		// dessous
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&Zneg, 0, vbottom+vheight*0);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&Zneg, 0, vbottom+vheight*1);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&Zneg, u, vbottom+vheight*0);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&Zneg, u, vbottom+vheight*1);
		mesh->addFace(index+28,index+29,index+30);
		mesh->addFace(index+30,index+29,index+31);

		// cot�s gauche et droit
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*1);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*1);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0.25f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0.75f);
		v.mulAddMul(X,1,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0.25f);
		v.mulAddMul(X,1,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,&R, 0, vbottom+vheight*0.75f);
		mesh->addFace(index+32,index+33,index+35);
		mesh->addFace(index+32,index+35,index+37);
		mesh->addFace(index+32,index+37,index+36);
		mesh->addFace(index+32,index+36,index+34);
		mesh->addFace(index+36,index+37,index+39);
		mesh->addFace(index+36,index+39,index+38);

		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*1);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH,Z,0); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*1);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0.75f);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0.25f);
		v.mulAddMul(X,0,&Y,RAILS_HALF_WIDTH-RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0.75f);
		v.mulAddMul(X,0,&Y,-RAILS_HALF_WIDTH+RAILS_WHEEL_WIDTH,Z,RAILS_THICKNESS*2.f); 		v.add(P);	mesh->addVertex(&v,&L, 0, vbottom+vheight*0.25f);
		mesh->addFace(index+40,index+41,index+43);
		mesh->addFace(index+40,index+43,index+45);
		mesh->addFace(index+40,index+45,index+44);
		mesh->addFace(index+40,index+44,index+42);
		mesh->addFace(index+40,index+45,index+47);
		mesh->addFace(index+40,index+47,index+46);

	}
	else
	{
		v.mulAddMul(&Y,LAYER2D_RAILS,Z,0); v.add(P); mesh->addVertex(&v, ZVector::Yneg(), 0, vbottom);
		v.mulAddMul(X,1,&Y,LAYER2D_RAILS,Z,0); v.add(P); mesh->addVertex(&v, ZVector::Yneg(), u, vbottom);
		v.mulAddMul(&Y,LAYER2D_RAILS,Z,RAILS_THICKNESS*2.f); v.add(P); mesh->addVertex(&v, ZVector::Yneg(), 0, vbottom+vheight);
		v.mulAddMul(X,1,&Y,LAYER2D_RAILS,Z,RAILS_THICKNESS*2.f); v.add(P); mesh->addVertex(&v, ZVector::Yneg(), u, vbottom+vheight);
		mesh->addFace(index+0,index+1,index+3);
		mesh->addFace(index+0,index+3,index+2);

	}
}

/*
	public static class Helper
	{
		static ZVector V = new ZVector();

		public static ZMesh createMesh(float width)
		{
			ZMesh meshRails = new ZMesh();
			meshRails.allocMesh(48, 28, true);
			float w = width*0.5f;
			float u = Math.max(1,Math.round(width*ReleaseSettings.instance.RAILS_U_FACTOR));

			// dessus
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constYNeg, 0, 0);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constYNeg, 0, 0.125f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, 0, 0);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, 0, 0.25f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constYNeg, 0, 0.125f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constYNeg, 0, 0.25f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, 0, 0.25f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, 0, 0.75f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constY, 0, 0.25f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constY, 0, 0.125f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, 0, 0.75f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, 0, 1);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constY, 0, 0.125f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);		meshRails.addVertex(V,ZVector.constY, 0, 0);

			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constYNeg, u, 0);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constYNeg, u, 0.125f);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, u, 0);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, u, 0.25f);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constYNeg, u, 0.125f);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constYNeg, u, 0.25f);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, u, 0.25f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constZ, u, 0.75f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,ZVector.constY, u, 0.25f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constY, u, 0.125f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);	meshRails.addVertex(V,ZVector.constZ, u, 0.75f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constZ, u, 1);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constY, u, 0.125f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);		meshRails.addVertex(V,ZVector.constY, u, 0);

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
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, 0, 0);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, 0, 1);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, u, 0);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constZNeg, u, 1);
			meshRails.addFace(28,29,30);
			meshRails.addFace(30,29,31);

			// cot�s gauche et droit
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constX, 0, 0);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constX, 0, 1);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constX, 0, 0);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constX, 0, 1);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constX, 0, 0.25f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constX, 0, 0.75f);
			V.set(w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constX, 0, 0.25f);
			V.set(w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constX, 0, 0.75f);
			meshRails.addFace(32,33,35);
			meshRails.addFace(32,35,37);
			meshRails.addFace(32,37,36);
			meshRails.addFace(32,36,34);
			meshRails.addFace(36,37,39);
			meshRails.addFace(36,39,38);

			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constXNeg, 0, 1);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,0);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constXNeg, 0, 1);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);		meshRails.addVertex(V,ZVector.constXNeg, 0, 0);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.75f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.25f);
			V.set(-w,ReleaseSettings.instance.RAILS_HALF_WIDTH-ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.75f);
			V.set(-w,-ReleaseSettings.instance.RAILS_HALF_WIDTH+ReleaseSettings.instance.RAILS_WHEEL_WIDTH,ReleaseSettings.instance.RAILS_THICKNESS*2.f);												meshRails.addVertex(V,ZVector.constXNeg, 0, 0.25f);
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
			float u = Math.max(1,Math.round(width*ReleaseSettings.instance.RAILS_U_FACTOR));

			meshRails.allocMesh(4, 2, false);
			V.set(-w,0,0);												meshRails.addVertex(V,null, 0, 0);
			V.set(w,0,0);												meshRails.addVertex(V,null, u, 0);
			V.set(-w,0,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,null, 0, 1);
			V.set(w,0,ReleaseSettings.instance.RAILS_THICKNESS*2.f);	meshRails.addVertex(V,null, u, 1);
			meshRails.addFace(0,1,3);
			meshRails.addFace(0,3,2);

			meshRails.setMaterial("rails");
			meshRails.enableBackfaceCulling(false);

			return meshRails;
		}
	}
*/
