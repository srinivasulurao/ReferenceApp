

package dealsforsure.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationRequest;




import dealsforsure.in.R;
import dealsforsure.in.fragments.FragmentHome;
import dealsforsure.in.fragments.FragmentNavigationDrawer;
import dealsforsure.in.libraries.GPSTracker;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

@SuppressLint("NewApi")
public class ActivityHome extends ActionBarActivity implements 
	FragmentNavigationDrawer.NavigationDrawerCallbacks, 
	FragmentHome.OnDataListSelectedListener,
	SearchView.OnQueryTextListener,
	SearchView.OnSuggestionListener, OnClickListener{
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private SuggestionsAdapter mSuggestionsAdapter;

	private JSONObject pointJson;
	protected GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	TextView tvTitle; 
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private FragmentNavigationDrawer mNavigationDrawerFragment;
   
	private Dialog dialog; 
	
	GPSTracker gps;
	
	private GoogleCloudMessaging gcm;
	
	// Declare object of AdView class
	private AdView adView;
	
	private UserFunctions userFunction;

	// Declare object of Context and Utils class
	private Context ctx;
	private Utils utils;
	private Bundle bundle;
	
	private ActionBar actionbar;
	
	 SharedPreferences sharedPreferences;
	 String userId,userType;
	 String deviceId;
	
	// Use in GCM 
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	//private GoogleCloudMessaging gcm;
	private JSONObject jsonGCM;
	
	// Create variable for email and status value
	private String regid;
	private String mStatus="3";
	
	GetAddressTask  getAddressTask;
	
	
	@SuppressLint("NewApi")
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.actionbar_home, menu);
			
			// get the SearchView and set the searchable configuration
		  /*  SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    SearchView searchView = (SearchView) menu.findItem(R.id.abSearch).getActionView();
		    // assumes current activity is the searchable activity
		    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		    // do not iconify the widget expand it by default
		    searchView.setIconifiedByDefault(false); */
		    
			return true;
		}
		return super.onCreateOptionsMenu(menu);     
    }
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ctx = this;
		setContentView(R.layout.activity_home);
		
		// Declare object of Utils class;
		utils 		= new Utils(this);
		userFunction= new UserFunctions();
		getAddressTask=new GetAddressTask(ActivityHome.this);
		// connect view objects and xml ids
		adView = (AdView)this.findViewById(R.id.adView);
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityHome.this);
		userId = sharedPreferences.getString("userId", null);
		userType = sharedPreferences.getString("type", null);
		deviceId = Secure.getString(ActivityHome.this.getContentResolver(),
                Secure.ANDROID_ID);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		
		editor.remove("userLat");
		editor.remove("userLong");
		editor.commit();
		
	
		
		String userLat = sharedPreferences.getString("userLat", null);
    	String userLong = sharedPreferences.getString("userLong", null);
		
		
		actionbar = getSupportActionBar();
     	//actionbar.setTitle("vvvvvvvv");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.actionbar_home, null);
		
		tvTitle= (TextView) mCustomView.findViewById(R.id.tvtitle);
		//tvCartBadge=(TextView)mCustomView.findViewById(R.id.tab_badge);
		
	//	updateCartBadge();
		
		//tvTitle.setText("My Own Title");
		actionbar.setCustomView(mCustomView);
		actionbar.setDisplayShowCustomEnabled(true);
		
		
		//getCurrentLocation();
 
		/****
		 * set GCM
		 * Check device for Play Services APK. If check succeeds, proceed with
		 * GCM registration.
		 * ***/
		if(utils.isNetworkAvailable()){
			if (checkPlayServices()) {
	        	/* If this check succeeds, proceed with normal processing.
		         Otherwise, prompt user to get valid Play Services APK. */
	            gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(ctx);
	            if (regid.isEmpty()) {
	            	// Call asynctask class
	             	new registerInBackground().execute();
	            }
	            
	            // Condition for admob (0=gone, 1=visible)
	            
	            
	        } else {
	            Log.i("Google Cloud Messangging", "No valid Google Play Services APK found.");
	        }
		}
		
		// Check paramter overlays
		int paramOverlay = utils.loadPreferences(utils.UTILS_OVERLAY);
		
		// Condition if app start in the first time overlay will show 
	//	if(paramOverlay!=1) showOverLay();

		mNavigationDrawerFragment = (FragmentNavigationDrawer) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

     	/* In case this activity was started with special instructions from an Intent,
        Pass the Intent's extras to the fragment as arguments */
       bundle = new Bundle();
       bundle.putString(utils.EXTRA_ACTIVITY, utils.EXTRA_ACTIVITY_HOME);
       
             
       mNavigationDrawerFragment.close(); 

	}	
	
