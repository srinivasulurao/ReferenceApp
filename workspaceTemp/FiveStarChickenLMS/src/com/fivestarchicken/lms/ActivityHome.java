package com.fivestarchicken.lms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.RadioGroup.LayoutParams;

import com.fivestarchicken.lms.adapter.NavDrawerListAdapter;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.fragments.FragmentDashboard;
import com.fivestarchicken.lms.fragments.FragmentProfile;
import com.fivestarchicken.lms.fragments.FragmentTakeExam;
import com.fivestarchicken.lms.fragments.FragmentViewResult;
import com.fivestarchicken.lms.model.NavDrawerItem;
import com.fivestarchicken.lms.sync.SyncReceiver;
import com.fivestarchicken.lms.utils.Commons;

@SuppressLint("NewApi")
public class ActivityHome extends ActionBarActivity implements
		FragmentDashboard.DashboardSelectListener {

	private ActionBar actionbar;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private ArrayList<NavDrawerItem> navDrawerItems;
	NavDrawerItem navDrawerItem;
	private NavDrawerListAdapter adapter;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	AlarmManager alarmManager;
	String languageType;
	int selectedMenu;
	Locale myLocale;
	SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		actionbar = getSupportActionBar();

		/*
		 * actionbar.setDisplayHomeAsUpEnabled(true);
		 * actionbar.setHomeButtonEnabled(true);
		 */
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);

		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		// initDrawerLayout();
		//displayView(0);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityHome.this);
		 languageType = sharedPreferences.getString("languageType", null);
		 if (languageType.equals("1")) {
				myLocale = Locale.ENGLISH;
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

				// myLocale = new Locale(Locale.ENGLISH);
			} else if (languageType.equals("2")) {

				myLocale = new Locale("kn");
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

			} else if (languageType .equals("3")) {

				myLocale = new Locale("te");
				Locale.setDefault(myLocale);
				android.content.res.Configuration config = new android.content.res.Configuration();
				config.locale = myLocale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());

			}

		navDrawerItems = new ArrayList<NavDrawerItem>();
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		selectedMenu = 0;
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems, selectedMenu);
		mDrawerList.setAdapter(adapter);
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		displayView(0);
		adapter.setSelectedIndex(0);
		adapter.notifyDataSetChanged();
		// startSyncServices();

	}

	protected void onPause() {
		super.onPause();

		// stopAlertService(getApplicationContext());

	}

	void startSyncServices() {
		DbAdapter dh;

		dh = new DbAdapter(ActivityHome.this);

		String lastSyncTime = dh.getLastSynctime(Commons.resultModule);

		// BroadCase Receiver Intent Object
		Intent alarmIntent = new Intent(getApplicationContext(),
				SyncReceiver.class);
		// Pending Intent Object
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Alarm Manager Object
		AlarmManager alarmManager = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		// Alarm Manager calls BroadCast for every Ten seconds (10 * 1000),
		// BroadCase further calls service to check if new records are inserted
		// in
		// Remote MySQL DB
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 15 * (60 * 1000), pendingIntent);

	}

	@Override
	public void onDashBoardSelect(String type) {

		if (type.equals(Commons.PROFILE)) {
			adapter.setSelectedIndex(1);

		} else if (type.equals(Commons.TAKE_EXAM)) {

			adapter.setSelectedIndex(2);
		} else if (type.equals(Commons.VIEW_RESULT)) {

			adapter.setSelectedIndex(3);
		}else{
			adapter.setSelectedIndex(-1);
			
		}

		adapter.notifyDataSetChanged();
	}

	void initDrawerLayout() {

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(ActivityHome.this,
				mDrawerLayout, R.drawable.ic_drawer,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			public void onDrawerClosed(View view) {

				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {

				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));

		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems, selectedMenu);
		mDrawerList.setAdapter(adapter);
	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			selectedMenu = position;
			adapter.setSelectedIndex(selectedMenu);

			adapter.notifyDataSetChanged();
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new FragmentDashboard();
			break;
		case 1:
			fragment = new FragmentProfile();
			break;
		case 2:

			Intent i = new Intent(ActivityHome.this, ActivityCarrierGraph.class);// ActivityExamModule

			startActivity(i);
			// fragment = new FragmentTakeExam();
			break;
		case 3:
			fragment = new FragmentViewResult();
			break;
		case 4:

			SharedPreferences.Editor editor = sharedPreferences.edit();

			editor.remove("userId");
			editor.commit();
			Intent in = new Intent();
			in.setClass(ActivityHome.this, ActivityEmployeeLogin.class);
			startActivity(in);

			break;
		case 5:
			// fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			/*
			 * mDrawerList.setItemChecked(position, true);
			 * mDrawerList.setSelection(position);
			 * setTitle(navMenuTitles[position]);
			 * mDrawerLayout.closeDrawer(mDrawerList);
			 */
		} else {
			// error in creating fragment
			// Log.e("MainActivity", "Error in creating fragment");
			// mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * @Override public boolean onPrepareOptionsMenu(Menu menu) { // if nav
	 * drawer is opened, hide the action items boolean drawerOpen =
	 * mDrawerLayout.isDrawerOpen(mDrawerList);
	 * menu.findItem(R.id.action_settings).setVisible(!drawerOpen); return
	 * super.onPrepareOptionsMenu(menu); }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_home, menu);
		return true;
	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); // Pass any configuration change
	 * to the drawer toggls mDrawerToggle.onConfigurationChanged(newConfig); }
	 * 
	 * @Override protected void onPostCreate(Bundle savedInstanceState) {
	 * super.onPostCreate(savedInstanceState); // Sync the toggle state after
	 * onRestoreInstanceState has occurred. mDrawerToggle.syncState(); }
	 */
	public void onBackPressed() {

		exitFromView();

	}

	private void exitFromView() {

		new AlertDialog.Builder(this)
				.setTitle("Five Star Chicken LMS")
				.setMessage("Do you want to exit?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										getApplicationContext(),
										MainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra("EXIT", true);
								startActivity(intent);
								stopAlertService(getApplicationContext());
								finish();
							}

						}).setNegativeButton("No", null).show();
	}

	public void stopAlertService(Context context) {

		Intent alarmIntent = new Intent(getApplicationContext(),
				SyncReceiver.class);
		// Pending Intent Object
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, alarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Alarm Manager Object
		AlarmManager alarmManager = (AlarmManager) getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(pendingIntent);
	}
}
