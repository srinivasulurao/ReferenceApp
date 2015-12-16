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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import foodzu.com.Utils.GPSTracker;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.adapters.AdapterAddress;
import foodzu.com.adapters.AdapterMenu;
import foodzu.com.adapters.SubCategoryMenu;
import foodzu.com.models.AddressObj;
import foodzu.com.models.Child_Cat;
import foodzu.com.models.MainMenu;
import foodzu.com.models.Products;
import foodzu.com.models.SubMenu_Cat;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
@SuppressWarnings("deprecation")
@SuppressLint({ "InflateParams", "NewApi" })
public class NavigationDrawerFragment extends Fragment implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	ExpandableListView mDrawerSubListView;
	LinearLayout subview, backup;
	private View mFragmentContainerView;
	private View currentSelectedView, ChildSelectedView, GroupSelectedView;
	int GroupSID = -1;
	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	private AdapterMenu adapterMenu;
	private SubCategoryMenu subadapter;
	Products menulist;
	public static ArrayList<String> categorylist = new ArrayList<String>();
	public static ArrayList<String> subcategorylist = new ArrayList<String>();
	List<Products> menu_list, submenu_list;
	public String CAT_ID;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	TextView mLocation;
	ImageView ivChangeLocation;
	LinearLayout lleditloc;
	SharedPreferences sharedPreferences;
	GPSTracker gps;
	String address, subcat_ID = "";
	ListView lvAddress;
	SearchView locationSearchView;
	List<AddressObj> addressList = new ArrayList<AddressObj>();
	AdapterAddress adapterAddress;
	PlacesTask placesTask;
	AddressObj addressobj;

	MainMenu MainMenu, Temp_main_obj;
	SubMenu_Cat SubMenu;
	Child_Cat ChildMenu;

	ArrayList<MainMenu> MM_Array_List;
	ArrayList<SubMenu_Cat> SM_Array_List;
	ArrayList<Child_Cat> CM_Array_List;

	// Expansion list
	Expandablelist_adapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, ArrayList<String>> listDataChild;
	static String mainmenu;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		alertDialogBuilder = new AlertDialog.Builder(getActivity());
		adapterMenu = new AdapterMenu(getActivity());
		subadapter = new SubCategoryMenu(getActivity());

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition, "", "", "", "", "HIDE");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_navigation_drawer, null);

		v.setEnabled(false);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mLocation = (TextView) v.findViewById(R.id.tvLocation);
		ivChangeLocation = (ImageView) v.findViewById(R.id.ic_edit_loc);
		lleditloc = (LinearLayout) v.findViewById(R.id.lleditloc);

		mDrawerListView = (ListView) v.findViewById(R.id.list);
		subview = (LinearLayout) v.findViewById(R.id.sub_list_view);
		backup = (LinearLayout) v.findViewById(R.id.back_menu);
		mDrawerSubListView = (ExpandableListView) v.findViewById(R.id.sub_list);
		// expListView = (ExpandableListView) v.findViewById(R.id.sub_list);
		mDrawerSubListView.setVisibility(View.GONE);
		subview.setVisibility(View.GONE);
		menu_list = new ArrayList<Products>();
		submenu_list = new ArrayList<Products>();
		MM_Array_List = new ArrayList<MainMenu>();
		SM_Array_List = new ArrayList<SubMenu_Cat>();
		CM_Array_List = new ArrayList<Child_Cat>();

		mainlist();
		
		if(!Utils.ADD.equals(""))
			mLocation.setText(Utils.ADD);

		backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerSubListView.setVisibility(View.GONE);
				subview.setVisibility(View.GONE);
				if (GroupSelectedView != null) {
					GroupSID = -1;
					unhighlightGroupRow(GroupSelectedView);
				}
				if (ChildSelectedView != null) {
					unhighlightCurrentChildRow(ChildSelectedView);
				}
			}
		});

		lleditloc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				displaySelectType();

			}
		});

		// --------------------------------------------------------------------

		// --------------------------------------------------------------------

		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						v.setSelected(true);
						if (position == -1)
							selectItem(position, "", "", "", "", "HIDE");
						else {
							// CAT_ID = MM_Array_List.get(position -
							// 1).getmenu_id();
							v.setSelected(true);
							if (currentSelectedView != null
									&& currentSelectedView != v) {
								unhighlightCurrentRow(currentSelectedView);
							}
							currentSelectedView = v;
							// sublist();
							mainmenu = MM_Array_List.get(position)
									.getmenu_name();
							get_submenu(MM_Array_List.get(position));
							Temp_main_obj = MM_Array_List.get(position);

							// mDrawerSubListView
							// .setOnItemClickListener(new
							// AdapterView.OnItemClickListener() {
							// @Override
							// public void onItemClick(
							// AdapterView<?> parent,
							// View view, int pos, long id) {
							// }
							// });

							// Listview Group click listener
							mDrawerSubListView
									.setOnGroupClickListener(new OnGroupClickListener() {

										@Override
										public boolean onGroupClick(
												ExpandableListView parent,
												View v, int groupPosition,
												long id) {
											// Toast.makeText(getActivity(),
											// "Group Clicked " +
											// listDataHeader.get(groupPosition),
											// Toast.LENGTH_SHORT).show();
											v.setSelected(true);
											if (GroupSelectedView != null
													&& GroupSelectedView != v) {
												GroupSID = -1;
												unhighlightGroupRow(GroupSelectedView);
											}
											if (get_count(Temp_main_obj,
													listDataHeader
															.get(groupPosition)) == 0) {
												GroupSID = groupPosition;
												selectItem(
														groupPosition,
														mainmenu,
														"",
														listDataHeader
																.get(groupPosition),
														subcat_ID, "SHOW");

												if (GroupSelectedView != null
														&& GroupSelectedView != v)
													unhighlightGroupRow(GroupSelectedView);
												GroupSelectedView = v;
												highlightGroupRow(GroupSelectedView);
											}

											return false;
										}
									});

							// Listview Group expanded listener
							mDrawerSubListView
									.setOnGroupExpandListener(new OnGroupExpandListener() {

										@Override
										public void onGroupExpand(
												int groupPosition) {
											// Toast.makeText(
											// getActivity(),
											// listDataHeader.get(groupPosition)
											// + " Expanded",
											// Toast.LENGTH_SHORT).show();
										}
									});

							// Listview Group collasped listener
							mDrawerSubListView
									.setOnGroupCollapseListener(new OnGroupCollapseListener() {

										@Override
										public void onGroupCollapse(
												int groupPosition) {
											// Toast.makeText(
											// getActivity(),
											// listDataHeader.get(groupPosition)
											// + " Collapsed",
											// Toast.LENGTH_SHORT)
											// .show();
											if (ChildSelectedView != null) {
												unhighlightCurrentChildRow(ChildSelectedView);
											}
										}
									});

							// Listview on child click listener
							mDrawerSubListView
									.setOnChildClickListener(new OnChildClickListener() {

										@Override
										public boolean onChildClick(
												ExpandableListView parent,
												View v, int groupPosition,
												int childPosition, long id) {
											// Toast.makeText(
											// getActivity(),
											// listDataHeader.get(groupPosition)
											// + " : "
											// + listDataChild.get(
											// listDataHeader.get(groupPosition)).get(
											// childPosition),
											// Toast.LENGTH_SHORT)
											// .show();

											// System.out.println(listDataChild
											// .get(listDataHeader
											// .get(groupPosition))
											// .get(childPosition)
											// + "---------"
											// + get_itemid(
											// Temp_main_obj,
											// listDataChild
											// .get(listDataHeader
											// .get(groupPosition))
											// .get(childPosition)));
											// get_itemid(Temp_main_obj,
											// listDataChild.get(
											// listDataHeader.get(groupPosition)).get(
											// childPosition));
											String item_id = get_itemid(
													Temp_main_obj,
													listDataChild
															.get(listDataHeader
																	.get(groupPosition))
															.get(childPosition));
											String item_name = listDataChild
													.get(listDataHeader
															.get(groupPosition))
													.get(childPosition);
											selectItem(groupPosition, mainmenu,
													"", item_name, item_id,
													"SHOW");

											v.setSelected(true);
											if (ChildSelectedView != null
													&& ChildSelectedView != v) {
												unhighlightCurrentChildRow(ChildSelectedView);
											}
											ChildSelectedView = v;
											highlightCurrentChildRow(ChildSelectedView);
											return false;
										}
									});
						}
					}
				});

		mDrawerSubListView.setItemChecked(mCurrentSelectedPosition, true);
		return v;
	}

	void mainlist() {
		if (new Utils(getActivity()).isNetworkAvailable()) {
			// new getMenuList().execute();
			new get_Menu().execute();
			getCurrentLocation();
		} else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mainlist();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	void sublist() {
		if (new Utils(getActivity()).isNetworkAvailable())
			new getSubMenuList().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									sublist();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	String get_itemid(MainMenu TEMP_DATA, String IT_name) {

		List<SubMenu_Cat> sublist = new ArrayList<SubMenu_Cat>(
				TEMP_DATA.getsubmenu_list());
		for (int i = 0; i < sublist.size(); i++) {
			List<Child_Cat> childlist = new ArrayList<Child_Cat>(sublist.get(i)
					.getchildmenu_list());
			for (int j = 0; j < childlist.size(); j++) {
				if (childlist.get(j).getChild_name().equals(IT_name))
					return childlist.get(j).getChild_id();
			}
		}
		return "0";
	}

	int get_count(MainMenu TEMP_DATA, String IT_name) {

		List<SubMenu_Cat> sublist = new ArrayList<SubMenu_Cat>(
				TEMP_DATA.getsubmenu_list());
		for (int i = 0; i < sublist.size(); i++) {
			if (sublist.get(i).getsubmenu_name().equals(IT_name)) {
				subcat_ID = sublist.get(i).getsubmenu_id();
				return sublist.get(i).childmenu.size();
			}

		}
		return 0;
	}

	void get_submenu(MainMenu DATA) {

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, ArrayList<String>>();
		List<SubMenu_Cat> sublist = new ArrayList<SubMenu_Cat>(
				DATA.getsubmenu_list());
		for (int i = 0; i < sublist.size(); i++) {
			ArrayList<String> data = new ArrayList<String>();
			List<Child_Cat> childlist = new ArrayList<Child_Cat>(sublist.get(i)
					.getchildmenu_list());
			// if(childlist.size()==0)data.add("No Products");
			// listDataHeader.add("(" + childlist.size() + ") "
			// + sublist.get(i).getsubmenu_name());
			listDataHeader.add(sublist.get(i).getsubmenu_name());
			for (int j = 0; j < childlist.size(); j++) {
				data.add(childlist.get(j).getChild_name());
			}

			listDataChild.put(listDataHeader.get(i), data);
		}

		listAdapter = new Expandablelist_adapter(getActivity(), listDataHeader,
				listDataChild);
		mDrawerSubListView.setVisibility(View.VISIBLE);
		subview.setVisibility(View.VISIBLE);
		// setting list adapter
		mDrawerSubListView.setAdapter(listAdapter);

		if (currentSelectedView != null) {
			highlightCurrentRow(currentSelectedView);
		}

	}

	// private void prepareListData() {
	// listDataHeader = new ArrayList<String>();
	// // listDataChild = new HashMap<String, List<String>>();
	//
	// // Adding child data
	// listDataHeader.add("Top 250");
	// listDataHeader.add("Now Showing");
	// listDataHeader.add("Coming Soon..");
	//
	// // Adding child data
	// List<String> top250 = new ArrayList<String>();
	// top250.add("The Shawshank Redemption");
	// top250.add("The Godfather");
	// top250.add("The Godfather: Part II");
	// top250.add("Pulp Fiction");
	// top250.add("The Good, the Bad and the Ugly");
	// top250.add("The Dark Knight");
	// top250.add("12 Angry Men");
	//
	// List<String> nowShowing = new ArrayList<String>();
	// nowShowing.add("The Conjuring");
	// nowShowing.add("Despicable Me 2");
	// nowShowing.add("Turbo");
	// nowShowing.add("Grown Ups 2");
	// nowShowing.add("Red 2");
	// nowShowing.add("The Wolverine");
	//
	// List<String> comingSoon = new ArrayList<String>();
	// comingSoon.add("2 Guns");
	// comingSoon.add("The Smurfs 2");
	// comingSoon.add("The Spectacular Now");
	// comingSoon.add("The Canyons");
	// comingSoon.add("Europa Report");
	//
	// // listDataChild.put(listDataHeader.get(0), top250); // Header, Child
	// // data
	// // listDataChild.put(listDataHeader.get(1), nowShowing);
	// // listDataChild.put(listDataHeader.get(2), comingSoon);
	// }
	public void close_drawer() {
		if (mDrawerLayout != null)
			mDrawerLayout.closeDrawers();
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 *
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}
				// mDrawerSubListView.setVisibility(View.GONE);
				// if (currentSelectedView != null)
				// unhighlightCurrentRow(currentSelectedView);
				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}
				reload();
				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position, String Main_cat, String Main_cat_ID,
			String Sub_cat, String Sub_cat_ID, String show) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			if (mDrawerSubListView != null) {
				mDrawerSubListView.setItemChecked(position, true);
			}
		}

		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position, Main_cat,
					Main_cat_ID, Sub_cat, Sub_cat_ID, show);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// if (item.getItemId() == R.id.action_example) {
		// Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT)
		// .show();
		// return true;
		// }

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		// background="#4CBB17"
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position, String Main_cat,
				String Main_cat_ID, String Sub_cat, String Sub_cat_ID,
				String show);
	}

	public String getcategory() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.CATEGORY_URL;
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

	public String getsubcategory(String menu_id) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.SUB_MENU_URL;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL
					+ menu_id));
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

	public class getMenuList extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			String category = getcategory();
			categorylist.clear();
			categorylist.add("Home");
			try {
				if (category != null && category.length() > 0) {

					JSONObject jObj_main = new JSONObject(category);
					JSONObject result = jObj_main.getJSONObject("result");
					JSONArray arr = result.getJSONArray("menu_list");
					if (arr.length() > 0) {
						for (int i = 0; i < arr.length(); i++) {
							menulist = new Products();
							JSONObject menu = arr.getJSONObject(i);
							menulist.setmenu_id(menu.getString("menu_id")
									.toString());
							menulist.setmenu_name(menu.getString("menu_name"));
							categorylist.add(menu.getString("menu_name"));
							menu_list.add(menulist);
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mDrawerListView.setAdapter(adapterMenu);
			mDrawerListView.setItemChecked(0, true);

		}
	}

	public class get_Menu extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			InputStream inputStream = null;
			String result = null;
			String URL = URLs.MENU_URL;
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

			// categorylist.clear();
			// categorylist.add("Home");
			try {
				if (result != null && result.length() > 0) {
					MM_Array_List.clear();
					JSONObject jObj_main = new JSONObject(result);
					JSONObject output = jObj_main.getJSONObject("result");
					JSONArray main_arr = output.getJSONArray("menu_list");
					if (main_arr.length() > 0) {
						for (int i = 0; i < main_arr.length(); i++) {
							MainMenu = new MainMenu();

							JSONObject menu = main_arr.getJSONObject(i);
							MainMenu.setmenu_id(menu.getString("menu_id")
									.toString());
							MainMenu.setmenu_name(menu.getString("menu_name"));
							int sub_cat = menu.getInt("total");
							if (sub_cat > 0) {
								SM_Array_List.clear();
								JSONArray sub_arr = menu
										.getJSONArray("category");
								if (sub_arr.length() > 0) {
									for (int j = 0; j < sub_arr.length(); j++) {
										SubMenu = new SubMenu_Cat();
										JSONObject submenu = sub_arr
												.getJSONObject(j);

										int sub_cat_total = submenu
												.getInt("total");
										if (sub_cat_total > 0) {

											JSONArray sublevel_arr = submenu
													.getJSONArray("subcategory");
											if (sublevel_arr.length() > 0) {
												for (int k = 0; k < sublevel_arr
														.length(); k++) {
													SubMenu = new SubMenu_Cat();

													JSONObject sublevelmenu = sublevel_arr
															.getJSONObject(k);
													SubMenu.setsubmenu_id(sublevelmenu
															.getString(
																	"menu_id")
															.toString());
													SubMenu.setsubmenu_name(sublevelmenu
															.getString("menu_name"));
													int child_cat_total = sublevelmenu
															.getInt("total");
													if (child_cat_total > 0) {
														CM_Array_List.clear();
														JSONArray childlevel_arr = sublevelmenu
																.getJSONArray("childcategory");
														if (childlevel_arr
																.length() > 0) {
															for (int m = 0; m < childlevel_arr
																	.length(); m++) {
																ChildMenu = new Child_Cat();
																JSONObject child_data = childlevel_arr
																		.getJSONObject(m);
																ChildMenu
																		.setChild_id(child_data
																				.getString(
																						"menu_id")
																				.toString());
																ChildMenu
																		.setChild_name(child_data
																				.getString("menu_name"));
																CM_Array_List
																		.add(ChildMenu);
															}// for loop of
																// child
																// category
															SubMenu.getchildmenu_list()
																	.addAll(CM_Array_List);
														}
													}
													SM_Array_List.add(SubMenu);
												}// for loop of sub category
											}
										} else {
											SubMenu.setsubmenu_id(submenu
													.getString("menu_id")
													.toString());
											SubMenu.setsubmenu_name(submenu
													.getString("menu_name"));
											SubMenu.setsubmenu_haschild("N");
											SM_Array_List.add(SubMenu);
										}
									}// for loop of category
									MainMenu.getsubmenu_list().addAll(
											SM_Array_List);
								}
							}
							categorylist.add(MainMenu.getmenu_name());
							MM_Array_List.add(MainMenu);
						}// for loop of menu
					}
					// JSONArray arr = result.getJSONArray("menu_list");
					// if (arr.length() > 0) {
					// for (int i = 0; i < arr.length(); i++) {
					// menulist = new Products();
					// JSONObject menu = arr.getJSONObject(i);
					// menulist.setmenu_id(menu.getString("menu_id")
					// .toString());
					// menulist.setmenu_name(menu.getString("menu_name"));
					// categorylist.add(menu.getString("menu_name"));
					// menu_list.add(menulist);
					// }
					// }
				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mDrawerListView.setAdapter(adapterMenu);
			mDrawerListView.setItemChecked(0, true);
		}
	}

	// AsyncTask to get SubMenu list
	public class getSubMenuList extends AsyncTask<Void, Void, Void> {

		Dialog dialog;

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(getActivity());
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String subcategory = getsubcategory(CAT_ID);
			subcategorylist.clear();
			submenu_list.clear();
			try {
				if (subcategory != null && subcategory.length() > 0) {

					JSONObject jObj_main = new JSONObject(subcategory);
					int total = jObj_main.getInt("total");

					JSONObject result = jObj_main.getJSONObject("result");

					if (total > 0) {
						for (int i = 0; i < total; i++) {
							menulist = new Products();
							String str = Integer.toString(i);
							JSONObject menu = result.getJSONObject(str);
							menulist.setsubmenu_id(menu.getString("cat_id")
									.toString());
							menulist.setsubmenu_name(menu.getString("cat_name"));
							subcategorylist.add(menu.getString("cat_name"));
							submenu_list.add(menulist);
						}
					}

				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (currentSelectedView != null) {
				highlightCurrentRow(currentSelectedView);
			}
			mDrawerSubListView.setVisibility(View.VISIBLE);
			subview.setVisibility(View.VISIBLE);
			mDrawerSubListView.setAdapter(subadapter);
			dialog.dismiss();
		}
	}

	private void unhighlightCurrentRow(View rowView) {
		rowView.setBackgroundColor(getResources().getColor(R.color.White));
		TextView textView = (TextView) rowView.findViewById(R.id.txtCategory);
		textView.setTextColor(getResources().getColor(R.color.foodzu_green));
	}

	private void highlightCurrentRow(View rowView) {
		rowView.setBackgroundColor(getResources()
				.getColor(R.color.foodzu_green));
		TextView textView = (TextView) rowView.findViewById(R.id.txtCategory);
		textView.setTextColor(getResources().getColor(R.color.White));
	}

	private void highlightGroupRow(View rowView) {
		LinearLayout lnly = (LinearLayout) rowView.findViewById(R.id.grp_view);
		lnly.setBackgroundColor(getResources().getColor(R.color.orange));
		TextView textView = (TextView) rowView
				.findViewById(R.id.txtsubCategory);
		textView.setTextColor(getResources().getColor(R.color.White));
		listAdapter.notifyDataSetChanged();
	}

	private void unhighlightGroupRow(View rowView) {
		LinearLayout lnly = (LinearLayout) rowView.findViewById(R.id.grp_view);
		lnly.setBackgroundColor(getResources().getColor(R.color.foodzu_green));
		TextView textView = (TextView) rowView
				.findViewById(R.id.txtsubCategory);
		textView.setTextColor(getResources().getColor(R.color.black));
		listAdapter.notifyDataSetChanged();
	}

	private void unhighlightCurrentChildRow(View rowView) {
		rowView.setBackgroundColor(getResources()
				.getColor(R.color.foodzu_green));
		TextView textView = (TextView) rowView.findViewById(R.id.txtsub_menu);
		textView.setTextColor(getResources().getColor(R.color.White));
	}

	private void highlightCurrentChildRow(View rowView) {
		rowView.setBackgroundColor(getResources().getColor(R.color.orange));
		TextView childView = (TextView) rowView.findViewById(R.id.txtsub_menu);
		childView.setTextColor(getResources().getColor(R.color.White));
	}

	// fetch current location & edit shipping address.

	public void getCurrentLocation() {

		gps = new GPSTracker(getActivity());
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			Location location = gps.getLocation();

			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("userLat", Double.toString(latitude));
			editor.putString("userLong", Double.toString(longitude));
			editor.commit();

			(new getAddressTask(getActivity())).execute(latitude + ","
					+ longitude);
		} else {
			gps.showSettingsAlert();
		}
	}

	protected class getAddressTask extends AsyncTask<String, Void, JSONObject> {

		Context localContext;

		public getAddressTask(Context context) {
			super();
			localContext = context;
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
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("address", address);
			editor.apply();
			reload();
		}

	}

	public void reload() {
		sharedPreferences = this.getActivity().getSharedPreferences("Login",
				Context.MODE_PRIVATE);
		String ans = sharedPreferences.getString("myaddress", "");
		if (!ans.equals("")) {
			SharedPreferences.Editor editor = sharedPreferences.edit();

			editor.putString("address", ans);
			editor.putString("myaddress", "");
			editor.apply();
			Utils.ADD = ans;
			System.out.println(ans);
			mLocation.setText(ans);
		} else {
			address = sharedPreferences.getString("address", null);
			mLocation.setText(address);
			Utils.ADD = address;

			System.out.println("NEW:"+address);
		}
	}

	void displaySelectType() {

		TextView tvpicklocality, tvmylocation;
		Button btclose;

		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_select_address, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		tvpicklocality = (TextView) promptsView
				.findViewById(R.id.tvpicklocality);
		tvmylocation = (TextView) promptsView.findViewById(R.id.tvmylocation);
		btclose = (Button) promptsView.findViewById(R.id.btnsave);

		tvpicklocality.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				displaySearchLocation();

				alertDialog.cancel();
			}
		});

		tvmylocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getCurrentLocation();

				alertDialog.cancel();

			}
		});

		btclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// getCurrentLocation();

				alertDialog.cancel();
			}
		});

	}

	void displaySearchLocation() {

		ImageView ivClose;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_search_location, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
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

	public boolean onQueryTextChange(String newText) {
		showResults(newText);
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		showResults(query);
		return false;
	}

	public boolean onClose() {
		showResults("");
		return false;
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

				adapterAddress = new AdapterAddress(getActivity(),
						R.layout.adapter_address, addressList);

				lvAddress.setAdapter(adapterAddress);

				lvAddress.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						addressobj = (AddressObj) parent
								.getItemAtPosition(position);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();

						editor.putString("address", addressobj.getAddressName());
						editor.apply();

						reload();

						new getLocationTask().execute(addressobj.getAddressId());

						/*
						 * addressobj = (Address) parent
						 * .getItemAtPosition(position);
						 * 
						 * tvAddress.setText(addressobj.getAddressName());
						 */

						alertDialog.cancel();

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

				SharedPreferences.Editor editor = sharedPreferences.edit();

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

	// ----------------------------------------------------------------------------------
	// expanable list adapter

	public class Expandablelist_adapter extends BaseExpandableListAdapter {

		private Context _context;
		private List<String> _listDataHeader; // header titles
		// child data in format of header title, child title
		private HashMap<String, ArrayList<String>> _listDataChild;

		public Expandablelist_adapter(Context context,
				List<String> listDataHeader,
				HashMap<String, ArrayList<String>> listChildData) {
			this._context = context;
			this._listDataHeader = listDataHeader;
			this._listDataChild = listChildData;
		}

		@Override
		public Object getChild(int groupPosition, int childPosititon) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition))
					.get(childPosititon);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			final String childText = (String) getChild(groupPosition,
					childPosition);

			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.adapter_sublist_menu, null);
			}

			TextView txtListChild = (TextView) convertView
					.findViewById(R.id.txtsub_menu);

			txtListChild.setText(childText);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listDataChild.get(
					this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataHeader.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			final ViewHolder holder;
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) this._context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(
						R.layout.adapter_subcat_menu, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.lblListHeader = (TextView) convertView
					.findViewById(R.id.txtsubCategory);
			holder.indicator = (ImageView) convertView
					.findViewById(R.id.ic_img);
			holder.llview = (LinearLayout) convertView
					.findViewById(R.id.grp_view);

			get_count(Temp_main_obj, listDataHeader.get(groupPosition));
			if (GroupSID != groupPosition) {
				holder.llview.setBackgroundColor(getResources().getColor(
						R.color.foodzu_green));
				holder.lblListHeader.setTextColor(getResources().getColor(
						R.color.black));
			} else {

				holder.llview.setBackgroundColor(getResources().getColor(
						R.color.orange));
				holder.lblListHeader.setTextColor(getResources().getColor(
						R.color.White));
			}
			if (getChildrenCount(groupPosition) == 0) {
				holder.indicator.setImageResource(R.drawable.extend);
			} else {
				holder.indicator.setVisibility(View.VISIBLE);
				if (isExpanded)
					holder.indicator.setImageResource(R.drawable.down_up);
				else
					holder.indicator.setImageResource(R.drawable.down_drop);
			}
			// holder.lblListHeader.setTypeface(null, Typeface.BOLD);
			holder.lblListHeader.setText(headerTitle);

			return convertView;
		}

		class ViewHolder {
			TextView lblListHeader;
			ImageView indicator;
			LinearLayout llview;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	// ----------------------------------------------------------------------------------

}
