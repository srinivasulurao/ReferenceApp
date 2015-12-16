package foodzu.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import foodzu.com.Utils.URLs;
import foodzu.com.Utils.Utils;

public class AboutUsActivity extends Activity {

	TextView foodzu_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		foodzu_data = (TextView) findViewById(R.id.aboutdata);
		new getaboutfoodzu().execute();
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

	public String getdata() {
		InputStream inputStream = null;
		String result = null;
		String URL = URLs.ABOUT_US_URL;

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}

	public class getaboutfoodzu extends AsyncTask<Void, Void, String> {

		Dialog dialog;
		String data;

		protected void onPreExecute() {

			dialog = Utils.createProgressDialog(AboutUsActivity.this);
			dialog.setCancelable(false);
			dialog.show();

		}

		protected String doInBackground(Void... params) {

			data = getdata();

			return "";
		}

		protected void onPostExecute(String result) {
			JSONObject info;
			try {
				info = new JSONObject(data);
				foodzu_data.setText(info.getString("details"));
				dialog.dismiss();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
