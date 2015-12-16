package com.rna.activities;

import java.util.ArrayList;

import com.aotd.activities.R;
import com.aotd.activities.SignatureActivity;
import com.aotd.activities.R.drawable;
import com.aotd.activities.R.id;
import com.aotd.activities.R.layout;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.RNADataBase;
import com.aotd.model.RxItemModel;
import com.aotd.parsers.PickupParser;
import com.aotd.parsers.RxItemParser;
import com.aotd.utils.Utils;
import com.rna.activities.RNAPickUpActivity.RNAPickupHandler;
import com.rna.activities.RNAPickUpActivity.RxItemAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RNADeliveryActivity extends Activity {
/** Called when the activity is first created. */
	
	
	private ListView rna_items_listview;
	private ProgressDialog progressdialog;
	private ArrayList<RxItemModel> arr_rxitem;
	private Button rna_delivery;
	public ImageView imgOnline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.rna_delivery_data);
		progressdialog = ProgressDialog.show(RNADeliveryActivity.this, "", "please wait...");
		initializeUI();		
		final String rna_items_data = getIntent().getStringExtra("rna_data");
		final String batch_id = getIntent().getStringExtra("rna_batchId");
		getParsedData(rna_items_data);
		
		rna_delivery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				RNADataBase _RNADataBase = new RNADataBase(RNADeliveryActivity.this);		
				_RNADataBase.updateDelivery(batch_id, rna_items_data, arr_rxitem.get(0).getNHomeCode());
				
				
				Intent sign_intent = new Intent(RNADeliveryActivity.this,SignatureActivity.class);
				sign_intent.putExtra("rna_batchId", batch_id);
				sign_intent.putExtra("rna_data", rna_items_data);				
				sign_intent.putExtra("rna_nhomecode", arr_rxitem.get(0).getNHomeCode());
				
				int ictr = 0;
				String xmlData = "<RxList>";
				while(ictr< arr_rxitem.size())
				{
					xmlData = xmlData + "<RxItem><RxNumber>" + arr_rxitem.get(ictr).getRxN() + "</RxNumber><Status>" + arr_rxitem.get(ictr).getStatus() +"</Status><Filler></Filler></RxItem>";
					ictr++;
				}
				xmlData = xmlData + "</RxList>";
				sign_intent.putExtra("rna_xmlData", xmlData);
				sign_intent.putExtra("from", "rna");
				startActivity(sign_intent);
			}
		});
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
	}

	private void initializeUI() {
		rna_items_listview = (ListView)findViewById(R.id.rna_delivery_lsitview);
		arr_rxitem = new ArrayList<RxItemModel>();
		rna_delivery = (Button)findViewById(R.id.rna_delivery_btn);
		 imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
	}

	private void getParsedData(String rna_items_data) {
		
		RxItemParser mRxItemParser = new RxItemParser(new RNAPickupHandler(),arr_rxitem);
				     mRxItemParser.parse(rna_items_data);
	}
	
	class RNAPickupHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
					
					progressdialog.dismiss();
					
					if(msg.what == 1){
						
						String errorMsg=msg.getData().getString("HttpError");
						
						
						if(errorMsg.length()>0){
							
							showAlertDialog("AOTD",errorMsg,false);
						}else{						
							
							if(arr_rxitem != null && arr_rxitem.size()>0){
														
							
								rna_items_listview.setAdapter(new RxItemAdapter(arr_rxitem));
			
							 }else{
								 
								 showAlertDialog("AOTD","No BatchId Items Available",false);
								
							 }
											
						}					
					}else if(msg.what ==0){
						String errorMsg=msg.getData().getString("HttpError");
						String successMsg = msg.getData().getString("success");
						
						if(errorMsg.length()>0){
							
							showAlertDialog("AOTD",errorMsg,false);
						}else{
							
							showAlertDialog("AOTD",successMsg,true);
						}
						
					}
					
					
		}
}

	public void showAlertDialog(String title, String msg,final boolean found) {
		
		new AlertDialogMsg(RNADeliveryActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which){
				if(found){
					finish();
				}
				
			}
						
			
		 }).create().show();		
		
	}
	
class RxItemAdapter extends BaseAdapter{
		
		ArrayList<RxItemModel> data;
		public RxItemAdapter(ArrayList<RxItemModel> data) {
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
             v = inflater.inflate(R.layout.rna_pickup_row, null);	 
			 
			 TextView controlled_drug_txt = (TextView)v.findViewById(R.id.controlled_drug_txt);		
			 TextView NHome_txt = (TextView)v.findViewById(R.id.NHome_txt);
			 TextView NHomeCode_txt = (TextView)v.findViewById(R.id.NHomeCode_txt);
			 TextView RxDescription_txt = (TextView)v.findViewById(R.id.RxDescription_txt);
			 TextView RxN_txt = (TextView)v.findViewById(R.id.RxN_txt);
			 TextView RxQuantity_txt = (TextView)v.findViewById(R.id.RxQuantity_txt);
			 TextView Status_txt = (TextView)v.findViewById(R.id.Status_txt);
			 
			 controlled_drug_txt.setText("ControlledDrug :"+data.get(position).getControlledDrug());
			 NHome_txt.setText("NHome  :"+data.get(position).getNHome());
			 NHomeCode_txt.setText("NHomeCode  :"+data.get(position).getNHomeCode());
			 RxDescription_txt.setText("RxDescription  :"+data.get(position).getRxDescription());
			 RxN_txt.setText("RxN  :"+data.get(position).getRxN());
			 RxQuantity_txt.setText("RxQuantity  :"+data.get(position).getRxQuantity());
			 Status_txt.setText("Status  :"+data.get(position).getStatus());
			
			 
			 

			return v;
		}
		
	}
}	