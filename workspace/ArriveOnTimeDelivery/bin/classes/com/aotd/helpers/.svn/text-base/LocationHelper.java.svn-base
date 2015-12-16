package com.aotd.helpers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import com.aotd.model.LocationModel;
import com.aotd.parsers.CurrentLocationParser;
import com.aotd.utils.CurrentLocation;


import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;



public class LocationHelper 
{
	private final String TAG = LocationManager.class.getSimpleName();
	public  final String GOOGLE_GEOCODING_API = "http://maps.googleapis.com/maps/api/geocode/xml?latlng=%s,%s&sensor=true";

	private LocationManager 		mLocationManager			=null;
	private CurrentLocationListener mCurrentLocationListener	=null;
	private CurrentLocation 		mCurrentLocation;
	private Geocoder 				mGeocoder					=null;
	private String 					currentCityName				="";		
	private String 					currentState				="";
	private String 					currentCountry				="";
	private String					addressLine					="";
	private String 					currentPlaceZipCode 		="";
	private LocationModel			mLocationModel;
	private locationReciverHandler  handler=new locationReciverHandler();
	private static Context 				context;
	private double latitude=0,longitude=0;
	private locationReciverHandler mReciverHandler = new locationReciverHandler();
	private final int SUCCESS = 1;
	private final int ERROR	 = 0;

	// 9966283672
	
	public LocationHelper(Context context)
	{
		this.context=context;
		mLocationManager=(LocationManager)context.getSystemService(context.LOCATION_SERVICE);
		mCurrentLocationListener=new CurrentLocationListener();
		mCurrentLocation=(CurrentLocation) context;
		mGeocoder=new Geocoder(context);
		
		Log.e(TAG, LocationManager.NETWORK_PROVIDER+"");
		Log.e(TAG, mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)+"");

		
		if(NetworkConnectionHelper.checkNetworkAvailability(this.context))
		{
			if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			{
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,mCurrentLocationListener);
			}
			if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,mCurrentLocationListener);
			}
		}else{
			Log.e(TAG, "internet connection is not available");
			mCurrentLocation.getCurrentLocation(currentCityName, addressLine, currentState, currentCountry, longitude, latitude,currentPlaceZipCode);

		}
		
	}
	
	class CurrentLocationListener implements LocationListener{
		
		@Override
		public void onLocationChanged(Location location) 
		{
			mLocationManager.removeUpdates(this);
			latitude	=	location.getLatitude();
			longitude	=	location.getLongitude();			
			
			new LocationNameFetcherThread(latitude, longitude).start();
			Log.d(TAG, latitude+"::"+longitude);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private double mLatitude,mLongitude;
	
	public class LocationNameFetcherThread extends Thread{
		
		String url;
		public LocationNameFetcherThread(double latitude,double longitude)
		{
			mLatitude	=	latitude;
			mLongitude	=	longitude;
			url = String.format(GOOGLE_GEOCODING_API,mLatitude,mLongitude);
			Log.e(TAG, url);
		}
		@Override
		public void run() {
			super.run();
			new CurrentLocationParser(url, mReciverHandler ).start();
		}
		
	}
	
	class locationReciverHandler extends Handler
	{
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == SUCCESS)
			{
				mLocationModel = (LocationModel) msg.getData().getSerializable("locationresult");
				
				Log.e(TAG, mLocationModel.result.get(0).address_component.size()+"");
				addressLine =  mLocationModel.result.get(0).getFormatted_address();
				for(int i=0;i<mLocationModel.result.get(0).address_component.size();i++){
					if(mLocationModel.result.get(0).address_component.get(i).type.get(0).equalsIgnoreCase("locality"))
						currentCityName= mLocationModel.result.get(0).address_component.get(i).getLongName();
					else if(mLocationModel.result.get(0).address_component.get(i).type.get(0).equalsIgnoreCase("administrative_area_level_1"))
						currentState =  mLocationModel.result.get(0).address_component.get(i).getLongName();
					else if(mLocationModel.result.get(0).address_component.get(i).type.get(0).equalsIgnoreCase("country"))
						currentCountry = mLocationModel.result.get(0).address_component.get(i).getLongName();
					else if(mLocationModel.result.get(0).address_component.get(i).type.get(0).equalsIgnoreCase("postal_code"))
						currentPlaceZipCode = mLocationModel.result.get(0).address_component.get(i).getLongName();
				}
				
				Log.e(TAG, "city "+currentCityName+" state "+currentState+" country "+currentCountry);
				mCurrentLocation.getCurrentLocation(currentCityName, addressLine, currentState, currentCountry, longitude, latitude,currentPlaceZipCode);
			}else{
				mCurrentLocation.getCurrentLocation(currentCityName, addressLine, currentState, currentCountry, longitude, latitude,currentPlaceZipCode);
			}
			
		}
		
		
	}
	

	
}
