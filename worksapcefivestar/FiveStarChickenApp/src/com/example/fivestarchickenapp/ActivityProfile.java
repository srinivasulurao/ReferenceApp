package com.example.fivestarchickenapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ActivityProfile  extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_profile);
	    
	    
	    
	}

}
