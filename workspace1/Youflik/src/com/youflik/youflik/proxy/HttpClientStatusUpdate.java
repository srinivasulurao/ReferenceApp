package com.youflik.youflik.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
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

public class HttpClientStatusUpdate {
	public static int statuscode;
	public static String fileFlag;
	public static String status_text;
	public static String status_loc;
	public static String status_privacy;
	public static String status_custom_user;

	public static JSONObject sendHttpPostImage(String URL,File file,String vp,String text,String location,String privacy,String custom_user,String thirdUID) {

		try {
			HttpClient httpclient;
			httpclient = HttpClientSingalTon.getHttpClienttest();
			HttpPost httpPostRequest = new HttpPost(URL);

			if(Util.API_TOKEN !=null)
			{
				httpPostRequest.setHeader("X-Auth-Token", Util.API_TOKEN);
			}
			//SETTING OF DATA
			fileFlag=vp;
			status_text=text;
			status_loc=location;
			status_privacy=privacy;
			status_custom_user=custom_user;
			// END SETTING OF DATA
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			MultipartEntity mpEntity = new MultipartEntity();
			if(fileFlag=="0")
			{
			ContentBody cbFile = new FileBody(file, "image/jpeg");
			mpEntity.addPart("photo", cbFile); 
			mpEntity.addPart("tracker_type", new StringBody("post_photo"));
			
			}
			else
			{
				ContentBody cbFile = new FileBody(file, "image/jpeg");
				mpEntity.addPart("video", cbFile); 
				mpEntity.addPart("tracker_type", new StringBody("post_video"));
			}
			if(status_text=="null")
			{
				
			}
			else
			{
				mpEntity.addPart("status_update_text", new StringBody(status_text));
			}
			if(status_loc=="null")
			{
				
			}
			else
			{
				mpEntity.addPart("location_name", new StringBody(status_loc));
			}
			if(status_privacy=="null")
			{
				
			}
			else
			{
				mpEntity.addPart(status_privacy, new StringBody("1"));
			}
			if(status_privacy.equalsIgnoreCase("custom_privacy") && status_custom_user!="null")
			{
				mpEntity.addPart("csv_custom_list_users", new StringBody(status_custom_user));
			}
			else
			{
				
			}
			if(thirdUID=="null")
			{
				
			}
			else
			{
				mpEntity.addPart("to_user", new StringBody(Util.THIRD_PARTY_USER_ID));
			}
			
			httpPostRequest.setEntity(mpEntity);
			System.out.println("executing request " + httpPostRequest.getRequestLine());
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);

			StatusLine statusline = response.getStatusLine();
			statuscode = statusline.getStatusCode();
			System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRRRR" +statuscode + "RRRRRRRRRRR");;
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				System.out.println("The response is "+instream);

				try {

					String resultString= convertStreamToString(instream);
					instream.close();
					System.out.println("The response is "+resultString);
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
		return sb.toString();
	}



}
