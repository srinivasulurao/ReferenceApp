package com.aotd.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DriverGeoLocationModel;



public class Utils {

	public static double LATITUDE = 0.0;
	public static double LONGITUDE = 0.0;
	public static String CITY = "";
	public static String ADDRESS = "";
	public static boolean LOCATION_FOUND = false;


	//public static final String DISPATCH_URL =BASE_URL+"getNewDispatches.php?roleName=%s&user_id=%s";

	public static boolean IS_OFFICE_DATA_UPDATED = false;

	public static ArrayList<DriverGeoLocationModel> mDriverGeoLocations = null;
	public static ArrayList<DispatchAllListModel> DriverOrdersList = null;


	public static String USER_ID = "";
	public static String isRoundTrip = "none";

	public static String ROLENAME = "";
	public static String USERNAME = "";
	public static boolean NEW_ORDER_VIEWED = false;

	public static String wifi = "WIFI";
	public static String mobile = "mobile";


	//	public static final String DOMAIN_NAME = "http://dev.aotdelivery.com/";	// Old Development
	//	public static final String DOMAIN_NAME = "http://www.aotdelivery.com/"; // Old Production

	//	public static final String DOMAIN_NAME = "http://50.62.78.204/d/";	// New Development	
	//	public static final String DOMAIN_NAME = "http://50.62.78.204/";	// New Production

	public static final String DOMAIN_NAME = "http://www.aotdelivery.com/";
	//"http://50.63.55.253/";	// Latest Production


	public static final String BASE_URL = DOMAIN_NAME + "Mobile/";
	public static final String LOGIN_URL = BASE_URL+"Login.php?user_id=%s&password=%s";

	public static final String OFFLINE_DISPATCH_URL 		=	BASE_URL+"getNewDispatches.php?roleName=%s&user_id=%s";
	public static final String OFFLINE_PICK_UP_UPDATE		=	BASE_URL+"updateOfflinePickup.php?roleName=%s&order_id=%s&dpDate=%s&pcDate=%s";
	public static final String OFFLINE_DELIVERY_UPDATE		=	BASE_URL+"updateOfflineDelivery.php?roleName=%s&order_id=%s&dpDate=%s&dlDate=%s" +
			"&puDate=%s&roundTrip=%s&waittime=%s&transportation=%s&boxes=%s";

	public static final String OFFLINE_ROUNDTRIP_DELIVERY	=	BASE_URL+"updateOfflineRoundTripDelivery.php?roleName=%s&order_id=%s&dpDate=%s&dlDate=%s" +
			"&puDate=%s&waittime=%s&transportation=%s&boxes=%s&signDelivery=%s";

	public static final String OFFLINE_SIGNATURE_DELIVERY	=	BASE_URL+"updateOfflineDeliverySignature.php?roleName=%s&order_id=%s";


	public static final String OFFLINE_RNA_PICKUP	 		=	BASE_URL+"updateRNAOrderPickupOffline.php?batchID=%s&puDate=%s";
	public static final String OFFLINE_RNA_ORDER_DELIVERY	=	BASE_URL+"updateRNAOrderDeliveryOffline.php?batchID=%s&puDate=%sdlDate=%s";


	public static final String DISPATCH_URL 				=	BASE_URL+"getNewOfflineDispatches.php?roleName=%s&user_id=%s";
	public static final String UPLOAD_ORDERS_URL 			=	BASE_URL+"updateNewDispatches.php";	
	public static final String DRIVER_SIGNATURE_PATH 		= 	DOMAIN_NAME+"upload/sign/";
	public static final String PRESENT_DISPATCH_URL 		=	BASE_URL+"getOrders.php?roleName=%s&user_id=%s&forDate=current";
	public static final String FUTURE_DISPATCH_URL 			=	BASE_URL+"getOrders.php?roleName=%s&user_id=%s&forDate=future";
	public static final String PAST_DISPATCH_URL 			=	BASE_URL+"getOrders.php?roleName=%s&user_id=%s&forDate=past";
	public static final String DISPATCH_DELIVERY_URL 		= 	BASE_URL+"getOrderDetails.php?roleName=%s&order_id=%s";
	public static final String UPLOAD_FIRSTSIGN_DATA 		=	BASE_URL+"updateOrderDelivery.php?roleName=%s&order_id=%s&isRoundTrip=%s&waittime=%s&transportation=%s&boxes=%s&roundTrip=%s";
	public static final String PICKUP_URL 					= 	BASE_URL+"updateOrderPickup.php";
	public static final String UPLOAD_SECONDSIGN_DATA 		= 	BASE_URL+"updateRoundTripOrderDelivery.php?roleName=%s&order_id=%s&isRoundTrip=%s&waittime=%s&transportation=%s&boxes=%s&roundTrip=%s";
	public static final String UPDATE_ROUNDTRIP_URL 		= 	BASE_URL+"updateRoundTripOrderPickup.php?roleName=%s&order_id=%s&isRoundTrip=%s&waittime=%s&transportation=%s&boxes=%s&roundTrip=%s";
	public static final String DRIVER_ORDERS 				= 	BASE_URL+"getDriverOrders.php?user_id=%s&page_no=%s";	
	public static final String DISPATCH_COUNT_URL	 		=	BASE_URL+"getCountNewDispatches.php?roleName=%s&user_id=%s";

