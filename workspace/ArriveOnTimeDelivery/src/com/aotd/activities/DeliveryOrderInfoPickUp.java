package com.aotd.activities;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.AOTDDataBase;
import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.OfflineModel;
import com.aotd.model.Question;
import com.aotd.parsers.DispatchDeliveryParser;
import com.aotd.parsers.PickupParser;
import com.aotd.utils.Utils;


public class DeliveryOrderInfoPickUp extends Activity {
	
	private Button 		pickup_btn, scan_btn, pickup_botton,scan_bottom, btn_camera;
	private	TextView 	
			orderNum, accountName, accountNotes, pickUp_from, delivery_to, 
			rfc, puinstruction, dlinstruction, serviceType,	pickupReady,	
			requestedBy, pieces, weight, adminNotes;	
	
	private DetailDeliveryModel mDetailDeliveryModel = null;
	private TextView   isRoundtrip   =null;
	
	private String openorderStatus;
	private ProgressDialog progressdialog  =null;
	private String data;
	String orderId = "";
	private String pickupAction = "get";
	public ImageView imgOnline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.aotd_delivery_order_info_pickup);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		orderId = getIntent().getStringExtra("orderNumber");
		
		intializeUI(); 
		pickupAction = "get";
		
		Bundle bundleObject = getIntent().getExtras();
		final ArrayList<DispatchAllListModel> classObject = (ArrayList<DispatchAllListModel>)bundleObject.getSerializable("key");
	
			
	
		if(Utils.checkNetwork(getApplicationContext()))
		{
			progressdialog = ProgressDialog.show(DeliveryOrderInfoPickUp.this,null,null);
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
			
			Question.mListClass = classObject;
			SetDataOffline(classObject.get(0));
			
		}
		
		pickup_botton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				pickupAction = "set";
				
				
				if(Utils.checkNetwork(getApplicationContext()))
				{
					
					progressdialog = ProgressDialog.show(DeliveryOrderInfoPickUp.this,null,null);
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
										//setPickupOffline();
				
					
					Question.mListClass = classObject;
					OfflineDB db = new OfflineDB(DeliveryOrderInfoPickUp.this);
					
					db.addData("Picked up",Question.mListClass.get(0));
					
					Intent in_pending = new Intent(DeliveryOrderInfoPickUp.this, PendingOrders.class);
					startActivity(in_pending);
					finish();
					
				}
			}
		});
		pickup_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				pickupAction = "set";
				
				
				if(Utils.checkNetwork(getApplicationContext()))
				{
					
					progressdialog = ProgressDialog.show(DeliveryOrderInfoPickUp.this,null,null);
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
										//setPickupOffline();
				
					
					Question.mListClass = classObject;
					OfflineDB db = new OfflineDB(DeliveryOrderInfoPickUp.this);
					
					db.addData("Picked up",Question.mListClass.get(0));
					
					Intent in_pending = new Intent(DeliveryOrderInfoPickUp.this, PendingOrders.class);
					startActivity(in_pending);
					finish();
					
				}
			}
			});
		btn_camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(DeliveryOrderInfoPickUp.this,CameraActivity.class);
				in.putExtra("ORDERID", orderId);
				startActivity(in);
			}
		});
		
		scan_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent("com.vinscan.barcode.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});
		
		scan_bottom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.vinscan.barcode.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
				
			}
		});
		
		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClicked)
				{
					if(Utils.NetworkType(DeliveryOrderInfoPickUp.this).equalsIgnoreCase(Utils.wifi))
					{
						Utils.wifiOFF(DeliveryOrderInfoPickUp.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					else if(Utils.NetworkType(DeliveryOrderInfoPickUp.this).equalsIgnoreCase(Utils.mobile))
					{
						Utils.mobileDataOFF(DeliveryOrderInfoPickUp.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					
				}
				else
				{
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(DeliveryOrderInfoPickUp.this);
				}

				return false;
			}
		});
		
		
	}
	
	boolean isClicked = false;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Utils.NetworkType(DeliveryOrderInfoPickUp.this).equalsIgnoreCase(Utils.wifi))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else if(Utils.NetworkType(DeliveryOrderInfoPickUp.this).equalsIgnoreCase(Utils.mobile))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else
		{
			imgOnline.setBackgroundResource(R.drawable.offline);
			isClicked = false;
		}
	}
	
	private void getPickupDataOnline(){		
		try
		{
			String encodedUserId   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
			
			String encodedorderId  = URLEncoder.encode(getIntent().getStringExtra("orderNumber"),"UTF-8");
			
			String url = String.format(Utils.DISPATCH_DELIVERY_URL,encodedUserId,encodedorderId);
			
			DispatchDeliveryParser mDispatchparser = new DispatchDeliveryParser(new PickUpHandler(), url , mDetailDeliveryModel);
			mDispatchparser.start(); 
			
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), "Error in porcessing login details, contact support", Toast.LENGTH_LONG).show();
		}
	}
	
	private void getPickupDataOffline(){
		
		//AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		//mDetailDeliveryModel= _AOTDDataBase.getOrderDetails(orderId);
		setData();
	}
	
	private void setPickupOnline(){
		
		try{
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date_time = sdf.format(new Date());
			
			String encodedURoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
			String encodedOrderIds = URLEncoder.encode(getIntent().getStringExtra("orderNumber"),"UTF-8");
			
			//data = "roleName="+encodedURoleName+"&order_ids="+encodedOrderIds;
			data = "roleName="+encodedURoleName+"&order_ids="+encodedOrderIds+"&datetime="+date_time;
			
		}catch(Exception e){
			
			Toast.makeText(getApplicationContext(), "Error in porcessing login details, contact support", Toast.LENGTH_LONG).show();
		}
		
		PickupParser mResponseParser = null;
		mResponseParser = new PickupParser(Utils.PICKUP_URL,new PickUpHandler());				
		mResponseParser.isPost=true;
		mResponseParser.postData = data;
		mResponseParser.start();
		
	}
	
	private void setPickupOffline(){
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String order_id = getIntent().getStringExtra("orderNumber");
		String mydate =  sdf.format(date);
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(DeliveryOrderInfoPickUp.this);
		_AOTDDataBase.updatePickup(order_id,mydate);
		alertDialogWithMsg("AOTD","Order Picked-up updted successfully");
		
	}
	
