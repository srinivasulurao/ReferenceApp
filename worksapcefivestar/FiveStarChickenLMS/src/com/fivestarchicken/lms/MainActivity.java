package com.fivestarchicken.lms;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;

public class MainActivity extends ActionBarActivity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		} else {
			
			
			Intent i = new Intent();
			i.setClass(MainActivity.this, ActivitySplash.class);
			startActivity(i);
		}
	}

}
