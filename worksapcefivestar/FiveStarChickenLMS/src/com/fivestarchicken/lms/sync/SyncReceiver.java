package com.fivestarchicken.lms.sync;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.fivestarchicken.lms.ActivityExamModule;
import com.fivestarchicken.lms.database.DbAdapter;
import com.fivestarchicken.lms.utils.Commons;
import com.fivestarchicken.lms.webservice.Webservice;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SyncReceiver extends BroadcastReceiver {
	static int noOfTimes = 0;
	private DbAdapter dh;
	String lastSyncTime, userId, url;
	Boolean isSyncServer = false, isPendingSync;

	SharedPreferences sharedPreferences;

	// Method gets called when Broad Case is issued from MainActivity for every
	// 10 seconds
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		noOfTimes++;
		this.dh = new DbAdapter(context);
		
		lastSyncTime = dh.getLastSynctime(Commons.resultModule);
		isPendingSync = dh.isPendingSyncResult();
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		userId = sharedPreferences.getString("userId", null);
		String methodName = "exam_control/checkresult?";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();

		try {
			url = Webservice.url + methodName + "user_id=" + userId
					+ "&check_sync_time="
					+ URLEncoder.encode(lastSyncTime, "UTF-8");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				System.out.println(response);

				try {

					// Create JSON object out of the response sent by
					// getdbrowcount.php
					JSONObject obj = new JSONObject(response);
					String status = obj.getString("status");

					if (status.equals("200")) {

						/*
						 * Toast.makeText(context, status,
						 * Toast.LENGTH_SHORT).show();
						 */

						isSyncServer = true;

						checkPendingSync(context);

					} else {

						/*
						 * Toast.makeText(context, response,
						 * Toast.LENGTH_SHORT).show();
						 */

					}

					/*
					 * final Intent intnt = new Intent(context,
					 * MyService.class); // Set unsynced count in intent data
					 * intnt.putExtra("intntdata",
					 * "Unsynced Rows Count "+obj.getInt("count")); // Call
					 * MyService context.startService(intnt); }else{
					 * Toast.makeText(context, "Sync not needed",
					 * Toast.LENGTH_SHORT).show(); }
					 */
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable error,
					String content) {

				checkPendingSync(context);

				// TODO Auto-generated method stub
				/*
				 * Toast.makeText(context, "Error occured!",
				 * Toast.LENGTH_SHORT).show();
				 */
			}

		});

		

	}

	void checkPendingSync(Context context) {

		if (isPendingSync || isSyncServer) {

			Intent intnt = new Intent(context, SyncServices.class);

			if (isPendingSync) {
				intnt.putExtra("isPendingSync", "1");
			} else {

				intnt.putExtra("isPendingSync", "0");
			}
			if (isSyncServer) {
				intnt.putExtra("isSyncServer", "1");
			} else {
				intnt.putExtra("isSyncServer", "0");
			}

		

			context.startService(intnt);

		}

	}

}
