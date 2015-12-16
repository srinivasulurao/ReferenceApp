package dealsforsure.in;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class ActivityOptVerify extends ActionBarActivity {
	
	SharedPreferences sharedPreferences;
	private ActionBar actionbar;
	private Utils utils;
	String tokenKey,deviceId;
	String vid,userId,phone,otp;
	Button btSave;
    EditText etotp;
    JSONObject regjson;
    UserFunctions userFunction;
    TextView tvHeadertext;
    String userid, userName, userCode, userPoint, typeValue, merchantPoint,
	useremail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opt_verify);
		
		Bundle b=getIntent().getExtras();
		phone=b.getString("phone");
		vid=b.getString("vid");
		userId=b.getString("userId");
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		utils = new Utils(ActivityOptVerify.this);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityOptVerify.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		
		btSave=(Button)findViewById(R.id.btnsubmit);
		etotp=(EditText)findViewById(R.id.etotp);
		tvHeadertext=(TextView)findViewById(R.id.tvheader);
		userFunction = new UserFunctions();
		tvHeadertext.setText("We have sent a SMS with verification code on your mobile number "+phone+"\n\n Please enter Verifcation Code:");
		
		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				otp = ((TextView) etotp).getText().toString();

				if (otp.trim().length() <= 0) {

					Toast.makeText(ActivityOptVerify.this,
							"Please enter OTP", Toast.LENGTH_LONG).show();

				} else {

					if (utils.isNetworkAvailable()) {
						new registerAsyk().execute();
					} else {
						Toast.makeText(getBaseContext(),
								"Error in connection .", Toast.LENGTH_LONG)
								.show();

					}

					
				}
			}
		});

		
	}
	private class registerAsyk extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = Utils.createProgressDialog(ActivityOptVerify.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.register( deviceId,userId,
					 otp, vid, tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {

				if (pDialog.isShowing())
					pDialog.dismiss();
				Log.d("aaaa", regjson + "::");

				if (regjson != null) {

					JSONArray dataRegisterArray;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						userid = regjson.getString("uid");
						userName = regjson.getString("name");
						typeValue = regjson.getString("type");
						userCode = regjson.getString("user_code");
						userPoint = regjson.getString("user_points");
						useremail = regjson.getString("email");
						merchantPoint = regjson
								.getString("merchant_points");

						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userName", userName);
						editor.putString("userId", userid);
						editor.putString("type", typeValue);
						editor.putString("userCode", userCode);
						editor.putString("userPoint", userPoint);
						editor.putString("userEmail", useremail);
						editor.putString("merchentPoint", merchantPoint);
						editor.commit();

						if(typeValue.equals("user")){

							Intent i = new Intent(
									ActivityOptVerify.this,
									ActivityUserProfile.class);
							startActivity(i);
							finish();
						}else{
							
							Intent i = new Intent(
									ActivityOptVerify.this,
									ActivityProfile.class);
							startActivity(i);
							finish();
						}

						

					} else {
						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityOptVerify.this,
								errormessage, Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
