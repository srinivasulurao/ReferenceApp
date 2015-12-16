package com.youflik.youflik.forgotpassword;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.youflik.youflik.R;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.proxy.HttpPostClient_NoToken;
import com.youflik.youflik.utils.AlertDialogManager;
import com.youflik.youflik.utils.Util;

public class ActivityForgotPassword extends Activity {
	private EditText mEditEmailOrUsername;
	private TextView mLabel,mLabelmain;
	private Button submit;
	private String mEmailOrUsername;
	private ProgressDialog pDialog;
	private static final String FORGOT_PASSWORD_API = Util.API+ "forgot_password";
    private AlertDialogManager alert = new AlertDialogManager();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgotpassword);
		mEditEmailOrUsername = (EditText) findViewById(R.id.activity_forgotpasswordedit);
		mLabel = (TextView) findViewById(R.id.activity_forgotpasswordlbl);
		
		Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/BentonSans-Regular.otf");
		Typeface typeface_bold = Typeface.createFromAsset(getAssets(),"fonts/BentonSans-Bold.otf");
		submit = (Button) findViewById(R.id.activity_forgotpasswordsubmit);
		mLabelmain=(TextView)findViewById(R.id.activity_forgotpasswordtitle);
		mEditEmailOrUsername.setTypeface(typeface);
		submit.setTypeface(typeface);
		mLabel.setTypeface(typeface);
		
		mEditEmailOrUsername.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				mEditEmailOrUsername.setError(null);
			}
			
		});
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEmailOrUsername = mEditEmailOrUsername.getText().toString().trim();
               if(mEmailOrUsername.length()>0){
            	   ConnectionDetector conn = new ConnectionDetector(ActivityForgotPassword.this);
            	   if(conn.isConnectingToInternet()){
            	    new PostForgotPasswordAsync().execute();
            	    } else {
            	    	alert.showAlertDialog(ActivityForgotPassword.this,"Connection Error","Check your internet connection",false);
            	    }
            	   
                 } else {
            	   mEditEmailOrUsername.setError("Enter a valid username or email");
               }
			}
		});

	}

	class PostForgotPasswordAsync extends AsyncTask<Void, Void, JSONObject> {
		String message, error_message;

		@Override
		protected void onPreExecute() {
			if (pDialog == null) {
				pDialog = Util
						.createProgressDialog(ActivityForgotPassword.this);
				pDialog.show();
			} else {
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			JSONObject sendJsonObject = new JSONObject();
			try {
				sendJsonObject.put("username", mEmailOrUsername);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			JSONObject responseJsonObject = HttpPostClient_NoToken
					.sendHttpPost(FORGOT_PASSWORD_API, sendJsonObject);
			return responseJsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result != null) {
				try {
					
					if(result.has("errorMessages")){
						Toast.makeText(ActivityForgotPassword.this, result.getString("errorMessages"),Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ActivityForgotPassword.this,result.getString("message"),Toast.LENGTH_LONG).show();
					}
					if(result.getString("status").equalsIgnoreCase("1")){
						Intent verifyCodeIntent = new Intent(ActivityForgotPassword.this,ActivityVerifyCode.class);	
						verifyCodeIntent.putExtra("user_id",result.getString("user_id"));
						startActivity(verifyCodeIntent);
						finish();
					}
				} catch (JSONException e){e.printStackTrace();}

			}
		}
	}
}