package com.youflik.youflik.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class LocationUtil extends Service implements LocationListener {
	private AlertDialog.Builder alertDialog;
	private ProgressDialog progress;
	private final Context context;
	private boolean isGPSEnabled = false;
	private boolean locationDataAvailable=false;
	private Location location; 
	private LocationManager locationManager;
	private double latitude; 
	private double longitude;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	public LocationUtil(Context context) {
		this.context = context;
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!isGPSEnabled ) {
				showSettingsAlert();
			} else {
				if (location == null) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
						else
						{
							showProgress();
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(LocationUtil.this);
		}		
	}

	//Function to get latitude
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}

		return latitude;
	}


	//Function to get longitude
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public boolean locationDataAvailable()
	{
		return locationDataAvailable;
	}
	private void  showProgress()
	{
		progress = new ProgressDialog(context);
		progress.setMessage("Please wait...");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(false);
		progress.show();
	}

	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("GPS is settings");
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}


	@Override
	public void onLocationChanged(Location location) {
		if(progress!=null)
		{
			if(progress.isShowing())
			{
				progress.dismiss();
			}
		}
		locationDataAvailable=true;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
