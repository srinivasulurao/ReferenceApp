package com.texastech.activity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.google.gson.reflect.TypeToken;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.GetMentalHealthInfo.MentalHealthInfo;

public class MentalHealthResourcesListActivity extends BaseActivity {

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
		
		String jsonArr= getIntent().getStringExtra("JSON");
		
		Type listType = new TypeToken<List<MentalHealthInfo>>(){}.getType();
		List<MentalHealthInfo> healthInfos = (List<MentalHealthInfo>) gson.fromJson(jsonArr, listType);
		Collections.sort(healthInfos);
		ArrayAdapter<MentalHealthInfo> adapter = new ArrayAdapter<MentalHealthInfo>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, healthInfos);
		listView.setAdapter(adapter);
	}
	
	@Override
	protected void initXmlView() {
		listView.setOnItemClickListener(clickListener);
	}

	  
	
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			MentalHealthInfo info =(MentalHealthInfo)parent.getItemAtPosition(position);
			Intent intent = new Intent(getApplicationContext(), MentalHealthResourceDetailActivity.class);
			intent.putExtra("INFO",info);
			startActivity(intent);
		}
	}; 
}
