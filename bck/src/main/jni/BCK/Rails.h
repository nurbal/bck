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

#ifndef __RAILS__
#define __RAILS__

class ZMesh;
class ZVector;

// some helpers to create rails meshs...

class RailsHelper
{
public:
	static int GetRailsMeshNbVerts(bool is3D);
	static int GetRailsMeshNbFaces(bool is3D);
	static void CreateRailsMesh(bool is3D,ZMesh *mesh,const ZVector *P, const ZVector *X, const ZVector *Z,float vbottom,float vheight);
};




#endif // __RAILS__
