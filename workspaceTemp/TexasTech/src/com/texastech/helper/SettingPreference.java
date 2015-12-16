package com.texastech.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingPreference {

	private SharedPreferences preferences;
	private Editor editor;
	
	
	public SettingPreference(Context activity) {
		preferences = activity.getSharedPreferences("SETTINGS", 0);
		editor = preferences.edit();
	}

	public void clearPreference() {
		editor.clear();
		editor.commit();
	}
	
	//------------------------//
	/*public void setToken(String _token){
		editor.putString("token", _token);
		editor.commit();
	}
	
	public String getToken() {
		return preferences.getString("token", "");
	}*/
	
	
	//------------------------//
	public void setLogoUrl(String _logoUrl){
		editor.putString("logoUrl", _logoUrl);
		editor.commit();
	}
	
	public String getLogoUrl() {
		return preferences.getString("logoUrl", "");
	}
	
	
	//------------------------//
	public void setBackground1Url(String _background1){
		editor.putString("background1", _background1);
		editor.commit();
	}
	
	public String getBackground1Url() {
		return preferences.getString("background1", "");
	}
	
	//------------------------//
	public void setBackground2Url(String _background1){
		editor.putString("background2", _background1);
		editor.commit();
	}
	
	public String getBackground2Url() {
		return preferences.getString("background2", "");
	}
	
	//------------------------//
	public void setBackButtonUrl(String _backButtonUrl){
		editor.putString("backButtonUrl", _backButtonUrl);
		editor.commit();
	}
	
	public String getBackButtonUrl() {
		return preferences.getString("backButtonUrl", "");
	}
	
	
	//------------------------//
	public void setTitleBarUrl(String _titlebar){
		editor.putString("titlebar", _titlebar);
		editor.commit();
	}
	
	public String getTitleBarUrl() {
		return preferences.getString("titlebar", "");
	}
	
	
	//------------------------//
	public void setTitleColor(String _titleColor){
		editor.putString("titlebartextcolor", "#ffffff");
		editor.commit();
	}
	
	public String getTitleColor() {
		return preferences.getString("titlebartextcolor", "");
	}
	
	
	//------------------------//
	public void setRegistrationId(String _id){
		editor.putString("registration_id", _id);
		editor.commit();
	}
	
	public String getRegistrationId() {
		return preferences.getString("registration_id", "");
	}
	
	
	public void setGUID(String _guid){
		editor.putString("GUID", _guid);
		editor.commit();
	}
	
	public String getGUID() {
		return preferences.getString("GUID", "");
	}
	
	
	public void setUserName(String _userName){
		editor.putString("USERNAME", _userName);
		editor.commit();
	}
	
	public String getUserName() {
		return preferences.getString("USERNAME", "");
	}
	
	public void setPassword(String _userName){
		editor.putString("Password", _userName);
		editor.commit();
	}
	
	public String getPassword() {
		return preferences.getString("Password", "");
	}
}

//deviceResolution 5- 600x1024
//deviceResolution 1- 320x480
//deviceResolution 2- 480x800
