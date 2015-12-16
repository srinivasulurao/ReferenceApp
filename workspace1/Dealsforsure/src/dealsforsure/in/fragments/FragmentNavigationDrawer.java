package dealsforsure.in.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

import dealsforsure.in.ActivityHome;
import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterAddress;
import dealsforsure.in.adapters.AdapterMenu;
import dealsforsure.in.libraries.GPSTracker;
import dealsforsure.in.model.AddressObj;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class FragmentNavigationDrawer extends Fragment implements
SearchView.OnQueryTextListener, SearchView.OnCloseListener{

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;
	
	SharedPreferences sharedPreferences;
	ListView  lvAddress;
	SearchView locationSearchView;
	TextView mLocation;
	ImageView ivChangeLocation;
	LinearLayout lleditloc,lladdress;
	AlertDialog alertDialog;
	List<AddressObj> addressList = new ArrayList<AddressObj>();
	AdapterAddress adapterAddress;
	PlacesTask placesTask;
	AddressObj addressobj;
	GPSTracker gps;
	
	
	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;
	String userId,userType,address;

	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	// Declare object of AdapterMenuList class
	private AdapterMenu adapterMenu;
	
	// Icon in sliding menu (drawer)
	public static String[] listMenu;
	public static int[] imageMenu= new int[]{
		R.drawable.ic_latest_blue, R.drawable.ic_collection_blue,R.drawable.ic_profile_blue, 
		R.drawable.ic_settings_blue, R.drawable.ic_about_blue
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Read in the flag indicating whether or not the user has demonstrated
		 awareness of the drawer. See PREF_USER_LEARNED_DRAWER for details. */
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
	
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
		
		mLocation=(TextView)v.findViewById(R.id.tvLocation);
		ivChangeLocation=(ImageView)v.findViewById(R.id.ic_edit_loc);
		// connect view objects and xml ids
		mDrawerListView = (ListView) v.findViewById(R.id.list);
		lleditloc=(LinearLayout)v.findViewById(R.id.lleditloc);
		lladdress=(LinearLayout)v.findViewById(R.id.lladdress);
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		userId = sharedPreferences.getString("userId", null);
		userType= sharedPreferences.getString("type", null);
		// Create adapterMenu object
		adapterMenu = new AdapterMenu(getActivity());
		
		address = sharedPreferences.getString("address", null);
		mLocation.setText(address);
		
		lladdress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		lleditloc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				displaySelectType();
				
				
			}
		});
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});
		
		// Execute Asyntask to get Menu List
		new getMenuList().execute();
			
		return v;
	}
	
	
	
	void displaySelectType() {

		TextView tvpicklocality,tvmylocation;
		Button btclose;
		
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_select_address, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		

		tvpicklocality = (TextView) promptsView.findViewById(R.id.tvpicklocality);
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
				
				//getCurrentLocation();

				alertDialog.cancel();
			}
		});

	}
	
	
	protected class getAddressTask extends AsyncTask<String, Void, JSONObject> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public getAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }

        /*//**
         * get a geocoding service instance, pass latitude and longitude to it, format the returned
         * address, and return the address to the UI thread.
         */
        @Override
        protected JSONObject doInBackground(String... params) {
            
         
            String placesName = params[0];
            

            String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + placesName; //+ "&ka&sensor=false"
           
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

            // Set the address in the UI
        	//lblPosition.setText(address);
        	 String address = null;
        	try {
                JSONArray array = result.getJSONArray("results");
                if( array.length() > 0 ){
                    JSONObject place = array.getJSONObject(0);
                   address = place.getString("formatted_address");
                    
                  
                    
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        	SharedPreferences.Editor editor = sharedPreferences.edit();
			
			editor.putString("address", address);
			editor.commit();
			reload();
			((ActivityHome)getActivity()).updateActionbarAddress(address);
    	}
        
    }
	
	
	
	 
	
	
	public void getCurrentLocation(){
		
		 gps = new GPSTracker(getActivity());
		if(gps.canGetLocation()){
        	
        	double latitude = gps.getLatitude();
        	double longitude = gps.getLongitude();
        	
        	Location location=gps.getLocation();
        	
            SharedPreferences.Editor editor = sharedPreferences.edit();
        	editor.putString("userLat", Double.toString(latitude));
			editor.putString("userLong", Double.toString(longitude));
			editor.commit();
        	
        	(new getAddressTask(getActivity())).execute(latitude + "," + longitude);
        	
        	// \n is for new line
        	//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
        }else{
        	// can't get location
        	// GPS or Network is not enabled
        	// Ask user to enable GPS/network in settings
        	gps.showSettingsAlert();
        }
		 
	       
		
	}

	void displaySearchLocation() {

		ImageView ivClose;
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView = li.inflate(R.layout.dialog_search_location, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
						SharedPreferences.Editor editor = sharedPreferences.edit();
						
						editor.putString("address", addressobj.getAddressName());
						editor.commit();
						
						reload();
						
						((ActivityHome)getActivity()).updateActionbarAddress(addressobj.getAddressName());
						
						new getLocationTask().execute(addressobj.getAddressId());
						
						
						

						/*addressobj = (Address) parent
								.getItemAtPosition(position);

						tvAddress.setText(addressobj.getAddressName());*/

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
				JSONObject geoObject=resultObject.getJSONObject("geometry");
				JSONObject locObject=geoObject.getJSONObject("location");
				
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



	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}
	
	public void close(){
		
		 mDrawerLayout.closeDrawer(Gravity.START);
	}
	public void reload(){
		userId = sharedPreferences.getString("userId", null);
		userType= sharedPreferences.getString("type", null);
		
		address = sharedPreferences.getString("address", null);
		mLocation.setText(address);
		
		new getMenuList().execute();
		
		// mDrawerLayout.closeDrawer(Gravity.START);
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

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
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

	private void selectItem(int position) {
		
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement .");
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
		
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
	
	// AsyncTask to get Menu list
	public class getMenuList extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Resources res = getResources();
			
			if (userId == null) {

				listMenu = res.getStringArray(R.array.menu_list);
			 imageMenu= new int[]{
						R.drawable.ic_latest_blue, R.drawable.ic_collection_blue,R.drawable.ic_profile_blue, 
						 R.drawable.ic_about_blue};
			} else {
				
				if(userType.equals("user")){
				listMenu = res.getStringArray(R.array.menu_profile_list);
				
				 imageMenu= new int[]{
						R.drawable.ic_latest_blue, R.drawable.ic_collection_blue,R.drawable.ic_profile_blue, R.drawable.mydeals,
						 R.drawable.ic_about_blue};
				}else{
					listMenu = res.getStringArray(R.array.menu_merchant_profile_list);
					
					 imageMenu= new int[]{
							R.drawable.ic_latest_blue, R.drawable.ic_collection_blue,R.drawable.ic_profile_blue, R.drawable.mydeals,
							R.drawable.verify_code,R.drawable.ic_profile_blue, R.drawable.ic_about_blue};
					
				}

			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mDrawerListView.setAdapter(adapterMenu);
		}
	}
}