//	Called when the sync id done
	
	class getSyncHandler extends Handler{
		
		public void handleMessage(android.os.Message msg)
		{
			
			if(msg.what == 10){
				
				Log.e("Sync Data", "******************* getOnlineOrders 10");
				
				if(pickupAction.equalsIgnoreCase("get"))
					getPickupDataOnline();
				else
					setPickupOnline();
				
			}else if(msg.what == 11){
				
				Log.e("Sync Data", "******************* getOfflineOrders 11");
				progressdialog.dismiss();
				if(pickupAction.equalsIgnoreCase("get"))
					//getPickupDataOffline();
					getPickupDataOnline();
				else{
				//	setPickupOffline();
					setPickupOnline();
				}
				
			}else if(msg.what == 12){
				
				Log.e("Sync Data", "******************* getOnlineOrders 12");
				if(pickupAction.equalsIgnoreCase("get"))
					getPickupDataOnline();
				else
					setPickupOnline();
			}
		}
	}
	
	private void intializeUI() {
		
		openorderStatus =   getIntent().getExtras().getString("openorder"); 
		
		pickup_btn  	=   (Button)findViewById(R.id.aotd_delivery_delivery_btn_pup);
		scan_btn  		=   (Button)findViewById(R.id.aotd_delivery_scan_btn_pup);
		
		orderNum		=	(TextView)findViewById(R.id.aotd_delivery_orderNum_btn_pup);
		accountName		=   (TextView)findViewById(R.id.aotd_delivery_accountName_txt_pup);
		accountNotes	=   (TextView)findViewById(R.id.aotd_delivery_accountNote_txt_pup);
		rfc				= 	(TextView)findViewById(R.id.aotd_delivery_rfc_txt_pup);	
		pickUp_from		=   (TextView)findViewById(R.id.aotd_delivery_PUaddress_txt_pup);
		delivery_to		=   (TextView)findViewById(R.id.aotd_delivery_DLaddress_txt_pup);
		puinstruction	=	(TextView)findViewById(R.id.aotd_delivery_PUInstruction_txt_pup);
		dlinstruction  	=   (TextView)findViewById(R.id.aotd_delivery_DLInstruction_txt_pup);
		serviceType    	=   (TextView)findViewById(R.id.aotd_delivery_serviceType_txt_pup);
		pickupReady		=   (TextView)findViewById(R.id.aotd_delivery_pickupReady_txt_pup);
		requestedBy		=   (TextView)findViewById(R.id.aotd_delivery_requiredBy_txt_pup);
		pieces		 	=   (TextView)findViewById(R.id.aotd_delivery_piece_txt_pup);
		weight			=   (TextView)findViewById(R.id.aotd_delivery_weight_txt_pup);		
		isRoundtrip    	=   (TextView)findViewById(R.id.aotd_roundtrip_pup);
		adminNotes		=   (TextView)findViewById(R.id.aotd_delivery_adminNote_txt_pup);
		pickup_botton   =   (Button)findViewById(R.id.aotd_pickup_bottom);
		scan_bottom     =   (Button)findViewById(R.id.atod_scan_bottom);
		btn_camera = (Button)findViewById(R.id.pick_camera);
		
		imgOnline 		= 	(ImageView)findViewById(R.id.aotd_img_mode);
		
		mDetailDeliveryModel = new DetailDeliveryModel();		
		
		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
		
	}
	
	private void SetDataOffline(DispatchAllListModel mList)
	{

		
		orderNum.setText(getIntent().getStringExtra("orderNumber").trim());
		accountName.setText(mList.getAccountName().trim());
		rfc.setText("");
		
	
		final String sFrom = mList.getAddress()+" "+
				mList.getCity()+" "+
				mList.getState()+" "+
				mList.getZip();
		
		final String sTo =  mList.getDladdress()+" "+
				mList.getDlcity()+" "+
				mList.getDlstate()+" "+
				mList.getDlzip();
		
		String dl_Company = mList.getDlcompany();
		String dl_Address= mList.getDladdress();
		String dl_Suite= mList.getDlsuit();
		String dl_City= mList.getDlcity();
		String dl_State= mList.getDlstate();
		String dl_Zip= mList.getDlzip();
		
		String pu_Company = mList.getCompany();
		String pu_Address= mList.getAddress();
		String pu_Suite= mList.getSuit();
		String pu_City= mList.getCity();
		String pu_State= mList.getState();
		String pu_Zip= mList.getZip();
		
		
		TextView tv = (TextView)findViewById(R.id.showMap);
		
		TextView tvDLCOmpany = (TextView)findViewById(R.id.DL_Company); tvDLCOmpany.setText("Company: "+dl_Company);
		TextView tvDLAddress= (TextView)findViewById(R.id.DL_Address);tvDLAddress.setText("Address: "+dl_Address);
		TextView tvDlSite = (TextView)findViewById(R.id.DL_Suite);tvDlSite.setText("Suite: "+dl_Suite);
		TextView tvDLCity = (TextView)findViewById(R.id.DL_City);tvDLCity.setText("City: "+dl_City);
		TextView tvDLState = (TextView)findViewById(R.id.DL_State);tvDLState.setText("State: "+dl_State);
		TextView tvDLZip = (TextView)findViewById(R.id.DL_Zip);tvDLZip.setText("Zip: "+dl_Zip);
		
		TextView tvPUCOmpany = (TextView)findViewById(R.id.PU_Company); tvPUCOmpany.setText("Company: "+pu_Company);
		TextView tvPUAddress= (TextView)findViewById(R.id.PU_Address);tvPUAddress.setText("Address: "+pu_Address);
		TextView tvPUSite = (TextView)findViewById(R.id.PU_Suite);tvPUSite.setText("Suite: "+pu_Suite);
		TextView tvPUCity = (TextView)findViewById(R.id.PU_City);tvPUCity.setText("City: "+pu_City);
		TextView tvPUState = (TextView)findViewById(R.id.PU_State);tvPUState.setText("State: "+pu_State);
		TextView tvPUZip = (TextView)findViewById(R.id.PU_Zip);tvPUZip.setText("Zip: "+pu_Zip);
		
		final TextView tvPh1 = (TextView)findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView)findViewById(R.id.phone2);
		
		final TextView tvFPh1 = (TextView)findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView)findViewById(R.id.Fphone2);
		
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		if(mList.getDlCellPhone().length()>0)
		{
			tvPh1.setText(mList.getDlCellPhone());
		}
		
		if(mList.getDlHomePhone().length()>0)
		{
			tvPh2.setText(mList.getDlHomePhone());
		}
		
		if(mList.getPuCellPhone().length()>0)
		{
			tvFPh1.setText(mList.getPuCellPhone());
		}
		
		if(mList.getPuHomephone().length()>0)
		{
			tvFPh2.setText(mList.getPuHomephone());
		}
		
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(DeliveryOrderInfoPickUp.this,MapView.class);
				in.putExtra("FROM", sFrom);
				in.putExtra("TO", sTo);
				in.putExtra("ACTIVITY", "Info");
				
				startActivity(in);
				
			}
		});
		
		/*final TextView tvPh1 = (TextView)findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView)findViewById(R.id.phone2);
		
		final TextView tvFPh1 = (TextView)findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView)findViewById(R.id.Fphone2);*/
		
		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		if(mList.getDlCellPhone().length()>0)
		{
			tvPh1.setText(mList.getDlCellPhone());
		}
		
		if(mList.getDlHomePhone().length()>0)
		{
			tvPh2.setText(mList.getDlHomePhone());
		}
		
		if(mList.getPuCellPhone().length()>0)
		{
			tvFPh1.setText(mList.getPuCellPhone());
		}
		
		if(mList.getPuHomephone().length()>0)
		{
			tvFPh2.setText(mList.getPuHomephone());
		}
		
		tvPh1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
			    .setTitle("Phone Call")
			    .setCancelable(false)
			    .setMessage("Are you sure you want to make call to "+tvPh1.getText().toString()+" ?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // continue with delete
			        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
			        	String currentDateandTime = sdf.format(new Date());
			        	
			        	Question.setDLPhoneCall("Phone Call made to "+tvPh1.getText().toString()+" at "+currentDateandTime);
			        	Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" +tvPh1.getText().toString()));
						startActivity(intent);
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			   
			     .show();
				
				
			}
		});
		
