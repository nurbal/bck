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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.zerracsoft.Lib3D.ZActivity;
import com.zerracsoft.Lib3D.ZRenderer;

import android.os.Environment;

public class ShopManager 
{
	public static ShopManager instance;
	
	public int mStateCounter = 0;	// indique un changement d'�tat (pour les menus)
	
	private boolean mLoaded = false;
	
	public boolean mSkipWorldVirtualGoodsTransfered = false;
	public boolean mBugRewarded = false;			// tells if the bug of lost items has been rewarded (if applicable)
	
	public static enum AddonType
	{
		FREEBUILD_MODE,
		ONBOARD_CAMERA,
		NO_ADS,
		SHOW_CONSTRAINTS,
		SCREENSHOT,
		SKIP_WORLD   		// this one is specific	
	};
	

	private ArrayList<AddonType> mPurchasedItems = new ArrayList<AddonType>(10);
	private ArrayList<AddonType> mPurchasingItems = new ArrayList<AddonType>(10);	// purshased, not confirmed yet by tapjoy
	
	
	public boolean isPurchased(AddonType addon)
	{
		if (ReleaseSettings.DEBUG_UNLOCK_ALL_ADDONS)
			return true;
		else
		    return mPurchasedItems.contains(addon);
	}
	
	public boolean isPurchasing(AddonType addon)
	{
		return mPurchasingItems.contains(addon);
	}
	
	public boolean isPurchasingAny()
	{
		return (!mPurchasingItems.isEmpty());
	}
	
	public static int getPrice(AddonType addon)
	{
		switch (addon)
		{
		case FREEBUILD_MODE:	return 30;
		case ONBOARD_CAMERA:	return 10;
		case NO_ADS:			return 30;
		case SHOW_CONSTRAINTS:	return 10;
		case SCREENSHOT:		return 10;
		case SKIP_WORLD:		return 30;
		}
		return 0;
	}
	
	public static boolean canPurchase(AddonType addon)
	{
		return false; //(BCKTapjoyManager.instance.mCredits >= getPrice(addon));
	}
	
	public static String getTitle(AddonType addon)
	{
		int resID = 0;
		switch (addon)
		{
		case FREEBUILD_MODE:	resID = R.string.menu_shop_freebuild_mode;		break;
		case ONBOARD_CAMERA:	resID = R.string.menu_shop_onboard_camera;		break;
		case NO_ADS:			resID = R.string.menu_shop_no_ads;				break;
		case SHOW_CONSTRAINTS:	resID = R.string.menu_shop_show_constraints;	break;
		case SCREENSHOT:		resID = R.string.menu_shop_screenshots;			break;
		case SKIP_WORLD:		resID = R.string.menu_shop_skip_world;			break;
		}
		return ZActivity.instance.getResources().getString(resID);
	}
	public static String getDesc(AddonType addon)
	{
		int resID = 0;
		switch (addon)
		{
		case FREEBUILD_MODE:	resID = R.string.menu_shop_freebuild_mode_desc;		break;
		case ONBOARD_CAMERA:	resID = R.string.menu_shop_onboard_camera_desc;		break;
		case NO_ADS:			resID = R.string.menu_shop_no_ads_desc;				break;
		case SHOW_CONSTRAINTS:	resID = R.string.menu_shop_show_constraints_desc;	break;
		case SCREENSHOT:		resID = R.string.menu_shop_screenshots_desc;		break;
		case SKIP_WORLD:		resID = R.string.menu_shop_skip_world_desc;			break;
		}
		return ZActivity.instance.getResources().getString(resID);
	}
	
	public static String getMaterialName(AddonType addon)
	{
		switch (addon)
		{
		case FREEBUILD_MODE:	return "addon_free_build";
		case ONBOARD_CAMERA:	return "addon_onboard_camera";
		case NO_ADS:			return "addon_no_ads";
		case SHOW_CONSTRAINTS:	return "addon_show_constraints";
		case SCREENSHOT:		return "addon_screenshots";
		case SKIP_WORLD:		return "addon_skip_world";
		}
		return "";
	}
	
	public void purchase(AddonType addon,Runnable onSuccess, Runnable onFail)
	{
		return;
	}
	
