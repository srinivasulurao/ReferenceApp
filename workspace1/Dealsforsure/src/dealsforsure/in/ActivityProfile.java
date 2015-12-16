package dealsforsure.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterAddress;
import dealsforsure.in.adapters.AdapterDropDownCategory;
import dealsforsure.in.adapters.AdapterStore;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.AddressObj;
import dealsforsure.in.model.Category;
import dealsforsure.in.model.Store;
import dealsforsure.in.utils.Utils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProfile extends ActionBarActivity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {
	TextView tvAddress,tvUserName,tvPoints,tvMerchantPoints;
	TextView tvAddStore,tvemail,tvrecharecoins;
	PlacesTask placesTask;
	private JSONObject json;
	private UserFunctions userFunction;
	AddressObj addressobj;
	SharedPreferences sharedPreferences;
	// ParserTask parserTask;
	AdapterAddress adapterAddress;
	ListView lvStore, lvAddress;
	String deviceId,tokenKey;
	Store store;
	String userId,userName,userPoints,email,merchantPoints;
	List<Store> storeList = new ArrayList<Store>();
	AdapterStore adapterStore;
	SearchView locationSearchView;
	List<AddressObj> addressList = new ArrayList<AddressObj>();
	List<Category> categoryList = new ArrayList<Category>();
	String categoryId;
	AlertDialog alertDialog;
	private Spinner spCategory;
	private ActionBar actionbar;
	private Utils utils;
	JSONObject regjson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		tvAddStore = (TextView) findViewById(R.id.tvaddStore);
		lvStore = (ListView) findViewById(R.id.lvstore);
		//tvLogout= (TextView) findViewById(R.id.tvlogout);
		tvUserName= (TextView) findViewById(R.id.tvUserName);
		tvPoints= (TextView) findViewById(R.id.tvpoints);
		tvMerchantPoints=(TextView) findViewById(R.id.tvmerchantpoints);
		tvemail= (TextView) findViewById(R.id.tvemail);
		tvrecharecoins= (TextView) findViewById(R.id.tvrecharecoins);
		userFunction = new UserFunctions();
		lvStore.setVisibility(View.GONE);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		utils = new Utils(ActivityProfile.this);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityProfile.this); 
		userId = sharedPreferences.getString("userId", null);
		userName= sharedPreferences.getString("userName", null);
		userPoints= sharedPreferences.getString("userPoint", null);
		merchantPoints= sharedPreferences.getString("merchentPoint", null);
		email= sharedPreferences.getString("userEmail", null);
        tokenKey = sharedPreferences.getString("tokenKey", null);
        deviceId = sharedPreferences.getString("deviceId", null);
		tvUserName.setText("Name:"+userName);
		tvemail.setText("E mail:"+email);
		tvPoints.setText("User Coins: "+userPoints);
		tvMerchantPoints.setText("Merchant Coins: "+merchantPoints);
		
		adapterStore = new AdapterStore(ActivityProfile.this,
				R.layout.adapter_store, storeList);

		lvStore.setAdapter(adapterStore);

		lvStore.setVisibility(View.VISIBLE);

		lvStore.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {

				Store store = (Store) parent
						.getItemAtPosition(position);

				// tvAddress.setText(address.getAddressName());

				Intent i = new Intent(ActivityProfile.this,
						ActivityAddDeals.class);
				i.putExtra("storeId", store.getStoreId());

				startActivity(i);

				//alertDialog.cancel();

			}

		});
		
		tvrecharecoins.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i = new Intent(ActivityProfile.this,
						ActivityRechargeCoins.class);
			i.putExtra("type", "merchant");

				startActivity(i);
				
			}
		});


		if (utils.isNetworkAvailable()) {
			new getStoreList().execute();
		} else {
			
			Toast.makeText(getBaseContext(), "Error in connection .",
					Toast.LENGTH_LONG).show();

		}
		
		/*tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//displayAddStore();
				
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				editor.clear();
				editor.commit();
				
				
				Intent i = new Intent(ActivityProfile.this,
						ActivityHome.class);
				

				startActivity(i);

			}
		});*/

		tvAddStore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayAddStore();

			}
		});

	}

	void displayAddStore() {

		ImageView ivClose;
		Button btSave;
		final EditText etName,etaddress,etlocality;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_addstore, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setView(promptsView);
		final AlertDialog alertDialog = alertDialogBuilder.create();
		addressobj = null;
		categoryId = null;
		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.argb(0, 0, 0, 0)));

		alertDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.show();

		ivClose = (ImageView) promptsView.findViewById(R.id.ivclose);
		new getCategoryAsyncTask().execute();

		tvAddress = (TextView) promptsView.findViewById(R.id.atv_places);
		spCategory = (Spinner) promptsView.findViewById(R.id.spcategory);
		btSave = (Button) promptsView.findViewById(R.id.btnsave);
		etName = (EditText) promptsView.findViewById(R.id.etstorename);
		etaddress= (EditText) promptsView.findViewById(R.id.etaddress);
		etlocality= (EditText) promptsView.findViewById(R.id.etlocality);
		
		tvAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				displaySearchLocation();

				// alertDialog.cancel();
			}
		});

		btSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				store = new Store();

				String Name = ((TextView) etName).getText().toString();
				String address = ((TextView) etaddress).getText().toString();
				String locality = ((TextView) etlocality).getText().toString();

				if (Name.length() == 0) {
					Toast.makeText(getBaseContext(), "Please enter Name .",
							Toast.LENGTH_LONG).show();

				}else if(address.length()== 0){
					Toast.makeText(getBaseContext(), "Please address .",
							Toast.LENGTH_LONG).show();
					
				}else if(locality.length()== 0){
					Toast.makeText(getBaseContext(), "Please Locality .",
							Toast.LENGTH_LONG).show();
					
				}
				else if (categoryId == null || categoryId.equals("0")) {

					Toast.makeText(getBaseContext(),
							"Please select Category .", Toast.LENGTH_LONG)
							.show();

				}
				else {
					try {
						store.setStoreName(URLEncoder.encode(Name,
								"UTF-8"));
					
					store.setUserId(userId);
					store.setAddress(URLEncoder.encode(address,
							"UTF-8"));
					store.setLocality(URLEncoder.encode(locality,
							"UTF-8"));
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (addressobj != null) {
						store.setStoreAddress(addressobj.getAddressName());
						store.setPlaceId(addressobj.getAddressId());

					} else {
						Toast.makeText(getBaseContext(),
								"Please enter Google Map Address .", Toast.LENGTH_LONG)
								.show();

					}
					if (categoryId != null) {

						store.setCategoryId(categoryId);
					}

					if (utils.isNetworkAvailable()) {
						new SaveStoreView().execute();
					} else {

					}

				
					alertDialog.cancel();

					

				}

			}
		});

		ivClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				alertDialog.cancel();
			}
		});

	}

	

	void displaySearchLocation() {

		ImageView ivClose;
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.dialog_search_location, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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

				adapterAddress = new AdapterAddress(ActivityProfile.this,
						R.layout.adapter_address, addressList);

				lvAddress.setAdapter(adapterAddress);

				lvAddress.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						addressobj = (AddressObj) parent
								.getItemAtPosition(position);

						tvAddress.setText(addressobj.getAddressName());

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

	private class getStoreList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;
		
		@Override
        protected void onPreExecute() {
        	
        	pDialog = Utils.createProgressDialog(ActivityProfile.this);
    		pDialog.setCancelable(false);
    		pDialog.show();

        }

		
		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getStoreList(userId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				//progDailog.dismiss();
				if(pDialog.isShowing()) pDialog.dismiss();
				storeList.clear();
				if (regjson != null) {

					/*
					 * JSONArray dataRegisterArray;
					 * 
					 * dataRegisterArray = regjson.getJSONArray("result");
					 */

					JSONObject storeObject;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;

						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {

							storeObject = dataRegisterArray.getJSONObject(i);
							store = new Store();
							store.setStoreName(storeObject
									.getString("store_name"));
							store.setStoreId(storeObject.getString("sid"));
							storeList.add(store);

						}
						
						userPoints= regjson.getString("user_points");
						merchantPoints= regjson.getString("merchant_points");
						
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userPoint", userPoints);
						editor.putString("merchentPoint", merchantPoints);
						editor.commit();
						
						tvPoints.setText("User Coins: "+userPoints);
						tvMerchantPoints.setText("Merchant Coins: "+merchantPoints);
						adapterStore.notifyDataSetChanged();

						
						// storeList

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityProfile.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	

	private class SaveStoreView extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = Utils.createProgressDialog(ActivityProfile.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.saveStore(store,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				if(pDialog.isShowing()) pDialog.dismiss();

				if (regjson != null) {

					JSONArray dataRegisterArray;

					/*dataRegisterArray = regjson.getJSONArray("result");

					JSONObject dealsObject = dataRegisterArray.getJSONObject(0);*/

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						store=new Store();
						store.setStoreName(regjson.getString("store_name"));
						store.setStoreId(regjson.getString("sid"));
						
						storeList.add(store);
						adapterStore.notifyDataSetChanged();
						} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityProfile.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class getCategoryAsyncTask extends AsyncTask<String, Void, String> {
		Dialog dialog;

		@Override
		protected String doInBackground(String... urls) {
			publishProgress((Void[]) null);
			Category category;
			try {
				categoryList.clear();
				category = new Category();
				category.setCategoryId("0");
				category.setCategoryName("--Select--");
				categoryList.add(category);
				
				json = userFunction.spinnerCategoryList(tokenKey,deviceId);
				if (json != null) {
					JSONArray dataDealsArray = json
							.getJSONArray(userFunction.array_category_list);

					for (int i = 0; i < dataDealsArray.length(); i++) {
						JSONObject dealObject = dataDealsArray.getJSONObject(i);

						category = new Category();
						category.setCategoryId(dealObject
								.getString(userFunction.key_category_id));
						category.setCategoryName(dealObject
								.getString(userFunction.key_category_name));
						categoryList.add(category);
					}

				}
			} catch (Exception e) {

			}

			return null;
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
				spCategory.setAdapter(new AdapterDropDownCategory(
						ActivityProfile.this, R.layout.spinner_content,
						categoryList));

				spCategory
						.setOnItemSelectedListener(new CategorySelectedListener());

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

			categoryId = category.getCategoryId();

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}

	// Listener for option menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Previous page or exit
			finish();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
