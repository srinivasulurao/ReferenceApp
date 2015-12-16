package com.texastech.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.texastech.bean.GetSplashScreensInfo;
import com.texastech.bean.GetSplashScreensInfo.SplashScreensInfo;
import com.texastech.helper.AppUtil;
import com.texastech.helper.PushRegistration;
import com.texastech.httputil.Action;
import com.texastech.httputil.HomeIntentService;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;


public class SplashActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.splash_background)
	ImageView ivSplashBack;
	
	@InjectView(R.id.splash_logo)
	ImageView ivSplashLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		//new PushRegistration(context);
		Intent intentP = new Intent(getApplicationContext(), PushRegistration.class);
		startService(intentP);
		
		Intent intent = new Intent(getApplicationContext(),HomeIntentService.class);
		intent.putExtra("ACTION", 		Action.GET_HOME_SCREEN.toString());
		startService(intent);
		sendHttpRequest(Action.GET_SPLASH_SCREEN, null);
	}

	@Override
	public void setTitle(TextView tv) {
		
	}

	@Override
	protected void initXmlView() {
		 
	}

	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RestClient client = new RestClient(ac, this);
		client.get();
		
	}

	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		try {
			GetSplashScreensInfo splashScreensInfo = gson.fromJson(response, GetSplashScreensInfo.class);
			if(splashScreensInfo.success){
				for (SplashScreensInfo screensInfo  : splashScreensInfo.screensInfosList) {
					if(MyApplication.getDeviceResulotionId()==Integer.parseInt(screensInfo.deviceResolution)){
						settingPre.setLogoUrl(screensInfo.icon);
						settingPre.setBackButtonUrl(screensInfo.backbutton);
						settingPre.setBackground1Url(screensInfo.background1);
						settingPre.setBackground2Url(screensInfo.background2);
						settingPre.setTitleBarUrl(screensInfo.titlebar);
						settingPre.setTitleColor(screensInfo.titlebartextcolor);
						setSplashBackground();
						break;
					}
				}
			}else{
				startSplashTimer();
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
	
	
	
	private void setSplashBackground(){
		MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getBackground1Url(), ivSplashLogo, AppUtil.getImageSetting());
		
		MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getBackground2Url(), ivSplashBack, AppUtil.getImageSetting(), new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				 
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				startSplashTimer();
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				startSplashTimer();
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				startSplashTimer();
			}
		});
	}
	
	
	private void startSplashTimer(){
		CountDownTimer timer = new CountDownTimer(4000,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				 pushActivity(LoginActivity.class, true);
			}
		};
		timer.start();
	}
}
