package com.example.fivestarchickenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ActivityHome extends Activity {
	
 RelativeLayout rrprofile,rrtakeexample,rrviewresule;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_home);
	    
	    rrprofile=(RelativeLayout)findViewById(R.id.rrprofile);
	    rrtakeexample=(RelativeLayout)findViewById(R.id.rrtakeexample);
	    rrviewresule=(RelativeLayout)findViewById(R.id.rrviewresule);
	    
	    rrtakeexample.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Intent i=new Intent();
			      i.setClass(ActivityHome.this, ActivityExamCategory.class);
			      startActivity(i);
			    
				
			}
		});
	    
	    rrprofile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Intent i=new Intent();
			      i.setClass(ActivityHome.this, ActivityProfile.class);
			      startActivity(i);
			    
				
			}
		});
	    
	    rrviewresule.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Intent i=new Intent();
			      i.setClass(ActivityHome.this, ActivityViewResult.class);
			      startActivity(i);
			    
				
			}
		});
	    
	    

}
}
