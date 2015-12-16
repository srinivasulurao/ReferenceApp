package com.texastech.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.InjectView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.texastech.adapter.EventAdapter;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.CalenderEvent;
import com.texastech.bean.GetEvetntInfo;
import com.texastech.bean.GetEvetntInfo.EvetntInfo;
import com.texastech.helper.AlertManager;
import com.texastech.helper.CalendarUtility;
import com.texastech.helper.DateTimeUtil;
import com.texastech.httputil.Action;
import com.texastech.httputil.HttpTaskListener;
import com.texastech.httputil.RestClient;

public class EventsActivity extends BaseActivity implements HttpTaskListener{
	
	private GoogleMap map;
	
	ListView listView;
	
	private PullToRefreshListView mPullRefreshListView;
	
	
	/*@InjectView(R.id.tv_list)
	TextView tvList;
	
	@InjectView(R.id.tv_map)
	TextView tvMap;*/
	
	@InjectView(R.id.view_flip)
	ViewFlipper flipper;
	
	@InjectView(R.id.btn_save)
	TextView btnCheckIn;
	
	private LinkedHashMap<String,List<EvetntInfo>> hashMap;
	
	private HashMap<Marker, EvetntInfo> markerMAp=new HashMap<Marker, EvetntInfo>();
	
	public static boolean allowCalendar=false; 
	
	private List<EvetntInfo> infos;
	
	@Override
	public void setTitle(TextView tv) {
		tv.setText(getIntent().getStringExtra("TITLE"));
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ptr_list);
		showProgressBar();
		sendHttpRequest(Action.GET_EVENTS, null);
	}
	
	
	@Override
	protected void initXmlView() {
		btnCheckIn.setVisibility(View.VISIBLE);
		btnCheckIn.setText("Check-in");
		btnCheckIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pushActivity(CheckInActivity.class, false);
			}
		});
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				sendHttpRequest(Action.GET_EVENTS, null);
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				//Toast.makeText(EventsActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
			}
		});

		listView = mPullRefreshListView.getRefreshableView();
		listView.setOnItemClickListener(clickListener);
		
		try {
			FragmentManager myFragmentManager = getSupportFragmentManager();
			SupportMapFragment mySupportMapFragment =(SupportMapFragment)myFragmentManager.findFragmentById(R.id.event_map); 
			map =mySupportMapFragment.getMap();
			if(map!=null){
				//default location
				//map.animateCamera(CameraUpdateFactory.newLatLngZoom(AppConstant.defaultLatLon, 14));
				
				// Enabling MyLocation Layer of Google Map
				map.setMyLocationEnabled(true);
				map.getUiSettings().setZoomControlsEnabled(true);
				map.getUiSettings().setCompassEnabled(true);
				map.getUiSettings().setMyLocationButtonEnabled(true);
				map.getUiSettings().setAllGesturesEnabled(true);
				
				map.setInfoWindowAdapter(new MInfoWindowAdapter());
				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {
						EvetntInfo info = (EvetntInfo)markerMAp.get(marker);
						loadDefaultMap(info);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*@OnClick(R.id.tv_list)
	public void listEvent(){
		if(!isListEnable){
			flipper.setDisplayedChild(0);
			tvList.setBackgroundResource(R.drawable.white_listview);
			tvList.setTextColor(Color.RED);
			tvMap.setBackgroundResource(R.drawable.red_mapview);
			tvMap.setTextColor(Color.WHITE);
			isListEnable=true;
		}
	}
	
	@OnClick(R.id.tv_map)
	public void mapEvent(){
		if(isListEnable){
			flipper.setDisplayedChild(1);
			tvList.setBackgroundResource(R.drawable.red_listview);
			tvList.setTextColor(Color.WHITE);
			tvMap.setBackgroundResource(R.drawable.white_mapview);
			tvMap.setTextColor(Color.RED);
			isListEnable=false;
		}
	}*/

	@Override
	public void sendHttpRequest(Action ac, String... param) {
		//showProgressBar();
		RestClient client = new RestClient(ac, this);
		client.get();
	}

	@Override
	public void onSuccess(Action ac, String response) {
		if(!isActivityVisible)return;
		try {
			hashMap = new LinkedHashMap<String, List<EvetntInfo>>();
			
			GetEvetntInfo getEvetntInfo = gson.fromJson(response, GetEvetntInfo.class);
			Collections.sort(getEvetntInfo.evetntInfosList);
			
			for (EvetntInfo info : getEvetntInfo.evetntInfosList) {
				if(hashMap.containsKey(DateTimeUtil.getDateFormat(info.Date))){
					 List<EvetntInfo> keyList = hashMap.get(DateTimeUtil.getDateFormat(info.Date));
					 keyList.add(info);
					 hashMap.put(DateTimeUtil.getDateFormat(info.Date), keyList);
				}else{
					List<EvetntInfo> temp = new ArrayList<EvetntInfo>();
					temp.add(info);
					hashMap.put(DateTimeUtil.getDateFormat(info.Date), temp);
				}
			}
			
			infos = new ArrayList<EvetntInfo>();
			for (Map.Entry<String, List<EvetntInfo>> entry : hashMap.entrySet()) {
				entry.getValue().get(0).isHeader=true;
				infos.addAll(entry.getValue());
			}
			listView.setAdapter(new EventAdapter(getApplicationContext(), infos));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPullRefreshListView.onRefreshComplete();
		dismissProgressBar();
		
		AlertManager.showCalanderAlert(this);
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
			EvetntInfo info = (EvetntInfo)parent.getItemAtPosition(position);
			//loadDefaultMap(info);
			Intent intent = new Intent(getApplicationContext(), EventDetailActivity.class);
			intent.putExtra("EVENT", info);
			startActivity(intent);
		}
	}; 
	
	private void loadDefaultMap(EvetntInfo info){
		String url= " http://maps.google.com/maps?z=12&t=m&g=loc:"+info.Latitude+"+"+info.Longitude;
		Intent intent = new Intent(getApplicationContext(), WebActivity.class);
		intent.putExtra("URL", url);
		intent.putExtra("TITLE", info.Title);
		startActivity(intent);
	}
	
	
	private class MInfoWindowAdapter implements com.google.android.gms.maps.GoogleMap.InfoWindowAdapter{
		@Override
		public View getInfoContents(Marker arg0) {
			View view = getLayoutInflater().inflate(R.layout.map_popup_event, null);
			TextView tv = (TextView)view.findViewById(R.id.tv_val);
			tv.setText(arg0.getTitle());
			return view;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			return null;
		}
	}
	
	
	public void setEventsOnCal(){
		if(allowCalendar){
			try {
				List<CalenderEvent>  calenderEvents = CalendarUtility.readCalendarEvent(getApplicationContext());
					for (EvetntInfo evetntInfo : infos) {
						boolean isEventFound = false;
						for (CalenderEvent calenderEvent : calenderEvents) {
							if(evetntInfo.Title.equals(calenderEvent.nameOfEvent)){
								isEventFound = true;
							}
					}
					
					if(!isEventFound){
						Calendar cal = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date dt = sdf.parse(evetntInfo.Date);
							cal.setTime(dt);
							CalendarUtility.addEvent(getApplicationContext(), evetntInfo.Title, cal);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
