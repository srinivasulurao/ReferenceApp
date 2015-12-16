package com.example.fivestarchickenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ActivityLogin extends Activity {
	
	Button btLogin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_login);
	    
	    btLogin=(Button)findViewById(R.id.btlogin);
	    
	    btLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 Intent i=new Intent();
			      i.setClass(ActivityLogin.this, ActivityHome.class);
			      startActivity(i);
			    
				
				
			}
		});
	    

    }
	
public void onBackPressed() {
		
		
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
		  }

	


}
