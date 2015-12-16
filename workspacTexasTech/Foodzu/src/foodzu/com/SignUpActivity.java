package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;

public class SignUpActivity extends Activity {

	EditText etname, etlastname, etphone, etemail, etpassword;
	Button btLogin;
	String deviceId, name, lname, phone, password, type, email, otp, vid;
	String userid, userName, userCode, userPoint, typeValue, merchantPoint,
			useremail;
	String tokenKey;
	// UserFunctions userFunction;
	JSONObject regjson;
	TextView tvtermscondition;
	SharedPreferences sharedPreferences;
	private Utils utils;
	// private ActionBar actionbar;
	private CheckBox chkterms;

	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		ActionBar actionBar = getActionBar();

		// Enabling Up / Back navigation
		actionBar.setDisplayHomeAsUpEnabled(true);
		etname = (EditText) findViewById(R.id.etname);
		etlastname = (EditText) findViewById(R.id.etlname);
		etphone = (EditText) findViewById(R.id.etphone);
		etemail = (EditText) findViewById(R.id.etemail);
		etpassword = (EditText) findViewById(R.id.etpassword);

		btLogin = (Button) findViewById(R.id.btnLogin);
		chkterms = (CheckBox) findViewById(R.id.chkterms);
		tvtermscondition = (TextView) findViewById(R.id.tvtermscondition);
		// actionbar = getSupportActionBar();
		// actionbar.setDisplayHomeAsUpEnabled(true);
		utils = new Utils(SignUpActivity.this);
		deviceId = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		// Bundle b = getIntent().getExtras();
		// type = b.getString("type");
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(SignUpActivity.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		// userFunction = new UserFunctions();
		typeValue = "user";
		btLogin.setEnabled(false);

		alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);

		chkterms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// displayTermsCondition();
				if (chkterms.isChecked()) {
					btLogin.setEnabled(true);
				} else
					btLogin.setEnabled(false);
			}
		});

		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				name = ((TextView) etname).getText().toString();
				lname = ((TextView) etlastname).getText().toString();
				// try {
				// name = URLEncoder.encode(name, "UTF-8");
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				phone = ((TextView) etphone).getText().toString();
				password = ((TextView) etpassword).getText().toString();
				email = ((TextView) etemail).getText().toString();

				if (name.trim().length() <= 0) {

					Toast.makeText(SignUpActivity.this,
							"Please enter First Name", Toast.LENGTH_LONG)
							.show();

				}
