package com.aotd.activities;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.aotd.activities.DispatchFutureTabActivity.DispatchPastAdapter;
import com.aotd.activities.DispatchFutureTabActivity.DispatchPastHandler;
import com.aotd.activities.DispatchFutureTabActivity.getSyncHandler;
import com.aotd.activities.DispatchPresentTabActivity.AuthenticateHandler;
import com.aotd.activities.DispatchTabActivity.DispatchAdapter;
import com.aotd.activities.DispatchTabActivity.DispatchHandler;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.AOTDDataBase;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DispatchListModel;
import com.aotd.parsers.AuthenticationParser;
import com.aotd.parsers.DispatchAllParser;
import com.aotd.parsers.DispatchParser;
import com.aotd.utils.Utils;
import com.rna.activities.RNATabActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author bharath
 *
 */


public class DispatchFutureTabActivity extends Activity {
	/** Called when the activity is first created. */
	
	private ListView mDispatchPastList = null;	
	private ProgressDialog progressdialog = null;
	private DispatchPastAdapter mDataAdapter = null;
	private ArrayList<DispatchAllListModel> marrDispatchPastList = null;
	private Button acceptOrders = null;
	
	private boolean isOnlieMode = true;
	private boolean dataRequestSent = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.aotd_dispatch_tab_listview);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		mDispatchPastList 		= (ListView)findViewById(R.id.dispatch_listView);
		acceptOrders  	    	= (Button)findViewById(R.id.dispatch_accept);
		acceptOrders.setVisibility(View.GONE);
		marrDispatchPastList	= new ArrayList<DispatchAllListModel>();
		
		Button rna_btn	     = (Button)findViewById(R.id.rna_btn);  
		rna_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				
				SharedPreferences loginprefs;
				loginprefs	= DispatchFutureTabActivity.this.getSharedPreferences("loginprefs", 0);
				System.out.println("The user id is " + loginprefs.getString("username",""));
				System.out.println("The password  is " + loginprefs.getString("temppassword",""));
				
				String url =String.format(Utils.RNA_AUTHENTICATION_URL,loginprefs.getString("username",""),loginprefs.getString("temppassword",""));
				
				//startActivity(new Intent(DispatchPresentTabActivity.this,SignatureActivity.class).putExtra("from", "rna"));
				progressdialog = ProgressDialog.show(DispatchFutureTabActivity.this,null,null);
				progressdialog.setContentView(R.layout.loader);
				 	
				AuthenticationParser mAuthParser = new AuthenticationParser(new AuthenticateHandler(), url);
				mAuthParser.start(); 	
				
			}
		});
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		MainDispatchScreenTabView.title.setText("My Open Orders");
		
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
	
	private void getParserData(){
		

		/*MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.BLACK);
		MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.BLACK);
		MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(2).setBackgroundColor(Color.GRAY);*/
		
		if(Utils.NEW_ORDER_VIEWED)
			MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(3).setBackgroundColor(Color.BLACK);
		else
			MainDispatchScreenTabView.tabHost.getTabWidget().getChildAt(3).setBackgroundColor(Color.MAGENTA);
		
		
		dataRequestSent = true;
		if(Utils.checkNetwork(getApplicationContext()))
		{
			
			progressdialog = ProgressDialog.show(DispatchFutureTabActivity.this,null,null);
			progressdialog.setContentView(R.layout.loader);
			 
			
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
			String encodedRoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
			String encodedId   = URLEncoder.encode(Utils.USER_ID,"UTF-8");
			String url = String.format(Utils.FUTURE_DISPATCH_URL,encodedRoleName,encodedId); 			
			DispatchAllParser mDispatchparser = new DispatchAllParser(new DispatchPastHandler(), url , marrDispatchPastList);
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
		marrDispatchPastList = _AOTDDataBase.getOfflineOrders(mydate, "future");
		
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
			
			// RelativeLayout rl1=(RelativeLayout)v.findViewById(R.id.dispatchScreen_listview_main);
			
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
			
			
			
//			 final Button mOrderNum  = (Button)rl1.getChildAt(2);
//			 final Button mOrderStatus  = (Button)rl1.getChildAt(1);
			
			final Button mOrderNum  	= (Button)v.findViewById(R.id.dispatch_order_num_btn);
			final Button mOrderStatus  = (Button)v.findViewById(R.id.dispathch_list_row_btn_order_status);
			
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
			Log.v("Status", position + " " + mDataFeeds.get(position).getSignDelivery().length() +  " " + mDataFeeds.get(position).getSignRoundTrip().length());
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
						Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoPickUp.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "true");
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Picked")){
						Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
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
							Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
							Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateRoundTripPickup");
							startActivity(intent);
							// if isRoundTrip = no and RoundTrip = no and pickup
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
							
							roundTripAlertDialog(mOrderNum.getText().toString(), roundtrip);
//								
////									Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
////									intent.putExtra("orderNumber", mOrderNum.getText().toString());	
////									intent.putExtra("openorder", "false");
////									intent.putExtra("roundtrip", roundtrip);
////									intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
							Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
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
						Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoPickUp.class);
						intent.putExtra("orderNumber", mOrderNum.getText().toString());	
						intent.putExtra("openorder", "true");
						startActivity(intent);
					}else if(mOrderStatus.getText().toString().startsWith("Picked")){
						Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
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
							Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
							Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateRoundTripPickup");
							startActivity(intent);
							// if isRoundTrip = no and RoundTrip = no and pickup
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
							
							roundTripAlertDialog(mOrderNum.getText().toString(), roundtrip);
//								
////									Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
////									intent.putExtra("orderNumber", mOrderNum.getText().toString());	
////									intent.putExtra("openorder", "false");
////									intent.putExtra("roundtrip", roundtrip);
////									intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
						}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
							Intent intent=new Intent(DispatchFutureTabActivity.this,DeliveryOrderInfoDelivery.class);
							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
							intent.putExtra("openorder", "false");
							intent.putExtra("roundtrip", roundtrip);
							intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
							startActivity(intent);
						}
					}	
					
				}
			});
			
			
