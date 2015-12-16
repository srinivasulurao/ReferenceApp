package com.aotd.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.aotd.helpers.CustomItemizedOverlay;
import com.aotd.helpers.CustomOverlayItem;
import com.aotd.model.DriverGeoLocationModel;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;

public class DriverGpsScreenActivity extends MapActivity{
	
	private TapControlledMapView mapView;
	private MapController controller;
	private CustomItemizedOverlay<CustomOverlayItem> mDriverLocOverlay;
	private Intent mRecieverIntent;
	private ArrayList<DriverGeoLocationModel> mDriverGeoLocations = null;
	private boolean FIRST_LOC = true;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aotd_driver_gps_screen);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        
        mRecieverIntent = getIntent();
        mDriverGeoLocations = (ArrayList<DriverGeoLocationModel>) mRecieverIntent.getExtras().get("geolocations");
        
        Log.e("size", ""+mDriverGeoLocations.size());
        mapView = (TapControlledMapView) findViewById(R.id.driver_gps_screen_mapview);
        mapView.setBuiltInZoomControls(true);
        controller = mapView.getController();
        mDriverLocOverlay = new CustomItemizedOverlay<CustomOverlayItem>(DriverGpsScreenActivity.this.getResources().getDrawable(R.drawable.map_pin), mapView);
        
        mapView.setOnSingleTapListener(new OnSingleTapListener() {		
			@Override
			public boolean onSingleTap(MotionEvent e) {
				mDriverLocOverlay.hideAllBalloons();
				return true;
			}
		});   
        searchLoctaionPin(mDriverGeoLocations);
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void searchLoctaionPin(ArrayList<DriverGeoLocationModel> driverLocation){
		if(driverLocation != null){
			for (int i=0; i<driverLocation.size(); i++) {		
				double lat = Double.parseDouble(driverLocation.get(i).getLat());							
				double lon = Double.parseDouble(driverLocation.get(i).getLon());				
				
		        
				GeoPoint point = new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
				CustomOverlayItem overlayitem = new CustomOverlayItem(point, driverLocation.get(i).getName().trim(),driverLocation.get(i).getLat().trim(),driverLocation.get(i).getLon().trim(),DriverGpsScreenActivity.this);
				mDriverLocOverlay.addOverlay(overlayitem);
				mapView.getOverlays().add(mDriverLocOverlay);
				
				
				if(FIRST_LOC && (lat > 0.0 && lon > 0.0) ){
					FIRST_LOC = false;
					controller.animateTo(point);
				}
			}
			mapView.invalidate(); 
		}
	}
}