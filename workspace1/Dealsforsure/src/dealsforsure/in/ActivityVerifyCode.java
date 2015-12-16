package dealsforsure.in;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class ActivityVerifyCode extends ActionBarActivity {
	
	JSONObject regjson,resultjson;
	UserFunctions userFunction;
	String userId, userName, Type,userCode;
	SharedPreferences sharedPreferences;
	private ActionBar actionbar;
	private Utils utils;
	TextView tvVerifyPromo;
	String deviceId,tokenKey;
	EditText etPromoCode;
	private JSONObject promoJson;
	private TextView lblCompany, lblTitle, lblAddress,  lblDateStart, lblDateEnd, lbldealpoint,
			lblofferdeatil;
	private ImageView imgThumbnail;
	RelativeLayout rlBody;
	String pid,cid;
	EditText etBillAmount;
	
	private Button btnaccept;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify_promocode);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityVerifyCode.this);
		userId = sharedPreferences.getString("userId", null);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		utils = new Utils(ActivityVerifyCode.this);
		
		rlBody=(RelativeLayout)findViewById(R.id.rlbody);
		etPromoCode=(EditText)findViewById(R.id.etpromocode);
		tvVerifyPromo=(TextView)findViewById(R.id.tvverifypromo);
		lblCompany=(TextView)findViewById(R.id.lblCompany);
		lblTitle=(TextView)findViewById(R.id.lblTitle);
		lblAddress=(TextView)findViewById(R.id.lblAddress);
		lblDateStart=(TextView)findViewById(R.id.lblStartDateValue);
		lblDateEnd=(TextView)findViewById(R.id.lblEndDateValue);
		lbldealpoint=(TextView)findViewById(R.id.lbldealpoint);
		lblofferdeatil=(TextView)findViewById(R.id.lblofferdeatil);
		etBillAmount=(EditText)findViewById(R.id.etbillamount);
		imgThumbnail=(ImageView)findViewById(R.id.imgThumbnail);
		btnaccept=(Button) findViewById(R.id.btnaccept);
		
		
		btnaccept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String billAmount=((TextView)etBillAmount).getText().toString();
				
				new acceptPromocodeAsync().execute(billAmount);
				
			}
		});
		tvVerifyPromo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		
				String promoCode=((TextView)etPromoCode).getText().toString();
				
				new verifyPromocodeAsync().execute(promoCode);
			}
		});
		
		
		
	}
	
	public class acceptPromocodeAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			pDialog = Utils.createProgressDialog(ActivityVerifyCode.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}


		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();
		

			promoJson = userFunction.acceptPromoCode(pid,cid,userId,params[0],deviceId,tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			if(pDialog.isShowing()) pDialog.dismiss();
			try {
				if (promoJson != null) {

					String status = promoJson.getString("status");

					if (status.equals("200")) {
						
						rlBody.setVisibility(View.GONE);	
						etPromoCode.setText("");
						Toast.makeText(ActivityVerifyCode.this, "Promo Code accept successfully",
								Toast.LENGTH_LONG).show();

					}else {

						String errormessage = promoJson.getString("message");

						Toast.makeText(ActivityVerifyCode.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}
			} catch (Exception e) {

			}

			

		}

	}
	
	public class verifyPromocodeAsync extends AsyncTask<String, Void, Void> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			// Show progress dialog when fetching data from database
			pDialog = Utils.createProgressDialog(ActivityVerifyCode.this);
    		pDialog.setCancelable(false);
    		pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub

			// Method to get Data from Server
			// getDataFromServer();
			//String identifier = params[0];

			promoJson = userFunction.verifyPromoCode(params[0],userId,deviceId,tokenKey);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			if(pDialog.isShowing()) pDialog.dismiss();
			try {
				if (promoJson != null) {

					String status = promoJson.getString("status");

					if (status.equals("200")) {
						
						resultjson=promoJson.getJSONObject("result");
						
						String storeName=resultjson.getString("store_name");
						String title=resultjson.getString("title");
						String address=resultjson.getString("address");
						String imageUrl=resultjson.getString("image");
						String offerDetail=resultjson.getString("discount_type");
						String points=resultjson.getString("points_for_deal");
						String startDate=resultjson.getString("start_date");
						String endDate=resultjson.getString("end_date");
						pid=resultjson.getString("pid");
						cid=resultjson.getString("cid");
						rlBody.setVisibility(View.VISIBLE);
						
						lblCompany.setText(storeName);
						lblTitle.setText(title);
						lblAddress.setText(address);
						lblDateStart.setText(startDate);
						lblDateEnd.setText(endDate);
						lbldealpoint.setText(points);
						lblofferdeatil.setText(offerDetail);

						Picasso.with(getApplicationContext())
								.load(userFunction.URLAdmin+userFunction.folderAdmin + imageUrl).fit()
								.centerCrop().tag(getApplicationContext())
								.into(imgThumbnail);

						

					}else {

						String errormessage = promoJson.getString("message");

						Toast.makeText(ActivityVerifyCode.this, errormessage,
								Toast.LENGTH_LONG).show();
					}

				}
			} catch (Exception e) {

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
