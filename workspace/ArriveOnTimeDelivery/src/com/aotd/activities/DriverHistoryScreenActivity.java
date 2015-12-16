package com.aotd.activities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DriverGeoLocationModel;
import com.aotd.parsers.DriverOrdersParser;
import com.aotd.utils.Utils;
/**
 * 
 * 
 * @author bharath
 *
 */
public class DriverHistoryScreenActivity extends Activity{
	
	ListView mListView;
	Context mContext = DriverHistoryScreenActivity.this;
	private Intent mRecieverIntent;
	private ArrayList<DispatchAllListModel> mDispatchList;
	private ProgressDialog progressdialog;
	private DispatchAdapter mDispatchAdapter;
	private DriverGeoLocationModel mDriverGeoLocationModel = null;
	private TextView heading_txt;
	private int pageNumber=0;
	private int showing_from;
	public ImageView imgOnline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aotd_driver_list_listview);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		mRecieverIntent = getIntent();
		mDriverGeoLocationModel = (DriverGeoLocationModel) mRecieverIntent.getExtras().get("driverinfo");
		
		mListView = (ListView)findViewById(R.id.driver_list_listView);	
		heading_txt = (TextView)findViewById(R.id.heading_txtView);
		heading_txt.setText("Driver History");
		mDispatchList = new ArrayList<DispatchAllListModel>();
		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
		
