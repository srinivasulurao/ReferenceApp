package com.aotd.activities;

import java.util.ArrayList;

import com.aotd.model.DriverGeoLocationModel;
import com.aotd.utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 *  
 * @author bharath
 *
 */
public class DriverListScreenActivity extends Activity implements OnItemClickListener{
	
	private ListView mListView;
	Context mContext = DriverListScreenActivity.this;
	private Intent mRecieverIntent;
	private ArrayList<DriverGeoLocationModel> mDriverGeoLocations = null;
	private TextView heading_txt;
	public ImageView imgOnline;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.aotd_driver_list_listview);
		overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
		
		mRecieverIntent = getIntent();
		mDriverGeoLocations = (ArrayList<DriverGeoLocationModel>) mRecieverIntent.getExtras().get("geolocations");
		
		mListView = (ListView)findViewById(R.id.driver_list_listView);
		heading_txt = (TextView)findViewById(R.id.heading_txtView);
		heading_txt.setText("Drivers History");
		imgOnline = (ImageView)findViewById(R.id.aotd_img_mode);
		
		if(new DispatchAdapter().getCount()==0)
			Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
		else		
			mListView.setAdapter(new DispatchAdapter());
		
		
		mListView.setOnItemClickListener(this);
			

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(Utils.checkNetwork(getApplicationContext()))
			imgOnline.setBackgroundResource(R.drawable.online);
		else
			imgOnline.setBackgroundResource(R.drawable.offline);
	}
	
	
	private class DispatchAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
    	
    	public DispatchAdapter() 
        {
        	mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  
        }
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDriverGeoLocations.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		@Override
		public View getView(int pp, View v, ViewGroup parent) {
			 
			
             v = mInflater.inflate(R.layout.aotd_driver_list_listrow, null);  
			 TextView txt_name = (TextView)v.findViewById(R.id.driver_listrow_name);		 
			 txt_name.setText(mDriverGeoLocations.get(pp).getName().trim());
			
			return v;
		}
		
	}
	

	
		
		
		


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		Intent intent=new Intent(DriverListScreenActivity.this,DriverHistoryScreenActivity.class).putExtra("driverinfo", mDriverGeoLocations.get(arg2));
		startActivity(intent);
		
	}
	
}