package com.aotd.activities;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.aotd.activities.DispatchPresentTabActivity.DispatchPresentAdapter;
import com.aotd.activities.DispatchPresentTabActivity.DispatchPresentHandler;
import com.aotd.activities.MainDispatchScreenTabView.DriverGeoLocationHandler;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.AOTDDataBase;
import com.aotd.model.Allorders;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DriverGeoLocationModel;
import com.aotd.model.RouteOptamizationModel;
import com.aotd.parsers.AuthenticationParser;
import com.aotd.parsers.DispatchAllParser;
import com.aotd.parsers.DispatchDeliverParser;
import com.aotd.parsers.DriverGeoLocationParser;
import com.aotd.utils.GetPresentSortDistance;
import com.aotd.utils.Utils;
import com.rna.activities.RNATabActivity;

/**
 * @author bharath
 */


public class DispatchPastTabActivity extends Activity {
	/** Called when the activity is first created. */
	
	private ListView mDispatchPastList = null;	
	private ProgressDialog progressdialog = null;
	private DispatchPastAdapter mDataAdapter = null;
	private ArrayList<DispatchAllListModel> marrDispatchPastList = null;
	private Button acceptOrders = null;
	private Button selectedOrders = null;
	private Button cancel_btn = null;
	private LinearLayout dispatch_selected_ok_linear = null;
	
	private boolean isOnlieMode = true;
	private boolean dataRequestSent = false;
	private int isDriverListView = 1;
	
	private Boolean isCheckListView = false;
	
	private boolean isCurrentLocation = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.aotd_dispatch_tab_listview);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		Utils.DriverOrdersList = new ArrayList<DispatchAllListModel>();
		
		mDispatchPastList 		= (ListView)findViewById(R.id.dispatch_listView);
		acceptOrders  	    	= (Button)findViewById(R.id.dispatch_accept);
		acceptOrders.setVisibility(View.GONE);
		
		selectedOrders  	    = (Button)findViewById(R.id.dispatch_selected_ok);
		cancel_btn  	    = (Button)findViewById(R.id.dispatch_selected_cancel);
		dispatch_selected_ok_linear = (LinearLayout)findViewById(R.id.dispatch_selected_ok_linear);
		
		marrDispatchPastList	= new ArrayList<DispatchAllListModel>();
		marrDispatchPastList = Allorders.pastOrders;
		
		/** Broadcast receiver for active network either available or not */ 
		this.registerReceiver(this.myWifiReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		Button rna_btn	        = (Button)findViewById(R.id.rna_btn);  
		rna_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				
				SharedPreferences loginprefs;
				loginprefs	= DispatchPastTabActivity.this.getSharedPreferences("loginprefs", 0);
				System.out.println("The user id is " + loginprefs.getString("username",""));
				System.out.println("The password  is " + loginprefs.getString("temppassword",""));
				
				String url =String.format(Utils.RNA_AUTHENTICATION_URL,loginprefs.getString("username",""),loginprefs.getString("temppassword",""));
				
				//startActivity(new Intent(DispatchPresentTabActivity.this,SignatureActivity.class).putExtra("from", "rna"));
				progressdialog = ProgressDialog.show(DispatchPastTabActivity.this,null,null);
				progressdialog.setContentView(R.layout.loader);
				 	
				AuthenticationParser mAuthParser = new AuthenticationParser(new AuthenticateHandler(), url);
				mAuthParser.start(); 	
				
			}
		});
		
		selectedOrders.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Utils.DriverOrdersList.clear();
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					Log.e("", "kkk selectedOrders "+marrDispatchPastList.size());

					for (int i = 0; i < marrDispatchPastList.size(); i++) {
						Log.e("", "kkk state "+marrDispatchPastList.get(i).getOrder_id()+" "+marrDispatchPastList.get(i).getSelectedItem());

						if (marrDispatchPastList.get(i).getSelectedItem()) {
							
							Log.e("", "state "+marrDispatchPastList.get(i).getSelectedItem());
							Utils.DriverOrdersList.add(marrDispatchPastList.get(i));
							
						}
					}
					
					if(Utils.DriverOrdersList.size() == 0){
						alertDialogWithMsg("AOTD","No Orders are available");
						return;
					}
