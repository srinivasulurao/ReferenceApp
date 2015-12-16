package com.youflik.youflik.forgotpassword;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ActivityChangePassword extends Activity implements OnFocusChangeListener{

	private EditText mEditCurrentPassword,mEditNewPassword,mEditConfirmPassword;
	private Button mSavePassword,mCancel;
	
	private String mCurrentPassword,mNewPassword,mConfirmPassword,mUserId,mVerificationCode;
	private static String FORGOT_PASSWORD_CHANGE_PASSWORD_API=Util.API+"change_pswd";
	
	private ProgressDialog pDialog;
	private AlertDialogManager alert = new AlertDialogManager();
	@Override
	public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_settings_change_password);
		initView();
		//mEditCurrentPassword.setOnFocusChangeListener(this);
		mEditNewPassword.setOnFocusChangeListener(this);
		mEditConfirmPassword.setOnFocusChangeListener(this);
		
		Bundle extras = getIntent().getExtras();
		mUserId = extras.getString("user_id");
		mVerificationCode = extras.getString("verification_code");
			
		mSavePassword.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
			 
			 mNewPassword     = (mEditNewPassword).getText().toString().trim();
			 mConfirmPassword = (mEditConfirmPassword).getText().toString().trim();
			  if( mNewPassword.length()>5 && mConfirmPassword.length()>5 && mNewPassword.equalsIgnoreCase(mConfirmPassword)){
			 	 ConnectionDetector conn = new ConnectionDetector(ActivityChangePassword.this);
			 	 if(conn.isConnectingToInternet()){
			 		 new PostChangePasswordAsync().execute();
			 	 } else {
			 		 alert.showAlertDialog(ActivityChangePassword.this,"Connection Error","Check your Internet Connection",false);
			 	 }
			 }else {
				Toast.makeText(ActivityChangePassword.this,"Password should be atleast 6 characters",Toast.LENGTH_LONG).show();
			}
			}
		});
		}
	
	private void initView() {
		Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/BentonSans-Regular.otf");
		mEditCurrentPassword = (EditText)findViewById(R.id.fragment_settings_change_password_current);
		mEditNewPassword = (EditText)findViewById(R.id.fragment_settings_change_password_new);
		mEditConfirmPassword = (EditText)findViewById(R.id.fragment_settings_change_password_confirm);
		mSavePassword = (Button)findViewById(R.id.fragment_settings_change_password_save);
		//mCancel= (Button)v.findViewById(R.id.fragment_settings_change_password_cancel);
		
		mEditCurrentPassword.setVisibility(View.GONE);
	    mEditNewPassword.setTypeface(typeFace);
		mEditConfirmPassword.setTypeface(typeFace);
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id =v.getId();
		int len = ((EditText) v).getText().toString().trim().length();
		if((!hasFocus) && len<6){
			((EditText) v).setError("Password Should be atleast 6 characters");
		}
	   }
	class PostChangePasswordAsync extends AsyncTask<Void,Void,JSONObject>{
		String message = null,errorMessages=null;	
       @Override
       protected void onPreExecute(){
    	   if(pDialog==null){
    		   pDialog = Util.createProgressDialog(ActivityChangePassword.this);
    		   pDialog.show();
    		} else { pDialog.show(); }
    	}
		@Override
		protected JSONObject doInBackground(Void... params) {
			
		    JSONObject jsonObjSend = new JSONObject();
			JSONObject receivedJsonObject = null;
			try {
				jsonObjSend.put("user_id",mUserId);
				jsonObjSend.put("verification_code",mVerificationCode);
				jsonObjSend.put("password",mConfirmPassword);
				receivedJsonObject = HttpPostClient.sendHttpPost(FORGOT_PASSWORD_CHANGE_PASSWORD_API, jsonObjSend);
				}
				catch (JSONException e) { e.printStackTrace();}
			return receivedJsonObject;
		}  
		@Override
	    protected void onPostExecute(JSONObject result){
	           super.onPostExecute(result);
	    	   pDialog.dismiss();
	           if(result!=null){
	        	   try{
	    	          message = result.getString("message");
			          if(result.has("errorMessages")){
				         errorMessages = result.getString("errorMessages");
				         Toast.makeText(ActivityChangePassword.this, errorMessages, Toast.LENGTH_LONG).show();
				      } else {
					    Toast.makeText(ActivityChangePassword.this, message, Toast.LENGTH_LONG).show();
				      }
			         if(result.getString("error").equalsIgnoreCase("false")){
				      finish();
			         }
	        	   }catch(JSONException e){ e.printStackTrace();}
	    	  }
			}
	     }
	  }

