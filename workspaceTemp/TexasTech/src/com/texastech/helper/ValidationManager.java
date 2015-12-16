package com.texastech.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

/**
 * Utility class for helper methods
 */
public class ValidationManager { 
	
	
	/** Value - {@value}, EMAIL_ADDRESS_PATTERN for storing the email string format.*/ 
	public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	
	
	
	
	
	/**
	 * Returns an boolean object that identify whether given string is valid email format or not. 
	 * <br>
	 * @param  email :	a string  
	 * @return boolean     
	 */
	public static boolean isValidEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	
	/**
	 * Function for check the network connectivity
	 * Check whether your device Internet is enable or not.  
	 * returns true if connected otherwise false.
	 * <br>
	 * @param  context 	:	application context  
	 * @return boolean   
	 */
	public static boolean isInternetConnected(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	} 
	
	
	/**
	 * Check whether your device has external storage with read write permission.
	 * returns true if present otherwise false.
	 * @return boolean	:	
	 */
    public static boolean isExternalStorage(){
    	String state = android.os.Environment.getExternalStorageState();
    	boolean sdcard_avail =  state.equals(android.os.Environment.MEDIA_MOUNTED);
    	boolean sdcard_readonly =  state.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY);

            if ( ! sdcard_avail || sdcard_readonly ){
            	return false;
            }
            return true;
    }
    
    
    
    
    
    
    /**
     * 
     * Ex - isThisDateValid("30/04/2012", "dd/MM/yyyy")
     * */
    public boolean isDateValid(String dateToValidate, String dateFromat){
		if(dateToValidate == null){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
		try {
			//if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    
   /**
    * 
    * @param number
    * @return
    */
    public static boolean isValidMobile(String number){
         Pattern pattern = Pattern.compile("\\d{10}");
         Matcher matcher = pattern.matcher(number);
         if (matcher.matches()) {
       	  System.out.println("Phone Number Valid");
       	  	return true;
         }
         return false; 
    }
    
    
    
    /**
     * 
     * @param number
     * @return
     */
    public Boolean InValidWebsite(String number){
    	boolean isValid = false;  
    	String expression = "^http(s{0,1})://[a-zA-Z0-9-#$&_/\\-]+[\\.]{1}([A-Za-z]{2,5})+[a-zA-Z0-9_/\\-\\.]*";
    			//"^http(s{0,1})://[a-zA-Z0-9/\\-]+[\\.]{1}([A-Za-z.]{2,5})+/[a-zA-Z0-9/\\-\\.]*";
    	CharSequence inputStr = number;  
    	Pattern pattern = Pattern.compile(expression);  
    	Matcher matcher = pattern.matcher(inputStr);  
    	if(matcher.matches())
    	{  
    		isValid = true;  
    	}  
    	return isValid;  
    }
    
    /**
     * 
     * @param number
     * @return
     */
    public static boolean isNumeric(String number){  
    	boolean isValid = false;  
    	String expression = "^[0-9a-zA-Z][a-zA-Z0-9-_.\\s]*";  
    	CharSequence inputStr = number;  
    	Pattern pattern = Pattern.compile(expression);  
    	Matcher matcher = pattern.matcher(inputStr);  
    	if(matcher.matches())
    	{  
    		isValid = true;  
    	}  
    	return isValid;  
    } 
    
    
    /**
	 * Check whether your application is running first time or not.
	 * @return boolean {return's true if application running first time otherwise false}
	 */
    public static final int MODE_PRIVATE = 0;
	public static boolean isAppRunningFirstTime(Context con){
		SharedPreferences pref = null;
		pref = con.getSharedPreferences("RunningState", MODE_PRIVATE);
		boolean isRunningFirstTime = pref.getBoolean("KEY", true);
		if(isRunningFirstTime){
    		return true;
    	}
    	else
    		return false;
	}
	
	
	public static boolean isApplicationBroughtToBackground(Context context) {
	    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	        ComponentName topActivity = tasks.get(0).topActivity;
	        if (!topActivity.getPackageName().equals(context.getPackageName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	public static boolean isFlashSupport(Context context){
		// First check if device is supporting flashlight or not        
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	public static boolean hasCamara(Context context){
		//Your device doesn't have camera!
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA); 
	}
	
	
	public static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}