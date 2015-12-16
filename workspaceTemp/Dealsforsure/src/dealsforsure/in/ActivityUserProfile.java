package dealsforsure.in;

import org.json.JSONObject;

import dealsforsure.in.R;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.utils.Utils;
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
import android.widget.TextView;

public class ActivityUserProfile extends ActionBarActivity{
	
	private ActionBar actionbar;
	SharedPreferences sharedPreferences;
	TextView tvUserName,tvPoints,tvemail,tvrecharecoins;
	String userId,userName,userPoints,email;
	JSONObject pointJson;
	private UserFunctions userFunction;
	private Utils utils;
	String deviceId,tokenKey;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		//tvLogout= (TextView) findViewById(R.id.tvlogout);
		tvUserName= (TextView) findViewById(R.id.tvUserName);
		tvPoints= (TextView) findViewById(R.id.tvpoints);
		tvemail=(TextView) findViewById(R.id.tvemail);
		tvrecharecoins= (TextView) findViewById(R.id.tvrecharecoins);
		//email= sharedPreferences.getString("email", "");
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityUserProfile.this);
		
		userId = sharedPreferences.getString("userId", "");
		userName= sharedPreferences.getString("userName", "");
		userPoints= sharedPreferences.getString("userPoint", "");
		email= sharedPreferences.getString("userEmail", "");
		 tokenKey = sharedPreferences.getString("tokenKey", null);
	        deviceId = sharedPreferences.getString("deviceId", null);
		tvUserName.setText("Name:"+userName);
		tvPoints.setText("Coins Remaining: "+userPoints);
		tvemail.setText("E-mail: "+email);
		utils 		= new Utils(this);
		userFunction= new UserFunctions();
		
	tvrecharecoins.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent i = new Intent(ActivityUserProfile.this,
						ActivityRechargeCoins.class);
				i.putExtra("type", "user");

				startActivity(i);
				
			}
		});
	
	if(utils.isNetworkAvailable()){
	new getPointAsyc().execute();
	}
	
		
		//userId = sharedPreferences.getString("userId", null);
		/*tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//displayAddStore();
				
				SharedPreferences.Editor editor = sharedPreferences
						.edit();
				editor.clear();
				editor.commit();
				
				
				Intent i = new Intent(ActivityUserProfile.this,
						ActivityHome.class);
				

				startActivity(i);

			}
		});*/
		
	}
	
	
private class getPointAsyc extends AsyncTask<Void, Void, Void> {
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		protected Void doInBackground(Void... unused) {
			pointJson = userFunction.getPoints(userId,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				
				if (pointJson != null) {

					JSONObject pointsObject;
					String status = pointJson.getString("status");

					if (status.equals("200")) {
						
						pointsObject= pointJson.getJSONObject("result");
						sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(ActivityUserProfile.this);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						
						String userPoints=pointsObject.getString("user_points");
						
						editor.putString("userPoint", userPoints);
						editor.commit();
						tvPoints.setText("Coins Remaining: "+userPoints);
						
						
					}
				}
					}catch(Exception e){
						
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
