package dealsforsure.in;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dealsforsure.in.R;
import dealsforsure.in.adapters.AdapterReview;
import dealsforsure.in.libraries.UserFunctions;
import dealsforsure.in.model.Review;
import dealsforsure.in.utils.Utils;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityReview extends ActionBarActivity {

	JSONObject regjson;
	UserFunctions userFunction;
	String userId, userName, Type, userCode, cid;
	SharedPreferences sharedPreferences;
	private ActionBar actionbar;
	ListView lvComments;
	private Utils utils;
	List<Review> myReviewList = new ArrayList<Review>();
	Review review;
	AdapterReview adapterReview;
	String deviceId, tokenKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
		actionbar = getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		userFunction = new UserFunctions();

		Bundle b = getIntent().getExtras();
		cid = b.getString("cid");

		lvComments = (ListView) findViewById(R.id.lvcomments);

		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ActivityReview.this);
		userId = sharedPreferences.getString("userId", null);
		tokenKey = sharedPreferences.getString("tokenKey", null);
		deviceId = sharedPreferences.getString("deviceId", null);

		utils = new Utils(ActivityReview.this);

		if (utils.isNetworkAvailable()) {
			new getReviewAsy().execute();
		} else {

			Toast.makeText(ActivityReview.this, "Error in connection .",
					Toast.LENGTH_LONG).show();

		}

	}

	private class getReviewAsy extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			pDialog = Utils.createProgressDialog(ActivityReview.this);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected Void doInBackground(Void... unused) {
			regjson = userFunction.getReviewList(cid,deviceId,tokenKey);

			// Store previous value of current page

			return (null);
		}

		protected void onPostExecute(Void unused) {
			try {
				myReviewList.clear();

				if (pDialog.isShowing())
					pDialog.dismiss();
				if (regjson != null) {

					// JSONArray dataRegisterArray;

					// dataRegisterArray = regjson.getJSONArray("result");

					// JSONObject dealsObject =
					// dataRegisterArray.getJSONObject(0);
					JSONObject dealObject;
					String status = regjson.getString("status");

					if (status.equals("200")) {

						JSONArray dataRegisterArray;

						dataRegisterArray = regjson.getJSONArray("result");
						for (int i = 0; i < dataRegisterArray.length(); i++) {
							dealObject = dataRegisterArray.getJSONObject(i);
							review = new Review();
							review.setComments(dealObject.getString("comments"));
							review.setDate(dealObject.getString("created_at"));
							review.setStarRate(dealObject
									.getString("review_count"));
							review.setUserName(dealObject.getString("name"));

							myReviewList.add(review);

						}

						adapterReview = new AdapterReview(ActivityReview.this,
								R.layout.adapter_mydeal, myReviewList);

						lvComments.setAdapter(adapterReview);

					} else {

						String errormessage = regjson.getString("message");

						Toast.makeText(ActivityReview.this, errormessage,
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
