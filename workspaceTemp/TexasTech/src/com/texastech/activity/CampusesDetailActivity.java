package com.texastech.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.texastech.app.BaseActivity;
import com.texastech.app.R;
import com.texastech.app.WebActivity;
import com.texastech.bean.GetCampusMapsInfo.CampusMapsInfo;
import com.texastech.httputil.HttpConst;

public class CampusesDetailActivity extends BaseActivity{
	 
	private CampusMapsInfo info;
	
	private GoogleMap map;
	
	@InjectView(R.id.browse)
	TextView btnBrowse;
	
	@InjectView(R.id.map_link)
	TextView btnLink;
	
	@InjectView(R.id.tv_detail)
	TextView tvDetail;
	
	
	@Override
	public void setTitle(TextView tv) {
		info = (CampusMapsInfo)getIntent().getSerializableExtra("INFO");
		tv.setText(info.map_title);
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_campus_detail);
	}

	@Override
	protected void initXmlView() {
		if(map!=null){
			map.addMarker(new MarkerOptions()
			.position(new LatLng(Double.parseDouble(info.latitude), Double.parseDouble(info.longitude)))
			.title(info.Campus_Name)
			.snippet(info.directions)
			.icon(BitmapDescriptorFactory
			.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(info.latitude), Double.parseDouble(info.longitude)), 14));
		}
		
		String detail = "Campus Name: "+info.Campus_Name+"\n\n"+
						"Call: "+info.call_us+"\n\n"+
						"Email: "+info.email_id+"\n\n"+
						"Direction: "+info.directions;
		
		tvDetail.setText(detail);
	}

	  
	@OnClick(R.id.browse)
	public void browseEvent(View view){
		viewAnimation(view);
		
		Intent intent = new Intent(getApplicationContext(), WebActivity.class);
		intent.putExtra("URL", info.map_link);
		intent.putExtra("TITLE", info.Campus_Name);
		startActivity(intent);
	}
	
	
	@OnClick(R.id.map_link)
	public void mapLinkEvent(View view){
		viewAnimation(view);
		
		Intent intent = new Intent(getApplicationContext(), WebActivity.class);
		intent.putExtra("URL", HttpConst.IMAGE_URL.concat(info.map_image));
		intent.putExtra("TITLE", info.Campus_Name);
		startActivity(intent);
	}

}
