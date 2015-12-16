package dealsforsure.in;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMerchantRegister extends ActionBarActivity {

	EditText etbusinessname, etcontactperson, etcontactphone, etbusinessphone,
			etemail, etwebsite, etaddress, etfacebookurl, etcategory,
			etpassword, etconfirmpassword;
	Button btLogin;
	String tokenKey;
	String userid, userName, userCode, userPoint,typeValue,merchantPoint,vid,emailValue;
	private Utils utils;
	String deviceId, businessName, contactPerson, type, email, password,
			confirmPassword, contactphone, businessphone, website, address,
			facebookurl, category,otp;
	UserFunctions userFunction;
	JSONObject regjson;
	 TextView tvtermscondition;
	SharedPreferences sharedPreferences;
	 private CheckBox chkterms;
	private ActionBar actionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_register);

		etbusinessname = (EditText) findViewById(R.id.etbusinessname);
		etcontactperson = (EditText) findViewById(R.id.etcontactperson);
		etcontactphone = (EditText) findViewById(R.id.etcontactphone);
		etbusinessphone = (EditText) findViewById(R.id.etbusinessphone);
		etwebsite = (EditText) findViewById(R.id.etwebsite);
		etaddress = (EditText) findViewById(R.id.etaddress);
		etfacebookurl = (EditText) findViewById(R.id.etfacebookurl);
		etcategory = (EditText) findViewById(R.id.etcategory);
		chkterms=(CheckBox)findViewById(R.id.chkterms);
		tvtermscondition=(TextView)findViewById(R.id.tvtermscondition);
		etemail = (EditText) findViewById(R.id.etemail);
		etpassword = (EditText) findViewById(R.id.etpassword);
		etconfirmpassword = (EditText) findViewById(R.id.etconpassword);
		btLogin = (Button) findViewById(R.id.btnLogin);
        actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		utils = new Utils(ActivityMerchantRegister.this);
		deviceId = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityMerchantRegister.this);
		 tokenKey = sharedPreferences.getString("tokenKey", null);
		userFunction = new UserFunctions();
		typeValue="merchant";
		btLogin.setEnabled(true);
		 
		 tvtermscondition.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					displayTermsCondition();
					
				}
				});
		 
		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					
					
					businessName = ((TextView) etbusinessname).getText()
							.toString();

					businessName = URLEncoder.encode(businessName, "UTF-8");

					contactPerson = ((TextView) etcontactperson).getText()
							.toString();

					contactPerson = URLEncoder.encode(contactPerson, "UTF-8");

					contactphone = ((TextView) etcontactphone).getText()
							.toString();

					email = ((TextView) etemail).getText().toString();

					contactphone = ((TextView) etcontactphone).getText()
							.toString();
					businessphone = ((TextView) etbusinessphone).getText()
							.toString();
					website = ((TextView) etwebsite).getText().toString();
					website = URLEncoder.encode(website, "UTF-8");
					address = ((TextView) etaddress).getText().toString();
					address = URLEncoder.encode(address, "UTF-8");
					facebookurl = ((TextView) etfacebookurl).getText()
							.toString();
					facebookurl = URLEncoder.encode(facebookurl, "UTF-8");
					category = ((TextView) etcategory).getText().toString();
					category = URLEncoder.encode(category, "UTF-8");
					password = ((TextView) etpassword).getText().toString();

					confirmPassword = ((TextView) etconfirmpassword).getText()
							.toString();

					if (businessName.trim().length() <= 0) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter Business Name", Toast.LENGTH_LONG)
								.show();

					} else if (contactPerson.trim().length() <= 0) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter Contact Person",
								Toast.LENGTH_LONG).show();

					} else if (contactphone.trim().length() <= 0) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter Contact Phone", Toast.LENGTH_LONG)
								.show();

					} else if (password.trim().length() <= 0) {
						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter Password", Toast.LENGTH_LONG)
								.show();

					} else if (confirmPassword.trim().length() <= 0) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter Confirm Password",
								Toast.LENGTH_LONG).show();

					} else if (!password.equals(confirmPassword)) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Password Does Not Match", Toast.LENGTH_LONG)
								.show();

					} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
							email).matches()) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter valid email id",
								Toast.LENGTH_LONG).show();

					} else if (password.length() < 4) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Password length minimum 4 letters",
								Toast.LENGTH_LONG).show();

					}else if (!(contactphone.trim().length()==10)) {

						Toast.makeText(ActivityMerchantRegister.this,
								"Please enter 10 digit Contact number", Toast.LENGTH_LONG)
								.show();

					}else if(!chkterms.isChecked()){
						
						
						Toast.makeText(ActivityMerchantRegister.this,
								"Please accept terms and conditions", Toast.LENGTH_LONG)
								.show();
						
					} else {
						btLogin.setEnabled(false); 
						if (utils.isNetworkAvailable()) {
							new getOTPAsyk().execute();
						} else {
							Toast.makeText(getBaseContext(),
									"Error in connection .", Toast.LENGTH_LONG)
									.show();

						}
						
						// new registertView().execute();

					}

				}

				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
	
	