	public void setPurchased(AddonType addon)
	{
		if (isPurchased(addon)) return;
		mPurchasedItems.add(addon);
	}
	private int mNbSkipWorldVirtualGoodsTransfered = 0;
	public void transferSkipWorldVirtualGoods(int nb)
	{
		if (mSkipWorldVirtualGoodsTransfered) return;
		mNbSkipWorldVirtualGoodsTransfered = nb;
		checkTransferedVirtualGoods();
	}
	
	public void checkTransferedVirtualGoods()
	{
	}
	
	public void onSpendResult(boolean success)
	{
		// incr�mentation de l'�tat du shop...
		mStateCounter++; 
		
		if (mPurchasingItems.isEmpty())
		{
			BCKLog.e("ShopManager", "spendResult called but no pending purchase...");
			return;
		}
		AddonType addon = mPurchasingItems.remove(0); 
		if (success)
		{
			mPurchasedItems.add(addon);
			save();
		}
		
	}
	
	
	
	public void save()
	{
		BCKLog.d("ShopManager", "Saving shop status mLoaded="+Boolean.toString(mLoaded));
		if (!mLoaded) return; // BUGFIX !!!!!!
		String dirname = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX;
		if (!ReleaseSettings.DEBUG_SHOP_SDCARD)
			dirname = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX;	
		File dir = new File(dirname);
		dir.mkdirs();
		String filename = dirname+ReleaseSettings.STATUS_SHOP_FILENAME;
		
		File f = new File(filename);
		
		try { 
			OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			save(out);
			out.flush();
			} catch (IOException e)
			{
				BCKLog.e("ShopManager", e.getMessage());
				e.printStackTrace(); 
			}
	}	
	
	public void save(OutputStream out) throws IOException
	{
		out.write(("<shop virtualgoodstransfered=\""+Boolean.toString(mSkipWorldVirtualGoodsTransfered)+"\" bugrewarded=\""+Boolean.toString(mBugRewarded)+"\">\n").getBytes());	
		for (AddonType addon : mPurchasedItems)
		{
			String s = "\t<purchased id=\""+addon.name()+"\"/>\n"; // SHOULD I USE addon.name() instead of addon.toString() ?
			out.write(s.getBytes());		
			BCKLog.d("ShopManager", "Saving "+addon.name());
		}	
		out.write("</shop>\n".getBytes());		 
		 
	}
	
	public void load()
	{
		BCKLog.d("ShopManager", "Loading shop status mLoaded="+Boolean.toString(mLoaded));
		String filename = Environment.getExternalStorageDirectory()+ReleaseSettings.SAVEGAME_PREFIX+ReleaseSettings.STATUS_SHOP_FILENAME;
		if (!ReleaseSettings.DEBUG_SHOP_SDCARD)
			filename = GameActivity.instance.getFilesDir().getAbsolutePath()+ReleaseSettings.SAVEGAME_PREFIX+ReleaseSettings.STATUS_SHOP_FILENAME;	
		File f = new File(filename);
		
		try { load(new BufferedInputStream(new FileInputStream(f),8192)); } 
		catch (FileNotFoundException e)
		{ 
			BCKLog.e("ShopManager", e.getMessage());
			e.printStackTrace(); 
			BCKLog.e("ShopManager", "Bug has not to be rewarded");
			mBugRewarded = true;
		}

		// rename old file ?
		if (ReleaseSettings.DEBUG_SHOP_SDCARD)
		{
			File old1 = new File(filename+".1");
			File old2 = new File(filename+".2");
			File old3 = new File(filename+".3");
			File old4 = new File(filename+".4");
			File old5 = new File(filename+".5");
			old5.delete();
			old4.renameTo(old5);
			old3.renameTo(old4);
			old2.renameTo(old3);
			old1.renameTo(old2);
			f.renameTo(old1);
		}
		
		
		mLoaded = true;
		
	}
	
	// les points de d�dommagement (pour le bug des items du shop qui se r�initialisaient) ont bien ete accord�s...
	public void onBugRewardSuccess()
	{
		BCKLog.d("ShopManager", "onBugRewardSuccess");
		mBugRewarded = true;
		save();
		ZActivity.handler.post( new Runnable() {public void run() { ZActivity.instance.showDialog(GameActivity.dlgShopBugReward); }});	
	}
	
	public void load(InputStream in)
	{
		ShopManagerLoader ldr = new ShopManagerLoader();
		ldr.load(in);	
		// incr�mentation de l'�tat du shop...
		mStateCounter++;
	}

}
