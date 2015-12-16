package com.texastech.app;

import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.RequestParams;
import com.texastech.bean.GetLoginInfo;
import com.texastech.helper.AppConstant;
import com.texastech.helper.AppUtil;
import com.texastech.helper.PushRegistration;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class LoginActivity  extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.et_email)
	EditText etEmail;
	
	@InjectView(R.id.et_password)
	EditText etPassword;
	
	@InjectView(R.id.chk_remember_me)
	CheckBox chkRememberMe;
	
	@InjectView(R.id.logo)
	ImageView imageView; 
	
	@InjectView(R.id.btn_sign_in)
	Button btnSignIn;
	
	private SharedPreferences loginPreferences;
	
	private SharedPreferences.Editor loginPrefsEditor;
	
	private Boolean saveLogin;
	
	@Override
	public void setTitle(TextView tv) {
		
	}

	@Override
	protected void initXmlView() {
		etEmail.setText("");
		etPassword.setText("");
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getLogoUrl(), imageView, AppUtil.getImageSetting());
		loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
        	etEmail.setText(loginPreferences.getString("username", ""));
        	etPassword.setText(loginPreferences.getString("password", ""));
        	chkRememberMe.setChecked(true);
        }
	}
	
	
	@OnClick(R.id.btn_sign_in)
	public void onSignInEvent(View view){
		viewAnimation(view);
		if(TextUtils.isEmpty(etEmail.getText().toString())){
			etEmail.setError("Please enter username.");
			return;
		}
		if(TextUtils.isEmpty(etPassword.getText().toString())){
			etPassword.setError("Please enter password.");
			return;
		}
		
		if (chkRememberMe.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", etEmail.getText().toString());
            loginPrefsEditor.putString("password", etPassword.getText().toString());
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
		 sendHttpRequest(Action.LOGIN, null);
	}
	
	@OnClick(R.id.btn_forget_pwd)
	public void onForgetPasswordEvent(View view){
		viewAnimation(view);
		Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
		intent.putExtra("TITLE", "Forget Password");
		startActivity(intent);
		
	}
	
	@OnClick(R.id.btn_sign_up)
	public void onSignUpEvent(View view){
		viewAnimation(view);
		Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
		intent.putExtra("TITLE", "Sign Up");
		startActivity(intent);
	}
	
	
	
	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RequestParams params = new RequestParams();
		params.add("email", etEmail.getText().toString());
		params.add("password",etPassword.getText().toString());
		params.add("GUID", settingPre.getGUID());
		params.add("DeviceToken", PushRegistration.getDeviceToken(getApplicationContext()));
		RestClient client = new RestClient(ac, this);
		client.post(params);
	}

	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		try {
			JSONObject object = new JSONObject(response);
			if(object.getBoolean("success")){
				GetLoginInfo loginInfo = gson.fromJson(response, GetLoginInfo.class);
				AppConstant.loginUser = loginInfo.loginInfo;
				pushActivity(HomeActivity.class, false);
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
