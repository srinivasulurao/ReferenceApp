package dealsforsure.in;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class ActivityMerchantDeal extends ActionBarActivity { 
	
	
	JSONObject regjson,resultjson;
	UserFunctions userFunction;
	String userId, userName, Type,userCode,cid,deviceId,tokenKey;
	SharedPreferences sharedPreferences;
	private ActionBar actionbar;
	private Utils utils;
	
	private TextView lblCompany, lblTitle, lblAddress,  lblDateStart, lblDateEnd,lblView,lblGet,lblBuy,
	lblofferdeatil;
private ImageView imgThumbnail;
RelativeLayout rlBody;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_deal);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		utils = new Utils(ActivityMerchantDeal.this);
		Bundle b = getIntent().getExtras();
		cid = b.getString("cid");
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityMerchantDeal.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		
		rlBody=(RelativeLayout)findViewById(R.id.lytDetail);
		lblCompany=(TextView)findViewById(R.id.lblCompany);
		lblTitle=(TextView)findViewById(R.id.lblTitle);
		lblAddress=(TextView)findViewById(R.id.lblAddress);
		lblDateStart=(TextView)findViewById(R.id.lblStartDateValue);
		lblDateEnd=(TextView)findViewById(R.id.lblEndDateValue);
		
		lblofferdeatil=(TextView)findViewById(R.id.lblofferdeatil);
		lblView=(TextView)findViewById(R.id.lblViewValue);
		lblGet=(TextView)findViewById(R.id.lblGetValue);
		lblBuy=(TextView)findViewById(R.id.lblBuyValue);
		
		imgThumbnail=(ImageView)findViewById(R.id.imgThumbnail);
		
		if (utils.isNetworkAvailable()) {
			new getDealAsync().execute();
		} else {

			Toast.makeText(ActivityMerchantDeal.this, "Error in connection .",
					Toast.LENGTH_LONG).show();

		}
		
		
		
	}
	
	
	
	public class getDealAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			/*progress = ProgressDialog.show(ActivityMerchantDeal.this, "",
					getString(R.string.loading_data), true);*/
			
			progress = Utils.createProgressDialog(ActivityMerchantDeal.this);
			progress.setCancelable(false);
			progress.show();

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();
			//String identifier = params[0];

			regjson = userFunction.getAnalyticDeal(cid,deviceId,tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			try {
				if (regjson != null) {

					String status = regjson.getString("status");

					if (status.equals("200")) {
						
						JSONArray dataRegisterArray=regjson.getJSONArray("result");
						
						resultjson=dataRegisterArray.getJSONObject(0);
						
						String storeName=resultjson.getString("company");
						String title=resultjson.getString("title");
						String address=resultjson.getString("address");
						String imageUrl=resultjson.getString("image");
						String offerDetail=resultjson.getString("discount_type");
						String points=resultjson.getString("points_for_deal");
						String startDate=resultjson.getString("start_date");
						String endDate=resultjson.getString("end_date");
						
						String viewCount=resultjson.getString("viewed_it");
						String getCount=resultjson.getString("get_it");
						String buyCount=resultjson.getString("buy_it");
						
						
						rlBody.setVisibility(View.VISIBLE);
						
						lblCompany.setText(storeName);
						lblTitle.setText(title);
						lblAddress.setText(address);
						lblDateStart.setText(startDate);
						lblDateEnd.setText(endDate);
					
						lblofferdeatil.setText(offerDetail);
						lblView.setText(viewCount);
						lblGet.setText(getCount);
						lblBuy.setText(buyCount);

						Picasso.with(getApplicationContext())
								.load(userFunction.URLAdmin+userFunction.folderAdmin + imageUrl).fit()
								.centerCrop().tag(getApplicationContext())
								.into(imgThumbnail);

						

					}else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityMerchantDeal.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}
			} catch (Exception e) {

			}

			if (progress.isShowing()) {
				progress.dismiss();
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
