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
import com.texastech.bean.GetVirtualTours;
import com.texastech.bean.GetVirtualTours.VirtualTour;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class VirtualToursActivity extends BaseActivity implements HttpTaskListener{

	
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
		
		//String[] arr = getResources().getStringArray(R.array.arr_virtual_tour);
		sendHttpRequest(Action.GET_PHOTO_GALLERY, null);
	}
	
	
	@Override
	protected void initXmlView() {
		listView.setOnItemClickListener(clickListener);
	}

	
	public OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			VirtualTour tour = (VirtualTour)parent.getItemAtPosition(position);
			
			Intent intent = new Intent(getApplicationContext(), VirtualTourGalleryActivity.class);
			intent.putExtra("TITLE", tour.album_name);
			intent.putExtra("TOUR", tour);
			startActivity(intent);
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
			GetVirtualTours getVirtualTours = gson.fromJson(response, GetVirtualTours.class);
			if(getVirtualTours.success){
				ArrayAdapter<VirtualTour> adapter = new ArrayAdapter<VirtualTour>(getApplicationContext(), R.layout.row_simple_next, R.id.tv_lable, getVirtualTours.virtualTours);
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
}
