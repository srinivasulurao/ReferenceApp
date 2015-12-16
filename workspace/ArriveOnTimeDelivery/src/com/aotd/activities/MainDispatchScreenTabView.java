 package com.aotd.activities;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aotd.activities.DispatchPresentTabActivity.DispatchPresentAdapter;
import com.aotd.activities.DispatchPresentTabActivity.DispatchPresentHandler;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DriverGeoLocationModel;
import com.aotd.model.RouteOptamizationModel;
import com.aotd.parsers.DriverCurrentOrdersParser;
import com.aotd.parsers.DriverGeoLocationParser;
import com.aotd.service.DispatchOrderService;
import com.aotd.service.LocationUpdateService;
import com.aotd.utils.Utils;






/**
 * 
 * @author bharath
 *
 */

public class MainDispatchScreenTabView extends TabActivity 
{
	
	private ProgressDialog progressdialog;
	public static TabHost tabHost;  // The activity TabHost
	private TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	private Intent intent;  // Reusable Intent for each tab	 
	public static TextView title;
	public static ImageView imgOnline;
	private boolean mIsBound;
	private Messenger mService;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	public int count = 0;
	private SharedPreferences mPreferences;
	
	private int isDriverListView = 1;
	private boolean isCurrentLocation = false;
	
	boolean isClicked=false;
	
	private void setTabs()
	{
		addTab("DELIVERED", R.drawable.tab_delivered, DispatchPastTabActivity.class);
		addTab("PRESENT", R.drawable.tab_present, DispatchPresentTabActivity.class);
		addTab("FUTURE", R.drawable.tab_future, DispatchFutureTabActivity.class);
		addTab("DISPATCH", R.drawable.tab_dispatch, DispatchTabActivity.class);
		addTab("OPEN", R.drawable.tab_open, DispatchOpenTabActivity.class);

	}

	private void addTab(String labelId, int drawableId, Class<?> c)
	{
		tabHost = getTabHost();
		Intent intent = new Intent(this, c).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
	
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator1, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		
		tabHost.addTab(spec);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);        
		requestWindowFeature(Window.FEATURE_NO_TITLE);	           
		setContentView(R.layout.aotd_dispatchscreen);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		title = (TextView)findViewById(R.id.dispatchScreen_layout_title);     
		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
		
		mPreferences = this.getSharedPreferences("DispatchCount", MODE_PRIVATE);
		
        setTabs();
		
		
		tabHost.setCurrentTab(3);  
		title.setText("Dispatch Screen");
		
		
		if(getIntent().getStringExtra("from").equalsIgnoreCase("dispatch")){
			tabHost.setCurrentTab(1);  
		}else if(getIntent().getStringExtra("from").equalsIgnoreCase("roundtrip")){
			tabHost.setCurrentTab(1);    
		}else if(getIntent().getStringExtra("from").equalsIgnoreCase("rna")){
			tabHost.setCurrentTab(3);    
		}else{
			tabHost.setCurrentTab(3);
			title.setText("Dispatch Screen");
		}	
		
		if(Utils.ROLENAME.trim().equalsIgnoreCase("driver")){
			System.out.println("The location update for driver as started");
			startService(new Intent(MainDispatchScreenTabView.this, LocationUpdateService.class));
			
		}
		
