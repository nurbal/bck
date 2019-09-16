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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.zerracsoft.Lib3D.ZActivity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;



public class TutorialDialog extends Dialog implements android.view.View.OnClickListener{

	public static final String TUTO_BRIDGE_BARS = "bridge_bars";
	public static final String TUTO_ZOOM = "zoom";
	public static final String TUTO_PAN = "pan";
	public static final String TUTO_MOVE_NODE = "move_node";
	public static final String TUTO_CONTEXT_MENU = "context_menu";
	public static final String TUTO_UNDO_REDO = "undo_redo";
	public static final String TUTO_TOWER = "tower";
	public static final String TUTO_BOAT_JACK = "boat_jack";
	public static final String TUTO_CABLES = "cables";
	public static final String TUTO_BUDGET = "budget";
	
	private static Object mSyncObject = new Object();
	
	public TutorialDialog(Context context) 
	{
		super(context);
		instance = this;
	}
	

	// fenetre de tuto
	static public TutorialDialog instance;
	static private int tutoTextID,tutoTitleID;
	static private boolean mActive = false;
	static public boolean isActive()
	{
		synchronized(mSyncObject)
		{
			return mActive;
		}
	}
	
	private static class TutoImage
	{
		public int mImageID;
		public int mDelay;	// time to display image in ms
		public TutoImage(int imageID,int delay) {mImageID = imageID; mDelay=delay;}
	}
	static private ArrayList<TutoImage> mTutoImages = new ArrayList<TutoImage>(5);
	static private int mNextImage;
	static private Timer mImageTimer = null;
	
	static void DisplayImage(int index)
	{
		if (index>=mTutoImages.size())
		{
			BCKLog.e("TutorialDialog", "image index overflow");
			return;
		}
		ImageView image = (ImageView) instance.findViewById(R.id.image);
		image.setImageResource(mTutoImages.get(index).mImageID);
		mNextImage = (index+1)%mTutoImages.size();
		
		if (mImageTimer==null) 						
			mImageTimer=new Timer();
		
		mImageTimer.schedule(new TimerTask() { public void run() { 
			GameActivity.handler.post( new Runnable() { public void run() { DisplayImage(mNextImage); } } );
								} }, 
							mTutoImages.get(index).mDelay);
	}
	
//	public static 
	
	public static void showTutorial(String tutoID) 
	{
		synchronized(mSyncObject) // pour proteger la variable isActive
		{
			if (mActive)
			{
				BCKLog.e("TutorialDialog","DiaBuilderLog already active, cannot show "+tutoID);
				return;
			}
			mActive = true;
		}
		
		if (tutoID.equalsIgnoreCase(TUTO_BRIDGE_BARS))
		{
			tutoTextID = R.string.tuto_bridge_bars_text; 
			tutoTitleID = R.string.tuto_bridge_bars_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_1,2000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_2,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_3,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_4,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_5,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_6,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_7,1000));	
			mTutoImages.add(new TutoImage(R.drawable.tuto_bars_8,4000));	
		}
		else if (tutoID.equalsIgnoreCase(TUTO_MOVE_NODE))
		{
			tutoTextID = R.string.tuto_move_node_text; 
			tutoTitleID = R.string.tuto_move_node_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node7,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_move_node8,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_CONTEXT_MENU))
		{
			tutoTextID = R.string.tuto_contextual_menu_text; 
			tutoTitleID = R.string.tuto_contextual_menu_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup7,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_popup8,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_UNDO_REDO))
		{
			tutoTextID = R.string.tuto_undo_text; 
			tutoTitleID = R.string.tuto_undo_title;
			mTutoImages.clear();
			//mTutoImages.add(new TutoImage(R.drawable.tuto_undo1,1000));
			//mTutoImages.add(new TutoImage(R.drawable.tuto_undo2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo7,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo8,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo9,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo10,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo11,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo12,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo13,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo14,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo15,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo16,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_undo17,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_BOAT_JACK))
		{
			tutoTextID = R.string.tuto_boat_jack_text; 
			tutoTitleID = R.string.tuto_boat_jack_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks2,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks7,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks8,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks9,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks10,3000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks11,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_jacks12,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_TOWER))
		{
			tutoTextID = R.string.tuto_tower_text; 
			tutoTitleID = R.string.tuto_tower_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_tower1,3000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_tower2,3000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_tower3,3000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_CABLES))
		{
			tutoTextID = R.string.tuto_cables_text;
			tutoTitleID = R.string.tuto_cables_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_cables1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_cables2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_cables3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_cables4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_cables5,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_BUDGET))
		{
			tutoTextID = R.string.tuto_budget_text; 
			tutoTitleID = R.string.tuto_budget_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_budget,1500));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_ZOOM))
		{
			tutoTextID = R.string.tuto_zoom_text; 
			tutoTitleID = R.string.tuto_zoom_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_zoom7,4000));
		}
		else if (tutoID.equalsIgnoreCase(TUTO_PAN))
		{
			tutoTextID = R.string.tuto_pan_text; 
			tutoTitleID = R.string.tuto_pan_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan1,2000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan2,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan3,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan4,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan5,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan6,1000));
			mTutoImages.add(new TutoImage(R.drawable.tuto_pan7,1000));
		}
		else
		{
			// default: debug dialog
			tutoTextID = R.string.tutoDbg_text; 
			tutoTitleID = R.string.tutoDbg_title;
			mTutoImages.clear();
			mTutoImages.add(new TutoImage(R.drawable.stone,1000));
			mTutoImages.add(new TutoImage(R.drawable.mud,1000));
			mTutoImages.add(new TutoImage(R.drawable.bcklogo,1000));
			mTutoImages.add(new TutoImage(R.drawable.mud,250));
			mTutoImages.add(new TutoImage(R.drawable.zerraclogo,250));
		}
		
		GameActivity.handler.post(
				new Runnable()
				{
					public void run()
					{
						TutorialDialog diaBuilderLog = new TutorialDialog(ZActivity.instance);

						diaBuilderLog.setContentView(R.layout.tuto_dialog);
						diaBuilderLog.setTitle(ZActivity.instance.getResources().getString(tutoTitleID));

						TextView text = (TextView) diaBuilderLog.findViewById(R.id.text);
						text.setText(ZActivity.instance.getResources().getString(tutoTextID));
						
						if (mImageTimer!=null)
						{
							mImageTimer.cancel();
							mImageTimer=null;
						}
						
						DisplayImage(0);
						
						
						Button btn = (Button) diaBuilderLog.findViewById(R.id.button1);
						btn.setOnClickListener(diaBuilderLog);

						// fix status bar farceuse sur milestone
						diaBuilderLog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
						
						WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					    lp.copyFrom(diaBuilderLog.getWindow().getAttributes());
					    lp.width = WindowManager.LayoutParams.FILL_PARENT;
					    //lp.height = WindowManager.LayoutParams.FILL_PARENT;
					    diaBuilderLog.show();
					    diaBuilderLog.getWindow().setAttributes(lp);
					
						
						
					}
				});
		
	}
	@Override
	public void onClick(View v) 
	{
		dismiss();
		synchronized(mSyncObject)
		{
			mActive=false;
		}
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		BCKLog.d("TutorialDialog","onStop");
		if (mImageTimer!=null)
		{
			mImageTimer.cancel();
			mImageTimer=null;
		}
		
		synchronized(mSyncObject)
		{
			mActive=false;
		}
		
	}
	
}
