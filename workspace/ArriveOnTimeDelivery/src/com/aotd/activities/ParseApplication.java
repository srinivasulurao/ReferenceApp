package com.aotd.activities;

import android.app.Activity;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.parse.PushService;

public class ParseApplication extends Application {

	Activity activity;

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "AhW8fR8L7hu3rSybkTXpHlHk93L2xPlpfRXTIqp3",
				"UBbMzdeYH7ThTQnq6eXra4izz13Wwqk7iAPEO8sH");// YOUR_APPLICATION_ID,YOUR_CLIENT_KEY
//698925975930
		ParseUser.enableAutomaticUser();

		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
		PushService.setDefaultPushCallback(getApplicationContext(),
				ParseStarterProjectActivity.class);

	}
	// PushService.setDefaultPushCallback(this, ParseApplication.class);

}
//AIzaSyAmhxI_YRtOVp_GrrigU9pEhyfICPtjGfo