tvPh2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
			    .setTitle("Phone Call")
			    .setCancelable(false)
			    .setMessage("Are you sure you want to make call to "+tvPh2.getText().toString()+" ?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // continue with delete
			        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
			        	String currentDateandTime = sdf.format(new Date());
			        	
			        	Question.setDLHomeCall("Phone Call made to "+tvPh2.getText().toString()+" at "+currentDateandTime);
			        	Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" +tvPh2.getText().toString()));
						startActivity(intent);
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			   
			     .show();
				
				
			}
		});

tvFPh1.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
	    .setTitle("Phone Call")
	    .setCancelable(false)
	    .setMessage("Are you sure you want to make call to "+tvFPh1.getText().toString()+" ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
	        	String currentDateandTime = sdf.format(new Date());
	        	
	        	Question.setPUPhoneCall("Phone Call made to "+tvFPh1.getText().toString()+" at "+currentDateandTime);
	        	Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" +tvFPh1.getText().toString()));
				startActivity(intent);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	   
	     .show();
		
		
	}
});

tvFPh2.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
	    .setTitle("Phone Call")
	    .setCancelable(false)
	    .setMessage("Are you sure you want to make call to "+tvFPh2.getText().toString()+" ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
	        	String currentDateandTime = sdf.format(new Date());
	        	
	        	Question.setPUHomeCall("Phone Call made to "+tvFPh2.getText().toString()+" at "+currentDateandTime);
	        	Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" +tvFPh2.getText().toString()));
				startActivity(intent);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	   
	     .show();
		
		
	}
});
		
		
		serviceType.setText(mList.getHour().trim());
		pickupReady.setText(mList.getRDDate().trim());
		requestedBy.setText("");
		pieces.setText(mList.getPeice().trim());
		weight.setText(mList.getWeight().trim());
		
		
		
			/*puinstruction.setText(mDetailDeliveryModel.getPUInstruction().trim());
			puinstruction.setText(" Not Provided");
		
		if(mList.getDLInstruction().length()>1)
			dlinstruction.setText(mDetailDeliveryModel.getDLInstruction().trim());
		else
			dlinstruction.setText(" Not Provided");*/
		
		
			accountNotes.setText(mList.getAccountnotes());
			adminNotes.setText(mList.getAdminnotes());
			isRoundtrip.setText(mList.getIsRoundTrip());
			rfc.setText(mList.getRef());
			requestedBy.setText(mList.getRequestor());
			puinstruction.setText(mList.getPuinstruction());
			puinstruction.setText(mList.getDlinstruction());
		
	
	}
	
	private void setData() {
		
		orderNum.setText(getIntent().getStringExtra("orderNumber").trim());
		accountName.setText(mDetailDeliveryModel.getAccountName().trim());
		rfc.setText(mDetailDeliveryModel.getRef().trim());
		
	
		final String sFrom = mDetailDeliveryModel.getPUaddress()+" "+
				mDetailDeliveryModel.getPUcity()+" "+
				mDetailDeliveryModel.getPUstate()+" "+
				mDetailDeliveryModel.getPUzip();
		
		final String sTo =  mDetailDeliveryModel.getDLaddress()+" "+
				mDetailDeliveryModel.getDLcity()+" "+
				mDetailDeliveryModel.getDLstate()+" "+
				mDetailDeliveryModel.getDLzip();
		
		String dl_Company = mDetailDeliveryModel.getDLcompany();
		String dl_Address= mDetailDeliveryModel.getDLaddress();
		String dl_Suite= mDetailDeliveryModel.getDLsuit();
		String dl_City= mDetailDeliveryModel.getDLcity();
		String dl_State= mDetailDeliveryModel.getDLstate();
		String dl_Zip= mDetailDeliveryModel.getDLzip();
		
		String pu_Company = mDetailDeliveryModel.getPUcompany();
		String pu_Address= mDetailDeliveryModel.getPUaddress();
		String pu_Suite= mDetailDeliveryModel.getPUsuit();
		String pu_City= mDetailDeliveryModel.getPUcity();
		String pu_State= mDetailDeliveryModel.getPUstate();
		String pu_Zip= mDetailDeliveryModel.getPUzip();
		
		
		TextView tv = (TextView)findViewById(R.id.showMap);
		
		TextView tvDLCOmpany = (TextView)findViewById(R.id.DL_Company); tvDLCOmpany.setText("Company: "+dl_Company);
		TextView tvDLAddress= (TextView)findViewById(R.id.DL_Address);tvDLAddress.setText("Address: "+dl_Address);
		TextView tvDlSite = (TextView)findViewById(R.id.DL_Suite);tvDlSite.setText("Suite: "+dl_Suite);
		TextView tvDLCity = (TextView)findViewById(R.id.DL_City);tvDLCity.setText("City: "+dl_City);
		TextView tvDLState = (TextView)findViewById(R.id.DL_State);tvDLState.setText("State: "+dl_State);
		TextView tvDLZip = (TextView)findViewById(R.id.DL_Zip);tvDLZip.setText("Zip: "+dl_Zip);
		
		TextView tvPUCOmpany = (TextView)findViewById(R.id.PU_Company); tvPUCOmpany.setText("Company: "+pu_Company);
		TextView tvPUAddress= (TextView)findViewById(R.id.PU_Address);tvPUAddress.setText("Address: "+pu_Address);
		TextView tvPUSite = (TextView)findViewById(R.id.PU_Suite);tvPUSite.setText("Suite: "+pu_Suite);
		TextView tvPUCity = (TextView)findViewById(R.id.PU_City);tvPUCity.setText("City: "+pu_City);
		TextView tvPUState = (TextView)findViewById(R.id.PU_State);tvPUState.setText("State: "+pu_State);
		TextView tvPUZip = (TextView)findViewById(R.id.PU_Zip);tvPUZip.setText("Zip: "+pu_Zip);
		
		
		
		final TextView tvPh1 = (TextView)findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView)findViewById(R.id.phone2);
		
		final TextView tvFPh1 = (TextView)findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView)findViewById(R.id.Fphone2);
		
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		if(mDetailDeliveryModel.getDLcellPhone().length()>0)
		{
			tvPh1.setText(mDetailDeliveryModel.getDLcellPhone());
		}
		
		if(mDetailDeliveryModel.getDLhomephone().length()>0)
		{
			tvPh2.setText(mDetailDeliveryModel.getDLhomephone());
		}
		
		if(mDetailDeliveryModel.getPUcellPhone().length()>0)
		{
			tvFPh1.setText(mDetailDeliveryModel.getPUcellPhone());
		}
		
		if(mDetailDeliveryModel.getPUhomephone().length()>0)
		{
			tvFPh2.setText(mDetailDeliveryModel.getPUhomephone());
		}
		
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(DeliveryOrderInfoPickUp.this,MapView.class);
				in.putExtra("FROM", sFrom);
				in.putExtra("TO", sTo);
				in.putExtra("ACTIVITY", "Info");
				
				startActivity(in);
				
			}
		});
		
		/*final TextView tvPh1 = (TextView)findViewById(R.id.phone1);
		final TextView tvPh2 = (TextView)findViewById(R.id.phone2);
		
		final TextView tvFPh1 = (TextView)findViewById(R.id.Fphone1);
		final TextView tvFPh2 = (TextView)findViewById(R.id.Fphone2);*/
		
		tvPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvFPh2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		tv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		if(mDetailDeliveryModel.getDLcellPhone().length()>0)
		{
			tvPh1.setText(mDetailDeliveryModel.getDLcellPhone());
		}
		
		if(mDetailDeliveryModel.getDLhomephone().length()>0)
		{
			tvPh2.setText(mDetailDeliveryModel.getDLhomephone());
		}
		
		if(mDetailDeliveryModel.getPUcellPhone().length()>0)
		{
			tvFPh1.setText(mDetailDeliveryModel.getPUcellPhone());
		}
		
		if(mDetailDeliveryModel.getPUhomephone().length()>0)
		{
			tvFPh2.setText(mDetailDeliveryModel.getPUhomephone());
		}
		
		tvPh1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
			    .setTitle("Phone Call")
			    .setCancelable(false)
			    .setMessage("Are you sure you want to make call to "+tvPh1.getText().toString()+" ?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // continue with delete
			        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
			        	String currentDateandTime = sdf.format(new Date());
			        	
			        	Question.setDLPhoneCall("Phone Call made to "+tvPh1.getText().toString()+" at "+currentDateandTime);
			        	Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" +tvPh1.getText().toString()));
						startActivity(intent);
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			   
			     .show();
				
				
			}
		});
		
