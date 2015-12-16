package com.aotd.map.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class CircleOverlay extends Overlay {
	
	
	private GeoPoint point;
	private Paint paint1, paint2;

	public CircleOverlay(GeoPoint point) {
		
	    this.point = point;

	    paint1 = new Paint();
	    paint1.setARGB(128, 105, 150, 211);
	    paint1.setStrokeWidth(2);
	    paint1.setStrokeCap(Paint.Cap.ROUND);
	    paint1.setAntiAlias(true);
	    paint1.setDither(false);
	    paint1.setStyle(Paint.Style.STROKE);

	    paint2 = new Paint();
	    paint2.setARGB(64, 203, 208, 215);  

	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

	    Point pt = mapView.getProjection().toPixels(point, null);
//	    Log.e("","************mapView.getZoomLevel() "+mapView.getZoomLevel());
	  
	    float radius;
	    
	    if(mapView.getZoomLevel() <= 14){
	    	
	        radius = 0;
	        
	    }else{
	    	
	    	radius = (float) Math.pow(2, mapView.getZoomLevel() - 10);
	    }

	    canvas.drawCircle(pt.x, pt.y, radius, paint2);
	    canvas.drawCircle(pt.x, pt.y, radius, paint1);

	}

}