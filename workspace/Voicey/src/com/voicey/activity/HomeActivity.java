package com.voicey.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.voicey.adapter.CategoryAdapter;
import com.voicey.adapter.NavDrawerListAdapter;
import com.voicey.fragment.ChatFriendFragment;
import com.voicey.fragment.FriendListFragment;
import com.voicey.fragment.InboxFragment;
import com.voicey.fragment.ListFragment;
import com.voicey.fragment.UserEditFragment;
import com.voicey.model.AudioInfo;
import com.voicey.model.Category;
import com.voicey.model.Friend;
import com.voicey.model.MenuObject;
import com.voicey.model.VoiceyReply;
import com.voicey.utils.Constants;
import com.voicey.webservices.Webservices;

public class HomeActivity extends FragmentActivity implements
		EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private TypedArray navMenuIcons;
	private String[] navMenuTitles;
	private ArrayList<MenuObject> navDrawerItems;
	private CharSequence mTitle;
	private NavDrawerListAdapter adapter;
	Dialog alertDialog;
	private Boolean isActivePopup;
	private Spinner spCategory;
	List<Friend> shareFriendList = new ArrayList<Friend>();
	List<Friend> shareGroupList = new ArrayList<Friend>();
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	private Handler mHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	VoiceyReply voiceyReply;
	private SeekBar songProgressBar;
	private String outputFile = null, categoryId;
	private MediaRecorder myAudioRecorder;
	RelativeLayout rrvoicey, rrinbox, rrfriends, rrmute, rrchat;
	View vlfriends, vlinbox, vlvoicey, vlchats;
	String audio_file_name = "";
	MediaPlayer m;
	String userCode, userId, userName, userPhone, userEmail, popupControl,
			shareCount, requestCount, navigationValue, videoUrl;
	ImageView ivstop, ivplay, ivAddPhoto, ivaddimage;
	ImageButton ivstart;
	TextView tvTimmer, tvComment, tvmaxTime, tvcancel, tvsave, tvUserId, cam,
			gal, close;
	EditText etTitle, etMood;
	ListView lvFriendList;
	List<Friend> friendList;
	TextView tvRequestCount;
	TextView tvShareCount;
	RelativeLayout llPublic;
	RelativeLayout llUser;
	ImageView ivMaximize, ivMinimize, ivClose;
	ToggleButton tgmakepublic, tguser;
	Bitmap imagebitmap;
	SharedPreferences sharedPreferences;
	Webservices Webservices = new Webservices();
	private AudioInfo audioInfo;
	private final static int ACTIVITY_TAKE_PHOTO = 1;
	private static int RESULT_LOAD_IMAGE = 2;
	private static int RESULT_LOAD_VIDEO = 5;
	Gson gson;
	List<String> selItemList;
	byte[] imageByte;
	final int PIC_CROP = 4044;

	View grpview;
	LayoutInflater li;
	Dialog releaseNote;
	// Activity request codes
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static int LOAD_IMAGE = 10;
	ImageView propic;
	String[] codes;
	String possibleEmail, mPhoneNumber, encodedImageString;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		initilizeUI();

		Bundle bundle = getIntent().getExtras();
		navigationValue = bundle.getString("Navigation");

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(HomeActivity.this);
		userName = sharedPreferences.getString("userName", null);

		if (userName == null) {
			displayAddUser();
			//
		} else {

			if (savedInstanceState == null) {
				if (navigationValue.equals("Request")) {

					vlvoicey.setVisibility(View.GONE);
					vlinbox.setVisibility(View.GONE);
					vlfriends.setVisibility(View.VISIBLE);
					vlchats.setVisibility(View.GONE);

					displayView(3, "requests");

				} else if (navigationValue.equals("inbox")) {

					// displayView(0, "inbox");

					vlvoicey.setVisibility(View.GONE);
					vlinbox.setVisibility(View.VISIBLE);
					vlfriends.setVisibility(View.GONE);
					vlchats.setVisibility(View.GONE);

					Fragment fragment = null;
					fragment = new InboxFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();

				} else if (navigationValue.equals("accept")) {

					vlvoicey.setVisibility(View.GONE);
					vlinbox.setVisibility(View.GONE);
					vlfriends.setVisibility(View.VISIBLE);
					vlchats.setVisibility(View.GONE);

					displayView(3, "normal");
				} else {
					vlvoicey.setVisibility(View.GONE);
					vlinbox.setVisibility(View.VISIBLE);
					vlfriends.setVisibility(View.GONE);
					vlchats.setVisibility(View.GONE);

					Fragment fragment = null;
					fragment = new InboxFragment();

					// fragment = new fragTest();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
					// on first time display view for first nav item
					// displayView(0, "normal");
					/*
					 * rrvoicey.setBackgroundResource(R.color.blue);
					 * rrinbox.setBackgroundResource(R.color.gray);
					 * rrfriends.setBackgroundResource(R.color.gray);
					 */
				}
			}
		}

	}

	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		codes = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < codes.length; i++) {
			String[] g = codes[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = codes[i];
				break;
			}
		}
		return CountryZipCode;
	}

	@SuppressLint("NewApi")
	private void initilizeUI() {

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(HomeActivity.this);
		userCode = sharedPreferences.getString("userCode", null);
		userId = sharedPreferences.getString("userId", null);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		rrvoicey = (RelativeLayout) findViewById(R.id.rrvoicey);
		rrinbox = (RelativeLayout) findViewById(R.id.rrinbox);
		rrfriends = (RelativeLayout) findViewById(R.id.rrfriends);
		rrmute = (RelativeLayout) findViewById(R.id.rrmute);

		rrchat = (RelativeLayout) findViewById(R.id.rrchat);
		ivaddimage = (ImageView) findViewById(R.id.ivaddimage);

		vlvoicey = (View) findViewById(R.id.vlvoicey);
		vlinbox = (View) findViewById(R.id.vlinbox);
		vlfriends = (View) findViewById(R.id.vlfriends);
		vlchats = (View) findViewById(R.id.vlchats);

		navDrawerItems = new ArrayList<MenuObject>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new MenuObject(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new MenuObject(navMenuTitles[1], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new MenuObject(navMenuTitles[2] + userCode,
				navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new MenuObject(navMenuTitles[3], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new MenuObject(navMenuTitles[4], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new MenuObject(navMenuTitles[5], navMenuIcons
				.getResourceId(0, -1)));

		// Find People

		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button

		/*
		 * getActionBar().setDisplayShowTitleEnabled(false);
		 * getActionBar().setDisplayShowCustomEnabled(true);
		 */
		// LayoutInflater li = LayoutInflater.from(this);

		// View customView = li.inflate(R.layout.action_bar, null);
		// ab.setCustomView(customView);

		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.action_bar, null);
		ActionBar actionBar = getActionBar();

		getActionBar().setCustomView(actionBarLayout);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// getActionBar().setCustomView(R.layout.action_bar);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setIcon(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));

		shareCount = sharedPreferences.getString("sharecount", null);
		requestCount = sharedPreferences.getString("friendRequest", null);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// getActionBar().setDisplayShowHomeEnabled(false);
		isActivePopup = true;
		ImageView ibItem1 = (ImageView) actionBarLayout
				.findViewById(R.id.actionBarLogo);

		tvRequestCount = (TextView) actionBarLayout
				.findViewById(R.id.tvrequestcount);
		tvShareCount = (TextView) actionBarLayout
				.findViewById(R.id.tvsharecount);

		Integer shareInt = new Integer(shareCount);
		Integer rquestInt = new Integer(requestCount);
		if (shareInt > 0) {
			tvShareCount.setText(shareCount);
		} else {
			tvShareCount.setVisibility(View.GONE);

		}

		if (rquestInt > 0) {
			tvRequestCount.setText(requestCount);
		} else {
			tvRequestCount.setVisibility(View.GONE);

		}

		tvRequestCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				displayView(3, "requests");

				tvRequestCount.setVisibility(View.GONE);
			}
		});

		tvShareCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				tvShareCount.setVisibility(View.GONE);
				displayView(0, "inbox");
			}
		});

		rrvoicey.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*
				 * rrvoicey.setBackgroundResource(R.color.blue);
				 * rrinbox.setBackgroundResource(R.color.gray);
				 * rrfriends.setBackgroundResource(R.color.gray);
				 */
				vlvoicey.setVisibility(View.VISIBLE);
				vlinbox.setVisibility(View.GONE);
				vlfriends.setVisibility(View.GONE);
				vlchats.setVisibility(View.GONE);

				displayView(0, "normal");
			}
		});

		rrchat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*
				 * rrvoicey.setBackgroundResource(R.color.blue);
				 * rrinbox.setBackgroundResource(R.color.gray);
				 * rrfriends.setBackgroundResource(R.color.gray);
				 */
				vlvoicey.setVisibility(View.GONE);
				vlinbox.setVisibility(View.GONE);
				vlfriends.setVisibility(View.GONE);
				vlchats.setVisibility(View.VISIBLE);

				displayView(6, "normal");
			}
		});

		rrinbox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				/*
				 * rrvoicey.setBackgroundResource(R.color.gray);
				 * rrinbox.setBackgroundResource(R.color.blue);
				 * rrfriends.setBackgroundResource(R.color.gray);
				 */
				vlvoicey.setVisibility(View.GONE);
				vlinbox.setVisibility(View.VISIBLE);
				vlfriends.setVisibility(View.GONE);
				vlchats.setVisibility(View.GONE);

				Fragment fragment = null;
				fragment = new InboxFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();
			}
		});

		rrfriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*
				 * rrvoicey.setBackgroundResource(R.color.gray);
				 * rrinbox.setBackgroundResource(R.color.gray);
				 * rrfriends.setBackgroundResource(R.color.blue);
				 */
				vlvoicey.setVisibility(View.GONE);
				vlinbox.setVisibility(View.GONE);
				vlfriends.setVisibility(View.VISIBLE);
				vlchats.setVisibility(View.GONE);
				displayView(3, "normal");
			}
		});

		rrmute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(HomeActivity.this);

				String muteControl = sharedPreferences.getString("muteControl",
						null);
				SharedPreferences.Editor editor = sharedPreferences.edit();

				if (muteControl == null || muteControl.equals("0")) {

					editor.putString("muteControl", "1");
					editor.commit();
					ivaddimage.setBackgroundResource(R.drawable.mute1);

				} else {
					editor.putString("muteControl", "0");
					editor.commit();
					ivaddimage.setBackgroundResource(R.drawable.mute2);

				}

			}
		});

		ibItem1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				displayView(0, "normal");
			}
		});

		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ Constants.app_folder + "");
		if (f.mkdir()) {
			System.out.println("Directory created");
		} else {
			System.out.println("Directory is not created");
		}
		imagebitmap = null;
		videoUrl = null;
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.menu, R.string.app_name,

				R.string.app_name

		) {
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	void displayAddUser() {

		final EditText etUserName, etPhoneNo, etEmail;
		final TextView etCode, etRegion, phmsg, mailmsg;
		TextView tvSave, tvuserId, tvalreadymember;

		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_username, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		etUserName = (EditText) promptsView.findViewById(R.id.etname);
		etPhoneNo = (EditText) promptsView.findViewById(R.id.etphone);
		etEmail = (EditText) promptsView.findViewById(R.id.etmail);
		etCode = (TextView) promptsView.findViewById(R.id.countrycode);
		etRegion = (TextView) promptsView.findViewById(R.id.regionname);
		phmsg = (TextView) promptsView.findViewById(R.id.phonenotify);
		mailmsg = (TextView) promptsView.findViewById(R.id.mailnotify);
		propic = (ImageView) promptsView.findViewById(R.id.capture);
		tvuserId = (TextView) promptsView.findViewById(R.id.tvuseridvaluel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);
		tvalreadymember = (TextView) promptsView
				.findViewById(R.id.tvalreadymember);

		tvuserId.setText(userCode);
		codes = this.getResources().getStringArray(R.array.CountryCodes);

		TelephonyManager tMgr = (TelephonyManager) HomeActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
			mPhoneNumber = tMgr.getLine1Number();
			String code = GetCountryZipCode();
			String[] s = code.split(",");
			etRegion.setText(s[0].replaceAll("[^A-Za-z ]", ""));
			etCode.setText("+" + s[0].replaceAll("[^0-9]", ""));
		} else {
			Toast.makeText(getApplicationContext(), "Your device has no SIM!",
					Toast.LENGTH_LONG).show();
			// etCode.setVisibility(View.GONE);
			// phmsg.setVisibility(View.GONE);
			// etPhoneNo.setVisibility(View.GONE);
		}
		Account[] accounts = AccountManager.get(HomeActivity.this)
				.getAccountsByType("com.google");
		for (Account account : accounts) {
			if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
				possibleEmail = account.name;
			}
		}

		if (possibleEmail.length() == 0) {
			Toast.makeText(getApplicationContext(),
					"No Registered Account Found", Toast.LENGTH_LONG).show();
			// mailmsg.setVisibility(View.GONE);
			// etEmail.setVisibility(View.GONE);
		}
		getFromSdcard();
		etPhoneNo.setText(mPhoneNumber);
		etEmail.setText(possibleEmail);

		etRegion.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						HomeActivity.this);
				builder.setTitle("Select Your Region");
				builder.setItems(codes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// Do something with the selection
						String c = codes[item];
						String[] s = c.split(",");
						etRegion.setText(s[0].replaceAll("[^A-Za-z ]", ""));
						etCode.setText("+" + s[0].replaceAll("[^0-9]", ""));
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		propic.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				Chooser();
			}
		});

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}

		tvalreadymember.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				alertDialog.cancel();
				alreadyMember();
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					userName = etUserName.getText().toString();
					userPhone = etCode.getText().toString()
							+ etPhoneNo.getText().toString();
					userEmail = etEmail.getText().toString();

					Bitmap bitmap = ((BitmapDrawable) propic.getDrawable())
							.getBitmap();
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
					imageByte = stream.toByteArray();
					encodedImageString = Base64.encodeToString(imageByte,
							Base64.DEFAULT);

					// byte[] bytarray = Base64.decode(encodedImageString,
					// Base64.DEFAULT);
					// Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray,
					// 0,
					// bytarray.length);

					if (userName.length() == 0) {
						// || userPhone.length() == 0
						// || userEmail.length() == 0) {
						Toast.makeText(HomeActivity.this, "Profile Name Blank",
								Toast.LENGTH_LONG).show();
					} else if (userPhone.length() < 6
							|| userPhone.length() > 13
							|| etCode.getText().equals("")) {
						Toast.makeText(HomeActivity.this,
								"Phone number Invalid", Toast.LENGTH_LONG)
								.show();
					} else {
						alertDialog.cancel();
						new saveUserNameAsyncTask().execute();

					}
					// else if (!userEmail.equals("")
					// && android.util.Patterns.EMAIL_ADDRESS.matcher(
					// userEmail).matches())

					// else {
					// Toast.makeText(HomeActivity.this, "Email Invalid",
					// Toast.LENGTH_LONG).show();
					// }

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public void getFromSdcard() {
		File f = new File("/sdcard/Voicey/profile.png");
		if (f.exists())
			f.delete();
		// String path = f.getAbsolutePath();
		// System.out.println(path);
		// Bitmap myBitmap = BitmapFactory.decodeFile(path);
		// propic.setImageBitmap(myBitmap);

		Resources res = getResources();
		/** from an Activity */
		propic.setImageDrawable(res.getDrawable(R.drawable.add_img));

	}

	void alreadyMember() {

		li = LayoutInflater.from(HomeActivity.this);
		grpview = li.inflate(R.layout.already_member, null);

		releaseNote = new Dialog(HomeActivity.this);
		releaseNote.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(false);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		EditText mob = (EditText) grpview.findViewById(R.id.etMobNum);
		EditText mail = (EditText) grpview.findViewById(R.id.etmail);
		EditText verify = (EditText) grpview.findViewById(R.id.etverify);
		final TextView codeset = (TextView) grpview
				.findViewById(R.id.countrycode);
		close = (TextView) grpview.findViewById(R.id.tvclose);
		TextView verifycode = (TextView) grpview.findViewById(R.id.save_grp);
		TextView sendmail = (TextView) grpview.findViewById(R.id.tvsendmail);

		codes = this.getResources().getStringArray(R.array.CountryCodes);
		codeset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				TelephonyManager tMgr = (TelephonyManager) HomeActivity.this
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
					mPhoneNumber = tMgr.getLine1Number();
					String cd = GetCountryZipCode();
					String[] s = cd.split(",");
					codeset.setText("+" + s[0].replaceAll("[^0-9]", ""));
				} else {
					Toast.makeText(getApplicationContext(),
							"Your device has no SIM!", Toast.LENGTH_LONG)
							.show();
					AlertDialog.Builder builder = new AlertDialog.Builder(
							HomeActivity.this);
					builder.setTitle("Select Your Region");
					builder.setItems(codes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									// Do something with the selection
									String c = codes[item];
									String[] s = c.split(",");
									codeset.setText("+"
											+ s[0].replaceAll("[^0-9]", ""));
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});

		sendmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		verifycode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// releaseNote.dismiss();
				displayView(0, "normal");
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				displayAddUser();
			}
		});
		releaseNote.show();
	}

	void Chooser() {

		li = LayoutInflater.from(HomeActivity.this);
		grpview = li.inflate(R.layout.photo_select, null);

		releaseNote = new Dialog(HomeActivity.this);
		releaseNote.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		releaseNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		releaseNote.setContentView(grpview);
		releaseNote.setCancelable(false);
		releaseNote.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		cam = (TextView) grpview.findViewById(R.id.tvcamera);
		gal = (TextView) grpview.findViewById(R.id.tvgallery);
		close = (TextView) grpview.findViewById(R.id.tvcancel);
		TextView tv1 = (TextView) grpview.findViewById(R.id.tvvideo);
		TextView tv2 = (TextView) grpview.findViewById(R.id.textView34);

		tv1.setVisibility(View.GONE);
		tv2.setVisibility(View.GONE);
		cam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent,
						CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
			}
		});
		gal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
				String path = Environment.getExternalStorageDirectory()
						+ "/images/imagename.jpg";
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, LOAD_IMAGE);
			}
		});
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseNote.dismiss();
			}
		});
		releaseNote.show();
	}

	private class saveUserNameAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			System.out.println(userName + "------" + userPhone + "-------"
					+ userEmail);
			return Webservices.saveUserName(userCode, userName, userPhone,
					userEmail);
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result != null) {

					JSONObject jObj = new JSONObject(result);
					sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(HomeActivity.this);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					String Name = URLDecoder.decode(jObj.getString("Name"),
							"UTF-8");
					editor.putString("userName", Name);
					editor.putString("userPhone", userPhone);
					editor.putString("userEmail", userEmail);

					editor.commit();
					// new GetAudioList().execute();

					displayView(0, "normal");

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	public void displayCompose() {
		ImageView ivrecord;
		TextView tvClose, tvsent;
		final EditText ettextmsg;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.compose_popup, null);

		ivrecord = (ImageView) promptsView.findViewById(R.id.ivrecord);
		ivAddPhoto = (ImageView) promptsView.findViewById(R.id.ivaddimage);
		ettextmsg = (EditText) promptsView.findViewById(R.id.ettextmsg);

		tvClose = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvsent = (TextView) promptsView.findViewById(R.id.tvsend);

		ivAddPhoto.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				alertDialog.cancel();
				// displayimageReply(audioInfo);
				displayPhotoSelect("compose");

				// displayPhotoSelect();

			}
		});

		ivrecord.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				/*
				 * StringBuffer friendBuffer = new StringBuffer(); if
				 * (shareFriendList.size() > 0) { for (Friend f :
				 * shareFriendList) {
				 * 
				 * friendBuffer.append(f.getFriendId() + ",");
				 * 
				 * }
				 */

				alertDialog.cancel();
				displayAlert("1", null);
				/*
				 * } else {
				 * 
				 * Toast.makeText(HomeActivity.this.getBaseContext(),
				 * "Please click on Send to and select friends ",
				 * Toast.LENGTH_LONG).show();
				 * 
				 * }
				 */

				// displayPhotoSelect();

			}
		});

		/*
		 * tvsendto.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * 
		 * 
		 * 
		 * 
		 * } });
		 */

		tvClose.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				alertDialog.cancel();

			}
		});

		tvsent.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				String title = ((TextView) ettextmsg).getText().toString();

				if (title.length() == 0) {
					Toast.makeText(HomeActivity.this.getBaseContext(),
							"Enter your message", Toast.LENGTH_LONG).show();

				} else if (title.length() > 140) {
					Toast.makeText(HomeActivity.this.getBaseContext(),
							"No digging please. Maximum 140 char per message.",
							Toast.LENGTH_LONG).show();

				} else {

					voiceyReply = new VoiceyReply();
					voiceyReply.setTitle(title);
					voiceyReply.setUsercode(userCode);

					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");

					// new ReplayText().execute();
					displayVoiceyFriendCC("text");
					// alertDialog.cancel();

					// friendBuffer.append(audioInfo.getUser_code() + ",");

				}
			}
		});

		alertDialog = new Dialog(HomeActivity.this, R.style.Dialog_No_Border);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams wmlp = alertDialog.getWindow()
				.getAttributes();

		wmlp.gravity = Gravity.TOP;
		wmlp.x = 0; // x position
		wmlp.y = 80;

		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		alertDialog.setContentView(promptsView);
		alertDialog.setCancelable(true);
		alertDialog.show();

	}

	void displayimageReply(final AudioInfo audioInfo) {

		TextView tvcancel, tvReply;
		LayoutInflater li = LayoutInflater.from(HomeActivity.this);
		View promptsView = li.inflate(R.layout.image_reply, null);
		final EditText etTitle;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				HomeActivity.this,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		ivAddPhoto = (ImageView) promptsView.findViewById(R.id.ivaddimage);

		tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvReply = (TextView) promptsView.findViewById(R.id.tvreply);
		etTitle = (EditText) promptsView.findViewById(R.id.ettextmsg);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		/*
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.argb(0, 0, 0, 0)));
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 */
		alertDialog.show();

		tvcancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					alertDialog.cancel();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		ivAddPhoto.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					// displayPhotoSelect();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvReply.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					voiceyReply = new VoiceyReply();
					String title = null;
					if (imagebitmap != null) {

						String titleStr = ((TextView) etTitle).getText()
								.toString();

						if (titleStr.length() > 0) {
							title = titleStr;
						} else {
							// title="Voicey ID " + userCode;
						}

						File file = new File(Constants.image_folder + "/"
								+ userCode + ".jpeg");

						voiceyReply.setImage_name(userCode);

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(file);
							imagebitmap.compress(Bitmap.CompressFormat.JPEG,
									90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					/*
					 * StringBuffer friendBuffer = new StringBuffer(); if
					 * (shareFriendList.size() > 0) { for (Friend f :
					 * shareFriendList) {
					 * 
					 * friendBuffer.append(f.getFriendId() + ",");
					 * 
					 * } } friendBuffer.append(audioInfo.getUser_code() + ",");
					 */

					voiceyReply.setTitle(title);
					voiceyReply.setUsercode(userCode);
					// voiceyReply.setSharedFriendCode(friendBuffer.toString());
					voiceyReply.setPublic_control("0");
					voiceyReply.setUser_control("1");
					voiceyReply.setUserid(userId);
					voiceyReply.setType("classifield");
					if (videoUrl != null) {
						voiceyReply.setVideo_name(videoUrl);
					}

					alertDialog.cancel();
					displayVoiceyFriendCC("image");
					// new ReplayImage().execute();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	private class ReplayImage extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.replayImage(voiceyReply);
		}

		protected void onProgressUpdate(Void... progress) {
			dialog = new Dialog(HomeActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(HomeActivity.this,
								"Sent message sucessfully.", Toast.LENGTH_LONG)
								.show();
						playBeep();

					} else {
						Toast.makeText(HomeActivity.this,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	private class ReplayText extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			// publishProgress((Void[]) null);
			return Webservices.replayText(voiceyReply);
		}

		protected void onProgressUpdate(Void... progress) {

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null && result.length() > 0) {

					JSONObject jObj = new JSONObject(result);

					String status = jObj.getString("status");

					if (status.equals("1")) {

						Toast.makeText(HomeActivity.this,
								"Sent message sucessfully.", Toast.LENGTH_LONG)
								.show();
						playBeep();

					} else {
						Toast.makeText(HomeActivity.this,
								"Error occure while Share file.",
								Toast.LENGTH_LONG).show();

					}
				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
	}

	void playBeep() {
		MediaPlayer mPlayer;
		mPlayer = MediaPlayer.create(HomeActivity.this, R.raw.toest_sound);
		mPlayer.start();
	}

	void displayVoiceyFriendCC(final String type) {

		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(HomeActivity.this);
		View promptsView = li.inflate(R.layout.share_frienslist, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				HomeActivity.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertdialogfriemds = alertDialogBuilder.create();

		// etFriendId = (EditText) promptsView.findViewById(R.id.etuserid);
		// etFriendName = (EditText) promptsView.findViewById(R.id.etname);
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);

		lvFriendList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		friendList = new ArrayList<Friend>();
		new GetFriendList().execute();

		tvCancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					alertdialogfriemds.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (shareFriendList.size() > 0 || shareGroupList.size() > 0) {
						StringBuffer friendBuffer = new StringBuffer();
						if (shareFriendList.size() > 0) {
							for (Friend f : shareFriendList) {

								friendBuffer.append(f.getFriendId() + ",");

							}
							voiceyReply.setSharedFriendCode(friendBuffer
									.toString());
						}

						if (shareGroupList.size() > 0) {
							friendBuffer = new StringBuffer();

							for (Friend f : shareGroupList) {

								friendBuffer.append(f.getFriendId() + ",");

							}
							voiceyReply.setSharedGroupCode(friendBuffer
									.toString());

						}

						if (type.equals("text")) {

							new ReplayText().execute();
						} else if (type.equals("image")) {

							new ReplayImage().execute();

						}
						alertdialogfriemds.cancel();
						alertDialog.cancel();

					} else {

						Toast.makeText(HomeActivity.this.getBaseContext(),
								"Please click on Send to and select friends",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		alertdialogfriemds.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertdialogfriemds.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertdialogfriemds.show();

	}

	public void displayAlert(final String senderId, final String replyId) {

		TextView tvquickshare, tvquickreply;
		RelativeLayout quicksharepannel, rrsavepannel, rrquickreply;

		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.record_page, null);

		ivstart = (ImageButton) promptsView.findViewById(R.id.ivstart);
		ivstop = (ImageView) promptsView.findViewById(R.id.ivstop);
		ivplay = (ImageView) promptsView.findViewById(R.id.ivplay);
		tvTimmer = (TextView) promptsView.findViewById(R.id.currentduration);
		tvmaxTime = (TextView) promptsView.findViewById(R.id.totalduration);
		tvComment = (TextView) promptsView.findViewById(R.id.tvcomment);
		tvUserId = (TextView) promptsView.findViewById(R.id.tvuserid);
		// tvcancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvsave = (TextView) promptsView.findViewById(R.id.tvsave);
		tvquickshare = (TextView) promptsView.findViewById(R.id.tvqshare);
		tvquickreply = (TextView) promptsView.findViewById(R.id.tvquickreply);
		quicksharepannel = (RelativeLayout) promptsView
				.findViewById(R.id.quicksharepannel);
		rrsavepannel = (RelativeLayout) promptsView
				.findViewById(R.id.rrsavepannel);
		rrquickreply = (RelativeLayout) promptsView
				.findViewById(R.id.rrquickreply);
		imagebitmap = null;

		// tvControl= (TextView) promptsView.findViewById(R.id.tvcontol);
		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		Typeface face = Typeface.createFromAsset(this.getAssets(),
				"verdana.ttf");
		spCategory = (Spinner) promptsView.findViewById(R.id.spCategory);
		// tvheading = (TextView) promptsView.findViewById(R.id.tvheading);

		// tvheading.setTypeface(face);

		songProgressBar = (SeekBar) promptsView
				.findViewById(R.id.songProgressBar);

		etTitle = (EditText) promptsView.findViewById(R.id.ettitle);
		etMood = (EditText) promptsView.findViewById(R.id.etmood);
		// tvAdd = (EditText) promptsView.findViewById(R.id.etyouradd);
		ivAddPhoto = (ImageView) promptsView.findViewById(R.id.ivaddimage);

		llPublic = (RelativeLayout) promptsView.findViewById(R.id.llpublic);
		llUser = (RelativeLayout) promptsView.findViewById(R.id.lluser);
		ivMaximize = (ImageView) promptsView.findViewById(R.id.ivmaximize);
		tgmakepublic = (ToggleButton) promptsView
				.findViewById(R.id.tgmakepublic);
		tguser = (ToggleButton) promptsView.findViewById(R.id.tganonymous);
		ivMinimize = (ImageView) promptsView.findViewById(R.id.ivminimize);
		tvUserId.setText(userCode);
		etMood.setVisibility(View.GONE);
		llPublic.setVisibility(View.GONE);
		llUser.setVisibility(View.GONE);
		ivMinimize.setVisibility(View.GONE);
		llUser.setVisibility(View.GONE);
		tvComment.setVisibility(View.GONE);
		videoUrl = null;

		if (senderId.equals("0")) {

			rrquickreply.setVisibility(View.GONE);
		} else {
			rrsavepannel.setVisibility(View.GONE);
			quicksharepannel.setVisibility(View.GONE);

		}
		// tvAdd.setVisibility(View.GONE);
		// ivAddPhoto.setVisibility(View.GONE);
		// spCategory.setVisibility(View.GONE);
		popupControl = "classifield";
		new getCategoryAsyncTask().execute();
		// tvControl.setText("6 Sec");
		tgmakepublic.setChecked(true);
		tguser.setChecked(true);
		ivstart.setBackgroundResource(R.drawable.record_inactive);
		ivplay.setBackgroundResource(R.drawable.play_inactive);
		ivstop.setBackgroundResource(R.drawable.stop_inactive);

		/*
		 * AlertDialog.Builder alertDialogBuilder = new
		 * AlertDialog.Builder(this, R.style.Dialog_No_Border);
		 * alertDialogBuilder.setCancelable(false);
		 * 
		 * alertDialogBuilder.setView(promptsView); final AlertDialog
		 * alertDialog = alertDialogBuilder.create();
		 * 
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.argb(0, 0, 0, 0)));
		 * alertDialog.setCancelable(false);
		 * alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * alertDialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
		 * alertDialog.show();
		 */

		alertDialog = new Dialog(HomeActivity.this, R.style.Dialog_No_Border);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		alertDialog.setContentView(promptsView);
		alertDialog.show();

		/*
		 * spControl = (Spinner) promptsView.findViewById(R.id.spControl);
		 * List<String> list = new ArrayList<String>(); list.add("VOICEY");
		 * list.add("VOICEY +");
		 * 
		 * ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, list);
		 * 
		 * dataAdapter
		 * .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item
		 * );
		 */

		/*
		 * spControl.setAdapter(dataAdapter);
		 * 
		 * spControl.setOnItemSelectedListener(new OnItemSelectedListener() {
		 * 
		 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1,
		 * int pos, long id) {
		 * 
		 * String workRequestType = arg0.getItemAtPosition(pos).toString();
		 * 
		 * if (workRequestType.equals("VOICEY")) { popupControl = "normal";
		 * 
		 * etMood.setVisibility(View.GONE); llPublic.setVisibility(View.GONE);
		 * llUser.setVisibility(View.GONE); ivMinimize.setVisibility(View.GONE);
		 * ivMaximize.setVisibility(View.VISIBLE);
		 * tvAdd.setVisibility(View.GONE); spCategory.setVisibility(View.GONE);
		 * ivAddPhoto.setVisibility(View.GONE);
		 * 
		 * } else if (workRequestType.equals("VOICEY +")) { popupControl =
		 * "classifield";
		 * 
		 * etMood.setVisibility(View.GONE); llPublic.setVisibility(View.GONE);
		 * llUser.setVisibility(View.GONE); ivMinimize.setVisibility(View.GONE);
		 * ivMaximize.setVisibility(View.GONE);
		 * tvAdd.setVisibility(View.VISIBLE);
		 * ivAddPhoto.setVisibility(View.VISIBLE);
		 * spCategory.setVisibility(View.VISIBLE); new
		 * getCategoryAsyncTask().execute();
		 * 
		 * }
		 * 
		 * }
		 * 
		 * @Override public void onNothingSelected(AdapterView<?> arg0) { //
		 * TODO Auto-generated method stub
		 * 
		 * } });
		 */
		/*
		 * tvControl.setOnClickListener(new OnClickListener() {
		 * 
		 * @SuppressLint("NewApi") public void onClick(View v) {
		 * if(popupControl.equals("normal")){
		 * 
		 * popupControl="classifield";
		 * 
		 * tvControl.setText("Classifield");
		 * 
		 * etMood.setVisibility(View.GONE); llPublic.setVisibility(View.GONE);
		 * llUser.setVisibility(View.GONE); ivMinimize.setVisibility(View.GONE);
		 * ivMaximize.setVisibility(View.GONE);
		 * tvAdd.setVisibility(View.VISIBLE);
		 * ivAddPhoto.setVisibility(View.VISIBLE);
		 * spCategory.setVisibility(View.VISIBLE); new
		 * getCategoryAsyncTask().execute();
		 * 
		 * }else if(popupControl.equals("classifield")){
		 * 
		 * tvControl.setText("6 Sec"); popupControl="normal";
		 * 
		 * etMood.setVisibility(View.GONE); llPublic.setVisibility(View.GONE);
		 * llUser.setVisibility(View.GONE); ivMinimize.setVisibility(View.GONE);
		 * ivMaximize.setVisibility(View.VISIBLE);
		 * tvAdd.setVisibility(View.GONE); spCategory.setVisibility(View.GONE);
		 * ivAddPhoto.setVisibility(View.GONE); }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * } });
		 */

		if (m != null) {

			m.stop();
		}
		customHandler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacksAndMessages(null);
		playBeep("B");

		ivstart.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (myAudioRecorder != null) {
						// delay()

						stopRecord();
						// playBeep("S");

					}

					if (m != null) {

						m.stop();
					}
					customHandler.removeCallbacksAndMessages(null);
					mHandler.removeCallbacksAndMessages(null);
					playBeep("B");

				} catch (Exception e) {

				}
			}
		});

		/*
		 * ivstart.setOnLongClickListener(new OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) {
		 * 
		 * if (m != null) {
		 * 
		 * m.stop(); } customHandler.removeCallbacksAndMessages(null);
		 * mHandler.removeCallbacksAndMessages(null); playBeep("B");
		 * 
		 * return true; } });
		 */
		/*
		 * ivstart.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { if
		 * (event.getAction() == MotionEvent.ACTION_DOWN) {
		 * 
		 * } else if (event.getAction() == MotionEvent.ACTION_UP) {
		 * 
		 * try { if (myAudioRecorder != null) { // delay()
		 * 
		 * stopRecord(); playBeep("S");
		 * 
		 * }
		 * 
		 * } catch (Exception e) {
		 * 
		 * } } return false; } });
		 */

		ivstop.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				// stopPlay();

				if (myAudioRecorder != null) {
					// delay()

					stopRecord();
					playBeep("S");

				}

				if (m != null) {

					m.stop();
				}
			}
		});

		ivplay.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					play();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivAddPhoto.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					displayPhotoSelect("record");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivMaximize.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					etMood.setVisibility(View.VISIBLE);
					llPublic.setVisibility(View.VISIBLE);
					llUser.setVisibility(View.VISIBLE);
					ivMinimize.setVisibility(View.VISIBLE);
					ivMaximize.setVisibility(View.GONE);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivMinimize.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					etMood.setVisibility(View.GONE);
					llPublic.setVisibility(View.GONE);
					llUser.setVisibility(View.GONE);
					ivMinimize.setVisibility(View.GONE);
					ivMaximize.setVisibility(View.VISIBLE);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		ivClose.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {
					if (myAudioRecorder != null) {

						myAudioRecorder.release();
					}
					if (m != null) {
						m.stop();
					}
					customHandler.removeCallbacksAndMessages(null);
					mHandler.removeCallbacksAndMessages(null);
					isActivePopup = true;
					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tguser.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				String usercontrol = (String) tguser.getText();

				if (tguser.isChecked()) {

					tvUserId.setText(userCode);

				} else {
					tvUserId.setText("Annunymous");
				}

			}
		});

		tvquickreply.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (myAudioRecorder != null) {

					myAudioRecorder.release();
				}
				if (m != null) {
					m.stop();
				}
				customHandler.removeCallbacksAndMessages(null);
				mHandler.removeCallbacksAndMessages(null);

				audioInfo = new AudioInfo();
				String title = ((TextView) etTitle).getText().toString();
				if (title.length() == 0) {
					// title = "Voicey ID " + userCode;

				}

				audioInfo.setTitle(title);

				audioInfo.setPublic_control("0");
				audioInfo.setUser_control("1");

				if (popupControl.equals("classifield")) {
					// String yourAd = ((TextView) tvAdd).getText().toString();
					// audioInfo.setYourAd(yourAd);

					if (videoUrl != null) {

						audioInfo.setVideoFilePath(videoUrl);

					}

					audioInfo.setImagebitmap(imagebitmap);

					if (imagebitmap != null) {

						File file = new File(Constants.image_folder + "/"
								+ title + ".jpeg");

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(file);
							imagebitmap.compress(Bitmap.CompressFormat.JPEG,
									90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					audioInfo.setCategoryId(categoryId);
				}
				isActivePopup = true;
				audioInfo.setUserid(userId);
				audioInfo.setUser_code(userCode);
				audioInfo.setType(popupControl);
				try {
					File f = new File(Constants.temp_url);

					if (f.exists()) {

						InputStream in = new FileInputStream(Constants.temp_url);
						OutputStream out = new FileOutputStream(Environment
								.getExternalStorageDirectory()
								+ "/"
								+ Constants.app_folder
								+ "/"
								+ userCode
								+ ".3gp");

						audioInfo.setFileName(userCode);

						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						in.close();
						out.close();

						f.delete();

						alertDialog.cancel();

						Fragment fragment = null;
						fragment = new InboxFragment();
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.frame_container, fragment)
								.commit();

					}

					if (senderId.equals("1")) {

						displayVoiceyFriend();
					} else {
						audioInfo.setFriendStr(senderId);
						audioInfo.setId(replyId);
						new QuickShareSave().execute();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		tvquickshare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (myAudioRecorder != null) {

					myAudioRecorder.release();
				}
				if (m != null) {
					m.stop();
				}
				customHandler.removeCallbacksAndMessages(null);
				mHandler.removeCallbacksAndMessages(null);
				isActivePopup = true;
				audioInfo = new AudioInfo();
				String title = ((TextView) etTitle).getText().toString();
				if (title.length() == 0) {
					// title = "Voicey ID " + userCode;

				}

				audioInfo.setTitle(title);

				audioInfo.setPublic_control("0");
				audioInfo.setUser_control("1");

				if (popupControl.equals("classifield")) {
					// String yourAd = ((TextView) tvAdd).getText().toString();
					// audioInfo.setYourAd(yourAd);

					if (videoUrl != null) {

						audioInfo.setVideoFilePath(videoUrl);

					}

					audioInfo.setImagebitmap(imagebitmap);

					if (imagebitmap != null) {

						File file = new File(Constants.image_folder + "/"
								+ userCode + ".jpeg");
						audioInfo.setImageName(userCode);

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(file);
							imagebitmap.compress(Bitmap.CompressFormat.JPEG,
									90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					audioInfo.setCategoryId(categoryId);
				}

				audioInfo.setUserid(userId);
				audioInfo.setUser_code(userCode);
				audioInfo.setType(popupControl);
				try {
					File f = new File(Constants.temp_url);

					if (f.exists()) {

						InputStream in = new FileInputStream(Constants.temp_url);
						OutputStream out = new FileOutputStream(Environment
								.getExternalStorageDirectory()
								+ "/"
								+ Constants.app_folder
								+ "/"
								+ userCode
								+ ".3gp");

						audioInfo.setFileName(userCode);

						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						in.close();
						out.close();

						f.delete();

						alertDialog.cancel();

						displayVoiceyFriend();

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		tvsave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				if (myAudioRecorder != null) {

					myAudioRecorder.release();
				}
				if (m != null) {
					m.stop();
				}
				customHandler.removeCallbacksAndMessages(null);
				mHandler.removeCallbacksAndMessages(null);
				isActivePopup = true;
				String title = ((TextView) etTitle).getText().toString();
				String source = ((TextView) etMood).getText().toString();

				if (title.length() == 0) {
					Toast.makeText(getBaseContext(), "Please enter Name it.",
							Toast.LENGTH_LONG).show();

				} else if (title.length() > 140) {
					Toast.makeText(getBaseContext(),
							"Title should be less then 15 character.",
							Toast.LENGTH_LONG).show();

				} else if (source.length() > 10) {
					Toast.makeText(getBaseContext(),
							"Source should be less then 10 character.",
							Toast.LENGTH_LONG).show();

				}

				else {
					audioInfo = new AudioInfo();
					audioInfo.setTitle(title);

					String makepublic = (String) tgmakepublic.getText();
					String usercontrol = (String) tguser.getText();

					audioInfo.setSource(source);

					if (makepublic.equals("ON")) {
						audioInfo.setPublic_control("1");
					} else {

						audioInfo.setPublic_control("0");
					}
					if (usercontrol.equals("ON")) {
						audioInfo.setUser_control("1");
					} else {

						audioInfo.setUser_control("0");
					}

					// audioInfo.setPublic_control("1");
					// audioInfo.setUser_control("1");

					/*
					 * String yourAd = ((TextView) tvAdd).getText().toString();
					 * audioInfo.setYourAd(yourAd);
					 */

					if (imagebitmap != null) {
						audioInfo.setImagebitmap(imagebitmap);

						File file = new File(Constants.image_folder + "/"
								+ title + ".jpeg");

						if (file.exists())
							file.delete();
						try {
							FileOutputStream out = new FileOutputStream(file);
							imagebitmap.compress(Bitmap.CompressFormat.JPEG,
									90, out);
							out.flush();
							out.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (videoUrl != null) {

						audioInfo.setVideoFilePath(videoUrl);

					}

					audioInfo.setCategoryId(categoryId);
					// }

					audioInfo.setUserid(userId);
					audioInfo.setType(popupControl);
					try {
						File f = new File(Constants.temp_url);

						if (f.exists()) {

							InputStream in = new FileInputStream(
									Constants.temp_url);
							OutputStream out = new FileOutputStream(Environment
									.getExternalStorageDirectory()
									+ "/"
									+ Constants.app_folder
									+ "/"
									+ title
									+ ".3gp");

							byte[] buf = new byte[1024];
							int len;
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}
							in.close();
							out.close();

							f.delete();

							alertDialog.cancel();

							new saveAudioAsyncTask().execute();

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

	}

	void displayVoiceyFriend() {

		TextView tvCancel, tvSave;
		// ListView lvUserList;
		LayoutInflater li = LayoutInflater.from(HomeActivity.this);
		View promptsView = li.inflate(R.layout.share_frienslist, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				HomeActivity.this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);

		final AlertDialog alertDialog = alertDialogBuilder.create();

		// etFriendId = (EditText) promptsView.findViewById(R.id.etuserid);
		// etFriendName = (EditText) promptsView.findViewById(R.id.etname);
		tvCancel = (TextView) promptsView.findViewById(R.id.tvcancel);
		tvSave = (TextView) promptsView.findViewById(R.id.tvsave);

		lvFriendList = (ListView) promptsView.findViewById(R.id.lvuserlist);
		friendList = new ArrayList<Friend>();
		new GetFriendList().execute();

		tvCancel.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					alertDialog.cancel();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {

				try {

					StringBuffer friendBuffer = new StringBuffer();
					if (shareFriendList.size() > 0) {
						for (Friend f : shareFriendList) {

							friendBuffer.append(f.getFriendId() + ",");

						}
						audioInfo.setFriendStr(friendBuffer.toString());
						alertDialog.cancel();
						new QuickShareSave().execute();

					} else {

						Toast.makeText(HomeActivity.this.getBaseContext(),
								"Please click on Send to and select friends",
								Toast.LENGTH_LONG).show();

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

	}

	private class QuickShareSave extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.saveQuickSaveAuto(audioInfo);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(HomeActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();

				// List<String> selItemList = new ArrayList<String>();

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	private class GetFriendList extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getFriendList(userCode);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(HomeActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			try {
				dialog.dismiss();
				friendList.clear();
				// List<String> selItemList = new ArrayList<String>();

				Friend friend;
				if (result != null && result.length() > 0) {

					JSONArray arr = new JSONArray(result);
					// JSONArray arr1= arr.getJSONObject(a)(0);
					if (arr.length() > 0) {
						friend = new Friend();
						friend.setFriendId(userCode);
						friend.setFriendName("Me");
						friend.setType("friend");
						// friend.setType("friend");
						friendList.add(friend);

						for (int i = 0; i < arr.length(); i++) {
							JSONObject jObj = arr.getJSONObject(i);
							friend = new Friend();
							String friendName = URLDecoder.decode(
									jObj.getString("friend_name"), "UTF-8");
							friend.setFriendId(jObj.getString("added_friend"));
							friend.setFriendName(friendName);
							friend.setType(jObj.getString("type"));
							// friend.setType("friend");
							friendList.add(friend);
						}
					}
				}

				FriendListAdapter friendAdapter = new FriendListAdapter(
						HomeActivity.this, R.layout.friend_list, friendList);

				lvFriendList.setAdapter(friendAdapter);

				// lvFriendList.setAdapter(friendAdapter);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	public class FriendListAdapter extends ArrayAdapter<Friend> {

		Context context;
		List<Friend> friendList;
		ViewHolder holder = null;
		Friend friendobj;

		public FriendListAdapter(Context context, int resourceId,
				List<Friend> friendList) {
			super(context, resourceId, friendList);
			this.context = context;
			this.friendList = friendList;
		}

		private class ViewHolder {

			TextView tvUserName;
			TextView tvUserId;
			RelativeLayout rlBody;
			ImageView ivRemove;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			Friend friend = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.user_list, null);
				holder = new ViewHolder();

				holder.tvUserName = (TextView) convertView
						.findViewById(R.id.tvuserName);
				holder.tvUserId = (TextView) convertView
						.findViewById(R.id.tvuserid);
				holder.rlBody = (RelativeLayout) convertView
						.findViewById(R.id.rlbody);
				holder.ivRemove = (ImageView) convertView
						.findViewById(R.id.remove_user);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.rlBody.setId(position);
			holder.rlBody.setTag(holder);
			shareFriendList.clear();
			holder.ivRemove.setVisibility(View.GONE);
			shareGroupList.clear();
			if (position % 4 == 0)
				// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

				holder.rlBody
						.setBackgroundResource(R.drawable.list_background1);
			else if (position % 4 == 1)
				holder.rlBody
						.setBackgroundResource(R.drawable.list_background2);
			else if (position % 4 == 2)
				holder.rlBody
						.setBackgroundResource(R.drawable.list_background3);
			else if (position % 4 == 3)
				holder.rlBody
						.setBackgroundResource(R.drawable.list_background4);

			holder.rlBody.setOnClickListener(new OnClickListener() {

				@SuppressLint("NewApi")
				public void onClick(View v) {

					friendobj = friendList.get(v.getId());
					int id = v.getId();
					holder = (ViewHolder) v.getTag();
					holder.rlBody.setBackgroundResource(R.color.black);
					if (friendobj.getType().equals("friend")) {
						if (shareFriendList.contains(friendobj)) {
							shareFriendList.remove(friendobj);

							if (id % 4 == 0)
								// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background1);
							else if (id % 4 == 1)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background2);
							else if (id % 4 == 2)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background3);
							else if (id % 4 == 3)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background4);

						} else {
							holder.rlBody.setBackgroundResource(R.color.black);
							shareFriendList.add(friendobj);
						}
					} else {

						if (shareGroupList.contains(friendobj)) {

							shareGroupList.remove(friendobj);
							// sharefriendId.remove(friendobj.getFriendId());
							if (id % 4 == 0)
								// holder.rlBody.setBackgroundColor(Color.parseColor("#22b14c"));

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background1);
							else if (id % 4 == 1)

								holder.rlBody
										.setBackgroundResource(R.drawable.list_background2);
							else if (id % 4 == 2)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background3);
							else if (id % 4 == 3)
								holder.rlBody
										.setBackgroundResource(R.drawable.list_background4);

						} else {
							holder.rlBody.setBackgroundResource(R.color.black);
							shareGroupList.add(friendobj);
							// sharefriendId.add(friendobj.getFriendId());
						}
					}

				}
			});

			holder.tvUserName.setText(friend.getFriendName());
			holder.tvUserId.setText(friend.getFriendId());

			return convertView;
		}

	}

	private class getCategoryAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.getCategoryList();
		}

		protected void onProgressUpdate(Void... progress) {

			/*
			 * dialog = new Dialog(HomeActivity.this);
			 * 
			 * dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
			 * ; dialog.setContentView(R.layout.loading_layout);
			 * dialog.setCancelable(false);
			 * dialog.getWindow().setBackgroundDrawableResource(
			 * android.R.color.transparent); dialog.show();
			 */

		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// dialog.dismiss();
			try {
				if (result != null) {
					Category category;
					ArrayList<Category> categoryList = new ArrayList<Category>();

					JSONArray arr = new JSONArray(result);
					for (int i = 0; i < arr.length(); i++) {
						JSONObject jObj = arr.getJSONObject(i);
						category = new Category();
						category.setCategoryName(jObj
								.getString("category_name"));
						category.setId(jObj.getString("id"));
						categoryList.add(category);
					}

					// spCountry = (Spinner)
					// findViewById(R.id.guestListCategory);
					spCategory.setAdapter(new CategoryAdapter(
							HomeActivity.this, R.layout.spinner_content,
							categoryList));

					spCategory
							.setOnItemSelectedListener(new CategorySelectedListener());

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class CategorySelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int position, long id) {

			Category category = (Category) spCategory.getSelectedItem();
			categoryId = category.getId();

		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	void displayPhotoSelect(final String type) {

		TextView camera, gallery, video;
		final Dialog dialog = new Dialog(HomeActivity.this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photo_select);

		camera = (TextView) dialog.findViewById(R.id.tvcamera);
		gallery = (TextView) dialog.findViewById(R.id.tvgallery);
		video = (TextView) dialog.findViewById(R.id.tvvideo);

		dialog.show();

		camera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				imagebitmap = null;
				if (type.equals("compose")) {
					displayimageReply(audioInfo);
				}
				takePictureButtonClicked();
				dialog.dismiss();
			}
		});

		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();
				imagebitmap = null;
				if (type.equals("compose")) {
					displayimageReply(audioInfo);
				}
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		video.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				imagebitmap = null;
				videoUrl = null;
				dialog.dismiss();
				if (type.equals("compose")) {
					displayimageReply(audioInfo);
				}

				Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
				// comma-separated MIME types
				mediaChooser.setType("video/*");
				startActivityForResult(mediaChooser, RESULT_LOAD_VIDEO);

			}
		});

	}

	private void takePictureButtonClicked() {
		Uri imageUri = Uri.fromFile(getTempFile(getApplicationContext()));
		Intent intent = createIntentForCamera(imageUri);
		startActivityForResult(intent, ACTIVITY_TAKE_PHOTO);
	}

	private File getTempFile(Context context) {
		String fileName = "temp_photo.jpg";
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, fileName);
	}

	private Intent createIntentForCamera(Uri imageUri) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// if the result is capturing Image
		if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
			propic.setImageBitmap(bitmap);
			try {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

				File f = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "Voicey" + File.separator
						+ "profile.png");
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());

				fo.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// create folder first
				createFolder();
				Bitmap bmp = (Bitmap) data.getExtras().get("data");

				// String filePath = "/sdcard/Voicey/profile.png";

				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(
							Environment.getExternalStorageDirectory()
									+ File.separator + "Voicey"
									+ File.separator + "profile.png");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedOutputStream bos = new BufferedOutputStream(
						fileOutputStream);

				// choose another format if PNG doesn't suit you
				bmp.compress(CompressFormat.PNG, 100, bos);

				propic.setImageBitmap(bmp);

				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(HomeActivity.this,
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
			} else {
				// failed to capture image
				Toast.makeText(HomeActivity.this,
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		}

		File imgFile;
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			imgFile = new File(picturePath);

			// new LoginActss().execute();

			try {
				// call the standard crop action intent (the user device may not
				// support it)
				final Bitmap thePic = BitmapFactory.decodeFile(picturePath);
				imagebitmap = thePic;
				ivAddPhoto.setImageBitmap(thePic);
				/*
				 * Intent cropIntent = new
				 * Intent("com.android.camera.action.CROP"); // indicate image
				 * type and Uri cropIntent.setDataAndType(Uri.fromFile(imgFile),
				 * "image/*"); // set crop properties
				 * cropIntent.putExtra("crop", "true"); // indicate aspect of
				 * desired crop cropIntent.putExtra("aspectX", 1);
				 * cropIntent.putExtra("aspectY", 1); // indicate output X and Y
				 * cropIntent.putExtra("outputX", 256);
				 * cropIntent.putExtra("outputY", 256); // retrieve data on
				 * return cropIntent.putExtra("return-data", true); // start the
				 * activity - we handle returning in onActivityResult
				 * startActivityForResult(cropIntent, PIC_CROP);
				 */
			}
			// respond to users whose devices do not support the crop action
			catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "Whoops - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage,
						Toast.LENGTH_SHORT);
				toast.show();
			}

		} else if (requestCode == ACTIVITY_TAKE_PHOTO
				&& resultCode == RESULT_OK) {

			try {
				// call the standard crop action intent (the user device may not
				// support it)

				final Bitmap thePic = (Bitmap) data.getExtras().get("data");
				imagebitmap = thePic;
				ivaddimage.setImageBitmap(thePic);

				/*
				 * Intent cropIntent = new
				 * Intent("com.android.camera.action.CROP"); // indicate image
				 * type and Uri cropIntent.setDataAndType(
				 * Uri.fromFile(getTempFile(getApplicationContext())),
				 * "image/*"); // set crop properties
				 * cropIntent.putExtra("crop", "true"); // indicate aspect of
				 * desired crop cropIntent.putExtra("aspectX", 1);
				 * cropIntent.putExtra("aspectY", 1); // indicate output X and Y
				 * cropIntent.putExtra("outputX", 256);
				 * cropIntent.putExtra("outputY", 256); // retrieve data on
				 * return cropIntent.putExtra("return-data", true); // start the
				 * activity - we handle returning in onActivityResult
				 * startActivityForResult(cropIntent, 1221);
				 */
			}
			// respond to users whose devices do not support the crop action
			catch (ActivityNotFoundException anfe) {
				// display an error message
				String errorMessage = "Whoops - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage,
						Toast.LENGTH_SHORT);
				toast.show();
			}

		} else if (requestCode == 1221 && resultCode == RESULT_OK
				&& null != data) {
			Bundle extras = data.getExtras();
			// get the cropped bitmap
			final Bitmap thePic = extras.getParcelable("data");
			imagebitmap = thePic;
			ivAddPhoto.setImageBitmap(thePic);

		} else if (requestCode == PIC_CROP && resultCode == RESULT_OK
				&& null != data) {
			Bundle extras = data.getExtras();
			// get the cropped bitmap
			final Bitmap thePic = extras.getParcelable("data");
			imagebitmap = thePic;
			ivAddPhoto.setImageBitmap(thePic);

		} else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK
				&& null != data) {
			// String path=data.getData();
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			videoUrl = picturePath;

			Bitmap bmThumbnail;

			// MICRO_KIND: 96 x 96 thumbnail
			bmThumbnail = ThumbnailUtils.createVideoThumbnail(picturePath,
					Thumbnails.MICRO_KIND);

			imagebitmap = bmThumbnail;
			ivAddPhoto.setImageBitmap(bmThumbnail);

		}
	}

	public void createFolder() {
		String RootDir = Environment.getExternalStorageDirectory()
				+ File.separator + "Voicey";
		File RootFile = new File(RootDir);
		RootFile.mkdir();
	}

	void playintial() {
		MediaPlayer mPlayer;

		mPlayer = MediaPlayer.create(HomeActivity.this, R.raw.bring_it_on);

		mPlayer.start();

	}

	void delay() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					stopRecord();
					playBeep("S");

					// play();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// your code here
			}
		}, 1000);

	}

	void delaybeforeplay() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {

					play();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// your code here
			}
		}, 1000);

	}

	void playBeep(final String type) {
		MediaPlayer mPlayer;
		if (type.equals("B")) {
			mPlayer = MediaPlayer.create(HomeActivity.this, R.raw.toest_sound);
		} else if (type.equals("S")) {
			mPlayer = MediaPlayer.create(HomeActivity.this, R.raw.toest_sound);

		}

		else {
			mPlayer = MediaPlayer.create(HomeActivity.this, R.raw.start_audio);

		}

		mPlayer.start();

		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (type.equals("R")) {

					playBeep("B");

				} else if (type.equals("S")) {

					delaybeforeplay();

				}

				else if (type.equals("B")) {

					startRecord();

				}

			}
		});

	}

	private void play() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {

		m = new MediaPlayer();
		m.setDataSource(outputFile);
		m.prepare();
		m.start();
		ivplay.setBackgroundResource(R.drawable.play_active);
		ivstop.setBackgroundResource(R.drawable.stop_inactive);
		// ivplay.setVisibility(View.GONE);
		// ivstop.setVisibility(View.VISIBLE);
		tvComment.setText("Playing...");

		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);

		// Updating progress bar
		updateProgressBar();

		m.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// songProgressBar.setVisibility(View.GONE);
				// tvTimmer.setVisibility(View.GONE);
				// tvmaxTime.setVisibility(View.GONE);
				// TODO Auto-generated method stub
				// btn_PlayAudio.setTag("1");
				// btn_PlayAudio.setText("Play");
				m.start();
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);

				// Updating progress bar
				updateProgressBar();

			}
		});

	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = m.getDuration();
			long currentDuration = m.getCurrentPosition();

			int secs = (int) (currentDuration / 1000);
			int maxsec = (int) (totalDuration / 1000);

			int currentmilisec = (int) (currentDuration % 60);
			int totalmilisec = (int) (totalDuration % 60);

			secs = secs % 60;
			maxsec = maxsec % 60;
			Double percentage = ((double) secs / maxsec) * 100;
			songProgressBar.setProgress(percentage.intValue());

			tvTimmer.setText("" + String.format("%02d", secs) + ":"
					+ currentmilisec);

			// Displaying Total Duration time
			// tvTimmer.setText("" + utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			if (maxsec == 6) {
				totalmilisec = 00;

			}
			tvmaxTime.setText("" + String.format("%02d", maxsec) + ":"
					+ totalmilisec);

			// Updating progress bar

			// Log.d("Progress", ""+progress);

			// Running this thread after 100 milliseconds
			if (secs < maxsec) {
				mHandler.postDelayed(this, 100);
			}
		}
	};

	private void startRecord() {
		try {

			File f = new File(Constants.temp_url);

			if (f.exists()) {
				f.delete();
			}

			ivstart.setBackgroundResource(R.drawable.record_active);

			// ivstop.setVisibility(View.VISIBLE);
			// ivstart.setVisibility(View.GONE);
			startTime = SystemClock.uptimeMillis();
			tvComment.setText("Recording...");

			tvmaxTime.setText("06:00");
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			customHandler.postDelayed(updateTimerThread, 0);
			setUpMediaRecorder();

			myAudioRecorder.prepare();
			myAudioRecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Toast.makeText(getApplicationContext(), "Recording started",
		// Toast.LENGTH_LONG).show();
	}

	private void stopRecord() {

		ivstart.setBackgroundResource(R.drawable.record_inactive);
		timeSwapBuff += timeInMilliseconds;
		customHandler.removeCallbacks(updateTimerThread);

		// ivstart.setEnabled(false);
		// ivplay.setVisibility(View.VISIBLE);

		// rlbottombutton.setVisibility(View.GONE);
		// songProgressBar.setVisibility(View.GONE);
		// tvTimmer.setVisibility(View.GONE);
		// tvmaxTime.setVisibility(View.GONE);
		// llbotombitton.setVisibility(View.VISIBLE);

		// btn_PlayAudio.setEnabled(true);
		myAudioRecorder.stop();
		myAudioRecorder.release();
		myAudioRecorder = null;
		// Toast.makeText(getApplicationContext(),
		// "Audio recorded successfully",
		// Toast.LENGTH_LONG).show();
	}

	void setUpMediaRecorder() {
		outputFile = Constants.temp_url;

		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		myAudioRecorder.setOutputFile(outputFile);
	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {

			int maxSec = 6;
			// timeSwapBuff = 0L;

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);

			int mins = secs / 60;

			secs = secs % 60;

			int milliseconds = (int) (updatedTime % 60);

			if (mins == 0 && secs <= 6) {
				Double percentage = ((double) secs / maxSec) * 100;
				songProgressBar.setProgress(percentage.intValue());

				Integer secInt = new Integer(secs);
				Integer maxCount = new Integer(Constants.total_audio_time);
				Integer actualSec = maxCount - secInt;

				tvTimmer.setText("" + String.format("%02d", actualSec) + ":"
						+ milliseconds);
				customHandler.postDelayed(this, 50);
			} else {
				tvTimmer.setText("0:00");
				/*
				 * stopRecord(); playBeep("S");
				 * 
				 * try { play();
				 * 
				 * } catch (Exception e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

				stopRecord();
				playBeep("S");

				// delay();

			}

		}

	};

	private void stopPlay() {

		m.stop();

		ivplay.setBackgroundResource(R.drawable.play_inactive);
		ivstop.setBackgroundResource(R.drawable.stop_active);

		tvComment.setText("Play your voicey");

	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position, "normal");
		}
	}

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

		case R.id.action_record:
			// playintial();
			if (isActivePopup) {
				displayAlert("0", null);
				isActivePopup = false;
			}
			return true;

		case R.id.action_compose:
			// playintial();

			displayCompose();

			/*
			 * Fragment fragment= new RecordFragment(); FragmentManager
			 * fragmentManager = getFragmentManager();
			 * fragmentManager.beginTransaction() .replace(R.id.frame_container,
			 * fragment).commit();
			 */

			return true;

		case R.id.actionBarLogo:
			displayAlert("0", null);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void sendfeedback() {

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL,
				new String[] { Constants.notifican_mail });
		// email.putExtra(Intent.EXTRA_CC, new String[]{ to});
		// email.putExtra(Intent.EXTRA_BCC, new String[]{to});
		email.putExtra(Intent.EXTRA_SUBJECT, Constants.notifican_mail_subject);

		// need this to prompts email client only
		email.setType("message/rfc822");

		startActivity(Intent.createChooser(email, "Choose an Email client :"));

	}

	void rateUs() {

		TextView tvrates, tvlater;

		final Dialog dialog = new Dialog(HomeActivity.this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialograteus);

		tvrates = (TextView) dialog.findViewById(R.id.textView3);
		tvlater = (TextView) dialog.findViewById(R.id.textView3a);

		dialog.show();

		tvrates.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.omkar.map"));
				if (!MyStartActivity(intent)) {
					// Market (Google play) app seems not installed, let's try
					// to open a webbrowser
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id=com.omkar.map"));
					if (!MyStartActivity(intent)) {
						// Well if this also fails, we have run out of options,
						// inform the user.
						Toast.makeText(
								HomeActivity.this,
								"Could not open Android market, please install the market app.",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

		tvlater.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});

	}

	private boolean MyStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position, String message) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		Bundle bundle = new Bundle();
		switch (position) {
		case 0:

			vlvoicey.setVisibility(View.VISIBLE);
			vlinbox.setVisibility(View.GONE);
			vlfriends.setVisibility(View.GONE);
			vlchats.setVisibility(View.GONE);

			fragment = new ListFragment();
			/*
			 * rrvoicey.setBackgroundResource(R.color.blue);
			 * rrinbox.setBackgroundResource(R.color.gray);
			 * rrfriends.setBackgroundResource(R.color.gray);
			 */
			bundle.putString("edttext", message);
			fragment.setArguments(bundle);
			break;
		case 1:

			// playintial();
			displayAlert("0", null);

			break;
		case 2:

			fragment = new UserEditFragment();

			break;
		case 3:

			vlvoicey.setVisibility(View.GONE);
			vlinbox.setVisibility(View.GONE);
			vlfriends.setVisibility(View.VISIBLE);
			vlchats.setVisibility(View.GONE);

			fragment = new FriendListFragment();

			bundle.putString("edttext", message);
			fragment.setArguments(bundle);
			/*
			 * rrvoicey.setBackgroundResource(R.color.gray);
			 * rrinbox.setBackgroundResource(R.color.gray);
			 * rrfriends.setBackgroundResource(R.color.blue);
			 */
			break;
		case 4:
			rateUs();

			break;
		case 5:
			sendfeedback();

			break;

		case 6:
			fragment = new ChatFriendFragment();

			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			// setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	private class saveAudioAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			return Webservices.saveAudio(audioInfo);
		}

		protected void onProgressUpdate(Void... progress) {

			dialog = new Dialog(HomeActivity.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				dialog.dismiss();
				if (result != null) {

					JSONObject jObj = new JSONObject(result);

					displayView(0, "normal");

				}

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	public void onBackPressed() {

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();

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

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {

		FragmentManager fragmentManager = getFragmentManager();

		InboxFragment fragment = (InboxFragment) fragmentManager
				.findFragmentById(R.id.frame_container);
		fragment.updatesmily(emojicon);

		// EmojiconsFragment.input(mEditEmojicon, emojicon);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		// EmojiconsFragment.backspace(mEditEmojicon);
	}

}
