package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.squareup.picasso.Picasso;

import foodzu.com.Utils.AppRater;
import foodzu.com.Utils.GPSTracker;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.adapters.AdapterAddress;
import foodzu.com.models.AddressObj;
import foodzu.com.models.Data_Models;
import foodzu.com.models.FilterItem;
import foodzu.com.models.Products;

public class HomeActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		ConnectionCallbacks, OnConnectionFailedListener,
		SearchView.OnQueryTextListener, SearchView.OnCloseListener,FilterFragment.FilterListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	// private CharSequence mTitle;
	// private ArrayList<HashMap<String, String>> products;
	private ArrayList<Products> Products_Arraylist, Search_Arraylist;
	public static Data_Models Cart_checklist;
	public static Data_Models homeproductslist;
	static MenuItem search_icon, more_icon;
	public static ArrayList<Products> Cart_Arraylist, mycartlist;
	private Products item_of_product, item;
	public static ProductsAdapter PD_adapt;
	private static CartAdapter CART_adapt;
	private static ListView homeproductlist, cartproductlist;
	static TextView bottombar, topbar, cartitem_count, top_cartitem_count,
			total_cost, top_total_cost, cart_empty_tv, maincat_name,
			subcat_name, current_address;
	static double item_total_cost = 0;
	static ImageView chkout_top, chkoutcart_top, chkoutcart_buttom,
			chkout_bottom, cart_empty, home_icon, noproduct_icon;
	static RelativeLayout Main_layout, View_layout, bottom_bar_rl,rl_filter,rl_sort;
	static LinearLayout navigation_bar, loader,filter_Bar;
	Intent i;

	static Integer sortPosition=0;
	public static Context baseContext;
	static SearchView searchView;
	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";
	public static String PID = "", UID = "";
	Dialog dialog;
	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;
	private static final int RC_SIGN_IN = 0;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	Products menu, submenu;
	String check,filterBrandSelact,filterPriceSelect;

	static String submenu_ID;

	String menu_name;

	String submenu_name;

	String S_query;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	String search_check = "YES";

	static String GRD_TOTAL;
	GPSTracker gps;
	String My_address;
	ListView lvAddress;
	SearchView locationSearchView;
	List<AddressObj> addressList = new ArrayList<AddressObj>();
	AdapterAddress adapterAddress;
	PlacesTask placesTask;
	AddressObj addressobj;
	public Menu mOptionsMenu;
	SharedPreferences.Editor edit;
	ArrayList<String> keywords;
	int RS = 0;
	 HashMap<String, FilterItem> brandSelactList;
	 HashMap<String, FilterItem> priceSelactList ;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_home);

		// AppRater.app_launched(HomeActivity.this);

		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		UID = sharedpreferences.getString("user_id", "");
		
		alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		// mTitle = "FoodZu";
		// getTitle();
		baseContext = getBaseContext();
		// products = new ArrayList<HashMap<String, String>>();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		Search_Arraylist = new ArrayList<Products>();
		Products_Arraylist = new ArrayList<Products>();
		Cart_Arraylist = new ArrayList<Products>();
		keywords = new ArrayList<String>();
		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		menu = new Products();
		submenu = new Products();
		homeproductslist = new Data_Models();
		
		getCurrentLocation();

	}

	@Override
	public void onBackPressed() {

	}

	/*
	 * @Override public void openOptionsMenu() {
	 * 
	 * Configuration config = getResources().getConfiguration();
	 * 
	 * if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >
	 * Configuration.SCREENLAYOUT_SIZE_LARGE) {
	 * 
	 * int originalScreenLayout = config.screenLayout; config.screenLayout =
	 * Configuration.SCREENLAYOUT_SIZE_LARGE; super.openOptionsMenu();
	 * config.screenLayout = originalScreenLayout;
	 * 
	 * } else { super.openOptionsMenu(); } }
	 */

	@Override
	public void onResume() {
		super.onResume();
		UID = sharedpreferences.getString("user_id", "");
		if (Utils.ORDER.equals("DONE")) {
			int size = Cart_Arraylist.size();
			for (int m = 0; m < size; m++) {
				Cart_Arraylist.get(0).setitem_qty_count(0);
				Cart_Arraylist.get(0).setitem_in_cart("NO");
				Cart_Arraylist.remove(0);
				Utils.ORDER = "OD";
			}
			buttombarAction(Cart_Arraylist, item_total_cost);
		} else {
			if (Cart_Arraylist != null & Cart_Arraylist.size() > 0) {
				reset();
				buttombarAction(Cart_Arraylist, item_total_cost);
			}
		}
		if (homeproductslist != null && Utils.load.equals("1")) {
			Products_Arraylist = homeproductslist.gethome_data();
			ProductsAdapter PD = new ProductsAdapter(HomeActivity.this,
					Products_Arraylist);
			homeproductlist.setAdapter(PD);
			Utils.load = "NO";
			displaySelectType();
		} else
			home_product();

	}
	@Override
	public void onFilterSelect(){
		
		check = "FILTER";
		StringBuffer brandBuffer = new StringBuffer();
		StringBuffer priceBuffer = new StringBuffer();
		for (Entry<String, FilterItem> brandItem : brandSelactList.entrySet()) {
			if (brandBuffer.length() > 0) {
				brandBuffer.append(",");

			}
			brandBuffer.append(brandItem.getKey());

		}
		filterBrandSelact = brandBuffer.toString();

		for (Entry<String, FilterItem> priceItem : priceSelactList.entrySet()) {
			if (priceBuffer.length() > 0) {
				priceBuffer.append(",");

			}
			FilterItem fi = priceItem.getValue();

			priceBuffer.append(fi.getStartValue() + "-" + fi.getEndValue());

		}

		filterPriceSelect = priceBuffer.toString();

		home_product();
		
		/*filterBrandSelact;
		filterPriceSelect=
		
		
		home_product();*/
	}

	@Override
	public void onNavigationDrawerItemSelected(int position, String Main_cat,
			String Main_cat_ID, String Sub_cat, String Sub_cat_ID, String show) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		if (show.equals("HIDE")) {
			check = "HIDE";
		} else {
			check = "SHOW";
			submenu_ID = Sub_cat_ID;
			menu_name = Main_cat;
			submenu_name = Sub_cat;
			Utils.Reload = "SUB";
		}
		brandSelactList=new HashMap<String, FilterItem>();
		priceSelactList=new HashMap<String, FilterItem>();
		//brandSelactList.clear();;
		//priceSelactList.clear(); ;
		home_product();
	}
	
	

	void home_product() {
		if (IsInternetPresent()) {
			if (Utils.load.equals("NO"))
				new getHomeProductList().execute();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									home_product();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			// mTitle = getString(R.string.title_section1);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("Foodzu");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			mOptionsMenu = menu;
			sharedpreferences = getSharedPreferences(MyLogin,
					Context.MODE_PRIVATE);
			if (sharedpreferences.getString("login", "").equals("success")
					|| sharedpreferences.getString("g_login", "").equals(
							"signin")
					|| sharedpreferences.getString("f_login", "").equals(
							"signin")) {
				if (Session.getActiveSession() == null
						&& sharedpreferences.getString("f_login", "").equals(
								"signin")) {
					logintoFB();
				}
				getMenuInflater().inflate(R.menu.home_logout, menu);

			} else
				getMenuInflater().inflate(R.menu.home, menu);

			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	public boolean showMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {

			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			sharedpreferences = getSharedPreferences(MyLogin,
					Context.MODE_PRIVATE);
			if (sharedpreferences.getString("login", "").equals("success")
					|| sharedpreferences.getString("g_login", "").equals(
							"signin")
					|| sharedpreferences.getString("f_login", "").equals(
							"signin")) {
				if (Session.getActiveSession() == null
						&& sharedpreferences.getString("f_login", "").equals(
								"signin")) {
					logintoFB();
				}
				getMenuInflater().inflate(R.menu.home_logout, menu);

			} else
				getMenuInflater().inflate(R.menu.home, menu);

			more_icon = menu.findItem(R.id.action_More);

			if (Build.VERSION.SDK_INT <= 10
					|| (Build.VERSION.SDK_INT >= 14 && ViewConfiguration.get(
							this).hasPermanentMenuKey())) {
				// menu key is present
				more_icon.setVisible(true);
			} else {
				more_icon.setVisible(false);
			}
			restoreActionBar();
			return true;
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (loader.getVisibility() == View.VISIBLE)
			loader.setVisibility(View.GONE);
		menu.clear();

		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		if (sharedpreferences.getString("login", "").equals("success")
				|| sharedpreferences.getString("g_login", "").equals("signin")
				|| sharedpreferences.getString("f_login", "").equals("signin"))
			getMenuInflater().inflate(R.menu.home_logout, menu);
		else
			getMenuInflater().inflate(R.menu.home, menu);

		search_icon = menu.findItem(R.id.action_search);
		if (searchView.getVisibility() == View.VISIBLE)
			search_icon.setVisible(false);
		more_icon = menu.findItem(R.id.action_More);

		if (Build.VERSION.SDK_INT <= 10
				|| (Build.VERSION.SDK_INT >= 14 && ViewConfiguration.get(this)
						.hasPermanentMenuKey())) {
			// menu key is present
			more_icon.setVisible(true);
		} else {
			more_icon.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_More:
			if (mNavigationDrawerFragment.isDrawerOpen())
				mNavigationDrawerFragment.close_drawer();
			new Handler().postDelayed(new Runnable() {
				public void run() {
					openOptionsMenu();
				}
			}, 0);
			return true;
		case R.id.action_search:
			if (mNavigationDrawerFragment.isDrawerOpen())
				mNavigationDrawerFragment.close_drawer();
			search_icon.setVisible(false);
			searchView.setVisibility(View.VISIBLE);
			searchView.setIconified(false);
			return true;
		case R.id.action_refresh:
			onBackPressed();
			if (loader.getVisibility() == View.VISIBLE)
				loader.setVisibility(View.GONE);
			homeproductlist.setVisibility(View.VISIBLE);
			noproduct_icon.setVisibility(View.GONE);
			search_icon.setVisible(true);
			searchView.setVisibility(View.GONE);
			home_asynctask();
			return true;
		case R.id.action_Aboutus:
			startActivity(new Intent(HomeActivity.this, AboutUsActivity.class));
			return true;
		case R.id.action_Login:
			i = new Intent(HomeActivity.this, LoginActivity.class);
			startActivity(i);
			return true;
		case R.id.action_SignUp:
			i = new Intent(HomeActivity.this, SignUpActivity.class);
			startActivity(i);
			return true;
		case R.id.action_Logout:
			sharedpreferences = getSharedPreferences(MyLogin,
					Context.MODE_PRIVATE);
			if (sharedpreferences.getString("login", "").equals("success")) {
				Editor editor = sharedpreferences.edit();
				editor.putString("login", "nosuccess");
				editor.putString("login_status", "OUT_N");
				editor.putString("user_email", "");
				editor.putString("user_pword", "");
				editor.commit();
			} else if (sharedpreferences.getString("g_login", "").equals(
					"signin"))
				signOutFromGplus();
			else if (sharedpreferences.getString("f_login", "")
					.equals("signin")) {
				signOutFromFacebook();
			} else
				updateUI(true, "G");

			return true;
		case R.id.action_orders:
			i = new Intent(HomeActivity.this, MyOrdersActivity.class);
			startActivity(i);
			return true;
		case R.id.action_profile:
			i = new Intent(HomeActivity.this, Profile.class);
			startActivity(i);
			return true;
		case R.id.action_favorite:
			startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
			return true;
		case R.id.action_share:

			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"FOODZU: Shopping Grocery with a fingertip.");
			sharingIntent
					.putExtra(
							android.content.Intent.EXTRA_TEXT,
							"Buy Your Daily/Monthly grocery with ease. Get it delivered at your doorstep. Enrich Better Living."
									+ System.getProperty("line.separator")
									+ System.getProperty("line.separator")
									+ "Download it at:      "
									+ System.getProperty("line.separator")
									+ "https://play.google.com/store/apps/details?id=foodzu.com&hl=en");
			startActivity(Intent.createChooser(sharingIntent, "Share via"));
			return true;
		default:

			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
			signOutFromGplus();
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
		updateUI(false, "G");
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	private void updateUI(boolean isSignedIn, String from) {

		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		if (isSignedIn) {
			Editor editor = sharedpreferences.edit();
			if (from.equals("G")) {
				editor.putString("g_login", "signin");
				editor.putString("login_status", "OK_G");
			} else {
				editor.putString("f_login", "signout");
				editor.putString("login_status", "OUT_FB");
				editor.putString("user_email", "empty");
			}
			editor.commit();
		} else {
			Editor editor = sharedpreferences.edit();
			editor.putString("g_login", "signout");
			editor.putString("login_status", "OUT_G");
			editor.putString("user_email", "empty");
			editor.commit();
		}
	}

	// private void signInWithGplus() {
	// if (!mGoogleApiClient.isConnecting()) {
	// mSignInClicked = true;
	// resolveSignInError();
	// }
	// }

	private void signOutFromFacebook() {
		if (Session.getActiveSession() != null) {
			Session session = Session.getActiveSession();
			session.closeAndClearTokenInformation();
			updateUI(true, "F");
		} else {
			logintoFB();
			signOutFromFacebook();
		}
	}

	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false, "G");
			Toast.makeText(this, "User logged out of G+!", Toast.LENGTH_LONG)
					.show();
		}
	}

	void logintoFB() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {
					boolean isPermissionAvailable = false;
					for (int i = 0; i < session.getPermissions().size(); i++) {
						if (session.getPermissions().get(i).contains("email")) {
							isPermissionAvailable = true;

							Request.newMeRequest(session,
									new GraphUserCallback() {

										@Override
										public void onCompleted(
												final GraphUser user,
												Response response) {

											if (user != null) {
												// getUserInfoFromFacebook(user);
											}
										}
									}).executeAsync();
						}
					}
					if (!isPermissionAvailable)
						;
					// getPermissionUserInfo();
				}
			}
		});
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		updateUI(true, "G");

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			View rootView = inflater.inflate(R.layout.activity_home, container,
					false);

			searchView = (SearchView) rootView.findViewById(R.id.searchView);

			searchView.setVisibility(View.GONE);
			homeproductlist = (ListView) rootView
					.findViewById(R.id.homeproduct_list);
			cartproductlist = (ListView) rootView
					.findViewById(R.id.cartproduct_list);
			bottombar = (TextView) rootView.findViewById(R.id.bottombar);
			topbar = (TextView) rootView.findViewById(R.id.topbar);
			chkout_bottom = (ImageView) rootView
					.findViewById(R.id.chk_out_bottom);
			noproduct_icon = (ImageView) rootView.findViewById(R.id.noproduct);
			home_icon = (ImageView) rootView.findViewById(R.id.home_icon);
			cart_empty = (ImageView) rootView.findViewById(R.id.cart_empty);
			chkout_top = (ImageView) rootView.findViewById(R.id.chk_out_top);
			chkoutcart_top = (ImageView) rootView
					.findViewById(R.id.cart_icon_top);
			chkoutcart_buttom = (ImageView) rootView
					.findViewById(R.id.cart_icon_buttom);
			navigation_bar = (LinearLayout) rootView
					.findViewById(R.id.navigation_bar);
			filter_Bar= (LinearLayout) rootView
					.findViewById(R.id.filter_bar);
			rl_filter= (RelativeLayout) rootView
					.findViewById(R.id.rl_filter);
			rl_sort= (RelativeLayout) rootView
					.findViewById(R.id.rl_sort);
			loader = (LinearLayout) rootView.findViewById(R.id.Loader);
			Main_layout = (RelativeLayout) rootView
					.findViewById(R.id.main_rel_layout);
			View_layout = (RelativeLayout) rootView
					.findViewById(R.id.view_rel_layout);
			bottom_bar_rl = (RelativeLayout) rootView
					.findViewById(R.id.main_bottombar);
			Main_layout.setVisibility(View.VISIBLE);
			maincat_name = (TextView) rootView.findViewById(R.id.maincat);
			subcat_name = (TextView) rootView.findViewById(R.id.subcat);
			cartitem_count = (TextView) rootView
					.findViewById(R.id.cartitem_count);
			top_cartitem_count = (TextView) rootView
					.findViewById(R.id.top_cartitem_count);
			total_cost = (TextView) rootView.findViewById(R.id.total_cost);
			top_total_cost = (TextView) rootView
					.findViewById(R.id.top_total_cost);
			cart_empty_tv = (TextView) rootView
					.findViewById(R.id.cart_empty_tv);
			navigation_bar.setVisibility(View.GONE);
			filter_Bar.setVisibility(View.GONE);
			noproduct_icon.setVisibility(View.GONE);
			loader.setVisibility(View.GONE);
			Cart_checklist = new Data_Models();

			searchView.setOnQueryTextListener((HomeActivity) getActivity());
			searchView.setOnCloseListener((HomeActivity) getActivity());

			// home_icon.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// homeproductlist.setVisibility(View.VISIBLE);
			// noproduct_icon.setVisibility(View.GONE);
			// ((HomeActivity) getActivity()).home_asynctask();
			// }
			// });
			rl_filter.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
					FragmentManager fm =getFragmentManager();
					FilterFragment frag = new FilterFragment();
					Bundle args = new Bundle();
				    args.putString("categoryId", submenu_ID);
				    frag.setArguments(args);
					frag.show(fm, "txn_tag");
					
				}
			});
           rl_sort.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					TextView tvPopularity, tvHightolow, tvLowtoHigh, tvNewestFirst;
					ImageView ivPopularity, ivHightolow, ivLowtoHigh, ivNewestFirst;
					final Dialog dialog = new Dialog(
							getActivity(),
							android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
					dialog.setContentView(R.layout.dialog_sort_by);
					tvPopularity = (TextView) dialog
							.findViewById(R.id.tvpopularity);
					tvHightolow = (TextView) dialog
							.findViewById(R.id.tvhightolow);
					tvLowtoHigh = (TextView) dialog
							.findViewById(R.id.tvlowtohigh);
					tvNewestFirst = (TextView) dialog
							.findViewById(R.id.tvnewest);
					ivPopularity= (ImageView) dialog
							.findViewById(R.id.ivselectpopularity);
					ivHightolow= (ImageView) dialog
							.findViewById(R.id.ivselecthightolow);
					ivLowtoHigh= (ImageView) dialog
							.findViewById(R.id.ivselectlowtohigh);
					ivNewestFirst= (ImageView) dialog
							.findViewById(R.id.ivselectnewest);
					dialog.show();
					ivPopularity.setVisibility(View.GONE);
					ivHightolow.setVisibility(View.GONE);
					ivLowtoHigh.setVisibility(View.GONE);
					ivNewestFirst.setVisibility(View.GONE);
					
					if (sortPosition == 0) {
                        ivPopularity.setVisibility(View.VISIBLE);
					} else if (sortPosition == 1) {
						ivHightolow.setVisibility(View.VISIBLE);
					} else if (sortPosition == 2) {
						ivLowtoHigh.setVisibility(View.VISIBLE);
					} else if (sortPosition == 3) {
						ivNewestFirst.setVisibility(View.VISIBLE);
					}
					
					
					tvPopularity.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							sortPosition =0;
							dialog.dismiss();

						}
					});
					tvHightolow.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							sortPosition =1;
							dialog.dismiss();

						}
					});
					tvLowtoHigh.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							sortPosition =2;
							dialog.dismiss();

						}
					});
					tvNewestFirst.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							
							sortPosition =3;
							dialog.dismiss();
						}
					});
					
				}
			});
			chkoutcart_buttom.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Cart_checklist.setcartdata(cartitems(Cart_Arraylist));
					Intent intent = new Intent(getActivity(),
							CheckoutActivity.class);
					intent.putExtra("GT", GRD_TOTAL);
					startActivity(intent);
				}
			});

			chkoutcart_top.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SlideToDown();
					Cart_checklist.setcartdata(cartitems(Cart_Arraylist));
					Intent intent = new Intent(getActivity(),
							CheckoutActivity.class);
					intent.putExtra("GT", GRD_TOTAL);
					startActivity(intent);
				}
			});

			bottombar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					SlideToAbove();
					search_icon.setVisible(true);
					searchView.setVisibility(View.GONE);
					chkoutcart_top.setVisibility(View.VISIBLE);
					View_layout.setVisibility(View.VISIBLE);

					if (CART_adapt != null)
						((HomeActivity) getActivity()).showcart();
				}
			});

			topbar.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					SlideToDown();
					// ((HomeActivity) getActivity()).home_asynctask();
					Main_layout.setVisibility(View.VISIBLE);
					// chkoutcart_buttom.setVisibility(View.VISIBLE);
					View_layout.setVisibility(View.GONE);
					chkoutcart_top.setVisibility(View.GONE);
				}
			});
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			// ((HomeActivity)
			// activity).onSectionAttached(getArguments().getInt(
			// ARG_SECTION_NUMBER));
		}

	}
	
     
		
		
		
	

	public void showcart() {
		CART_adapt = new CartAdapter(HomeActivity.this, Cart_Arraylist);
		cartproductlist.setAdapter(CART_adapt);

	}

	public void home_asynctask() {
		check = "HIDE";
		if (IsInternetPresent())
			new getHomeProductList().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									home_asynctask();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {

	}

	public void buttombarAction(ArrayList<Products> product,
			Double item_total_sum) {
		if (product.size() > 0) {
			// chkout_top.setVisibility(View.VISIBLE);
			chkoutcart_top.setEnabled(true);
			cart_empty_tv.setVisibility(View.GONE);
			cart_empty.setVisibility(View.GONE);
			bottom_bar_rl.setVisibility(View.VISIBLE);
			chkoutcart_buttom.setVisibility(View.VISIBLE);
			cartitem_count.setText(Integer.toString(product.size()));
			top_cartitem_count.setText(Integer.toString(product.size()));
			GRD_TOTAL = Double.toString(item_total_sum);
			total_cost.setText("\u20B9  " + Double.toString(item_total_sum));
			top_total_cost
					.setText("\u20B9  " + Double.toString(item_total_sum));
			if (CART_adapt != null) {
				CART_adapt.notifyDataSetChanged();
				CART_adapt = new CartAdapter(HomeActivity.this, Cart_Arraylist);
				// cartproductlist.setAdapter(CART_adapt);
			} else {
				CART_adapt = new CartAdapter(HomeActivity.this, Cart_Arraylist);
				cartproductlist.setAdapter(CART_adapt);
				CART_adapt.notifyDataSetChanged();
			}
			if (PD_adapt != null) {
				PD_adapt.notifyDataSetChanged();
				PD_adapt = new ProductsAdapter(HomeActivity.this,
						Products_Arraylist);
				homeproductlist.invalidateViews();
				// homeproductlist.setAdapter(PD_adapt);
			} else {
				PD_adapt = new ProductsAdapter(HomeActivity.this,
						Products_Arraylist);
				homeproductlist.invalidateViews();
				// homeproductlist.setAdapter(PD_adapt);
				PD_adapt.notifyDataSetChanged();
			}
		} else {
			// chkout_top.setVisibility(View.INVISIBLE);
			chkoutcart_top.setEnabled(false);
			cart_empty_tv.setVisibility(View.VISIBLE);
			cart_empty.setVisibility(View.VISIBLE);
			bottom_bar_rl.setVisibility(View.GONE);
			chkoutcart_buttom.setVisibility(View.GONE);
			cartitem_count.setText("");
			top_cartitem_count.setText("0");
			total_cost.setText("\u20B9  " + "0");
			top_total_cost.setText("\u20B9  " + "0");
			item_total_cost = 0.0;
			if (PD_adapt != null)
				PD_adapt.notifyDataSetChanged();
			else {
				PD_adapt = new ProductsAdapter(HomeActivity.this,
						Products_Arraylist);
				// homeproductlist.setAdapter(PD_adapt);
				PD_adapt.notifyDataSetChanged();
			}
		}
	}

	public String getkeywords() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.SEARCH_KEYWORD_URL;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String gethomeproducts() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.MAIN_HOME_PROD_URL;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String getfiltered_homeproducts(String submenu_ID) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.FILTER_PROD_URL;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL
					+ submenu_ID));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}
	
	public String getfiltered_products() {
		InputStream inputStream = null;
		String result = null;
		String URL = String.format(URLs.FILTER_URL,submenu_ID ,filterBrandSelact,filterPriceSelect);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public String getSearch_Items(String query) {
		InputStream inputStream = null;
		String result = null;
		String URL = String.format(URLs.SEARCH_URL, query, UID);
		System.out.println(URL);
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public class getSearchKeyword extends AsyncTask<Void, Void, Void> {

		protected void onPreExecute() {
		}

		protected Void doInBackground(Void... params) {

			String ky = getkeywords();
			try {
				if (ky != null && ky.length() > 0) {
					keywords.clear();
					JSONObject jObj_main = new JSONObject(ky);
					JSONArray result = jObj_main.getJSONArray("resultdata");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject data = result.getJSONObject(i);
							keywords.add(data.getString("product_name"));
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
		}
	}

	public class getHomeProductList extends AsyncTask<Void, Void, String> {

		Dialog dialog;
		String Main = "Y";

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(HomeActivity.this);
			dialog.setCancelable(false);
			if (dialog.isShowing())
				dialog.dismiss();
			dialog.show();
		}

		protected String doInBackground(Void... params) {
			String homeproducts;
			if (check.equals("HIDE") && Utils.Reload.equals("NO")) {
				homeproducts = gethomeproducts();
				Main = "Y";
			}else if(check.equals("FILTER")){
				homeproducts = getfiltered_products();
				Main = "N";
			}else {
			
				homeproducts = getfiltered_homeproducts(submenu_ID);
				Main = "N";
			}

			return homeproducts;
		}

		protected void onPostExecute(String result) {

			if (Products_Arraylist != null)
				Products_Arraylist.clear();
			if (Main.equals("Y"))
				parsejson(result);
			else if (Main.equals("N"))
				parsesubjson(result);

			if (Products_Arraylist != null)
				PD_adapt = new ProductsAdapter(HomeActivity.this,
						Products_Arraylist);
			if (Products_Arraylist.size() == 0) {
				noproduct_icon.setVisibility(View.VISIBLE);
				homeproductlist.setVisibility(View.GONE);
			} else
				homeproductlist.setAdapter(PD_adapt);

			if (check.equals("SHOW")||check.equals("FILTER")) {
				navigation_bar.setVisibility(View.VISIBLE);
				filter_Bar.setVisibility(View.VISIBLE);
				maincat_name.setText(menu_name);
				subcat_name.setText(submenu_name);
				if (Cart_Arraylist.size() > 0)
					buttombarAction(Cart_Arraylist, item_total_cost);
			} else {
				navigation_bar.setVisibility(View.GONE);
				filter_Bar.setVisibility(View.GONE);
				if (Cart_Arraylist.size() > 0)
					buttombarAction(Cart_Arraylist, item_total_cost);
			}
			dialog.dismiss();
		}
	}

	ArrayList<Products> parsejson(String homeproducts) {
		try {
			if (Products_Arraylist != null)
				Products_Arraylist.clear();
			if (homeproducts != null && homeproducts.length() > 0) {

				JSONObject jObj_main = new JSONObject(homeproducts);
				if (!jObj_main.getString("success").equals("0")) {
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject cat = result.getJSONObject(i);
							int total = cat.getInt("total_product");
							if (total > 0) {
								JSONArray product = cat
										.getJSONArray("products");
								if (product.length() > 0) {
									for (int j = 0; j < product.length(); j++) {
										item_of_product = new Products();
										JSONObject items = product
												.getJSONObject(j);

										// -------------------------------------item
										// 1-----------------------------------------
										if (!items.getString("item_name")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_1")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name(items.getString(
													"item_name").toString());
											item.setactual_price(items
													.getString("product_mrp_1")
													.toString());
											item.setfinal_price(items
													.getString(
															"product_actual_price_1")
													.toString());
											item.setvaliddiscount(items
													.getString(
															"product_offer_1")
													.toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_wieght(items.getString(
													"product_wt_1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setitem_in_cart("NO");
											item.setitem_qty_count(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_0() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_0()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_0()
																	.getitem_qty_count() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_0()
																			.getitem_in_cart()
																			.equals("YES")) {
																item.setitem_qty_count(Cart_Arraylist
																		.get(c)
																		.getprod_0()
																		.getitem_qty_count());
																item.setitem_in_cart("YES");
															} else {
																item.setitem_qty_count(0);
																item.setitem_in_cart("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count(0);
												item.setitem_in_cart("NO");
											}

											item_of_product
													.setlocation_specific(items
															.getString(
																	"islocspecific")
															.toString());
											item_of_product
													.setlocation_availability(items
															.getString(
																	"loc_available")
															.toString());
											item_of_product
													.setlocation_address(items
															.getString(
																	"loc_specificvalue")
															.toString());
											item_of_product.setprod_0(item);
											item_of_product.setIs_Favorite(items
													.getString(
															"is_fav")
													.toString());
											item_of_product.setisProduct("Y");

										} else
											item_of_product.setisProduct("N");

										// ---------------------------------------item
										// 2--------------------------------------------------------------
										if (!items.getString("item_name1")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_2")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_1(items
													.getString("item_name1")
													.toString());
											item.setactual_price1(items
													.getString("product_mrp_2")
													.toString());
											item.setfinal_price1(items
													.getString(
															"product_actual_price_2")
													.toString());
											item.setvaliddiscount1(items
													.getString(
															"product_offer_2")
													.toString());
											item.setpd_wieght1(items.getString(
													"product_wt_2").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct1("Y");
											item.setitem_in_cart1("NO");
											item.setitem_qty_count1(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_1() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_1()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_1()
																	.getitem_qty_count1() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_1()
																			.getitem_in_cart1()
																			.equals("YES")) {
																item.setitem_qty_count1(Cart_Arraylist
																		.get(c)
																		.getprod_1()
																		.getitem_qty_count1());
																item.setitem_in_cart1("YES");
															} else {
																item.setitem_qty_count1(0);
																item.setitem_in_cart1("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count1(0);
												item.setitem_in_cart1("NO");
											}

											item_of_product.setprod_1(item);
											item_of_product.setisProduct1("Y");
										} else
											item_of_product.setisProduct1("N");
										// ---------------------------------------item
										// 3--------------------------------------------------------------
										if (!items.getString("item_name2")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_3")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_2(items
													.getString("item_name2")
													.toString());
											item.setactual_price2(items
													.getString("product_mrp_3")
													.toString());
											item.setfinal_price2(items
													.getString(
															"product_actual_price_3")
													.toString());
											item.setvaliddiscount2(items
													.getString(
															"product_offer_3")
													.toString());
											item.setpd_wieght2(items.getString(
													"product_wt_3").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct2("Y");
											item.setitem_in_cart2("NO");
											item.setitem_qty_count2(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_2() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_2()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_2()
																	.getitem_qty_count2() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_2()
																			.getitem_in_cart2()
																			.equals("YES")) {
																item.setitem_qty_count2(Cart_Arraylist
																		.get(c)
																		.getprod_2()
																		.getitem_qty_count2());
																item.setitem_in_cart2("YES");
															} else {
																item.setitem_qty_count2(0);
																item.setitem_in_cart2("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count2(0);
												item.setitem_in_cart2("NO");
											}

											item_of_product.setprod_2(item);
											item_of_product.setisProduct2("Y");
										} else
											item_of_product.setisProduct2("N");
										// ---------------------------------------item
										// 4--------------------------------------------------------------
										if (!items.getString("item_name3")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_4")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_3(items
													.getString("item_name3")
													.toString());
											item.setactual_price3(items
													.getString("product_mrp_4")
													.toString());
											item.setfinal_price3(items
													.getString(
															"product_actual_price_4")
													.toString());
											item.setvaliddiscount3(items
													.getString(
															"product_offer_4")
													.toString());
											item.setpd_wieght3(items.getString(
													"product_wt_4").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct3("Y");
											item.setitem_in_cart3("NO");
											item.setitem_qty_count3(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_3() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_3()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_3()
																	.getitem_qty_count3() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_3()
																			.getitem_in_cart3()
																			.equals("YES")) {
																item.setitem_qty_count3(Cart_Arraylist
																		.get(c)
																		.getprod_3()
																		.getitem_qty_count3());
																item.setitem_in_cart3("YES");
															} else {
																item.setitem_qty_count3(0);
																item.setitem_in_cart3("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count3(0);
												item.setitem_in_cart3("NO");
											}

											item_of_product.setprod_3(item);
											item_of_product.setisProduct3("Y");
										} else
											item_of_product.setisProduct3("N");
										// ---------------------------------------item
										// 5--------------------------------------------------------------
										if (!items.getString("item_name4")
												.toString().equals("")
												&& !items
														.getString(
																"product_wt_5")
														.toString().equals("")) {
											item = new Products();
											item.setitem_id(items.getString(
													"item_id").toString());
											item.setitem_name_4(items
													.getString("item_name4")
													.toString());
											item.setactual_price4(items
													.getString("product_mrp_5")
													.toString());
											item.setfinal_price4(items
													.getString(
															"product_actual_price_5")
													.toString());
											item.setvaliddiscount4(items
													.getString(
															"product_offer_5")
													.toString());
											item.setpd_wieght4(items.getString(
													"product_wt_5").toString());
											item.setitem_image(items.getString(
													"medimg1").toString());
											item.setpd_short_description(items
													.getString("small_desc")
													.toString());
											item.setisProduct4("Y");
											item.setitem_in_cart4("NO");
											item.setitem_qty_count4(0);

											if (Cart_Arraylist.size() > 0
													&& Cart_Arraylist != null) {
												for (int c = 0; c < Cart_Arraylist
														.size(); c++) {
													if (Cart_Arraylist.get(c)
															.getprod_4() != null) {
														if (Cart_Arraylist
																.get(c)
																.getprod_4()
																.getitem_id()
																.equals(items
																		.getString(
																				"item_id")
																		.toString())) {
															if (Cart_Arraylist
																	.get(c)
																	.getprod_4()
																	.getitem_qty_count4() > 0
																	&& Cart_Arraylist
																			.get(c)
																			.getprod_4()
																			.getitem_in_cart4()
																			.equals("YES")) {
																item.setitem_qty_count4(Cart_Arraylist
																		.get(c)
																		.getprod_4()
																		.getitem_qty_count4());
																item.setitem_in_cart4("YES");
															} else {
																item.setitem_qty_count4(0);
																item.setitem_in_cart4("NO");
															}
														}
													}
												}
											} else {
												item.setitem_qty_count4(0);
												item.setitem_in_cart4("NO");
											}

											item_of_product.setprod_4(item);
											item_of_product.setisProduct4("Y");
										} else
											item_of_product.setisProduct4("N");
										// ---------------------------------------------end-----------------------------------------------------

										Products_Arraylist.add(item_of_product);
									}// End of for-loop
								}
							}
						}
					}
				}
			}
			return Products_Arraylist;
		} catch (JSONException e) {
			System.out.println(e);
		}
		return null;
	}

	ArrayList<Products> parsesubjson(String homeproducts) {
		try {
			if (Products_Arraylist != null)
				Products_Arraylist.clear();
			if (homeproducts != null && homeproducts.length() > 0) {

				JSONObject jObj_main = new JSONObject(homeproducts);
				if (!jObj_main.getString("success").equals("0")) {
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							item_of_product = new Products();
							JSONObject items = result.getJSONObject(i);
							if (!items.getString("item_name").toString()
									.equals("")
									&& !items.getString("product_wt_1")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name(items.getString("item_name")
										.toString());
								item.setactual_price(items.getString(
										"product_mrp_1").toString());
								item.setfinal_price(items.getString(
										"product_actual_price_1").toString());
								item.setvaliddiscount(items.getString(
										"product_offer_1").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_wieght(items.getString(
										"product_wt_1").toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setitem_in_cart("NO");
								item.setitem_qty_count(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist
												.get(c)
												.getprod_0()
												.getitem_id()
												.equals(items.getString(
														"item_id").toString())) {
											if (Cart_Arraylist.get(c)
													.getprod_0()
													.getitem_qty_count() > 0
													&& Cart_Arraylist.get(c)
															.getprod_0()
															.getitem_in_cart()
															.equals("YES")) {
												item.setitem_qty_count(Cart_Arraylist
														.get(c).getprod_0()
														.getitem_qty_count());
												item.setitem_in_cart("YES");
											} else {
												item.setitem_qty_count(0);
												item.setitem_in_cart("NO");
											}
										}
									}
								} else {
									item.setitem_qty_count(0);
									item.setitem_in_cart("NO");
								}

								item_of_product.setlocation_specific(items
										.getString("islocspecific").toString());
								item_of_product.setlocation_availability(items
										.getString("loc_available").toString());
								item_of_product.setlocation_address(items
										.getString("loc_specificvalue")
										.toString());
								item_of_product.setprod_0(item);
								item_of_product.setIs_Favorite(items
										.getString(
												"is_fav")
										.toString());
								item_of_product.setisProduct("Y");
							} else
								item_of_product.setisProduct("N");

							// -------------------------------------------------------------------------------------------------------
							if (!items.getString("item_name1").toString()
									.equals("")
									&& !items.getString("product_wt_2")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_1(items.getString(
										"item_name1").toString());
								item.setactual_price1(items.getString(
										"product_mrp_2").toString());
								item.setfinal_price1(items.getString(
										"product_actual_price_2").toString());
								item.setvaliddiscount1(items.getString(
										"product_offer_2").toString());
								item.setpd_wieght1(items.getString(
										"product_wt_2").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct1("Y");
								item.setitem_in_cart1("NO");
								item.setitem_qty_count1(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_1() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_1()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_1()
														.getitem_qty_count1() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_1()
																.getitem_in_cart1()
																.equals("YES")) {
													item.setitem_qty_count1(Cart_Arraylist
															.get(c)
															.getprod_1()
															.getitem_qty_count1());
													item.setitem_in_cart1("YES");
												} else {
													item.setitem_qty_count1(0);
													item.setitem_in_cart1("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count1(0);
									item.setitem_in_cart1("NO");
								}

								item_of_product.setprod_1(item);
								item_of_product.setisProduct1("Y");
							} else
								item_of_product.setisProduct1("N");

							if (!items.getString("item_name2").toString()
									.equals("")
									&& !items.getString("product_wt_3")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_2(items.getString(
										"item_name2").toString());
								item.setactual_price2(items.getString(
										"product_mrp_3").toString());
								item.setfinal_price2(items.getString(
										"product_actual_price_3").toString());
								item.setvaliddiscount2(items.getString(
										"product_offer_3").toString());
								item.setpd_wieght2(items.getString(
										"product_wt_3").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct2("Y");
								item.setitem_in_cart2("NO");
								item.setitem_qty_count2(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_2() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_2()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_2()
														.getitem_qty_count2() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_2()
																.getitem_in_cart2()
																.equals("YES")) {
													item.setitem_qty_count2(Cart_Arraylist
															.get(c)
															.getprod_2()
															.getitem_qty_count2());
													item.setitem_in_cart2("YES");
												} else {
													item.setitem_qty_count2(0);
													item.setitem_in_cart2("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count2(0);
									item.setitem_in_cart2("NO");
								}

								item_of_product.setprod_2(item);
								item_of_product.setisProduct2("Y");
							} else
								item_of_product.setisProduct2("N");

							if (!items.getString("item_name3").toString()
									.equals("")
									&& !items.getString("product_wt_4")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_3(items.getString(
										"item_name3").toString());
								item.setactual_price3(items.getString(
										"product_mrp_4").toString());
								item.setfinal_price3(items.getString(
										"product_actual_price_4").toString());
								item.setvaliddiscount3(items.getString(
										"product_offer_4").toString());
								item.setpd_wieght3(items.getString(
										"product_wt_4").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct3("Y");
								item.setitem_in_cart3("NO");
								item.setitem_qty_count3(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_3() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_3()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_3()
														.getitem_qty_count3() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_3()
																.getitem_in_cart3()
																.equals("YES")) {
													item.setitem_qty_count3(Cart_Arraylist
															.get(c)
															.getprod_3()
															.getitem_qty_count3());
													item.setitem_in_cart3("YES");
												} else {
													item.setitem_qty_count3(0);
													item.setitem_in_cart3("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count3(0);
									item.setitem_in_cart3("NO");
								}

								item_of_product.setprod_3(item);
								item_of_product.setisProduct3("Y");
							} else
								item_of_product.setisProduct3("N");

							if (!items.getString("item_name4").toString()
									.equals("")
									&& !items.getString("product_wt_5")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_4(items.getString(
										"item_name4").toString());
								item.setactual_price4(items.getString(
										"product_mrp_5").toString());
								item.setfinal_price4(items.getString(
										"product_actual_price_5").toString());
								item.setvaliddiscount4(items.getString(
										"product_offer_5").toString());
								item.setpd_wieght4(items.getString(
										"product_wt_5").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct4("Y");
								item.setitem_in_cart4("NO");
								item.setitem_qty_count4(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_4() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_4()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_4()
														.getitem_qty_count4() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_4()
																.getitem_in_cart4()
																.equals("YES")) {
													item.setitem_qty_count4(Cart_Arraylist
															.get(c)
															.getprod_4()
															.getitem_qty_count4());
													item.setitem_in_cart4("YES");
												} else {
													item.setitem_qty_count4(0);
													item.setitem_in_cart4("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count4(0);
									item.setitem_in_cart4("NO");
								}

								item_of_product.setprod_4(item);
								item_of_product.setisProduct4("Y");
							} else
								item_of_product.setisProduct4("N");
							// -------------------------------------------------------------------------------------------------------

							Products_Arraylist.add(item_of_product);
						}
					}
				}
			}
			return Products_Arraylist;
		} catch (JSONException e) {
			System.out.println(e);
		}
		return null;
	}

	public class getSearchList extends AsyncTask<Void, Void, Void> {

		// ProgressDialog dialog;
		AnimationDrawable spinner;

		protected void onPreExecute() {
			loader.setVisibility(View.VISIBLE);
			homeproductlist.setVisibility(View.GONE);
			ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
			spinner = (AnimationDrawable) imageView.getBackground();
			spinner.start();
			// dialog = Utils.createProgressDialog(HomeActivity.this);
			// dialog.setCancelable(false);
			// dialog.show();

		}

		protected Void doInBackground(Void... params) {
			String homeproducts = "";

			// while (RS == 1) {
			// if (isCancelled())
			// break;
			// }

			homeproducts = getSearch_Items(S_query);
			Search_Arraylist.clear();
			try {
				if (homeproducts != null && homeproducts.length() > 0) {

					JSONObject jObj_main = new JSONObject(homeproducts);
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							item_of_product = new Products();
							JSONObject items = result.getJSONObject(i);

							// -------------------------------------item
							// 1-----------------------------------------
							if (!items.getString("item_name").toString()
									.equals("")
									&& !items.getString("product_wt_1")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name(items.getString("item_name")
										.toString());
								item.setactual_price(items.getString(
										"product_mrp_1").toString());
								item.setfinal_price(items.getString(
										"product_actual_price_1").toString());
								item.setvaliddiscount(items.getString(
										"product_offer_1").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_wieght(items.getString(
										"product_wt_1").toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setitem_in_cart("NO");
								item.setitem_qty_count(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist
												.get(c)
												.getprod_0()
												.getitem_id()
												.equals(items.getString(
														"item_id").toString())) {
											if (Cart_Arraylist.get(c)
													.getprod_0()
													.getitem_qty_count() > 0
													&& Cart_Arraylist.get(c)
															.getprod_0()
															.getitem_in_cart()
															.equals("YES")) {
												item.setitem_qty_count(Cart_Arraylist
														.get(c).getprod_0()
														.getitem_qty_count());
												item.setitem_in_cart("YES");
											} else {
												item.setitem_qty_count(0);
												item.setitem_in_cart("NO");
											}
										}
									}
								} else {
									item.setitem_qty_count(0);
									item.setitem_in_cart("NO");
								}
								item_of_product.setlocation_specific(items
										.getString("islocspecific").toString());
								item_of_product.setlocation_availability(items
										.getString("loc_available").toString());
								item_of_product.setlocation_address(items
										.getString("loc_specificvalue")
										.toString());
								item_of_product.setprod_0(item);
								item_of_product.setIs_Favorite(items
										.getString(
												"is_fav")
										.toString());
								item_of_product.setisProduct("Y");

							} else
								item_of_product.setisProduct("N");

							// ---------------------------------------item
							// 2--------------------------------------------------------------
							if (!items.getString("item_name1").toString()
									.equals("")
									&& !items.getString("product_wt_2")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_1(items.getString(
										"item_name1").toString());
								item.setactual_price1(items.getString(
										"product_mrp_2").toString());
								item.setfinal_price1(items.getString(
										"product_actual_price_2").toString());
								item.setvaliddiscount1(items.getString(
										"product_offer_2").toString());
								item.setpd_wieght1(items.getString(
										"product_wt_2").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct1("Y");
								item.setitem_in_cart1("NO");
								item.setitem_qty_count1(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_1() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_1()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_1()
														.getitem_qty_count1() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_1()
																.getitem_in_cart1()
																.equals("YES")) {
													item.setitem_qty_count1(Cart_Arraylist
															.get(c)
															.getprod_1()
															.getitem_qty_count1());
													item.setitem_in_cart1("YES");
												} else {
													item.setitem_qty_count1(0);
													item.setitem_in_cart1("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count1(0);
									item.setitem_in_cart1("NO");
								}

								item_of_product.setprod_1(item);
								item_of_product.setisProduct1("Y");
							} else
								item_of_product.setisProduct1("N");
							// ---------------------------------------item
							// 3--------------------------------------------------------------
							if (!items.getString("item_name2").toString()
									.equals("")
									&& !items.getString("product_wt_3")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_2(items.getString(
										"item_name2").toString());
								item.setactual_price2(items.getString(
										"product_mrp_3").toString());
								item.setfinal_price2(items.getString(
										"product_actual_price_3").toString());
								item.setvaliddiscount2(items.getString(
										"product_offer_3").toString());
								item.setpd_wieght2(items.getString(
										"product_wt_3").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct2("Y");
								item.setitem_in_cart2("NO");
								item.setitem_qty_count2(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_2() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_2()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_2()
														.getitem_qty_count2() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_2()
																.getitem_in_cart2()
																.equals("YES")) {
													item.setitem_qty_count2(Cart_Arraylist
															.get(c)
															.getprod_2()
															.getitem_qty_count2());
													item.setitem_in_cart2("YES");
												} else {
													item.setitem_qty_count2(0);
													item.setitem_in_cart2("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count2(0);
									item.setitem_in_cart2("NO");
								}

								item_of_product.setprod_2(item);
								item_of_product.setisProduct2("Y");
							} else
								item_of_product.setisProduct2("N");
							// ---------------------------------------item
							// 4--------------------------------------------------------------
							if (!items.getString("item_name3").toString()
									.equals("")
									&& !items.getString("product_wt_4")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_3(items.getString(
										"item_name3").toString());
								item.setactual_price3(items.getString(
										"product_mrp_4").toString());
								item.setfinal_price3(items.getString(
										"product_actual_price_4").toString());
								item.setvaliddiscount3(items.getString(
										"product_offer_4").toString());
								item.setpd_wieght3(items.getString(
										"product_wt_4").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct3("Y");
								item.setitem_in_cart3("NO");
								item.setitem_qty_count3(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_3() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_3()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_3()
														.getitem_qty_count3() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_3()
																.getitem_in_cart3()
																.equals("YES")) {
													item.setitem_qty_count3(Cart_Arraylist
															.get(c)
															.getprod_3()
															.getitem_qty_count3());
													item.setitem_in_cart3("YES");
												} else {
													item.setitem_qty_count3(0);
													item.setitem_in_cart3("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count3(0);
									item.setitem_in_cart3("NO");
								}

								item_of_product.setprod_3(item);
								item_of_product.setisProduct3("Y");
							} else
								item_of_product.setisProduct3("N");
							// ---------------------------------------item
							// 5--------------------------------------------------------------
							if (!items.getString("item_name4").toString()
									.equals("")
									&& !items.getString("product_wt_5")
											.toString().equals("")) {
								item = new Products();
								item.setitem_id(items.getString("item_id")
										.toString());
								item.setitem_name_4(items.getString(
										"item_name4").toString());
								item.setactual_price4(items.getString(
										"product_mrp_5").toString());
								item.setfinal_price4(items.getString(
										"product_actual_price_5").toString());
								item.setvaliddiscount4(items.getString(
										"product_offer_5").toString());
								item.setpd_wieght4(items.getString(
										"product_wt_5").toString());
								item.setitem_image(items.getString("medimg1")
										.toString());
								item.setpd_short_description(items.getString(
										"small_desc").toString());
								item.setisProduct4("Y");
								item.setitem_in_cart4("NO");
								item.setitem_qty_count4(0);

								if (Cart_Arraylist.size() > 0
										&& Cart_Arraylist != null) {
									for (int c = 0; c < Cart_Arraylist.size(); c++) {
										if (Cart_Arraylist.get(c).getprod_4() != null) {
											if (Cart_Arraylist
													.get(c)
													.getprod_4()
													.getitem_id()
													.equals(items.getString(
															"item_id")
															.toString())) {
												if (Cart_Arraylist.get(c)
														.getprod_4()
														.getitem_qty_count4() > 0
														&& Cart_Arraylist
																.get(c)
																.getprod_4()
																.getitem_in_cart4()
																.equals("YES")) {
													item.setitem_qty_count4(Cart_Arraylist
															.get(c)
															.getprod_4()
															.getitem_qty_count4());
													item.setitem_in_cart4("YES");
												} else {
													item.setitem_qty_count4(0);
													item.setitem_in_cart4("NO");
												}
											}
										}
									}
								} else {
									item.setitem_qty_count4(0);
									item.setitem_in_cart4("NO");
								}

								item_of_product.setprod_4(item);
								item_of_product.setisProduct4("Y");
							} else
								item_of_product.setisProduct4("N");
							// ---------------------------------------------end-----------------------------------------------------

							Search_Arraylist.add(item_of_product);
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			PD_adapt = new ProductsAdapter(HomeActivity.this, Search_Arraylist);
			if (Search_Arraylist.size() == 0) {
				loader.setVisibility(View.GONE);
				noproduct_icon.setVisibility(View.VISIBLE);
				homeproductlist.setVisibility(View.GONE);
			} else {
				loader.setVisibility(View.GONE);
				homeproductlist.setVisibility(View.GONE);
				noproduct_icon.setVisibility(View.GONE);
				homeproductlist.setVisibility(View.VISIBLE);
				homeproductlist.setAdapter(PD_adapt);
			}
			navigation_bar.setVisibility(View.VISIBLE);
			filter_Bar.setVisibility(View.VISIBLE);
			maincat_name.setText("Search Result");
			subcat_name.setText(S_query.toString().replace("%20", " "));
			if (Cart_Arraylist.size() > 0)
				buttombarAction(Cart_Arraylist, item_total_cost);

			spinner.start();
			// dialog.dismiss();
		}
	}

	public static void SlideToAbove() {
		Animation slide = null;

		slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

		slide.setDuration(300);
		slide.setFillAfter(true);
		slide.setFillEnabled(true);
		View_layout.startAnimation(slide);
		chkoutcart_top.startAnimation(slide);
		slide.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				Main_layout.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

				View_layout.clearAnimation();

				chkoutcart_top.clearAnimation();
				// RelativeLayout.LayoutParams lp = new
				// RelativeLayout.LayoutParams(
				// View_layout.getWidth(), View_layout.getHeight());
				// // lp.setMargins(0, 0, 0, 0);
				// lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				// View_layout.setLayoutParams(lp);
			}

		});

	}

	public static void SlideToDown() {
		Animation slide = null;
		slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

		slide.setDuration(1000);
		slide.setFillAfter(true);
		slide.setFillEnabled(true);
		View_layout.startAnimation(slide);
		chkoutcart_top.startAnimation(slide);

		slide.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {

				chkoutcart_top.clearAnimation();
				View_layout.clearAnimation();
				// RelativeLayout.LayoutParams lp = new
				// RelativeLayout.LayoutParams(
				// View_layout.getWidth(), View_layout.getHeight());
				// lp.setMargins(0, View_layout.getWidth(), 0, 0);
				// lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				// View_layout.setLayoutParams(lp);

			}

		});

	}

	static void reset() {
		for (int i = 0; i < Cart_Arraylist.size(); i++) {
			if (Cart_Arraylist.get(i).getprod_0() != null) {
				if (Cart_Arraylist.get(i).getprod_0().getitem_in_cart()
						.equals("YES")
						&& Cart_Arraylist.get(i).getprod_0().getcontrol()
								.equals("D"))
					Cart_Arraylist.get(i).getprod_0().setcontrol("S");
			}
			if (Cart_Arraylist.get(i).getprod_1() != null) {
				if (Cart_Arraylist.get(i).getprod_1().getitem_in_cart1()
						.equals("YES")
						&& Cart_Arraylist.get(i).getprod_1().getcontrol1()
								.equals("D"))
					Cart_Arraylist.get(i).getprod_1().setcontrol1("S");
			}
			if (Cart_Arraylist.get(i).getprod_2() != null) {
				if (Cart_Arraylist.get(i).getprod_2().getitem_in_cart2()
						.equals("YES")
						&& Cart_Arraylist.get(i).getprod_2().getcontrol2()
								.equals("D"))
					Cart_Arraylist.get(i).getprod_2().setcontrol2("S");
			}
			if (Cart_Arraylist.get(i).getprod_3() != null) {
				if (Cart_Arraylist.get(i).getprod_3().getitem_in_cart3()
						.equals("YES")
						&& Cart_Arraylist.get(i).getprod_3().getcontrol3()
								.equals("D"))
					Cart_Arraylist.get(i).getprod_3().setcontrol3("S");
			}
			if (Cart_Arraylist.get(i).getprod_4() != null) {
				if (Cart_Arraylist.get(i).getprod_4().getitem_in_cart4()
						.equals("YES")
						&& Cart_Arraylist.get(i).getprod_4().getcontrol4()
								.equals("D"))
					Cart_Arraylist.get(i).getprod_4().setcontrol4("S");
			}
		}
	}

	public static ArrayList<Products> cartitems(ArrayList<Products> temp) {

		mycartlist = new ArrayList<Products>();
		reset();
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).getprod_0() != null) {
				if (temp.get(i).getprod_0().getitem_in_cart().equals("YES")
						&& temp.get(i).getprod_0().getcontrol().equals("S")) {
					temp.get(i).getprod_0().setcontrol("D");
					Products data = new Products();
					data.setitem_id(temp.get(i).getprod_0().getitem_id());
					data.setitem_name(temp.get(i).getprod_0().getitem_name());
					data.setactual_price(temp.get(i).getprod_0()
							.getactual_price());
					data.setfinal_price(temp.get(i).getprod_0()
							.getfinal_price());
					data.setitem_qty_count(temp.get(i).getprod_0()
							.getitem_qty_count());
					data.setpd_wieght(temp.get(i).getprod_0().getpd_wieght());
					data.setitem_image(temp.get(i).getprod_0().getitem_image());
					mycartlist.add(data);
				}
			}
			if (temp.get(i).getprod_1() != null) {
				if (temp.get(i).getprod_1().getitem_in_cart1().equals("YES")
						&& temp.get(i).getprod_1().getcontrol1().equals("S")) {
					temp.get(i).getprod_1().setcontrol1("D");
					Products data = new Products();
					data.setitem_id(temp.get(i).getprod_1().getitem_id());
					data.setitem_name(temp.get(i).getprod_1().getitem_name_1());
					data.setactual_price(temp.get(i).getprod_1()
							.getactual_price1());
					data.setfinal_price(temp.get(i).getprod_1()
							.getfinal_price1());
					data.setitem_qty_count(temp.get(i).getprod_1()
							.getitem_qty_count1());
					data.setpd_wieght(temp.get(i).getprod_1().getpd_wieght1());
					data.setitem_image(temp.get(i).getprod_1().getitem_image());
					mycartlist.add(data);
				}
			}
			if (temp.get(i).getprod_2() != null) {
				if (temp.get(i).getprod_2().getitem_in_cart2().equals("YES")
						&& temp.get(i).getprod_2().getcontrol2().equals("S")) {
					temp.get(i).getprod_2().setcontrol2("D");
					Products data = new Products();
					data.setitem_id(temp.get(i).getprod_2().getitem_id());
					data.setitem_name(temp.get(i).getprod_2().getitem_name_2());
					data.setactual_price(temp.get(i).getprod_2()
							.getactual_price2());
					data.setfinal_price(temp.get(i).getprod_2()
							.getfinal_price2());
					data.setitem_qty_count(temp.get(i).getprod_2()
							.getitem_qty_count2());
					data.setpd_wieght(temp.get(i).getprod_2().getpd_wieght2());
					data.setitem_image(temp.get(i).getprod_2().getitem_image());
					mycartlist.add(data);
				}
			}
			if (temp.get(i).getprod_3() != null) {
				if (temp.get(i).getprod_3().getitem_in_cart3().equals("YES")
						&& temp.get(i).getprod_3().getcontrol3().equals("S")) {
					temp.get(i).getprod_3().setcontrol3("D");
					Products data = new Products();
					data.setitem_id(temp.get(i).getprod_3().getitem_id());
					data.setitem_name(temp.get(i).getprod_3().getitem_name_3());
					data.setactual_price(temp.get(i).getprod_3()
							.getactual_price3());
					data.setfinal_price(temp.get(i).getprod_3()
							.getfinal_price3());
					data.setitem_qty_count(temp.get(i).getprod_3()
							.getitem_qty_count3());
					data.setpd_wieght(temp.get(i).getprod_3().getpd_wieght3());
					data.setitem_image(temp.get(i).getprod_3().getitem_image());
					mycartlist.add(data);
				}
			}
			if (temp.get(i).getprod_4() != null) {
				if (temp.get(i).getprod_4().getitem_in_cart4().equals("YES")
						&& temp.get(i).getprod_4().getcontrol4().equals("S")) {
					temp.get(i).getprod_4().setcontrol4("D");
					Products data = new Products();
					data.setitem_id(temp.get(i).getprod_4().getitem_id());
					data.setitem_name(temp.get(i).getprod_4().getitem_name_4());
					data.setactual_price(temp.get(i).getprod_4()
							.getactual_price4());
					data.setfinal_price(temp.get(i).getprod_4()
							.getfinal_price4());
					data.setitem_qty_count(temp.get(i).getprod_4()
							.getitem_qty_count4());
					data.setpd_wieght(temp.get(i).getprod_4().getpd_wieght4());
					data.setitem_image(temp.get(i).getprod_4().getitem_image());
					mycartlist.add(data);
				}
			}
		}
		return mycartlist;

	}

	public class ProductsAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Products> data;
		private LayoutInflater inflater = null;

		SharedPreferences sharedPreferences, pref;
		private ImageView imgThumbnail, plus, closeit, minus;
		private TextView prod_name, prod_cost, prod_offer_cost, prod_desc,
				prod_quantity, count, tab1, tab2, tab3, tab4, tab5;
		JSONObject promoJson;
		HashMap<String, String> item = new HashMap<String, String>();
		private Context mContext;
		int value = 0;
		String control = "";
		Dialog dialog;

		public ProductsAdapter(Activity a, ArrayList<Products> d) {
			activity = a;
			data = d;
			this.mContext = a;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			pref = PreferenceManager.getDefaultSharedPreferences(activity);

		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			private ImageView imgThumbnail, plus, minus, more, fav;
			private TextView lblDateEnd, tvmrp, tvoffcost, tvProdName,
					prod_qty, count;
			RelativeLayout view, tick_me;
			// ImageLoader imgLoader;
		}

		public View getView(final int p, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_home, parent,
						false);
				holder = new ViewHolder();
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.view = (RelativeLayout) convertView
					.findViewById(R.id.wrap_rl);
			holder.tick_me = (RelativeLayout) convertView
					.findViewById(R.id.lytTitle);
			holder.lblDateEnd = (TextView) convertView
					.findViewById(R.id.lblEndDate);
			holder.tvProdName = (TextView) convertView
					.findViewById(R.id.lblstore);
			holder.prod_qty = (TextView) convertView.findViewById(R.id.p_qty);
			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);
			holder.plus = (ImageView) convertView.findViewById(R.id.plus);
			holder.minus = (ImageView) convertView.findViewById(R.id.minus);
			holder.fav = (ImageView) convertView.findViewById(R.id.fav);
			holder.more = (ImageView) convertView.findViewById(R.id.more);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.tvmrp = (TextView) convertView.findViewById(R.id.lblAddress);
			holder.tvoffcost = (TextView) convertView
					.findViewById(R.id.prod_off_cost);
			if (pref.getString("user_id", "").equals(""))
				holder.fav.setVisibility(View.GONE);
			else {
				if (data.get(p).getIs_Favorite().equals("1"))
					holder.fav.setVisibility(View.VISIBLE);
				else
					holder.fav.setVisibility(View.GONE);
			}

			if (data.get(p).getisProduct1().equals("Y"))
				holder.more.setVisibility(View.VISIBLE);
			else
				holder.more.setVisibility(View.GONE);

			holder.plus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String GO = "N";
					if (data.get(p).getlocation_specific().equals("1")) {
						if (data.get(p).getlocation_availability().equals("1")) {
							GO = "Y";
						} else if (data.get(p).getlocation_availability()
								.equals("2")) {
							String add = Utils.ADD;
							if (add.matches("(.*)india(.*)"))
								GO = "Y";
							else
								GO = "N";
							// india
						} else if (data.get(p).getlocation_availability()
								.equals("3")) {
							// location specific
							String ad = Utils.ADD;
							String keyword[] = data.get(p)
									.getlocation_address().split("@#");
							System.out.println(data.get(p)
									.getlocation_address());
							for (int i = 0; i < keyword.length; i++) {
								String sub_key[] = keyword[i].toString().split(
										",");
								if (sub_key.length == 3) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())) {
										GO = "Y";
									}
								}
								if (sub_key.length == 4) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())
											&& ad.replaceAll("[0-9]+", "")
													.contains(
															sub_key[1]
																	.toString())) {
										GO = "Y";
									}
								}
							}

						} else {
							alertDialogBuilder
									.setMessage(
											"Item not available for the Shipping region!")
									.setCancelable(false)
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													alertDialog.dismiss();
												}
											});
							alertDialog = alertDialogBuilder.create();
							alertDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialog.show();
						}
					} else
						GO = "Y";

					if (GO.equals("N")) {
						alertDialogBuilder
								.setMessage(
										"Item not available for the Shipping region!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												alertDialog.dismiss();
											}
										});
						alertDialog = alertDialogBuilder.create();
						alertDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertDialog.show();
					} else {

						value = data.get(p).getprod_0().getitem_qty_count();
						if (value >= 0) {
							value = value + 1;
							data.get(p).getprod_0().setitem_qty_count(value);

							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("NO")) {
								data.get(p).getprod_0().setcontrol("S");
								data.get(p).getprod_0().setitem_in_cart("YES");
								Cart_Arraylist.add(data.get(p));
							}
							if (Cart_Arraylist.size() == 0)
								item_total_cost = 0;
							item_total_cost = item_total_cost
									+ Double.parseDouble(data.get(p)
											.getprod_0().getfinal_price());

							for (int i = 0; i < Cart_Arraylist.size(); i++) {
								if (Cart_Arraylist.get(i).getprod_0() != null) {
									if (Cart_Arraylist.get(i).getprod_0()
											.getitem_in_cart().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_0().getcontrol()
													.equals("D")) {
										data.get(i).getprod_0().setcontrol("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_0()
												.getitem_id()
												.equals(data.get(p).getprod_0()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_0()
													.setitem_qty_count(value);
										}
									}
								}

								if (Cart_Arraylist.get(i).getprod_1() != null) {
									if (Cart_Arraylist.get(i).getprod_1()
											.getitem_in_cart1().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_1().getcontrol1()
													.equals("D")) {
										data.get(i).getprod_1()
												.setcontrol1("S");
										if (Cart_Arraylist
												.get(i)
												.getprod_1()
												.getitem_id()
												.equals(data.get(p).getprod_1()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_1()
													.setitem_qty_count1(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_2() != null) {
									if (Cart_Arraylist.get(i).getprod_2()
											.getitem_in_cart2().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_2().getcontrol2()
													.equals("D")) {
										data.get(i).getprod_2()
												.setcontrol2("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_2()
												.getitem_id()
												.equals(data.get(p).getprod_2()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_2()
													.setitem_qty_count2(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_3() != null) {
									if (Cart_Arraylist.get(i).getprod_3()
											.getitem_in_cart3().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_3().getcontrol3()
													.equals("D")) {
										data.get(i).getprod_3()
												.setcontrol3("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_3()
												.getitem_id()
												.equals(data.get(p).getprod_3()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_3()
													.setitem_qty_count3(value);
										}
									}
								}
								if (Cart_Arraylist.get(i).getprod_4() != null) {
									if (Cart_Arraylist.get(i).getprod_4()
											.getitem_in_cart4().equals("YES")
											&& Cart_Arraylist.get(i)
													.getprod_4().getcontrol4()
													.equals("D")) {
										data.get(i).getprod_4()
												.setcontrol4("S");

										if (Cart_Arraylist
												.get(i)
												.getprod_4()
												.getitem_id()
												.equals(data.get(p).getprod_4()
														.getitem_id())) {
											Cart_Arraylist.get(i).getprod_4()
													.setitem_qty_count4(value);
										}
									}
								}

							}
							notifyDataSetChanged();
							((HomeActivity) mContext).buttombarAction(
									Cart_Arraylist, item_total_cost);
						}
					}
				}
			});

			holder.minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					int cnt_val = 0;
					for (int m = 0; m < Cart_Arraylist.size(); m++) {
						if (Cart_Arraylist.get(m).getprod_0().getitem_id()
								.equals(data.get(p).getprod_0().getitem_id()))
							cnt_val = m;
					}
					value = data.get(p).getprod_0().getitem_qty_count();
					if (value > 0) {

						value = value - 1;
						data.get(p).getprod_0().setitem_qty_count(value);
						Cart_Arraylist.get(cnt_val).getprod_0()
								.setitem_qty_count(value);
						if (Cart_Arraylist.size() == 0)
							item_total_cost = 0;
						item_total_cost = item_total_cost
								- Double.parseDouble(data.get(p).getprod_0()
										.getfinal_price());

						if (value == 0) {
							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("YES")) {
								data.get(p).getprod_0().setitem_in_cart("NO");
								Cart_Arraylist.remove(Cart_Arraylist
										.get(cnt_val));
							}
						}
						for (int i = 0; i < Cart_Arraylist.size(); i++) {
							if (Cart_Arraylist.get(i).getprod_0() != null) {
								if (Cart_Arraylist.get(i).getprod_0()
										.getitem_in_cart().equals("YES")
										&& Cart_Arraylist.get(i).getprod_0()
												.getcontrol().equals("D"))
									Cart_Arraylist.get(i).getprod_0()
											.setcontrol("S");
							}
							if (Cart_Arraylist.get(i).getprod_1() != null) {
								if (Cart_Arraylist.get(i).getprod_1()
										.getitem_in_cart1().equals("YES")
										&& Cart_Arraylist.get(i).getprod_1()
												.getcontrol1().equals("D"))
									Cart_Arraylist.get(i).getprod_1()
											.setcontrol1("S");
							}
							if (Cart_Arraylist.get(i).getprod_2() != null) {
								if (Cart_Arraylist.get(i).getprod_2()
										.getitem_in_cart2().equals("YES")
										&& Cart_Arraylist.get(i).getprod_2()
												.getcontrol2().equals("D"))
									Cart_Arraylist.get(i).getprod_2()
											.setcontrol2("S");
							}
							if (Cart_Arraylist.get(i).getprod_3() != null) {
								if (Cart_Arraylist.get(i).getprod_3()
										.getitem_in_cart3().equals("YES")
										&& Cart_Arraylist.get(i).getprod_3()
												.getcontrol3().equals("D"))
									Cart_Arraylist.get(i).getprod_3()
											.setcontrol3("S");
							}
							if (Cart_Arraylist.get(i).getprod_4() != null) {
								if (Cart_Arraylist.get(i).getprod_4()
										.getitem_in_cart4().equals("YES")
										&& Cart_Arraylist.get(i).getprod_4()
												.getcontrol4().equals("D"))
									Cart_Arraylist.get(i).getprod_4()
											.setcontrol4("S");
							}
						}
						notifyDataSetChanged();
						((HomeActivity) mContext).buttombarAction(
								Cart_Arraylist, item_total_cost);
					}

				}
			});

			holder.view.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					dialog = new Dialog(
							HomeActivity.this,
							android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
					dialog.setContentView(R.layout.view_product_details);

					tab1 = (TextView) dialog.findViewById(R.id.product_wt1);
					tab2 = (TextView) dialog.findViewById(R.id.product_wt2);
					tab3 = (TextView) dialog.findViewById(R.id.product_wt3);
					tab4 = (TextView) dialog.findViewById(R.id.product_wt4);
					tab5 = (TextView) dialog.findViewById(R.id.product_wt5);

					prod_name = (TextView) dialog.findViewById(R.id.prod_name);
					prod_cost = (TextView) dialog.findViewById(R.id.prod_cost);
					prod_offer_cost = (TextView) dialog
							.findViewById(R.id.prod_offer_cost);
					prod_desc = (TextView) dialog
							.findViewById(R.id.desc_details);
					prod_quantity = (TextView) dialog
							.findViewById(R.id.prod_quantity);
					imgThumbnail = (ImageView) dialog
							.findViewById(R.id.imgThumbnail);

					closeit = (ImageView) dialog.findViewById(R.id.closeit);
					plus = (ImageView) dialog.findViewById(R.id.plus);
					minus = (ImageView) dialog.findViewById(R.id.minus);
					count = (TextView) dialog.findViewById(R.id.count);
					data.get(p).setcontrol("TAB1");
					control = "TAB1";

					prod_name.setText(data.get(p).getprod_0().getitem_name());
					prod_offer_cost.setText("\u20B9  "
							+ data.get(p).getprod_0().getfinal_price());
					if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
						prod_cost.setVisibility(View.INVISIBLE);
					prod_cost.setText("\u20B9  "
							+ data.get(p).getprod_0().getactual_price());
					prod_cost.setPaintFlags(prod_cost.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					prod_desc.setText(data.get(p).getprod_0()
							.getpd_short_description());
					prod_quantity.setText(data.get(p).getprod_0()
							.getpd_wieght());
					count.setText(Integer.toString(data.get(p).getprod_0()
							.getitem_qty_count()));
					System.out.println(Integer.toString(data.get(p).getprod_0()
							.getitem_qty_count()));

					closeit.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					if (data.get(p).getisProduct1().equals("Y")) {
						tab1.setVisibility(View.VISIBLE);
						tab2.setVisibility(View.VISIBLE);

						if (data.get(p).getisProduct2().equals("Y")) {
							tab3.setVisibility(View.VISIBLE);
							tab3.setText(data.get(p).getprod_2()
									.getpd_wieght2().toString());
							if (data.get(p).getisProduct3().equals("Y")) {
								tab4.setVisibility(View.VISIBLE);
								tab4.setText(data.get(p).getprod_3()
										.getpd_wieght3().toString());
								if (data.get(p).getisProduct4().equals("Y")) {
									tab5.setVisibility(View.VISIBLE);
									tab5.setText(data.get(p).getprod_4()
											.getpd_wieght4().toString());
								} else
									tab5.setVisibility(View.GONE);
							} else
								tab4.setVisibility(View.GONE);
						} else
							tab3.setVisibility(View.GONE);

						tab1.setText(data.get(p).getprod_0().getpd_wieght()
								.toString());
						tab2.setText(data.get(p).getprod_1().getpd_wieght1()
								.toString());

						tab1.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.green_bg_round));
						tab1.setTextColor(getResources().getColor(color.white));

					} else {
						tab1.setVisibility(View.GONE);
						tab2.setVisibility(View.GONE);
					}

					tab1.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab1.setTextColor(getResources().getColor(
									color.white));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_0()
									.getitem_name());
							prod_offer_cost.setText("\u20B9  "
									+ data.get(p).getprod_0().getfinal_price());
							if (!data.get(p).getprod_0().getvaliddiscount()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_0()
													.getactual_price());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_0()
									.getpd_wieght());
							count.setText(Integer.toString(data.get(p)
									.getprod_0().getitem_qty_count()));
							control = "TAB1";
							data.get(p).setcontrol("TAB1");
						}
					});

					tab2.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab2.setTextColor(getResources().getColor(
									color.white));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_1()
									.getitem_name_1());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_1()
													.getfinal_price1());
							if (!data.get(p).getprod_1().getvaliddiscount1()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_1()
											.getactual_price1());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_1()
									.getpd_wieght1());
							count.setText(Integer.toString(data.get(p)
									.getprod_1().getitem_qty_count1()));
							control = "TAB2";
							data.get(p).setcontrol("TAB2");
						}
					});

					tab3.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab3.setTextColor(getResources().getColor(
									color.white));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_2()
									.getitem_name_2());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_2()
													.getfinal_price2());
							if (!data.get(p).getprod_2().getvaliddiscount2()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_2()
											.getactual_price2());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_2()
									.getpd_wieght2());
							count.setText(Integer.toString(data.get(p)
									.getprod_2().getitem_qty_count2()));
							control = "TAB3";
							data.get(p).setcontrol("TAB3");
						}
					});

					tab4.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab4.setTextColor(getResources().getColor(
									color.white));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab5.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_3()
									.getitem_name_3());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_3()
													.getfinal_price3());
							if (!data.get(p).getprod_3().getvaliddiscount3()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_3()
											.getactual_price3());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_3()
									.getpd_wieght3());
							count.setText(Integer.toString(data.get(p)
									.getprod_3().getitem_qty_count3()));
							control = "TAB4";
							data.get(p).setcontrol("TAB4");
						}
					});

					tab5.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							tab1.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab1.setTextColor(getResources().getColor(
									R.color.orange));
							tab2.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab2.setTextColor(getResources().getColor(
									R.color.orange));
							tab5.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_bg_round));
							tab5.setTextColor(getResources().getColor(
									color.white));
							tab4.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab4.setTextColor(getResources().getColor(
									R.color.orange));
							tab3.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.green_round_border));
							tab3.setTextColor(getResources().getColor(
									R.color.orange));

							prod_name.setText(data.get(p).getprod_4()
									.getitem_name_4());
							prod_offer_cost
									.setText("\u20B9  "
											+ data.get(p).getprod_4()
													.getfinal_price4());
							if (!data.get(p).getprod_4().getvaliddiscount4()
									.equals("1"))
								prod_cost.setVisibility(View.INVISIBLE);
							else
								prod_cost.setVisibility(View.VISIBLE);
							prod_cost.setText("\u20B9  "
									+ data.get(p).getprod_4()
											.getactual_price4());
							prod_cost.setPaintFlags(prod_cost.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
							prod_quantity.setText(data.get(p).getprod_4()
									.getpd_wieght4());
							count.setText(Integer.toString(data.get(p)
									.getprod_4().getitem_qty_count4()));
							control = "TAB5";
							data.get(p).setcontrol("TAB5");
						}
					});

					plus.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							if (control.equals("TAB2")) {
								value = data.get(p).getprod_1()
										.getitem_qty_count1();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_1()
											.setitem_qty_count1(value);
									if (data.get(p).getprod_1()
											.getitem_in_cart1().equals("NO")) {
										data.get(p).getprod_1()
												.setitem_in_cart1("YES");
										data.get(p).getprod_1()
												.setcontrol1("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_1()
													.getfinal_price1());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_1() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_1()
													.getitem_in_cart1()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_1()
															.getcontrol1()
															.equals("D"))
												data.get(i).getprod_1()
														.setcontrol1("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}

							} else if (control.equals("TAB3")) {

								value = data.get(p).getprod_2()
										.getitem_qty_count2();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_2()
											.setitem_qty_count2(value);
									if (data.get(p).getprod_2()
											.getitem_in_cart2().equals("NO")) {
										data.get(p).getprod_2()
												.setitem_in_cart2("YES");
										data.get(p).getprod_2()
												.setcontrol2("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_2()
													.getfinal_price2());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_2() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_2()
													.getitem_in_cart2()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_2()
															.getcontrol2()
															.equals("D"))
												data.get(i).getprod_2()
														.setcontrol2("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB4")) {
								value = data.get(p).getprod_3()
										.getitem_qty_count3();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_3()
											.setitem_qty_count3(value);
									if (data.get(p).getprod_3()
											.getitem_in_cart3().equals("NO")) {
										data.get(p).getprod_3()
												.setitem_in_cart3("YES");
										data.get(p).getprod_3()
												.setcontrol3("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_3()
													.getfinal_price3());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_3() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_3()
													.getitem_in_cart3()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_3()
															.getcontrol3()
															.equals("D"))
												data.get(i).getprod_3()
														.setcontrol3("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB5")) {
								value = data.get(p).getprod_4()
										.getitem_qty_count4();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_4()
											.setitem_qty_count4(value);
									if (data.get(p).getprod_4()
											.getitem_in_cart4().equals("NO")) {
										data.get(p).getprod_4()
												.setitem_in_cart4("YES");
										data.get(p).getprod_4()
												.setcontrol4("S");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_4()
													.getfinal_price4());

									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_4() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_4()
													.getitem_in_cart4()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_4()
															.getcontrol4()
															.equals("D"))
												data.get(i).getprod_4()
														.setcontrol4("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							} else if (control.equals("TAB1")
									|| control.equals("")) {
								value = data.get(p).getprod_0()
										.getitem_qty_count();
								if (value >= 0) {

									value = value + 1;
									data.get(p).getprod_0()
											.setitem_qty_count(value);
									if (data.get(p).getprod_0()
											.getitem_in_cart().equals("NO")) {

										data.get(p).getprod_0().setcontrol("S");
										data.get(p).getprod_0()
												.setitem_in_cart("YES");
										Cart_Arraylist.add(data.get(p));
									}

									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_0()
													.getfinal_price());
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getcontrol() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_0()
													.getitem_in_cart()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_0()
															.getcontrol()
															.equals("D"))
												data.get(i).getprod_0()
														.setcontrol("S");
										}
									}
									notifyDataSetChanged();
									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);
									count.setText(Integer.toString(value));
								}
							}
						}
					});

					minus.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							if (control.equals("TAB2")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_1() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_1()
												.getitem_id()
												.equals(data.get(p).getprod_1()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_1()
										.getitem_qty_count1();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_1()
											.setitem_qty_count1(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count1(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_1()
													.getfinal_price1());
									if (value == 0) {
										if (data.get(p).getprod_1()
												.getitem_in_cart1()
												.equals("YES")) {
											data.get(p).getprod_1()
													.setitem_in_cart1("NO");

											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_1() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_1()
													.getitem_in_cart1()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_1()
															.getcontrol1()
															.equals("D"))
												data.get(i).getprod_1()
														.setcontrol1("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB3")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_2() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_2()
												.getitem_id()
												.equals(data.get(p).getprod_2()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_2()
										.getitem_qty_count2();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_2()
											.setitem_qty_count2(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count2(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_2()
													.getfinal_price2());
									if (value == 0) {
										if (data.get(p).getprod_2()
												.getitem_in_cart2()
												.equals("YES")) {
											data.get(p).getprod_2()
													.setitem_in_cart2("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_2() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_2()
													.getitem_in_cart2()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_2()
															.getcontrol2()
															.equals("D"))
												data.get(i).getprod_2()
														.setcontrol2("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB4")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_3() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_3()
												.getitem_id()
												.equals(data.get(p).getprod_3()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_3()
										.getitem_qty_count3();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_3()
											.setitem_qty_count3(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count3(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_3()
													.getfinal_price3());
									if (value == 0) {
										if (data.get(p).getprod_3()
												.getitem_in_cart3()
												.equals("YES")) {
											data.get(p).getprod_3()
													.setitem_in_cart3("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_3() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_3()
													.getitem_in_cart3()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_3()
															.getcontrol3()
															.equals("D"))
												data.get(i).getprod_3()
														.setcontrol3("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB5")) {
								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist.get(m).getprod_4() != null) {
										if (Cart_Arraylist
												.get(m)
												.getprod_4()
												.getitem_id()
												.equals(data.get(p).getprod_4()
														.getitem_id()))
											cnt_val = m;
									}
								}
								value = data.get(p).getprod_4()
										.getitem_qty_count4();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_4()
											.setitem_qty_count4(value);
									Cart_Arraylist.get(cnt_val)
											.setitem_qty_count4(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_4()
													.getfinal_price4());
									if (value == 0) {
										if (data.get(p).getprod_4()
												.getitem_in_cart4()
												.equals("YES")) {
											data.get(p).getprod_4()
													.setitem_in_cart4("NO");
											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));
									for (int i = 0; i < Cart_Arraylist.size(); i++) {
										if (Cart_Arraylist.get(i).getprod_4() != null) {
											if (Cart_Arraylist.get(i)
													.getprod_4()
													.getitem_in_cart4()
													.equals("YES")
													&& Cart_Arraylist.get(i)
															.getprod_4()
															.getcontrol4()
															.equals("D"))
												data.get(i).getprod_4()
														.setcontrol4("S");
										}
									}
									notifyDataSetChanged();
								}
							} else if (control.equals("TAB1")
									|| control.equals("")) {

								int cnt_val = 0;
								for (int m = 0; m < Cart_Arraylist.size(); m++) {
									if (Cart_Arraylist
											.get(m)
											.getprod_0()
											.getitem_id()
											.equals(data.get(p).getprod_0()
													.getitem_id()))
										cnt_val = m;
								}
								value = data.get(p).getprod_0()
										.getitem_qty_count();
								if (value > 0) {

									value = value - 1;
									data.get(p).getprod_0()
											.setitem_qty_count(value);
									Cart_Arraylist.get(cnt_val).getprod_0()
											.setitem_qty_count(value);
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											- Double.parseDouble(data.get(p)
													.getprod_0()
													.getfinal_price());
									if (value == 0) {
										if (data.get(p).getprod_0()
												.getitem_in_cart()
												.equals("YES")) {
											data.get(p).getprod_0()
													.setitem_in_cart("NO");

											Cart_Arraylist
													.remove(Cart_Arraylist
															.get(cnt_val));
											dialog.dismiss();
										}
									}

									((HomeActivity) mContext).buttombarAction(
											Cart_Arraylist, item_total_cost);

									count.setText(Integer.toString(value));

									notifyDataSetChanged();
								}
							}
						}
					});

					if (data.get(p).getcontrol() != null && count != null) {
						if (data.get(p).getcontrol().equals("TAB2")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count1()));

						} else if (data.get(p).getcontrol().equals("TAB3")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count2()));
						} else if (data.get(p).getcontrol().equals("TAB4")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count3()));
						} else if (data.get(p).getcontrol().equals("TAB5")) {
							count.setText(Integer.toString(data.get(p)
									.getitem_qty_count4()));
						} else if (data.get(p).getcontrol().equals("TAB1")) {
							count.setText(Integer.toString(data.get(p)
									.getprod_0().getitem_qty_count()));
						}
					}

					if (data.get(p).getprod_0().getitem_image() != null
							&& !data.get(p).getprod_0().getitem_image()
									.equals(""))
						Picasso.with(HomeActivity.this)
								.load(data.get(p).getprod_0().getitem_image())
								.fit().centerInside().tag(HomeActivity.this)
								.into(imgThumbnail);

					String GO = "N";
					if (data.get(p).getlocation_specific().equals("1")) {
						if (data.get(p).getlocation_availability().equals("1")) {
							GO = "Y";
						} else if (data.get(p).getlocation_availability()
								.equals("2")) {
							String add = Utils.ADD;
							if (add.matches("(.*)india(.*)"))
								GO = "Y";
							else
								GO = "N";
							// india
						} else if (data.get(p).getlocation_availability()
								.equals("3")) {
							// location specific
							String ad = Utils.ADD;
							String keyword[] = data.get(p)
									.getlocation_address().split("@#");
							System.out.println(data.get(p)
									.getlocation_address());
							for (int i = 0; i < keyword.length; i++) {
								String sub_key[] = keyword[i].toString().split(
										",");
								if (sub_key.length == 3) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())) {
										GO = "Y";
									}
								}
								if (sub_key.length == 4) {
									if (ad.replaceAll("[0-9]+", "").contains(
											sub_key[0].toString())
											&& ad.replaceAll("[0-9]+", "")
													.contains(
															sub_key[1]
																	.toString())) {
										GO = "Y";
									}
								}
							}

						} else {
							alertDialogBuilder
									.setMessage(
											"Item not available for the Shipping region!")
									.setCancelable(false)
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													alertDialog.dismiss();
												}
											});
							alertDialog = alertDialogBuilder.create();
							alertDialog
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialog.show();
						}
					} else
						GO = "Y";

					if (GO.equals("N")) {
						alertDialogBuilder
								.setMessage(
										"Item not available for the Shipping region!")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												alertDialog.dismiss();
											}
										});
						alertDialog = alertDialogBuilder.create();
						alertDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alertDialog.show();
					} else {

						dialog.show();
					}

				}
			});

			if (data.get(p).getisProduct().equals("Y"))
				if (data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct1().equals("Y"))
				if (data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct2().equals("Y"))
				if (data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct3().equals("Y"))
				if (data.get(p).getprod_3().getitem_qty_count3() == 0
						&& data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);
			if (data.get(p).getisProduct4().equals("Y"))
				if (data.get(p).getprod_4().getitem_qty_count4() == 0
						&& data.get(p).getprod_3().getitem_qty_count3() == 0
						&& data.get(p).getprod_2().getitem_qty_count2() == 0
						&& data.get(p).getprod_1().getitem_qty_count1() == 0
						&& data.get(p).getprod_0().getitem_qty_count() == 0)
					holder.tick_me.setVisibility(View.GONE);
				else
					holder.tick_me.setVisibility(View.VISIBLE);

			holder.prod_qty.setText(data.get(p).getprod_0().getpd_wieght());
			holder.tvProdName.setText(data.get(p).getprod_0().getitem_name());
			holder.tvoffcost.setText("\u20B9  "
					+ data.get(p).getprod_0().getfinal_price());

			if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
				holder.tvmrp.setVisibility(View.INVISIBLE);
			else
				holder.tvmrp.setVisibility(View.VISIBLE);
			holder.tvmrp.setText("\u20B9  "
					+ data.get(p).getprod_0().getactual_price());
			holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);

			holder.count.setText(Integer.toString(data.get(p).getprod_0()
					.getitem_qty_count()));
			if (data.get(p).getprod_0().getitem_image() == null
					|| data.get(p).getprod_0().getitem_image().equals(""))
				holder.imgThumbnail.setImageResource(R.drawable.no_image);
			else
				Picasso.with(activity)
						.load(data.get(p).getprod_0().getitem_image()).fit()
						.centerCrop().tag(activity).into(holder.imgThumbnail);
			// {
			// holder.imgLoader = ImageLoader.getInstance();
			// holder.imgLoader.displayImage(data.get(p).getitem_image(),
			// holder.imgThumbnail);
			// }
			// holder.lblDateEnd.setText("  " + item.get(""));

			return convertView;
		}
	}

	public class CartAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Products> data;
		private LayoutInflater inflater = null;

		SharedPreferences sharedPreferences;
		private ImageView imgThumbnail, plus, closeit, minus;
		private TextView prod_name, prod_cost, prod_offer_cost, prod_desc,
				prod_quantity, count, tab1, tab2, tab3, tab4, tab5;
		JSONObject promoJson;
		HashMap<String, String> item = new HashMap<String, String>();
		private Context mContext;
		int value = 0;
		String control = "";

		public CartAdapter(Activity a, ArrayList<Products> d) {
			activity = a;
			data = d;
			this.mContext = a;
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(activity);

		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			private ImageView imgThumbnail, plus, minus, fav;
			private TextView lblDateEnd, tvmrp, tvoffcost, tvProdName,
					prod_qty, count;
			RelativeLayout view;
			// ImageLoader imgLoader;
		}

		public View getView(final int p, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.adapter_home, parent,
						false);
				holder = new ViewHolder();
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.view = (RelativeLayout) convertView
					.findViewById(R.id.wrap_rl);
			holder.lblDateEnd = (TextView) convertView
					.findViewById(R.id.lblEndDate);
			holder.tvProdName = (TextView) convertView
					.findViewById(R.id.lblstore);
			holder.prod_qty = (TextView) convertView.findViewById(R.id.p_qty);
			holder.imgThumbnail = (ImageView) convertView
					.findViewById(R.id.imgThumbnail);
			holder.plus = (ImageView) convertView.findViewById(R.id.plus);
			holder.minus = (ImageView) convertView.findViewById(R.id.minus);
			holder.fav = (ImageView) convertView.findViewById(R.id.fav);

			holder.count = (TextView) convertView.findViewById(R.id.count);
			holder.tvmrp = (TextView) convertView.findViewById(R.id.lblAddress);
			holder.tvoffcost = (TextView) convertView
					.findViewById(R.id.prod_off_cost);

			holder.fav.setVisibility(View.VISIBLE);
			holder.fav.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					favorite(data.get(p).getprod_0().getitem_id());
				}
			});

			holder.plus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (data.get(p).getprod_0() != null) {
						if (data.get(p).getprod_0().getitem_in_cart()
								.equals("YES")) {
							if (data.get(p)
									.getprod_0()
									.getpd_wieght()
									.replaceAll("[^0-9]", "")
									.equals(holder.prod_qty.getText()
											.toString()
											.replaceAll("[^0-9]", ""))) {
								value = data.get(p).getprod_0()
										.getitem_qty_count();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_0()
											.setitem_qty_count(value);

									if (data.get(p).getprod_0()
											.getitem_in_cart().equals("NO")) {
										data.get(p).getprod_0()
												.setitem_in_cart("YES");
										Cart_Arraylist.add(data.get(p));
									}
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_0()
													.getfinal_price());
								}
								for (int i = 0; i < Cart_Arraylist.size(); i++) {
									if (Cart_Arraylist.get(i).getprod_0() != null) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getitem_in_cart()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_0()
														.getcontrol()
														.equals("D")) {
											data.get(i).getprod_0()
													.setcontrol("S");

										}
									}

									if (Cart_Arraylist.get(i).getprod_1() != null) {
										if (Cart_Arraylist.get(i).getprod_1()
												.getitem_in_cart1()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_1()
														.getcontrol1()
														.equals("D")) {
											data.get(i).getprod_1()
													.setcontrol1("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_2() != null) {
										if (Cart_Arraylist.get(i).getprod_2()
												.getitem_in_cart2()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_2()
														.getcontrol2()
														.equals("D")) {
											data.get(i).getprod_2()
													.setcontrol2("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_3() != null) {
										if (Cart_Arraylist.get(i).getprod_3()
												.getitem_in_cart3()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_3()
														.getcontrol3()
														.equals("D")) {
											data.get(i).getprod_3()
													.setcontrol3("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_4() != null) {
										if (Cart_Arraylist.get(i).getprod_4()
												.getitem_in_cart4()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_4()
														.getcontrol4()
														.equals("D")) {
											data.get(i).getprod_4()
													.setcontrol4("S");

										}
									}
								}
								notifyDataSetChanged();
								((HomeActivity) mContext).buttombarAction(
										Cart_Arraylist, item_total_cost);
							}
						}
					}// Product 1
					if (data.get(p).getprod_1() != null) {
						if (data.get(p).getprod_1().getitem_in_cart1()
								.equals("YES")) {
							if (data.get(p)
									.getprod_1()
									.getpd_wieght1()
									.replaceAll("[^0-9]", "")
									.equals(holder.prod_qty.getText()
											.toString()
											.replaceAll("[^0-9]", ""))) {
								value = data.get(p).getprod_1()
										.getitem_qty_count1();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_1()
											.setitem_qty_count1(value);
									if (data.get(p).getprod_1()
											.getitem_in_cart1().equals("NO")) {
										data.get(p).getprod_1()
												.setitem_in_cart1("YES");
										Cart_Arraylist.add(data.get(p));
									}
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_1()
													.getfinal_price1());
								}
								for (int i = 0; i < Cart_Arraylist.size(); i++) {
									if (Cart_Arraylist.get(i).getprod_0() != null) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getitem_in_cart()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_0()
														.getcontrol()
														.equals("D")) {
											data.get(i).getprod_0()
													.setcontrol("S");
										}
									}

									if (Cart_Arraylist.get(i).getprod_1() != null) {
										if (Cart_Arraylist.get(i).getprod_1()
												.getitem_in_cart1()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_1()
														.getcontrol1()
														.equals("D")) {
											data.get(i).getprod_1()
													.setcontrol1("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_2() != null) {
										if (Cart_Arraylist.get(i).getprod_2()
												.getitem_in_cart2()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_2()
														.getcontrol2()
														.equals("D")) {
											data.get(i).getprod_2()
													.setcontrol2("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_3() != null) {
										if (Cart_Arraylist.get(i).getprod_3()
												.getitem_in_cart3()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_3()
														.getcontrol3()
														.equals("D")) {
											data.get(i).getprod_3()
													.setcontrol3("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_4() != null) {
										if (Cart_Arraylist.get(i).getprod_4()
												.getitem_in_cart4()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_4()
														.getcontrol4()
														.equals("D")) {
											data.get(i).getprod_4()
													.setcontrol4("S");

										}
									}

								}
								notifyDataSetChanged();
								((HomeActivity) mContext).buttombarAction(
										Cart_Arraylist, item_total_cost);
							}
						}
					}// product 2
					if (data.get(p).getprod_2() != null) {
						if (data.get(p).getprod_2().getitem_in_cart2()
								.equals("YES")) {
							if (data.get(p)
									.getprod_2()
									.getpd_wieght2()
									.replaceAll("[^0-9]", "")
									.equals(holder.prod_qty.getText()
											.toString()
											.replaceAll("[^0-9]", ""))) {
								value = data.get(p).getprod_2()
										.getitem_qty_count2();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_2()
											.setitem_qty_count2(value);
									if (data.get(p).getprod_2()
											.getitem_in_cart2().equals("NO")) {
										data.get(p).getprod_2()
												.setitem_in_cart2("YES");
										Cart_Arraylist.add(data.get(p));
									}
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_2()
													.getfinal_price2());
								}
								for (int i = 0; i < Cart_Arraylist.size(); i++) {
									if (Cart_Arraylist.get(i).getprod_0() != null) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getitem_in_cart()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_0()
														.getcontrol()
														.equals("D")) {
											data.get(i).getprod_0()
													.setcontrol("S");

										}
									}

									if (Cart_Arraylist.get(i).getprod_1() != null) {
										if (Cart_Arraylist.get(i).getprod_1()
												.getitem_in_cart1()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_1()
														.getcontrol1()
														.equals("D")) {
											data.get(i).getprod_1()
													.setcontrol1("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_2() != null) {
										if (Cart_Arraylist.get(i).getprod_2()
												.getitem_in_cart2()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_2()
														.getcontrol2()
														.equals("D")) {
											data.get(i).getprod_2()
													.setcontrol2("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_3() != null) {
										if (Cart_Arraylist.get(i).getprod_3()
												.getitem_in_cart3()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_3()
														.getcontrol3()
														.equals("D")) {
											data.get(i).getprod_3()
													.setcontrol3("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_4() != null) {
										if (Cart_Arraylist.get(i).getprod_4()
												.getitem_in_cart4()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_4()
														.getcontrol4()
														.equals("D")) {
											data.get(i).getprod_4()
													.setcontrol4("S");

										}
									}
								}
								notifyDataSetChanged();
								((HomeActivity) mContext).buttombarAction(
										Cart_Arraylist, item_total_cost);
							}
						}
					}// product 3
					if (data.get(p).getprod_3() != null) {
						if (data.get(p).getprod_3().getitem_in_cart3()
								.equals("YES")) {
							if (data.get(p)
									.getprod_3()
									.getpd_wieght3()
									.replaceAll("[^0-9]", "")
									.equals(holder.prod_qty.getText()
											.toString()
											.replaceAll("[^0-9]", ""))) {
								value = data.get(p).getprod_3()
										.getitem_qty_count3();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_3()
											.setitem_qty_count3(value);
									if (data.get(p).getprod_3()
											.getitem_in_cart3().equals("NO")) {
										data.get(p).getprod_3()
												.setitem_in_cart3("YES");
										Cart_Arraylist.add(data.get(p));
									}
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_3()
													.getfinal_price3());
								}
								for (int i = 0; i < Cart_Arraylist.size(); i++) {
									if (Cart_Arraylist.get(i).getprod_0() != null) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getitem_in_cart()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_0()
														.getcontrol()
														.equals("D")) {
											data.get(i).getprod_0()
													.setcontrol("S");

										}
									}

									if (Cart_Arraylist.get(i).getprod_1() != null) {
										if (Cart_Arraylist.get(i).getprod_1()
												.getitem_in_cart1()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_1()
														.getcontrol1()
														.equals("D")) {
											data.get(i).getprod_1()
													.setcontrol1("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_2() != null) {
										if (Cart_Arraylist.get(i).getprod_2()
												.getitem_in_cart2()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_2()
														.getcontrol2()
														.equals("D")) {
											data.get(i).getprod_2()
													.setcontrol2("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_3() != null) {
										if (Cart_Arraylist.get(i).getprod_3()
												.getitem_in_cart3()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_3()
														.getcontrol3()
														.equals("D")) {
											data.get(i).getprod_3()
													.setcontrol3("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_4() != null) {
										if (Cart_Arraylist.get(i).getprod_4()
												.getitem_in_cart4()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_4()
														.getcontrol4()
														.equals("D")) {
											data.get(i).getprod_4()
													.setcontrol4("S");

										}
									}
								}
								notifyDataSetChanged();
								((HomeActivity) mContext).buttombarAction(
										Cart_Arraylist, item_total_cost);
							}
						}
					}// product 4
					if (data.get(p).getprod_4() != null) {
						if (data.get(p).getprod_4().getitem_in_cart4()
								.equals("YES")) {
							if (data.get(p)
									.getprod_4()
									.getpd_wieght4()
									.replaceAll("[^0-9]", "")
									.equals(holder.prod_qty.getText()
											.toString()
											.replaceAll("[^0-9]", ""))) {
								value = data.get(p).getprod_4()
										.getitem_qty_count4();
								if (value >= 0) {
									value = value + 1;
									data.get(p).getprod_4()
											.setitem_qty_count4(value);
									if (data.get(p).getprod_4()
											.getitem_in_cart4().equals("NO")) {
										data.get(p).getprod_4()
												.setitem_in_cart4("YES");
										Cart_Arraylist.add(data.get(p));
									}
									if (Cart_Arraylist.size() == 0)
										item_total_cost = 0;
									item_total_cost = item_total_cost
											+ Double.parseDouble(data.get(p)
													.getprod_4()
													.getfinal_price4());
								}
								for (int i = 0; i < Cart_Arraylist.size(); i++) {
									if (Cart_Arraylist.get(i).getprod_0() != null) {
										if (Cart_Arraylist.get(i).getprod_0()
												.getitem_in_cart()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_0()
														.getcontrol()
														.equals("D")) {
											data.get(i).getprod_0()
													.setcontrol("S");

										}
									}

									if (Cart_Arraylist.get(i).getprod_1() != null) {
										if (Cart_Arraylist.get(i).getprod_1()
												.getitem_in_cart1()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_1()
														.getcontrol1()
														.equals("D")) {
											data.get(i).getprod_1()
													.setcontrol1("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_2() != null) {
										if (Cart_Arraylist.get(i).getprod_2()
												.getitem_in_cart2()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_2()
														.getcontrol2()
														.equals("D")) {
											data.get(i).getprod_2()
													.setcontrol2("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_3() != null) {
										if (Cart_Arraylist.get(i).getprod_3()
												.getitem_in_cart3()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_3()
														.getcontrol3()
														.equals("D")) {
											data.get(i).getprod_3()
													.setcontrol3("S");

										}
									}
									if (Cart_Arraylist.get(i).getprod_4() != null) {
										if (Cart_Arraylist.get(i).getprod_4()
												.getitem_in_cart4()
												.equals("YES")
												&& Cart_Arraylist.get(i)
														.getprod_4()
														.getcontrol4()
														.equals("D")) {
											data.get(i).getprod_4()
													.setcontrol4("S");

										}
									}
								}
								notifyDataSetChanged();
								((HomeActivity) mContext).buttombarAction(
										Cart_Arraylist, item_total_cost);
							}
						}
					}// product 5
					notifyDataSetChanged();
					((HomeActivity) mContext).buttombarAction(Cart_Arraylist,
							item_total_cost);
				}
			});

			holder.minus.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					while (true) {
						if (data.get(p).getprod_0() != null) {
							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("YES")) {
								if (data.get(p)
										.getprod_0()
										.getpd_wieght()
										.replaceAll("[^0-9]", "")
										.equals(holder.prod_qty.getText()
												.toString()
												.replaceAll("[^0-9]", ""))) {
									int cnt_val = 0;
									for (int m = 0; m < Cart_Arraylist.size(); m++) {
										if (Cart_Arraylist.get(m).getprod_0() != null)
											if (Cart_Arraylist
													.get(m)
													.getprod_0()
													.getitem_id()
													.equals(data.get(p)
															.getprod_0()
															.getitem_id()))
												cnt_val = m;
									}
									value = data.get(p).getprod_0()
											.getitem_qty_count();
									if (value > 0) {

										value = value - 1;
										data.get(p).getprod_0()
												.setitem_qty_count(value);
										Cart_Arraylist.get(cnt_val).getprod_0()
												.setitem_qty_count(value);
										if (Cart_Arraylist.size() == 0)
											item_total_cost = 0;
										item_total_cost = item_total_cost
												- Double.parseDouble(data
														.get(p).getprod_0()
														.getfinal_price());

										if (value == 0) {
											if (data.get(p).getprod_0()
													.getitem_in_cart()
													.equals("YES")) {
												data.get(p).getprod_0()
														.setitem_in_cart("NO");
												Cart_Arraylist
														.remove(Cart_Arraylist
																.get(cnt_val));
												notifyDataSetChanged();
												((HomeActivity) mContext)
														.buttombarAction(
																Cart_Arraylist,
																item_total_cost);
												break;
											}
										}
									}
								}
							}
						}// product 1
						if (data.get(p).getprod_1() != null) {
							if (data.get(p).getprod_1().getitem_in_cart1()
									.equals("YES")) {
								if (data.get(p)
										.getprod_1()
										.getpd_wieght1()
										.replaceAll("[^0-9]", "")
										.equals(holder.prod_qty.getText()
												.toString()
												.replaceAll("[^0-9]", ""))) {
									int cnt_val = 0;
									for (int m = 0; m < Cart_Arraylist.size(); m++) {
										if (Cart_Arraylist.get(m).getprod_1() != null)
											if (Cart_Arraylist
													.get(m)
													.getprod_1()
													.getitem_id()
													.equals(data.get(p)
															.getprod_1()
															.getitem_id()))
												cnt_val = m;
									}
									value = data.get(p).getprod_1()
											.getitem_qty_count1();
									if (value > 0) {

										value = value - 1;
										data.get(p).getprod_1()
												.setitem_qty_count1(value);
										Cart_Arraylist.get(cnt_val).getprod_1()
												.setitem_qty_count1(value);
										if (Cart_Arraylist.size() == 0)
											item_total_cost = 0;
										item_total_cost = item_total_cost
												- Double.parseDouble(data
														.get(p).getprod_1()
														.getfinal_price1());

										if (value == 0) {
											if (data.get(p).getprod_1()
													.getitem_in_cart1()
													.equals("YES")) {
												data.get(p).getprod_1()
														.setitem_in_cart1("NO");
												Cart_Arraylist
														.remove(Cart_Arraylist
																.get(cnt_val));
												notifyDataSetChanged();
												((HomeActivity) mContext)
														.buttombarAction(
																Cart_Arraylist,
																item_total_cost);
												break;
											}
										}
									}
								}
							}
						}// product 2
						if (data.get(p).getprod_2() != null) {
							if (data.get(p).getprod_2().getitem_in_cart2()
									.equals("YES")) {
								if (data.get(p)
										.getprod_2()
										.getpd_wieght2()
										.replaceAll("[^0-9]", "")
										.equals(holder.prod_qty.getText()
												.toString()
												.replaceAll("[^0-9]", ""))) {
									int cnt_val = 0;
									for (int m = 0; m < Cart_Arraylist.size(); m++) {
										if (Cart_Arraylist.get(m).getprod_2() != null)
											if (Cart_Arraylist
													.get(m)
													.getprod_2()
													.getitem_id()
													.equals(data.get(p)
															.getprod_2()
															.getitem_id()))
												cnt_val = m;
									}
									value = data.get(p).getprod_2()
											.getitem_qty_count2();
									if (value > 0) {

										value = value - 1;
										data.get(p).getprod_2()
												.setitem_qty_count2(value);
										Cart_Arraylist.get(cnt_val).getprod_2()
												.setitem_qty_count2(value);
										if (Cart_Arraylist.size() == 0)
											item_total_cost = 0;
										item_total_cost = item_total_cost
												- Double.parseDouble(data
														.get(p).getprod_2()
														.getfinal_price2());

										if (value == 0) {
											if (data.get(p).getprod_2()
													.getitem_in_cart2()
													.equals("YES")) {
												data.get(p).getprod_2()
														.setitem_in_cart2("NO");
												Cart_Arraylist
														.remove(Cart_Arraylist
																.get(cnt_val));
												notifyDataSetChanged();
												((HomeActivity) mContext)
														.buttombarAction(
																Cart_Arraylist,
																item_total_cost);
												break;
											}
										}
									}
								}
							}
						}// product 3
						if (data.get(p).getprod_3() != null) {
							if (data.get(p).getprod_3().getitem_in_cart3()
									.equals("YES")) {
								if (data.get(p)
										.getprod_3()
										.getpd_wieght3()
										.replaceAll("[^0-9]", "")
										.equals(holder.prod_qty.getText()
												.toString()
												.replaceAll("[^0-9]", ""))) {
									int cnt_val = 0;
									for (int m = 0; m < Cart_Arraylist.size(); m++) {
										if (Cart_Arraylist.get(m).getprod_3() != null)
											if (Cart_Arraylist
													.get(m)
													.getprod_3()
													.getitem_id()
													.equals(data.get(p)
															.getprod_3()
															.getitem_id()))
												cnt_val = m;
									}
									value = data.get(p).getprod_3()
											.getitem_qty_count3();
									if (value > 0) {

										value = value - 1;
										data.get(p).getprod_3()
												.setitem_qty_count3(value);
										Cart_Arraylist.get(cnt_val).getprod_3()
												.setitem_qty_count3(value);
										if (Cart_Arraylist.size() == 0)
											item_total_cost = 0;
										item_total_cost = item_total_cost
												- Double.parseDouble(data
														.get(p).getprod_3()
														.getfinal_price3());

										if (value == 0) {
											if (data.get(p).getprod_3()
													.getitem_in_cart3()
													.equals("YES")) {
												data.get(p).getprod_3()
														.setitem_in_cart3("NO");
												Cart_Arraylist
														.remove(Cart_Arraylist
																.get(cnt_val));
												notifyDataSetChanged();
												((HomeActivity) mContext)
														.buttombarAction(
																Cart_Arraylist,
																item_total_cost);
												break;
											}
										}
									}
								}
							}
						}// product 4
						if (data.get(p).getprod_4() != null) {
							if (data.get(p).getprod_4().getitem_in_cart4()
									.equals("YES")) {
								if (data.get(p)
										.getprod_4()
										.getpd_wieght4()
										.replaceAll("[^0-9]", "")
										.equals(holder.prod_qty.getText()
												.toString()
												.replaceAll("[^0-9]", ""))) {
									int cnt_val = 0;
									for (int m = 0; m < Cart_Arraylist.size(); m++) {
										if (Cart_Arraylist.get(m).getprod_4() != null)
											if (Cart_Arraylist
													.get(m)
													.getprod_4()
													.getitem_id()
													.equals(data.get(p)
															.getprod_4()
															.getitem_id()))
												cnt_val = m;
									}
									value = data.get(p).getprod_4()
											.getitem_qty_count4();
									if (value > 0) {

										value = value - 1;
										data.get(p).getprod_4()
												.setitem_qty_count4(value);
										Cart_Arraylist.get(cnt_val).getprod_4()
												.setitem_qty_count4(value);
										if (Cart_Arraylist.size() == 0)
											item_total_cost = 0;
										item_total_cost = item_total_cost
												- Double.parseDouble(data
														.get(p).getprod_4()
														.getfinal_price4());

										if (value == 0) {
											if (data.get(p).getprod_4()
													.getitem_in_cart4()
													.equals("YES")) {
												data.get(p).getprod_4()
														.setitem_in_cart4("NO");
												Cart_Arraylist
														.remove(Cart_Arraylist
																.get(cnt_val));
												notifyDataSetChanged();
												((HomeActivity) mContext)
														.buttombarAction(
																Cart_Arraylist,
																item_total_cost);
												break;
											}
										}
									}
								}
							}
						}// product 5
						notifyDataSetChanged();
						((HomeActivity) mContext).buttombarAction(
								Cart_Arraylist, item_total_cost);
						break;
					}
					for (int i = 0; i < Cart_Arraylist.size(); i++) {
						if (Cart_Arraylist.get(i).getprod_0() != null) {
							if (Cart_Arraylist.get(i).getprod_0()
									.getitem_in_cart().equals("YES")
									&& Cart_Arraylist.get(i).getprod_0()
											.getcontrol().equals("D"))
								Cart_Arraylist.get(i).getprod_0()
										.setcontrol("S");
						}
						if (Cart_Arraylist.get(i).getprod_1() != null) {
							if (Cart_Arraylist.get(i).getprod_1()
									.getitem_in_cart1().equals("YES")
									&& Cart_Arraylist.get(i).getprod_1()
											.getcontrol1().equals("D"))
								Cart_Arraylist.get(i).getprod_1()
										.setcontrol1("S");
						}
						if (Cart_Arraylist.get(i).getprod_2() != null) {
							if (Cart_Arraylist.get(i).getprod_2()
									.getitem_in_cart2().equals("YES")
									&& Cart_Arraylist.get(i).getprod_2()
											.getcontrol2().equals("D"))
								Cart_Arraylist.get(i).getprod_2()
										.setcontrol2("S");
						}
						if (Cart_Arraylist.get(i).getprod_3() != null) {
							if (Cart_Arraylist.get(i).getprod_3()
									.getitem_in_cart3().equals("YES")
									&& Cart_Arraylist.get(i).getprod_3()
											.getcontrol3().equals("D"))
								Cart_Arraylist.get(i).getprod_3()
										.setcontrol3("S");
						}
						if (Cart_Arraylist.get(i).getprod_4() != null) {
							if (Cart_Arraylist.get(i).getprod_4()
									.getitem_in_cart4().equals("YES")
									&& Cart_Arraylist.get(i).getprod_4()
											.getcontrol4().equals("D"))
								Cart_Arraylist.get(i).getprod_4()
										.setcontrol4("S");
						}
					}
					notifyDataSetChanged();
					((HomeActivity) mContext).buttombarAction(Cart_Arraylist,
							item_total_cost);
				}
			});

			if (data.get(p).getprod_1() != null) {
				if (data.get(p).getprod_1().getitem_in_cart1().equals("YES")
						&& data.get(p).getprod_1().getcontrol1().equals("S")) {
					data.get(p).getprod_1().setcontrol1("D");
					holder.prod_qty.setText(data.get(p).getprod_1()
							.getpd_wieght1());
					holder.tvProdName.setText(data.get(p).getprod_1()
							.getitem_name_1());
					holder.tvoffcost.setText("\u20B9  "
							+ data.get(p).getprod_1().getfinal_price1());

					if (!data.get(p).getprod_1().getvaliddiscount1()
							.equals("1"))
						holder.tvmrp.setVisibility(View.INVISIBLE);
					else
						holder.tvmrp.setVisibility(View.VISIBLE);
					holder.tvmrp.setText("\u20B9  "
							+ data.get(p).getprod_1().getactual_price1());
					holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);

					if (data.get(p).getprod_1().getitem_in_cart1()
							.equals("YES"))
						holder.count.setText(Integer.toString(data.get(p)
								.getprod_1().getitem_qty_count1()));
				} else if (data.get(p).getprod_2() != null) {
					if (data.get(p).getprod_2().getitem_in_cart2()
							.equals("YES")
							&& data.get(p).getprod_2().getcontrol2()
									.equals("S")) {
						data.get(p).getprod_2().setcontrol2("D");
						holder.prod_qty.setText(data.get(p).getprod_2()
								.getpd_wieght2());
						holder.tvProdName.setText(data.get(p).getprod_2()
								.getitem_name_2());
						holder.tvoffcost.setText("\u20B9  "
								+ data.get(p).getprod_2().getfinal_price2());

						if (!data.get(p).getprod_2().getvaliddiscount2()
								.equals("1"))
							holder.tvmrp.setVisibility(View.INVISIBLE);
						else
							holder.tvmrp.setVisibility(View.VISIBLE);
						holder.tvmrp.setText("\u20B9  "
								+ data.get(p).getprod_2().getactual_price2());
						holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG);

						if (data.get(p).getprod_2().getitem_in_cart2()
								.equals("YES"))
							holder.count.setText(Integer.toString(data.get(p)
									.getprod_2().getitem_qty_count2()));
					} else if (data.get(p).getprod_3() != null) {
						if (data.get(p).getprod_3().getitem_in_cart3()
								.equals("YES")
								&& data.get(p).getprod_3().getcontrol3()
										.equals("S")) {
							data.get(p).getprod_3().setcontrol3("D");
							holder.prod_qty.setText(data.get(p).getprod_3()
									.getpd_wieght3());
							holder.tvProdName.setText(data.get(p).getprod_3()
									.getitem_name_3());
							holder.tvoffcost
									.setText("\u20B9  "
											+ data.get(p).getprod_3()
													.getfinal_price3());

							if (!data.get(p).getprod_3().getvaliddiscount3()
									.equals("1"))
								holder.tvmrp.setVisibility(View.INVISIBLE);
							else
								holder.tvmrp.setVisibility(View.VISIBLE);
							holder.tvmrp.setText("\u20B9  "
									+ data.get(p).getprod_3()
											.getactual_price3());
							holder.tvmrp.setPaintFlags(holder.tvmrp
									.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);

							if (data.get(p).getprod_3().getitem_in_cart3()
									.equals("YES"))
								holder.count.setText(Integer.toString(data
										.get(p).getprod_3()
										.getitem_qty_count3()));
						} else if (data.get(p).getprod_0().getitem_in_cart()
								.equals("YES")) {
							holder.prod_qty.setText(data.get(p).getprod_0()
									.getpd_wieght());
							holder.tvProdName.setText(data.get(p).getprod_0()
									.getitem_name());
							holder.tvoffcost.setText("\u20B9  "
									+ data.get(p).getprod_0().getfinal_price());

							if (!data.get(p).getprod_0().getvaliddiscount()
									.equals("1"))
								holder.tvmrp.setVisibility(View.INVISIBLE);
							else
								holder.tvmrp.setVisibility(View.VISIBLE);
							holder.tvmrp
									.setText("\u20B9  "
											+ data.get(p).getprod_0()
													.getactual_price());
							holder.tvmrp.setPaintFlags(holder.tvmrp
									.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);

							if (data.get(p).getprod_0().getitem_in_cart()
									.equals("YES"))
								holder.count.setText(Integer
										.toString(data.get(p).getprod_0()
												.getitem_qty_count()));
						}
					} else if (data.get(p).getprod_0().getitem_in_cart()
							.equals("YES")) {
						holder.prod_qty.setText(data.get(p).getprod_0()
								.getpd_wieght());
						holder.tvProdName.setText(data.get(p).getprod_0()
								.getitem_name());
						holder.tvoffcost.setText("\u20B9  "
								+ data.get(p).getprod_0().getfinal_price());

						if (!data.get(p).getprod_0().getvaliddiscount()
								.equals("1"))
							holder.tvmrp.setVisibility(View.INVISIBLE);
						else
							holder.tvmrp.setVisibility(View.VISIBLE);
						holder.tvmrp.setText("\u20B9  "
								+ data.get(p).getprod_0().getactual_price());
						holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
								| Paint.STRIKE_THRU_TEXT_FLAG);

						if (data.get(p).getprod_0().getitem_in_cart()
								.equals("YES"))
							holder.count.setText(Integer.toString(data.get(p)
									.getprod_0().getitem_qty_count()));
					}
				} else if (data.get(p).getprod_0().getitem_in_cart()
						.equals("YES")) {
					holder.prod_qty.setText(data.get(p).getprod_0()
							.getpd_wieght());
					holder.tvProdName.setText(data.get(p).getprod_0()
							.getitem_name());
					holder.tvoffcost.setText("\u20B9  "
							+ data.get(p).getprod_0().getfinal_price());

					if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
						holder.tvmrp.setVisibility(View.INVISIBLE);
					else
						holder.tvmrp.setVisibility(View.VISIBLE);
					holder.tvmrp.setText("\u20B9  "
							+ data.get(p).getprod_0().getactual_price());
					holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);

					if (data.get(p).getprod_0().getitem_in_cart().equals("YES"))
						holder.count.setText(Integer.toString(data.get(p)
								.getprod_0().getitem_qty_count()));
				}
			} else {
				holder.prod_qty.setText(data.get(p).getprod_0().getpd_wieght());
				holder.tvProdName.setText(data.get(p).getprod_0()
						.getitem_name());
				holder.tvoffcost.setText("\u20B9  "
						+ data.get(p).getprod_0().getfinal_price());

				if (!data.get(p).getprod_0().getvaliddiscount().equals("1"))
					holder.tvmrp.setVisibility(View.INVISIBLE);
				else
					holder.tvmrp.setVisibility(View.VISIBLE);
				holder.tvmrp.setText("\u20B9  "
						+ data.get(p).getprod_0().getactual_price());
				holder.tvmrp.setPaintFlags(holder.tvmrp.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);

				if (data.get(p).getprod_0().getitem_in_cart().equals("YES"))
					holder.count.setText(Integer.toString(data.get(p)
							.getprod_0().getitem_qty_count()));
			}
			if (data.get(p).getprod_0().getitem_image() == null
					|| data.get(p).getprod_0().getitem_image().equals(""))
				holder.imgThumbnail.setImageResource(R.drawable.no_image);
			else
				Picasso.with(activity)
						.load(data.get(p).getprod_0().getitem_image()).fit()
						.centerCrop().tag(activity).into(holder.imgThumbnail);

			// {
			// holder.imgLoader = ImageLoader.getInstance();
			// holder.imgLoader.displayImage(data.get(p).getitem_image(),
			// holder.imgThumbnail);
			// }
			// holder.lblDateEnd.setText("  " + item.get(""));

			return convertView;
		}

	}

	@Override
	public boolean onClose() {
		if (search_check.equals("YES"))
			showResults("");
		search_icon.setVisible(true);
		searchView.setVisibility(View.GONE);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (search_check.equals("YES"))
			showResults(query);
		else {
			String key = query != null ? query.toString() : "@@@@";
			S_query = key.toString().replace(" ", "%20");
			search();
		}
		return false;
	}

	void search() {
		if (IsInternetPresent())
			new getSearchList().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									search();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (search_check.equals("YES"))
			showResults(newText);
		if (newText.length() > 2) {
			String key = newText != null ? newText.toString() : "@@@@";
			S_query = key.toString().replace(" ", "%20");
			search();
		}
		return false;
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	public boolean IsInternetPresent() {
		if (new Utils(HomeActivity.this).isNetworkAvailable())
			return true;
		else
			return false;
	}

	// location

	void displaySelectType() {

		TextView tvpicklocality, tvmylocation;

		final Dialog dialog = new Dialog(HomeActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.dialog_select_address_home);

		tvpicklocality = (TextView) dialog.findViewById(R.id.tvpicklocality);
		tvmylocation = (TextView) dialog.findViewById(R.id.tvmylocation);
		// btclose = (Button) promptsView.findViewById(R.id.btnsave);
		current_address = (TextView) dialog.findViewById(R.id.tvLocation);

		if (Utils.ADD.equals(""))
			current_address.setText(sharedpreferences
					.getString("myaddress", ""));
		else
			current_address.setText(Utils.ADD);
		tvpicklocality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				displaySearchLocation();
				dialog.cancel();
			}
		});

		tvmylocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getCurrentLocation();

				search_check = "NO";
				dialog.cancel();
				AppRater.app_launched(HomeActivity.this);

			}
		});
		dialog.show();
	}

	public void reload() {

		My_address = sharedpreferences.getString("myaddress", "");
		Utils.ADD = My_address;
		current_address.setText(My_address);
	}

	void displaySearchLocation() {

		ImageView ivClose;
		LayoutInflater li = LayoutInflater.from(HomeActivity.this);
		View promptsView = li.inflate(R.layout.dialog_search_location, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				HomeActivity.this);
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setView(promptsView);
		alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		lvAddress = (ListView) promptsView.findViewById(R.id.lvaddresslist);

		locationSearchView = (SearchView) promptsView
				.findViewById(R.id.svsearch);
		locationSearchView.setIconifiedByDefault(false);
		locationSearchView.setOnQueryTextListener(this);
		locationSearchView.setOnCloseListener(this);
	}

	public void getCurrentLocation() {

		gps = new GPSTracker(HomeActivity.this);
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			Location location = gps.getLocation();

			sharedpreferences = getSharedPreferences(MyLogin,
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("userLat", Double.toString(latitude));
			editor.putString("userLong", Double.toString(longitude));
			editor.commit();

			new getAddressTask().execute(latitude + "," + longitude);
		} else {
			gps.showSettingsAlert();
		}
	}

	protected class getAddressTask extends AsyncTask<String, Void, JSONObject> {

		Context localContext;

		public getAddressTask() {
			super();
			// localContext = context;
		}

		protected void onPreExecute() {

			// dialog = Utils.createProgressDialog(HomeActivity.this);
			// dialog.setCancelable(false);
			// dialog.show();

		}

		@Override
		protected JSONObject doInBackground(String... params) {

			String placesName = params[0];

			String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
					+ placesName;

			HttpGet httpGet = new HttpGet(apiRequest);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {

				e.printStackTrace();
			}

			return jsonObject;

		}

		@Override
		protected void onPostExecute(JSONObject result) {

			String address = null;
			try {
				JSONArray array = result.getJSONArray("results");
				if (array.length() > 0) {
					JSONObject place = array.getJSONObject(0);
					address = place.getString("formatted_address");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (current_address != null)
				current_address.setText(My_address);
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("myaddress", address);
			Utils.ADD = address;
			editor.commit();
			reload();
			// dialog.dismiss();
		}

	}

	private void showResults(String query) {

		String suggtionname = query != null ? query.toString() : "@@@@";

		placesTask = new PlacesTask();
		placesTask.execute(suggtionname.toString().replace(" ", "%20"));

	}

	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyAsPPV7T0TZXhOaMUKKShN9lEwCh-luqRo";

			String input = "";

			try {
				input = "input=" + place[0];
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			String parameters = input + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				AddressObj address;
				addressList.clear();
				JSONObject jObject = new JSONObject(result);
				JSONArray jPlaces = jObject.getJSONArray("predictions");

				for (int i = 0; i < jPlaces.length(); i++) {
					JSONObject jO = (JSONObject) jPlaces.get(i);
					address = new AddressObj();
					address.setAddressName(jO.getString("description"));
					address.setAddressId(jO.getString("place_id"));
					// address.setAddressId(jO.getString("_id"));
					addressList.add(address);

				}

				adapterAddress = new AdapterAddress(HomeActivity.this,
						R.layout.adapter_address, addressList);

				lvAddress.setAdapter(adapterAddress);

				lvAddress.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						addressobj = (AddressObj) parent
								.getItemAtPosition(position);
						SharedPreferences.Editor editor = sharedpreferences
								.edit();

						editor.putString("myaddress",
								addressobj.getAddressName());
						editor.commit();

						reload();
						new getLocationTask().execute(addressobj.getAddressId());

						/*
						 * addressobj = (Address) parent
						 * .getItemAtPosition(position);
						 * 
						 * tvAddress.setText(addressobj.getAddressName());
						 */
						search_check = "NO";
						alertDialog.cancel();

						AppRater.app_launched(HomeActivity.this);
					}

				});

			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Creating ParserTask
			// parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			// parserTask.execute(result);
		}
	}

	private class getLocationTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyAsPPV7T0TZXhOaMUKKShN9lEwCh-luqRo";

			String input = "";

			try {
				input = "placeid=" + place[0];
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// Building the parameters to the web service
			String parameters = input + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/details/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {

				JSONObject jObject = new JSONObject(result);
				JSONObject resultObject = jObject.getJSONObject("result");
				JSONObject geoObject = resultObject.getJSONObject("geometry");
				JSONObject locObject = geoObject.getJSONObject("location");

				SharedPreferences.Editor editor = sharedpreferences.edit();

				editor.putString("userLat", locObject.getString("lat"));
				editor.putString("userLong", locObject.getString("lng"));

				editor.commit();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Creating ParserTask
			// parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			// parserTask.execute(result);
		}
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	void favorite(String prod_id) {
		PID = prod_id;
		
		if (IsInternetPresent())
			if (UID.equals("")) {
				alertDialogBuilder
						.setMessage("Login to add Favorites.")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = alertDialogBuilder.create();
				alert.show();
			} else
				new create_favList().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									search();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public class create_favList extends AsyncTask<Void, Void, String> {

		String URL;

		protected void onPreExecute() {
			URL = String.format(URLs.SET_FAV_URL, UID, PID);
		}

		protected String doInBackground(Void... params) {

			InputStream inputStream = null;
			String result = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient
						.execute(new HttpGet(URL));
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null)
					result = convertInputStreamToString(inputStream);

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
			return result;
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);

				String res = jObj_main.getString("success").toString();
				if (res.equals("1")) {
					Toast.makeText(getApplicationContext(),
							"Item added to Favorite List.", Toast.LENGTH_LONG)
							.show();

				} else {
					alertDialogBuilder
							.setMessage("Favorite not set!")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = alertDialogBuilder.create();
					alert.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	public HashMap<String, FilterItem> getBrandSelactList() {
		return brandSelactList;
	}

	public void setBrandSelactList(HashMap<String, FilterItem> brandSelactList) {
		this.brandSelactList = brandSelactList;
	}

	public HashMap<String, FilterItem> getPriceSelactList() {
		return priceSelactList;
	}

	public void setPriceSelactList(HashMap<String, FilterItem> priceSelactList) {
		this.priceSelactList = priceSelactList;
	}
	
}
