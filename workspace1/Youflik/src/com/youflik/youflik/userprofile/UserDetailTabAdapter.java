package com.youflik.youflik.userprofile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class UserDetailTabAdapter extends FragmentPagerAdapter {
	public UserDetailTabAdapter(FragmentManager fm) {
		super(fm);
	}
	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new FragmentProfileView();
		case 1:
			return new FragmentUserPhotos();
		case 2:
			return new FragmentUserVideos();
		case 3:
			return new FragmentUserFriends();
		}
		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 4;
	}
}
