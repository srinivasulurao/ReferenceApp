package com.rna.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.widget.ListView;
import android.widget.TextView;

import com.aotd.activities.MainDispatchScreenTabView;
import com.aotd.activities.R;
import com.aotd.activities.R.drawable;
import com.aotd.activities.R.id;
import com.aotd.activities.R.layout;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.RNADataBase;
import com.aotd.model.RNABatchIdModel;
import com.aotd.parsers.RNAParser;
import com.aotd.utils.Utils;

public class RNADispatchPresentTabActivity extends Activity {
	/** Called when the activity is first created. */
	
	
	private ListView rna_present_listview;
	private ProgressDialog progressdialog;
	private ArrayList<RNABatchIdModel> arr_batchId;
	private Button aotd_btn;
	private boolean dataRequestSent = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.rna_dispatch_presenttab);
		initializeUI();		
		//aotd button
		aotd_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent rna_present  = new Intent(RNADispatchPresentTabActivity.this,MainDispatchScreenTabView.class);
				rna_present.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				rna_present.putExtra("from", "rna");
				startActivity(rna_present);		
				
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(Utils.checkNetwork(getApplicationContext()))
			RNATabActivity.imgOnline.setBackgroundResource(R.drawable.online);
		else
			RNATabActivity.imgOnline.setBackgroundResource(R.drawable.offline);
		
		if(!dataRequestSent){
			arr_batchId.clear();
			if(mBatchIdAdapter != null)
				mBatchIdAdapter.notifyDataSetChanged();
			getBatchIdParserData();
		}
		
	}
	
	private void initializeUI() {
		
		rna_present_listview = (ListView)findViewById(R.id.rna_dispatch_present_lsitview);
		arr_batchId = new ArrayList<RNABatchIdModel>();
		aotd_btn = (Button)findViewById(R.id.rna_btn);
		
	}
	
	private void getBatchIdParserData() {
		
		
		dataRequestSent = true;
		if(Utils.checkNetwork(getApplicationContext()))
		{
			
			progressdialog = ProgressDialog.show(RNADispatchPresentTabActivity.this, "", "please wait..."); 
			
			try {
				
				Log.e("Sync Data", "******************* syncData");
				new SyncRNAData(getApplicationContext(), new getSyncHandler());
				
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
		
		String url = Utils.RNA_DISPATCH_PRESENT_URL;
		RNAParser mPickupParser = new RNAParser(new BatchIdHandler(),url,arr_batchId);
		mPickupParser.start();
		
	}
	
	private void getOfflineData(){
		
		
		Date today  = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String mydate =  sdf.format(today);
		Log.e("Date", "mydate "+ mydate);
		
		RNADataBase _RNADataBase = new RNADataBase(this);
		arr_batchId = _RNADataBase.getOfflineRNAOrders(mydate, "present");
		
		if(arr_batchId != null && arr_batchId.size()>0){
			
			dataRequestSent = false;
			rna_present_listview.setAdapter(new BatchIdAdapter(arr_batchId));
			
		}else{
			
			showAlertDialog("AOTD","No Dispatches available");
			
		}
	}
	
//		Called when the sync id done
	
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
	
	BatchIdAdapter mBatchIdAdapter = null;
	
	class BatchIdHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			
			progressdialog.dismiss();
			
			if(msg.what == 0){
				
				String errorMsg=msg.getData().getString("HttpError");						
				
				if(errorMsg.length()>0){
					
					showAlertDialog("AOTD",errorMsg);
					
				}else{							
					
					if(arr_batchId != null && arr_batchId.size()>0){													
						
						dataRequestSent = false;
						mBatchIdAdapter = new BatchIdAdapter(arr_batchId);
						rna_present_listview.setAdapter(mBatchIdAdapter);		
						
					}else{
						
						showAlertDialog("AOTD","No RNA Orders Available");								
					}
					
				}				
			}
		}
	}
	
	public void showAlertDialog(String title, String msg) {
		
		new AlertDialogMsg(RNADispatchPresentTabActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				
				dataRequestSent = false;
				
			}
			
			
		}).create().show();		
		
	}
	
	class BatchIdAdapter extends BaseAdapter{
		
		ArrayList<RNABatchIdModel> data;
		public BatchIdAdapter(ArrayList<RNABatchIdModel> data) {
			this.data = data;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}
		
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		
		@Override
		public View getView(final int position, View v, ViewGroup arg2) {		
			
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.rna_batchid_list_row, null);
			
			
			
			
			TextView mData = (TextView)v.findViewById(R.id.batchid_date_txtView);			
			Button batchId  = (Button)v.findViewById(R.id.rna_batchId_btn);
			final Button batchId_status  = (Button)v.findViewById(R.id.rna_batchId_status_btn);
			
			mData.setText(""+data.get(position).getDPDate());
			batchId.setText(data.get(position).getBatchId());
			batchId_status.setText(data.get(position).getStatus());
			
			batchId.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(batchId_status.getText().toString().equalsIgnoreCase("open")){
						
						Intent rna_pickup = new Intent(RNADispatchPresentTabActivity.this,RNAPickUpActivity.class);	
						rna_pickup.putExtra("rna_data", data.get(position).getBatchDetails());
						rna_pickup.putExtra("rna_batchId", data.get(position).getBatchId());
						startActivity(rna_pickup);
						
					}else if(batchId_status.getText().toString().startsWith("Picked")){
						
						Intent rna_pickup = new Intent(RNADispatchPresentTabActivity.this,RNADeliveryActivity.class);	
						rna_pickup.putExtra("rna_data", data.get(position).getBatchDetails());	
						rna_pickup.putExtra("rna_batchId", data.get(position).getBatchId());
						startActivity(rna_pickup);
						
					}
					
				}
			});
			batchId_status.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(batchId_status.getText().toString().equalsIgnoreCase("open")){
						Intent rna_pickup = new Intent(RNADispatchPresentTabActivity.this,RNAPickUpActivity.class);	
						rna_pickup.putExtra("rna_data", data.get(position).getBatchDetails());
						rna_pickup.putExtra("rna_batchId", data.get(position).getBatchId());
						startActivity(rna_pickup);	
					}else if(batchId_status.getText().toString().startsWith("Picked")){
						Intent rna_pickup = new Intent(RNADispatchPresentTabActivity.this,RNADeliveryActivity.class);	
						rna_pickup.putExtra("rna_data", data.get(position).getBatchDetails());	
						rna_pickup.putExtra("rna_batchId", data.get(position).getBatchId());
						startActivity(rna_pickup);	
					}  
				}
				
				
			});
			
			
			
			return v;
		}
		
	}
	
}
