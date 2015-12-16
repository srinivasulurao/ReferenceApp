package com.fivestarchicken.lms;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.model.User;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;

public class ActivityLogin extends ActionBarActivity implements OnClickListener {

	Button btLogin;
	private DbAdapter dh;
	EditText etUserName, etPassword;
	String userName, password;
	Webservice Webservices = new Webservice();
	User user;
	SharedPreferences sharedPreferences;
	Locale myLocale;
	// Spinner spLanguage;
	Integer languageSelection;
	String syncTime;
	// ArrayList<String> spinnerOption =new ArrayList<String>();
	ArrayList<User> userList = new ArrayList<User>();
	private TextView forgotpassword;
	private ActionBar actionbar;
	private String forgotPwd;
	private TextView successMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		actionbar = getSupportActionBar();

		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);

		etUserName = (EditText) findViewById(R.id.etusername);
		etPassword = (EditText) findViewById(R.id.etpassword);
		// spLanguage=(Spinner)findViewById(R.id.splanguage);

		forgotpassword = (TextView) findViewById(R.id.txt_forgotpassword);

		// code by TH

		btLogin = (Button) findViewById(R.id.btlogin);
		this.dh = new DbAdapter(this);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityLogin.this);

		btLogin.setText(R.string.login);
		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				userName = ((TextView) etUserName).getText().toString();
				password = ((TextView) etPassword).getText().toString();

				if (userName == null || userName.length() == 0) {

					Toast.makeText(ActivityLogin.this,
							"Please enter user name", Toast.LENGTH_SHORT)
							.show();

				} else if (password == null || password.length() == 0) {

					Toast.makeText(ActivityLogin.this, "Please enter password",
							Toast.LENGTH_SHORT).show();

				}
				
				
				
				/*
				 * else if(languageSelection==null||languageSelection==0){
				 * 
				 * Toast.makeText(ActivityLogin.this, "Please enter Language",
				 * Toast.LENGTH_SHORT).show();
				 * 
				 * 
				 * }
				 */else {

					if (Commons.isNetworkAvailable(ActivityLogin.this)) {

						new loginView().execute();

					}

				}

				/*
				 * Intent i=new Intent(); i.setClass(ActivityLogin.this,
				 * ActivityHome.class); startActivity(i);
				 */

			}
		});

		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ Commons.appFolder + "");
		if (f.mkdir()) {
			
			File fimage = new File( Commons.app_image_folder+ "");
			 if (fimage.mkdir()) {
				 System.out.println("Image	Directory created");	 
				 
			 }
			 
			 File fvideo = new File( Commons.app_video_folder+ "");
			 if (fvideo.mkdir()) {
				 System.out.println("Video Directory created");	 
				 
			 }
			 File fpdf = new File( Commons.app_pdf_folder+ "");
			 if (fpdf.mkdir()) {
				 System.out.println("PDF Directory created");	 
				 
			 }
			 
			System.out.println("Directory created");
		} else {
			System.out.println("Directory is not created");
		}

		/*
		 * spinnerOption.add("--Select Language--");
		 * spinnerOption.add("English"); spinnerOption.add("Kannada");
		 * spinnerOption.add("Telugu");
		 */

		/*
		 * spLanguage.setAdapter(new AdapterDropDown( ActivityLogin.this,
		 * R.layout.spinner_content, spinnerOption)); spLanguage
		 * .setOnItemSelectedListener(new LanguageSelectedListener());
		 */

		// code by TH
		forgotpassword.setOnClickListener(this);

	}

	// code by TH
	void dialogForgotPwd() {// final User user

		ImageView ivClose;
		Button btForgotPwd;
		final EditText etEmail;

		LayoutInflater li = LayoutInflater.from(ActivityLogin.this);
		View promptsView = li.inflate(R.layout.dialog_forgot_pwd, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ActivityLogin.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);

		successMsg = (TextView) promptsView.findViewById(R.id.sucessMsg_FP);

		etEmail = (EditText) promptsView.findViewById(R.id.et_emailId);
		btForgotPwd = (Button) promptsView.findViewById(R.id.btnforgot_pwd);

		btForgotPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				forgotPwd = etEmail.getText().toString();
				if (forgotPwd.isEmpty()) {

					Toast.makeText(ActivityLogin.this, "Please enter Email Id",
							Toast.LENGTH_SHORT).show();

				} else {

					// validatePassward(user, password);
					callforgotpasswordAPI();

				}
			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();

			}
		});

	}

	public class LanguageSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			languageSelection = position;

			if (languageSelection == 1) {
				myLocale = Locale.ENGLISH;
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

				// myLocale = new Locale(Locale.ENGLISH);
			} else if (languageSelection == 2) {

				myLocale = new Locale("kn");
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

			} else if (languageSelection == 3) {

				myLocale = new Locale("te");
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	private class loginView extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityLogin.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.managerLogin(userName, password);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					String status = joresult.getString("status");

					// syncTime=joresult.getString("sync_time");

					if (status.equals("200")) {

						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("managerId",
								joresult.getString("managerId"));
						editor.putString("branchId",
								joresult.getString("branchId"));
						// editor.putString("userId", userid);

						editor.commit();

						JSONArray employeeJarray = joresult
								.getJSONArray("result");

						for (int j = 0; j < employeeJarray.length(); j++) {

							JSONObject joValues = (JSONObject) employeeJarray
									.get(j);

							user = new User();
							user.setUserId(joValues.getString("user_id"));
							user.setEmail(joValues.getString("user_email"));
							user.setPhone(joValues.getString("user_phone"));
							user.setUserName(joValues.getString("user_name"));
							user.setRole(joValues.getString("role"));
							user.setProfileImage(joValues
									.getString("profile_pic"));
							user.setStarRate(joValues.getString("profile_star"));
							user.setPassward(joValues.getString("user_pass"));
							user.setManagerId(joresult.getString("managerId"));
							user.setCategoryId(joValues.getString("employee_category"));
							userList.add(user);

						}
						dh.saveUser(userList);

						// dh.insertSyncStatus(Commons.profileModule, syncTime);

						/*
						 * Intent i=new Intent(); i.setClass(ActivityLogin.this,
						 * ActivityHome.class); startActivity(i);
						 */

						Intent i = new Intent();
						i.setClass(ActivityLogin.this,
								ActivityEmployeeLogin.class);// ActivityHome
						startActivity(i);

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityLogin.this, errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void onBackPressed() {

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
	}

	private class forgotPwdAPI extends AsyncTask<String, Void, String> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityLogin.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected String doInBackground(String... unused) {
			// regjson = userFunction.login(email, passward, typeValue);

			return Webservices.forgotPassword(forgotPwd);
		}

		protected void onPostExecute(String result) {
			try {
				progDailog.dismiss();
				if (result != null && result.length() > 0) {

					JSONObject joresult = new JSONObject(result);

					Log.d("TAG", "joresult" + joresult);

					String status = joresult.getString("status");

					// syncTime=joresult.getString("sync_time");

					if (status.equals("200")) {

						// SharedPreferences.Editor editor = sharedPreferences
						// .edit();
						// editor.putString("managerId",
						// joresult.getString("managerId"));
						// editor.putString("branchId",
						// joresult.getString("branchId"));
						// // editor.putString("userId", userid);
						//
						// editor.commit();

						String Message = joresult.getString("message");

						Toast.makeText(ActivityLogin.this, Message,
								Toast.LENGTH_LONG).show();

						successMsg.setText("" + Message);

						// for (int j = 0; j < employeeJarray.length(); j++) {
						//
						// JSONObject joValues = (JSONObject) employeeJarray
						// .get(j);
						//
						// user = new User();
						// user.setUserId(joValues.getString("user_id"));
						// user.setEmail(joValues.getString("user_email"));
						// user.setPhone(joValues.getString("user_phone"));
						// user.setUserName(joValues.getString("user_name"));
						// user.setRole(joValues.getString("role"));
						// user.setProfileImage(joValues
						// .getString("profile_pic"));
						// user.setStarRate(joValues.getString("profile_star"));
						// user.setPassward(joValues.getString("user_pass"));
						// user.setManagerId(joresult.getString("managerId"));
						// userList.add(user);
						//
						// }

						// /////dh.saveUser(userList);

						// dh.insertSyncStatus(Commons.profileModule, syncTime);

						/*
						 * Intent i=new Intent(); i.setClass(ActivityLogin.this,
						 * ActivityHome.class); startActivity(i);
						 */

						// Intent i = new Intent();
						// i.setClass(ActivityLogin.this,ActivityEmployeeLogin.class);//
						// ActivityHome
						// startActivity(i);

					} else {

						String errormessage = joresult.getString("message");

						Toast.makeText(ActivityLogin.this, errormessage,
								Toast.LENGTH_LONG).show();

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
			}

		}
	}

	public void callforgotpasswordAPI() {

		new forgotPwdAPI().execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_forgotpassword:
			dialogForgotPwd();
			break;

		default:
			break;
		}

	}

}
