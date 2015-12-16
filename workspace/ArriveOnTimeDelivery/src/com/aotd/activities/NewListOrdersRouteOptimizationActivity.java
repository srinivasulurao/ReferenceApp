package com.aotd.activities;

import java.util.ArrayList;

import com.aotd.adapters.RouteOptimizationAdapter;
import com.aotd.helpers.GetPlaceAPIResponseHandler;
import com.aotd.model.DispatchAllListModel;
import com.aotd.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewListOrdersRouteOptimizationActivity extends Activity implements LocationListener{

	private ListView routeOptList;
	private ImageView imgNetworkMode;
	private TextView txtCurrentAddress;
	private ArrayList<DispatchAllListModel> driverOrdersList;

	protected LocationManager locationManager;
	String provider;
	protected double latitude = -1;
	protected double longitude = -1;
	ProgressDialog progressdialog;

	String currentLocationName;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aotd_routeoptimizition_list);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		routeOptList = (ListView) findViewById(R.id.list_route_optimization);
		imgNetworkMode = (ImageView) findViewById(R.id.aotd_img_mode);
		txtCurrentAddress = (TextView) findViewById(R.id.txt_current_address);

		driverOrdersList = (ArrayList<DispatchAllListModel>) getIntent().getSerializableExtra("sortdata");

		getCurrentLocation();

		RouteOptimizationAdapter routeOptimizationAdapter = new RouteOptimizationAdapter(NewListOrdersRouteOptimizationActivity.this,driverOrdersList); 
		routeOptList.setCacheColorHint(Color.TRANSPARENT);

		routeOptList.setAdapter(routeOptimizationAdapter);
		if (latitude != -1 && longitude!= -1 ) {

			toGetCurrentNameUsingGoogleAPI(latitude, longitude);
		}else{

			mCurrentPlaceHandler.sendEmptyMessage(ERROR);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "Show Map").setIcon(R.drawable.menu_map_icon);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(Utils.checkNetwork(getApplicationContext()))
		{
			Intent i = new Intent(NewListOrdersRouteOptimizationActivity.this,MapView.class);
			i.putExtra("sortdata", driverOrdersList);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(Utils.checkNetwork(getApplicationContext()))
			imgNetworkMode.setBackgroundResource(R.drawable.online);
		else
			imgNetworkMode.setBackgroundResource(R.drawable.offline);
	}

	/** Get current latitude and longtude values by using location */
	private void getCurrentLocation() {

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);

		if (provider != null && !provider.equals("")) {

			Location location = locationManager.getLastKnownLocation(provider);

			locationManager.requestLocationUpdates(provider, 20000, 1, this);

			if (location == null) {

				Toast.makeText(NewListOrdersRouteOptimizationActivity.this, "Unable find Current Location..Please Switch on the GPS", Toast.LENGTH_LONG).show();

			} else {

				latitude = location.getLatitude();
				longitude = location.getLongitude();

				//				latitude = 29.464475;
				//				longitude = -98.446733;


				Utils.LATITUDE = location.getLatitude();
				Utils.LONGITUDE = location.getLongitude();

				//				Utils.LATITUDE = 29.464475;
				//				Utils.LONGITUDE = -98.446733;


				Log.e("lag & long ","kkk "+location.getLatitude() + "" + location.getLongitude());

			}

		} else {

			Toast.makeText(getBaseContext(), "No Provider Found",Toast.LENGTH_SHORT).show();
		}
	}

	private void toGetCurrentNameUsingGoogleAPI(double latitude,double longitude) {

		progressdialog = ProgressDialog.show(NewListOrdersRouteOptimizationActivity.this,null,null);
		progressdialog.setContentView(R.layout.loader); 

		new GetPlaceAPIResponseHandler(String.format(Utils.GET_LOCATION_NAME_URL,	latitude, longitude), mCurrentPlaceHandler).start();
	}

	@Override
	public void onLocationChanged(Location location) {

		/* Working code*/
		Log.e("lag & long ","kkk "+location.getLatitude() + "" + location.getLongitude());

		Utils.LATITUDE = location.getLatitude();
		Utils.LONGITUDE = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	private final int SUCCESS = 1;
	private final int ERROR = 0;

	/** Response Handler it's called when Google API response is come back */
	private Handler mCurrentPlaceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			try{

				if (progressdialog != null && progressdialog.isShowing()) {

					progressdialog.dismiss();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			switch (msg.what) {
			case SUCCESS:

				String result = msg.getData().getString("err_Msg");
				String response = msg.getData().getString("response");
				if (result.equalsIgnoreCase("OK") && !TextUtils.isEmpty(response)) {
					Log.e("resonce Address","kkk "+ response);

					txtCurrentAddress.setText(response);

				} else {

					txtCurrentAddress.setText("Unable to get current location.\n"+"Route Optimizaion based on PickUp Location.");
					Toast.makeText(NewListOrdersRouteOptimizationActivity.this, "Unable to get current location....",Toast.LENGTH_SHORT).show();
				}

				break;

			case ERROR:

				String error = msg.getData().getString("err_Msg");
				txtCurrentAddress.setText("Unable to get current location.\n"+"Route Optimizaion based on PickUp Location.");
				break;
			}
		}
	};
}
