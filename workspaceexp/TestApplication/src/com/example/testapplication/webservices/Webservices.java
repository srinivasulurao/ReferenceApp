package com.example.testapplication.webservices;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class Webservices {

	String url = "http://www.theienable.com/interntest/interntest.php?";

	public String getImageUrl() {

		String result = null;

		try {

			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams
					.setConnectionTimeout(client.getParams(), 10000); // Timeout
																		// Limit
			HttpResponse response;
			JSONObject json = new JSONObject();

			HttpPost post = new HttpPost(url);
			json.put("password", "test12345");

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();

			result = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;

	}

}
