package com.omkar.myactivitytracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.omkar.myactivitytracker.DayEnd.SmileyAdapter;

import com.personaltrainer.database.LoginDB;
import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.model.ActivityPoints;
import com.personaltrainer.model.RegistrationModel;

import com.personaltrainer.widgets.Utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SplashActivity extends Activity implements OnClickListener{

	private static int SPLASH_TIME_OUT = 1000;
	RelativeLayout imgLogo;
	EditText edt_password;
	Button btn_Login;
	LinearLayout relMain;
	TextView txt_forgot;
	int logo_x, logo_y;
	String sMail="", sPassword="", sFrom="";
	boolean isLock=false;

	void initilizeUI()
	{
		imgLogo = (RelativeLayout)findViewById(R.id.imgLogo);
		edt_password = (EditText)findViewById(R.id.edt_password);
		btn_Login = (Button)findViewById(R.id.btn_Login);
		relMain = (LinearLayout)findViewById(R.id.relMain);
		txt_forgot = (TextView)findViewById(R.id.txt_forgot);
		

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		isLock = preferences.getBoolean("lock", false);
		sMail = preferences.getString("email", "");
		sPassword = preferences.getString("password", "");
		

		
	}

	private class MyAnimationListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			imgLogo.clearAnimation();
			LayoutParams lp = new LayoutParams(imgLogo.getWidth(), imgLogo.getHeight());
			lp.setMargins(logo_x, logo_y-400, 0, 0);
			imgLogo.setLayoutParams(lp);
			lp.addRule(RelativeLayout.ABOVE, R.id.relMain);

			//Animate the Layout:
			if(sFrom.equalsIgnoreCase("lock"))
			{
				relMain.setVisibility(View.VISIBLE);
				Animation bottomUp = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.bottom_up);
				relMain.startAnimation(bottomUp);
				relMain.setVisibility(View.VISIBLE);
			}
			else
			{
				Intent in = new Intent(SplashActivity.this,Dashboard.class);
				startActivity(in);
				finish();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		isLock = preferences.getBoolean("enable", false);
		sMail = preferences.getString("email", "");
		sPassword = preferences.getString("password", "");
		
		
		
		
		if(isLock)
		{
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.splash);
			
			initilizeUI();

			btn_Login.setOnClickListener(this);
			txt_forgot.setOnClickListener(this);
			
			/*ViewTreeObserver vto = imgLogo.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					// TODO Auto-generated method stub

					logo_x = imgLogo.getLeft();
					logo_y = imgLogo.getTop();

				}
			});

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					sFrom="lock";
					//Animate the APP LOGO:
					TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 70);
					animation.setDuration(1000);
					animation.setFillAfter(false);
					animation.setAnimationListener(new MyAnimationListener());
					imgLogo.startAnimation(animation);

				}
			}, 1000);*/
		}
		else
		{
			Intent in = new Intent(SplashActivity.this,Dashboard.class);
			startActivity(in);
			finish();
			
		}

		


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch(v.getId())
		{
		case R.id.btn_Login:
			String sPassword = edt_password.getText().toString().trim();

			if(sPassword.equals(""))
			{
				Utils.showAlertBoxSingle(SplashActivity.this, "Error", "Please Enter the Password");
			}
			else
			{
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				String mPassword = preferences.getString("password","");

				if(sPassword.equals(mPassword))
				{
					Intent i = new Intent(SplashActivity.this, Dashboard.class);
					startActivity(i);
					finish();
				}
				else
				{
					Utils.showAlertBoxSingle(SplashActivity.this, "Error", "Please Enter the Correct Password");
				}
			}

			break;

		case R.id.txt_forgot:
			if(hasConnection(SplashActivity.this))
			{
				new ForgotPassword(new MyHandler()).start();
			}
			else
			{
				Utils.showAlertBoxSingle(SplashActivity.this, "Error", "Please Check for Internet Connection");
			}
			break;
		}

	}
	
	/*Chexk for Internet Connection*/
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(
				Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}

	public class MyHandler extends Handler
	{
		public void handleMessage(android.os.Message msg) 
		{
			String errorMsg=msg.getData().getString("HttpError");
			int len = errorMsg.length();
			if(errorMsg.length()>0)
			{
				Utils.showAlertBoxSingle(SplashActivity.this, "Error", "Unable to send password to your email");
			}
			else
			{
				Utils.showAlertBoxSingle(SplashActivity.this, "Forgot Password", "Password Sent to your email.");
			}
		}
	}

	private class ForgotPassword extends Thread
	{
		private Handler parentHandler;
		String sMsg="";
		
		
		public ForgotPassword(Handler pHandler)
		{
			this.parentHandler = pHandler;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(); 
			pairs.add(new BasicNameValuePair("mail",sMail));  
			pairs.add(new BasicNameValuePair("password",sPassword));  
			
			String s= sMail;
			String s1 = sPassword;
			sMsg="";
			String str2 ="";
			try
			{
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://mapimoapp.com/personal_trainer/forget_password.php?page=forget_password");
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				str2 = EntityUtils.toString(httpEntity);
				
				//[{"Result" : "0"}]
				
				JSONObject jObject = new JSONObject(str2);
				String sRes = jObject.getString("Result");
				if(sRes.equalsIgnoreCase("0"))
				{
					sMsg="Error";
				}
				
			}
			catch(Exception e)
			{
				sMsg = "Error";
			}
			
			Message messageToParent = new Message();
			Bundle messageData = new Bundle();
			messageToParent.what = 0;
			messageData.putString("HttpError",sMsg);
			messageToParent.setData(messageData);
			parentHandler.sendMessage(messageToParent);
		}
	}




}
