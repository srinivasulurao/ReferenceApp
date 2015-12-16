package com.aotd.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.aotd.helpers.NetworkConnectionHelper;
import com.aotd.map.helper.LocationParser;
import com.aotd.model.LocationModel;
import com.aotd.utils.Utils;


public class LocationUpdateService extends Service{

	private final String TAG = LocationManager.class.getSimpleName();

	private LocationManager 		mLocationManager			=null;
	private CurrentLocationListener mCurrentLocationListener	=null;

	Context context;
	private double latitude,longitude,old_lat = 0.0, old_lon = 0.0;
	private boolean remainUpdating = true;

	@Override
	public void onCreate() {	
		super.onCreate();
		this.context = LocationUpdateService.this;
		new UpdateLocation().start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		remainUpdating = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class UpdateLocation extends Thread{

		@Override
		public void run() {
			super.run();
			try{
				while(remainUpdating){
					updateLocationHandler.sendEmptyMessage(0);
					//					sleep( 60000);
					sleep(180000);

				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void getCurrentLocation(Context context)	{

		//		System.out.println("The location update service  is called...............................");
		mLocationManager=(LocationManager)context.getSystemService(context.LOCATION_SERVICE);
		mCurrentLocationListener=new CurrentLocationListener();

		//		Log.e(TAG, LocationManager.NETWORK_PROVIDER+"");
		//		Log.e(TAG, mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)+"");


		if(NetworkConnectionHelper.checkNetworkAvailability(context))
		{
			if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			{
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,mCurrentLocationListener);
			}
			if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			{
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,mCurrentLocationListener);
			}
		}

	}

	class CurrentLocationListener implements LocationListener{
		//String url;
		@Override
		public void onLocationChanged(Location location) 
		{
			try{
				mLocationManager.removeUpdates(mCurrentLocationListener);
				if (location != null) {

					latitude	=	location.getLatitude();
					longitude	=	location.getLongitude();

					//latitude = 32.461833;
					//longitude = -96.080933;

					Utils.LATITUDE = latitude;
					Utils.LONGITUDE = longitude;

					Utils.LOCATION_FOUND = true;

					double dist = getDistance(old_lat, latitude, old_lon, longitude);
					Log.i("", "kkk distance is :"+ dist);
					//if(dist >= 3){

					old_lat = latitude;
					old_lon = longitude;
					new UpdateLocationAsync().execute(latitude,longitude);
					//	updateLocation(latitude,longitude);
					new LocationNameFetcherThread(latitude, longitude).start();
					//				}

					Log.d(TAG, latitude+"::"+longitude);
				}else{
					Log.i(TAG, "kkk location null....");
				}

			}catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, "kkk location not found...." + location);
			}
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

	public Handler updateLocationHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0){
				getCurrentLocation(context);
				getLocationStopThread();
			}
		}
	};


	Thread myThread;
	private void getLocationStopThread(){

		try {

			myThread = new Thread(new Runnable() {

				@Override
				public void run() {

					try {

						Thread.sleep(20000);
						mLocationManager.removeUpdates(mCurrentLocationListener);
						mainLocationHandler.sendEmptyMessage(0);


					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			myThread.start();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	public Handler mainLocationHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 0) {

				interruptThread();
			}
		}
	};


	private void interruptThread() {

		try {

			if (myThread != null)
				myThread.interrupt();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}	


	String sLat="", sLon="";

	public void updateLocation(Double latitude,Double longitude){

		try{

			String encodedUserId = URLEncoder.encode(Utils.USER_ID.trim(),"UTF-8");
			String encodedLat    = URLEncoder.encode(latitude+"","UTF-8");
			String encodedLong   = URLEncoder.encode(longitude+"","UTF-8");

			

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf_ = new SimpleDateFormat("hh:mm:ss");

			String date= sdf.format(new Date());
			String time= sdf_.format(new Date());

			
				sLat = encodedLat;
				sLon = encodedLong;
				
				URL url = new  URL(String.format(Utils.DRIVER_GEOLOCATION_UPDATE_URL,encodedUserId ,encodedLat,encodedLong,date,time));
				Log.e("url *****************************","kkk url"+url+"");
				url.openConnection().getInputStream();
			




			URL urlOne = new  URL(String.format(Utils.DRIVER_UPDATE_USER_LOCATION,encodedUserId ,encodedLat,encodedLong));
			Log.e("urlOne *****************************","kkk urlOne"+urlOne+"");
			urlOne.openConnection().getInputStream();



		}catch (MalformedURLException e) {

			e.printStackTrace();
		}catch (IOException e) {

			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getDistance(double lat1, double lat2, double lon1,
			double lon2) {

		Location locationA = new Location("point A");

		locationA.setLatitude(lat1);
		locationA.setLongitude(lon1);

		Location locationB = new Location("point B");

		locationB.setLatitude(lat2);
		locationB.setLongitude(lon2);

		double distance = (double) ((locationA.distanceTo(locationB))*0.00062137);
		return distance;
	}






	private double mLatitude,mLongitude;

	class LocationNameFetcherThread extends Thread{

		String url;
		public LocationNameFetcherThread(double latitude,double longitude)
		{
			mLatitude	=	latitude;
			mLongitude	=	longitude;
			url = String.format("http://maps.googleapis.com/maps/api/geocode/xml?latlng=%s,%s&sensor=true",mLatitude,mLongitude);
			Log.e(TAG, url);
		}
		@Override
		public void run() {
			super.run();
			new LocationParser(url, mReciverHandler ).parse();
		}

	}


	private LocationReciverHandler mReciverHandler = new LocationReciverHandler();
	private LocationModel			mLocationModel;

	class LocationReciverHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			try {

				if(msg.what == 0)
				{

					mLocationModel = (LocationModel) msg.getData().getSerializable("locationresult");

					Log.e(TAG, mLocationModel.result.get(0).address_component.size()+"");
					Utils.ADDRESS =  mLocationModel.result.get(0).getFormatted_address();
					for(int i=0;i<mLocationModel.result.get(0).address_component.size();i++){
						if(mLocationModel.result.get(0).address_component.get(i).type.get(0).equalsIgnoreCase("locality"))
							Utils.CITY= mLocationModel.result.get(0).address_component.get(i).getLongName();

					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			}

		}
	}

	private class  UpdateLocationAsync extends AsyncTask<Double, Void, Void>{

		@Override
		protected Void doInBackground(Double... params) {
			updateLocation(params[0],params[1]);
			return null;
		}

	}
}
