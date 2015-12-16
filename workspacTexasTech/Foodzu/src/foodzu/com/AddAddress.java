package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.models.Country_Model;
import foodzu.com.models.Data_Models;
import foodzu.com.models.Shipping_Model;

public class AddAddress extends Activity implements OnItemClickListener {

	Utils UTL = new Utils(AddAddress.this);
	Country_Model CM_DATA;
	Shipping_Model Shipping_Address;

	ArrayList<Country_Model> countrylist_array;
	private static Data_Models Country_Arraylist_model;
	Spinner country;
	Button save, cancel;
	ArrayList<String> countries;

	EditText full_name, home_address, landmark, pincode, phone_num;
	AutoCompleteTextView city;
	String username_text, address_text, city_text, landmark_text, pincode_text,
			phonenum_text;
	public ArrayAdapter<String> adapter, adapt;
	ArrayList<String> add_list;
	static String address_input = "";

	private static ArrayList<Country_Model> Country_Arraylist;

	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_address);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		save = (Button) findViewById(R.id.btnsave);
		cancel = (Button) findViewById(R.id.btncancel);

		full_name = (EditText) findViewById(R.id.etfullname);
		home_address = (EditText) findViewById(R.id.etaddress1);
		city = (AutoCompleteTextView) findViewById(R.id.etcity);
		landmark = (EditText) findViewById(R.id.etaddress2);
		pincode = (EditText) findViewById(R.id.etpincode);
		phone_num = (EditText) findViewById(R.id.etphonenumber);

		country = (Spinner) findViewById(R.id.country);
		add_list = new ArrayList<String>();
		Country_Arraylist = new ArrayList<Country_Model>();
		countrylist_array = new ArrayList<Country_Model>();
		countries = new ArrayList<String>();
		if (new Data_Models().getcountrydata() != null) {
			Country_Arraylist = new Data_Models().getcountrydata();

			for (int i = 0; i < Country_Arraylist.size(); i++) {
				countries.add(Country_Arraylist.get(i).getcountry_name());
			}
		} else
			new getCountryData().execute();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, countries);

		HashMap<String, String> USER = (HashMap<String, String>) getIntent()
				.getSerializableExtra("Info");
		if (USER != null) {
			full_name.setText(USER.get("User_Name"));
			home_address.setText(USER.get("User_Address"));
			city.setText(USER.get("User_City"));
			landmark.setText(USER.get("User_Landmark"));
			pincode.setText(USER.get("User_Pincode"));
			phone_num.setText(USER.get("User_Phone"));
			country.setAdapter(adapter);
			country.setSelection(adapter.getPosition(USER.get("User_Country")));
		} else {
			country.setAdapter(adapter);
			country.setSelection(adapter.getPosition("India"));
		}

		country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		city.setAdapter(new GooglePlacesAutocompleteAdapter(this,
				R.layout.googleplace_list));
		city.setOnItemClickListener(this);

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				username_text = ((TextView) full_name).getText().toString();
				address_text = ((TextView) home_address).getText().toString();
				phonenum_text = ((TextView) phone_num).getText().toString();
				pincode_text = ((TextView) pincode).getText().toString();
				city_text = ((TextView) city).getText().toString();
				landmark_text = ((TextView) landmark).getText().toString();

				if (username_text.trim().length() <= 0) {

					Toast.makeText(AddAddress.this, "Please enter User Name",
							Toast.LENGTH_LONG).show();

				} else if (address_text.trim().length() <= 0) {

					Toast.makeText(AddAddress.this,
							"Please enter Full Address", Toast.LENGTH_LONG)
							.show();

				} else if (!(pincode_text.trim().length() == 6)) {

					Toast.makeText(AddAddress.this,
							"Please provide valid Pincode", Toast.LENGTH_LONG)
							.show();

				} else if (city_text.trim().length() <= 0) {

					Toast.makeText(AddAddress.this, "Please enter city",
							Toast.LENGTH_LONG).show();

				} else if (!(phonenum_text.trim().length() == 10)) {

					Toast.makeText(AddAddress.this,
							"Please enter valid 10 digit phone number",
							Toast.LENGTH_LONG).show();

				} else {

					new validatePincode().execute();
				}
			}
		});

	}

	public class getCountryData extends AsyncTask<Void, Void, Void> {

		protected void onPreExecute() {
		}

		protected Void doInBackground(Void... params) {

			String countries = get_countries();
			try {
				if (countries != null && countries.length() > 0) {
					countrylist_array.clear();
					JSONObject jObj_main = new JSONObject(countries);
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject data = result.getJSONObject(i);
							CM_DATA = new Country_Model();
							CM_DATA.setcountry_id(data.getString("countryid"));
							CM_DATA.setcountry_name(data
									.getString("countryname"));
							countrylist_array.add(CM_DATA);
							AddAddress.this.countries.add(data
									.getString("countryname"));
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			Country_Arraylist_model = new Data_Models();
			Country_Arraylist_model.setcountrydata(countrylist_array);
			adapter = new ArrayAdapter<String>(AddAddress.this,
					android.R.layout.simple_spinner_item,
					AddAddress.this.countries);

			country.setAdapter(adapter);
			country.setSelection(adapter.getPosition("India"));
		}
	}

	public class validatePincode extends AsyncTask<Void, Void, String> {

		AlertDialog.Builder builder = new AlertDialog.Builder(AddAddress.this);

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {

			return check_pincode(pincode_text);
		}

		protected void onPostExecute(String result) {
			JSONObject jObj_main;
			try {
				jObj_main = new JSONObject(result);

				if (jObj_main.getInt("success") == 0) {
					builder.setMessage(
							"Delivery not available to the current pincode!")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else {
					Shipping_Address = new Shipping_Model();
					Shipping_Address.setS_name(username_text);
					Shipping_Address.setS_mail(sharedpreferences.getString(
							"user_email", ""));
					Shipping_Address.setS_address(address_text);
					Shipping_Address.setS_landmark(landmark_text);
					Shipping_Address.setS_city(city_text);
					Shipping_Address.setS_country(getcountryid(country
							.getSelectedItem().toString()));
					Shipping_Address.setS_pin(pincode_text);
					Shipping_Address.setS_phone(phonenum_text);
					new save_Shipping_address().execute();

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public class save_Shipping_address extends AsyncTask<Void, Void, String> {

		protected void onPreExecute() {
		}

		protected String doInBackground(Void... params) {
			return saveaddress(Shipping_Address);

		}

		protected void onPostExecute(String result) {
			try {
				if (result != null && result.length() > 0) {
					JSONObject jObj_main = new JSONObject(result);
					if (jObj_main.getInt("success") == 1) {
						onBackPressed();
						finish();
					} else {

					}

				}
			} catch (JSONException e) {
				System.out.println(e);
			}
		}
	}

	public String saveaddress(Shipping_Model data) {
		String result = null;
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("S_email", data.getS_mail()));
			pairs.add(new BasicNameValuePair("S_username", data.getS_name()));
			pairs.add(new BasicNameValuePair("S_address", data.getS_address()));
			pairs.add(new BasicNameValuePair("S_landmark", data.getS_landmark()));
			pairs.add(new BasicNameValuePair("S_city", data.getS_city()));
			pairs.add(new BasicNameValuePair("S_country", data.getS_country()));
			pairs.add(new BasicNameValuePair("S_phonenumber", data.getS_phone()));
			pairs.add(new BasicNameValuePair("S_pincode", data.getS_pin()));

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(URLs.SHIPPING_ADDRESS_URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));

			HttpResponse httpResponse = httpClient.execute(httpPost);

			result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println(result);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}

	public String check_pincode(String pincode) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.CHK_PINCODE_URL + pincode;
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

	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	public static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {

			URL url = new URL(
					"https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
							+ input
							+ "&types=geocode&sensor=false&key=AIzaSyAsPPV7T0TZXhOaMUKKShN9lEwCh-luqRo");

			System.out.println("URL: " + url);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("EXCEPTION_PLACES_API", "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e("EXCEPTION_PLACES_API", "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {

			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString(
						"description"));
				System.out
						.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e("JSON EXCEPTION_PLACES", "Cannot process JSON results", e);
		}

		return resultList;
	}

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public GooglePlacesAutocompleteAdapter(Context context,
				int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	public String getcountryid(String countryname) {

		for (int i = 0; Country_Arraylist.size() > i; i++) {
			if (Country_Arraylist.get(i).getcountry_name().equals(countryname))
				return Country_Arraylist.get(i).getcountry_id();
		}
		return countryname;

	}

	public String get_countries() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.GETCOUNTRYS_URL;
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
}
