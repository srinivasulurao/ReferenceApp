package com.aotd.activities;

import java.io.File;
import java.util.ArrayList;

import com.aotd.activities.DeliveryOrderInfoDelivery.PdfHandler;
import com.aotd.helpers.DownLoadPDFAsync;
import com.aotd.helpers.DownLoadPDFAsyncForPending__;
import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.OfflineModel;
import com.aotd.model.Question;
import com.aotd.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Off_delivered_page extends Activity {
	
	private Button delivered, scanBtn,scanBottom,deliverBottom, btnCamera;
	private TextView orderNum, accountName, accountNotes, pickUp_from,
	delivery_to, rfc, puinstruction, dlinstruction, serviceType,
	pickupReady, requestedBy, pieces, weight, roundTrip, adminNotes;

	private ProgressDialog progress = null;
	private DetailDeliveryModel mDetailDeliveryModel = null;
	private String openorderStatus;
	public ImageView imgOnline;

	String orderId = "", deliveryType = "first";
	String to_Comp="";
	String OrderNumber="";
	
	private void intializeUI() {

		openorderStatus = getIntent().getExtras().getString("openorder");

		delivered = (Button) findViewById(R.id.aotd_delivery_delivery_btn);
		scanBtn = (Button) findViewById(R.id.aotd_delivery_scan_btn);
		orderNum = (TextView) findViewById(R.id.aotd_delivery_orderNum_btn);
		accountName = (TextView) findViewById(R.id.aotd_delivery_accountName_txt);
		accountNotes = (TextView) findViewById(R.id.aotd_delivery_accountNotes_txt);
		rfc = (TextView) findViewById(R.id.aotd_delivery_rfc_txt);
		pickUp_from = (TextView) findViewById(R.id.aotd_delivery_PUaddress_txt);
		delivery_to = (TextView) findViewById(R.id.aotd_delivery_DLaddress_txt);
		puinstruction = (TextView) findViewById(R.id.aotd_delivery_PUInstruction_txt);
		dlinstruction = (TextView) findViewById(R.id.aotd_delivery_DLInstruction_txt);
		serviceType = (TextView) findViewById(R.id.aotd_delivery_serviceType_txt);
		pickupReady = (TextView) findViewById(R.id.aotd_delivery_pickupReady_txt);
		requestedBy = (TextView) findViewById(R.id.aotd_delivery_requiredBy_txt);
		pieces = (TextView) findViewById(R.id.aotd_delivery_piece_txt);
		weight = (TextView) findViewById(R.id.aotd_delivery_weight_txt);
		roundTrip = (TextView) findViewById(R.id.aotd_roundtrip);
		adminNotes = (TextView) findViewById(R.id.aotd_delivery_adminNotes_txt);
		scanBottom = (Button)findViewById(R.id.aotd_scan_bottom);
		deliverBottom = (Button)findViewById(R.id.atod_delivered_bottom);
		btnCamera = (Button)findViewById(R.id.camImage);

		mDetailDeliveryModel = new DetailDeliveryModel();

		imgOnline = (ImageView) findViewById(R.id.aotd_img_mode);
		if (Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);

	}
	

	private void setData()
	{
		OrderNumber = getIntent().getStringExtra("orderNumber");
		
		ArrayList<DispatchAllListModel> al = Question.offDel;
		DispatchAllListModel om = al.get(0);
		String acc_name = om.getAccountName();
		String acc_Notes = om.getAccountnotes();
		String acc_admin_notes= om.getAdminnotes();
		
		String from_Comp = om.getCompany();
		String from_Add= om.getAddress();
		String from_Suit= om.getSuit();
		String from_City= om.getCity();
		String from_State= om.getState();
		String from_zip= om.getZip();
		String from_cell= om.getPuCellPhone();
		String from_home= om.getPuHomephone();
		
		to_Comp = om.getDlcompany();
		String to_add = om.getDladdress();
		String to_suit= om.getDlsuit();
		String to_city= om.getDlcity();
		String to_state= om.getDlstate();
		String to_zip= om.getDlzip();
		String to_cell= om.getDlCellPhone();
		String to_home= om.getDlHomePhone();
		
		String rt= om.getIsRoundTrip();
		String service= om.getHour();
		String piece= om.getPeice();
		String weight_= om.getWeight();
		
		orderNum.setText(OrderNumber);
		accountName.setText(acc_name);
		rfc.setText("");
		
		serviceType.setText(service);
		pickupReady.setText("");
		requestedBy.setText("");
		pieces.setText(piece);
		weight.setText(weight_);
		
		TextView tvDLCOmpany = (TextView)findViewById(R.id.DL_Company); tvDLCOmpany.setText("Company: "+to_Comp);
		TextView tvDLAddress= (TextView)findViewById(R.id.DL_Address);tvDLAddress.setText("Address: "+to_add);
		TextView tvDlSite = (TextView)findViewById(R.id.DL_Suite);tvDlSite.setText("Suite: "+to_suit);
		TextView tvDLCity = (TextView)findViewById(R.id.DL_City);tvDLCity.setText("City: "+to_city);
		TextView tvDLState = (TextView)findViewById(R.id.DL_State);tvDLState.setText("State: "+to_state);
		TextView tvDLZip = (TextView)findViewById(R.id.DL_Zip);tvDLZip.setText("Zip: "+to_zip);

		TextView tvPUCOmpany = (TextView)findViewById(R.id.PU_Company); tvPUCOmpany.setText("Company: "+from_Comp);
		TextView tvPUAddress= (TextView)findViewById(R.id.PU_Address);tvPUAddress.setText("Address: "+from_Add);
		TextView tvPUSite = (TextView)findViewById(R.id.PU_Suite);tvPUSite.setText("Suite: "+from_Suit);
		TextView tvPUCity = (TextView)findViewById(R.id.PU_City);tvPUCity.setText("City: "+from_City);
		TextView tvPUState = (TextView)findViewById(R.id.PU_State);tvPUState.setText("State: "+from_State);
		TextView tvPUZip = (TextView)findViewById(R.id.PU_Zip);tvPUZip.setText("Zip: "+from_zip);
		
		
		
		
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.off_delivered_page);
		
		Bundle bundleObject = getIntent().getExtras();
		final ArrayList<DispatchAllListModel> classObject = (ArrayList<DispatchAllListModel>)bundleObject.getSerializable("key");
	
		
		intializeUI();
		setData();
		
		delivered.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String DLName_ = to_Comp;
				String sPeice = pieces.getText().toString().trim();
				Question.setDLName(DLName_);
				Question.setPIECE(sPeice);
				
				if(Utils.checkNetwork(Off_delivered_page.this))
				{/*
					String pdfUrl = String.format(Utils.PDF_DOWNLOAD_URL,OrderNumber);
					String newFolder = "/AOTD";
					String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
					File myNewFolder = new File(extStorageDirectory + newFolder);
					
					if(myNewFolder.exists())
					{
						File file = new File(myNewFolder,OrderNumber+".pdf");
						if(file.exists())
						{
							Intent sign_intent = new Intent(Off_delivered_page.this,PdfSignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("company",to_Comp);
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);
						}
						else
						{
							pdfDownloadedView(pdfUrl);
						}
					}
					else
					{
						
					}
				*/}
				else
				{
					Question.mListClass = classObject;
					
					Intent sign_intent = new Intent(Off_delivered_page.this,SignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
					sign_intent.putExtra("deliveryType",deliveryType);
					sign_intent.putExtra("from", "aotd");
					sign_intent.putExtra("Piece", sPeice);
					startActivity(sign_intent);
				}
				
				
			}
		});
		
		Button btm_delivered = (Button)findViewById(R.id.atod_delivered_bottom);
		Button btm_scan = (Button)findViewById(R.id.aotd_scan_bottom);
		
		btm_delivered.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String DLName_ = to_Comp;
				String sPeice = pieces.getText().toString().trim();
				Question.setDLName(DLName_);
				Question.setPIECE(sPeice);
				
				if(Utils.checkNetwork(Off_delivered_page.this))
				{/*
					String pdfUrl = String.format(Utils.PDF_DOWNLOAD_URL,OrderNumber);
					String newFolder = "/AOTD";
					String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
					File myNewFolder = new File(extStorageDirectory + newFolder);
					
					if(myNewFolder.exists())
					{
						File file = new File(myNewFolder,OrderNumber+".pdf");
						if(file.exists())
						{
							Intent sign_intent = new Intent(Off_delivered_page.this,PdfSignatureActivity.class);
							sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
							sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
							sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
							sign_intent.putExtra("deliveryType", deliveryType);
							sign_intent.putExtra("company",to_Comp);
							sign_intent.putExtra("from", "aotd");
							sign_intent.putExtra("Piece", sPeice);
							startActivity(sign_intent);
						}
						else
						{
							pdfDownloadedView(pdfUrl);
						}
					}
					else
					{
						
					}
				*/}
				else
				{
					Question.mListClass = classObject;
					
					Intent sign_intent = new Intent(Off_delivered_page.this,SignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
					sign_intent.putExtra("deliveryType",deliveryType);
					sign_intent.putExtra("from", "aotd");
					sign_intent.putExtra("Piece", sPeice);
					startActivity(sign_intent);
				}
				
				
			}
		});
		
		btm_scan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent("com.vinscan.barcode.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});
		
		Button btnCamera = (Button)findViewById(R.id.camImage);
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent in = new Intent(Off_delivered_page.this,CameraActivity.class);
				in.putExtra("ORDERID", orderId);
				startActivity(in);

			}
		});
		
	}
	
	protected void pdfDownloadedView(String pdfUrl)
	{
		if (Utils.checkNetwork(Off_delivered_page.this))
		{
			DownLoadPDFAsyncForPending__ downLoadPDFAsync = new DownLoadPDFAsyncForPending__(Off_delivered_page.this,new PdfHandler());
			downLoadPDFAsync.execute(pdfUrl, OrderNumber);

		}

	}
	
	public class PdfHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {

			if(msg.what == 0){
				//				Toast.makeText(DeliveryOrderInfoDelivery.this, "Error occured while downloading.", Toast.LENGTH_LONG).show();

				Intent sign_intent = new Intent(Off_delivered_page.this,SignatureActivity.class);
				sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
				sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
				sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
				sign_intent.putExtra("deliveryType",deliveryType);
				sign_intent.putExtra("from", "aotd");

				startActivity(sign_intent);

			}else{
				File file = new File(Environment
						.getExternalStorageDirectory().toString(), "/AOTD/"+getIntent()
						.getStringExtra("orderNumber")+".pdf");
				if(file.exists()){
					// calling PdfSignatureActivity
					Intent sign_intent = new Intent(Off_delivered_page.this,PdfSignatureActivity.class);
					sign_intent.putExtra("orderNumber", getIntent().getStringExtra("orderNumber"));
					sign_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
					sign_intent.putExtra("condition", getIntent().getStringExtra("condition"));
					sign_intent.putExtra("deliveryType", deliveryType);
					sign_intent.putExtra("from", "aotd");
					startActivity(sign_intent);
				}else{
					Toast.makeText(Off_delivered_page.this, "File not found.", Toast.LENGTH_LONG).show();
				}
			}
		}

	}

}
