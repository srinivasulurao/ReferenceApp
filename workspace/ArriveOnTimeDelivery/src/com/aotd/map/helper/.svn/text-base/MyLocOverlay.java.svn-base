package com.aotd.map.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.aotd.activities.R;
import com.aotd.activities.R.drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MyLocOverlay extends MyLocationOverlay implements CurrentLocation
{
	MapController mapController;
	private GeoPoint currentLocationGeopoint=null;
	private Context	context;
	private MapView mapView;
	private static String TAG=MyLocOverlay.class.getSimpleName();
	private CurrentLocation mCurrentLocation = null;
	private boolean gotLocation = false;
	private Paint mPaintBorder,mPaintFill;
	public MyLocOverlay(Context context, MapView mapView) 
	{
		super(context, mapView);
		this.context = context;
		this.mapView = mapView;
		mapController = mapView.getController();		
		mapController.setZoom(5);
	}

	@Override
	public synchronized void onLocationChanged(Location location) 
	{
		super.onLocationChanged(location);		
		if(!gotLocation){
			getCurrentLocation("", "", "", "", location.getLongitude(), location.getLatitude(), "", "");
			gotLocation = true;
		}
		
	}

	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
	{
		
		return super.draw(canvas, mapView, shadow, when);
	
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView arg1, Location location,
			GeoPoint myLocation, long arg4) {
		// TODO Auto-generated method stub
		 // translate the GeoPoint to screen pixels
        Point screenPts = mapView.getProjection().toPixels(myLocation, null);
        Bitmap currentLocBitmap = BitmapFactory.decodeResource( context.getResources(), R.drawable.icon);

        mPaintBorder = new Paint();
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setAntiAlias(true);
        mPaintBorder.setColor(0xee4D2EFF);
        mPaintFill = new Paint();
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(0x154D2EFF);
        
        int radius = (int) mapView.getProjection().metersToEquatorPixels(location.getAccuracy());
        /** Draw the boundary of the circle */
        canvas.drawCircle(screenPts.x, screenPts.y, radius, mPaintBorder);
        /** Fill the circle with semitransparent color */
        canvas.drawCircle(screenPts.x, screenPts.y, radius, mPaintFill);
        canvas.drawBitmap(currentLocBitmap,screenPts.x - (currentLocBitmap.getWidth()  / 2),screenPts.y - (currentLocBitmap.getHeight() / 2),null);
		
	
	}

	@Override
	public void getCurrentLocation(String locationName, String addressline,
			String currentState, String currentCountry, double longitude,
			double latitude, String country_short_name, String state_short_name) {
		// TODO Auto-generated method stub
		
	}
}
	
	


	
	
