package com.texastech.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.texastech.adapter.FacultyDirAdapter;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.bean.GetFacultyInfo;
import com.texastech.bean.GetFacultyInfo.FacultyInfo;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class FacultyDirectoryActivity extends BaseActivity implements HttpTaskListener{

	@InjectView(R.id.list_view)
	ListView listView; 
	
	@InjectView(R.id.et_search)
	EditText etSearch;
	
    private List<FacultyInfo> contactsInfos; 
    
    Map<String, Integer> mapIndex;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.list_alphabet);
		listView.setOnItemClickListener(clickListenerFacDir);
		sendHttpRequest(Action.GET_FACULTY_DIRECTORY_LIST, null);
	}
	
	
	@Override
	protected void initXmlView() {
		displayIndex();
		
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				List<FacultyInfo> temp = new ArrayList<FacultyInfo>();
				for (int i = 0; i < contactsInfos.size(); i++) {
					FacultyInfo info = contactsInfos.get(i);
					if(info.LastName.toLowerCase().contains(etSearch.getText().toString().toLowerCase())){
						temp.add(info);
					}
				}
				setListView(temp);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	
	
	private void displayIndex() {
		LinearLayout indexLayout = (LinearLayout) findViewById(R.id.sideIndex);
		TextView textView;
		String  str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < str.length(); i++) {
			textView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
			textView.setText(String.valueOf(str.charAt(i)));
			textView.setOnClickListener(indexListener);
			indexLayout.addView(textView);
		}
	}
	
	private OnClickListener indexListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			try {
				TextView selectedIndex = (TextView) view;
				listView.setSelection(mapIndex.get(selectedIndex.getText()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

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
			GetFacultyInfo contactsInfo = gson.fromJson(response, GetFacultyInfo.class);
			if(contactsInfo.success && !contactsInfo.contactsInfosList.isEmpty()){
				contactsInfos = contactsInfo.contactsInfosList;
				Collections.sort(contactsInfos);
				setListView(contactsInfos);
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
	
	
	
	public OnItemClickListener clickListenerFacDir = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			FacultyInfo info = (FacultyInfo)parent.getItemAtPosition(position);
			Intent intent = new Intent(getApplicationContext(), DirectoryDetailActivity.class);
			intent.putExtra("CONTACTINFO", info);
			intent.putExtra("TITLE", view.getTag().toString());
			intent.putExtra("FACULTY_INFO", true);
			startActivity(intent);
		}
	}; 
	
	
	
	    private void setListView(List<FacultyInfo> infos){
	    	mapIndex = new LinkedHashMap<String, Integer>();
	    	String temp = "";
	    	int index=0;
	    	for (FacultyInfo contactsInfo : infos) {
				if(!temp.equals(contactsInfo.LastName.substring(0, 1))){
					contactsInfo.isHeader = true;
					mapIndex.put(contactsInfo.LastName.substring(0, 1), index);
				}else{
					contactsInfo.isHeader = false;
				}
				temp = contactsInfo.LastName.substring(0, 1);
				index++;
			}
	    	listView.setAdapter(new FacultyDirAdapter(getApplicationContext(), infos));
	    }
}
