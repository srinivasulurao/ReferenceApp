package com.aotd.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.map.helper.CircleOverlay;
import com.aotd.map.helper.CurrentLocationCustomItemizedOverlay;
import com.aotd.map.helper.CurrentLocationCustomOverlayItem;
import com.aotd.map.helper.CustomItemizedOverlay;
import com.aotd.map.helper.CustomOverlayItem;
import com.aotd.map.helper.LatLongModel;
import com.aotd.map.helper.LocationPointsParser;
import com.aotd.map.helper.MyLocOverlay;
import com.aotd.map.helper.PathOverLay;
import com.aotd.map.helper.RoadModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.RouteOptamizationModel;
import com.aotd.utils.Utils;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;


public class OrdersRouteOptimizationMapActivity extends MapActivity{
	
	boolean FIRST_LOC = true;
	private ArrayList<DispatchAllListModel> mDataModelList = null;
	private ArrayList<RouteOptamizationModel> mRouteOptamizationModelFeeds =  new ArrayList<RouteOptamizationModel>();
	private RouteOptamizationModel routeOptamizationModel;
	private CustomItemizedOverlay<CustomOverlayItem> mDriverLocOverlay, mDriverPULocOverlay, mDriverDLLocOverlay;
	TapControlledMapView mMapView;
	Drawable drawable;    	
	Button mBtnBusinessList;
	ImageButton mBTNHome;		
	MapController mMapController;
	MyLocationOverlay myLocationOverlay;
	ProgressDialog progressDialog;
	String mTitle;
	MyLocOverlay mMyLocOverlay;
	TextView mTxtTitle;
	
