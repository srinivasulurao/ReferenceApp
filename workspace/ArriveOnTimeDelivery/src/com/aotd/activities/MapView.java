package com.aotd.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import com.aotd.model.DispatchAllListModel;
import com.aotd.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

public class MapView extends Activity {
	private GoogleMap map;
	private ArrayList<DispatchAllListModel> driverOrdersList;

	public static String getResponseFromUrl(String url) {
		String xml = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	private String getLatLon(String address)
	{
		String sLoc="";
		try{
			String add_ = address.replace(" ", "%20");
			String url_="http://maps.googleapis.com/maps/api/geocode/json?address="+add_+"&sensor=true";
			String resp= getResponseFromUrl(url_);
			JSONObject jo = new JSONObject(resp);
			JSONArray ja = new JSONArray(jo.getString("results"));

			for(int i=0; i<ja.length(); i++)
			{
				JSONObject j = ja.getJSONObject(i);
				String addr = j.getString("formatted_address");
				JSONObject jLoc = new JSONObject(j.getString("geometry"));
				sLoc  = jLoc.getString("location");
			}

		}catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
		}

		return sLoc;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 

		driverOrdersList = (ArrayList<DispatchAllListModel>) getIntent().getSerializableExtra("sortdata");

		String sInfo = getIntent().getStringExtra("ACTIVITY");
		
	 if(sInfo != null)
	 {
		 String fromAdd = getIntent().getStringExtra("FROM");
		 String address = getIntent().getStringExtra("TO");
		 
		 String loc_DL = getLatLon(address);
			String loc_PU = getLatLon(fromAdd);
			
			double latFrom =0.0;
			double lonFrom = 0.0;

			double latFrom_ =0.0;
			double lonFrom_ = 0.0;
			
			try{
				JSONObject jo = new JSONObject(loc_DL);
				JSONObject jo_ = new JSONObject(loc_PU);
				
				String sTOLat = jo.getString("lat");
				String sTOLon = jo.getString("lng");
				
				lonFrom = Double.parseDouble(sTOLon);
				latFrom = Double.parseDouble(sTOLat);
				
				String sFROMLat = jo_.getString("lat");
				String sFROMLon = jo_.getString("lng");
				
				lonFrom_ = Double.parseDouble(sFROMLon);
				latFrom_ = Double.parseDouble(sFROMLat);
				
				String s="";
				
			}catch(Exception e){}

			
			if (Utils.checkNetwork(MapView.this))
			{
				try{
					
					double longitude =lonFrom;
					double latitude = latFrom;

					double longitude_ =lonFrom_;
					double latitude_ = latFrom_;

					final LatLng HAMBURG = new LatLng(latitude, longitude);
					final LatLng HAMBURG_ = new LatLng(latitude_, longitude_);
					map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
							.getMap();

					Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
							.snippet(address)
							);
					Marker hamburg_ = map.addMarker(new MarkerOptions().position(HAMBURG_)
							.snippet(fromAdd)
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
							);

					/*hamburg.showInfoWindow();
					hamburg_.showInfoWindow();*/

					Polyline line = map.addPolyline(new PolylineOptions()
					.add(HAMBURG,HAMBURG_)
					.width(5)
					.color(Color.RED));

					map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

					// Zoom in, animating the camera.
					map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);



				}catch(Exception e){}
			}
			else{ Toast.makeText(getApplicationContext(), "Please check for Internet Connection", Toast.LENGTH_SHORT).show();}
	 }
	 else{		
				for(int i=0;i<driverOrdersList.size();i++)
				{
					String DLAddress = driverOrdersList.get(i).getDladdress();
					String DLCity= driverOrdersList.get(i).getDlcity();
					String DLState= driverOrdersList.get(i).getState();
					String DLZip= driverOrdersList.get(i).getDlzip();

					String PUAddress = driverOrdersList.get(i).getAddress();
					String PUCity= driverOrdersList.get(i).getCity();
					String PUState = driverOrdersList.get(i).getState();
					String PUZip = driverOrdersList.get(i).getZip();


					String address = DLAddress+" "+DLCity+" "+DLState+" "+DLZip;
					String fromAdd = PUAddress+" "+PUCity+" "+PUState+" "+PUZip;
					
					String loc_DL = getLatLon(address);
					String loc_PU = getLatLon(fromAdd);
					
					double latFrom =0.0;
					double lonFrom = 0.0;

					double latFrom_ =0.0;
					double lonFrom_ = 0.0;
					
					try{
						JSONObject jo = new JSONObject(loc_DL);
						JSONObject jo_ = new JSONObject(loc_PU);
						
						String sTOLat = jo.getString("lat");
						String sTOLon = jo.getString("lng");
						
						lonFrom = Double.parseDouble(sTOLon);
						latFrom = Double.parseDouble(sTOLat);
						
						String sFROMLat = jo_.getString("lat");
						String sFROMLon = jo_.getString("lng");
						
						lonFrom_ = Double.parseDouble(sFROMLon);
						latFrom_ = Double.parseDouble(sFROMLat);
						
						String s="";
						
					}catch(Exception e){}

					
					if (Utils.checkNetwork(MapView.this))
					{
						try{
							
							double longitude =lonFrom;
							double latitude = latFrom;

							double longitude_ =lonFrom_;
							double latitude_ = latFrom_;

							final LatLng HAMBURG = new LatLng(latitude, longitude);
							final LatLng HAMBURG_ = new LatLng(latitude_, longitude_);
							map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
									.getMap();

							Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
									.snippet(address)
									.title(Integer.toString(i)));
							Marker hamburg_ = map.addMarker(new MarkerOptions().position(HAMBURG_)
									.snippet(fromAdd)
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
									.title(Integer.toString(i)));

							/*hamburg.showInfoWindow();
							hamburg_.showInfoWindow();*/

							Polyline line = map.addPolyline(new PolylineOptions()
							.add(HAMBURG,HAMBURG_)
							.width(5)
							.color(Color.RED));

							map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));

							// Zoom in, animating the camera.
							map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);



						}catch(Exception e){
							


						}

					}
					else
					{
						Toast.makeText(getApplicationContext(), "Please Check for Internet Connection", Toast.LENGTH_SHORT).show();
					}
				}
	 }
				
				
				
			
		
		
		






	}

	private void showGPSDisabledAlertToUser(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
		.setCancelable(false)
		.setPositiveButton("Enable GPS",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}


}
