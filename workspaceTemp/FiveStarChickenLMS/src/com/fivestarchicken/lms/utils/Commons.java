package com.fivestarchicken.lms.utils;

import com.fivestarchicken.lms.ActivityLogin;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class Commons {
	
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void goToSkypeMarket(Context myContext) {
		Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
		Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		myContext.startActivity(myIntent);
		return;
	}
	
	public static boolean isSkypeClientInstalled(Context myContext) {
		PackageManager myPackageMgr = myContext.getPackageManager();
		try {
			myPackageMgr.getPackageInfo("com.skype.raider",
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			return (false);
		}
		return (true);
	}
	
	public static void internetErrorMessage(Context ctx){
		
		
		Toast.makeText(ctx,"Error In Internet Connection",
				Toast.LENGTH_LONG).show();
	}
	public static String statusPass="PASS";
	public static String statusFail="FAIL";
	public static String statusProcessing="Processing";
	public static String resultModule="ResultModule";
	public static String profileModule="profileModule";
	public static String genralModule="genralModule";
	
	public static String appFolder="Five Star";
	public static String imageFolder="images";
	public static String videoFolder="videos";
	public static String pdfFolder="PDF";
	
	
	public static String imagePath="http://taskdynamo.com/lmsstage/userprofile/";
	public static String app_image_path="http://taskdynamo.com/lmsstage/module-media/image";
	public static String app_video_path="http://taskdynamo.com/lmsstage/module-media/video";
	public static String app_certificate_path="http://taskdynamo.com/lmsstage/pdf/";
	public static String app_pdf_path="http://taskdynamo.com/lmsstage/module-media/file/";
	
	public static String skype_name="SKYPE_ID";
	public static String admin_email="ADMIN_EMAIL";
	public static String admin_phone="ADMIN_PHONE";
	public static String image_folder=Environment.getExternalStorageDirectory() + "/"+ appFolder;
	
	public static String app_image_folder=Environment.getExternalStorageDirectory() + "/"+ appFolder+ "/"+ imageFolder;
	public static String app_video_folder=Environment.getExternalStorageDirectory() + "/"+ appFolder+ "/"+ videoFolder;
	public static String app_pdf_folder=Environment.getExternalStorageDirectory() + "/"+ appFolder+ "/"+ pdfFolder+ "/";
	
	public static String TAKE_EXAM="takeexam";
	public static String DEFAULT_SELECT="default";
	public static String PROFILE="profile";
	public static String VIEW_RESULT="viewresult";
	public static String INTERVIEW_MODULEID="-1";
	
	public static String MODULE_FAIL_LIMIT="module_fail_limit";
	public static String STAR_PASS_LIMT="star_pass_limit";
	
	public static int LINE_IMAGE=10;
	public static Integer MAX_STAR=5;
	public static String DEFAULT_LANGUAGE_TYPE="1";
	
	public static String TYPE_IMAGE="image";
	public static String TYPE_TEXT="text";
	public static String TYPE_VIDEO="video";
	public static String TYPE_PDF="document";
	
	public static String SYNC_TYPE_RESTORE="restore";
	public static String SYNC_TYPE_UPDATE="update";
	
	public static String Example_Text="Five Star Chicken comes from Thai Multi-National Conglomerate, CP Foods with over $13billion sales in Agro and Food Industry, "
			+ "globally. Its products are sold in over 50 countries worldwide. CP Foods is a pioneer in this industry and is also known to be the world’s largest "
			+ "feed manufacturing company,as well as the largest shrimp/prawn manufacturing company in the world. In terms of poultry (chicken), it is one of the top"
			+ " 5 companies in the world.";

}
