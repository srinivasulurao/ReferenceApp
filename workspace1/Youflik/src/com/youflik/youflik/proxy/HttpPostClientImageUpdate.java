package com.youflik.youflik.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONObject;

import com.youflik.youflik.utils.Util;


public class HttpPostClientImageUpdate {
	//private static final String TAG = "HttpClient";
	public static int statuscode;

	public static JSONObject sendHttpPostImage(String URL, File file,Integer flag) {

		try {
			HttpClient httpclient = HttpClientSingalTon.getHttpClienttest();
			HttpPost httpPostRequest = new HttpPost(URL);
			if(Util.API_TOKEN !=null)
			{
				httpPostRequest.setHeader("X-Auth-Token", Util.API_TOKEN);
			}

			// Try This
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			ContentBody cbFile = new FileBody(file, "image/jpeg");
			//	mpEntity.addPart("file", cbFile); 
			// add condition
			if(flag==1)
			{
				//mpEntity.addPart("profile_photo", new StringBody("1"));
				mpEntity.addPart("profile_photo", cbFile); 
			}
			else if (flag==2) {
				//mpEntity.addPart("cover_photo", new StringBody("1"));
				mpEntity.addPart("cover_photo", cbFile); 

			}

			else
			{
				System.out.println("invalid flag");
			}

			httpPostRequest.setEntity(mpEntity);



			System.out.println("executing request " + httpPostRequest.getRequestLine());
			//long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			//Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");


			StatusLine statusline = response.getStatusLine();
			statuscode = statusline.getStatusCode();
			System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" +statuscode + "RRRRRRRRRRR");;
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				System.out.println("The response is "+instream);

				// convert content stream to a String

				try {

					String resultString= convertStreamToString(instream);
					instream.close();
					System.out.println("The response is "+resultString);
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
		//System.out.println("converted string is "+sb.toString());
		return sb.toString();
	}



}
