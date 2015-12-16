package com.texastech.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.texastech.adapter.NavListAdapter;
import com.texastech.bean.GetHomeScreenInfo.HomeScreenInfo;
import com.texastech.helper.AppUtil;
import com.texastech.httputil.HttpConst;

public class MenuFragment extends Fragment{

	@InjectView(R.id.title_menu)
	TextView tvTitle;
	
	public static String TAG="MenuFragment";
	
	public static MenuFragment fragment;
	
	private static HomeActivity activity;
	
	private static List<HomeScreenInfo> screenInfos;
	
	@InjectView(R.id.gridViewCustom)
	GridView gridView;
	
	
	@Override
	public void onAttach(Activity _activity) {
		super.onAttach(_activity);
		activity = (HomeActivity)_activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static MenuFragment instance(List<HomeScreenInfo> _screenInfos){
		screenInfos = new ArrayList<HomeScreenInfo>();
		for (HomeScreenInfo homeScreenInfo : _screenInfos) {
			if(!homeScreenInfo.TypeID.equals("10") && !homeScreenInfo.TypeID.equals("11")){
				screenInfos.add(homeScreenInfo);
			}
		}
		//screenInfos = _screenInfos;
		return new MenuFragment();
	}
	
	 @Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_menu, null);
		ButterKnife.inject(this,view);
		
		tvTitle.setTextColor(Color.parseColor(activity.settingPre.getTitleColor()));
		ImageView ivHeaderBack = (ImageView)view.findViewById(R.id.header_background_menu_layout);
		MyApplication.getLoader().displayImage(HttpConst.IMAGE_URL+activity.settingPre.getTitleBarUrl(), ivHeaderBack, AppUtil.getImageSetting());
		
		gridView.setAdapter(new NavListAdapter(activity,screenInfos,R.layout.row_grid_view));
		gridView.setOnItemClickListener(activity.navListener);
		return view;
	}
	 
	 
	 @OnClick(R.id.btn_menu)
	 public void onMenuClick(View view){
		 activity.openDrawer();
	 }
	 
	 
	 
	 
	  
	@Override 
	public void onDestroyView() {
	   super.onDestroyView();
	    ButterKnife.reset(this);
	}
	
	/*@OnClick({R.id.tv_campuse_directory,R.id.tv_event,R.id.tv_faculty_directory,R.id.tv_mental_resources,R.id.tv_campuse,R.id.tv_social_media,R.id.tv_student_health,R.id.tv_virtual_tours})
	public void homeButtonEvent(View view){
		String title = ((TextView)view).getText().toString();
		
		switch (view.getId()) {
			case R.id.tv_campuse_directory:
				activity.pushMenuActivity(DirectoryActivity.class, title);
			break;
			//------------------//
			case R.id.tv_event:
				activity.pushMenuActivity(EventsActivity.class, title);
			break;
			//------------------//
			case R.id.tv_faculty_directory:
				activity.pushMenuActivity(DirectoryActivity.class, title);
			break;
			//------------------//
			case R.id.tv_mental_resources:
				activity.pushMenuActivity(MentalHealthResourcesActivity.class, title);
			break;
			//------------------//
			case R.id.tv_campuse:
				activity.pushMenuActivity(CampusesActivity.class, title);
			break;
			//------------------//
			case R.id.tv_social_media:
				activity.pushMenuActivity(SocialMediaActivity.class, title);
			break;
			//------------------//
			case R.id.tv_student_health:
				activity.pushMenuActivity(StudentResourcesListActivity.class, title);
			break;
			//------------------//
			case R.id.tv_virtual_tours:
				activity.pushMenuActivity(VirtualToursActivity.class, title);
			break;
			//------------------//
		}
	}*/
	
}
