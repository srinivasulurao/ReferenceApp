package com.texastech.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.GetContactsInfo.ContactsInfo;
import com.texastech.bean.GetFacultyInfo.FacultyInfo;
import com.texastech.helper.AppUtil;
import com.texastech.httputil.HttpConst;

public class DirectoryDetailActivity extends BaseActivity{
	
	//private ContactsInfo info;
	
	@InjectView(R.id.tv_fname)
	TextView tvFname;
	
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	
	@InjectView(R.id.tv_phone)
	TextView tvPhone;
	
	@InjectView(R.id.tv_email)
	TextView tvEmail;
	
	@InjectView(R.id.tv_url)
	TextView tvurl;
	
	@InjectView(R.id.place_holder)
	ImageView ivLogo;
	
	boolean isFacultyInfo=false;
	
	
	private String strPhone="", strEmail="", strUrl="";
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
		isFacultyInfo = getIntent().getBooleanExtra("FACULTY_INFO", false);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_directory_detail);
	}
	
	@Override
	protected void initXmlView() {
		ivLogo.setBackgroundColor(getResources().getColor(R.color.gray));
		
		if(isFacultyInfo){
			FacultyInfo info = (FacultyInfo)getIntent().getSerializableExtra("CONTACTINFO");
			
			TextView tvRank = (TextView)findViewById(R.id.tv_rank);
			tvRank.setVisibility(View.VISIBLE);
			tvRank.setText(info.Rank);
			
			tvFname.setText(info.Title);
			tvTitle.setText(info.campus_name);
			
			tvPhone.setText(info.Phone);
			tvEmail.setText(info.Email);
			tvurl.setText(info.URL);
			imageLoader.displayImage(info.Image, ivLogo, AppUtil.getImageSetting());
			
			strPhone = info.Phone;
			strEmail =  info.Email;
			strUrl = info.URL;
		}else{
			ContactsInfo info = (ContactsInfo)getIntent().getSerializableExtra("CONTACTINFO");
			tvFname.setText(info.Title);
			tvTitle.setText(info.Name);
			tvPhone.setText(info.Phone);
			tvEmail.setText(info.Email);
			tvurl.setText(info.URL);
			imageLoader.displayImage(HttpConst.IMAGE_URL+info.Image, ivLogo, AppUtil.getImageSetting());
			
			strPhone = info.Phone;
			strEmail =  info.Email;
			strUrl = info.URL;
		}
	}
	
	/*@OnClick(R.id.tv_phone)
	public void phoneEvent(){
		if(!TextUtils.isEmpty(tvPhone.getText().toString())){
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+strPhone));
    		startActivity(intent);
		}	
	}
	
	@OnClick(R.id.tv_email)
	public void emailEvent(){
		if(!TextUtils.isEmpty(tvEmail.getText().toString())){
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{strEmail});		  
			email.putExtra(Intent.EXTRA_SUBJECT, "");
			email.putExtra(Intent.EXTRA_TEXT, "");
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
		}
	}
	
	
	@OnClick(R.id.tv_url)
	public void urlEvent(){
		if(!TextUtils.isEmpty(tvurl.getText().toString())){
			Intent intent = new Intent(getApplicationContext(), WebActivity.class);
			 intent.putExtra("URL", strUrl);
			 intent.putExtra("TITLE", tvTitle.getText().toString());
			 startActivity(intent);
		}
	}*/
}
