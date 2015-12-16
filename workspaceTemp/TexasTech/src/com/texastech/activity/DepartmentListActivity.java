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
import com.texastech.bean.GetContactsInfo.ContactsInfo;

public class DepartmentListActivity extends BaseActivity{
 
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
		
		List<ContactsInfo> contactsInfosList = (List<ContactsInfo>)getIntent().getSerializableExtra("LIST");
		if(contactsInfosList.size()==1){
			Intent intent = new Intent(getApplicationContext(), DirectoryDetailActivity.class);
			intent.putExtra("CONTACTINFO", contactsInfosList.get(0));
			intent.putExtra("TITLE", contactsInfosList.get(0).Title);
			startActivity(intent);
			finish();
			return;
		}
		Collections.sort(contactsInfosList);
		ArrayAdapter<ContactsInfo> adapter = new ArrayAdapter<ContactsInfo>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, contactsInfosList);
		listView.setAdapter(adapter);
	}
	
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			ContactsInfo info = (ContactsInfo)parent.getItemAtPosition(position);
			Intent intent = new Intent(getApplicationContext(), DirectoryDetailActivity.class);
			intent.putExtra("CONTACTINFO", info);
			intent.putExtra("TITLE", info.Title);
			startActivity(intent);
		}
	}; 
}
