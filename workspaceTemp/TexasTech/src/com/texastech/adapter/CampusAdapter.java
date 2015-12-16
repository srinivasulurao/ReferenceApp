package com.texastech.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.texastech.app.MyApplication;
import com.texastech.app.R;
import com.texastech.bean.GetCampusMapsInfo.CampusMapsInfo;

public class CampusAdapter extends MyAdapter{
	
	private ImageLoader imageLoader;
	
	public CampusAdapter(Context context, List list) {
		super(context, list);
		imageLoader = MyApplication.getLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view==null){
			view = inflater.inflate(R.layout.row_campus_list, null);
		}
		
		
		TextView tvTitle =(TextView)view.findViewById(R.id.tv_title);
		TextView tvCall=(TextView)view.findViewById(R.id.tv_call);
		TextView tvEmail=(TextView)view.findViewById(R.id.tv_email);
		//ImageView ivLogo = (ImageView)view.findViewById(R.id.place_holder);
		CampusMapsInfo info =(CampusMapsInfo)getItem(position);
		tvTitle.setText(info.map_title);
		tvCall.setText(info.call_us);
		tvEmail.setText(info.email_id);
		 
		//imageLoader.displayImage(HttpConst.IMAGE_URL+info.Icon, ivLogo, AppUtil.getImageSetting());
		return view;
	}

}
