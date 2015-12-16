package com.texastech.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.texastech.app.MyApplication;
import com.texastech.app.R;
import com.texastech.bean.GetEvetntInfo.EvetntInfo;
import com.texastech.helper.AppUtil;
import com.texastech.helper.DateTimeUtil;
import com.texastech.httputil.HttpConst;

public class EventAdapter extends MyAdapter{
	
	private ImageLoader imageLoader;
	
	
	
	public EventAdapter(Context context, List list) {
		super(context, list);
		imageLoader = MyApplication.getLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view==null){
			view = inflater.inflate(R.layout.row_event_list, null);
		}
		
		
		TextView tvTitle =(TextView)view.findViewById(R.id.tv_title);
		TextView tvDescription=(TextView)view.findViewById(R.id.tv_description);
		TextView tvDate=(TextView)view.findViewById(R.id.tv_date);
		ImageView ivLogo = (ImageView)view.findViewById(R.id.place_holder);
		
		EvetntInfo info =(EvetntInfo)getItem(position);
		tvTitle.setText(info.Title);
		tvDescription.setText(info.Description);
		tvDate.setText(DateTimeUtil.getDateFormat(info.Date));
		
		TextView tvHeader = (TextView)view.findViewById(R.id.tv_date_header);
		if(info.isHeader){
			tvHeader.setVisibility(View.VISIBLE);
			tvHeader.setText(DateTimeUtil.getDateFormat(info.Date));
		}else{
			tvHeader.setVisibility(View.GONE);
		}
		
		imageLoader.displayImage(HttpConst.IMAGE_URL+info.Icon, ivLogo, AppUtil.getImageSetting());
		return view;
	}

}
