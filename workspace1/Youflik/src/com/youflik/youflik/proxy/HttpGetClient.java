package com.youflik.youflik.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import com.youflik.youflik.utils.Util;

import android.util.Log;

public class HttpGetClient {
	public static int statuscode;

	public static JSONObject sendHttpPost(String URL){

		try {
			HttpClient httpclient;
			httpclient = HttpClientSingalTon.getHttpClienttest();
			HttpGet httpGetRequest = new HttpGet(URL);
			if(Util.API_TOKEN !=null)
			{
				System.out.println(Util.API_TOKEN);
				httpGetRequest.setHeader("X-Auth-Token", Util.API_TOKEN);
				httpGetRequest.setHeader("user_id", Util.USER_ID);
			}

			HttpResponse response;

			//	System.out.println("executing request " + httpGetRequest.getRequestLine());
			//	System.out.println("executing request " + httpGetRequest.getMethod());
			//	System.out.println("executing request " + httpGetRequest.getFirstHeader("X-Auth-Token"));
			//	System.out.println("executing request Tsting  " + httpGetRequest.getHeaders("X-Auth-Token"));

			response = httpclient.execute(httpGetRequest);

			StatusLine statusline = response.getStatusLine();
			statuscode = statusline.getStatusCode();
			// Examine the response status
			Log.i("Praeda",response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
	
				String result= convertStreamToString(instream);

				// now you have the string representation of the HTML request
				instream.close();
				JSONObject jsonObjRecv = new JSONObject(result);

				return jsonObjRecv;

			}


		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
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
		System.out.println(sb.toString());
		return sb.toString();
	}	    


}
