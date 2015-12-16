package com.youflik.youflik.utils;


import java.util.HashMap;

import com.youflik.youflik.MainActivity;
import com.youflik.youflik.MainScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences

	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "youfliklogin";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	public static final String WHICH_LOGIN = "loggedInWith";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "username";

	public static final String KEY_PASSWORD = "password";

	public static final String OAUTH_SOCIAL = "facebookFb";

	public static final String OAUTH_USER_ID_SOCIAL = "facebookUserIdFb";

	public static final String KEY_CHAT_PASSWORD = "chatpassword";

	public static final String KEY_USER_ID = "userid";

	public static final String KEY_USER_EMAIL = "email";

	public static final String KEY_CSFR_TOKEN = "csrf_token";


	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String name, String password,String userid,String useremail,
			String token,String chatpassword,String loggedInWith,String socialName, String SocialUserID){

		// Storing login value as TRUE


		editor.putBoolean(IS_LOGIN, true);

		editor.putString(KEY_NAME, name);

		editor.putString(KEY_PASSWORD, password);

		editor.putString(KEY_USER_ID, userid);

		editor.putString(KEY_USER_EMAIL, useremail);

		editor.putString(KEY_CSFR_TOKEN, token);

		editor.putString(KEY_CHAT_PASSWORD, chatpassword);

		editor.putString(WHICH_LOGIN, loggedInWith);

		editor.putString(OAUTH_SOCIAL, socialName);

		editor.putString(OAUTH_USER_ID_SOCIAL, SocialUserID);

		// commit changes
		editor.commit();
	}   

	public void changePassword(String password){

		editor.putString(KEY_PASSWORD, password);
		// commit changes
		editor.commit();

	}

	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, MainScreen.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		}
		else{
			Intent i = new Intent(_context, MainActivity.class);
			_context.startActivity(i);
		}

	}



	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		// user email id
		user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

		user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));

		user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));

		user.put(KEY_CSFR_TOKEN, pref.getString(KEY_CSFR_TOKEN, null));

		user.put(KEY_CHAT_PASSWORD, pref.getString(KEY_CHAT_PASSWORD, null));

		user.put(WHICH_LOGIN, pref.getString(WHICH_LOGIN, null));

		user.put(OAUTH_SOCIAL, pref.getString(OAUTH_SOCIAL, null));

		user.put(OAUTH_USER_ID_SOCIAL, pref.getString(OAUTH_USER_ID_SOCIAL, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, MainScreen.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}