//					isCheckListView = false;
//					selectedOrders.setVisibility(View.GONE);
//					mDataAdapter.notifyDataSetChanged();
					alertDialogForRouteOptimization("AOTD", "Choose Location for RouteOptimization");
//					if(Utils.DriverOrdersList.size()>1)						
//						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
//					getOrders();
					
				}else{
					
					alertDialogWithMsg("AOTD","No Orders are available");
					
				}
			}
		});
		
		cancel_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				isCheckListView = false;
				dispatch_selected_ok_linear.setVisibility(View.GONE);
				
				for (int i = 0; i < marrDispatchPastList.size(); i++) {
					
					marrDispatchPastList.get(i).setSelectedItem(false);
				}
				mDataAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	private BroadcastReceiver myWifiReceiver  = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if(progressdialog != null && !progressdialog.isShowing()){
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
				displayWifiState();
			}else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
				displayMobiState();
			}
			}
		}};
		
	@Override
	public void onResume(){
		super.onResume();
		
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		MainDispatchScreenTabView.title.setText("My Open Orders");
		
		if(Utils.checkNetwork(getApplicationContext()))
		{
			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.online);
			if(!dataRequestSent){
				marrDispatchPastList.clear();
				if(mDataAdapter != null)
					mDataAdapter.notifyDataSetChanged();
				getParserData();
			}
			
		}
		else
		{
			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.offline);
			mDataAdapter = null;							
			dataRequestSent = false;
			if(marrDispatchPastList!=null){
			mDataAdapter =  new DispatchPastAdapter(marrDispatchPastList);
			mDispatchPastList.setAdapter(mDataAdapter);
			}
		}
		
		
		isCheckListView = false;
		dispatch_selected_ok_linear.setVisibility(View.GONE);
	}
	@Override
	protected void onPause() {
		super.onPause();
		try{
			if(progressdialog != null && progressdialog.isShowing()){
				progressdialog.dismiss();
			}
		}catch(Exception e){
				e.printStackTrace();
		}
		
	}
