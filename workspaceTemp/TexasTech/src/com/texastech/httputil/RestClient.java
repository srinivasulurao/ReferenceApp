package com.texastech.httputil;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.texastech.helper.MLog;

public class RestClient extends AsyncHttpResponseHandler implements HttpConst{

	  private static AsyncHttpClient client = new AsyncHttpClient();
	  
	  private HttpTaskListener listener;
	  
	  private Action action;
	  
	  public RestClient(Action action, HttpTaskListener listener){
		  this.listener= listener;
		  this.action = action;
	  }

	  public void get(String url, RequestParams params) {
		  printParam(params);
	      client.get(url, params, this);
	  }

	  public void post(String url, RequestParams params) {
		  printParam(params);
	      client.post(url, params, this);
	  }
	  
	  //------------------------------//
	  public void get(RequestParams params) {
		  printParam(params);
	      client.get(getAbsoluteUrl(action.toString()), params, this);
	  }

	  public void post(RequestParams params) {
		  printParam(params);
	      client.post(getAbsoluteUrl(action.toString()), params, this);
	  }
	  //------------------------------//
	  public void get() {
	      client.get(getAbsoluteUrl(action.toString()),  this);
	  }

	  public void post() {
	      client.post(getAbsoluteUrl(action.toString()),  this);
	  }
	  //------------------------------//
	  
	  
	  public void post(Context context,String url,String str) {
		MLog.v("JSON", ""+str);  
		try {
			StringEntity se = new StringEntity(str);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
			client.post(context, getAbsoluteUrl(url), se, "application/json",this);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	  }
	  
	  private static String getAbsoluteUrl(String relativeUrl) {
		  String url = SERVICE_URL + relativeUrl;
		  MLog.v("URL", url);
	      return url;
	  }
	  
	  public static void cancelAllRequest(){
		  client.cancelAllRequests(true);
	  }
	
	
	@Override
	public void onFailure(int statusCode, Header[] arg1, byte[] arg2, Throwable e) {
		MLog.v("Failure ", ""+e.getMessage());
		//listener.onFaliure(action, AppConstant.NETWORK_ERROR);
		listener.onFaliure(action, e.getMessage());
	}
	
	
	@Override
    public void onProgress(int bytesWritten, int totalSize) {
		
    }
	
	
	@Override
    public void onFinish() {
        // Completed the request (either success or failure)
    }
	

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
		MLog.v("RESPONSE ", ""+new String(bytes));
		listener.onSuccess(action, new String(bytes));
	}
	
	
	private void printParam(RequestParams params){
		MLog.v("PARAM", params.toString());
	}
}
