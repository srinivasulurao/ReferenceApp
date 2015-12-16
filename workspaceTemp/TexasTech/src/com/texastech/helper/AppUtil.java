package com.texastech.helper;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class AppUtil {

	public static DisplayImageOptions getImageSetting(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        //.showImageOnFail(defalutImage)
        .build();
		return options;
	}
	
	
	public static String getAddress(Context context,double lat, double lng) {
		MLog.v("Lat& Long", lat+":"+lng);
		String strAddress="NA";
	    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
	    try {
	        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
	        
	        Address obj = addresses.get(0);
	        if(obj.getLocality()!=null){
	        	strAddress = obj.getLocality()+",";
	        }
	        
	        if(obj.getCountryName()!=null){
	        	if(strAddress.equals("NA")){
	        		strAddress = obj.getCountryName();
	        	}else{
	        		strAddress = strAddress + obj.getCountryName();
	        	}
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    Log.v("Address", ""+ strAddress);
	    return strAddress;
	}
}	 
