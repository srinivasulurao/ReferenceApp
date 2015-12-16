package com.texastech.activity;

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

import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.GetResourcesInfo.ResourcesInfo;

public class StudentResourceListActivity extends BaseActivity{
 
	@InjectView(R.id.list_view)
	ListView listView;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void initXmlView() {
		listView.setOnItemClickListener(clickListener);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_list);
		
		List<ResourcesInfo> contactsInfosList = (List<ResourcesInfo>)getIntent().getSerializableExtra("LIST");
		Collections.reverse(contactsInfosList);
		ArrayAdapter<ResourcesInfo> adapter = new ArrayAdapter<ResourcesInfo>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, contactsInfosList);
		listView.setAdapter(adapter);
	}
	
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ResourcesInfo info = (ResourcesInfo)parent.getItemAtPosition(position);
			
			Intent intent = new Intent(getApplicationContext(), WebActivity.class);
			intent.putExtra("TITLE", info.TeaserSentence);
			intent.putExtra("URL", IMAGE_URL+info.UploadURL);
			startActivity(intent);
		}
	}; 
}