	private ArrayList<double[]> latlong_arr;	
	private ArrayList<RoadModel> mRoad = new ArrayList<RoadModel>();
	private PathOverLay mpathOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aotd_driver_gps_screen);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		initializeUI();
		
	}
	
	@SuppressWarnings("unchecked")
	private void initializeUI() {
		// TODO Auto-generated method stub
		
		mMapView = (TapControlledMapView) this.findViewById(R.id.driver_gps_screen_mapview);
		mRouteOptamizationModelFeeds = (ArrayList<RouteOptamizationModel>) getIntent().getExtras().getSerializable("data");
		latlong_arr = (ArrayList<double[]>) getIntent().getExtras().getSerializable("points");

		progressDialog = ProgressDialog.show(OrdersRouteOptimizationMapActivity.this,null,null);
		progressDialog.setContentView(R.layout.loader);
		mMapController = mMapView.getController();  
		
		mDriverPULocOverlay = new CustomItemizedOverlay<CustomOverlayItem>(OrdersRouteOptimizationMapActivity.this.getResources().getDrawable(R.drawable.pin_green), mMapView);
		mDriverDLLocOverlay = new CustomItemizedOverlay<CustomOverlayItem>(OrdersRouteOptimizationMapActivity.this.getResources().getDrawable(R.drawable.pin_red), mMapView);
		
		try {
			
			mMyLocOverlay = new MyLocOverlay(OrdersRouteOptimizationMapActivity.this, mMapView);
			mMapView.getOverlays().add(mMyLocOverlay);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		mThread = new Thread(new Runnable() {
			public void run() {
				
				try {
					
					if(Utils.LOCATION_FOUND){
						
						addLocationPin(mRouteOptamizationModelFeeds.get(point_cnt));				
						Thread.sleep(3000);
						locationMapPin();
						
					}else{
						
						locationMapPinOne();
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mThread.start();
		
		
	}
	
	Thread mThread;
	int point_cnt = 0;
	
	@SuppressWarnings("unused")
	private void locationMapPin() {
		
		if(point_cnt < mRouteOptamizationModelFeeds.size()) {
			
			addRouteOnMap(latlong_arr.get(point_cnt)[0], latlong_arr.get(point_cnt)[1], latlong_arr.get(point_cnt+1)[0], latlong_arr.get(point_cnt+1)[1]);
			
		}
	}
	
	
	@SuppressWarnings("unused")
	private void locationMapPinOne() {
		
		if(point_cnt < mRouteOptamizationModelFeeds.size()) {
			
			addPin(mRouteOptamizationModelFeeds.get(point_cnt));
			addRouteOnMap(latlong_arr.get(point_cnt)[0], latlong_arr.get(point_cnt)[1], latlong_arr.get(point_cnt+1)[0], latlong_arr.get(point_cnt+1)[1]);
			
		}
	}
	
	private void setZoom(ArrayList<LatLongModel> checkInVenueItems) {
		
		if (checkInVenueItems != null) {
			
			int i = 0;
			int minLat = Integer.MAX_VALUE;
			int maxLat = Integer.MIN_VALUE;
			int minLon = Integer.MAX_VALUE;
			int maxLon = Integer.MIN_VALUE;
			
			for (LatLongModel item : checkInVenueItems) {
				i++;
				double lat = Double.parseDouble(item.getLat());
				double lon = Double.parseDouble(item.getLong());
				
				int lati = (int) (lat * 1E6);
				int longi = (int) (lon * 1E6);
				
				maxLat = Math.max(lati, maxLat);
				minLat = Math.min(lati, minLat);
				maxLon = Math.max(longi, maxLon);
				minLon = Math.min(longi, minLon);      
				
			}
			
			double fitFactor = 1.1;
			mMapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * fitFactor), (int)(Math.abs(maxLon - minLon) * fitFactor));            
			mMapController.animateTo(new GeoPoint( (maxLat + minLat)/2,(maxLon + minLon)/2 )); 
		}
	}
	
	private CurrentLocationCustomItemizedOverlay<CurrentLocationCustomOverlayItem> mLocationLocOverlay;
	private Double mLat, mCuLat, mLong, mCuLong;
	private CircleOverlay mCircleOverlay;
	private ArrayList<LatLongModel> mLatLongItems = new ArrayList<LatLongModel>();

	
	private void addLocationPin(RouteOptamizationModel routeOptamizationModel){
		
		mLocationLocOverlay = new CurrentLocationCustomItemizedOverlay<CurrentLocationCustomOverlayItem>(OrdersRouteOptimizationMapActivity.this.getResources().getDrawable(R.drawable.current_location_pin), mMapView);
		
		LatLongModel mLatLongModel = new LatLongModel();
		
		String mCiity = "";
		try {
			
			mCuLat = routeOptamizationModel.getLatitude();
			mCuLong = routeOptamizationModel.getLongitude();
			
			mCiity = "none";					
			mLatLongModel.setLat(mCuLat+"");
			mLatLongModel.setLong(mCuLong+"");
			
			mLatLongItems.add(mLatLongModel);
			mLatLongModel= null;
			
			GeoPoint point = new GeoPoint((int) (mCuLat * 1E6), (int) (mCuLong * 1E6));	
			mCircleOverlay = new CircleOverlay(point);
			mMapView.getOverlays().add(mCircleOverlay);
			mMapView.getOverlays().add(mLocationLocOverlay);
			mMapController.animateTo(point);
			mMapController.setZoom(5);			
			
			CurrentLocationCustomOverlayItem overlayitem = new CurrentLocationCustomOverlayItem( 
					point, 
					routeOptamizationModel.getOrder_id(),
					routeOptamizationModel.getAddress(),				
					OrdersRouteOptimizationMapActivity.this);
			
			mLocationLocOverlay.addOverlay(overlayitem);
			mMapView.setOnSingleTapListener(new OnSingleTapListener() {
				
				public boolean onSingleTap(MotionEvent e) {
					mLocationLocOverlay.hideAllBalloons();
					return true;
				}
			});	
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private void addPin(RouteOptamizationModel routeOptamizationModel){
		
		try {
			
			if(routeOptamizationModel.getType().equalsIgnoreCase("PU"))
				mDriverLocOverlay = mDriverPULocOverlay;
			else
				mDriverLocOverlay = mDriverDLLocOverlay;
			
			mLat = routeOptamizationModel.getLatitude();
			mLong = routeOptamizationModel.getLongitude();
			GeoPoint point = new GeoPoint((int) (mLat * 1E6), (int) (mLong * 1E6));			
			mMapView.getOverlays().add(mDriverLocOverlay);
			
			LatLongModel mLatLongModel = new LatLongModel();
			mLatLongModel.setLat(mLat+"");
			mLatLongModel.setLong(mLong+"");
			mLatLongItems.add(mLatLongModel);
			mLatLongModel = null;
			
			String order = routeOptamizationModel.getOrder_id()+", "+routeOptamizationModel.getCompany();
			String address = routeOptamizationModel.getAddress();
			
			CustomOverlayItem overlayitem = new CustomOverlayItem( point, 
					order, address, OrdersRouteOptimizationMapActivity.this);
			
			mDriverLocOverlay.addOverlay(overlayitem);
			mMapView.setOnSingleTapListener(new OnSingleTapListener() {
				
				public boolean onSingleTap(MotionEvent e) {
					mDriverLocOverlay.hideAllBalloons();
					return true;
				}
			});	
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	MyHandelr myhandler= new MyHandelr();
	
	class MyHandelr extends Handler{	
		
		public void handleMessage(android.os.Message message){	
			
			if(mLatLongItems.size()>1)
				setZoom(mLatLongItems);
			
			progressDialog.dismiss();
			
			try {
				
				mThread.interrupt();			
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		
		// TODO Auto-generated method stub
		return false;
		
	}
	
	protected void addRouteOnMap(double fromLat,double fromLon,double toLat,double toLon) {	 
		
		fromLat = 29.464475;
		fromLon =  -98.446733;
				
		String url = String.format("http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=false",String.valueOf(fromLat),String.valueOf(fromLon),String.valueOf(toLat),String.valueOf(toLon));   
		LocationPointsParser path_obj = new LocationPointsParser(mHandler,url);
		path_obj.start();   	
		
	}
	
	
	//handler show the progress until complete the task
	Handler mHandler = new Handler() { 		
		
		
		public void handleMessage(android.os.Message msg) { 
			
			if(msg.what==10){
				
				String errorMsg = msg.getData().getString("HttpError");
				
				if(errorMsg.length()>0){
					
						new AlertDialogMsg(OrdersRouteOptimizationMapActivity.this, "AOTD", "No corresponding geographic location could be found for one of the specified orders").setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
							
							@Override
							public void onClick(DialogInterface dialog, int which) {						
								
								finish();
								
							}
							
						}).create().show();
					
				}else{
					
					RoadModel mRoadModel = (RoadModel) msg.getData().getSerializable("points");  	  
					mRoad.add(mRoadModel);					
					mpathOverlay = new PathOverLay(mRoad, mMapView);
					List<Overlay> mapOverlays = mMapView.getOverlays(); 	    	
					mapOverlays.add(mpathOverlay);    	  
					
					point_cnt = point_cnt+1;
					
					if(point_cnt<mRouteOptamizationModelFeeds.size()-1){
						
						addPin(mRouteOptamizationModelFeeds.get(point_cnt));
						addRouteOnMap(latlong_arr.get(point_cnt)[0], latlong_arr.get(point_cnt)[1], latlong_arr.get(point_cnt+1)[0], latlong_arr.get(point_cnt+1)[1]);

					}else{
						
						addPin(mRouteOptamizationModelFeeds.get(point_cnt));

						try {
							Thread.sleep(100);
							myhandler.sendEmptyMessage(0);
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}	
			}
		}; 
	};  
}