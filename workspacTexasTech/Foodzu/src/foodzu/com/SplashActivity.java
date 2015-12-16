package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import foodzu.com.Utils.GPSTracker;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.models.Data_Models;
import foodzu.com.models.Products;

public class SplashActivity extends Activity {

	private ArrayList<Products> Products_Arraylist;
	private Products item_of_product, item;
	Data_Models DM;
	GPSTracker gps;
	SharedPreferences sharedpreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);

		DM = new Data_Models();
		Products_Arraylist = new ArrayList<Products>();
		if (new Utils(SplashActivity.this).isNetworkAvailable())

			new getHomeProductList().execute();
		else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					SplashActivity.this);

			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent intent = getIntent();
									finish();
									startActivity(intent);
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
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

	public class getHomeProductList extends AsyncTask<Void, Void, Void> {

		protected void onPreExecute() {

		}

		protected Void doInBackground(Void... params) {
			String homeproducts;
			homeproducts = gethomeproducts();

			Products_Arraylist.clear();
			try {
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
											if (!items.getString("item_name")
													.toString().equals("")
													&& !items
															.getString(
																	"product_wt_1")
															.toString()
															.equals("")) {
												item = new Products();
												item.setitem_id(items
														.getString("item_id")
														.toString());
												item.setitem_name(items
														.getString("item_name")
														.toString());
												item.setactual_price(items
														.getString(
																"product_mrp_1")
														.toString());
												item.setfinal_price(items
														.getString(
																"product_actual_price_1")
														.toString());
												item.setvaliddiscount(items
														.getString(
																"product_offer_1")
														.toString());
												item.setitem_image(items
														.getString("medimg1")
														.toString());
												item.setpd_wieght(items
														.getString(
																"product_wt_1")
														.toString());
												item.setpd_short_description(items
														.getString("small_desc")
														.toString());
												item.setitem_in_cart("NO");
												item.setitem_qty_count(0);
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
												item_of_product
														.setisProduct("Y");
											} else
												item_of_product
														.setisProduct("N");
											// ----------------------------------------------------------------------------------------------------------------
											if (!items.getString("item_name1")
													.toString().equals("")
													&& !items
															.getString(
																	"product_wt_2")
															.toString()
															.equals("")) {
												item = new Products();
												item.setitem_id(items
														.getString("item_id")
														.toString());
												item.setitem_name_1(items
														.getString("item_name1")
														.toString());
												item.setactual_price1(items
														.getString(
																"product_mrp_2")
														.toString());
												item.setfinal_price1(items
														.getString(
																"product_actual_price_2")
														.toString());
												item.setvaliddiscount1(items
														.getString(
																"product_offer_2")
														.toString());
												item.setpd_wieght1(items
														.getString(
																"product_wt_2")
														.toString());
												item.setitem_image(items
														.getString("medimg1")
														.toString());
												item.setpd_short_description(items
														.getString("small_desc")
														.toString());
												item.setisProduct1("Y");
												item.setitem_in_cart1("NO");
												item.setitem_qty_count1(0);
												item_of_product.setprod_1(item);
												item_of_product
														.setisProduct1("Y");
											} else
												item_of_product
														.setisProduct1("N");

											if (!items.getString("item_name2")
													.toString().equals("")
													&& !items
															.getString(
																	"product_wt_3")
															.toString()
															.equals("")) {
												item = new Products();
												item.setitem_id(items
														.getString("item_id")
														.toString());
												item.setitem_name_2(items
														.getString("item_name2")
														.toString());
												item.setactual_price2(items
														.getString(
																"product_mrp_3")
														.toString());
												item.setfinal_price2(items
														.getString(
																"product_actual_price_3")
														.toString());
												item.setvaliddiscount2(items
														.getString(
																"product_offer_3")
														.toString());
												item.setpd_wieght2(items
														.getString(
																"product_wt_3")
														.toString());
												item.setitem_image(items
														.getString("medimg1")
														.toString());
												item.setpd_short_description(items
														.getString("small_desc")
														.toString());
												item.setisProduct2("Y");
												item.setitem_in_cart2("NO");
												item.setitem_qty_count2(0);
												item_of_product.setprod_2(item);
												item_of_product
														.setisProduct2("Y");
											} else
												item_of_product
														.setisProduct2("N");

											if (!items.getString("item_name3")
													.toString().equals("")
													&& !items
															.getString(
																	"product_wt_4")
															.toString()
															.equals("")) {
												item = new Products();
												item.setitem_id(items
														.getString("item_id")
														.toString());
												item.setitem_name_3(items
														.getString("item_name3")
														.toString());
												item.setactual_price3(items
														.getString(
																"product_mrp_4")
														.toString());
												item.setfinal_price3(items
														.getString(
																"product_actual_price_4")
														.toString());
												item.setvaliddiscount3(items
														.getString(
																"product_offer_4")
														.toString());
												item.setpd_wieght3(items
														.getString(
																"product_wt_4")
														.toString());
												item.setitem_image(items
														.getString("medimg1")
														.toString());
												item.setpd_short_description(items
														.getString("small_desc")
														.toString());
												item.setisProduct3("Y");
												item.setitem_in_cart3("NO");
												item.setitem_qty_count3(0);

												item_of_product.setprod_3(item);
												item_of_product
														.setisProduct3("Y");
											} else
												item_of_product
														.setisProduct3("N");

											if (!items.getString("item_name4")
													.toString().equals("")
													&& !items
															.getString(
																	"product_wt_5")
															.toString()
															.equals("")) {
												item = new Products();
												item.setitem_id(items
														.getString("item_id")
														.toString());
												item.setitem_name_4(items
														.getString("item_name4")
														.toString());
												item.setactual_price4(items
														.getString(
																"product_mrp_5")
														.toString());
												item.setfinal_price4(items
														.getString(
																"product_actual_price_5")
														.toString());
												item.setvaliddiscount4(items
														.getString(
																"product_offer_5")
														.toString());
												item.setpd_wieght4(items
														.getString(
																"product_wt_5")
														.toString());
												item.setitem_image(items
														.getString("medimg1")
														.toString());
												item.setpd_short_description(items
														.getString("small_desc")
														.toString());
												item.setisProduct4("Y");
												item.setitem_in_cart4("NO");
												item.setitem_qty_count4(0);
												item_of_product.setprod_4(item);
												item_of_product
														.setisProduct4("Y");
											} else
												item_of_product
														.setisProduct4("N");
											// -----------------------------------------------------------------------------------------------------------------

											Products_Arraylist
													.add(item_of_product);
										}
									}
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			finish();
			startActivity(new Intent(SplashActivity.this, HomeActivity.class));
			Utils.load = "1";
			DM.sethome_data(Products_Arraylist);
		}
	}

	public void getCurrentLocation() {

		gps = new GPSTracker(SplashActivity.this);
		if (gps.canGetLocation()) {

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			Location location = gps.getLocation();

			sharedpreferences = getSharedPreferences("Login",
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

			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putString("myaddress", address);
			Utils.ADD = address;
			editor.commit();
		}

	}

}
