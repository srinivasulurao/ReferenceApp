package com.texastech.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
import com.texastech.bean.GetContactsInfo;
import com.texastech.bean.GetContactsInfo.ContactsInfo;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class ContactsActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.list_view)
	ListView listView;
	
	private LinkedHashMap<String,List<ContactsInfo>> hashMap;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_list);
		String title = getIntent().getStringExtra("TITLE");
		listView.setOnItemClickListener(clickListenerDept);
		sendHttpRequest(Action.GET_CONTACTS, null);
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
			GetContactsInfo contactsInfo = gson.fromJson(response, GetContactsInfo.class);
			if(contactsInfo.success && !contactsInfo.contactsInfosList.isEmpty()){
				hashMap = new LinkedHashMap<String, List<ContactsInfo>>();

				for (ContactsInfo info : contactsInfo.contactsInfosList) {
					if(hashMap.containsKey(info.Department)){
						 List<ContactsInfo> keyList = hashMap.get(info.Department);
						 keyList.add(info);
						 hashMap.put(info.Department, keyList);
					}else{
						List<ContactsInfo> temp = new ArrayList<ContactsInfo>();
						temp.add(info);
						hashMap.put(info.Department, temp);
					}
				}
				
				if(!hashMap.isEmpty()){
					List<String> list = new ArrayList<String>(hashMap.keySet());
					Collections.sort(list);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable,list );
					listView.setAdapter(adapter);
				}
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
	
	 
	public OnItemClickListener clickListenerDept = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			String key = parent.getItemAtPosition(position).toString();
			Intent intent = new Intent(getApplicationContext(), DepartmentListActivity.class);
			intent.putExtra("LIST", (Serializable)hashMap.get(key));
			intent.putExtra("TITLE", key);
			startActivity(intent);
		}
	}; 
}