		startService(new Intent(MainDispatchScreenTabView.this, DispatchOrderService.class));
		doBindService();
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) 
			{
				// TODO Auto-generated method stub
				Log.e("tabid", tabId);
			}
		});
		
		imgOnline.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClicked)
				{
					if(Utils.NetworkType(MainDispatchScreenTabView.this).equalsIgnoreCase(Utils.wifi))
					{
						Utils.wifiOFF(MainDispatchScreenTabView.this);
						MainDispatchScreenTabView.imgOnline
						.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					else if(Utils.NetworkType(MainDispatchScreenTabView.this).equalsIgnoreCase(Utils.mobile))
					{
						Utils.mobileDataOFF(MainDispatchScreenTabView.this);
						MainDispatchScreenTabView.imgOnline
						.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					
				}
				else
				{
					MainDispatchScreenTabView.imgOnline
					.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(MainDispatchScreenTabView.this);
				}

				return false;
			}
		});
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Utils.NetworkType(MainDispatchScreenTabView.this).equalsIgnoreCase(Utils.wifi))
		{
			MainDispatchScreenTabView.imgOnline
			.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else if(Utils.NetworkType(MainDispatchScreenTabView.this).equalsIgnoreCase(Utils.mobile))
		{
			MainDispatchScreenTabView.imgOnline
			.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else
		{
			MainDispatchScreenTabView.imgOnline
			.setBackgroundResource(R.drawable.offline);
			isClicked = false;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		stopService(new Intent(MainDispatchScreenTabView.this,LocationUpdateService.class));
		doUnbindService();
		stopService(new Intent(MainDispatchScreenTabView.this, DispatchOrderService.class));
	}
	
	public void tabTextAlignment()
	{
		
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().height=60;
		tabHost.getTabWidget().getChildAt(0).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(0).setPadding(10, 10, 10, 10);
		
		
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().height=60;
		tabHost.getTabWidget().getChildAt(1).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(1).setPadding(10, 10, 10, 10);
		
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().height=60;
		tabHost.getTabWidget().getChildAt(2).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(2).setPadding(10, 10, 10, 10);
		
		
		tabHost.getTabWidget().getChildAt(3).getLayoutParams().height=60;
		tabHost.getTabWidget().getChildAt(3).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(3).setPadding(10, 10, 10, 10);
		
		tabHost.getTabWidget().getChildAt(4).getLayoutParams().height=60;
		tabHost.getTabWidget().getChildAt(4).getLayoutParams().width=100;
		tabHost.getTabWidget().getChildAt(4).setPadding(10, 10, 10, 10);
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		Log.e("current tab when menu clicked",tabHost.getCurrentTab()+"");
		
		
		if(Utils.ROLENAME.trim().equalsIgnoreCase("superAdmin")){
			
			if (tabHost.getCurrentTab()==2 || tabHost.getCurrentTab() == 3) {
			
			menu.add(0, 1, 0, "Gps").setIcon(R.drawable.menu_map_icon);
			menu.add(0, 2, 1, "List").setIcon(R.drawable.menu_list_icon);
			}
			
		}else{
			
//			if (tabHost.getCurrentTab()==0 || tabHost.getCurrentTab() == 1) {
//			menu.add(0, 1, 0, "Gps").setIcon(R.drawable.menu_map_icon);
////			menu.add(0, 2, 1, "List").setIcon(R.drawable.menu_list_icon);
//			
//			}
			
		}
		return true;
		
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		return true;
	}
	
//	private ArrayList<DispatchAllListModel> marrDispatchPresentList;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		switch (item.getItemId()) {
		
		case 1:
			
			isDriverListView =0;
			
			break;
			
		case 2:
			
			isDriverListView =1;
			
			break;
			
		}
		
		
		if(Utils.checkNetwork(getApplicationContext())){
			
			if(Utils.ROLENAME.trim().equalsIgnoreCase("superAdmin")){
				
				progressdialog = ProgressDialog.show(this, "",	"Please wait...", true);
				Log.v("url",Utils.DRIVER_GEOLOCATION_URL);
				new DriverGeoLocationParser(Utils.DRIVER_GEOLOCATION_URL,new DriverGeoLocationHandler()).start();
				
				
			}else{
				
//				progressdialog = ProgressDialog.show(this, "",	"Please wait...", true);
//				
//				try {
//					
//					marrDispatchPresentList = new ArrayList<DispatchAllListModel>();
//					String encodedRoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
//					String encodedId   = URLEncoder.encode(Utils.USER_ID,"UTF-8");
//					String url = String.format(Utils.PRESENT_DISPATCH_URL,encodedRoleName,encodedId);
//					DriverCurrentOrdersParser mDispatchparser = new DriverCurrentOrdersParser(new DispatchPresentHandler(), url , marrDispatchPresentList);
//					mDispatchparser.start();
//					
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
				
				if(Utils.DriverOrdersList != null && Utils.DriverOrdersList.size()>0){
					
					
//					for(int i=0 ; i<Utils.DriverOrdersList.size();i++)
//						Log.e("", "marrDispatchPresentList "+ marrDispatchPresentList.get(i).getDLDate());
					
					alertDialogForRouteOptimization("AOTD", "Choose Location for RouteOptimization");
//					if(Utils.DriverOrdersList.size()>1)
//						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
//					
//					getOrders();
					
				}else{
					
					alertDialogWithMsg("AOTD","Currently no orders for delivery");
					
				}
				
			}
			
		}else{
			
			Toast.makeText(getApplicationContext(), "Internet not available",Toast.LENGTH_SHORT).show();
		}
		
		
		return false;
	}
	
	public class DriverGeoLocationHandler extends Handler
	{
		public void handleMessage(android.os.Message msg) 
		{
			progressdialog.dismiss();
			switch (msg.what) {
			
			case 1:
				
				Utils.mDriverGeoLocations = (ArrayList<DriverGeoLocationModel>) msg.getData().getSerializable("response");
				
				Log.v("isdriverlist","** "+isDriverListView);
				
				if(isDriverListView ==0)
				{ 	        			
					
					intent=new Intent(MainDispatchScreenTabView.this,DriverGpsScreenActivity.class).putExtra("geolocations", Utils.mDriverGeoLocations);
					startActivity(intent);
					
				}else{
					
					intent=new Intent(MainDispatchScreenTabView.this,DriverListScreenActivity.class).putExtra("geolocations", Utils.mDriverGeoLocations);
					startActivity(intent);
				}	
				
				break;
				
			case 0:
				
				String errorMsg=msg.getData().getString("HttpError");
				alertDialogWithMsg("AOTD",errorMsg);
				break;
			}
		}
	};
	
	
	
	class DispatchPresentHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			progressdialog.dismiss();
			
			String errorMsg=msg.getData().getString("HttpError");
			
			if(errorMsg.length()>0){
				
				alertDialogWithMsg("AOTD",errorMsg);				
				
			}else{
				
				if(Utils.DriverOrdersList != null && Utils.DriverOrdersList.size()>0){
					
					
					for(int i=0 ; i<Utils.DriverOrdersList.size();i++)
						Log.e("", "Utils.DriverOrdersList "+ Utils.DriverOrdersList.get(i).getDLDate());
					
					alertDialogForRouteOptimization("AOTD", "Choose Location for RouteOptimization");
//					if(Utils.DriverOrdersList.size()>1)
//						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
//					
//					getOrders();
					
				}else{
					
					alertDialogWithMsg("AOTD","Currently no orders for delivery");
					
				}
			}
		}
	}
	
	private ArrayList<RouteOptamizationModel> mRouteOptamizationModelFeeds;
	private RouteOptamizationModel routeOptamizationModel;
	private ArrayList<double[]> latlong_arr;
	private ArrayList<String> citynames;
	private StringBuffer sb;
	
	private void getOrders(){
		
		mRouteOptamizationModelFeeds =  new ArrayList<RouteOptamizationModel>();
		latlong_arr = new ArrayList<double[]>();
		citynames = new ArrayList<String>();
		
		if(Utils.LOCATION_FOUND){
			
			routeOptamizationModel = new RouteOptamizationModel();
			routeOptamizationModel.setOrder_id(Utils.CITY);
			routeOptamizationModel.setCompany("");
			routeOptamizationModel.setAddress(Utils.ADDRESS);
			routeOptamizationModel.setLatitude(Utils.LATITUDE);
			routeOptamizationModel.setLongitude(Utils.LONGITUDE);
			routeOptamizationModel.setType("CL");
			mRouteOptamizationModelFeeds.add(routeOptamizationModel);
			latlong_arr.add(new double[]{Utils.LATITUDE,Utils.LONGITUDE});
			
			sb = new StringBuffer();
			sb.append("Dallas"); 
			citynames.add(sb.toString());
			
		}
		
		
		for(int i = 0; i<Utils.DriverOrdersList.size();i++){
			
			routeOptamizationModel = new RouteOptamizationModel();
			routeOptamizationModel.setOrder_id(Utils.DriverOrdersList.get(i).getOrder_id());
			routeOptamizationModel.setCompany(Utils.DriverOrdersList.get(i).getCompany());
			routeOptamizationModel.setAddress(Utils.DriverOrdersList.get(i).getAddress()+", "+Utils.DriverOrdersList.get(i).getCity());
			routeOptamizationModel.setLatitude(Utils.DriverOrdersList.get(i).getLatitude());
			routeOptamizationModel.setLongitude(Utils.DriverOrdersList.get(i).getLongitude());
			routeOptamizationModel.setType("PU");
			mRouteOptamizationModelFeeds.add(routeOptamizationModel);
			latlong_arr.add(new double[]{Utils.DriverOrdersList.get(i).getLatitude(),Utils.DriverOrdersList.get(i).getLongitude()});
			
			sb = new StringBuffer();
			sb.append(Utils.DriverOrdersList.get(i).getAddress()+",");
			sb.append(Utils.DriverOrdersList.get(i).getCity()); 
			citynames.add(sb.toString());
			
			
			routeOptamizationModel = new RouteOptamizationModel();
			routeOptamizationModel.setOrder_id(Utils.DriverOrdersList.get(i).getOrder_id());
			routeOptamizationModel.setCompany(Utils.DriverOrdersList.get(i).getCompany());
			routeOptamizationModel.setAddress(Utils.DriverOrdersList.get(i).getDladdress()+", "+Utils.DriverOrdersList.get(i).getDlcity());
			routeOptamizationModel.setLatitude(Utils.DriverOrdersList.get(i).getDlLatitude());
			routeOptamizationModel.setLongitude(Utils.DriverOrdersList.get(i).getDlLongitude());
			routeOptamizationModel.setType("DL");
			mRouteOptamizationModelFeeds.add(routeOptamizationModel);
			latlong_arr.add(new double[]{Utils.DriverOrdersList.get(i).getDlLatitude(),Utils.DriverOrdersList.get(i).getDlLongitude()});
			
			sb = new StringBuffer();
			sb.append(Utils.DriverOrdersList.get(i).getDladdress()+",");
			sb.append(Utils.DriverOrdersList.get(i).getDlcity()); 
			citynames.add(sb.toString());
			sb = null;
			
		}
		
		
		if(isDriverListView ==0){ 
			
			startActivity(new Intent(MainDispatchScreenTabView.this,OrdersRouteOptimizationMapActivity.class)
			.putExtra("data", mRouteOptamizationModelFeeds)
			.putExtra("points", latlong_arr));
			
		}else{
			
			startActivity(new Intent(MainDispatchScreenTabView.this,OrdersRouteOptimizationListActivity.class)
			.putExtra("citynames", citynames));
			
		}
		
	}
	
	public void alertDialogWithMsg(String title, String msg)
	{
		
		new AlertDialogMsg(MainDispatchScreenTabView.this, title, msg ).setPositiveButton("OK", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
			}
			
		}).create().show();		
		
	}
	
	
	class IncomingHandler extends Handler {
		private Builder dialog;
		
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DispatchOrderService.MSG_TO_CLIENT:
				String response = msg.getData().getString("response");
				Log.e("response count", response+" ");
				Log.v("if loop count ",""+response+" mPreferences "+mPreferences.getInt("count", 0));
				
				if( mPreferences.getInt("count", 0) < Integer.parseInt(response)){	
					
					mPreferences.edit().putInt("count", Integer.parseInt(response)).commit();
					
					if(tabHost.getCurrentTab() != 3 ){
						
						Log.v("ok loop count ",""+response+" mPreferences "+mPreferences.getInt("count", 0));
						
						mPreferences.edit().putBoolean("colour", true).commit();
						tabHost.getTabWidget().getChildAt(3).setBackgroundColor(Color.MAGENTA);
						Utils.NEW_ORDER_VIEWED = false;
						
					}
					
				}else {	
					
					mPreferences.edit().putInt("count", Integer.parseInt(response)).commit();
				}
				
				break;
			case DispatchOrderService.ERROR:
				String error = msg.getData().getString("error");
				
				break;
			}
		}
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = new Messenger(service);
			try {
				Message msg = Message.obtain(null,
						DispatchOrderService.MSG_REGISTER_CLIENT);
				Bundle data = new Bundle();
				data.putString("fromuserid", Utils.USER_ID.trim());
				msg.replyTo = mMessenger;
				msg.setData(data);
				mService.send(msg);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even do
				// anything with it
			}
		}
		
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected - process crashed.
			mService = null;
		}
	};
	
	void doBindService() {
		bindService(new Intent(this, DispatchOrderService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with it,
			// then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							DispatchOrderService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service has
					// crashed.
				}
			}
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	public void alertDialogForRouteOptimization(String title, String msg){	
		
		new AlertDialogMsg(MainDispatchScreenTabView.this, title, msg )
		.setPositiveButton("Current Location", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				isCurrentLocation = true;
				
				if(Utils.DriverOrdersList.size()>1)						
					Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList,isCurrentLocation).sortData();

				getOrders();
			}
		})
		.setNegativeButton("PickUp Location", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				isCurrentLocation = false;
				
				if(Utils.DriverOrdersList.size()>1)						
					Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList,isCurrentLocation).sortData();

				getOrders();
			}
		})
		.create().show();		
	}
}
	