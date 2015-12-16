package com.youflik.youflik.thirdPartyProfileView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.youflik.youflik.userprofile.FragmentProfileView;
import com.youflik.youflik.userprofile.FragmentUserFriends;
import com.youflik.youflik.userprofile.FragmentUserPhotos;
import com.youflik.youflik.userprofile.FragmentUserVideos;

public class ThirdPartyUserDetailTabAdapter extends FragmentPagerAdapter {
	public ThirdPartyUserDetailTabAdapter(FragmentManager fm) {
		super(fm);
	}
	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new ThirdPartyFragmentProfileView();
		case 1:
			return new ThirdPartyFragmentUserPhotos();
		case 2:
			return new ThirdPartyFragmentUserVideos();
		case 3:
			return new ThirdPartyFragmentUserFriends();
		}
		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 4;
	}
}
