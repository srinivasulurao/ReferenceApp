package com.aotd.activities;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.aotd.View.SignatureView;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.dialog.EnterLastNameDialog;
import com.aotd.helpers.BackUpDataBase;
import com.aotd.helpers.RNADataBase;
import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.Question;
import com.aotd.model.SignatureModel;
import com.aotd.parsers.GetNoteParser;
import com.aotd.parsers.UpdateNoteParser;
import com.aotd.parsers.UploadImageDataParser;
import com.aotd.utils.Utils;
import com.itextpdf.text.List;
import com.rna.activities.RNATabActivity;

public class SignatureActivity extends Activity implements OnClickListener {

	private Button 			btnLastName = null;
	private RelativeLayout 	signatureCanvasContainer = null, signature_layout=null;
	private SignatureView 	signatureView = null;
	public Bitmap			signatureBitmap = null;
	public Boolean 			moveDone = false;
	private TextView		txt_title = null;
	private String 			mOrderNum;	
	private TextView 		titleHeader = null;
	private ImageButton 	addSign = null;
	private Button 			mSign = null;
	private EditText 		edt_notes = null;
	private ProgressDialog progressdialog;
	public ViewFlipper 		viewFlipper;
	public ArrayList<SignatureModel>   marrSignModel = null;
	private ArrayList<String>   notes = null;
	String from = "", deliveryType;
	String batchId = "";
	String rna_data = "";
	String rna_nhomecode = "";
	String rna_xmlData = "";
	String sName = "";
	String sRelation ="";

	String DLPhoneCall="";
	String DLHomeCall="";

	String PUPhoneCall="";
	String PUHomeCall="";

	public ImageView imgOnline, imgOnlineTwo;



	private Hashtable<String, String>		parameters = null;

	private boolean to_aotd_database = true;

	String comp = "";
	String sPiece="";
	boolean isClicked=false;

