package com.youflik.youflik.chat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ChatTabHostAdapter extends FragmentPagerAdapter{

	private String titles[] = {"Inbox","Online","Others"};

	public ChatTabHostAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub

		switch(position)
		{
		case 0:
			return new ChatFragmentInbox();
		case 1:
			return new ChatFragmentOnline();
		case 2:
			return new ChatFragmentOthers();
		default:
			return null;
		}

	}
	@Override
	public CharSequence getPageTitle(int position)
	{
		return titles[position];
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
