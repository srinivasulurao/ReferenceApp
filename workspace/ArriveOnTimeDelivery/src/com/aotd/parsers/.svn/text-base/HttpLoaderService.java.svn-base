package com.aotd.parsers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class HttpLoaderService extends Thread {

	private final String TAG = HttpLoaderService.class.getSimpleName();
	public String url = "";
	public String parameters = "";
	public String errMsg = "";
	public StringBuffer httpResponseMessage = new StringBuffer();
	private File image = null;
	private String userID;

	public HttpLoaderService(String url, String params) {
		
		this.url = url;
		this.parameters = params;
	}

	public HttpLoaderService(String url) {
		
		this.url = url;
	}

	public HttpLoaderService(String url, String params, File image,String userID) {
		
		this.url = url;
		this.image = image;
		this.userID = userID;
		this.parameters = params;
	}

	@Override
	public void run() {
		
		StringBuffer errMsgConnection = new StringBuffer();
		
		try {
			
			if (image == null) {
				
				httpResponseMessage.delete(0, httpResponseMessage.length());
				Log.v("Network", "Requesting the url " + url);
				int BUFFER_SIZE = 2000;
				InputStream in = null;

				try {
					
					in = openHttpConnection(url, errMsgConnection);
					if (errMsgConnection.length() == 0) {
						
						InputStreamReader isr = new InputStreamReader(in);
						int charRead;

						char[] inputBuffer = new char[BUFFER_SIZE];

						while ((charRead = isr.read(inputBuffer)) > 0) {
							// ---convert the chars to a String---
							String readString = String.copyValueOf(inputBuffer,	0, charRead);
							httpResponseMessage.append(readString);
							inputBuffer = new char[BUFFER_SIZE];
						}

						in.close();
						isr.close();
					} else {
						
						errMsg = errMsgConnection.toString();
					}
				} catch (IOException e) {
					errMsg = "There seems to be a connection problem. Try back shortly!";
					e.printStackTrace();
				}
			} else {
				
//				String UploadResponse = postAddProduct(url, image, userID);
//				Log.e("uploader img", UploadResponse);
			}
		} catch (Exception e) {
			
			errMsg = "There seems to be a connection problem. Try back shortly!";
		}

		handleHttpResponse(httpResponseMessage.toString(), errMsg);
	}

	protected void handleHttpResponse(String response, String errorMsg) {

	}

	private InputStream openHttpConnection(String urlStr, StringBuffer errMsg) {
		
		InputStream in = null;
		int resCode = -1;

		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();

			if (!(urlConn instanceof HttpURLConnection)) {
				
				errMsg.append("URL is not an Http URL");
			} else {

				if (parameters.equalsIgnoreCase("")) {

					HttpURLConnection httpConn = (HttpURLConnection) urlConn;
					httpConn.setAllowUserInteraction(false);
					httpConn.setConnectTimeout(20000);
					httpConn.setInstanceFollowRedirects(true);
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					resCode = httpConn.getResponseCode();
					
					if (resCode == HttpURLConnection.HTTP_OK) {
						
						in = httpConn.getInputStream();
					} else {

						errMsg.append("Invalid request to the Server, please check the URL requested...");
					}
				} else {
					
					HttpURLConnection httpConn = (HttpURLConnection) urlConn;
					httpConn.setAllowUserInteraction(false);
					httpConn.setConnectTimeout(20000);
					httpConn.setInstanceFollowRedirects(true);
					
					httpConn.setRequestMethod("POST");
					httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded/json;charset=UTF-8");

					httpConn.setRequestProperty("Content-Length", "" + Integer.toString(this.parameters.getBytes().length));
					httpConn.setRequestProperty("Content-Language", "en-US");

					httpConn.setUseCaches(false);
					httpConn.setDoInput(true);
					httpConn.setDoOutput(true);

					DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
					wr.writeBytes(this.parameters);
					wr.flush();
					wr.close();

					resCode = httpConn.getResponseCode();
					if (resCode == HttpURLConnection.HTTP_OK) {
						
						in = httpConn.getInputStream();

					} else {
						
						Log.e("connection error", httpConn.getErrorStream()	+ "");
						errMsg.append("Invalid request to the Server...");
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			errMsg.append("There seems to be a connection problem. Try back shortly!");
		} catch (IOException e) {
			e.printStackTrace();
			errMsg.append("There seems to be a connection problem. Try back shortly!");
		} catch (Exception e) {
			e.printStackTrace();
			errMsg.append("There seems to be a connection problem. Try back shortly!");
		}
		return in;
	}

}
