package com.texastech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.GetSocialMediaInfo;
import com.texastech.bean.GetSocialMediaInfo.SocialMediaInfo;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class SocialMediaActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.list_view)
	ListView listView;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_list);
		sendHttpRequest(Action.GET_SOCIAL_MEDIA, null);
	}
	
	@Override
	protected void initXmlView() {
		listView.setOnItemClickListener(clickListener);
		
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
			GetSocialMediaInfo socialMediaInfo = gson.fromJson(response, GetSocialMediaInfo.class);
			ArrayAdapter<SocialMediaInfo> adapter = new ArrayAdapter<SocialMediaInfo>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, socialMediaInfo.socialMediaInfosList);
			listView.setAdapter(adapter);
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
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			SocialMediaInfo info = (SocialMediaInfo)parent.getItemAtPosition(position);
		    Intent intent = new Intent(getApplicationContext(), WebActivity.class);
			intent.putExtra("TITLE", info.Title);
			intent.putExtra("URL", info.URL);
			//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}; 
}
