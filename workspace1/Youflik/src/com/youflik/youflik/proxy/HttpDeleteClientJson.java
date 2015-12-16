package com.youflik.youflik.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

import com.youflik.youflik.utils.Util;

public class HttpDeleteClientJson {
	private static final String TAG = "HttpClient";
	public static int statuscode;

	public static JSONObject sendDeletePost(String URL, JSONObject jsonObjSend) {

		try {
			//DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpClient httpclient ;
			httpclient = HttpClientSingalTon.getHttpClienttest();
			HttpDeleteWithBody httpDeleteRequest = new HttpDeleteWithBody(URL);
			if(Util.API_TOKEN !=null)
			{
				httpDeleteRequest.setHeader("X-Auth-Token", Util.API_TOKEN);
			}

			StringEntity se;

			se = new StringEntity(jsonObjSend.toString(), HTTP.UTF_8);

			// Set HTTP parameters
			httpDeleteRequest.setEntity(se);
			httpDeleteRequest.setHeader("Content-Type","application/json");
			//httpDeleteRequest.setHeader("Accept-Charset", "utf-8");
			//httpDeleteRequest.addHeader("accept", "application/json");
			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpDeleteRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");


			StatusLine statusline = response.getStatusLine();
			statuscode = statusline.getStatusCode();


			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();

				// convert content stream to a String

				try {

					String resultString= convertStreamToString(instream);
					instream.close();
					// Transform the String into a JSONObject
					JSONObject jsonObjRecv = new JSONObject(resultString);

					return jsonObjRecv;

				} catch (Exception e) {
					e.printStackTrace();
				}

			} 

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}


	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("converted string is "+sb.toString());
		return sb.toString();
	}

}