//			 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_confirm_clk);
//			 mOrderStatus.setEnabled(false);
//			 mOrderStatus.setClickable(false);
//			 mOrderStatus.setText("Waiting");
			
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
			progressdialog.dismiss();
			
			
			String errorMsg=msg.getData().getString("HttpError");
			if(errorMsg.length()>0){
				
				alertDialogWithMsg("AOTD",errorMsg);				
				
			}else{
				
				if(marrDispatchPastList != null && marrDispatchPastList.size()>0){
					
					Utils.DriverOrdersList = marrDispatchPastList;
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
		
		new AlertDialogMsg(DispatchFutureTabActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				dataRequestSent = false;
				
			}
			
		}).create().show();		
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
					Intent rna_dispatch_intent = new Intent(DispatchFutureTabActivity.this,RNATabActivity.class);
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
				
				
				Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
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



//public class DispatchFutureTabActivity extends Activity {
///** Called when the activity is first created. */
//	
//	private ListView mDispatchFutureList = null;
//	private ProgressDialog progressdialog = null;
//	private DispatchFutureAdapter mDataAdapter = null;
//	private ArrayList<DispatchAllListModel> marrDispatchFutureList = null;
//	private Button acceptOrders = null;
//	
//	
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);		
//		setContentView(R.layout.aotd_dispatch_tab_listview);
//		
//		mDispatchFutureList 	= (ListView)findViewById(R.id.dispatch_listView);
//		acceptOrders  	    	= (Button)findViewById(R.id.dispatch_accept);
//		acceptOrders.setVisibility(View.GONE);
//		marrDispatchFutureList  = new ArrayList<DispatchAllListModel>();
//		
//		Button rna_btn	     = (Button)findViewById(R.id.rna_btn);  
//		rna_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v)
//			{
//			
//				SharedPreferences loginprefs;
//				loginprefs	= DispatchFutureTabActivity.this.getSharedPreferences("loginprefs", 0);
//				System.out.println("The user id is " + loginprefs.getString("username",""));
//				System.out.println("The password  is " + loginprefs.getString("temppassword",""));
//
//				String url =String.format(Utils.RNA_AUTHENTICATION_URL,loginprefs.getString("username",""),loginprefs.getString("temppassword",""));
//
//				//startActivity(new Intent(DispatchPresentTabActivity.this,SignatureActivity.class).putExtra("from", "rna"));
//				 progressdialog = ProgressDialog.show(DispatchFutureTabActivity.this, "", "please wait..."); 	
//				AuthenticationParser mAuthParser = new AuthenticationParser(new AuthenticateHandler(), url);
//				mAuthParser.start(); 	
//				
//			}
//		});
//
//	}
//	
//	@Override
//	public void onResume(){
//		MainDispatchScreenTabView.title.setText("My Open Orders");
//		marrDispatchFutureList.clear();
//		getParserData();
//		super.onResume();
//	}
//	
//	private void getParserData(){
//		
//		progressdialog = ProgressDialog.show(DispatchFutureTabActivity.this, "", "please wait..."); 
//		try
//		{
//			String encodedRoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
//				
//		  //  String encodedId = URLEncoder.encode("11","UTF-8");
//			String encodedId   = URLEncoder.encode(Utils.USER_ID,"UTF-8");
//		    String url = String.format(Utils.FUTURE_DISPATCH_URL,encodedRoleName,encodedId); 			
//			
//			DispatchAllParser mDispatchparser = new DispatchAllParser(new DispatchFutureHandler(), url , marrDispatchFutureList);
//		    mDispatchparser.start(); 
//		}catch(Exception e)
//		{
//			
//		}
//	}
//
//	public class DispatchFutureAdapter extends BaseAdapter{
//
//		ArrayList<DispatchAllListModel> mDataFeeds;
//    	
//    	public DispatchFutureAdapter(ArrayList<DispatchAllListModel> mDataFeeds){    	
//    		
//    		this.mDataFeeds = mDataFeeds;
//    		Log.v("size ",""+mDataFeeds.size());
//        }
//    	
//    	
//		@Override
//		public int getCount(){		
//			
//			return mDataFeeds.size();
//		}
//
//		@Override
//		public Object getItem(int position){
//			
//			return position;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			
//			return position;
//		}
//
//				
//		@Override
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			 View v = convertView;
//			 
//			 if (v == null){
//				 LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                 v = inflater.inflate(R.layout.aotd_dispatchscreen_listrow, null);                  
//             }
//			 
//			// RelativeLayout rl1=(RelativeLayout)v.findViewById(R.id.dispatchScreen_listview_main);
//			 
//			 TextView mTime		 		 = (TextView)v.findViewById(R.id.dispathch_listrow_date);			
//			
//			 mTime.setText(mDataFeeds.get(position).getRDDate().trim());
//			 mTime.setTextColor(Color.BLACK);
//					
//			 
//			 
////			 final Button mOrderNum  = (Button)rl1.getChildAt(2);
////			 final Button mOrderStatus  = (Button)rl1.getChildAt(1);
//				
//			 final Button mOrderNum  	= (Button)v.findViewById(R.id.dispatch_order_num_btn);
//			 final Button mOrderStatus  = (Button)v.findViewById(R.id.dispathch_list_row_btn_order_status);
//
//			 //final Button mOrderNum  = (Button)rl1.getChildAt(2);
//			// final Button mOrderStatus  = (Button)rl1.getChildAt(1);
//			 mOrderNum.setText(mDataFeeds.get(position).getOrder_id().trim());
//			 mOrderNum.setTextColor(Color.BLACK); 
//			 
//			 if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("green"))
//			 {				 
//				 if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_green_roundtrip);
//					 mOrderStatus.setTag("1");
//				 }		 
//				 else{
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_green);
//					 mOrderStatus.setTag("0");
//				 }	 
//			 }	 
//			 else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("red")){
//				 if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_red_roundtrip);
//					 mOrderStatus.setTag("1");
//				 }		 
//				 else{
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_red);
//					 mOrderStatus.setTag("0");
//				 }
//			 }	 
//			 else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("orange")){
//				 if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_orange_roundtrip);
//					 mOrderStatus.setTag("1");
//				 }	 
//				 else{
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_orange);
//					 mOrderStatus.setTag("0");
//				 }
//			 }
//			 else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("blue") ){
//				 if(mDataFeeds.get(position).getIsRoundTrip().trim().equalsIgnoreCase("1") && !mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")){
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_past_delivered_roundtrip);
//					 mOrderStatus.setTag("1");
//				 }	 
//				 else{
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_past_delivered);
//					 mOrderStatus.setTag("0");
//				 }
//			 }
//			 else if(mDataFeeds.get(position).getOrderColor().trim().equalsIgnoreCase("white") ){				 
//					 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_openorder);
//					 mOrderStatus.setTag("0");
//			 }
//			mOrderStatus.setText(mDataFeeds.get(position).getOrderStatus().trim());
//			Log.v("Status", position + " " + mDataFeeds.get(position).getSignDelivery().length() +  " " + mDataFeeds.get(position).getSignRoundTrip().length());
//			if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && !mDataFeeds.get(position).getSignRoundTrip().trim().equalsIgnoreCase("")) 
//			{	
//				mOrderStatus.setEnabled(false);
//				mOrderNum.setEnabled(false);
//			}else{
//				mOrderStatus.setEnabled(true);
//				mOrderNum.setEnabled(true);
//				
//			}
//				
//			mOrderStatus.setOnClickListener(new OnClickListener() 
//			{
//			
//					public void onClick(View v) {
//						
//						if(mOrderStatus.getText().toString().equalsIgnoreCase("Open Order")){
//							Intent intent=new Intent(DispatchFutureTabActivity.this,PickUpActivity.class);
//							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//							intent.putExtra("openorder", "true");
//				        	startActivity(intent);
//						}else if(mOrderStatus.getText().toString().startsWith("Picked")){
//							Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//							intent.putExtra("openorder", "false");
//							intent.putExtra("condition", "updateSingDelivered");
//							
//				        	startActivity(intent);
//						}else if(mOrderStatus.getText().toString().startsWith("Delivered")){
//							//condition 1 
//							String roundtrip= mOrderStatus.getTag().toString();
//							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = YES	
//							//take to RoundTripDeliveredScreen
//							if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")   && roundtrip.equalsIgnoreCase("1") && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("1")){
//								Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
//								startActivity(intent);
//							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
//								Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickup");
//					        	startActivity(intent);
//					        // if isRoundTrip = no and RoundTrip = no and pickup
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
//								
//								Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
//					        	startActivity(intent);
//							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
//								Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
//					        	startActivity(intent);
//							}
//						}	
//			
//					}
//			 });
//			
//			mOrderNum.setOnClickListener(new OnClickListener() 
//			{
//			
//					public void onClick(View v) {
//						
//						if(mOrderStatus.getText().toString().equalsIgnoreCase("Open Order")){
//							Intent intent=new Intent(DispatchFutureTabActivity.this,PickUpActivity.class);
//							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//							intent.putExtra("openorder", "true");
//				        	startActivity(intent);
//						}else if(mOrderStatus.getText().toString().startsWith("Picked")){
//							Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//							intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//							intent.putExtra("openorder", "false");
//							intent.putExtra("condition", "updateSingDelivered");
//							
//				        	startActivity(intent);
//						}else if(mOrderStatus.getText().toString().startsWith("Delivered")){
//							//condition 1 
//							String roundtrip= mOrderStatus.getTag().toString();
//							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = YES	
//							//take to RoundTripDeliveredScreen
//							if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")   && roundtrip.equalsIgnoreCase("1") && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("1")){
//								Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
//								startActivity(intent);
//							// if isRoundTrip = yes and Delivered(FirstSign) and roundTripPickedUp = No	
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")  && mDataFeeds.get(position).getPCRoundTrip().trim().equalsIgnoreCase("0")){			
//								Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickup");
//					        	startActivity(intent);
//					        // if isRoundTrip = no and RoundTrip = no and pickup
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("") && roundtrip.equalsIgnoreCase("0")){									
//								
//								Intent intent=new Intent(DispatchFutureTabActivity.this,RoundTripActivity.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateRoundTripPickupByDriver");
//					        	startActivity(intent);
//							//if isRoundTrip = yes, driver chooses roundtrip yes, and Delivered(FirstSign)
//							}else if(!mDataFeeds.get(position).getSignDelivery().trim().equalsIgnoreCase("")  && roundtrip.equalsIgnoreCase("1")){			
//								Intent intent=new Intent(DispatchFutureTabActivity.this,DispatchDeliveryInfo.class);
//								intent.putExtra("orderNumber", mOrderNum.getText().toString());	
//								intent.putExtra("openorder", "false");
//								intent.putExtra("roundtrip", roundtrip);
//								intent.putExtra("condition", "UpdateSecondSignatureForDeliver");
//					        	startActivity(intent);
//							}
//						}	
//			
//					}
//			 });
//			 
//			 
////			 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_confirm_clk);
////			 mOrderStatus.setEnabled(false);
////			 mOrderStatus.setClickable(false);
////			 mOrderStatus.setText("Waiting");
//				
//			return v;
//		}
//		
//}
//	
//	class DispatchFutureHandler extends Handler
//	{
//		public void handleMessage(android.os.Message msg)
//		{
//			progressdialog.dismiss();
//			String errorMsg=msg.getData().getString("HttpError");
//			if(errorMsg.length()>0){
//				
//				alertDialogWithMsg("AOTD",errorMsg);				
//				
//			}else{
//				  
//					if(marrDispatchFutureList != null && marrDispatchFutureList.size()>0){
//						mDataAdapter = null;							
//					
//						mDataAdapter =  new DispatchFutureAdapter(marrDispatchFutureList);
//						
//						mDispatchFutureList.setAdapter(mDataAdapter);
//						
//	
//					 }else{
//						alertDialogWithMsg("AOTD","No Dispatches available");
//						
//					 }
//			
//				
//			}
//			
//
//				
//		}
//}	
//	
//	public void alertDialogWithMsg(String title, String msg){	
//		 
//			new AlertDialogMsg(DispatchFutureTabActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
//		
//				@Override
//				public void onClick(DialogInterface dialog, int which){
//					
//							
//				}
//				
//			 }).create().show();		
//	}
//	 
//	@Override
//	public void onBackPressed() {			
//				
//			android.os.Process.killProcess(android.os.Process.myPid()) ; 
//		}
//	
//	class AuthenticateHandler extends Handler
//	{
//		public void handleMessage(android.os.Message msg)
//		{
//			progressdialog.dismiss();
//			String errorMsg=msg.getData().getString("HttpError");
//			String authMsg =msg.getData().getString("Authentication");
//			//authMsg = "true";
//			Log.v("authentication",authMsg);
//			if(errorMsg.length()>0){
//				
//				alertDialogWithMsg("AOTD",errorMsg);				
//				
//			}else{
//				 	if(Boolean.parseBoolean(authMsg)){					 		
//				 		Intent rna_dispatch_intent = new Intent(DispatchFutureTabActivity.this,RNATabActivity.class);
//				 		rna_dispatch_intent.putExtra("from", "aotd_present");
//				 		startActivity(rna_dispatch_intent);
//				 		
//				 	}else{
//				 		alertDialogWithMsg("AOTD","You are not Authorized User");	
//					 }					
//				}				
//		}
//}	
//
//}

