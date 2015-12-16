package com.texastech.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.app.BaseActivity;
import com.texastech.app.MyApplication;
import com.texastech.app.R;
import com.texastech.helper.AppUtil;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class MentalHealthResourcesActivity extends BaseActivity implements HttpTaskListener {

	@InjectView(R.id.logo)
	ImageView imageView;
	
	@InjectView(R.id.list_view)
	ListView listView;
	
	 HashMap<String, JSONArray> hashMap = new HashMap<String, JSONArray>();
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_mental_health_resources);
		sendHttpRequest(Action.GET_MENTAL_HEALTH, null);
	}

	@Override
	protected void initXmlView() {
		MyApplication.getLoader().displayImage(IMAGE_URL+settingPre.getLogoUrl(), imageView, AppUtil.getImageSetting());
		listView.setOnItemClickListener(clickListener);
	}

	
	
	
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			String key =parent.getItemAtPosition(position).toString();
			Intent intent = new Intent(getApplicationContext(), MentalHealthResourcesListActivity.class);
			intent.putExtra("JSON", hashMap.get(key).toString());
			intent.putExtra("TITLE", key);
			startActivity(intent);
		}
	}; 
	
	
	
	
	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		try {
			hashMap.clear();
			JSONObject object = new JSONObject(response);
			if(object.getBoolean("success")){
				JSONObject questionMark = object.getJSONObject("result");
			    Iterator keys = questionMark.keys();
			    while(keys.hasNext()) {
			        // loop to get the dynamic key
			        String currentDynamicKey = (String)keys.next();
			        // get the value of the dynamic key
			        JSONArray currentDynamicValue = questionMark.getJSONArray(currentDynamicKey);
			        hashMap.put(currentDynamicKey, currentDynamicValue);
			    }
			    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, new ArrayList<String>(hashMap.keySet()));
				listView.setAdapter(adapter);
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
	
	@Override
	public void sendHttpRequest(Action ac, String... param) {
		showProgressBar();
		RestClient client = new RestClient(ac, this);
		client.get();
	}
}
