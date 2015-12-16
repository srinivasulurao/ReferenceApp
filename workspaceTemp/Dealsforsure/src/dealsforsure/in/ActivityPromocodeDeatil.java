package dealsforsure.in;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.ActivityMerchantDeal.getDealAsync;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class ActivityPromocodeDeatil extends ActionBarActivity { 
	
	
	JSONObject regjson,resultjson;
	UserFunctions userFunction;
	String userId, userName, Type,userCode,cid,promocode,type,oid;
	SharedPreferences sharedPreferences;
	private ActionBar actionbar;
	private Utils utils;
	String deviceId,tokenKey;
	private Double mDblLatitude;
	private Double mDblLongitude;
	private String mIcMarker;
	
	private TextView lblCompany, lblTitle, lblAddress,  lblDateStart, lblDateEnd,couponcode,tvdescription,
	lblofferdeatil,tvamount,tvquantity;
	LinearLayout lvprice,lycoupencode;
private ImageView imgThumbnail;
RelativeLayout rlBody;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promocode_detail);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		utils = new Utils(ActivityPromocodeDeatil.this);
		Bundle b = getIntent().getExtras();
		type=b.getString("type");
		
		
		
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityPromocodeDeatil.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		userId = sharedPreferences.getString("userId", null);
		rlBody=(RelativeLayout)findViewById(R.id.lytDetail);
		lblCompany=(TextView)findViewById(R.id.lblCompany);
		lblTitle=(TextView)findViewById(R.id.lblTitle);
		lblAddress=(TextView)findViewById(R.id.lblAddress);
		lblDateStart=(TextView)findViewById(R.id.lblStartDateValue);
		lblDateEnd=(TextView)findViewById(R.id.lblEndDateValue);
		couponcode=(TextView)findViewById(R.id.couponcode);
		lblofferdeatil=(TextView)findViewById(R.id.lblofferdeatil);
		tvdescription=(TextView) findViewById(R.id.tvdescription);
		tvamount=(TextView) findViewById(R.id.tvamount);
		tvquantity=(TextView) findViewById(R.id.tvquantity);
	//	icMarker=(ImageView)findViewById(R.id.icMarker);
		lvprice=(LinearLayout) findViewById(R.id.lvprice);
		lycoupencode=(LinearLayout) findViewById(R.id.lycoupencode);
		imgThumbnail=(ImageView)findViewById(R.id.imgThumbnail);
		
		if(type.equals("deal")){
			cid = b.getString("cid");
			promocode= b.getString("promocode");
			lvprice.setVisibility(View.GONE);
			lycoupencode.setVisibility(View.VISIBLE);
		if (utils.isNetworkAvailable()) {
			new getDealAsync().execute();
		} else {

			Toast.makeText(ActivityPromocodeDeatil.this, "Error in connection .",
					Toast.LENGTH_LONG).show();

		}
		}else{
			oid= b.getString("oid");
			lycoupencode.setVisibility(View.GONE);
			lvprice.setVisibility(View.VISIBLE);
			if (utils.isNetworkAvailable()) {
				new getOrderAsync().execute();
			} else {

				Toast.makeText(ActivityPromocodeDeatil.this, "Error in connection .",
						Toast.LENGTH_LONG).show();

			}
			
			
		}
		
/*icMarker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i;
				i = new Intent(ActivityPromocodeDeatil.this, ActivityDirection.class);
				i.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
				i.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
				i.putExtra(utils.EXTRA_CATEGORY_MARKER, mIcMarker);
				startActivity(i);
				
			}
		});*/
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.aactionbar_detail, menu);
		
		return true;
	}
	
	public class getDealAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			progress = Utils.createProgressDialog(ActivityPromocodeDeatil.this);
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
					    String mdescription=resultjson.getString("description");
						
						mIcMarker = resultjson
								.getString(userFunction.key_category_marker);
						mDblLatitude = resultjson
								.getDouble(userFunction.key_deals_lat);
						mDblLongitude = resultjson
								.getDouble(userFunction.key_deals_lng);
						
						
						rlBody.setVisibility(View.VISIBLE);
						
						lblCompany.setText(storeName);
						lblTitle.setText(title);
						lblAddress.setText(address);
						lblDateStart.setText(startDate);
						lblDateEnd.setText(endDate);
						couponcode.setText(promocode);
						lblofferdeatil.setText(offerDetail);
						tvdescription.setText(mdescription);

						Picasso.with(getApplicationContext())
								.load(userFunction.URLAdmin+userFunction.folderAdmin + imageUrl).fit()
								.centerCrop().tag(getApplicationContext())
								.into(imgThumbnail);

						/*Picasso.with(ActivityPromocodeDeatil.this)
						.load(userFunction.URLAdmin + userFunction.folderAdmin
								+ mIcMarker).fit()
						.centerCrop().tag(ActivityPromocodeDeatil.this).into(icMarker);*/

					}else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityPromocodeDeatil.this, errormessage,
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
	
	
	public class getOrderAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			progress = Utils.createProgressDialog(ActivityPromocodeDeatil.this);
			progress.setCancelable(false);
			progress.show();

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();
			//String identifier = params[0];

			regjson = userFunction.getOrderDetail(oid,userId,deviceId,tokenKey);

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
						
						String storeName=resultjson.getString("store_name");
						String title=resultjson.getString("title");
						String address=resultjson.getString("address");
						String imageUrl=resultjson.getString("image");
						String offerDetail=resultjson.getString("discount_type");
						
						String startDate=resultjson.getString("start_date");
						String quantity=resultjson.getString("order_qty");
						String amount=resultjson.getString("amount");
						
					
					    String mdescription=resultjson.getString("description");
						
						mIcMarker = resultjson
								.getString(userFunction.key_category_marker);
						mDblLatitude = resultjson
								.getDouble(userFunction.key_deals_lat);
						mDblLongitude = resultjson
								.getDouble(userFunction.key_deals_lng);
						
						
						rlBody.setVisibility(View.VISIBLE);
						
						lblCompany.setText(storeName);
						lblTitle.setText(title);
						lblAddress.setText(address);
						lblDateStart.setText(startDate);
						tvamount.setText(amount);
						tvquantity.setText(quantity);
						couponcode.setText(promocode);
						lblofferdeatil.setText(offerDetail);
						tvdescription.setText(mdescription);

						Picasso.with(getApplicationContext())
								.load(userFunction.URLAdmin+userFunction.folderAdmin + imageUrl).fit()
								.centerCrop().tag(getApplicationContext())
								.into(imgThumbnail);

						/*Picasso.with(ActivityPromocodeDeatil.this)
						.load(userFunction.URLAdmin + userFunction.folderAdmin
								+ mIcMarker).fit()
						.centerCrop().tag(ActivityPromocodeDeatil.this).into(icMarker);*/

					}else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityPromocodeDeatil.this, errormessage,
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
			
		case R.id.mapIcon:
			
			Intent is;
			is = new Intent(ActivityPromocodeDeatil.this, ActivityDirection.class);
			is.putExtra(utils.EXTRA_DEST_LAT, mDblLatitude);
			is.putExtra(utils.EXTRA_DEST_LNG, mDblLongitude);
			is.putExtra(utils.EXTRA_CATEGORY_MARKER, mIcMarker);
			startActivity(is);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		
		return true;
	}

}
