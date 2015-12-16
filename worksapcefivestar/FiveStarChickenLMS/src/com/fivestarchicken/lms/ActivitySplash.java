package com.fivestarchicken.lms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class ActivitySplash extends Activity {
	private static long SLEEP_TIME = 1;
	
	SharedPreferences sharedPreferences;
	String managerId, userId;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.splash_screen);

			IntentLauncher launcher = new IntentLauncher();
			launcher.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class IntentLauncher extends Thread {
		@Override
		/**
		 * Sleep for some time and than start new activity.
		 */
		public void run() {
			try {
				// Sleeping
				Thread.sleep(SLEEP_TIME * 4000);
			} catch (Exception e) {

			}



			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(ActivitySplash.this);
			managerId = sharedPreferences.getString("managerId", null);
			if (managerId == null) {

				Intent i = new Intent();
				i.setClass(ActivitySplash.this, ActivityLogin.class);
				startActivity(i);
			} else {

				userId = sharedPreferences.getString("userId", null);

				if (userId == null) {
					
					Intent i = new Intent();
					i.setClass(ActivitySplash.this, ActivityEmployeeLogin.class);
					startActivity(i);

				} else {
					
					Intent i = new Intent();
					i.setClass(ActivitySplash.this, ActivityHome.class);
					startActivity(i);

				}

			}
		
		}
	}

}
