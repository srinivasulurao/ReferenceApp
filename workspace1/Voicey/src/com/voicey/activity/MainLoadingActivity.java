package com.voicey.activity;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import com.voicey.model.AudioInfo;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class MainLoadingActivity extends Activity {

	SharedPreferences sharedPreferences;
	Webservices Webservices = new Webservices();
	String userId;
	  String regId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_loading);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(MainLoadingActivity.this);
		userId = sharedPreferences.getString("userCode", null);
		if (userId != null) {
			
				
				
				
				
			if(Constants.hasConnection(this)){
				
			new GetUserIdAsyncTask().execute();
			}else{
				
				Toast.makeText(getBaseContext(), "No Internet Connection.",
						Toast.LENGTH_LONG).show();
			}
		} else {
			
			Toast.makeText(getBaseContext(), "No Internet Connection.",
					Toast.LENGTH_LONG).show();
		}
			  /*Intent i = new Intent();
			  i.setClass(MainLoadingActivity.this, HomeActivity.class);
			  
			  startActivity(i);*/
			/*if(Constants.hasConnection(this)){
			new GetAudioList().execute();
			}else{
				
				Toast.makeText(getBaseContext(), "No Internet Connection.",
						Toast.LENGTH_LONG).show();
			}*/

			/*
			 Thread splashThread = new Thread() {
			  
			  @Override public void run() { try { int waited = 0; while (waited
			 < 5000) { sleep(100); waited += 100; } } catch
			  (InterruptedException e) { // do nothing
			  
			  } finally { finish();
		
			  Intent i = new Intent(); i.setClass(MainLoadingActivity.this,
			  HomeActivity.class);
			  
			  startActivity(i);
			  
			  } } }; splashThread.start();*/
			 

		//}

	}

	private class GetUserIdAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

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
							.getDefaultSharedPreferences(MainLoadingActivity.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("friendRequest", jObj.getString("friend"));
					editor.putString("sharecount", jObj.getString("shared"));
					editor.commit();
					//new GetAudioList().execute();

					  Intent i = new Intent(); i.setClass(MainLoadingActivity.this,
							  HomeActivity.class);
					  i.putExtra("Navigation", "normal");
							  
							  startActivity(i);
					
					 
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class GetAudioList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.getAudioList();
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			try {
				// List<String> selItemList = new ArrayList<String>();
				Set<String> audioItemset = new HashSet<String>();
				AudioInfo audioInfo;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							audioInfo = new AudioInfo();
							audioInfo.setTitle(jObj.getString("title"));
							audioInfo.setSource(jObj.getString("source"));
							audioInfo.setPublic_control(jObj
									.getString("public_control"));
							audioInfo.setUser_control(jObj
									.getString("user_control"));
							audioInfo.setFileName(jObj.getString("audio"));
							audioInfo.setId(jObj.getString("id"));
							audioInfo.setCounter(jObj.getString("counter"));
							audioInfo.setUser_code(jObj.getString("user_id"));
							audioInfo.setPosition(i);

							Gson gson = new Gson();
							String audioInfoStr = gson.toJson(audioInfo);

							// selItemList.add(audioInfoStr);

							audioItemset.add(audioInfoStr);

						}

						SharedPreferences sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(MainLoadingActivity.this);

						SharedPreferences.Editor editor = sharedPreferences
								.edit();

						editor.putStringSet("audioItemset", audioItemset);
						editor.putString("islistsyncrequired", "NO");
						editor.commit();

						Intent intent = new Intent();
						intent.setClass(MainLoadingActivity.this,
								HomeActivity.class);
						/*
						 * intent.putStringArrayListExtra("audioInfoList",
						 * (ArrayList<String>) selItemList);
						 */

						startActivity(intent);

					}

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

}
