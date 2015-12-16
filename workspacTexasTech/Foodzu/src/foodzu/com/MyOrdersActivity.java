package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;
import foodzu.com.adapters.OldOrdersAdapter;
import foodzu.com.models.Ordered_Products;
import foodzu.com.models.Orders_Model;

public class MyOrdersActivity extends Activity {

	ArrayList<Orders_Model> MyOrders_Arraylist;
	ArrayList<Ordered_Products> Product_Arraylist;
	Orders_Model Orderlist;
	Ordered_Products productlist;
	SharedPreferences sharedpreferences;
	public static final String MyLogin = "Login";
	static String USER_ID = "";
	private OldOrdersAdapter O_Adapter;
	ListView Prevorder_list;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorders);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		MyOrders_Arraylist = new ArrayList<Orders_Model>();
		Product_Arraylist = new ArrayList<Ordered_Products>();
		sharedpreferences = getSharedPreferences(MyLogin, Context.MODE_PRIVATE);
		USER_ID = sharedpreferences.getString("user_id", "");

		Prevorder_list = (ListView) findViewById(R.id.myorder_list);

		load_oldorders();

	}

	void load_oldorders() {
		if (new Utils(MyOrdersActivity.this).isNetworkAvailable())
			new getPrevOrderList().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									load_oldorders();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
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

	public String getPrevious_orders(String user_id) {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.GET_PREV_ORDERS_URL + user_id;

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

	public class getPrevOrderList extends AsyncTask<Void, Void, String> {

		Dialog dialog;
		String Main = "Y";

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(MyOrdersActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(Void... params) {
			String AllOrders;
			AllOrders = getPrevious_orders(USER_ID);

			try {
				if (AllOrders != null && AllOrders.length() > 0) {
					MyOrders_Arraylist.clear();
					JSONObject jObj_main = new JSONObject(AllOrders);
					JSONArray result = jObj_main.getJSONArray("result");
					if (result.length() > 0) {
						for (int i = 0; i < result.length(); i++) {
							JSONObject data = result.getJSONObject(i);
							Orderlist = new Orders_Model();
							Orderlist.setOrder_ID(data
									.getString("good_order_id"));
							Orderlist.setTransaction_ID(data
									.getString("txn_id"));
							Orderlist.setShipping_ID(data
									.getString("shipping_id"));
							Orderlist.setOrder_Total_Amount(data
									.getString("anount_paid"));
							Orderlist
									.setOrder_Date(data.getString("paid_date"));

							JSONArray orders = data.getJSONArray("orderItems");
							if (orders.length() > 0) {
								Product_Arraylist.clear();
								for (int j = 0; j < orders.length(); j++) {
									JSONObject order_data = orders
											.getJSONObject(j);

									productlist = new Ordered_Products();
									System.out.println(order_data.getString("product_name"));
									productlist.setPrd_ID(order_data
											.getString("productid"));
									productlist.setPrd_Cat_ID(order_data
											.getString("cat_id"));
									if (order_data.getString("product_name")
											.equals("") || order_data.getString("product_name") == null)
										productlist.setPrd_Name("SAMPLE");
									else
										productlist.setPrd_Name(order_data
												.getString("product_name"));
									productlist.setPrd_Alias(order_data
											.getString("product_alias"));
									productlist.setPrd_Qty(order_data
											.getString("quantity"));
									productlist.setPrd_Img(order_data
											.getString("image"));
									Product_Arraylist.add(productlist);

								}
							}
							Orderlist.getProducts_data().addAll(
									Product_Arraylist);

							MyOrders_Arraylist.add(Orderlist);
						}

					}

				}
			} catch (JSONException e) {
				System.out.println(e);
			}

			return "";
		}

		protected void onPostExecute(String result) {

			dialog.dismiss();
			
			O_Adapter = new OldOrdersAdapter(MyOrdersActivity.this,
					MyOrders_Arraylist);
			Prevorder_list.setAdapter(O_Adapter);
		}
	}

}