public void updateCartBadge(){
		
		Integer cartCount = sharedPreferences.getInt("cartCount", 0);
		if(cartCount>0){
			
		//	tvCartBadge.setVisibility(View.VISIBLE);
		//	tvCartBadge.setText(cartCount.toString());
			
		}else{
		//	tvCartBadge.setVisibility(View.GONE);
			
		}
		
	}
	
	
	
	protected class GetAddressTask extends AsyncTask<String, Void, JSONObject> {

        // Store the context passed to the AsyncTask when the system instantiates it.
        Context localContext;

        // Constructor called by the system to instantiate the task
        public GetAddressTask(Context context) {

            // Required by the semantics of AsyncTask
            super();

            // Set a Context for the background task
            localContext = context;
        }
        
        Dialog dialog;
        @Override
        protected void onPreExecute() {

			/*dialog = new Dialog(ActivityHome.this);

			dialog.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			dialog.setContentView(R.layout.loading_layout);
			dialog.setCancelable(false);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			dialog.show();*/
        	
        	dialog = Utils.createProgressDialog(ActivityHome.this);
        	dialog.setCancelable(false);
        	dialog.show();
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
        	dialog.dismiss();
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
			reloadMenu();
			
			FragmentHome fragObjList = new FragmentHome();
		       fragObjList.setArguments(bundle);

		        // add the fragment to the 'fragment_container' FrameLayout
		             getSupportFragmentManager().beginTransaction()
		                     .add(R.id.frame_content, fragObjList).commit();
        	
        	//actionbar.setTitle(address);
        	
        	updateActionbarAddress(address);
    	}
        
    }
	
	
	public void updateActionbarAddress(String address){
		
		
		tvTitle.setText(address);
	}
	 
	
	
	public void getCurrentLocation(){
		
		
		
		 
		gps = new GPSTracker(ActivityHome.this);
		if(gps.canGetLocation()){
        	
        	double latitude = gps.getLatitude();
        	double longitude = gps.getLongitude();
        	
        	SharedPreferences.Editor editor = sharedPreferences.edit();
        	
			
			editor.putString("userLat", Double.toString(latitude));
			editor.putString("userLong", Double.toString(longitude));
			editor.commit();
        	getAddressTask.execute(latitude + "," + longitude);
        	
        	// \n is for new line
        	//Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
        }else{
        	// can't get location
        	// GPS or Network is not enabled
        	// Ask user to enable GPS/network in settings
        	gps.showSettingsAlert();
        }
		  
	       
		
	}
	
	
	
	public static String getLocationCityName( double lat, double lon ){
        JSONObject result = getLocationFormGoogle(lat + "," + lon );
        return getCityAddress(result);
    }

    protected static JSONObject getLocationFormGoogle(String placesName) {

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

    protected static String getCityAddress( JSONObject result ){
        if( result.has("results") ){
            try {
                JSONArray array = result.getJSONArray("results");
                if( array.length() > 0 ){
                    JSONObject place = array.getJSONObject(0);
                    String address = place.getString("formatted_address");
                    
                  
                    return address;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
	
	
	 

	
	// Call Activity when Menu Seleceted
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// TODO Auto-generated method stub
		Intent i;
		userId = sharedPreferences.getString("userId", null);
		userType = sharedPreferences.getString("type", null);
		switch (position) {
		
		case 0:
			// Refresh data
			/* In case this activity was started with special instructions from an Intent,
	        Pass the Intent's extras to the fragment as arguments */
	       bundle = new Bundle();
	       bundle.putString(utils.EXTRA_ACTIVITY, utils.EXTRA_ACTIVITY_HOME);
	       FragmentHome fragObjList = new FragmentHome();
	       fragObjList.setArguments(bundle);

	        // add the fragment to the 'fragment_container' FrameLayout
	             getSupportFragmentManager().beginTransaction()
	                     .add(R.id.frame_content, fragObjList).commit();
	        
			break;
			
		case 1:
			// Call ActivityCategory
			i = new Intent(this, ActivityCategory.class);
			startActivity(i);
			
			break;
			
		case 2:
			
			if(userId==null){
			// Call ActivitySetting
			i = new Intent(this, ActivityRegistrationDetail.class);
			}else{
				
				if(userType.equals("user")){
					
					i = new Intent(this, ActivityUserProfile.class);	
				}else{
				i = new Intent(this, ActivityProfile.class);	
				}
			}
			i.putExtra("type", "user");
			startActivity(i);
			
			break;
		
		case 3:
			
			if(userId==null){
			// Call ActivitySetting
			i = new Intent(this, ActivityAbout.class);
			startActivity(i);
			}else{
				
				i = new Intent(this, ActivityMyDeals.class);
				startActivity(i);
				
			}
			
			break;	
			
		case 4:
			if(userId!=null&& userType.equals("merchant")){
			// Call ActivityAbout
				i = new Intent(this, ActivityVerifyCode.class);
				startActivity(i);
				
				
			}else{
				
				i = new Intent(this, ActivityAbout.class);
				startActivity(i);
			}
			
			break;	
			
		case 5:
			if(userId!=null&& userType.equals("merchant")){
				// Call ActivityAbout
					i = new Intent(this, ActivityMyCustomer.class);
					startActivity(i);
					
					
				}else{
			// Call ActivityAbout
			i = new Intent(this, ActivityAbout.class);
			startActivity(i);
				}
			
			break;	
			
		case 6:
			
			// Call ActivityAbout
			i = new Intent(this, ActivityAbout.class);
			startActivity(i);
				
			
			break;	
			
			
		default:
			break;
		}
		
	}
	
	/* Start: GCM proccess */
	 
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    /*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("Google Cloud Messangging", "This device is not supported.");
	            finish();
	            overridePendingTransition (R.anim.open_main, R.anim.close_next);
	        }
	        return false;
	    }
	    
	     CHECK_PLAY_SERV = 1 means Google Play services version on the device 
	    supports the version of the client library you are using 
	    utils.savePreferences(utils.CHECK_PLAY_SERV,1);*/
	    return true;
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		// Check parameter registration id device for Notification(using GCM) in server if paramReg=0 not registered
		String registrationId = utils.loadString(utils.REG_ID);
		
	    if (registrationId.equals(utils.VALUE_DEFAULT)) {
	        Log.i("Google Cloud Messangging", "Registration id not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = utils.loadPreferences(utils.APP_VERSION);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i("Google Cloud Messangging", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public class registerInBackground extends AsyncTask<Void, Void, Void>{
		
    	@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
               /* if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(ctx);
                }*/
            	
            	if (regid.isEmpty()) {
            		
            		/* Looping until get register ID, if get register 
            		id before the target, it will stop */ 
                    for(int i=0;i<60;i++){
                        try{
                        	regid = gcm.register(UserFunctions.SENDER_ID);
                        	Log.i("Google Cloud Messangging", "Error doInBackground:");
                            break;
                            
                        } catch(Exception e){
                        	Log.i("Google Cloud Messangging", "Error doInBackground:" + e.getMessage());
                        	
                            try {
								Thread.sleep(2000);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								Log.i("Google Cloud Messangging", "Error :" + e1.getMessage());
							}
                            continue;
                        }
                    }
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();
                    

                }
            
            return null;
		}
    	
    	@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
    		
            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the regID - no need to register again.
    		if(mStatus.equals("1") || mStatus.equals("2")){
    			storeRegistrationId(ctx, regid);
    		} else {
    			Log.i("Google Cloud Messangging","storeRegistrationId");
    		}
            
		}
	}
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		 try {      	
			if(!regid.isEmpty()) {
				
	        	// Insert Register Id into database server
				String mUniqueId = getUniquePsuedoID();
				
				jsonGCM = userFunction.registerIdGcm(regid, deviceId);
				Log.i("Google Cloud Messangging","getUniquePsuedoID= "+mUniqueId);
				
	            if(jsonGCM != null){
	            	JSONArray jsonArray = jsonGCM.getJSONArray(userFunction.array_register_id);
					JSONObject jsonObj	= jsonArray.getJSONObject(0);
					
					mStatus = jsonObj.getString(userFunction.key_status);
	            }
    		}
			                               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        	Log.i("Google Cloud Messangging","sendRegistrationIdToBackend: "+e);
        }
		
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    int appVersion = getAppVersion(context);
	    utils.savePreferences(utils.APP_VERSION, appVersion);
	    utils.saveString(utils.REG_ID, regId);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
/* End: GCM proccess */	
	
/* Start: Activity life cycle */
	
	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	    reloadMenu();
	    checkLocation();
		userId = sharedPreferences.getString("userId", null);
		/*if (userId != null) {
		new getPointAsyc().execute();
		}*/
		
	}
	
	public void checkLocation(){
		
		
		String userLat = sharedPreferences.getString("userLat", null);
    	String userLong = sharedPreferences.getString("userLong", null);
    	
    	if(userLat==null||userLong==null){
    		if (getAddressTask.getStatus() != AsyncTask.Status.RUNNING) {
    		    getAddressTask=new GetAddressTask(ActivityHome.this);
    		    getCurrentLocation();
    		    }
    		
    	}
	}
	
	public void reloadMenu(){
		
		mNavigationDrawerFragment .reload();
	}
/* End: Activity life cycle */	

/* Start: Get Uniqe Id */	
	public static String getUniquePsuedoID() {
	    // If all else fails, if the user does have lower than API 9 (lower
	    // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
	    // returns 'null', then simply the ID returned will be solely based
	    // off their Android device information. This is where the collisions
	    // can happen.
	    // Thanks http://www.pocketmagic.net/?p=1662!
	    // Try not to use DISPLAY, HOST or ID - these items could change.
	    // If there are collisions, there will be overlapping data
		
		String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

	    // Thanks to @Roman SL!
	    // http://stackoverflow.com/a/4789483/950427
	    // Only devices with API >= 9 have android.os.Build.SERIAL
	    // http://developer.android.com/reference/android/os/Build.html#SERIAL
	    // If a user upgrades software or roots their phone, there will be a duplicate entry
	    String serial = null;
	    try
	    {
	        serial = android.os.Build.class.getField("SERIAL").get(null).toString();

	        // Go ahead and return the serial for api => 9
	        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	    }
	    catch (Exception e)
	    {
	        // String needs to be initialized
	        serial = "serial"; // some value
	    }

	    // Thanks @Joe!
	    // http://stackoverflow.com/a/2853253/950427
	    // Finally, combine the values we have found by using the UUID class to create a unique identifier
	    return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	}

/* End: Get Uniqe Id */

	/* Start: Search method */
	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		 Toast.makeText(this, "You searched for: " + query, Toast.LENGTH_LONG).show();
	     return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class SuggestionsAdapter extends CursorAdapter {

        public SuggestionsAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv = (TextView) view;
            final int textIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
            tv.setText(cursor.getString(textIndex));
        }

    }
	
	@Override
	public boolean onSuggestionSelect(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		// TODO Auto-generated method stub
		Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
        String query = c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        Toast.makeText(this, "Suggestion clicked: " + query, Toast.LENGTH_LONG).show();
        return true;
	}
	
/* End: Search method */
	
	// Listener when item selected 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	   /* switch (item.getItemId()) {
        	case R.id.abSearch:
        	// Call ActivityPlaceAroundYou
            break;
            
	        case R.id.abAroundYou:
	        	// Call ActivityPlaceAroundYou
	        	Intent iCart = new Intent(this, ActivityAroundYou.class);
				startActivity(iCart);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
	            break;

	        default:
	            return super.onOptionsItemSelected(item);
	    }*/
		return super.onOptionsItemSelected(item);
	}

	// Listener when list selected
	@Override
	public void onListSelected(String idSelected) {
		// TODO Auto-generated method stub
		
		// Call ActivityDetailPlace
		Intent i = new Intent(this, ActivityDetail.class);
		i.putExtra(utils.EXTRA_DEAL_ID, idSelected);
		startActivity(i);
		
	}
	
	public void onBackPressed() {
		
		if (mNavigationDrawerFragment.isDrawerOpen())
			mNavigationDrawerFragment.close();
		  else{

		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		finish();
		  }

	}
	
	// Method is used to show overlay when apps starting in the first time.
	private void showOverLay(){

		dialog = new Dialog(ctx, android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setContentView(R.layout.overlay_view);

		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
		layout.setOnClickListener(this);
		dialog.show();

	}
	
	
	private class getPointAsyc extends AsyncTask<Void, Void, Void> {
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		protected Void doInBackground(Void... unused) {
			//pointJson = userFunction.getPoints(userId);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				
				if (pointJson != null) {

					
					String status = pointJson.getString("status");

					if (status.equals("200")) {
						
						
						sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(ActivityHome.this);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						
						String userPoints=pointJson.getString("user_points");
						
						editor.putString("userPoint", userPoints);
						editor.commit();
						
						reloadMenu();
						
					}
				}
					}catch(Exception e){
						
					}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.overlayLayout:
			utils.savePreferences(utils.UTILS_OVERLAY, 1);
			dialog.dismiss();
			break;

		default:
			break;
		}
	}
	
}