/***
 * Copyright (c) 2011 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.aotd.helpers;

import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aotd.activities.DispatchPastTabActivity;
import com.aotd.activities.R;
import com.aotd.dialog.AlertDialogMsg;
import com.aotd.parsers.DriverCurrentLocationParser;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CustomBalloonOverlayView<Item extends OverlayItem> extends BalloonOverlayView<CustomOverlayItem> {

	private TextView title;
	private TextView snippet;
	ProgressDialog progress;
	String drivername;
	Context context;
	public CustomBalloonOverlayView(Context context, int balloonBottomOffset) {		
		super(context, balloonBottomOffset);
		this.context = context;
	}
	
	@Override
	protected void setupView(Context context, final ViewGroup parent) {
		
		// inflate our custom layout into parent
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_overlay_example2, parent);
		
		// setup our fields
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);

	}

	@Override
	protected void setBalloonData(CustomOverlayItem item, ViewGroup parent) {
		
		// map our custom item data to fields
		title.setText("");
		drivername =item.getTitle(); 
		snippet.setText("");
//		Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault()); 
//		String result = null; 

//		
//		try {  
//			List<Address> list = geocoder.getFromLocation(Double.parseDouble(item.latitude), Double.parseDouble(item.longitude), 1);
//			if (list != null && list.size() > 0) {  
//				Address address = list.get(0);
//				// sending back first address line and locality  
//				result = address.getAddressLine(0);
//				snippet.setText(result);
//				
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			snippet.setText("Not Found");
//		}
		
		progress = ProgressDialog.show(context, "", "please wait");
		
		String url = String.format("http://maps.google.com/maps/geo?q=%s,%s&output=xml", item.latitude,item.longitude);
		
		DriverCurrentLocationParser mDriverCurrentLocationParser = new DriverCurrentLocationParser(url,new LocationHandler());
		mDriverCurrentLocationParser.start();		
		
		
	}

	class LocationHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {		
			
			progress.dismiss();
			title.setText(drivername);
			String errorMsg=msg.getData().getString("HttpError");
			if(errorMsg.length()>0){
				alertDialogWithMsg("AOTD",errorMsg);	
				snippet.setText("Not Found");
			}else{				
				String cityname=msg.getData().getString("cityname");				
				snippet.setText(cityname);
				}
					
			
			
			
		}
		
	}

	public void alertDialogWithMsg(String title, String msg){	
	 
		new AlertDialogMsg(context, title, msg ).setPositiveButton("ok", new android.content.DialogInterface.OnClickListener(){
	
			@Override
			public void onClick(DialogInterface dialog, int which){
				
						
			}
			
		 }).create().show();		
}
}