tvPh2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
			    .setTitle("Phone Call")
			    .setCancelable(false)
			    .setMessage("Are you sure you want to make call to "+tvPh2.getText().toString()+" ?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // continue with delete
			        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
			        	String currentDateandTime = sdf.format(new Date());
			        	
			        	Question.setDLHomeCall("Phone Call made to "+tvPh2.getText().toString()+" at "+currentDateandTime);
			        	Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" +tvPh2.getText().toString()));
						startActivity(intent);
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			   
			     .show();
				
				
			}
		});

tvFPh1.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
	    .setTitle("Phone Call")
	    .setCancelable(false)
	    .setMessage("Are you sure you want to make call to "+tvFPh1.getText().toString()+" ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
	        	String currentDateandTime = sdf.format(new Date());
	        	
	        	Question.setPUPhoneCall("Phone Call made to "+tvFPh1.getText().toString()+" at "+currentDateandTime);
	        	Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" +tvFPh1.getText().toString()));
				startActivity(intent);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	   
	     .show();
		
		
	}
});

tvFPh2.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DeliveryOrderInfoPickUp.this)
	    .setTitle("Phone Call")
	    .setCancelable(false)
	    .setMessage("Are you sure you want to make call to "+tvFPh2.getText().toString()+" ?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
	        	String currentDateandTime = sdf.format(new Date());
	        	
	        	Question.setPUHomeCall("Phone Call made to "+tvFPh2.getText().toString()+" at "+currentDateandTime);
	        	Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" +tvFPh2.getText().toString()));
				startActivity(intent);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	   
	     .show();
		
		
	}
});
		
		
		serviceType.setText(mDetailDeliveryModel.getServiceName().trim());
		pickupReady.setText(mDetailDeliveryModel.getRDDate().trim());
		requestedBy.setText(mDetailDeliveryModel.getRequestor().trim());
		pieces.setText(mDetailDeliveryModel.getPiece().trim());
		weight.setText(mDetailDeliveryModel.getWeight().trim());
		
		
		if(mDetailDeliveryModel.getPUInstruction().length()>1)
			puinstruction.setText(mDetailDeliveryModel.getPUInstruction().trim());
		else
			puinstruction.setText(" Not Provided");
		
		if(mDetailDeliveryModel.getDLInstruction().length()>1)
			dlinstruction.setText(mDetailDeliveryModel.getDLInstruction().trim());
		else
			dlinstruction.setText(" Not Provided");
		
		if(mDetailDeliveryModel.getAccountnotes().length()>1)
			accountNotes.setText(mDetailDeliveryModel.getAccountnotes().trim());
		else
			accountNotes.setText(" This is Test Notes");
		
		if(mDetailDeliveryModel.getAdminNotes().length()>1)
			adminNotes.setText(mDetailDeliveryModel.getAdminNotes().trim());
		else
			adminNotes.setText(" This is Test Notes");
		
		
		if (mDetailDeliveryModel.getRoundTrip().trim().equalsIgnoreCase("0"))
			isRoundtrip.setText("No");
		else
			isRoundtrip.setText("Yes");
		
	}	
	
	
	
	class PickUpHandler extends Handler{
		
		public void handleMessage(android.os.Message msg)
		{
			progressdialog.dismiss();				
			
			if(msg.what == 1){
				
				String errorMsg=msg.getData().getString("HttpError");
				if(errorMsg.length()>0){	
					
					alertDialogWithMsg("AOTD",errorMsg);
					
				}else{
					
					mDetailDeliveryModel = (DetailDeliveryModel) msg.getData().getSerializable("dispatchdetail");
					if(mDetailDeliveryModel!=null)
						setData();
					
				}
				
				
			}else{
				
				String errorMsg=msg.getData().getString("HttpError");
				if(errorMsg.length()>0){	
					
					alertDialogWithMsg("AOTD",errorMsg);
					
				}else{	
					String successDate=msg.getData().getString("success");
					AOTDDataBase _AOTDDataBase = new AOTDDataBase(DeliveryOrderInfoPickUp.this);
					_AOTDDataBase.updatePickup(orderId,successDate);
					alertDialogWithMsg("AOTD","Order Picked-up updated successfully");
					
					
				}  
			}
		}
	}
	
	public void alertDialogWithMsg(String title, String msg){	
		
		new AlertDialogMsg(DeliveryOrderInfoPickUp.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				System.out.println("The dialog box was clicked");
				finish();
			}		
			
		}).create().show();		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if (requestCode == 0) {
			
			if (resultCode == RESULT_OK) {
				
				String contents = intent.getStringExtra("batchid");
				
				try{
					
					Log.d("contents", "contents "+contents);
					
					if(contents.equalsIgnoreCase("none")){
						
						alertDialogWithOneMsg("AOTD", "Wrong Order Id Scan Please try again.");
						
					}else{
						
						char subId=contents.substring(contents.length()-1).toCharArray()[0];
						
						String scanOrderId = null;
						
						if (subId >= '0' && subId <= '9'){
							
							scanOrderId = contents;
							
						}else{
							
							scanOrderId = contents.substring(0,contents.length()-1);
						}
						
						if(scanOrderId.trim().equals(getIntent().getStringExtra("orderNumber").trim())){
							
							pickupAction = "set";
							
							if(Utils.checkNetwork(getApplicationContext()))
							{
								progressdialog = ProgressDialog.show(DeliveryOrderInfoPickUp.this,null,null);
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
								
								setPickupOffline();
								
							}
							
						}else{
							
							alertDialogWithOneMsg("AOTD", "Wrong Order Id Scan Please try again.");
							
						}
					}
					
				}catch(Exception e){
					
					e.printStackTrace();
					
				}
				
				// Toast.makeText(this,format+" "+contents, Toast.LENGTH_LONG).show();
				
			} else if (resultCode == RESULT_CANCELED) {
				
				Log.d("Cancle", "scanning cancle");
			}
		}
	}
	
	public void alertDialogWithOneMsg(String title, String msg){	
		 
		new AlertDialogMsg(DeliveryOrderInfoPickUp.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
	
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
						
			}
			
		}).create().show();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "Show Navigation").setIcon(R.drawable.menu_map_icon);
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
		super.onOptionsItemSelected(item);

		String FromCity = mDetailDeliveryModel.getPUcity();
		String FromAdd= mDetailDeliveryModel.getPUaddress();
		String FromState= mDetailDeliveryModel.getPUstate();
		String FromZip= mDetailDeliveryModel.getPUzip();

		String ToCity = mDetailDeliveryModel.getDLcity();
		String ToAdd= mDetailDeliveryModel.getDLaddress();
		String ToState= mDetailDeliveryModel.getDLstate();
		String ToZip= mDetailDeliveryModel.getDLzip();

		String FromAddress =FromAdd+" "+FromCity+", "+FromState+" "+FromZip;
		String ToAddress = ToAdd+" "+ToCity+", "+ToState+" "+ToZip;


		try{
			if(Utils.checkNetwork(getApplicationContext()))
			{
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		        	Geocoder coder = new Geocoder(getApplicationContext());
					ArrayList<Address> adressesFrom = (ArrayList<Address>) coder.getFromLocationName(FromAddress, 50);
					ArrayList<Address> adressesTo = (ArrayList<Address>) coder.getFromLocationName(ToAddress, 50);

					double latFrom =0.0;
					double lonFrom = 0.0;

					double latTo=0.0;
					double lonTo= 0.0;

					for(Address add : adressesFrom){
						lonFrom= add.getLongitude();
						latFrom= add.getLatitude();
					}

					for(Address add1 : adressesTo){
						lonTo= add1.getLongitude();
						latTo= add1.getLatitude();
					}

					String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", latFrom, lonFrom, FromAddress,  latTo,  lonTo, ToAddress);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					startActivity(intent);
					
		        }else{
		            showGPSDisabledAlertToUser();
		        }
		        
				
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Please Check for Internet Connection", Toast.LENGTH_LONG).show();
			}

		}catch(Exception e){}
		return false;
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
	
	
