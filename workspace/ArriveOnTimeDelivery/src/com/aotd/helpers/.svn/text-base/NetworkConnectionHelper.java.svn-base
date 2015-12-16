package com.aotd.helpers;


import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkConnectionHelper {

	static ConnectivityManager CONNECTIVITY_MANAGER;
	
	public static boolean checkNetworkAvailability(Context context)
	{
		CONNECTIVITY_MANAGER=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(CONNECTIVITY_MANAGER.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()
				||CONNECTIVITY_MANAGER.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected())
			return true;
		return false;
	}
}
