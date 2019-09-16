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


public class ReleaseSettings {
	
	// debug/dev options
	// ATTENTION AVANT RELEASE
	public static boolean DEBUG_LEVELS = false;
	public static boolean ALWAYS_SHOW_TUTO = false;
	
	public static int 		DEBUG_TIME_FACTOR = 1;
	
	public static boolean	DEBUG_SHOW_VERSION = false;
	public static String 	DEBUG_VERSION = "ZerracSoft BCK dbg3 - Work in progress, don't ditribute";
	public static boolean	DEBUG_SHOW_FPS = false;

	public static boolean 	DEBUG_UNLOCK_ALL_LEVELS = false;	// attention, cette option delocke DEFINITIVEMENT tous les niveaux de la sauvegarde
	
	public static boolean	DEBUG_SAVEGAME_SDCARD = false;
	public static boolean	DEBUG_SHOP_SDCARD = false;				
	
	public static boolean 	DEBUG_UNLOCK_ALL_ADDONS = true;		// shop is disabled in open-source version
	public static boolean 	DEBUG_LOCK_ALL_ADDONS = false;
	public static boolean 	DEBUG_CUSTOM_ALL_ADDONS = false;
	
	public static boolean 	DEBUG_MUTE_LOG = false;
	
//	public static boolean 	DEBUG_SHOP_CREDITS = false;		// buttons to add/remove credits to account
	
	
	// rewards
	public static int		CREDITS_LEVEL = 1; 
	public static int 		CREDITS_WORLD = 5;
	public static int		SHOP_BUG_REWARD = 100;
	
	public static int MAX_FIXED_NODES = 32;
	public static int MAX_MOBILE_NODES = 1024;
	public static int MAX_LINKS = 1024;
	public static int MAX_RAILS = 256;
	public static int MAX_FIXED_RAILS = 16;
	public static int MAX_STONE_COLLUMNS = 16;
	//public static int NB_ITERATIONS = 10;
	public static float DAMPER_FACTOR = 0.3f;
	public static float FRICTION_FACTOR = 10;
	public static float G = 10.f;
	public static float G_WATER = 1.f;
	
	public static int MAX_TRAIN_SIZE = 5;
	public static int MAX_TRAIN_LENGTH = 10;
	
	public static int MAX_NB_BOATS = 4;
	public static float BOAT_BIG_HALF_LENGTH = 5.25f;
	public static float BOAT_BIG_HALF_WIDTH = 2.475f;
	public static float BOAT_BIG_HALF_HEIGHT = 2.25f;
	public static float BOAT_BIG_CENTER_HEIGHT = 0.75f;
	public static float BOAT_BIG_SPEED = 3;
	public static float BOAT_SMALL_HALF_LENGTH = 1.625f;
	public static float BOAT_SMALL_HALF_WIDTH = 0.5f;
	public static float BOAT_SMALL_HALF_HEIGHT = 1.675f;
	public static float BOAT_SMALL_CENTER_HEIGHT = 1.425f;
	public static float BOAT_SMALL_SPEED = 3;

	public static float BAR_MAX_LENGTH = 4.49f; 
	
	public static float CABLE_MAX_LENGTH = 12.01f;
	
	public static float HYDRAULIC_MAX_LENGTH = 4.49f;
	public static float HYDRAULIC_EXTEND_FACTOR = 0.5f;
	public static float HYDRAULIC_RETRACT_FACTOR = 0.5f; 
	public static int HYDRAULIC_LIFTTIMER = 5000;
	public static int HYDRAULIC_PAUSETIMER = 1000;
	
	public static float HYDRAULIC_LOCK_DISTANCEMAX = 0.5f;	// distance mini entre deux noeuds pour se reverrouiller
	public static int START_PAUSETIMER = 3000;
	
	// r�glages de gameplay
	public static float LINK_WEIGHT_TIME_FACTOR = 1.f;
	
	public static int TOWER_COUNTDOWN_LENGTH = 5000;
	public static int TOWER_WINDTEST_LENGTH = 10000;
	
	public static float BREAK_TIME_THRESHOLD = 0.1f;
	
	public static float SUBFRAME_TIME_NODE = 0.04f;
	
	public static float TRAIN_WEIGHT = 22;
	public static float TRAIN_SPEED = 3;
	public static float TRAIN_WHEEL_RAY = 0.5f;
	public static float TRAIN_WHEEL_HALF_DISTANCE = 0.5f;

	public static int EDITOR_VIBRATE_TIME = 30;
	public static int EDITOR_UNDO_QUEUE_QIZE = 5;
	
