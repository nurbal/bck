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

import android.util.Log;


// en faisant passer tous les logs par cette classe, il est plus facile de les bloquer

public class BCKLog {
	
	static public void e(String tag, String s) 	{if (!ReleaseSettings.DEBUG_MUTE_LOG) Log.e(tag,s);}
	static public void d(String tag, String s) 	{if (!ReleaseSettings.DEBUG_MUTE_LOG) Log.d(tag,s);}
	static public void i(String tag, String s) 	{if (!ReleaseSettings.DEBUG_MUTE_LOG) Log.i(tag,s);}
	static public void v(String tag, String s) 	{if (!ReleaseSettings.DEBUG_MUTE_LOG) Log.v(tag,s);}
	static public void w(String tag, String s) 	{if (!ReleaseSettings.DEBUG_MUTE_LOG) Log.w(tag,s);}

}
