package com.aotd.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MapOverlayHelper extends MyLocationOverlay
{
	MapController mapController;
	private GeoPoint currentLocationGeopoint=null;
	private boolean gotcurrentlocation=false;
	private Context	context;
	private static String TAG=MapOverlayHelper.class.getSimpleName();
	
	
	public MapOverlayHelper(Context context, MapView mapView) 
	{
		super(context, mapView);
		this.context=context;
		mapController=mapView.getController();
	}

	@Override
	public synchronized void onLocationChanged(Location location) 
	{
		super.onLocationChanged(location);
		if(!gotcurrentlocation)
		{
			mapController.animateTo(this.getMyLocation());
			currentLocationGeopoint=new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
			gotcurrentlocation=true;
		}
		
	}

	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
	{
		
		return super.draw(canvas, mapView, shadow, when);
	
	}

	@Override
	public boolean onTap(GeoPoint p, MapView map) 
	{
		Log.d(TAG, "tapped");
		Point tapedPoint=new Point(); 
		Point currentPoint=new Point();
		map.getProjection().toPixels(p, tapedPoint);
		map.getProjection().toPixels(currentLocationGeopoint, currentPoint);
		if(tapedPoint==currentPoint)
			Toast.makeText(context, "tapped on current locatino", Toast.LENGTH_SHORT).show();
		
		return super.onTap(p, map);
	}
	
	
	
}