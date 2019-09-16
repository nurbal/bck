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

#ifndef __BUILDER_RELEASE_SETTINGS__
#define __BUILDER_RELEASE_SETTINGS__

#define GRAVITY 		(10.f)
#define GRAVITY_WATER 	(1.f)
#define DAMPER_FACTOR 	(0.3f)
#define FRICTION_FACTOR	(10.f)

#define WIND_FORCE		(10.f)

#define WATER_MAX_SPEED_NODE (1.f)
#define HYDRAULIC_LOCK_DISTANCEMAX (0.5f)

#define LINK_WEIGHT_TIME_FACTOR (0.5f)

#define BAR_UPDATE_ITERATIONS 5
#define BAR_MAXLENGTHFACTOR (1.025f)
#define BAR_MINLENGTHFACTOR (0.95f)
#define BAR_DENSITY (1.f)

#define CABLE_UPDATE_ITERATIONS 7
#define CABLE_SEGMENTS_UPDATE_ITERATIONS 4
#define CABLE_MAXLENGTHFACTOR (1.05f)
#define CABLE_DENSITY (1.f)

#define JACK_UPDATE_ITERATIONS 6
#define JACK_MAXLENGTHFACTOR (1.02f)
#define JACK_MINLENGTHFACTOR (0.98f)
#define JACK_DENSITY (1.f)

#define BREAK_TIME_THRESHOLD (0.5f)

#define P6_BARS_BREAK_NBPARTILES 20
#define P6_BARS_BREAK_COUNT 10
#define P6_CABLES_BREAK_NBPARTILES 40
#define P6_CABLES_BREAK_COUNT 7
#define P6_JACKS_BREAK_NBPARTILES 20
#define P6_JACKS_BREAK_COUNT 4

#define BARS_MESH_NB_VERTS		32*1024//10240
#define BARS_MESH_NB_FACES		16*1024//10240
#define CABLES_MESH_NB_VERTS 	16*1024//10240
#define CABLES_MESH_NB_FACES 	8*1024//10240

// 3d constants
#define BRIDGE_HALF_WIDTH 1.f
#define BAR_HALF_HEIGHT 0.1f		// hauteur d'une barre normale
#define BAR_HALF_HEIGHT2 0.075f		// hauteur d'une barre "diagonale"
#define BAR_HALF_HEIGHT3 0.075f		// hauteur d'une barre de tablier (rail)
#define BAR_TRAVERSE_WIDTH 0.05f	// largeur d'une traverse de barre

#define RAILS_THICKNESS 0.1f
#define RAILS_HALF_WIDTH 0.5f
#define RAILS_WHEEL_WIDTH 0.125f
#define RAILS_U_FACTOR 0.25f

#define CABLE_NB_SUBDIVISIONS 10
#define CABLE_HALF_HEIGHT 0.05f



// 2D constants
#define LAYER2D_LINK	(-0.2f)
#define LAYER2D_RAILS	(-0.15f)



#endif //__BUILDER_RELEASE_SETTINGS__
