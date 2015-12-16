package com.youflik.youflik.userprofile;

import com.youflik.youflik.R;
import com.youflik.youflik.utils.Util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;
import android.view.Menu;

public class UserDetailActivity extends ActionBarActivity  implements
ActionBar.TabListener { 

	private ViewPager viewPager;
	private UserDetailTabAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { Util.FIRSTNAME, "Photos", "Videos" ,"Friends"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		viewPager = (ViewPager) findViewById(R.id.user_pager);
		actionBar = getSupportActionBar();	
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		//getSupportActionBar().hide();
		mAdapter = new UserDetailTabAdapter(getSupportFragmentManager());
		viewPager.setOffscreenPageLimit(4);
		viewPager.setAdapter(mAdapter);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#40be7f")));

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
		//actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	@Override
	public void onTabReselected(Tab arg0,
			FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab arg0,
			FragmentTransaction arg1) {
		// TODO Auto-generated method stub

		viewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0,
			FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.main_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void finish() {
		if(Util.mediaPlayer.isPlaying()){
			Util.mediaPlayer.reset();
		}else {
			Util.mediaPlayer.reset();
		}
		super.finish();
		overridePendingTransition(R.anim.push_down_in_reverse,R.anim.push_down_out_reverse);

	}
	@Override
	public void onPause() {
		if(Util.mediaPlayer.isPlaying()){
			Util.mediaPlayer.pause();
			FragmentProfileView.song.setSelected(false);
			FragmentProfileView.song.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_play_song, 0, 0, 0);
		}else {

		}
		super.onPause(); 
	}

}
