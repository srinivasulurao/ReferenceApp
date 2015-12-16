package com.aotd.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.helpers.AOTDDataBase;
import com.aotd.helpers.BackUpDataBase;
import com.aotd.helpers.PdfUploadAsync;
import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.Question;
import com.aotd.model.SignatureModel;
import com.aotd.parsers.UploadImageDataParser;
import com.aotd.utils.Utils;
import com.itextpdf.text.List;

/**
 * 
 * 
 * @author bharath
 *
 */

public class RoundTripActivity extends Activity implements OnClickListener{

	private static final String TAG = "RoundTripActivity";
	private TextView titleHeader = null, orderStatus;
	private Button mFinished = null;
	private Button mYes = null;
	private Button mNo  = null;
	private EditText mWaitTime = null;
	private EditText mNoBoxes = null;
	private RadioGroup transport;
	private RadioButton car = null;
	private RadioButton truck = null;
	public RadioButton radioTransportButton;
	private ArrayList<SignatureModel> marrSing;
	private ArrayList<String> notes = null;
	private ProgressDialog progressdialog;
	private Hashtable<String, String>		parameters = null;
	private String Condition;

	public ImageView imgOnline;
	private String mFileName = "";
	
	boolean isClicked = false;

	private int iCtr = 0;
	String Yes_or_No="";
	String Vehicle = "0";
	boolean first_time =true;
	boolean yes_button_clicked = false;
	boolean no_button_clicked = false;

