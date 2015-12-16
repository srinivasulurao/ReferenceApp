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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRegistrationDetail extends ActionBarActivity {

	EditText etname, etphone, etemail;
	Button btLogin;
	String deviceId, name, phone, type, email, otp, vid;
	String userid, userName, userCode, userPoint, typeValue, merchantPoint,
			useremail;
	String tokenKey;
	UserFunctions userFunction;
	JSONObject regjson;
	TextView tvtermscondition;
	SharedPreferences sharedPreferences;
	private Utils utils;
	private ActionBar actionbar;
	private CheckBox chkterms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_detail);

		etname = (EditText) findViewById(R.id.etname);
		etphone = (EditText) findViewById(R.id.etphone);
		etemail = (EditText) findViewById(R.id.etemail);

		btLogin = (Button) findViewById(R.id.btnLogin);
		chkterms = (CheckBox) findViewById(R.id.chkterms);
		tvtermscondition = (TextView) findViewById(R.id.tvtermscondition);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		utils = new Utils(ActivityRegistrationDetail.this);
		deviceId = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		Bundle b = getIntent().getExtras();
		type = b.getString("type");
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityRegistrationDetail.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		userFunction = new UserFunctions();
		typeValue = "user";
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
				
				name = ((TextView) etname).getText().toString();
				try {
					name = URLEncoder.encode(name, "UTF-8");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				phone = ((TextView) etphone).getText().toString();

				email = ((TextView) etemail).getText().toString();

				if (name.trim().length() <= 0) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please enter Name", Toast.LENGTH_LONG).show();

				} else if (phone.trim().length() <= 0) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please enter Phone", Toast.LENGTH_LONG).show();

				} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches()) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please enter valid email id", Toast.LENGTH_LONG)
							.show();

				} else if (!(phone.trim().length() == 10)) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please enter 10 digit phone number",
							Toast.LENGTH_LONG).show();

				} else if (!chkterms.isChecked()) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please accept terms and conditions",
							Toast.LENGTH_LONG).show();

				} else {
					btLogin.setEnabled(false);

					if (utils.isNetworkAvailable()) {
						new getOTPAsyk().execute();
					} else {
						Toast.makeText(getBaseContext(),
								"Error in connection .", Toast.LENGTH_LONG)
								.show();

					}

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

				otp = ((TextView) etotp).getText().toString();

				if (otp.trim().length() <= 0) {

					Toast.makeText(ActivityRegistrationDetail.this,
							"Please enter OTP", Toast.LENGTH_LONG).show();

				} else {

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

		String terms = "1) Dealsforsure.in will not be responsivble for the quality of products and or services provided by merchants, it is the responsibility of the merchant to provide quality product and services to its customers.\n"
				+ "2) No refunds will be given for the purchase of Points and the Points cannot be redeemed in cash, they must be used on Dealsforsure.in platform.\n"
				+ "3) The Deals will be redeemed after due verification of the customer.\n"
				+ "4) All the deals must be redeemed within the Date, Time and Day restrictions as mentioned by the Merchant.\n";

		tvTerms.setText(terms);

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.dismiss();
				// TODO Auto-generated method stub

			}
		});

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

	private class getOTPAsyk extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			pDialog = Utils
					.createProgressDialog(ActivityRegistrationDetail.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected Void doInBackground(Void... unused) {
			// regjson=userFunction.otpGenerate(phone);
			regjson = userFunction.otpGenerate_new(name, email, phone,
					deviceId, typeValue, tokenKey);
			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				if (pDialog.isShowing())
					pDialog.dismiss();
				if (regjson != null) {

					JSONArray dataRegisterArray;

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						
						/*
						 * Intent i = new
						 * Intent(ActivityRegistrationDetail.this,
						 * ActivityRegistration.class); i.putExtra("type",
						 * type); startActivity(i);
						 */
						vid = regjson.getString("vid");
						String userId=regjson.getString("uid");
						String phone=regjson.getString("phone");
						
						
						//displayOTPVerify();
						
						Intent i = new Intent(ActivityRegistrationDetail.this,
								ActivityOptVerify.class);
						i.putExtra("vid", vid);
						i.putExtra("userId", userId);
						i.putExtra("phone", phone);
						startActivity(i);
						finish();

					} else {
						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityRegistrationDetail.this,
								errormessage, Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class registerAsyk extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = Utils.createProgressDialog(ActivityRegistrationDetail.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected Void doInBackground(Void... unused) {
			/*regjson = userFunction.register(name, email, phone, deviceId,
					typeValue, otp, vid, tokenKey);
*/
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

						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userName", userName);
						editor.putString("userId", userid);
						editor.putString("type", typeValue);
						editor.putString("userCode", userCode);
						editor.putString("userPoint", userPoint);
						editor.putString("userEmail", useremail);
						editor.commit();

						if (type.equals("promo")) {

							finish();

						} else {

							Intent i = new Intent(
									ActivityRegistrationDetail.this,
									ActivityUserProfile.class);
							startActivity(i);
							finish();

						}

					} else {
						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityRegistrationDetail.this,
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