	public static float TOUCH_MOVE_THRESHOLD = 0.05f;	// seuil de mouvement pour le mode UNKNOWN->CAMERAMOVE
	public static int TOUCH_DRAG_TIME = 0;	// seuil de temps pour le passage UNKNOWN->SELECT ou SELECT->DRAG --> inutile depuis le drag � 2 doigts!
	public static int TOUCH_POPUP_TIME = 500;	// seuil de temps pour le passage UNKNOWN->SELECT ou SELECT->DRAG
	
	public static float floorExtrudeHalfWidth = 15.f;
	public static float waterExtrudeHalfWidth = 14.9f;
	public static float floorUVscale = 0.125f;
	public static float floor2Dratio = 16.f/256.f;	// rapport hauteur/largeur de la texture de bordure d'herbe en 2D
	public static float mudUVscale = 0.25f;
	public static float stoneUVscale = 0.5f;
	
	public static float 	waterUVscale = 0.1f;
	public static int 		waterResolX = 8;
	public static int 		waterResolY = 8;
	public static int 		waterResolZ = 4;
	public static float	waterUVamplitude = 0.1f;
	public static float 	waterUVfrequency = 0.25f;
	
	// vitesse max dans l'eau
	public static float waterMaxSpeedNode = 1;
	public static float waterMaxSpeedTrain = 2;
	
	// failing state time length
	public static int FAILING_TRAIN_TIMER = 5000;
	public static int FAILING_BOAT_TIMER = 5000;
	
	
	// 2d offsets
	public static float layer2dMud = 0.f;
	public static float layer2dGrass = -0.1f;
	public static float layer2dLinkHighlight = -0.3f;
	public static float layer2dLink = -0.2f;
	public static float layer2dRails = -0.25f;
	public static float layer2dNodeHighlight = -1.05f;
	public static float layer2dNode = -1;
	public static float layer2dWater = 0.2f;
	public static float layer2dStone = 0.3f;
	public static float layer2dBoat = 0.1f;
	public static float layer2dGrid = 0.05f;
	public static float layer2dSky = 0.5f;
	public static float layer2dCursor = -1.5f;
	public static float layer2dCursorHighlighted = -2.f;
	public static float layer2dFlashEffect = -2.5f;
	
	// 3d constants
	public static boolean SHOW_PLEXYGLASS = false;
	
	public static float BRIDGE_HALF_WIDTH = 1;
	public static float BAR_HALF_HEIGHT = 0.1f;		// hauteur d'une barre normale
	public static float BAR_HALF_HEIGHT2 = 0.075f;		// hauteur d'une barre "diagonale"
	public static float BAR_HALF_HEIGHT3 = 0.075f;		// hauteur d'une barre de tablier (rail)
	public static float BAR_TRAVERSE_WIDTH = 0.05f;		// largeur d'une traverse de barre
//	public static float CABLE_HALF_HEIGHT = 0.05f;
//	public static int CABLE_NB_SUBDIVISIONS = 4;		// nombre de "segments" rendus pour un cable (nombre de noeuds inter�diaires, en fait, donc nbsegments = subdivisions+1)
	public static float RAILS_THICKNESS = 0.1f;
	public static float RAILS_HALF_WIDTH = 0.5f;
	public static float RAILS_WHEEL_WIDTH = 0.125f;
	public static float RAILS_U_FACTOR = 0.25f;
	public static float STONE_COLLUMN_HALF_WIDTH = 0.7f;
	public static float TOWER_SKY_UV_FACTOR = 0.125f;
	
	// particle systems
	public static int P6_TRAIN_SMOKE_NBPARTILES = 100;	
	
	// UI constants
	public static int MENU_POPUP_ENTER_TIME = 150;
	public static int MENU_POPUP_EXIT_TIME = 150;
	public static int MENU_BUTTON_ENTER_TIME = 150;
	public static int MENU_BUTTON_EXIT_TIME = 150;
	public static int MENU_TEXT_ENTER_TIME = 150;
	public static int MENU_TEXT_EXIT_TIME = 150;
	
	public static int MENU_BACKGROUND_ANIM_PERIOD = 60000;
	
	// game state filename
	public static String	STATUS_GAME_FILENAME = "builder_status.xml";
	public static String	STATUS_SHOP_FILENAME = "builder_shop.xml";
	
	// file prefix
	public static String 	SCREENSHOT_PREFIX = "/Builder_";
	public static String 	SAVEGAME_PREFIX = "/Builder/";

	// wind gauge
	public static float 	WINDGAUGE_ANIM_PERIOD = 1000;
	
	// stress meter
	public static int 		STRESS_NB_LINKS = 64;

	
	
	
	// suspend rendering or not?
	static public void suspendRendering(boolean suspend)
	{
	//	ZRenderer.suspendRendering(suspend);
	}
	
}
