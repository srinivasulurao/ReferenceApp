package com.texastech.httputil;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

import com.texastech.helper.FileUtil;

public class HomeIntentService extends IntentService implements HttpTaskListener{

	public HomeIntentService() {
		super("HomeIntentService");
	}
	
	
	@Override
	public void sendHttpRequest(final Action ac, final String... param) {
	    Handler mHandler = new Handler(getMainLooper());
	    mHandler.post(new Runnable() {
	        @Override
	        public void run() {
	        	 RestClient client = new RestClient(ac, HomeIntentService.this);
	        	 client.get();
	        }
	    });
	}

	@Override
	public void onSuccess(Action ac, String response) {
		switch (ac) {
			case GET_HOME_SCREEN:
				try {
					FileUtil.writeFile(getApplicationContext(), ac.toString()+".txt", response);
				} catch (Exception e) {
					e.printStackTrace(); 
				}
			break;
			//---------------------------//
		}
	}

	@Override
	public void onFaliure(Action ac, String error) {
		
	}
	
	
	@Override
	public void onCreate() { 
		super.onCreate();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String strAction=intent.getStringExtra("ACTION");
		Action action = Action.fromString(strAction);
		if(action==null)return; 
		sendHttpRequest(action, null);
	}
}
