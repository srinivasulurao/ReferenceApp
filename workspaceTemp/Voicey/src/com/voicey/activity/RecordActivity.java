package com.voicey.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class RecordActivity extends Activity {
	
	private ActionBar mActionBar;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.record_page);
		
		initilizeUI();
	}
	
	@SuppressLint("NewApi")
	private void initilizeUI()
	{
	     mActionBar = getActionBar();
	     
	    // mActionBar.setTitle("Favourite Clients");
	    
}
}
