package com.texastech.app;

import org.json.JSONObject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.RequestParams;
import com.texastech.helper.AlertManager;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class ForgetPasswordActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.et_email)
	EditText etEmail;

	@InjectView(R.id.btn_send)
	Button btnSend;

	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_forget_password);
	}

	@Override
	protected void initXmlView() {

	}

	@OnClick(R.id.btn_send)
	public void reSetPasswordEvent() {
		if(etEmail.getText().toString().equals("")){
			etEmail.setError("Please enter email.");
		}else{
			sendHttpRequest(Action.FORGET_PASSWORD, null);
		}
	}
	
	
	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RequestParams params = new RequestParams();
		params.add("email", 	etEmail.getText().toString());
		RestClient client = new RestClient(ac, this);
		client.get(params);
	}


	@Override
	public void onSuccess(Action ac, String response) {
		//{"Success":false,"result":"Enter Your Email ID !"}
		if(!isActivityVisible)return;
		try {
			JSONObject object = new JSONObject(response);
			if(object.getBoolean("Success")){
				 AlertManager.showSuccessMessage(this, "Message", object.getString("result"));
			}else{
				showMessage("Alert!", object.getString("result"));
			}
		} catch (Exception e) {
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
