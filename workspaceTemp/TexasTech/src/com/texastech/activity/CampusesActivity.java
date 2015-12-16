package com.texastech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.adapter.CampusAdapter;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.GetCampusMapsInfo;
import com.texastech.bean.GetCampusMapsInfo.CampusMapsInfo;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpConst;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class CampusesActivity extends BaseActivity implements HttpTaskListener{

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
		sendHttpRequest(Action.GET_CAMPUSMAPS, null);
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
			GetCampusMapsInfo campusMapsInfo = gson.fromJson(response, GetCampusMapsInfo.class);
			//ArrayAdapter<CampusMapsInfo> adapter = new ArrayAdapter<CampusMapsInfo>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, campusMapsInfo.campusMapsInfosList);
			listView.setAdapter(new CampusAdapter(getApplicationContext(), campusMapsInfo.campusMapsInfosList));
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
			CampusMapsInfo info =(CampusMapsInfo)parent.getItemAtPosition(position);
			/*Intent intent = new Intent(getApplicationContext(), CampusesDetailActivity.class);
			intent.putExtra("INFO", info);
			startActivity(intent);*/
			Intent intent = new Intent(getApplicationContext(), WebActivity.class);
			intent.putExtra("URL", HttpConst.IMAGE_URL.concat(info.map_image));
			intent.putExtra("TITLE", info.Campus_Name);
			startActivity(intent);
		}
	}; 
}
