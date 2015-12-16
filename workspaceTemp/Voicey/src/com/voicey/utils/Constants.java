package com.voicey.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Constants {
	
	public static String app_folder = "Voicey";
	
	public static String total_audio_time="6";
	
	public static String temp_url=Environment.getExternalStorageDirectory() + "/"+ Constants.app_folder + "/" + "temp" + ".3gp";
	
	public static String image_folder=Environment.getExternalStorageDirectory() + "/"+ Constants.app_folder;
	
	public static String audio_url="http://voicey.me/web-services/audio/";
	
	public static String image_url="http://voicey.me/web-services/images/";
	
	//public static String notifican_mail="info@voicey.me";
	public static String notifican_mail="info@voicey.me";
	
	
	public static String notifican_mail_subject="REPORT ABUSE";
	
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
				Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}
	

}
