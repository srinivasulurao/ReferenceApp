package com.texastech.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.texastech.app.MyApplication;
import com.texastech.app.R;
import com.texastech.bean.GetHomeScreenInfo.HomeScreenInfo;
import com.texastech.helper.AppUtil;
import com.texastech.httputil.HttpConst;

public class NavListAdapter extends MyAdapter{

	List<HomeScreenInfo> menus;
	
	private int layout;
	
	public NavListAdapter(Context context,List<HomeScreenInfo> _menus, int _layout) {
		super(context, _menus);
		menus = _menus;
		layout = _layout;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view==null){
			view = inflater.inflate(layout, null);
		}
		
		HomeScreenInfo screenInfo = (HomeScreenInfo)getItem(position);
		
		TextView tvLable =  (TextView)view.findViewById(R.id.tv_lable);
		tvLable.setText(screenInfo.Title);
		
		ImageView ivIcon = (ImageView)view.findViewById(R.id.iv_icon);
		MyApplication.getLoader().displayImage(HttpConst.IMAGE_URL+screenInfo.IconIphone, ivIcon, AppUtil.getImageSetting());
		 
		return view;
	}
}