private void displayWifiState(){
    	
    	ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
    	if (myNetworkInfo.isConnected()){
    		
    		if(Utils.checkNetwork(getApplicationContext()))
    			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.online);
    		else
    			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.offline);
    		if(!dataRequestSent){
    		marrDispatchPastList.clear();
			if(mDataAdapter != null)
				mDataAdapter.notifyDataSetChanged();
			getParserData();
    		}
    	}
    	else{

    	}
    	
    }
	
	private void displayMobiState(){

    	
    	ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo myNetworkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
    	if (myNetworkInfo.isConnected()){
        
    		if(Utils.checkNetwork(getApplicationContext()))
    			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.online);
    		else
    			MainDispatchScreenTabView.imgOnline.setBackgroundResource(R.drawable.offline);
    		if(!dataRequestSent){
    		marrDispatchPastList.clear();
			if(mDataAdapter != null)
				mDataAdapter.notifyDataSetChanged();
			getParserData();
    		}
//    		Toast.makeText(AndroidWifiMonitor.this, "--- CONNECTED ---",Toast.LENGTH_SHORT).show();
    	}
    	else{

    	}
	}
	private void getParserData(){
		
		/*MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.GRAY);
		MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.BLACK);
		MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.BLACK);*/
		
		if(Utils.NEW_ORDER_VIEWED)
			MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(3).setBackgroundColor(Color.BLACK);
		else
			MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(3).setBackgroundColor(Color.MAGENTA);
		
		dataRequestSent = true;
		if(Utils.checkNetwork(getApplicationContext()))
		{
			try {
				progressdialog = ProgressDialog.show(DispatchPastTabActivity.this,null,null);
				progressdialog.setContentView(R.layout.loader); 
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
			try {
				
				Log.e("Sync Data", "******************* syncData");
				new SyncData(getApplicationContext(), new getSyncHandler());
				
			} catch (Exception e) {
				// TODO: handle exception
				
				Log.e("Sync Data", "******************* syncData Error");
				e.printStackTrace();
			}
			
		}else{
			
			getOfflineData();
			
		}
		
		
	}
	
	
	private void getOnlineData(){
		
		isOnlieMode  = true;
		try
		{
			marrDispatchPastList.clear();
			String encodedRoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
			String encodedId   = URLEncoder.encode(Utils.USER_ID,"UTF-8");
			String url = String.format(Utils.PAST_DISPATCH_URL,encodedRoleName,encodedId); 			
			DispatchDeliverParser mDispatchparser = new DispatchDeliverParser(new DispatchPastHandler(), url , marrDispatchPastList);
			mDispatchparser.start();
			
		}catch(Exception e)
		{
			
		}
	}
	
	
	private void getOfflineData(){
		
		isOnlieMode  = false;
		Date today  = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String mydate =  sdf.format(today);
		Log.e("Date", "mydate "+ mydate);
		
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		marrDispatchPastList = _AOTDDataBase.getOfflineOrders(mydate, "past");
		
		if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
		
			dataRequestSent = false;
			mDataAdapter = null;							
			mDataAdapter =  new DispatchPastAdapter(marrDispatchPastList);
			Log.v("Present", " the size of teh adapter is " + marrDispatchPastList.size());
			mDispatchPastList.setAdapter(mDataAdapter);
			
		}else{
			
			alertDialogWithMsg("AOTD","No Dispatches available");
			
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		
		
		if(Utils.ROLENAME.trim().equalsIgnoreCase("superAdmin")){
			
			menu.add(0, 1, 0, "Gps").setIcon(R.drawable.menu_map_icon);
			menu.add(0, 2, 1, "List").setIcon(R.drawable.menu_list_icon);
			
		}else{
			
			Log.e("this is past tab", "this is past tab");
//			menu.add(0, 1, 0, "Gps").setIcon(R.drawable.menu_map_icon);
			menu.add(0, 1, 0, "Route Optimization").setIcon(R.drawable.menu_map_icon);

//			menu.add(0, 2, 1, "List").setIcon(R.drawable.menu_list_icon);
			
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
			
			isDriverListView  =0;
			
			break;
			
		case 2:
			
			isDriverListView =1;
			
			break;
			
		}
		
		
		
			
			if(Utils.ROLENAME.trim().equalsIgnoreCase("superAdmin")){
				if(Utils.checkNetwork(getApplicationContext())){
					progressdialog = ProgressDialog.show(DispatchPastTabActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader);
				Log.v("url",Utils.DRIVER_GEOLOCATION_URL);
				new DriverGeoLocationParser(Utils.DRIVER_GEOLOCATION_URL,new DriverGeoLocationHandler()).start();
				
				}else{
					
					Toast.makeText(getApplicationContext(), "Internet not available",Toast.LENGTH_SHORT).show();
				}
				
				
			}else{
				
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					
					alertDialogForSelection("AOTD","Select orders for Route optimization");
					
				}else{
					
					alertDialogWithMsg("AOTD","No Orders are available");
				}
		
//				if(Utils.DriverOrdersList != null && Utils.DriverOrdersList.size()>0){
//					
//					if(Utils.DriverOrdersList.size()>1)
//						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
//					
//					getOrders();
//					
//				}else{
//					
//					alertDialogWithMsg("AOTD","No Orders are available");
//					
//				}
				
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
					
					Intent intent=new Intent(DispatchPastTabActivity.this,DriverGpsScreenActivity.class).putExtra("geolocations", Utils.mDriverGeoLocations);
					startActivity(intent);
					
				}else{
					
					Intent intent=new Intent(DispatchPastTabActivity.this,DriverListScreenActivity.class).putExtra("geolocations", Utils.mDriverGeoLocations);
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
	
	private ArrayList<RouteOptamizationModel> mRouteOptamizationModelFeeds;
	private RouteOptamizationModel routeOptamizationModel;
	private ArrayList<double[]> latlong_arr;
	private ArrayList<String> citynames;
	private StringBuffer sb;
	
	private void getOrders(){
		
		if (Utils.DriverOrdersList.size() == 0) {
			
			alertDialogWithMsg("AOTD","No Orders are available");
			return;
		}
		
		Log.e("selected Array size", Utils.DriverOrdersList.size()+"");
		Log.e("selected positions", Utils.DriverOrdersList.get(0).getSelectedItem()+"");
		
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
			
			Log.e("Array size", mRouteOptamizationModelFeeds.size()+"");
			
			
			/**
			 * this code for showing List with rout optimization
			 */
			startActivity(new Intent(DispatchPastTabActivity.this,NewListOrdersRouteOptimizationActivity.class)
			.putExtra("sortdata", Utils.DriverOrdersList));
			
			
			/**
			 * this code for showing map with rout optimization
			 */
//			startActivity(new Intent(DispatchPastTabActivity.this,NewOrdersRouteOptimizationMapActivity.class)
//			.putExtra("data", mRouteOptamizationModelFeeds)
//			.putExtra("points", latlong_arr));
			
			
			/**
			 * this is old code
			 */
//			startActivity(new Intent(DispatchPastTabActivity.this,OrdersRouteOptimizationMapActivity.class)
//			.putExtra("data", mRouteOptamizationModelFeeds)
//			.putExtra("points", latlong_arr));
			
			
		}else{
			
			startActivity(new Intent(DispatchPastTabActivity.this,OrdersRouteOptimizationListActivity.class)
			.putExtra("citynames", citynames));
			
		}
		
	}
	
	public void alertDialogForSelection(String title, String msg)
	{
		
		// custom dialog
		final Dialog dialog = new Dialog(DispatchPastTabActivity.this);
		dialog.setContentView(R.layout.aotd_custom_dialog_for_selection);
		dialog.setTitle(title);

		// set the custom dialog components - text, image and button
		TextView dialog_message = (TextView) dialog.findViewById(R.id.aotd_custom_dialog_message);
		dialog_message.setText(msg);

		Button dialog_selectAll_btn = (Button) dialog.findViewById(R.id.aotd_custom_dialog_selectAll_btn);
		Button dialog_selectOneByOne_btn = (Button) dialog.findViewById(R.id.aotd_custom_dialog_selectOneByOne_btn);
		Button dialog_cancel_btn = (Button) dialog.findViewById(R.id.aotd_custom_dialog_cancel_btn);
		// if button is clicked, close the custom dialog
		dialog_selectAll_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
				
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					Utils.DriverOrdersList.clear();
					
					for(int i= 0;i < marrDispatchPastList.size();i++){
						if(!marrDispatchPastList.get(i).getSignDelivery().trim().equalsIgnoreCase("") && !marrDispatchPastList.get(i).getSignRoundTrip().trim().equalsIgnoreCase("")) 
						{
						
						}else{   
							if (marrDispatchPastList.get(i).getOrderStatus().trim().startsWith("Delivered"))  {
//								 if( marrDispatchPastList.get(i).getIsRoundTrip().trim().equalsIgnoreCase("1") && !marrDispatchPastList.get(i).getSignDelivery().trim().equalsIgnoreCase("")){
//									 
//									 Utils.DriverOrdersList.add(marrDispatchPastList.get(i));
//								 }
							}else{
								 Utils.DriverOrdersList.add(marrDispatchPastList.get(i));

							}
					
						}
						
				
//						if(!marrDispatchPastList.get(i).getSignDelivery().trim().equalsIgnoreCase("") && !marrDispatchPastList.get(i).getSignRoundTrip().trim().equalsIgnoreCase("")) 
//						{
//
//						}else{
//							Utils.DriverOrdersList.add(marrDispatchPastList.get(i));
//						}
						
					}
					
					
					Log.e("Atual Array size", "kkk Atual Array size"+Utils.DriverOrdersList.size()+"");

					alertDialogForRouteOptimization("AOTD", "Choose Location for RouteOptimization");
//					if(Utils.DriverOrdersList.size()>1)
//						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
//
//					getOrders();
					
				}else{
					
					alertDialogWithMsg("AOTD","No Orders are available");
					
				}
			}
		});
		
		dialog_selectOneByOne_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
				
				isCheckListView = true;
				
				dispatch_selected_ok_linear.setVisibility(View.VISIBLE);
				
				mDataAdapter.notifyDataSetChanged();
			}
		});
		
		dialog_cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				isCheckListView = false;
				dispatch_selected_ok_linear.setVisibility(View.GONE);
				for (int i = 0; i < marrDispatchPastList.size(); i++) {
					
					marrDispatchPastList.get(i).setSelectedItem(false);
				}

				mDataAdapter.notifyDataSetChanged();
			}
		});

		dialog.show();
				  
		
		/*new AlertDialogMsg(DispatchPastTabActivity.this, title, msg ).setPositiveButton("All", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					
					Utils.DriverOrdersList.addAll(marrDispatchPastList);
					
					Log.e("Atual Array size", Utils.DriverOrdersList.size()+"");

					if(Utils.DriverOrdersList.size()>1)
						Utils.DriverOrdersList = new GetDistance(Utils.DriverOrdersList).sortData();
					
					getOrders();
					
				}else{
					
					alertDialogWithMsg("AOTD","No Orders are available");
					
				}
			}
			
		}).setNegativeButton("Select", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				
				isCheckListView = true;
				
				selectedOrders.setVisibility(View.VISIBLE);
				
				mDataAdapter.notifyDataSetChanged();
			}
			
		}).setNeutralButton("cancel", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
			
		}).
		create().show();	*/	
		
	}
	