	ArrayList<DetailDeliveryModel> classObject = new ArrayList<DetailDeliveryModel>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aotd_signature_page);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		 
		
		comp = Question.DLName;
		sPiece = getIntent().getStringExtra("Piece");
		
		
		if(Question.getDLPhoneCall()!=null){ DLPhoneCall=Question.getDLPhoneCall();} else{ DLPhoneCall="";}
		if(Question.getDLHomeCall()!=null ){ DLHomeCall=Question.DLHomeCall;} else{ DLHomeCall="";}
		if(Question.getPUHomeCall()!=null){ PUHomeCall=Question.PUHomeCall;} else{ PUHomeCall="";}
		if(Question.getPUPhoneCall()!=null){ PUPhoneCall=Question.PUPhoneCall;} else{ PUPhoneCall="";}
		
		



		TextView txtDate = (TextView)findViewById(R.id.textSam);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date date = new Date();
		txtDate.setText(dateFormat.format(date).toString());

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setMessage("Does "+ comp +" live here?")
		.setTitle("AOTD")
		.setCancelable(false)
		.setPositiveButton("YES",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
				sName = "YES";
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignatureActivity.this);
				final EditText input = new EditText(SignatureActivity.this);
				alertDialogBuilder.setTitle("What is your relationship to the patient?")

				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener(){
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int id){
						if(input.getText().toString().isEmpty())
						{
							Toast.makeText(getApplicationContext(), "Please Enter the text", Toast.LENGTH_LONG).show();
						}
						else
						{
							dialog.cancel();

							sRelation = input.getText().toString();
							from = getIntent().getStringExtra("from");
							deliveryType = getIntent().getStringExtra("deliveryType");

							Log.i("", "kkk condition in signature activity..."+" "+getIntent().getStringExtra("condition"));



							initializeUI();	

							if(from.equalsIgnoreCase("rna")){

								batchId = getIntent().getStringExtra("rna_batchId");
								rna_data = getIntent().getStringExtra("rna_data");
								rna_nhomecode = getIntent().getStringExtra("rna_nhomecode");
								rna_xmlData = getIntent().getStringExtra("rna_xmlData");
							}
						}
					}
				});

				AlertDialog alert = alertDialogBuilder.create();

				alert.setView(input);
				alert.show();
			}
		});
		alertDialogBuilder.setNegativeButton("NO",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
				sName="NO";
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignatureActivity.this);
				final EditText input = new EditText(SignatureActivity.this);
				alertDialogBuilder.setTitle("What is your relationship to the patient?")

				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener(){
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int id){

						if(input.getText().toString().isEmpty())
						{
							Toast.makeText(getApplicationContext(), "Please Enter the text", Toast.LENGTH_LONG).show();
						}
						else
						{
							dialog.cancel();
							sRelation = input.getText().toString();
							from = getIntent().getStringExtra("from");
							deliveryType = getIntent().getStringExtra("deliveryType");

							Log.i("", "kkk condition in signature activity..."+" "+getIntent().getStringExtra("condition"));

							initializeUI();	

							if(from.equalsIgnoreCase("rna")){

								batchId = getIntent().getStringExtra("rna_batchId");
								rna_data = getIntent().getStringExtra("rna_data");
								rna_nhomecode = getIntent().getStringExtra("rna_nhomecode");
								rna_xmlData = getIntent().getStringExtra("rna_xmlData");
							}
						}
					}
				});

				AlertDialog alert = alertDialogBuilder.create();

				alert.setView(input);
				alert.show();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();

		from = getIntent().getStringExtra("from");
		deliveryType = getIntent().getStringExtra("deliveryType");

		Log.i("", "kkk condition in signature activity..."+" "+getIntent().getStringExtra("condition"));

		initializeUI();	

		if(from.equalsIgnoreCase("rna")){

			batchId = getIntent().getStringExtra("rna_batchId");
			rna_data = getIntent().getStringExtra("rna_data");
			rna_nhomecode = getIntent().getStringExtra("rna_nhomecode");
			rna_xmlData = getIntent().getStringExtra("rna_xmlData");
		}
		
		
		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(isClicked)
				{
					if(Utils.NetworkType(SignatureActivity.this).equalsIgnoreCase(Utils.wifi))
					{
						Utils.wifiOFF(SignatureActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					else if(Utils.NetworkType(SignatureActivity.this).equalsIgnoreCase(Utils.mobile))
					{
						Utils.mobileDataOFF(SignatureActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}
					
				}
				else
				{
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(SignatureActivity.this);
				}

				return false;
			}
		});

		
	}

	
	

	private SignatureModel _SignatureModel = null;
	private byte[] signature = null;
	private Bitmap mBitmap = null;
	private String firstname = "";

	private void initializeUI(){



		mOrderNum = getIntent().getExtras().getString("orderNumber");
		signatureView = new SignatureView(this);

		try {

			Log.e("", "before getting data ");
			if(deliveryType.equalsIgnoreCase("first")){

				Log.e("", "before getting first sign ");


				BackUpDataBase _BackUpDataBase = new BackUpDataBase(this);
				_SignatureModel = _BackUpDataBase.getData(mOrderNum);

			}else{

				Log.e("", "before getting second sign ");


				BackUpDataBase _BackUpDataBase = new BackUpDataBase(this);
				_SignatureModel = _BackUpDataBase.getSecondSignData(mOrderNum);

			}

			Log.e("", "after getting data ");


			if(_SignatureModel != null){

				signature = _SignatureModel.getBytes();
				firstname = _SignatureModel.getLastname();

				Log.e("", "signature "+signature.length);


				mBitmap = BitmapFactory.decodeByteArray(signature , 0, signature.length);
				signatureView.setBitmap(mBitmap);		

				signatureView.movedone = true;

				Log.e("", "signature "+signature.length);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


		signatureCanvasContainer=(RelativeLayout)findViewById(R.id.signature_layout_convascontainer);
		signature_layout = (RelativeLayout)findViewById(R.id.signature_layout);
		signature_layout.addView(signatureView);


		txt_title = (TextView)findViewById(R.id.signature_layout_title);		
		txt_title.setText(mOrderNum);
		btnLastName=(Button)findViewById(R.id.signature_btn_lastname);
		btnLastName.setOnClickListener(this);		

		titleHeader 	= (TextView)findViewById(R.id.sign_second_layout_title);		
		mSign			= (Button)findViewById(R.id.sec_sign_complete);
		edt_notes		= (EditText)findViewById(R.id.aotd_sign_editText);

		titleHeader.setText(mOrderNum);

		viewFlipper  	= (ViewFlipper)findViewById(R.id.viewf);
		marrSignModel 	= new ArrayList<SignatureModel>();	

		mSign.setOnClickListener(this);

		notes = new ArrayList<String>();


		imgOnlineTwo = (ImageView)findViewById(R.id.aotd_img_mode_two);

		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
		notes.clear();


	}

	public void getNotIfExist(){

		String url = "";

		try{

			progressdialog = ProgressDialog.show(SignatureActivity.this,null,null);
			progressdialog.setContentView(R.layout.loader);
			String encodedOrderId =  URLEncoder.encode(mOrderNum,"UTF-8");
			url =String.format(Utils.GET_NOTE_URL,encodedOrderId);
			new GetNoteParser(url, new GetNoteHandler()).start();

		}catch(Exception e){
			Log.e("", "");
		}

		Log.v("url",url);
	}

	 public static String removeExtraWhiteSpaces(String str){
	        String myString = str.replaceAll("\\s+", " ").trim();
	        return myString;
	    }
	 
	public class GetNoteHandler extends Handler{

		@Override
		public void handleMessage(android.os.Message msg) 
		{
			progressdialog.dismiss();			
			String note=msg.getData().getString("note");
			if(note!=null&&note.length()>0)
			{
				edt_notes.setText(note+"\n\n"+"Does "+ comp +" live here : "+sName+"\n\n"+"What is your relationship to the patient : "+sRelation+"\n"+
						DLPhoneCall+"\n"+
						DLHomeCall+"\n"+
						PUPhoneCall+"\n"+
						PUHomeCall
						);

			}
			else
			{
				String str = "Does "+ comp +" live here : "+sName+"\n\n"+"What is your relationship to the patient : "+sRelation+"\n"+
						DLPhoneCall+"\n"+
						DLHomeCall+"\n"+
						PUPhoneCall+"\n"+
						PUHomeCall+"";
				
				edt_notes.setText(str);
				/*edt_notes.setText("Does "+ comp +" live here : "+sName+"\n\n"+"What is your relationship to the patient : "+sRelation+"\n"+
						DLPhoneCall+"\n"+
						DLHomeCall+"\n"+
						PUPhoneCall+"\n"+
						PUHomeCall
						);*/
			}
		}
	}

	public void uploadOrderNote(String note){

		try{

			String orderId   = URLEncoder.encode(mOrderNum,"UTF-8");
			String encodedNote =URLEncoder.encode(note,"UTF-8");
			String url =String.format(Utils.UPDATE_NOTE_URL,orderId,encodedNote);
			new UpdateNoteParser(url,null).start();

		}catch(Exception e){

			Log.e("upload note", e.getLocalizedMessage());	

		}


	}

	public void changeMode(){

		if(Utils.checkNetwork(getApplicationContext()))
			imgOnlineTwo.setBackgroundResource(R.drawable.online);
		else
			imgOnlineTwo.setBackgroundResource(R.drawable.offline);
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(Utils.NetworkType(SignatureActivity.this).equalsIgnoreCase(Utils.wifi))
		{
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		}
		else if(Utils.NetworkType(SignatureActivity.this).equalsIgnoreCase(Utils.mobile))
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

	private Bitmap timestampItAndSave(Bitmap toEdit){
	    Bitmap dest = Bitmap.createBitmap(toEdit.getWidth(), toEdit.getHeight(), Bitmap.Config.ARGB_8888);

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

	    Canvas cs = new Canvas(dest);
	    Paint tPaint = new Paint();
	    tPaint.setTextSize(35);
	    tPaint.setColor(Color.BLUE);
	    tPaint.setStyle(Style.FILL);
	    float height = tPaint.measureText("yY");
	    cs.drawText(dateTime, 20f, height+15f, tPaint);
	    try {
	        dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/timestamped.jpg")));
	    } catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return null;
	  }
	    return dest;
	}
	private boolean storeImage(Bitmap imageData, String filename) {
		//get path to external storage (SD card)
		String iconsStoragePath = Environment.getExternalStorageDirectory() +"/myAppDir/myImages/";
		File sdIconStorageDir = new File(iconsStoragePath);

		//create storage directories, if they don't exist
		sdIconStorageDir.mkdirs();

		try {
			String filePath = sdIconStorageDir.toString() + filename;
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);

			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			//choose another format if PNG doesn't suit you
			imageData.compress(CompressFormat.PNG, 100, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.w("TAG", "Error saving image file: " + e.getMessage());
			return false;
		}

		return true;
	}
	
	Bitmap addTextToImage(Bitmap bm1)
	{
		Bitmap newBitmap = null;
		
		Config config = bm1.getConfig();
		   if(config == null)
		   {
		    config = Bitmap.Config.ARGB_8888;
		   }
		   
		   newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
		   
		   Canvas newCanvas = new Canvas(newBitmap);
		   newCanvas.drawBitmap(bm1, 0, 0, null);
		   
		   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			Date date = new Date();
			String sDate = dateFormat.format(date).toString();
		   String captionString = sDate;
		   
		   if(captionString != null)
		   {
			    
			    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			    paintText.setColor(Color.BLACK);
			    paintText.setTextSize(70);
			    paintText.setStyle(Style.FILL);
			  //  paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);
			    
			    Rect rectText = new Rect();
			    paintText.getTextBounds(captionString, 0, captionString.length(), rectText);
			    
			    int width = newBitmap.getWidth();
			    int height = newBitmap.getHeight();
			    
			    newCanvas.drawText(captionString, 
			      0, rectText.height(), paintText);
			    
			    Toast.makeText(getApplicationContext(), 
			      "drawText: " + captionString, 
			      Toast.LENGTH_LONG).show();
			    
			 }
		   else{
			    Toast.makeText(getApplicationContext(), 
			      "caption empty!", 
			      Toast.LENGTH_LONG).show();
			   }
		   
		   return newBitmap;
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.signature_btn_lastname:			




			if(_SignatureModel == null){

				if(signatureView.movedone){

					signatureBitmap=signatureView.mBitmap;
					 Bitmap mbmp = addTextToImage(signatureBitmap);
					 signatureBitmap = mbmp;
					 
					// storeImage(mbmp, "add.jpg");
					

					new EnterLastNameDialog(SignatureActivity.this,mOrderNum,getIntent().getStringExtra("condition")).show();

				}else{

					AlertDialogMsg("AOTD","Arrive On Time Delivery requires a signature");

				}

			}else{

				viewFlipper.showNext();
				changeMode();
				getNotIfExist();

			}

			break;

		case R.id.sec_sign_complete:

			if(_SignatureModel == null){

				if(deliveryType.equalsIgnoreCase("first")){

					Log.e("", "_SignatureModel first");

					BackUpDataBase _BackUpDataBase = new BackUpDataBase(this);
					_BackUpDataBase.insertData(mOrderNum, marrSignModel.get(0).getBytes(), marrSignModel.get(0).getLastname(), "AOTD");


				}else{

					Log.e("", "_SignatureModel second");

					BackUpDataBase _BackUpDataBase = new BackUpDataBase(this);
					_BackUpDataBase.insertSecondSignData(mOrderNum, marrSignModel.get(0).getBytes(), marrSignModel.get(0).getLastname(), "AOTD");


				}


			}else{

				SignatureModel signModel   = new SignatureModel();							
				signModel.setBytes(_SignatureModel.getBytes());
				signModel.setLastname(_SignatureModel.getLastname());	
				marrSignModel.add(signModel);
			}


			if(from.equalsIgnoreCase("rna")){

				rnaSignUpload();

			}else if(from.equalsIgnoreCase("aotd")){

				aotdSignUpload();
			}

			break;
		default:
			break;
		}
	}




	private void rnaSignUpload() 
	{

		if(Utils.checkNetwork(getApplicationContext())){

			progressdialog = ProgressDialog.show(SignatureActivity.this,null,null);
			progressdialog.setContentView(R.layout.loader);				
			String url = Utils.RNA_RXCheckin_URL;
			UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new RNADataUploadHandler(),url,"rna");
			//Base64.encodeToString(marrSignModel.get(0).getBytes(), Base64.DEFAULT)
			String data = rna_xmlData+"||||"+ Base64.encodeToString(marrSignModel.get(0).getBytes(), Base64.DEFAULT) ;
			data = data + "||||SYSTEM||||MYDEVICENAME||||"+batchId+"||||"+rna_nhomecode+"||||08/14/2012 12:34:00||||08/14/2012 17:34:00||||NurseInitials";
			Log.v("data",data);
			byte[] data_in_bytes = data.getBytes();					
			mUploadImageDataParser.responseBytes = data_in_bytes;
			mUploadImageDataParser.isPost=true;
			mUploadImageDataParser.isUpload = true;
			mUploadImageDataParser.start(); 

		}else{

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String mydate =  sdf.format(date);

			RNADataBase _RNADataBase = new RNADataBase(this);
			parameters = null;
			parameters = new Hashtable<String, String>();
			parameters.put( "filename", String.format("RNA%s.jpg",batchId));
			parameters.put("status", "Delivered");
			parameters.put("DLDate", mydate);
			_RNADataBase.updateRNADelivery(batchId, parameters, marrSignModel.get(0).getBytes());
			AlertDialogMsg("RNA", "RNA order delivery updated successfully.");
			Intent i = new Intent(SignatureActivity.this,RNATabActivity.class);
			i.putExtra("from", "rna_sign");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

		}

	}

	private void aotdSignUpload() {

		if(edt_notes.getText().toString().length() == 0 ){
			notes.add("");
		}else{

			uploadOrderNote(edt_notes.getText().toString());
			notes.add(edt_notes.getText().toString());

		}

		Log.e("notes size", notes.size()+" ");
		Intent sign_complete_intent = new Intent(SignatureActivity.this,RoundTripActivity.class);
		sign_complete_intent.putExtra("bitmap", marrSignModel);
		sign_complete_intent.putExtra("orderNumber", mOrderNum);			
		sign_complete_intent.putExtra("roundtrip", getIntent().getStringExtra("roundtrip"));
		sign_complete_intent.putExtra("notes", notes);
		sign_complete_intent.putExtra("condition", getIntent().getStringExtra("condition"));

		startActivity(sign_complete_intent);
		finish();
		notes.clear();


	}


	private void AlertDialogMsg(String title,String msg) {

		new AlertDialogMsg(SignatureActivity.this, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which){


			}

		}).create().show();	

	}


	public Bitmap getbiBitmap(){
		return signatureView.mBitmap;
	}


	class RNADataUploadHandler extends Handler
	{

		public void handleMessage(android.os.Message msg)
		{ 
			progressdialog.dismiss();
			if(msg.what ==2)
			{
				String errorMsg=msg.getData().getString("HttpError");
				String successMsg=msg.getData().getString("success");

				if(errorMsg.length()>0){

					Log.v("sucessmsg",errorMsg);
					AlertDialogMsg("AOTD", errorMsg);

				}else{				
					rnaSignUploadToAotdServer();
				}
			}else if(msg.what==0){ // for checking in to AOTD 
				String errorMsg=msg.getData().getString("HttpError");
				String successMsg=msg.getData().getString("success");

				if(errorMsg.length()>0){

					Log.v("sucessmsg",errorMsg);
					AlertDialogMsg("AOTD", errorMsg);

				}else{				
					AlertDialogMsg("AOTD", successMsg);
					Intent i = new Intent(SignatureActivity.this,RNATabActivity.class);
					i.putExtra("from", "rna_sign");
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}


			}

		}
	}

	protected void rnaSignUploadToAotdServer() {

		if(edt_notes.getText().toString().length() == 0 )
			notes.add("");
		else
			notes.add(edt_notes.getText().toString());

		progressdialog = ProgressDialog.show(SignatureActivity.this,null,null);
		progressdialog.setContentView(R.layout.loader);	
		String url = String.format(Utils.RNA_DELIVERY_SIGN_URL,batchId);
		parameters = new Hashtable<String, String>();
		Log.i("AOTD service debug",url);
		parameters.put("notes", notes.get(0));
		parameters.put("firstname", marrSignModel.get(0).getLastname());

		UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new RNADataUploadHandler(),url,"aotd");
		mUploadImageDataParser.isMultipart=true;
		mUploadImageDataParser.params = parameters;

		mUploadImageDataParser.imgBytes = marrSignModel.get(0).getBytes();
		mUploadImageDataParser.mFileName = String.format("RNA%s.jpg",batchId);
		mUploadImageDataParser.start(); 


	}
	
	
}