void displayOTPVerify() {

		
		Button btSave;
		final EditText etotp;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_verify_otp, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		btSave = (Button) promptsView.findViewById(R.id.btnsubmit);
		etotp = (EditText) promptsView.findViewById(R.id.etotp);

		

		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				otp=((TextView) etotp).getText()
						.toString();
				
                  if(otp.trim().length()<=0){
					
					Toast.makeText(ActivityMerchantRegister.this,
							"Please enter OTP",
							Toast.LENGTH_LONG).show();
					
				}else{
					
					if (utils.isNetworkAvailable()) {
						new registerAsyk().execute();
					} else {
						Toast.makeText(getBaseContext(),
								"Error in connection .", Toast.LENGTH_LONG)
								.show();

					}
					

				alertDialog.cancel();
				
				
				
				
				}
			}
		});

	}
	
	
	private class getOTPAsyk extends AsyncTask<Void, Void, Void> {
		 ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
        	
        	pDialog = Utils.createProgressDialog(ActivityMerchantRegister.this);
    		pDialog.setCancelable(false);
    		pDialog.show();

        }
 
        protected Void doInBackground(Void... unused) {
        	
        	//regjson=userFunction.otpGenerate(contactphone);
        	
        	regjson=userFunction.otpGenerate_merchant(businessName,
					contactPerson, email, password, contactphone,
					businessphone, website, address, facebookurl, category,
					deviceId, typeValue,tokenKey);
        	
        	
        	/*businessName,
			contactPerson, email, password, contactphone,
			businessphone, website, address, facebookurl, category,
			deviceId, typeValue, otp,vid*/
        	
			// Store previous value of current page
			
            return (null);
        }
 
        protected void onPostExecute(Void unused) {
        	try {
        		if(pDialog.isShowing()) pDialog.dismiss();	
        	if(regjson != null){
        		
        		JSONArray dataRegisterArray;
        		
        		
            	
        		String status=regjson.getString("status");
        		
        		if(status.equals("200")){
        			
        			vid=regjson.getString("vid");
        			String userId=regjson.getString("uid");
					String phone=regjson.getString("phone");
					
					
					//displayOTPVerify();
					
					Intent i = new Intent(ActivityMerchantRegister.this,
							ActivityOptVerify.class);
					i.putExtra("vid", vid);
					i.putExtra("userId", userId);
					i.putExtra("phone", phone);
					startActivity(i);
					finish();
				
        			
        		}else{
        			String errormessage = regjson.getString("message");

					Toast.makeText(ActivityMerchantRegister.this, errormessage,
							Toast.LENGTH_LONG).show();
        			
        		}

                
                
        		}else{
        			

					Toast.makeText(ActivityMerchantRegister.this, "error in connection",
							Toast.LENGTH_LONG).show();
        			
        		}
        		
        	
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            	
			
        }
    }
	
	void displayTermsCondition() {

		Button ivClose;
		TextView tvTerms;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_terms_condition, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tvTerms = (TextView) promptsView.findViewById(R.id.tvterms);
		
		ivClose = (Button) promptsView.findViewById(R.id.btncancel);

		String terms = "1) Merchants must provide genuine deals to customers with genuine discounts.\n"
				+ "2) Once a deal is live and purchased by a Customer, Merchant must honor the deal.\n"
				+ "3) All the fine points of the deals must be mentioned and there must not be any hidden condition(s).\n";

		tvTerms.setText(terms);
		
		ivClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				alertDialog.dismiss();
				// TODO Auto-generated method stub
				
			}
		});
		
	}
  
  
  private class registerAsyk extends AsyncTask<Void, Void, Void> {
    	
	  ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
        	
        	pDialog = Utils.createProgressDialog(ActivityMerchantRegister.this);
    		pDialog.setCancelable(false);
    		pDialog.show();


        }
 
        protected Void doInBackground(Void... unused) {
			regjson = userFunction.registerMerchant(businessName,
					contactPerson, email, password, contactphone,
					businessphone, website, address, facebookurl, category,
					deviceId, typeValue, otp,vid,tokenKey);

			// Store previous value of current page
			
            return (null);
        }
 
        protected void onPostExecute(Void unused) {

        if(pDialog.isShowing()) pDialog.dismiss();
        	Log.d("ressss",regjson+":");
        	
        	try {
        		
        	if(regjson != null){
        		
        		JSONArray dataRegisterArray;
        		
        		String status=regjson.getString("status");
        		
        		if(status.equals("200")){
        			
        			
        			userid = regjson.getString("uid");
					userName = regjson.getString("name");
					typeValue = regjson.getString("type");
					userCode = regjson.getString("user_code");
					userPoint = regjson.getString("user_points");
					emailValue = regjson.getString("email");
					merchantPoint = regjson
							.getString("merchant_points");
					
					SharedPreferences.Editor editor = sharedPreferences
							.edit();
					editor.putString("userName", userName);
					editor.putString("userId", userid);
					editor.putString("type", typeValue);
					editor.putString("userCode", userCode);
					editor.putString("userPoint", userPoint);
					editor.putString("email", emailValue);
					
					editor.putString("merchentPoint", merchantPoint);
				
					editor.commit();
					
					
					Intent i = new Intent(ActivityMerchantRegister.this,
							ActivityProfile.class);
					startActivity(i);
					finish();
        		
        			
        		}else{
        			String errormessage = regjson.getString("message");

					Toast.makeText(ActivityMerchantRegister.this, errormessage,
							Toast.LENGTH_LONG).show();
        			
        		}

              
              
        		}
        		
        	
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            	
			
        }
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		
	}
	
	// Listener for option menu
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    	case android.R.id.home:
		    		// Previous page or exit
		    		finish();
		    		
		    		return true;
		    		
				default:
					return super.onOptionsItemSelected(item);
			}
		}

}
