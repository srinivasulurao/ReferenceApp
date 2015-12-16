package com.youflik.youflik.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.OnTabChangeListener;
import com.youflik.youflik.R;

public class NotificationsFragment extends Fragment {

	private FragmentTabHost tabhost;
	private ViewPager pager;
	private NotificationTabhostAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_notifications, container, false);
		tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		tabhost.setup(getActivity(),getChildFragmentManager(),android.R.id.tabcontent);

		tabhost.addTab(tabhost.newTabSpec("General").setIndicator("General"),NotificationGeneral.class,null);
		tabhost.addTab(tabhost.newTabSpec("Friend Requests").setIndicator("Friend Requests"),NotificationFriendRequest.class,null);


		pager = (ViewPager) view.findViewById(R.id.viewpager2);
		adapter = new NotificationTabhostAdapter(getChildFragmentManager());
		//pager.setOffscreenPageLimit(2);
		pager.setAdapter(adapter);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		/*
		adapter = new NotificationTabhostAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);*/
		tabhost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabId) {
				pager.setCurrentItem(tabhost.getCurrentTab());

			}

		});
		pager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				tabhost.setCurrentTab(position);
			}

		});
	}

}
