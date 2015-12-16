package dealsforsure.in;

import org.json.JSONArray;
import org.json.JSONObject;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;
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
import android.widget.TextView;
import android.widget.Toast;

public class ActivityRegistration extends ActionBarActivity {

	Button btLogin, btRegister;
	EditText etemail, etpassward;
	private ActionBar actionbar;
	String type, email, passward, typeValue;
	JSONObject regjson;
	UserFunctions userFunction;
	String userid, userName, userCode, userPoint, merchantPoint;
	SharedPreferences sharedPreferences;
	private Utils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		etemail = (EditText) findViewById(R.id.etname);
		etpassward = (EditText) findViewById(R.id.etpass);
		btLogin = (Button) findViewById(R.id.btnLogin);
		btRegister = (Button) findViewById(R.id.btnregister);
		utils = new Utils(ActivityRegistration.this);
		Bundle b = getIntent().getExtras();
		type = b.getString("type");

		if (type.equals("promo")) {

			typeValue = "user";
		} else {

			typeValue = type;
		}

		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		userFunction = new UserFunctions();
		btLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				email = ((TextView) etemail).getText().toString();
				passward = ((TextView) etpassward).getText().toString();

				if (email.trim().length() <= 0) {
					Toast.makeText(ActivityRegistration.this,
							"Please enter email", Toast.LENGTH_LONG).show();

				} else if (passward.trim().length() <= 0) {
					Toast.makeText(ActivityRegistration.this,
							"Please enter password", Toast.LENGTH_LONG).show();

				} else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches()) {

					Toast.makeText(ActivityRegistration.this,
							"Please enter valid email id", Toast.LENGTH_LONG)
							.show();

				}

				else {

					if (utils.isNetworkAvailable()) {
						new loginView().execute();
					} else {
						Toast.makeText(getBaseContext(),
								"Error in connection .", Toast.LENGTH_LONG)
								.show();

					}
				}

			}
		});

		btRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(ActivityRegistration.this,
						ActivityRegistrationDetail.class);
				i.putExtra("type", typeValue);
				startActivity(i);

			}
		});

	}

	private class loginView extends AsyncTask<Void, Void, Void> {
		ProgressDialog progDailog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progDailog = new ProgressDialog(ActivityRegistration.this);
			progDailog.setMessage("Loading...");
			progDailog.setIndeterminate(false);
			progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDailog.setCancelable(true);
			progDailog.show();
		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.login(email, passward, typeValue);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				progDailog.dismiss();
				if (regjson != null) {

					// JSONArray dataRegisterArray;

					// dataRegisterArray = regjson.getJSONArray("result");

					// JSONObject dealsObject =
					// dataRegisterArray.getJSONObject(0);

					String status = regjson.getString("status");

					if (status.equals("200")) {

						userid = regjson.getString("uid");
						userName = regjson.getString("name");
						typeValue = regjson.getString("type");
						userCode = regjson.getString("user_code");
						userPoint = regjson.getString("user_points");

						sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(ActivityRegistration.this);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("userName", userName);
						editor.putString("userId", userid);
						editor.putString("type", typeValue);
						editor.putString("userCode", userCode);
						editor.putString("userPoint", userPoint);

						if (typeValue.equals("merchant")) {
							merchantPoint = regjson
									.getString("merchant_points");
							editor.putString("merchentPoint", merchantPoint);
						}

						editor.commit();

						if (type.equals("promo")) {

							finish();

						} else if (typeValue.equals("user")) {

							Intent i = new Intent(ActivityRegistration.this,
									ActivityUserProfile.class);
							startActivity(i);
						} else {

							Intent i = new Intent(ActivityRegistration.this,
									ActivityProfile.class);
							startActivity(i);

						}

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityRegistration.this, errormessage,
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