//	Called when the sync id done
	
	class getSyncHandler extends Handler{
		
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what == 10){
				
				Log.e("Sync Data", "******************* getOnlineOrders 10");
				getOnlineData();
				
			}else if(msg.what == 11){
				
				if(Utils.checkNetwork(getApplicationContext())){
					
					Log.e("Sync Data", "******************* getOnlineOrders 10");
					getOnlineData();
					
				}else{
					
					Log.e("Sync Data", "******************* getOfflineOrders 11");
					progressdialog.dismiss();
					dataRequestSent = false;
					getOfflineData();
				}
				
			}else if(msg.what == 12){
				
				Log.e("Sync Data", "******************* getOnlineOrders 12");
				getOnlineData();
			}
		}
		
	}
	
	
	public class DispatchPastAdapter extends BaseAdapter{
		
		ArrayList<DispatchAllListModel> mDataFeeds;
		
		public DispatchPastAdapter(ArrayList<DispatchAllListModel> mDataFeeds){    	
			
			this.mDataFeeds = mDataFeeds;
			Log.v("size ",""+mDataFeeds.size());
		}
		
		
		@Override
		public int getCount(){		
			
			return mDataFeeds.size();
			
		}
		
		@Override
		public Object getItem(int position){
			
			return position;
		}
		
		@Override
		public long getItemId(int position) {
			
			return position;
		}
		
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null){
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.aotd_dispatchscreen_listrow, null);                  
			}
			
			//RelativeLayout rl1=(RelativeLayout)v.findViewById(R.id.dispatchScreen_listview_main);
			
			TextView mTime		= (TextView)v.findViewById(R.id.dispathch_listrow_date);			
			TextView puCompany	= (TextView)v.findViewById(R.id.dispathch_listrow_PU_company);
			TextView puAddress	= (TextView)v.findViewById(R.id.dispathch_listrow_PU_address);		
			TextView dlCompany	= (TextView)v.findViewById(R.id.dispathch_listrow_DL_company);
			TextView dlAddress	= (TextView)v.findViewById(R.id.dispathch_listrow_DL_address);
			
			if(mDataFeeds.get(position).getCompany() != null)
				puCompany.setText("P/U : "+mDataFeeds.get(position).getCompany().trim());
			else{
				puCompany.setVisibility(View.GONE);
				puAddress.setVisibility(View.GONE);
			}
			
			if(mDataFeeds.get(position).getAddress() != null && mDataFeeds.get(position).getCompany() != null)
				puAddress.setText("Address: "+((mDataFeeds.get(position).getAddress().length()>0) ? mDataFeeds.get(position).getAddress()+"\n" : ""));
			else
				puAddress.setVisibility(View.GONE);
			
			if(mDataFeeds.get(position).getDlcompany() != null)
				dlCompany.setText("D/L : "+mDataFeeds.get(position).getDlcompany().trim());	
			else{
				
				dlCompany.setVisibility(View.GONE);
				dlAddress.setVisibility(View.GONE);
			}
			
			if(mDataFeeds.get(position).getDladdress() != null && mDataFeeds.get(position).getDlcompany() != null)
				dlAddress.setText("Address: "+((mDataFeeds.get(position).getDladdress().length()>0) ? mDataFeeds.get(position).getDladdress()+"\n" : ""));
			else
				dlAddress.setVisibility(View.GONE);
			
			mTime.setText(mDataFeeds.get(position).getRDDate().trim());
			mTime.setTextColor(Color.BLACK);		 
			
			final Button mOrderNum  	= (Button)v.findViewById(R.id.dispatch_order_num_btn);
			final Button mOrderStatus  = (Button)v.findViewById(R.id.dispathch_list_row_btn_order_status);
			if(!isOnlieMode)
				setOrderColorStatus(mDataFeeds.get(position));
			
			//final Button mOrderNum  = (Button)rl1.getChildAt(2);
			// final Button mOrderStatus  = (Button)rl1.getChildAt(1);
			mOrderNum.setText(mDataFeeds.get(position).getOrder_id().trim());
			mOrderNum.setTextColor(Color.BLACK); 
			
			if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("green"))
			{				 
				if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_green_rt);
					mOrderStatus.setTag("1");
				}		 
				else{
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_green);
					mOrderStatus.setTag("0");
				}	 
			}	 
			else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("red")){
				if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_red_rt);
					mOrderStatus.setTag("1");
				}		 
				else{
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_red);
					mOrderStatus.setTag("0");
				}
			}	 
			else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("orange")){
				if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_orange_rt);
					mOrderStatus.setTag("1");
				}	 
				else{
					mOrderStatus.setBackgroundResource(R.drawable.btn_pick_orange);
					mOrderStatus.setTag("0");
				}
			}
			else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("blue") ){
				if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_delivered_rt);
					mOrderStatus.setTag("1");
				}	 
				else{
					mOrderStatus.setBackgroundResource(R.drawable.btn_delivered);
					mOrderStatus.setTag("0");
					
				}
			}
			else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("white") ){				 
				mOrderStatus.setBackgroundResource(R.drawable.btn_order);
				
				mOrderStatus.setTag("0");
			}
			mOrderStatus.setText(mDataFeeds.get(position).getOrderStatus().trim());
			//Log.v("Status", position + " " + mDataFeeds.get(position).getSignDelivery().length() +  " " + mDataFeeds.get(position).getSignRoundTrip().length());
			if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && !mDataFeeds.get(position).getSignRoundTrip().trim().equalsIgnoreCase("")) 
			{	
				mOrderStatus.setEnabled(false);
				mOrderNum.setEnabled(false);
			}else{
				mOrderStatus.setEnabled(true);
				mOrderNum.setEnabled(true);
				
			}
			
			mOrderStatus.setOnClickListener(new OnClickListener() 
			{
				
				public void onClick(View v) {
					
					if(mOrderStatus.getText().toString().equalsIgnoreCase("Open Order")){
						Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoPickUp.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "true");
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Picked")){
						Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "false");
						intent.putExtra("condition", "updateSingDelivered");
						Utils.isRoundTrip = mDataFeeds.get(position).getIsRoundTrip();
						
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Delivered")){
						//condition 1 
						String roundtrip= mOrderStatus.getTag().toString();
						// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = YES	
						//take to RoundTripDeliveredScreen
						if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")   && roundtrip.equalsIgnoreCase("1") && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("1")){
							Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
							Intent intent=new Intent(DispatchPastTabActivity.this,RoundTripActivity.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateRoundTripPickup");
							startActivity(intent);
							// if isRoundTrip = no and RoundTrip = no and pickup
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
							
							roundTripAlertDialog(mOrderNum.getText().toString(), roundtrip);
							
//								Intent intent=new Intent(DispatchPastTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
//					        	startActivity(intent);
							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
							Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
						}
					}	
					
				}
			});
			
			mOrderNum.setOnClickListener(new OnClickListener() 
			{
				
				public void onClick(View v) {
					
					if(mOrderStatus.getText().toString().equalsIgnoreCase("Open Order")){
						Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoPickUp.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "true");
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Picked")){
						Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "false");
						intent.putExtra("condition", "updateSingDelivered");
						Utils.isRoundTrip = mDataFeeds.get(position).getIsRoundTrip();
						
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Delivered")){
						//condition 1 
						String roundtrip= mOrderStatus.getTag().toString();
						// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = YES	
						//take to RoundTripDeliveredScreen
						if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")   && roundtrip.equalsIgnoreCase("1") && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("1")){
							Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
							Intent intent=new Intent(DispatchPastTabActivity.this,RoundTripActivity.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateRoundTripPickup");
							startActivity(intent);
							// if isRoundTrip = no and RoundTrip = no and pickup
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
							
							roundTripAlertDialog(mOrderNum.getText().toString(), roundtrip);
							
//								Intent intent=new Intent(DispatchPastTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
//					        	startActivity(intent);
							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
							Intent intent=new Intent(DispatchPastTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
						}
					}	
				}
			});
			
			final DispatchAllListModel dispatchAllListModel = (DispatchAllListModel)mDataFeeds.get(position);
			final RelativeLayout dispathch_listrow_checkbox_rel  = (RelativeLayout)v.findViewById(R.id.dispathch_listrow_checkbox_rel);
			final CheckBox selected_checkbox  	= (CheckBox)v.findViewById(R.id.dispathch_listrow_checkbox);
			
			if (!isCheckListView) {
				
//				dispatchAllListModel.setSelectedItem(true);
				dispathch_listrow_checkbox_rel.setVisibility(View.GONE);
				
			}else {
				if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && !mDataFeeds.get(position).getSignRoundTrip().trim().equalsIgnoreCase("")) 
				{
					dispathch_listrow_checkbox_rel.setVisibility(View.GONE);

				}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && mOrderStatus.getTag().toString().equalsIgnoreCase("0")){									
					
					dispathch_listrow_checkbox_rel.setVisibility(View.GONE);
					
				}else if(mOrderStatus.getText().toString().startsWith("Delivered")){
					if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && mOrderStatus.getTag().toString().equalsIgnoreCase("0")){
						
						dispathch_listrow_checkbox_rel.setVisibility(View.GONE);
						
					}else if(mOrderStatus.isEnabled()){
						if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && mOrderStatus.getTag().toString().equalsIgnoreCase("1")){			
							/** here we are gone the checkbox to achieving when order is delivered it may be rt modified at 20thJan,14*/
//							dispathch_listrow_checkbox_rel.setVisibility(View.VISIBLE);
							dispathch_listrow_checkbox_rel.setVisibility(View.GONE);
						}else{
							dispathch_listrow_checkbox_rel.setVisibility(View.GONE);
						}
					}
					
				}else{
//					dispatchAllListModel.setSelectedItem(false);
					dispathch_listrow_checkbox_rel.setVisibility(View.VISIBLE);
					Log.e("default", " kkk else....");


				}

				selected_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						
						dispatchAllListModel.setSelectedItem(isChecked);							
						
						if(isChecked){
							selected_checkbox.setChecked(true);
							Log.e("checked value", "kkk chekced");

						}else{
							selected_checkbox.setChecked(false);								
							Log.e("checked value", "kkk unchekced");
						}
					}
				});
				
				if (mDataFeeds.get(position).getSelectedItem() == true) {
					selected_checkbox.setChecked(true);
					
					Log.e("checked value", "kkk if chekced");
				} else {
					selected_checkbox.setChecked(false);
					
					Log.e("checked value", "kkk else unchekced");
				}
				
			}
			
			return v;
		}
	}
	
	public void setOrderColorStatus(DispatchAllListModel data)
	{
		String pickupdate = data.getPUDate();
		String deliverydate = data.getDLDate();
		String orderdate = data.getRDDate();
		
		Log.e("","orderdate**** "+orderdate);
		
		Date rd_date = null;
		try {
			rd_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(orderdate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			
		}
		
		int hour = (int) Float.parseFloat(data.getHour().toString());
		Date serviceHour = new Date(rd_date.getYear(), rd_date.getMonth(), rd_date.getDate(), rd_date.getHours()+hour, rd_date.getMinutes(), rd_date.getSeconds());
		Date rd_time = new Date(serviceHour.getTime() /* + (hour*hourConstant)*/);
		Date today  = new Date();
		
		long min = (today.getTime() - rd_time.getTime())/60000;
		
		String bgcolor = "white";
		String orderStatus = "Open Order";
		
		if(!pickupdate.startsWith("0000-00-00 00:00:00"))//picked up: green
		{
			bgcolor = "green";//#44cc44";
			orderStatus = "Picked up";
		}
		
		if(!deliverydate.startsWith("0000-00-00 00:00:00"))//delivered:blue
		{
			bgcolor = "blue";//#00aaff";
			orderStatus = "Delivered"; 
		}else if(min > -10)//10min: redish
		{
			bgcolor = "red";//#ff6666";
		}
		else if(min > -30)//30min: yellow
		{
			bgcolor = "orange";//#eeccaa";	
		}
		
		data.setOrderColor(bgcolor);
		data.setOrderStatus(orderStatus);
		
	}
	
	class DispatchPastHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			if(progressdialog != null && progressdialog.isShowing()){
			progressdialog.dismiss();
			}
			
			String errorMsg=msg.getData().getString("HttpError");
			if(errorMsg.length()>0){
				
				alertDialogWithMsg("AOTD",errorMsg);				
				
			}else{
				
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					
					
					mDataAdapter = null;							
					dataRequestSent = false;
					mDataAdapter =  new DispatchPastAdapter(marrDispatchPastList);
					mDispatchPastList.setAdapter(mDataAdapter);
				
					
					
				}else{
					
					alertDialogWithMsg("AOTD","No Dispatches available");
					
				}
			}
		}
	}	
	
	
	
	public void alertDialogWithMsg(String title, String msg){	
		
		new AlertDialogMsg(DispatchPastTabActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				dataRequestSent = false;
				
			}
			
		}).create().show();		
	}
	
	public void alertDialogForRouteOptimization(String title, String msg){	
		
		new AlertDialogMsg(DispatchPastTabActivity.this, title, msg )
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

	class AuthenticateHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			progressdialog.dismiss();
			String errorMsg=msg.getData().getString("HttpError");
			String authMsg =msg.getData().getString("Authentication");
			
			//authMsg = "true";
			Log.v("authentication",authMsg);
			if(errorMsg.length()>0){
				
				alertDialogWithMsg("AOTD",errorMsg);				
				
			}else{
				if(Boolean.parseBoolean(authMsg)){					 		
					Intent rna_dispatch_intent = new Intent(DispatchPastTabActivity.this,RNATabActivity.class);
					rna_dispatch_intent.putExtra("from", "aotd_present");
					startActivity(rna_dispatch_intent);
					
				}else{
					
					alertDialogWithMsg("AOTD","You are not Authorized User");	
					
				}					
			}				
		}
	}	
	
	@Override
	public void onBackPressed() {			
		
		android.os.Process.killProcess(android.os.Process.myPid()) ; 
	}
	
	
	private void roundTripAlertDialog(String mOrderNum, String roundtrip){
		
		final String ornum = mOrderNum;
		final String rtrip = roundtrip;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("AOTD");
		builder.setMessage("Order already delivered do you wish to choose RonudTrip manually.")
		.setCancelable(false)					       
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				
				
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				
				Intent intent=new Intent(DispatchPastTabActivity.this,RoundTripActivity.class);
				intent.putExtra("orderNumber", ornum);	
				intent.putExtra("openorder", "false");
				intent.putExtra("roundtrip", rtrip);
				intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
				startActivity(intent);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
}
