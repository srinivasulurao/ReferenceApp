package com.texastech.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.OnClick;

import com.google.zxing.client.android.CaptureActivity;
import com.loopj.android.http.RequestParams;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.helper.AlertManager;
import com.texastech.helper.MLog;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class CheckInActivity extends BaseActivity implements HttpTaskListener{

	@Override
	public void setTitle(TextView tv) {
		tv.setText("CHECK-IN");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_checkin);
	}
	
	@Override
	protected void initXmlView() {
		
	}
	
	
	@OnClick(R.id.btn_start)
	public void onStartEvent(View view){
		startActivityForResult(new Intent(getApplicationContext(), CaptureActivity.class), 0);
	} 
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			MLog.v("SCAN_RESULT", ""+data.getStringExtra("SCAN_RESULT"));
			//http://appddictionstudio.biz/texas-tech/event/eventsurvey/12
			String url = data.getStringExtra("SCAN_RESULT");
			if(url!=null){
				try {
					sendHttpRequest(Action.SAVE_EVENT_SURVERY, url.substring(url.lastIndexOf("/")+1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			Toast.makeText(context, "Unable to decode !", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RestClient client = new RestClient(ac, this);
		
		RequestParams params = new RequestParams();
		params.add("GUID", 		settingPre.getGUID());
		params.add("EventID", 	param[0]);
		client.get(params);
	}

	@Override
	public void onSuccess(Action ac, String response) {
		//{"success":false,"msg": "Unable to save Information"}
		try {
			JSONObject object = new JSONObject(response);
			if(object.getBoolean("success")){
				AlertManager.showSuccessMessage(this, "Message", "Thank you for Checking In");
			}else{
				showMessage("Alert!", "Check-in Failed!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		dismissProgressBar();
	}

	@Override
	public void onFaliure(Action ac, String error) {
		if(!isActivityVisible)return;
		showMessage("Alert!", error);
		dismissProgressBar();
	}
}
