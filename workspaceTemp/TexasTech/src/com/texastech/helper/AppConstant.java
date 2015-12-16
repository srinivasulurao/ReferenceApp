package com.texastech.helper;

import com.google.android.gms.maps.model.LatLng;
import com.texastech.bean.GetLoginInfo.LoginInfo;
/**
 * Class containing String message , which are accessible throughout the application.
 */
public class AppConstant {
	
	public static boolean isUserLogout = false;
	
	public static final String LOW_MEMORY_MSG = "Your device is running out of memory. Please close some of your running Apps to prevent other Apps from crashing.";

	public static String NETWORK_ERROR = "An Internet Connection not detected. Please check your internet connection settings and Try Again";

	public static String LOCATION_NOT_FOUND = "Failed to Capture your location. Please cancel and make sure to turn on GPS on your device. Click OK to save anyway";
	
	public static final LatLng defaultLatLon=new LatLng(0.0, 0.0);
	
	public static LoginInfo loginUser=null;
	 
}