//				else if (lname.trim().length() <= 0) {
//
//					Toast.makeText(SignUpActivity.this,
//							"Please enter Last Name", Toast.LENGTH_LONG).show();
//
//				} 
				else if (phone.trim().length() <= 0) {

					Toast.makeText(SignUpActivity.this, "Please enter Phone",
							Toast.LENGTH_LONG).show();

				} else if (password.trim().length() <= 0) {

					Toast.makeText(SignUpActivity.this,
							"Please enter Password", Toast.LENGTH_LONG).show();

				} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches()) {

					Toast.makeText(SignUpActivity.this,
							"Please enter valid email id", Toast.LENGTH_LONG)
							.show();

				} else if (password.trim().length() < 6) {

					Toast.makeText(
							SignUpActivity.this,
							"Please enter atleast 6 characters as the password",
							Toast.LENGTH_LONG).show();

				} else if (!(phone.trim().length() == 10)) {

					Toast.makeText(SignUpActivity.this,
							"Please enter 10 digit phone number",
							Toast.LENGTH_LONG).show();

				} else if (!chkterms.isChecked()) {

					Toast.makeText(SignUpActivity.this,
							"Please accept terms and conditions",
							Toast.LENGTH_LONG).show();

				} else {
					btLogin.setEnabled(false);

					if (utils.isNetworkAvailable()) {
						// new getOTPAsyk().execute();
						signup();
					} else {
						Toast.makeText(getBaseContext(),
								"Error in connection .", Toast.LENGTH_LONG)
								.show();
						btLogin.setEnabled(true);
					}

				}

			}
		});

	}

	void signup() {
		if (new Utils(SignUpActivity.this).isNetworkAvailable()) {
			new Signup_validate().execute();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									signup();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public class Signup_validate extends AsyncTask<String, String, String> {

		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SignUpActivity.this);

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(SignUpActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(String... params) {

			InputStream inputStream = null;
			String result = null;
			String URL = URLs.SIGNUP_URL + name + "&Last_Name=" + lname + "&Email=" + email
					+ "&Phone_Number=" + phone + "&Password=" + password;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient
						.execute(new HttpGet(URL));
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);
			} catch (Exception e) {
				System.out.println(e);
			}

			return result;
		}

		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj_main = new JSONObject(result);
					String res = jObj_main.getString("success").toString();
					if (res.equals("0")) {
						builder.setMessage("Email or Mobile is already used")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
						AlertDialog alert = builder.create();
						alert.show();

					} else {
						finish();
						startActivity(new Intent(SignUpActivity.this,
								LoginActivity.class));
						Toast.makeText(SignUpActivity.this,
								"Signup Successful", Toast.LENGTH_LONG).show();
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
		}
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
	/*
	 * void displayOTPVerify() {
	 * 
	 * Button btSave; final EditText etotp; LayoutInflater li =
	 * LayoutInflater.from(this); View promptsView =
	 * li.inflate(R.layout.dialog_verify_otp, null);
	 * 
	 * AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	 * alertDialogBuilder.setCancelable(false);
	 * alertDialogBuilder.setView(promptsView); final AlertDialog alertDialog =
	 * alertDialogBuilder.create();
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(Color.argb(0, 0, 0, 0)));
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(android.graphics.Color.TRANSPARENT)); alertDialog.show();
	 * 
	 * btSave = (Button) promptsView.findViewById(R.id.btnsubmit); etotp =
	 * (EditText) promptsView.findViewById(R.id.etotp);
	 * 
	 * btSave.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * otp = ((TextView) etotp).getText().toString();
	 * 
	 * if (otp.trim().length() <= 0) {
	 * 
	 * Toast.makeText(ActivityRegistrationDetail.this, "Please enter OTP",
	 * Toast.LENGTH_LONG).show();
	 * 
	 * } else {
	 * 
	 * if (utils.isNetworkAvailable()) { new registerAsyk().execute(); } else {
	 * Toast.makeText(getBaseContext(), "Error in connection .",
	 * Toast.LENGTH_LONG) .show();
	 * 
	 * }
	 * 
	 * alertDialog.cancel();
	 * 
	 * } } });
	 * 
	 * }
	 * 
	 * void displayTermsCondition() {
	 * 
	 * Button ivClose; TextView tvTerms; LayoutInflater li =
	 * LayoutInflater.from(this); View promptsView =
	 * li.inflate(R.layout.dialog_terms_condition, null);
	 * 
	 * AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	 * alertDialogBuilder.setCancelable(false);
	 * alertDialogBuilder.setView(promptsView); final AlertDialog alertDialog =
	 * alertDialogBuilder.create();
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(Color.argb(0, 0, 0, 0)));
	 * 
	 * alertDialog.getWindow().setBackgroundDrawable( new
	 * ColorDrawable(android.graphics.Color.TRANSPARENT)); alertDialog.show();
	 * 
	 * tvTerms = (TextView) promptsView.findViewById(R.id.tvterms);
	 * 
	 * ivClose = (Button) promptsView.findViewById(R.id.btncancel);
	 * 
	 * String terms =
	 * "1) Dealsforsure.in will not be responsivble for the quality of products and or services provided by merchants, it is the responsibility of the merchant to provide quality product and services to its customers.\n"
	 * +
	 * "2) No refunds will be given for the purchase of Points and the Points cannot be redeemed in cash, they must be used on Dealsforsure.in platform.\n"
	 * +
	 * "3) The Deals will be redeemed after due verification of the customer.\n"
	 * +
	 * "4) All the deals must be redeemed within the Date, Time and Day restrictions as mentioned by the Merchant.\n"
	 * ;
	 * 
	 * tvTerms.setText(terms);
	 * 
	 * ivClose.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * 
	 * alertDialog.dismiss();
	 * 
	 * } });
	 * 
	 * }
	 * 
	 * @Override public void onBackPressed() { super.onBackPressed();
	 * 
	 * }
	 * 
	 * // Listener for option menu
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: // Previous page or exit
	 * finish();
	 * 
	 * return true;
	 * 
	 * default: return super.onOptionsItemSelected(item); } }
	 * 
	 * private class getOTPAsyk extends AsyncTask<Void, Void, Void> {
	 * ProgressDialog pDialog;
	 * 
	 * @Override protected void onPreExecute() {
	 * 
	 * pDialog = Utils .createProgressDialog(ActivityRegistrationDetail.this);
	 * pDialog.setCancelable(false); pDialog.show();
	 * 
	 * }
	 * 
	 * protected Void doInBackground(Void... unused) { //
	 * regjson=userFunction.otpGenerate(phone); regjson =
	 * userFunction.otpGenerate_new(name, email, phone, deviceId, typeValue,
	 * tokenKey); // Store previous value of current page
	 * 
	 * return (null); }
	 * 
	 * protected void onPostExecute(Void unused) { try { if
	 * (pDialog.isShowing()) pDialog.dismiss(); if (regjson != null) {
	 * 
	 * JSONArray dataRegisterArray;
	 * 
	 * String status = regjson.getString("status");
	 * 
	 * if (status.equals("200")) {
	 * 
	 * 
	 * /* Intent i = new Intent(ActivityRegistrationDetail.this,
	 * ActivityRegistration.class); i.putExtra("type", type); startActivity(i);
	 */
	/*
	 * vid = regjson.getString("vid"); String userId=regjson.getString("uid");
	 * String phone=regjson.getString("phone");
	 * 
	 * 
	 * //displayOTPVerify();
	 * 
	 * Intent i = new Intent(ActivityRegistrationDetail.this,
	 * ActivityOptVerify.class); i.putExtra("vid", vid); i.putExtra("userId",
	 * userId); i.putExtra("phone", phone); startActivity(i); finish();
	 * 
	 * } else { String errormessage = regjson.getString("message");
	 * 
	 * Toast.makeText(ActivityRegistrationDetail.this, errormessage,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * } }
	 * 
	 * private class registerAsyk extends AsyncTask<Void, Void, Void> {
	 * ProgressDialog pDialog;
	 * 
	 * @Override protected void onPreExecute() { pDialog =
	 * Utils.createProgressDialog(ActivityRegistrationDetail.this);
	 * pDialog.setCancelable(false); pDialog.show();
	 * 
	 * }
	 * 
	 * protected Void doInBackground(Void... unused) { /*regjson =
	 * userFunction.register(name, email, phone, deviceId, typeValue, otp, vid,
	 * tokenKey);
	 */
	// Store previous value of current page
	/*
	 * return (null); }
	 * 
	 * protected void onPostExecute(Void unused) { try {
	 * 
	 * if (pDialog.isShowing()) pDialog.dismiss(); Log.d("aaaa", regjson +
	 * "::");
	 * 
	 * if (regjson != null) {
	 * 
	 * JSONArray dataRegisterArray;
	 * 
	 * String status = regjson.getString("status");
	 * 
	 * if (status.equals("200")) {
	 * 
	 * userid = regjson.getString("uid"); userName = regjson.getString("name");
	 * typeValue = regjson.getString("type"); userCode =
	 * regjson.getString("user_code"); userPoint =
	 * regjson.getString("user_points"); useremail = regjson.getString("email");
	 * 
	 * SharedPreferences.Editor editor = sharedPreferences .edit();
	 * editor.putString("userName", userName); editor.putString("userId",
	 * userid); editor.putString("type", typeValue);
	 * editor.putString("userCode", userCode); editor.putString("userPoint",
	 * userPoint); editor.putString("userEmail", useremail); editor.commit();
	 * 
	 * if (type.equals("promo")) {
	 * 
	 * finish();
	 * 
	 * } else {
	 * 
	 * Intent i = new Intent( ActivityRegistrationDetail.this,
	 * ActivityUserProfile.class); startActivity(i); finish();
	 * 
	 * }
	 * 
	 * } else { String errormessage = regjson.getString("message");
	 * 
	 * Toast.makeText(ActivityRegistrationDetail.this, errormessage,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * } }
	 */
}
