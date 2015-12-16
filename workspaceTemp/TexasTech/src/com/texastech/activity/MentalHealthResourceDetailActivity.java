package com.texastech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.GetMentalHealthInfo.MentalHealthInfo;

public class MentalHealthResourceDetailActivity extends BaseActivity{
	 
	private MentalHealthInfo info;
	
	@InjectView(R.id.tv_fname)
	TextView tvFname;
	
	@InjectView(R.id.tv_title)
	TextView tvTitle;
	
	@InjectView(R.id.tv_phone)
	TextView tvPhone;
	
	@InjectView(R.id.tv_email)
	TextView tvEmail;
	
	@InjectView(R.id.tv_address)
	TextView tvAddress;
	
	@InjectView(R.id.tv_detail)
	TextView tvDetail;
	
	@InjectView(R.id.tv_detail_1)
	TextView tvDetail1;
	
	@InjectView(R.id.tv_detail_2)
	TextView tvDetail2;
	
	@Override
	public void setTitle(TextView tv) {
		info = (MentalHealthInfo)getIntent().getSerializableExtra("INFO");
		tv.setText(info.Campus);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mental_health_detail);
	}

	@Override
	protected void initXmlView() {
		if(info.pdf_document!=null && !TextUtils.isEmpty(info.pdf_document)){
			TextView tvPdf = (TextView)findViewById(R.id.btn_save);
			tvPdf.setText("PDF");
			tvPdf.setVisibility(View.VISIBLE);
			tvPdf.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), WebActivity.class);
					intent.putExtra("TITLE", info.Campus);
					intent.putExtra("URL", IMAGE_URL+info.pdf_document);
					startActivity(intent);
				}
			});
		}
			
		tvFname.setText(info.ContactPerson);
		tvAddress.setText(info.Address);
		tvTitle.setText(info.Campus);
		tvPhone.setText(info.Phone);
		tvEmail.setText(info.Email);
		
		tvDetail.setText(info.Details);
		tvDetail1.setText(info.Details2.replaceAll("/n", "\n"));
		tvDetail2.setText(info.Details3.replaceAll("/n", "\n"));
	}
	
	
	/*@OnClick(R.id.tv_phone)
	public void phoneEvent(){
		if(!TextUtils.isEmpty(tvPhone.getText().toString())){
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+info.Phone));
    		startActivity(intent);
		}	
	}
	
	@OnClick(R.id.tv_email)
	public void emailEvent(){
		if(!TextUtils.isEmpty(tvEmail.getText().toString())){
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[]{info.Email});		  
			email.putExtra(Intent.EXTRA_SUBJECT, "");
			email.putExtra(Intent.EXTRA_TEXT, "");
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
		}
	}*/
}
