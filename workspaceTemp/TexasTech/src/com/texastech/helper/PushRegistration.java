package com.texastech.helper;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.RequestParams;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpConst;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class PushRegistration extends IntentService  implements HttpConst , HttpTaskListener{
	 
	private SettingPreference preference;
	
	public PushRegistration() {
		super("PushRegistration");
		setIntentRedelivery(true);
	} 

	
	@Override
	public void onCreate() {
		super.onCreate();
		preference = new SettingPreference(getApplicationContext());
	}
	
	
	private void setDeviceToken(Context context,String token){
		SharedPreferences preferences = context.getSharedPreferences("GCM", 0);
		Editor editor = preferences.edit();
		editor.putString("TOKEN", token);
		editor.commit();
	}
	
	public static String getDeviceToken(Context context){
		SharedPreferences preferences = context.getSharedPreferences("GCM", 0);
		return preferences.getString("TOKEN", "");
	}


	@Override
	public void sendHttpRequest(final Action ac, final String... param) {
		Handler mHandler = new Handler(getMainLooper());
	    mHandler.post(new Runnable() {
	        @Override
	        public void run() {
	        	RequestParams params = new RequestParams();
	   		 
	    		params.add("DeviceToken", 	param[0]);
	    		params.add("DeviceType",	"Android"); 
	    		params.add("DeviceID",		param[0]);
	    		RestClient client = new RestClient(ac, PushRegistration.this);
	    		client.post(params);
	        }
	    });
		
		
	}


	@Override
	public void onSuccess(Action ac, String response) {
		MLog.i("response-->>", response);
		try {
			JSONObject object = new JSONObject(response);
			String guid = object.getJSONArray("result").getJSONObject(0).getString("GUID");
			MLog.v("GUID", "-->>"+guid);
			preference.setGUID(guid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onFaliure(Action ac, String error) {
		
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			GCMRegistrar.checkDevice(getApplicationContext());

			GCMRegistrar.checkManifest(getApplicationContext());

			if (GCMRegistrar.isRegistered(getApplicationContext())) {
				MLog.d("info 1", GCMRegistrar.getRegistrationId(getApplicationContext()));
			}

			final String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
			if (regId.equals("")) {
				// register the device, passing the SENDER_ID you got when you
				// signed up for GCM.
				GCMRegistrar.register(getApplicationContext(), senderId);
			} else {
				setDeviceToken(getApplicationContext(), regId);
				if(TextUtils.isEmpty(preference.getGUID())){
					sendHttpRequest(Action.GET_GUID, regId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
