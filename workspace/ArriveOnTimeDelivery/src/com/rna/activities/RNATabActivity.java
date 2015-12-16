package com.rna.activities;

import com.aotd.activities.R;
import com.aotd.activities.R.id;
import com.aotd.activities.R.layout;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class RNATabActivity extends TabActivity 
{
	
	public static TabHost tabHost;  // The activity TabHost
	private TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	private Intent intent;
	private Intent receive_intent;
	
	public static ImageView imgOnline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		requestWindowFeature(Window.FEATURE_NO_TITLE);	           
		setContentView(R.layout.rna_tabview); 
		receive_intent = getIntent();
		
		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
		
		tabHost = getTabHost();  // The activity TabHost
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, RNADispatchPastTabActivity.class);
		
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("Past")
				.setIndicator("Past")
				.setContent(intent);        			  
		
		tabHost.addTab(spec);
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, RNADispatchPresentTabActivity.class);
		spec = tabHost.newTabSpec("Present")
				.setIndicator("Present")
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, RNADispatchFutureTabActivity.class);
		spec = tabHost.newTabSpec("Future")
				.setIndicator("Future")
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, RNADispatchTabActivity.class);
		spec = tabHost.newTabSpec("Despatch")
				.setIndicator("Dispatch")
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabTextAlignment();  
		
		if(receive_intent.getStringExtra("from").equalsIgnoreCase("aotd_present")){    	   
			tabHost.setCurrentTab(3);  
		}else if(receive_intent.getStringExtra("from").equalsIgnoreCase("rna_dispatch")||receive_intent.getStringExtra("from").equalsIgnoreCase("rna_sign")){    
			tabHost.setCurrentTab(1);  
		}
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) 
			{
				// TODO Auto-generated method stub
				Log.e("tabid", tabId);
			}
		});
	}
	
	public void tabTextAlignment()
	{
		
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height=40;
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(0).setPadding(0, 0, 0, 8);
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height=40;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(1).setPadding(0, 0, 0, 8);
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height=40;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(2).setPadding(0, 0, 0, 8);
		tabHost.getTabWidget().getChildAt(3).getLayoutParams().height=40;
		tabHost.getTabWidget().getChildAt(3).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(3).setPadding(0, 0, 0, 8);
		
	}
}   