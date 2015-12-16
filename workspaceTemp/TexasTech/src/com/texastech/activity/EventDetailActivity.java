package com.texastech.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.GetEvetntInfo.EvetntInfo;
import com.texastech.helper.AppUtil;
import com.texastech.helper.DateTimeUtil;
import com.texastech.httputil.HttpConst;

public class EventDetailActivity extends BaseActivity{

	@InjectView(R.id.tv_title)
	TextView tvTitle;
	
	@InjectView(R.id.tv_detail)
	TextView tvDetail;
	
	@InjectView(R.id.tv_date_time)
	TextView tvDateTime;
	
	@InjectView(R.id.tv_address)
	TextView tvAddress;
	
	@InjectView(R.id.place_holder)
	ImageView ivLogo;
	
	@InjectView(R.id.tv_web)
	TextView tvWeb;
	
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText("Event Details");
	}

	@Override
	protected void initXmlView() {
		
	}


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_event_detail);
		EvetntInfo info = (EvetntInfo)getIntent().getSerializableExtra("EVENT");
		
		tvTitle.setText(info.Title);
		tvDetail.setText(info.Description);
		tvDateTime.setText(DateTimeUtil.getDateTimeInFromat(info.Date));
		tvAddress.setText(info.Address);
		
		imageLoader.displayImage(HttpConst.IMAGE_URL+info.Icon, ivLogo, AppUtil.getImageSetting());
		tvWeb.setText(info.UploadURL);
	}
}
