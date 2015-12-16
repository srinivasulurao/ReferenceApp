package com.youflik.youflik.notification;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class NotificationTabhostAdapter extends FragmentPagerAdapter{

	private String titles[] = {"General","Friend Requests"};
	public NotificationTabhostAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		
		switch(position)
		{
		case 0:
			return new NotificationGeneral();
		case 1:
			return new NotificationFriendRequest();
	
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
		return 2;
	}

}
