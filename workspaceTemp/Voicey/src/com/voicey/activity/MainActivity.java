package com.voicey.activity;

import static com.voicey.activity.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.voicey.activity.CommonUtilities.EXTRA_MESSAGE;
import static com.voicey.activity.CommonUtilities.SENDER_ID;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class MainActivity extends Activity {
	String regId = null;
	AsyncTask<Void, Void, Void> mRegisterTask;
	private ProgressBar progressBar;
	private int progressStatus = 0;
	private Handler handler = new Handler();
	SharedPreferences sharedPreferences;
	Webservices Webservices = new Webservices();
	String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_loading);
		progressBar = (ProgressBar) findViewById(R.id.dialogProgressBar);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		} else {

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(MainActivity.this);
			userId = sharedPreferences.getString("userCode", null);
			if (userId == null) {

				if (Constants.hasConnection(this)) {

					GCMRegistrar.checkDevice(this);

					GCMRegistrar.checkManifest(this);

					registerReceiver(mHandleMessageReceiver, new IntentFilter(
							DISPLAY_MESSAGE_ACTION));

					// Get GCM registration id
					regId = GCMRegistrar.getRegistrationId(this);

					// Check if regid already presents
					if (regId.equals("")) {
						// Registration is not present, register now with GCM
						GCMRegistrar.register(this, SENDER_ID);

						regId = GCMRegistrar.getRegistrationId(this);
						/*
						 * Intent i = new Intent();
						 * i.setClass(MainActivity.this,
						 * MainLoadingActivity.class);
						 * 
						 * startActivity(i);
						 */

						// new GetUserIdAsyncTask().execute();

					} else {
						// Device is already registered on GCM
						if (GCMRegistrar.isRegisteredOnServer(this)) {
							// Skips registration.

							Intent i = new Intent();
						i.setClass(MainActivity.this,
									MainLoadingActivity.class);

						startActivity(i);
							//new GetNotifyCountAsyncTask().execute();

						} else {

							new GetUserIdAsyncTask().execute();

						}

					}
				}

			} else {

				Thread splashThread = new Thread() {

					@Override
					public void run() {
						try {

							while (progressStatus < 100) {
								progressStatus += 1;
								sleep(100);
								// while (waited < 2000) {
								// sleep(100);
								// waited += 100;
								handler.post(new Runnable() {
									public void run() {
										progressBar.setProgress(progressStatus);
									}
								});
							}
						} catch (InterruptedException e) { // do nothing

						} finally {							
							Intent i = new Intent();
							i.setClass(MainActivity.this, HomeActivity.class);
							i.putExtra("Navigation", "normal");

							startActivity(i);
						}
					}
				};
				splashThread.start();
			}

		}
	}

	private class GetUserIdAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getUserId(regId);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {
					GCMRegistrar.setRegisteredOnServer(MainActivity.this, true);
					JSONObject jObj = new JSONObject(result);
					sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(MainActivity.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("userId", jObj.getString("userid"));
					editor.putString("userCode", jObj.getString("uniqueid"));
					editor.commit();
					// new GetAudioList().execute();

					if (jObj.getString("userid") != null) {
							new GetNotifyCountAsyncTask().execute();
					}
					
//					Intent i = new Intent();
//					i.setClass(MainActivity.this, MainLoadingActivity.class);
//
//					startActivity(i);

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class GetNotifyCountAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getNotificationCount(userId);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {

					JSONObject jObj = new JSONObject(result);
					sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(MainActivity.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("friendRequest", jObj.getString("friend"));
					editor.putString("sharecount", jObj.getString("shared"));
					editor.commit();

					Intent i = new Intent();
					i.setClass(MainActivity.this, HomeActivity.class);
					i.putExtra("Navigation", "normal");

					startActivity(i);
					finish();
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}
	
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String newMessage =
			// intent.getExtras().getString(Constant.EXTRA_MESSAGE);
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			WakeLocker.release();

		}
	};

}