	private String strWaitTime = "0";
	private String strNoBoxes = "0";


	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.aotd_round_trip);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		intializeUI();	
		Condition = getIntent().getStringExtra("condition");
		
		strNoBoxes = Question.PIECE;
		mNoBoxes.setText(strNoBoxes);
		
		
		
		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClicked)
				{
					if(Utils.NetworkType(RoundTripActivity.this).equalsIgnoreCase(Utils.wifi))
					{
						Utils.wifiOFF(RoundTripActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					else if(Utils.NetworkType(RoundTripActivity.this).equalsIgnoreCase(Utils.mobile))
					{
						Utils.mobileDataOFF(RoundTripActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					
				}
				else
				{
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(RoundTripActivity.this);
				}

				return false;
			}
		});
		

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(Utils.NetworkType(RoundTripActivity.this).equalsIgnoreCase(Utils.wifi))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else if(Utils.NetworkType(RoundTripActivity.this).equalsIgnoreCase(Utils.mobile))
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

	private void intializeUI() {

		mYes 		=   (Button)findViewById(R.id.roundtrip_btn_yes);
		mNo 		= 	(Button)findViewById(R.id.roundtrip_btn_no);
		mFinished 	= 	(Button)findViewById(R.id.roundtrip_btn_finished);
		mWaitTime 	= 	(EditText)findViewById(R.id.roundtrip_et_wait_time);
		mNoBoxes 	= 	(EditText)findViewById(R.id.roundtrip_et_no_of_boxes);
		titleHeader = 	(TextView)findViewById(R.id.roundtrip_layout_title);
		orderStatus = 	(TextView)findViewById(R.id.roundtrip_txt_order_status);
		car			=	(RadioButton)findViewById(R.id.radio_car);
		truck		=	(RadioButton)findViewById(R.id.radio_truck);
		transport	=	(RadioGroup)findViewById(R.id.roundtrip_radio_choose);

		titleHeader.setText(getIntent().getStringExtra("orderNumber").trim());

		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);

		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);


		mFinished.setOnClickListener(this);
		mYes.setOnClickListener(this);
		mNo.setOnClickListener(this);

		notes = getIntent().getExtras().getStringArrayList("notes");
		marrSing = (ArrayList<SignatureModel>) getIntent().getExtras().getSerializable("bitmap"); 



		String Condition = getIntent().getStringExtra("condition");


		Log.e("", "*********** Condition "+Condition);
		if(Condition.equals("UpdateSecondSignatureForDeliver"))
		{

			mYes.setEnabled(false);
			mNo.setEnabled(false);
			mNoBoxes.setEnabled(false);
			mWaitTime.setEnabled(false);
			transport.setEnabled(false);
			car.setEnabled(false);
			truck.setEnabled(false);

			mYes.setBackgroundResource(R.drawable.aotd_btn_yes_no_disable);
			mNo.setBackgroundResource(R.drawable.aotd_btn_yes_no_disable);

			mFinished.setEnabled(true);
			mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
			orderStatus.setText("Status: RoundTrip Delivery");

		}else if(Condition.equals("UpdateRoundTripPickup") || Condition.equals("UpdateRoundTripPickupByDriver")){

			mYes.setEnabled(false);
			mNo.setEnabled(false);
			mYes.setBackgroundResource(R.drawable.aotd_btn_yes_no_clk);
			mNo.setBackgroundResource(R.drawable.aotd_btn_yes_no_disable);
			mFinished.setEnabled(true);
			mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
			orderStatus.setText("Status: RoundTrip PickUp");
			Yes_or_No = "yes";

		}

	}




	@Override
	public void onClick(View v) {


		switch (v.getId()) {


		case R.id.roundtrip_btn_yes:

			if(Utils.isRoundTrip.equalsIgnoreCase("none")){

				mNoBoxes.setEnabled(true);
				mWaitTime.setEnabled(true);
				transport.setEnabled(true);
				car.setEnabled(true);
				truck.setEnabled(true);

				mFinished.setEnabled(true);
				mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
				mYes.setBackgroundResource(R.drawable.aotd_btn_yes_no_clk);
				mNo.setBackgroundResource(R.drawable.roundtrip_yes_or_no);
				Yes_or_No = "yes";

				no_button_clicked = false;
				yes_button_clicked = true;

			}else{

				if(Condition.equals("updateSingDelivered")){

					if(Utils.isRoundTrip.equalsIgnoreCase("1")){

						mNoBoxes.setEnabled(true);
						mWaitTime.setEnabled(true);
						transport.setEnabled(true);
						car.setEnabled(true);
						truck.setEnabled(true);

						orderStatus.setText("Status: Delivery -> RoundTrip PickUp");
						mFinished.setEnabled(true);
						mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
						mYes.setBackgroundResource(R.drawable.aotd_btn_yes_no_clk);
						mNo.setBackgroundResource(R.drawable.roundtrip_yes_or_no);
						Yes_or_No = "yes";

						no_button_clicked = false;
						yes_button_clicked = true;

					}else{

						if(!yes_button_clicked)
							roundTripAlertDialog();
					}
				}
			}


			break;

		case R.id.roundtrip_btn_no:		

			mNoBoxes.setEnabled(false);
			mWaitTime.setEnabled(false);
			transport.setEnabled(false);
			car.setEnabled(false);
			truck.setEnabled(false);

			no_button_clicked = true;
			yes_button_clicked = false;

			if(!Utils.isRoundTrip.equalsIgnoreCase("none")){

				if(Condition.equals("updateSingDelivered")){

					if(Utils.isRoundTrip.equalsIgnoreCase("1"))
						orderStatus.setText("Status: Only Delivery");
					else
						orderStatus.setText("Status: Delivery");
				}
			}

			mFinished.setEnabled(true);
			mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
			mNo.setBackgroundResource(R.drawable.aotd_btn_yes_no_clk);
			mYes.setBackgroundResource(R.drawable.roundtrip_yes_or_no);
			Yes_or_No = "no";

			break;

		case R.id.roundtrip_btn_finished:	

			strWaitTime = mWaitTime.getText().toString().trim();

			if(strWaitTime.length()<1)
				strWaitTime = "0";

			strNoBoxes = mNoBoxes.getText().toString().trim();

			if(strNoBoxes.length()<1)
				strNoBoxes = "0";

			int selectedTransportId = transport.getCheckedRadioButtonId();

			radioTransportButton = (RadioButton) findViewById(selectedTransportId);

			if (radioTransportButton.getText().toString().trim().equalsIgnoreCase("car")) {
				Vehicle="0";
			}else{
				Vehicle="1";	
			}

			//			if(car.isSelected())
			//				Vehicle="0";
			//			else
			//				Vehicle="1";
			//			
			Utils.isRoundTrip = "none";


			if(Condition.equals("updateSingDelivered"))
			{	
				if(Utils.checkNetwork(getApplicationContext()))
				{

					String s=Yes_or_No;
					Log.i("","kk yes r no :"+Yes_or_No);
					PdfUploadAsync mPdfUploadAsync = new PdfUploadAsync(RoundTripActivity.this,getIntent().getStringExtra("orderNumber").trim(),Condition, new UploadPdfHandler(),Yes_or_No);
					mPdfUploadAsync.execute();

					


				}else{
					Log.i(TAG, "kkk doFinishOffline.....");

					doFinishOffline();	

				}

			}else if(Condition.equals("UpdateRoundTripPickup") || Condition.equals("UpdateRoundTripPickupByDriver")){
				Log.i(TAG, "kkk Condition.....UpdateRoundTripPickup ");

				if(Yes_or_No.equalsIgnoreCase("yes"))
				{
					if(Utils.checkNetwork(getApplicationContext()))
					{

						progressdialog = ProgressDialog.show(RoundTripActivity.this,null,null);
						progressdialog.setContentView(R.layout.loader);
						String url = String.format(Utils.UPDATE_ROUNDTRIP_URL,Utils.ROLENAME.trim(),
								getIntent().getStringExtra("orderNumber").trim(),Yes_or_No,strWaitTime,
								Vehicle,strNoBoxes,Yes_or_No);
						Log.v("roundtrip url","roundtrip url"+url);
						UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(),url, "aotd");
						mUploadImageDataParser.start();

					}else{

						AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
						parameters = null;
						parameters = new Hashtable<String, String>();
						parameters.put( "isRoundTrip", "1");
						parameters.put( "PCRoundTrip", "1");
						parameters.put("waittime", strWaitTime);
						parameters.put("boxes", strNoBoxes);
						parameters.put("vehicle", Vehicle);

						deleteSignature();

						_AOTDDataBase.updateOrderRoundTripPickup(getIntent().getStringExtra("orderNumber").trim(), parameters, "update");
						alertDialogWithMsg("AOTD", "Order round trip picked-up updated successfully");	


					}
				}

			}else if(Condition.equals("UpdateSecondSignatureForDeliver")){
				Log.i(TAG, "kkk Condition.....UpdateSecondSignatureForDeliver ");

				Yes_or_No = "no";
				if(Utils.checkNetwork(getApplicationContext()))
				{


					Log.i("","kk yes r no :"+Yes_or_No);
					PdfUploadAsync mPdfUploadAsync = new PdfUploadAsync(RoundTripActivity.this,getIntent().getStringExtra("orderNumber").trim(),Condition, new UploadRTPdfHandler(),Yes_or_No);
					mPdfUploadAsync.execute();



					//					progressdialog = ProgressDialog.show(RoundTripActivity.this, "", "please wait");
					//					
					//					UpdateSecondSignatureForDeliverOnline();


				}else{
					//UpdateSecondSignatureForDeliverOffline();
					
					doFinishOffline();



				}

			}

			break;

		default:
			break;
		}

	}

	private void UpdateSecondSignatureForDeliverOffline() {
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		parameters = null;
		parameters = new Hashtable<String, String>();
		parameters.put("lastnamedl", marrSing.get(0).getLastname());
		parameters.put( "SignRoundTrip", String.format("rt%s.jpg",getIntent().getStringExtra("orderNumber").trim()));
		parameters.put("waittime", strWaitTime);
		parameters.put("boxes", strNoBoxes);
		parameters.put("vehicle", Vehicle);
		//			if(Yes_or_No.equalsIgnoreCase("yes"))
		//			{
		//				parameters.put( "isRoundTrip", "1");
		//				parameters.put( "PCRoundTrip", "1");
		//			}else{
		//				parameters.put( "isRoundTrip", "0");
		//				parameters.put( "PCRoundTrip", "0");
		//			}
		deleteSignature();

		_AOTDDataBase.updateOrderRoundTripDelivery(getIntent().getStringExtra("orderNumber").trim(), parameters, marrSing.get(0).getBytes(), "update");
		alertDialogWithMsg("AOTD", "Order round trip delivery updated successfully");	


	}

	private void UpdateSecondSignatureForDeliverOnline() {

		Log.v("second sign url","second sign url");
		String url = String.format(Utils.UPLOAD_SECONDSIGN_DATA,Utils.ROLENAME.trim(),
				getIntent().getStringExtra("orderNumber").trim(),Yes_or_No,strWaitTime,
				Vehicle,strNoBoxes,Yes_or_No);
		parameters = null;
		parameters = new Hashtable<String, String>();
		parameters.put("notes", notes.get(0));
		parameters.put("lastname", marrSing.get(0).getLastname());

		UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(),url, "aotd");
		mUploadImageDataParser.isMultipart=true;
		//		mUploadImageDataParser.isSendingPdf=true;
		mUploadImageDataParser.params = parameters;

		//convert pdf to bytes
		//		String filePath = Environment.getExternalStorageDirectory()+ "/AOTD/"+getIntent()
		//				.getStringExtra("orderNumber")+"_sign.pdf";
		//		 File file = new File(filePath);
		//		 byte[] bytes = null;
		//		   try {
		//	        FileInputStream fis = new FileInputStream(file);
		//	        
		//	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//	        byte[] buf = new byte[1024];
		//	     
		//	            for (int readNum; (readNum = fis.read(buf)) != -1;) {
		//	                bos.write(buf, 0, readNum); //no doubt here is 0
		//	                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
		//	                Log.i(TAG, "read " + readNum + " bytes,");
		//	            }
		//	       
		//	        bytes = bos.toByteArray();
		//		   } catch (Exception e) {
		//	            Log.e(TAG, e.toString());
		//	        }
		//		
		//		   Log.i(TAG, "Pdf bytes rtdelivered: "+bytes);
		//		   mUploadImageDataParser.imgBytes = bytes;
		//	       mFileName = String.format("rt%s.pdf",getIntent().getStringExtra("orderNumber").trim());
		//			


		mUploadImageDataParser.imgBytes = marrSing.get(0).getBytes();
		mFileName = String.format("rt%s.jpg",getIntent().getStringExtra("orderNumber").trim());

		mUploadImageDataParser.mFileName = mFileName;
		mUploadImageDataParser.start(); 
	}

	private void deleteSignedPdf() {
		String dlOrRt;
		if(getIntent().getStringExtra("condition").equalsIgnoreCase("updateSingDelivered"))
		{
			dlOrRt = "dl";
		}else {
			dlOrRt = "rt";
		}
		String path = Environment.getExternalStorageDirectory()+ "/AOTD/";

		String signedPDFPath =path+ dlOrRt+getIntent()
				.getStringExtra("orderNumber")+"_sign.pdf";
		File mFile  = new File(path+getIntent()
				.getStringExtra("orderNumber")+".pdf");
		File file  = new File(signedPDFPath);
		file.delete();
		mFile.delete();

	}	

	private void deleteSignature(){

		try {

			BackUpDataBase _BackUpDataBase = new BackUpDataBase(RoundTripActivity.this);
			_BackUpDataBase.deleteData(getIntent().getStringExtra("orderNumber").trim());

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void doFinishOnline(){

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		String url = String.format(Utils.UPLOAD_FIRSTSIGN_DATA,Utils.ROLENAME.trim(),
				getIntent().getStringExtra("orderNumber").trim(),Yes_or_No,strWaitTime,
				Vehicle,strNoBoxes,Yes_or_No);
		parameters = null;
		parameters = new Hashtable<String, String>();
		parameters.put("notes", notes.get(0));
		parameters.put("lastname", marrSing.get(0).getLastname());
		parameters.put("datetime", currentDateandTime);

		UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new DataUploadHandler(),url, "aotd");
		mUploadImageDataParser.isMultipart=true;
		mUploadImageDataParser.params = parameters;

		mUploadImageDataParser.imgBytes = marrSing.get(0).getBytes();
		mFileName = String.format("dl%s.jpg",getIntent().getStringExtra("orderNumber").trim());



		mUploadImageDataParser.mFileName = mFileName;
		mUploadImageDataParser.start();
	}


	private void doFinishOffline(){

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		//String order_id = getIntent().getStringExtra("orderNumber");
		//sdf.applyPattern("yyyy-MM-dd hh:mm:ss");
		String mydate =  sdf.format(date);
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		parameters = null;
		parameters = new Hashtable<String, String>();
		parameters.put("lastname", marrSing.get(0).getLastname());
		parameters.put("notes", notes.get(0));
		parameters.put("DLDate", mydate);
		//parameters.put("signature" , marrSing.get(0).getBytes());
		parameters.put("waittime", strWaitTime);
		parameters.put("boxes", strNoBoxes);
		parameters.put("vehicle", Vehicle);
		if(Yes_or_No.equalsIgnoreCase("yes"))
		{
			parameters.put( "isRoundTrip", "1");
			parameters.put( "PCRoundTrip", "1");
		}else{
			parameters.put( "isRoundTrip", "0");
			parameters.put( "PCRoundTrip", "0");
		}
		
		mFileName = String.format("dl%s.jpg",getIntent().getStringExtra("orderNumber").trim());
		parameters.put("SignDelivery",  String.format("dl%s.jpg",getIntent().getStringExtra("orderNumber").trim()));

		deleteSignature();

		String url = String.format(Utils.UPLOAD_FIRSTSIGN_DATA,Utils.ROLENAME.trim(),
				getIntent().getStringExtra("orderNumber").trim(),Yes_or_No,strWaitTime,
				Vehicle,strNoBoxes,Yes_or_No);

		byte[] b = marrSing.get(0).getBytes();
		String imgString = Base64.encodeToString(b, Base64.DEFAULT);

		try{
			ArrayList<DispatchAllListModel> aListClasss = Question.mListClass;
			DispatchAllListModel dm = aListClasss.get(0);
			String sOrderNum = dm.getOrder_id();
			String sDate = dm.getRDDate();
			String sStatus="Picked up";
			String sFromComp = dm.getCompany();
			String sToComp = dm.getDlcompany();
			String sFromAdd = dm.getAddress();
			String sToAddress = dm.getDladdress();
			
			JSONArray jArray = new JSONArray();
			JSONObject jObject = new JSONObject();
			jObject.put("LASTNAME", marrSing.get(0).getLastname());
			jObject.put("NOTES", notes.get(0));
			jObject.put("WAITTIME", strWaitTime);
			jObject.put("BOXES", strNoBoxes);
			jObject.put("URL", url);
			jObject.put("IMGSTR", imgString);
			jObject.put("FILENAME", mFileName);
			jObject.put("ORDER", sOrderNum);
			jObject.put("STATUS", sStatus);
			jObject.put("DATE", sDate);
			jObject.put("FROM_COMPANY", sFromComp);
			jObject.put("TO_COMPANY", sToComp);
			jObject.put("FROM_ADDRESS", sFromAdd);
			jObject.put("TO_ADDRESS", sToAddress);
			jObject.put("YES_NO", Yes_or_No);
			
			if(Condition.equals("updateSingDelivered"))
			{
				jObject.put("TAG", "1");
			}
			else
			{
				jObject.put("TAG", "2");
			}

			jArray.put(jObject);
			
			OfflineDB db = new OfflineDB(RoundTripActivity.this);
			db.addDataDeliver("Delivered", dm, jObject);
			
			Intent in = new Intent(RoundTripActivity.this,PendingOrders.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(in);
			finish();
			
			/*SharedPreferences preferences_ = PreferenceManager.getDefaultSharedPreferences(this);
			String RT_Data = preferences_.getString("ROUND_TRIP_OFFLINE","");
			
			JSONArray ja = new JSONArray();
			if(RT_Data.equals(""))
			{
				 ja = new JSONArray();
			}
			else
			{
				ja = new JSONArray(RT_Data);
			}
			if(ja.length()==0) // NO DATA
			{
				JSONArray jArray = new JSONArray();
				JSONObject jObject = new JSONObject();
				jObject.put("LASTNAME", marrSing.get(0).getLastname());
				jObject.put("NOTES", notes.get(0));
				jObject.put("WAITTIME", strWaitTime);
				jObject.put("BOXES", strNoBoxes);
				jObject.put("URL", url);
				jObject.put("IMGSTR", imgString);
				jObject.put("FILENAME", mFileName);
				jObject.put("ORDER", sOrderNum);
				jObject.put("STATUS", sStatus);
				jObject.put("DATE", sDate);
				jObject.put("FROM_COMPANY", sFromComp);
				jObject.put("TO_COMPANY", sToComp);
				jObject.put("FROM_ADDRESS", sFromAdd);
				jObject.put("TO_ADDRESS", sToAddress);

				jArray.put(jObject);
				int c = jArray.length();
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("ROUND_TRIP_OFFLINE",jArray.toString());
				editor.commit();
			}
			else  // DATA PRESENT
			{
				JSONArray jArray = new JSONArray(RT_Data);
				JSONObject jObject = new JSONObject();
				jObject.put("LASTNAME", marrSing.get(0).getLastname());
				jObject.put("NOTES", notes.get(0));
				jObject.put("WAITTIME", strWaitTime);
				jObject.put("BOXES", strNoBoxes);
				jObject.put("URL", url);
				jObject.put("IMGSTR", imgString);
				jObject.put("FILENAME", mFileName);
				jObject.put("ORDER", sOrderNum);
				jObject.put("STATUS", sStatus);
				jObject.put("DATE", sDate);
				jObject.put("FROM_COMPANY", sFromComp);
				jObject.put("TO_COMPANY", sToComp);
				jObject.put("FROM_ADDRESS", sFromAdd);
				jObject.put("TO_ADDRESS", sToAddress);

				jArray.put(jObject);

				int c = jArray.length();
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("ROUND_TRIP_OFFLINE",jArray.toString());
				editor.commit();
			}
*/




		}catch(Exception e){}



		//_AOTDDataBase.updateOrderDelivery(getIntent().getStringExtra("orderNumber").trim(), parameters, marrSing.get(0).getBytes(), "update");
		alertDialogWithMsg("AOTD", "Order delivery updated successfully");	

	}

	//	Called when the sync id done

	class getSyncHandler extends Handler{

		public void handleMessage(android.os.Message msg)
		{

			if(msg.what == 10){

				Log.e("Sync Data", "******************* getOnlineOrders 10");
				doFinishOnline();

			}else if(msg.what == 11){

				Log.e("Sync Data", "******************* getOfflineOrders 11");
				progressdialog.dismiss();
				doFinishOffline();

			}else if(msg.what == 12){

				Log.e("Sync Data", "******************* getOnlineOrders 12");
				doFinishOnline();
			}
		}
	}



	class DataUploadHandler extends Handler
	{

		public void handleMessage(android.os.Message msg)
		{ 
			if(progressdialog != null && progressdialog.isShowing()){
				progressdialog.dismiss();

			}
			//Log.v("array size",""+marrSing.size());
			String errorMsg=msg.getData().getString("HttpError");
			String successMsg=msg.getData().getString("success");


			if(errorMsg.length()>0){

				Log.v("erromsg",errorMsg);
				alertDialogWithMsg("AOTD", errorMsg);	

			}else{
				
				try{
					ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();  
					pairs.add(new BasicNameValuePair("order_id",getIntent().getStringExtra("orderNumber").trim()));
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost("http://50.63.55.253/trigger_dispatch_mails.php");
					
					httpPost.setEntity(new UrlEncodedFormEntity(pairs));

					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					String str2 = EntityUtils.toString(httpEntity);
					
					String s = str2;
					
					

					
				}catch(Exception e){}
				

				deleteSignature();
				//				deleteSignedPdf();

				if(Condition.equals("updateSingDelivered"))
				{
					//alertDialogWithMsg("AOTD", successMsg);	
					AOTDDataBase _AOTDDataBase = new AOTDDataBase(RoundTripActivity.this);
					parameters.remove("DLDate");
					parameters.put("DLDate", successMsg);
					parameters.put( "SignDelivery", mFileName);

					parameters.put("waittime", strWaitTime);
					parameters.put("boxes", strNoBoxes);
					parameters.put("vehicle", Vehicle);

					//_AOTDDataBase.updateOrderDelivery(getIntent().getStringExtra("orderNumber").trim(), parameters, marrSing.get(0).getBytes(), "none");
					alertDialogWithMsg("AOTD", "Order delivery updated successfully");	

				}else if(Condition.equals("UpdateRoundTripPickup") || Condition.equals("UpdateRoundTripPickupByDriver")){
					AOTDDataBase _AOTDDataBase = new AOTDDataBase(RoundTripActivity.this);
					parameters = null;
					parameters = new Hashtable<String, String>();
					parameters.put( "isRoundTrip", "1");
					parameters.put( "PCRoundTrip", "1");

					//_AOTDDataBase.updateOrderRoundTripPickup(getIntent().getStringExtra("orderNumber").trim(), parameters, "none");
					alertDialogWithMsg("AOTD", successMsg);	
				}else if(Condition.equals("UpdateSecondSignatureForDeliver")){


					AOTDDataBase _AOTDDataBase = new AOTDDataBase(RoundTripActivity.this);
					parameters = null;
					parameters = new Hashtable<String, String>();
					parameters.put("lastnamedl", marrSing.get(0).getLastname());
					parameters.put( "SignRoundTrip", mFileName);
					//parameters.put("DLDate", mydate);
					//parameters.put("signaturedl" , marrSing.get(0).getBytes());
					//						parameters.put("notes", notes.get(0));
					parameters.put("waittime", strWaitTime);
					parameters.put("boxes", strNoBoxes);
					parameters.put("vehicle", Vehicle);
					/*if(Yes_or_No.equalsIgnoreCase("yes"))
						{
							parameters.put( "isRoundTrip", "yes");
							parameters.put( "PCRoundTrip", "yes");
						}else{
							parameters.put( "isRoundTrip", "no");
							parameters.put( "PCRoundTrip", "no");
						}*/
					//_AOTDDataBase.updateOrderDelivery(getIntent().getStringExtra("orderNumber").trim(), parameters, marrSing.get(0).getBytes(), "none");
					alertDialogWithMsg("AOTD", successMsg);	
				}

			}
		}



	}

	public void alertDialogWithMsg(String title, String msg){	

		//progressdialog.dismiss();
		new AlertDialogMsg(RoundTripActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which){


				startActivity(new Intent(RoundTripActivity.this,MainDispatchScreenTabView.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra("from", "roundtrip"));
				//				MainDispatchScreenTabView.tabHost.setCurrentTab(1);
				finish();

			}

		}).create().show();		
	}


	private void roundTripAlertDialog(){


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("AOTD");
		builder.setMessage("Do you wish to choose RonudTrip manually.")
		.setCancelable(false)					       
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {



			}
		})

		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				mNoBoxes.setEnabled(true);
				mWaitTime.setEnabled(true);
				transport.setEnabled(true);
				car.setEnabled(true);
				truck.setEnabled(true);

				no_button_clicked = false;
				yes_button_clicked = true;

				orderStatus.setText("Status: Delivery -> RoundTrip PickUp");
				mFinished.setEnabled(true);
				mFinished.setBackgroundResource(R.drawable.aotd_btn_yes_no);
				mYes.setBackgroundResource(R.drawable.aotd_btn_yes_no_clk);
				mNo.setBackgroundResource(R.drawable.roundtrip_yes_or_no);
				Yes_or_No = "yes";

			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}


	class UploadPdfHandler extends Handler{

		public void handleMessage(android.os.Message msg)
		{
			Yes_or_No = msg.getData().getString("yes_or_No");
			Log.i("","kk yes r no :"+Yes_or_No);

			if(msg.what == 200){

				//				deleteSignedPdf();

				if(Utils.checkNetwork(RoundTripActivity.this)){
					progressdialog = ProgressDialog.show(RoundTripActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader);
					doFinishOnline();
				}else{
					doFinishOffline();

				}


			}else if(msg.what != 200){

				/** modified for offline upload pdf */
				/*try{
					String path = Environment.getExternalStorageDirectory()+ "/AOTD/"+getIntent()
							.getStringExtra("orderNumber")+".pdf";
					if(new File(path).exists()){

						File file  = new File(path);
						if(file.exists()){
							file.delete();
						}						
					}
				}catch(Exception e){
					e.printStackTrace();
				}*/


				if(Utils.checkNetwork(RoundTripActivity.this)){
					progressdialog = ProgressDialog.show(RoundTripActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader);
					doFinishOnline();
				}else{
					doFinishOffline();

				}
			}
		}
	}

	class UploadRTPdfHandler extends Handler{

		public void handleMessage(android.os.Message msg)
		{
			Yes_or_No = msg.getData().getString("yes_or_No");
			Log.i("","kk yes r no :"+Yes_or_No);

			if(msg.what == 200){
				deleteSignedPdf();

				if(Utils.checkNetwork(RoundTripActivity.this)){
					progressdialog = ProgressDialog.show(RoundTripActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader);
					UpdateSecondSignatureForDeliverOnline();
				}else{
					UpdateSecondSignatureForDeliverOffline();

				}


			}else if(msg.what != 200){

				try{
					String path = Environment.getExternalStorageDirectory()+ "/AOTD/"+getIntent()
							.getStringExtra("orderNumber")+".pdf";
					if(new File(path).exists()){

						File file  = new File(path);
						if(file.exists()){
							file.delete();
						}						
					}
				}catch(Exception e){
					e.printStackTrace();
				}


				if(Utils.checkNetwork(RoundTripActivity.this)){
					progressdialog = ProgressDialog.show(RoundTripActivity.this,null,null);
					progressdialog.setContentView(R.layout.loader);
					UpdateSecondSignatureForDeliverOnline();
				}else{
					UpdateSecondSignatureForDeliverOffline();

				}
			}
		}
	}
}

