package com.youflik.youflik.forgotpassword;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPutClient_NoToken;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ActivityVerifyCode extends Activity {
	private EditText mGetVerificationCode;
	private TextView mLevel,mLabel;
	private Button mSubmit;
	private String mVerificationCode,mUserId;
	private ProgressDialog mPDialog;
	private static String FORGOT_PASSWORD_LEVEL2_API = Util.API+ "forgot_password/";
	private AlertDialogManager alert = new AlertDialogManager();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgotpassword);
		
		mGetVerificationCode = (EditText) findViewById(R.id.activity_forgotpasswordedit);
		
		mSubmit = (Button) findViewById(R.id.activity_forgotpasswordsubmit);
		mLabel = (TextView) findViewById(R.id.activity_forgotpasswordlbl);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/BentonSans-Regular.otf");
		mGetVerificationCode.setTypeface(typeface);
		mSubmit.setTypeface(typeface);
		mLabel.setTypeface(typeface);
		
		mGetVerificationCode.setInputType(InputType.TYPE_CLASS_NUMBER);
		InputFilter filters[]= new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(5);
		mGetVerificationCode.setFilters(filters);
		mLabel.setText("Verification Code");
		
		
		Bundle extras = getIntent().getExtras();
		mUserId = extras.getString("user_id");
		
		FORGOT_PASSWORD_LEVEL2_API = FORGOT_PASSWORD_LEVEL2_API+mUserId;
		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mVerificationCode = mGetVerificationCode.getText().toString().trim();
               if(mVerificationCode.length()>0){
               ConnectionDetector conn = new ConnectionDetector(ActivityVerifyCode.this);
               if(conn.isConnectingToInternet()){
            	    new PutVerifyCodeAsync().execute();
               } else {
            	   alert.showAlertDialog(ActivityVerifyCode.this,"Connection Error","Check your Internet Connection",false); 
               }
               } else {
            	   mGetVerificationCode.setError("Enter a valid verification code");
               }
			}
		});

	}

	class PutVerifyCodeAsync extends AsyncTask<Void, Void, JSONObject> {
		String message, error_message;

		@Override
		protected void onPreExecute() {
			if (mPDialog == null) {
				mPDialog = Util.createProgressDialog(ActivityVerifyCode.this);
				mPDialog.show();
			} else {
				mPDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject sendJsonObject = new JSONObject();
			try {
				sendJsonObject.put("verification_code", mVerificationCode);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			JSONObject responseJsonObject = HttpPutClient_NoToken.sendHttpPost(FORGOT_PASSWORD_LEVEL2_API,sendJsonObject);
			return responseJsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			mPDialog.dismiss();
			if (result != null) {
				try {	
					if(result.has("errorMessages")){
						Toast.makeText(ActivityVerifyCode.this, result.getString("errorMessages"),Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ActivityVerifyCode.this,result.getString("message"),Toast.LENGTH_LONG).show();
					}
					if(result.getString("status").equalsIgnoreCase("1")){
						Intent changePasswordIntent = new Intent(ActivityVerifyCode.this,ActivityChangePassword.class);
						changePasswordIntent.putExtra("user_id",mUserId);
						changePasswordIntent.putExtra("verification_code",mVerificationCode);
						startActivity(changePasswordIntent);
						finish();
					}
				} catch (JSONException e){e.printStackTrace();}

			}
		}
	}
}