package com.aotd.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aotd.dialog.AlertDialogMsg;
import com.aotd.model.Allorders;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.DispatchListModel;
import com.aotd.model.OfflineDataModel;
import com.aotd.parsers.DispatchAllParser;
import com.aotd.parsers.LoginParser;
import com.aotd.parsers.OfflineDispatchParser;
import com.aotd.utils.Utils;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LoginActivity extends Activity implements OnClickListener {

	private CheckBox login_checkbox = null;
	private Button mLogin = null;
	private Button mRegister = null;
	private EditText mUserName = null;
	private EditText mPassword = null;
	SharedPreferences loginprefs;
	private ProgressDialog progressdialog;
	private SharedPreferences mPreferences;

	public ImageView imgOnline;

	boolean isRememberMeChecked = false;
	boolean isClicked = false;

	ArrayList<DispatchAllListModel> marrDispatchPresentList = new ArrayList<DispatchAllListModel>();
	ArrayList<DispatchAllListModel> marrDispatchPastList = new ArrayList<DispatchAllListModel>();
	ArrayList<DispatchListModel> marrDispatchList = new ArrayList<DispatchListModel>();
	ArrayList<OfflineDataModel> marrDispatchListOffline = new ArrayList<OfflineDataModel>();

	public static String getResponseFromUrl(String url) {
		String xml = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		initializeUI();

		Button b1 = (Button) findViewById(R.id.button1);
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				OfflineDB db = new OfflineDB(LoginActivity.this);
				db.deleteAll();

			}
		});

		isRememberMeChecked = loginprefs.getBoolean("rememberMe", false);

		if (isRememberMeChecked) {
			login_checkbox.setChecked(true);
			mUserName.setText(loginprefs.getString("username", ""));
			mPassword.setText(loginprefs.getString("password", ""));
		}

		imgOnline.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (isClicked) {
					if (Utils.NetworkType(LoginActivity.this).equalsIgnoreCase(
							Utils.wifi)) {
						Utils.wifiOFF(LoginActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					} else if (Utils.NetworkType(LoginActivity.this)
							.equalsIgnoreCase(Utils.mobile)) {
						Utils.mobileDataOFF(LoginActivity.this);
						imgOnline.setBackgroundResource(R.drawable.offline);
						isClicked = false;
					}

				} else {
					imgOnline.setBackgroundResource(R.drawable.online);
					isClicked = true;
					Utils.switchOnInternet(LoginActivity.this);
				}

				return false;
			}
		});
		
		//ParseAnalytics.trackAppOpened(getIntent());

	}

	@Override
	protected void onResume() {

		super.onResume();

		if (Utils.NetworkType(LoginActivity.this).equalsIgnoreCase(Utils.wifi)) {
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		} else if (Utils.NetworkType(LoginActivity.this).equalsIgnoreCase(
				Utils.mobile)) {
			imgOnline.setBackgroundResource(R.drawable.online);
			isClicked = true;
		} else {
			imgOnline.setBackgroundResource(R.drawable.offline);
			isClicked = false;
		}

	}

	private void initializeUI() {

		loginprefs = this.getSharedPreferences("loginprefs", 0);

		mLogin = (Button) findViewById(R.id.login_Btn);
		mRegister = (Button) findViewById(R.id.register_Btn);
		mUserName = (EditText) findViewById(R.id.email_EditTxt);
		mPassword = (EditText) findViewById(R.id.password_EditTxt);
		login_checkbox = (CheckBox) findViewById(R.id.remember_ChkBox);

		mLogin.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		login_checkbox.setOnClickListener(this);

		imgOnline = (ImageView) findViewById(R.id.aotd_img_mode);

		// AOTDDataBase _AOTDDataBase = new AOTDDataBase(this);
		// _AOTDDataBase.checkDatabaseDataTemp();
		//
		// RNADataBase _RNADataBase = new RNADataBase(LoginActivity.this);
		// _RNADataBase.checkRNADatabaseData();
		//
		// BackUpDataBase _BackUpDataBase = new
		// BackUpDataBase(LoginActivity.this);
		// _BackUpDataBase.checkData();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case (R.id.login_Btn):

			if (mUserName.getText().length() < 1
					&& mUserName.getText().toString().equals("")) {

				Toast.makeText(getApplicationContext(),
						"please enter username", Toast.LENGTH_SHORT).show();

			} else if (mPassword.getText().length() < 1
					&& mPassword.getText().toString().equals("")) {

				Toast.makeText(getApplicationContext(),
						"please enter password", Toast.LENGTH_SHORT).show();

			} else {

				mValidateUser();

			}
			break;

		case (R.id.register_Btn):

			Intent reg_intent = new Intent(LoginActivity.this,
					RegistrationAcivity.class);
			startActivity(reg_intent);

			break;

		}
	}

	private void mValidateUser() {

		try {
			String encodedUserId = URLEncoder.encode(mUserName.getText()
					.toString(), "UTF-8");
			String encodedPassword = URLEncoder.encode(mPassword.getText()
					.toString(), "UTF-8");

			if (Utils.checkNetwork(getApplicationContext())) {

				System.out.println("Connecting to server for authentication");
				String url = String.format(Utils.LOGIN_URL, encodedUserId,
						encodedPassword);

				progressdialog = ProgressDialog.show(LoginActivity.this, null,
						null);
				progressdialog.setContentView(R.layout.loader);

				Log.v("url", url);
				new LoginParser(url, new LoginHandler()).start();

			} else {

				String prefUserid = loginprefs.getString("username", "");
				String userRole = loginprefs.getString("role", "");
				String password = loginprefs.getString("password", "");

				if (prefUserid.length() > 0) {

					if (mUserName.getText().toString().equals(prefUserid)
							&& mPassword.getText().toString().equals(password)) {

						Utils.USER_ID = prefUserid;
						Utils.ROLENAME = userRole;

						startActivity(new Intent(LoginActivity.this,
								MainDispatchScreenTabView.class).putExtra(
								"from", "login"));
						finish();

					} else {

						alertDialogWithMsg("AOTD",
								"Invalid user id or password");

					}

				} else {

					System.out
							.println("Connecting to server for authentication in offline mode");
					String url = String.format(Utils.LOGIN_URL, encodedUserId,
							encodedPassword);
					progressdialog = ProgressDialog.show(this, "",
							"Please wait...", true);
					progressdialog.show();
					Log.v("url", url);
					new LoginParser(url, new LoginHandler()).start();

				}
			}
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(getApplicationContext(),
					"Error in porcessing login details, contact support",
					Toast.LENGTH_LONG).show();
		}

	}

	public class LoginHandler extends Handler {
		public void handleMessage(android.os.Message msg) {

			String errorMsg = msg.getData().getString("HttpError");
			if (errorMsg.length() > 0) {

				alertDialogWithMsg("AOTD", errorMsg);

			} else {

				if (login_checkbox.isChecked()) {

					SharedPreferences.Editor prefsEditor = loginprefs.edit();
					prefsEditor.putString("username", mUserName.getText()
							.toString());
					prefsEditor.putString("password", mPassword.getText()
							.toString());
					prefsEditor.putString("role", Utils.ROLENAME);
					prefsEditor.putBoolean("rememberMe", true);
					prefsEditor.putString("temppassword", mPassword.getText()
							.toString());
					prefsEditor.commit();

				} else {

					SharedPreferences.Editor prefsEditor = loginprefs.edit();
					prefsEditor.putString("username", mUserName.getText()
							.toString());
					prefsEditor.putString("password", mPassword.getText()
							.toString());
					prefsEditor.putString("role", Utils.ROLENAME);
					prefsEditor.putBoolean("rememberMe", false);
					prefsEditor.putString("temppassword", mPassword.getText()
							.toString());
					prefsEditor.commit();
				}

				try {
					marrDispatchPresentList.clear();
					String encodedRoleName = URLEncoder.encode(
							Utils.ROLENAME.trim(), "UTF-8");
					String encodedId = URLEncoder
							.encode(Utils.USER_ID, "UTF-8");
					String url = String.format(Utils.PRESENT_DISPATCH_URL,
							encodedRoleName, encodedId);
					DispatchAllParser mDispatchparser = new DispatchAllParser(
							new DispatchPresentHandler(), url,
							marrDispatchPresentList);
					mDispatchparser.start();

				} catch (Exception e) {
				}

			}

		}
	};

	class DispatchPresentHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			String errorMsg = msg.getData().getString("HttpError");

			if (errorMsg.length() > 0) {
				try {
					String encodedRoleName = URLEncoder.encode(Utils.ROLENAME,
							"UTF-8");
					String encodedId = URLEncoder
							.encode(Utils.USER_ID, "UTF-8");
					String url = String.format(Utils.DISPATCH_URL,
							encodedRoleName, encodedId);
					OfflineDispatchParser mDispatchparser = new OfflineDispatchParser(
							new OfflineDataHandler(), url,
							marrDispatchListOffline, marrDispatchList);
					mDispatchparser.start();
				} catch (Exception e) {
				}
			} else {
				Allorders.presentOrders = marrDispatchPresentList;

				try {
					String encodedRoleName = URLEncoder.encode(Utils.ROLENAME,
							"UTF-8");
					String encodedId = URLEncoder
							.encode(Utils.USER_ID, "UTF-8");
					String url = String.format(Utils.DISPATCH_URL,
							encodedRoleName, encodedId);
					OfflineDispatchParser mDispatchparser = new OfflineDispatchParser(
							new OfflineDataHandler(), url,
							marrDispatchListOffline, marrDispatchList);
					mDispatchparser.start();
				} catch (Exception e) {
				}
			}
		}
	}

	class OfflineDataHandler extends Handler {
		public void handleMessage(android.os.Message msg) {

			progressdialog.dismiss();
			String errorMsg = msg.getData().getString("HttpError");
			if (errorMsg.length() > 0) {

				startActivity(new Intent(LoginActivity.this,
						MainDispatchScreenTabView.class).putExtra("from",
						"login"));
				finish();
			} else {
				Allorders.dispatchOrders = marrDispatchList;
				Allorders.dispatchOrders_ = marrDispatchListOffline;
				startActivity(new Intent(LoginActivity.this,
						MainDispatchScreenTabView.class).putExtra("from",
						"login"));
				finish();
			}

		}
	}

	public void alertDialogWithMsg(String title, String msg) {

		new AlertDialogMsg(LoginActivity.this, title, msg)
				.setPositiveButton("ok",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).create().show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Sucess",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}