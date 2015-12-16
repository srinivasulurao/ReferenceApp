package com.rna.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aotd.activities.MainDispatchScreenTabView;
import com.aotd.activities.R;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.RNADataBase;
import com.aotd.parsers.DispatchOrderParser;
import com.aotd.parsers.RxListParser;
import com.aotd.utils.Utils;
import com.vinscan.barcode.CaptureActivity;

public class RNADispatchTabActivity extends Activity implements OnClickListener {
	
	private Button scan_btn,status_btn;

	private EditText batch_editText;	
	private String batchId = "";	
	private ProgressDialog progressdialog;
	private final int SCAN_COMPLETED = 1;
	private Button aotd_btn;
	private boolean from_rna_service = true;
	private String xmldata = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.rna_dispatch);
		initializeUI();			
		
		//aotd button
		aotd_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent rna_present  = new Intent(RNADispatchTabActivity.this,MainDispatchScreenTabView.class);
				rna_present.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				rna_present.putExtra("from", "rna");
				startActivity(rna_present);		
						
			}
		});
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Utils.checkNetwork(getApplicationContext()))
			RNATabActivity.imgOnline.setBackgroundResource(R.drawable.online);
		else
			RNATabActivity.imgOnline.setBackgroundResource(R.drawable.offline);
	}



	private void initializeUI() {
		
		
		scan_btn   = (Button)findViewById(R.id.rna_dispatch_scan_btn);
		status_btn = (Button)findViewById(R.id.rna_dispatch_status_btn); 		
		batch_editText = (EditText)findViewById(R.id.rna_dispatch_editText);
		aotd_btn = (Button)findViewById(R.id.rna_btn);
		
		
		
		 scan_btn.setOnClickListener(this);
		 status_btn.setOnClickListener(this);
		//batch_btn.setOnClickListener(this);
		//batch_status_btn.setOnClickListener(this); 
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.rna_dispatch_status_btn:	
			
			if(batch_editText.getText().toString().length()>0){
				
				progressdialog = ProgressDialog.show(RNADispatchTabActivity.this, "", "please wait..."); 				
				
				String url =String.format(Utils.RNA_RXLIST_URL,batch_editText.getText().toString());
				RxListParser mRxListParser = new RxListParser(url,new RxHandler());
				mRxListParser.start();
				
			}else{
				
				showAlertDialog("AOTD","Please Enter the BatchId First",false);
			}
				 
					
			break;
		case R.id.rna_dispatch_scan_btn:
			
			Intent	NewScreenIntent=new Intent(RNADispatchTabActivity.this,CaptureActivity.class);
			startActivityForResult(NewScreenIntent, SCAN_COMPLETED);
			

		break;	

		default:
		    break;
		}
		
	}





	class RxHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
					Log.v("status btn handler","status btn handler");	
					
					
					if(msg.what == 1){
						
						Log.v("msg what 1","msg what 1");
						progressdialog.dismiss();
						String errorMsg=msg.getData().getString("HttpError");
						String successMsg = msg.getData().getString("success");
						
						if(errorMsg.length()>0){
							
							showAlertDialog("AOTD",errorMsg,false);
							
						}else{
							
							RNADataBase _RNADataBase = new RNADataBase(RNADispatchTabActivity.this);
							_RNADataBase.insertNewRNA(batch_editText.getText().toString(), xmldata, successMsg);
							
							RNADataBase _RNADataBaseOne = new RNADataBase(RNADispatchTabActivity.this);
							_RNADataBaseOne.checkRNADatabaseData();
							
							new AlertDialogMsg(RNADispatchTabActivity.this, "AOTD", "RNA Order opened successfully" ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
								
								@Override
								public void onClick(DialogInterface dialog, int which){
									RNATabActivity.tabHost.setCurrentTab(1);
								}
							 }).create().show();		

							//showAlertDialog("AOTD",successMsg,true);
							
						}				
					}if(msg.what == 2){
						
						Log.v("msg what 2","msg what 2");
						progressdialog.dismiss();
						String errorMsg=msg.getData().getString("HttpError");
						String successMsg = msg.getData().getString("success");
						xmldata		=  msg.getData().getString("xml_data");
						
						if(errorMsg.length()>0){
							
							if(errorMsg.contains("400")){
								
								showAlertDialog("AOTD","Invalid batch id requested or alredy checked out in AOTD datbase, please contact support...",false);
								
							}else{
								
								showAlertDialog("AOTD",errorMsg,false);
								
							}
							
						}else{
							
							//showAlertDialog("AOTD",successMsg,true);	
							progressdialog.show();

							String data ="batchID="+batch_editText.getText().toString()+"&stationID=111&deviceName=MYDEVICENAME&driverName=SYSTEM&batchXml="+xmldata;	
							Log.v("data",data);
							DispatchOrderParser mResponseParser = null;
							mResponseParser = new DispatchOrderParser(Utils.RNA_UPDATEORDER_URL,new RxHandler());				
							
							mResponseParser.isPost=true;
							mResponseParser.postData = data;
							mResponseParser.start();

						}				
					}
		}
	}
	
		
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		
			case SCAN_COMPLETED:
				
			if(resultCode == RESULT_OK){	
				
				batchId = data.getStringExtra("batchid");
				

				Log.v("vin nunmber",data.getStringExtra("batchid"));					
					
				if(batchId.length()>0){
					//batchId_layout.setVisibility(View.VISIBLE);
					batch_editText.setText(batchId);
				}
			}	
		}	
	}
	
	public void showAlertDialog(String title, String msg,final boolean found){	
		 
			new AlertDialogMsg(RNADispatchTabActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
		
				@Override
				public void onClick(DialogInterface dialog, int which){
							
				}
			 }).create().show();		
	}





}
