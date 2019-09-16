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

public class BoatSmall extends Boat
{
	public BoatSmall(float x,boolean direction)
	{
		super("boat_small",
				ReleaseSettings.BOAT_SMALL_HALF_LENGTH, 
				ReleaseSettings.BOAT_SMALL_HALF_WIDTH, 
				ReleaseSettings.BOAT_SMALL_HALF_HEIGHT, 
				ReleaseSettings.BOAT_SMALL_CENTER_HEIGHT, 
				ReleaseSettings.BOAT_SMALL_SPEED,
				x,direction);
	}
}
