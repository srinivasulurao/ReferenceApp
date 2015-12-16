package com.youflik.youflik.chat;

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

public class ChatNMessagesFragment extends Fragment{

	private FragmentTabHost tabhost;
	private ViewPager pager;
	private ChatTabHostAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_chatnmessages, container, false);
		tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		tabhost.setup(getActivity(),getChildFragmentManager(),android.R.id.tabcontent);

		tabhost.addTab(tabhost.newTabSpec("Inbox").setIndicator("Inbox"),ChatFragmentInbox.class,null);
		tabhost.addTab(tabhost.newTabSpec("Online").setIndicator("Online"),ChatFragmentOnline.class,null);
		tabhost.addTab(tabhost.newTabSpec("Others").setIndicator("Others"),ChatFragmentOthers.class,null);

		pager = (ViewPager) view.findViewById(R.id.chatviewpager);
		adapter = new ChatTabHostAdapter(getChildFragmentManager());
		//pager.setOffscreenPageLimit(3);
		pager.setAdapter(adapter);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

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


	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			 Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
			 fragment.onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
		Fragment fragment = new ChatFragmentInbox();
		fragment.onActivityResult(requestCode, resultCode, data);
	}*/


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Fragment fragment = new ChatFragmentInbox();
		fragment.onActivityResult(requestCode, resultCode, data);
	}
}