//		getParserData();
		

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		pageNumber=0;
		mDispatchList.clear();
		getParserData();
		
		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
		
	}
	
	private void getParserData(){
		
		progressdialog = ProgressDialog.show(DriverHistoryScreenActivity.this,null,null);
		progressdialog.setContentView(R.layout.loader); 
		try
		{
			String encodedRoleName   = URLEncoder.encode(Utils.ROLENAME.trim(),"UTF-8");
				
		    String encodedId = URLEncoder.encode(mDriverGeoLocationModel.getEmail().trim(),"UTF-8");
		    String url = String.format(Utils.DRIVER_ORDERS,encodedId, pageNumber); 			
		    DriverOrdersParser mDriverOrdersParser = new DriverOrdersParser(new DispatchPresentHandler(), url , mDispatchList);
		    mDriverOrdersParser.start(); 
		}catch(Exception e)
		{
			
		}
				
	}
	
	
	private class DispatchAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		ArrayList<DispatchAllListModel> mDataFeeds;
		
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_BUTTON_MORE = 1;
   
		private static final int TYPE_MAX_COUNT = 2;
    	
		
    	public DispatchAdapter(ArrayList<DispatchAllListModel> mDispatchList) 
        {
        	mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	this.mDataFeeds = mDispatchList;
        }
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
//			return mDataFeeds.size();
			if (mDataFeeds.size() >= 10+(10*(pageNumber-1))) 
				return mDataFeeds.size() + 1;
			else
				return mDataFeeds.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (position == 10+(10*(pageNumber-1)))
			{
				return TYPE_BUTTON_MORE;
			}
			else{
				return TYPE_ITEM;
			}
		}
		
		 @Override 
		  public int getViewTypeCount()  
		  { 
			  return TYPE_MAX_COUNT;
		  }
		 
		 
		@Override
		public View getView(final int pp, View convertView, ViewGroup parent) {
			 View v = convertView;
			 int type = getItemViewType(pp);
			 
			 if (v == null) 
			 {
				 if (type == TYPE_ITEM) {
	                 v = mInflater.inflate(R.layout.aotd_driver_history_listrow, null);
				}else {
					showing_from = pp;
					v = mInflater.inflate(R.layout.viewmore_items, null);
				}
                  
             }
			 
		   if (type == TYPE_ITEM)
		   {
			
			 String mDpdate_Status = "0000-00-00 00:00:00";
			 String color = "", orderText = "";
			 RelativeLayout rl1=(RelativeLayout)v.findViewById(R.id.driver_list_layout_main);
			 
			 final Button mOrderNum  	= (Button)v.findViewById(R.id.dispatch_order_num_btn);
			 final Button mOrderStatus  = (Button)v.findViewById(R.id.driver_history_row_btn_order_status);
			 
//			 TextView mOrderNum = (TextView)v.findViewById(R.id.driver_history_txt_ordernum);
//			 TextView mAddress = (TextView)v.findViewById(R.id.driver_history_txt_address);
			 TextView mTime = (TextView)v.findViewById(R.id.driver_history_txt_time);
//			 TextView mDate = (TextView)v.findViewById(R.id.driver_history_txt_date);
			
			 mOrderNum.setText(mDataFeeds.get(pp).getOrder_id().trim());
//			 mAddress.setText(mDriverGeoLocationModel.getAddress().trim());
			 mTime.setText(mDataFeeds.get(pp).getRDDate().trim());
			 
			 if (mDataFeeds.get(pp).getDPDate().equalsIgnoreCase(mDpdate_Status)) {
				 
				 color = mDataFeeds.get(pp).getOrderColor();
				 orderText = "open order";
						 
			 }			 
			 
			 if (!mDataFeeds.get(pp).getDPDate().equalsIgnoreCase(mDpdate_Status) &&
					 (mDataFeeds.get(pp).getPUDate().equalsIgnoreCase(mDpdate_Status)||
							 mDataFeeds.get(pp).getDLDate().equalsIgnoreCase(mDpdate_Status)) ) {				 
				 
				 color = mDataFeeds.get(pp).getOrderColor();
				 orderText = "pick up";
				 
			 }
			 
			 
			 if (!mDataFeeds.get(pp).getPUDate().equalsIgnoreCase(mDpdate_Status) ) {				 
				 color = "green";
			 }
			 
			 if (!mDataFeeds.get(pp).getDLDate().equalsIgnoreCase(mDpdate_Status) ) {				 
				 color = "blue";
			 }
			
			 
			 
				 if(color.equalsIgnoreCase("red")){
					 
					 if(orderText.equalsIgnoreCase("open order")){
						 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_openorder);
						 mOrderStatus.setText("Open Order");
					 }else{
						 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_red);
						 mOrderStatus.setText("Pick Up");
					 } 
				 }else if(color.equalsIgnoreCase("green")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_green);
					mOrderStatus.setText("Picked Up");
				 }else if(color.equalsIgnoreCase("orange")){
					 
					 if(orderText.equalsIgnoreCase("open order")){
						 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_openorder);
						 mOrderStatus.setText("Open Order");
					 }else{
						 mOrderStatus.setBackgroundResource(R.drawable.btn_bg_pickedup_red);
						 mOrderStatus.setText("Pick Up");
					 } 
				 }else if(color.equalsIgnoreCase("white")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_bg_openorder);
					 mOrderStatus.setText("Open Order");
				 }else if(color.equalsIgnoreCase("blue")){
					mOrderStatus.setBackgroundResource(R.drawable.btn_bg_delivered_clk);
					 mOrderStatus.setText("Delivered");
				 }
			 
				 mOrderNum.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(DriverHistoryScreenActivity.this,DriverEachOrderDetailsActivity.class);						
						intent.putExtra("orderinfo", mDispatchList.get(pp));
						startActivity(intent);
						
						
					}
				});
				 mOrderStatus.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent=new Intent(DriverHistoryScreenActivity.this,DriverEachOrderDetailsActivity.class);						
							intent.putExtra("orderinfo", mDispatchList.get(pp));
							startActivity(intent);
							
							
						}
					});
				 
				 
		   }else{
			   
			  
			   ImageView mViewMore_Img = (ImageView)v.findViewById(R.id.view_more_Btn);
			   
			   mViewMore_Img.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					progressdialog = ProgressDialog.show(DriverHistoryScreenActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader); 
					try 
					{
						String encodedId = URLEncoder.encode(mDriverGeoLocationModel.getEmail().trim(),"UTF-8");
						//String encodedId = URLEncoder.encode("11","UTF-8");
				 		Log.v(getClass().getSimpleName(), " on more click "+pageNumber);

						String url = String.format(Utils.DRIVER_ORDERS,encodedId, pageNumber);

						DriverOrdersParser mDriverOrdersParser = new DriverOrdersParser(new DispatchPresentHandler(), url , mDispatchList);
					    mDriverOrdersParser.start(); 
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		   }
		   return v;
		}
		
		
}
	
	
	
	 
	 class DispatchPresentHandler extends Handler
		{
			public void handleMessage(android.os.Message msg)
			{
				progressdialog.dismiss();
				String errorMsg=msg.getData().getString("HttpError");
				if(errorMsg.length()>0){
					
					alertDialogWithMsg("AOTD",errorMsg);				
					
				}else{
					  
						if(mDispatchList != null && mDispatchList.size()>0){
							mDispatchAdapter = null;							
						
							mDispatchAdapter =  new DispatchAdapter(mDispatchList);
							pageNumber = pageNumber + 1;
							mListView.setAdapter(mDispatchAdapter);
							mListView.setSelection(showing_from);
		
						 }else{
							alertDialogWithMsg("AOTD","No Dispatches available");
							
						 }
				}
					
			}
}	
	 
	 public void alertDialogWithMsg(String title, String msg){	
		 
			new AlertDialogMsg(DriverHistoryScreenActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
		
				@Override
				public void onClick(DialogInterface dialog, int which){
					DriverHistoryScreenActivity.this.finish();
							
				}
				
			 }).create().show();		
	}

	
	 
	 
	
	
}