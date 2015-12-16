package com.youflik.youflik;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.youflik.youflik.chat.ChatNMessagesFragment;
import com.youflik.youflik.commonAdapters.NavDrawerListAdapter;
import com.youflik.youflik.fragments.SearchFragment;
import com.youflik.youflik.internet.ConnectionDetector;
import com.youflik.youflik.models.NavDrawerItem;
import com.youflik.youflik.notification.NotificationsFragment;
import com.youflik.youflik.settings.SettingsFragment;
import com.youflik.youflik.statusUpdate.UserStatusUpdate;
import com.youflik.youflik.timefeed.TimefeedFragment;
import com.youflik.youflik.userprofile.UserDetailActivity;
import com.youflik.youflik.utils.Util;


public class MainActivity extends ActionBarActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private View header;
	private TextView nav_user_name,nav_menu_name;
	public static ImageView nav_user_profile_path,chat;
	public static String Flag_BackPress="Timefeed";
	private float lastTranslate = 0.0f;// for sliding......................................
	private FrameLayout frame;// for sliding......................................
	private RelativeLayout bottom_menu;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageButton post;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.showImageForEmptyUri(R.drawable.placeholder) 
		.showImageOnFail(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources()	.obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		chat=(ImageView)findViewById(R.id.feed_chat);
		header = View.inflate(this, R.layout.header, null);
		nav_user_name =  (TextView) header.findViewById(R.id.nav_user_name);
		nav_user_profile_path =(ImageView)header.findViewById(R.id.image_header);

		//SETTING OF PROFILE PICTURE PATH
		imageLoader.displayImage(Util.NM_PROFILE_PIC, nav_user_profile_path, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current,
					int total) {

			}
		}
				);
		//END 


		nav_user_name.setText(Util.FIRSTNAME);
		bottom_menu=(RelativeLayout)findViewById(R.id.buttom_buttons_layout);
		navDrawerItems = new ArrayList<NavDrawerItem>();
		frame = (FrameLayout) findViewById(R.id.frame_container);// for sliding......................................
		// adding nav drawer items to array
		// Search
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Timefeed
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Notifications
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Messages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		/*// Discover Title
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], true));
		// People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));*/
		// Other Title
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], true));
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(5, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.addHeaderView(header);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.custom_action_bar_main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		// CUSTOM ACTIONBAR
		nav_menu_name=(TextView)findViewById(R.id.menu_text);
		post=(ImageButton)findViewById(R.id.status_update_ab);

		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,UserStatusUpdate.class);
				intent.putExtra("StatusUpdateType", "User");
				startActivity(intent);	
				MainActivity.this.overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			}
		});
		// END CUSTOM ACTION BAR

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.drawer_open, // nav drawer open - description for accessibility
				R.string.drawer_close // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			///// for sliding......................................
			@SuppressLint("NewApi")
			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				// getSupportActionBar().hide();
				float moveFactor = (mDrawerList.getWidth() * slideOffset);
				InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) 
				{
					frame.setTranslationX(moveFactor);
					bottom_menu.setTranslationX(moveFactor);
				}
				else
				{
					TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
					anim.setDuration(0);
					anim.setFillAfter(true);
					frame.startAnimation(anim);
					bottom_menu.startAnimation(anim);
					lastTranslate = moveFactor;
				}
			}
			///// for sliding......................................end......................................... 
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(2);
		}

		chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// replace messages fragment		
			}
		});

	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}
	// ONCLICK OF mDrawerList opening of nav drawer

	@SuppressLint("InlinedApi") @Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
	}
	/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//	menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			//Goto UserProfile
			mDrawerLayout.closeDrawer(mDrawerList);
			Intent user_profile= new Intent(MainActivity.this,UserDetailActivity.class);
			startActivity(user_profile);
			MainActivity.this.overridePendingTransition(R.anim.push_down_in,R.anim.push_down_out);
			break;
		case 1:
			fragment = new SearchFragment();
			setTitle(navMenuTitles[position-1]);
			Flag_BackPress="Search";
			chat.setVisibility(View.GONE);
			//	filter.setVisibility(View.GONE);
			break;
		case 2:
			fragment = new TimefeedFragment();
			setTitle(navMenuTitles[position-1]);
			Flag_BackPress="Timefeed";
			chat.setVisibility(View.GONE);
			//filter.setVisibility(View.VISIBLE);
			break;
		case 3:
			fragment = new NotificationsFragment();
			setTitle(navMenuTitles[position-1]);
			Flag_BackPress="Notification";
			chat.setVisibility(View.GONE);
			//filter.setVisibility(View.GONE);
			break;
		case 4:
			fragment = new ChatNMessagesFragment();
			setTitle(navMenuTitles[position-1]);
			Flag_BackPress="Message";
			chat.setVisibility(View.GONE);
			break;
		case 5:
			break;
		case 6:
			fragment = new SettingsFragment();
			setTitle(navMenuTitles[position-1]);
			Flag_BackPress="Settings";
			chat.setVisibility(View.GONE);
			break;
		default:
			break;
		}

		if (fragment != null) {

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			//	setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		//getSupportActionBar().setTitle(mTitle);
		nav_menu_name.setText(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {

		if(Flag_BackPress.equalsIgnoreCase("Timefeed")){
			AlertDialog alert_back = new AlertDialog.Builder(this).create();
			alert_back.setTitle("Exit?");
			alert_back.setMessage("Are you sure want to exit?");

			alert_back.setButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alert_back.setButton2("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			});
			alert_back.show();

		}

		else{
			displayView(2);
		}
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
		fragment.onActivityResult(requestCode, resultCode, data);
		//super.onActivityResult(requestCode, resultCode, data);
	}*/
}