	public static final String DRIVER_GEOLOCATION_URL 		= 	BASE_URL+"getDriverGeoLocations.php";	
	public static final String DRIVER_GEOLOCATION_UPDATE_URL= 	BASE_URL+"updateDriverGeoLocations.php?roleName=driver&user_id=%s&lat=%s&lon=%s&date=%s&time=%s";
	public static final String DRIVER_UPDATE_USER_LOCATION	= 	BASE_URL+"updateUserLocation.php?email=%s&lat=%s&lng=%s";

	public static final String UPDATE_NOTE_URL 				= 	BASE_URL+ "updateNotes.php?id=%s&note=%s";
	public static final String GET_NOTE_URL 				= 	BASE_URL+ "getNotes.php?id=%s";

	public static final String RNA_PICKUP_URL 				= BASE_URL+"insertRNAorderPickup.php";
	public static final String RNA_DISPATCH_PRESENT_URL 	= BASE_URL+"getRNAOrders.php?forDate=current";
	public static final String RNA_DISPATCH_PAST_URL 		= BASE_URL+"getRNAOrders.php?forDate=past";
	public static final String RNA_DISPATCH_FUTURE_URL 		= BASE_URL+"getRNAOrders.php?forDate=future";
	public static final String RNA_UPDATEORDER_PICKUP_URL 	= BASE_URL+"updateRNAOrderPickup.php?batchID=%s";
	public static final String RNA_DELIVERY_SIGN_URL 		= BASE_URL+"updateRNAOrderDelivery.php?batchID=%s";
	public static final String RNA_NEWORDERS_URL     		= BASE_URL+"getNewRNAOrders.php?batchID=%s&stationID=111&deviceName=MYDEVICENAME&driverName=SYSTEM";
	public static final String RNA_UPDATEORDER_URL 			= BASE_URL+"updateNewRNAOrders.php";

	//pdf download url
	public static final String PDF_DOWNLOAD_URL = BASE_URL+"downloadPDFSignature.php?orderNumber=%s";
	public static final String GET_LOCATION_NAME_URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%g,%g&sensor=false";

	public static final String RNA_AUTHENTICATION_URL 		= "http://67.198.16.34/RxManifest/eManiFest.svc/Drivers/Authentication/Rna1/%s/%s/";
	public static final String RNA_RXLIST_URL 				= "http://67.198.16.34/RxManifest/eManiFest.svc/Drivers/CheckOut/GetRxList/Rna1/111/SYSTEM/MYDEVICENAME/RPH/%s";
	public static final String RNA_RXCheckin_URL 			= "http://67.198.16.34/RxManifest/eManiFest.svc/Drivers/Checkin/RNA1/111/SYS/";

	public static final String PDF_SIGN_UPLOAD_URL          = BASE_URL+"uploadPDFSignature.php";
	public static final String IMAGE_URL         			= BASE_URL+"uploadOrderImage.php";

	//test server
	//public static final String RNA_AUTHENTICATION_URL = "http://216.138.115.213/RxManifest/eManiFest.svc/Drivers/Authentication/rna2/SYSTEM/ADMIN/";
	//public static final String RNA_RXLIST_URL = "http://216.138.115.213/RxManifest/eManiFest.svc/Drivers/CheckOut/GetRxList/rna2/111/SYSTEM/MYDEVICENAME/RPH/%s";
	//public static final String RNA_RXCheckin_URL = "http://216.138.115.213/RxManifest/eManiFest.svc/Drivers/Checkin/RNA2/111/SYS/";

	//live server


	public static boolean checkNetwork(Context context) {

		boolean isNetworkAvailable = true;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			isNetworkAvailable = true;
		} else {
			isNetworkAvailable  = false;
		}
		Log.e("", " isNetworkAvailable "+isNetworkAvailable);

		return isNetworkAvailable;

	}

	public static String NetworkType(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
		{
			if(activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
			{
				return Utils.wifi;
			}
			else
			{
				return Utils.mobile;
			}
		}
		else
		{
			return "";
		}
	}

	public static void wifiOFF(Context context)
	{
		WifiManager wifiManager ;
		wifiManager  = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);   
	}
	public static void wifiON(Context context)
	{
		WifiManager wifiManager ;
		wifiManager  = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);   
	}

	public static void mobileDataON(Context context)
	{
		try{
			ConnectivityManager dataManager;
			dataManager  = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
			Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(dataManager, true);

		}catch(Exception e){}
	}
	public static void mobileDataOFF(Context context)
	{
		try{
			ConnectivityManager dataManager;
			dataManager  = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
			Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(dataManager, false);

		}catch(Exception e){}
	}
	
	public static void switchOnInternet(final Context context)
	{
		WifiManager wifiManager ;
		wifiManager  = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);  
		
		try{
			ConnectivityManager dataManager;
			dataManager  = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
			Method dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(dataManager, true);

		}catch(Exception e){}
		
		

	}


}


