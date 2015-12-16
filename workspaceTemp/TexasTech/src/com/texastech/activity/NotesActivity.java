package com.texastech.activity;

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

public class NotesActivity extends BaseActivity{

	@InjectView(R.id.list_view)
	ListView listView;
	
	String []arr={"Reminders","To do"};
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void initXmlView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, arr);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(clickListener);
	}
	
	 @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_list);
	}
	 
	 public OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(position==0){
					pushActivity(RemindersActivity.class, false);
				}else if(position==1){
					pushActivity(ToDoActivity.class, false);
				}
			}
		}; 
}
