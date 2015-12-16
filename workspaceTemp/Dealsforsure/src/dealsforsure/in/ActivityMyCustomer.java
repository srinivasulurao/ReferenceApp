package dealsforsure.in;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import dealsforsure.in.adapters.AdapterMyCustomer;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Customer;
import dealsforsure.in.model.Deals;
import dealsforsure.in.utils.Utils;

public class ActivityMyCustomer extends ActionBarActivity {
	
	private ActionBar actionbar;
	private UserFunctions userFunction;
	private Utils utils;
	ListView lvCustomer;
	String deviceId,tokenKey,userId;
	SharedPreferences sharedPreferences;
	AdapterMyCustomer adapterMyCustomer;
	List<Customer> customerList = new ArrayList<Customer>();
	Customer customer;
	JSONObject regjson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_customer);
		
		lvCustomer=(ListView)findViewById(R.id.lvcustomer);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		utils = new Utils(ActivityMyCustomer.this);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityMyCustomer.this);
		userId = sharedPreferences.getString("userId", null);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		
		
		
		adapterMyCustomer = new AdapterMyCustomer(ActivityMyCustomer.this,
				R.layout.adapter_mydeal, customerList);

		lvCustomer.setAdapter(adapterMyCustomer);
		lvCustomer.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				customer = (Customer) parent.getItemAtPosition(position);

				// tvAddress.setText(address.getAddressName());

				Intent i = new Intent(ActivityMyCustomer.this,
						ActivityCustomerDetail.class);
				i.putExtra("customerId", customer.getCustomerId());

				startActivity(i);

				// alertDialog.cancel();

			}

		});
		
		if (utils.isNetworkAvailable()) {
			new getCustomerList().execute();
		} else {

			Toast.makeText(getBaseContext(), "Error in connection .",
					Toast.LENGTH_LONG).show();

		}

		
	}
	
	
	private class getCustomerList extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;
		
		 @Override
	        protected void onPreExecute() {
	        	
	        	pDialog = Utils.createProgressDialog(ActivityMyCustomer.this);
	    		pDialog.setCancelable(false);
	    		pDialog.show();

	        }


		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getCustomerList(userId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				// progDailog.dismiss();
				if(pDialog.isShowing()) pDialog.dismiss();
				customerList.clear();
				if (regjson != null) {

					JSONObject custObject;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;

						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {

							custObject = dataRegisterArray.getJSONObject(i);

							customer = new Customer();
							customer.setName(custObject.getString("name"));
							customer.setPhone(custObject.getString("phone"));
							customer.setVisitCount(custObject.getString("visited_times"));
							customer.setTotalAmount(custObject.getString("amount"));
							customer.setCustomerId(custObject.getString("uid"));
							customerList.add(customer);

						}

						adapterMyCustomer.notifyDataSetChanged();

						// storeList

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityMyCustomer.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
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
