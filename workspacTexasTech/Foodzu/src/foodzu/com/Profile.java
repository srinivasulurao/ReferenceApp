package foodzu.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;

public class Profile extends Activity {

	EditText Name, EmailId, Mobile;
	ImageView iveditaddrress;
	TextView tvaddress;
	HashMap<String, String> USER;
	AlertDialog.Builder alertDialogBuilder;
	AlertDialog alertDialog;
	SharedPreferences sharedpreferences;
	String RUN = "NO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
		Name = (EditText) findViewById(R.id.name_et);
		EmailId = (EditText) findViewById(R.id.email_et);
		Mobile = (EditText) findViewById(R.id.mobile_et);
		iveditaddrress = (ImageView) findViewById(R.id.ivedit);
		tvaddress = (TextView) findViewById(R.id.tvaddress);

		iveditaddrress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Profile.this, AddAddress.class)
						.putExtra("Info", USER));
				RUN = "YES";
			}
		});

		user();
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

	@Override
	public void onResume() {
		super.onResume();
		if (RUN.equals("YES")) {
			user();
			RUN = "NO";
		}
	}

	public boolean IsInternetPresent() {
		if (new Utils(Profile.this).isNetworkAvailable())
			return true;
		else
			return false;
	}

	void user() {
		if (IsInternetPresent())
			new getUserData().execute();
		else {
			alertDialogBuilder
					.setMessage("Internet/Mobile Data Not Available!")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									user();
								}
							});
			alertDialog = alertDialogBuilder.create();
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialog.show();
		}
	}

	public String get_userDetails(String email) {
		String result = null;
		String URL = URLs.FETCH_ADDRESS_URL;
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("emailid", email));

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(URL);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			System.out.println(pairs);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	public class getUserData extends AsyncTask<Void, Void, Void> {

		Dialog dialog;
		String no_add = "";

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(Profile.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected Void doInBackground(Void... params) {
			String userDetails;
			userDetails = get_userDetails(sharedpreferences.getString(
					"user_email", ""));
			try {
				if (userDetails != null && userDetails.length() > 0) {

					JSONObject jObj_main = new JSONObject(userDetails);

					JSONObject result = jObj_main.getJSONObject("resultdata");
					if (result.getInt("is_address") == 0)
						no_add = "0";
					else {
						JSONArray user_detail = result
								.getJSONArray("user_address");
						if (user_detail.length() > 0) {
							for (int i = 0; i < user_detail.length(); i++) {
								USER = new HashMap<String, String>();
								JSONObject data = user_detail.getJSONObject(i);
								USER.put("User_Name",
										data.getString("first_name"));
								USER.put("User_Email", data.getString("email"));
								USER.put("User_Address",
										data.getString("address"));
								USER.put("User_City", data.getString("city"));
								USER.put("User_Pincode",
										data.getString("pincode"));
								USER.put("User_Landmark",
										data.getString("landmark"));
								USER.put("User_Phone", data.getString("phone"));
								USER.put("User_Country",
										data.getString("country"));
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
			if (no_add.equals("0")) {
				Name.setText("FOODZU USER");
				EmailId.setText(sharedpreferences.getString("user_email", ""));
				Mobile.setText("Not-Available");
				tvaddress.setText(sharedpreferences.getString("myaddress", ""));
			} else {
				String Address = USER.get("User_Address") + "\n"
						+ USER.get("User_City") + "\n"
						+ USER.get("User_Landmark") + "\n"
						+ USER.get("User_Pincode") + "\n"
						+ USER.get("User_Country");
				Name.setText(USER.get("User_Name"));
				EmailId.setText(USER.get("User_Email"));
				Mobile.setText(USER.get("User_Phone"));
				tvaddress.setText(Address);
			}
			dialog.dismiss();
		}
	}

}
