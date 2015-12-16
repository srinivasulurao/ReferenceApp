package com.rna.activities;
//package com.aotd.activities;
//
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.aotd.dialog.AlertDialogMsg;
//import com.aotd.model.RxItemModel;
//import com.aotd.parsers.PickupParser;
//import com.aotd.parsers.RxItemParser;
//import com.aotd.utils.Utils;
//import com.vinscan.barcode.CaptureActivity;
//
//public class RNAScanActivity extends Activity implements OnClickListener {
//	
////	private Button scan_btn,scan_status_btn;
////	private TextView batch_txtView;
////	private EditText batch_editText;
////	private RelativeLayout batchId_layout;
////	private String batchId = "";
////	private ArrayList<RxItemModel> arr_RxItem;
////	private ProgressDialog progressdialog;
////	private final int SCAN_COMPLETED = 1;
////	private ListView rna_listview;
////	@Override
////	public void onCreate(Bundle savedInstanceState) {
////		super.onCreate(savedInstanceState);		
////		setContentView(R.layout.aotd_rna_scanner);
////		initializeUI();		
////		
////	}
////	
////	
////	private void initializeUI() {
////		
////		arr_RxItem = new ArrayList<RxItemModel>();		
////		scan_btn 		= (Button)findViewById(R.id.rna_scan_btn);
////		scan_status_btn = (Button)findViewById(R.id.rna_status_btn);
////		//batch_btn 		= (Button)findViewById(R.id.batchId_btn);
////		//batch_status_btn= (Button)findViewById(R.id.batch_status_btn);	
////		//batch_txtView = (TextView)findViewById(R.id.batchId_txtView);
////		batch_editText = (EditText)findViewById(R.id.batch_txt_data);
////		
////		//batchId_layout  = (RelativeLayout)findViewById(R.id.batchId_layout);
////		//rna_listview   = (ListView)findViewById(R.id.rna_listView);
////		
////		scan_btn.setOnClickListener(this);
////		scan_status_btn.setOnClickListener(this);
////		//batch_btn.setOnClickListener(this);
////		//batch_status_btn.setOnClickListener(this);
////		
////	}
////	@Override
////	public void onClick(View v) {
////		switch (v.getId()) {
////		case R.id.rna_status_btn:
////				 // batchId = "04CF35";04CF36,04CF35,04CF34,04CF33,
////			if(batch_editText.getText().toString().length()>0){
////				
////				if(scan_status_btn.getText().toString().equalsIgnoreCase("Open")){					  	
////						
////						progressdialog = ProgressDialog.show(RNAScanActivity.this, "", "please wait..."); 
////						String url = "http://216.138.115.213/RxManifest/eManiFest.svc/Drivers/CheckOut/GetRxList/rna2/111/SYSTEM/MYDEVICENAME/RPH/04CF35";
////						
////						RxItemParser mRxItemParser = new RxItemParser(new RxHandler(),url,arr_RxItem);
////						mRxItemParser.start();
////					 
////				}else if(scan_status_btn.getText().toString().equalsIgnoreCase("Pick Up")){					
////						
////						progressdialog = ProgressDialog.show(RNAScanActivity.this, "", "please wait..."); 
////						String xml_data = "<RxList><RxItem><RxDescription>ENSURE LIQ (2.5=1 BOTTLE</RxDescription><ControlledDrug>N</ControlledDrug><NHome>AMC3</NHome><NHomeCode>AMU3</NHomeCode><RxN>041304968</RxN><RxQuantity>0</RxQuantity><Status>Not Checked</Status></RxItem></RxList>";
////						String data = "batchID=04CF35&stationID=111&deviceName=MYDEVICENAME&batchDetails="+xml_data;
////						//String data = "batchID=04CF35&stationID=111&deviceName=MYDEVICENAME&batchDetails=<RxList></RxList>";
////						
////						PickupParser mRNAPickUpParser = new PickupParser(Utils.RNA_PICKUP_URL,new RxHandler());
////							
////						
////						mRNAPickUpParser.isPost=true;
////						mRNAPickUpParser.postData = data;
////						mRNAPickUpParser.start();
////					
////				}
////				
////			}else{
////				
////				alertDialogWithMsg("AOTD","please get the batchId first");	
////			}
////			
////			
////			
////			break;
////			
////		case R.id.rna_scan_btn:
////			
////			Intent	NewScreenIntent=new Intent(RNAScanActivity.this,CaptureActivity.class);
////			startActivityForResult(NewScreenIntent, SCAN_COMPLETED);
////			
//////			batchId = "04CF3Y";	
//////			if(batchId.length()>0){
//////				batchId_layout.setVisibility(View.VISIBLE);
//////				batch_txt_data.setText(batchId);
//////			}	
////		
////		break;	
////
////		default:
////			break;
////		}
////		
////	}
////		
////		 
////			
////
////	
////	class RxHandler extends Handler
////	{
////		public void handleMessage(android.os.Message msg)
////		{
////			progressdialog.dismiss();
////			
////			if(msg.what == 0){
////				
////				String errorMsg=msg.getData().getString("HttpError");
////				
//////				if(errorMsg.length()>0){				
//////					alertDialogWithMsg("AOTD",errorMsg);				
//////					
//////				}else{
//////					Log.v("size ",""+arr_RxItem.size());
//////					Log.v("data is",""+arr_RxItem.get(0).getRxDescription());
//////					
//////				}	
////				
////				//data adding statically at present
////				
////				RxItemModel obj1 = new RxItemModel();
////				obj1.setControlledDrug("Y");
////				obj1.setNHome("OIL CITY 1ST FLOOR");
////				obj1.setNHomeCode("OC01");
////				obj1.setRxDescription("CHERATUSSIN AC SYRUP");
////				obj1.setRxN("040165596");
////				obj1.setRxQuantity("118");
////				obj1.setStatus("Not Checked");
////				
////				RxItemModel obj2 = new RxItemModel();
////				obj2.setControlledDrug("N");
////				obj2.setNHome("OIL CITY 1ST FLOOR");
////				obj2.setNHomeCode("OC01");
////				obj2.setRxDescription("DOXYCYCLINE HYC 100 MG CAP*");
////				obj2.setRxN("040175434");
////				obj2.setRxQuantity("14");
////				obj2.setStatus("Not Checked");
////				
////				arr_RxItem.add(obj1);
////				arr_RxItem.add(obj2);
////				
////				rna_listview.setAdapter(new RNA_Adapter(arr_RxItem));
////				scan_status_btn.setText("Pick Up");
////				
////			}else if(msg.what==1){
////				
////				String errorMsg=msg.getData().getString("HttpError");
////				if(errorMsg.length()>0){	
////					
////					alertDialogWithMsg("AOTD",errorMsg);
////					
////			    }else{	
////			    	
////			    	alertDialogWithMsg("AOTD","RNAOrder Picked-up updted successfully");
////			    	
////					
////	            }  
////				
////			}
////			
////			
////			
////	}
////  }
////	
////
////
////	public void alertDialogWithMsg(String string, final String errorMsg) {
////		
////		new AlertDialogMsg(RNAScanActivity.this, "AOTD", errorMsg).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
////			
////			@Override
////			public void onClick(DialogInterface dialog, int which){
////				
////			//	if(!errorMsg.contains("error"))
////				//	finish();
////						
////			}
////		 }).create().show();		
////}
////		
////	
////	@Override
////	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
////		super.onActivityResult(requestCode, resultCode, data);
////		
////		switch (requestCode) {
////		
////			case SCAN_COMPLETED:
////				
////			if(resultCode == RESULT_OK){	
////				
////				batchId = data.getStringExtra("vinnum");
////				
////
////				Log.v("vin nunmber",data.getStringExtra("vinnum"));					
////					
////				if(batchId.length()>0){
////					//batchId_layout.setVisibility(View.VISIBLE);
////					batch_editText.setText(batchId);
////				}
////			}	
////		}	
////	}
////	
////class RNA_Adapter extends BaseAdapter{
////	
////	ArrayList<RxItemModel> data;
////	
////	public RNA_Adapter(ArrayList<RxItemModel> data) {
////		// TODO Auto-generated constructor stub
////		this.data = data;
////	}
////
////	@Override
////	public int getCount() {
////		// TODO Auto-generated method stub
////		return data.size();
////	}
////
////	@Override
////	public Object getItem(int arg0) {
////		// TODO Auto-generated method stub
////		return data.get(arg0);
////	}
////
////	@Override
////	public long getItemId(int arg0) {
////		// TODO Auto-generated method stub
////		return arg0;
////	}
////
////	@Override
////	public View getView(int position, View v, ViewGroup arg2) {
////		// TODO Auto-generated method stub
////		if (v == null){			
////			 LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////             v = inflater.inflate(R.layout.rna_listview_row, null);
////        }
////		 
////		
////		 TextView controllDrug_txt = (TextView)v.findViewById(R.id.controlled_drug_txt);			
////		 controllDrug_txt.setText(("ControlledDrug")+"   :   "+data.get(position).getControlledDrug());
////		 
////		 TextView nHome_txt = (TextView)v.findViewById(R.id.NHome_txt);			
////		 nHome_txt.setText(("NHome")+"   :   "+data.get(position).getNHome());
////		 
////		 TextView nHomeCode_txt = (TextView)v.findViewById(R.id.NHomeCode_txt);			
////		 nHomeCode_txt.setText(("NHomeCode")+"   :   "+data.get(position).getNHomeCode());
////		 
////		 TextView rxDesc_txt = (TextView)v.findViewById(R.id.RxDescription_txt);			
////		 rxDesc_txt.setText(("RxDescription")+"   :   "+data.get(position).getRxDescription());
////		 
////		 TextView rxN_txt = (TextView)v.findViewById(R.id.RxN_txt);			
////		 rxN_txt.setText(("RxN")+"   :   "+data.get(position).getRxN());
////		 
////		 TextView rxQuant_txt = (TextView)v.findViewById(R.id.RxQuantity_txt);			
////		 rxQuant_txt.setText(("RxQuantity")+"   :   "+data.get(position).getRxQuantity());
////		 
////		 TextView status_txt = (TextView)v.findViewById(R.id.Status_txt);			
////		 status_txt.setText(("Status")+"   :   "+data.get(position).getStatus());
////		 
////		 return v;
////	}
////	
////}
//}
