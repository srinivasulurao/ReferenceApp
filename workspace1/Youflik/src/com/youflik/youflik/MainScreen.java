package com.youflik.youflik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreen extends ActionBarActivity{
	private Button join,signup;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainscreen);
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		join =(Button)findViewById(R.id.join);
		signup=(Button)findViewById(R.id.signin);
		
		join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent join = new Intent(MainScreen.this,JoinActivity.class);
				startActivity(join);
				//finish();
			}
		});
		
		signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent login = new Intent(MainScreen.this,SigninActivity.class);
				startActivity(login);
				//finish();
			}
		});
	}
}