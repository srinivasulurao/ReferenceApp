package com.example.fivestarchickenapp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		} else {
        
      Intent i=new Intent();
      i.setClass(MainActivity.this, ActivityLogin.class);
      startActivity(i);
		}
    
    
    }


  
}
