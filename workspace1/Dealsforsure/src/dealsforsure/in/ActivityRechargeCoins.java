package dealsforsure.in;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;

public class ActivityRechargeCoins extends ActionBarActivity {
	String userid, userName, userCode, tokenKey, deviceId;
	JSONObject regjson;
	SharedPreferences sharedPreferences;
	UserFunctions userFunction;
	private ActionBar actionbar;
	private Utils utils;
	RadioGroup radGrp;
	EditText etAmount;
	Button btPayNOw;
	String rechargeAmount, rechargeType, type;
	TextView lbltype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge_icons);

		Bundle b = getIntent().getExtras();
		type = b.getString("type");

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityRechargeCoins.this);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);
		userid = sharedPreferences.getString("userId", null);
		utils = new Utils(ActivityRechargeCoins.this);
		radGrp = (RadioGroup) findViewById(R.id.rgtype);
		etAmount = (EditText) findViewById(R.id.etamount);

		lbltype = (TextView) findViewById(R.id.lbltype);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		btPayNOw = (Button) findViewById(R.id.btnpaynow);
		userFunction = new UserFunctions();
		rechargeType = "merchant_points";

		if (type.equals("merchant")) {
			radGrp.setVisibility(View.VISIBLE);
			lbltype.setVisibility(View.VISIBLE);
		} else {

			radGrp.setVisibility(View.GONE);
			lbltype.setVisibility(View.GONE);
		}

		radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case -1:

					break;
				case R.id.rbmerchant:

					rechargeType = "merchant_points";

					break;
				case R.id.rbuser:

					rechargeType = "user_points";
					break;

				}
			}
		});

		btPayNOw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				rechargeAmount = ((TextView) etAmount).getText().toString()
						.trim();

				if (utils.isNetworkAvailable()) {
					new recharegeAsyk().execute();
				} else {
					Toast.makeText(getBaseContext(), "Error in connection .",
							Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	private class recharegeAsyk extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = Utils.createProgressDialog(ActivityRechargeCoins.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.rechargeCoins(rechargeAmount, rechargeType,
					userid, deviceId, tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {

				if (pDialog.isShowing())
					pDialog.dismiss();

				if (regjson != null) {

					String url;

					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONObject jobj = regjson.getJSONObject("result");
						url = jobj.getString("url");

						Intent i = new Intent(ActivityRechargeCoins.this,
								ActivityRechargeWebView.class);
						i.putExtra("url", url);

						startActivity(i);

					}

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

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
