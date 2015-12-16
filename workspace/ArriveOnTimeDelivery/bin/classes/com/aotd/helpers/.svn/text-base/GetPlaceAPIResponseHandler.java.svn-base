package com.aotd.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aotd.parsers.HttpLoaderService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetPlaceAPIResponseHandler extends HttpLoaderService {

	private final String TAG = GetPlaceAPIResponseHandler.class.getSimpleName();
	public static final int SUCCESS = 1;
	public static final int ERROR = 0;
	String err_Msg = null;
	private Handler mResponseHandler;
	private Message msg;
	private Bundle msgData;
	private int type;

	JSONArray jArray;
	JSONObject resultJsonObject, jObject;

	private static final String TAG_STATUS = "status";
	private static final String TAG_RESULT = "results";
	private static final String TAG_FORMATTED_ADDRESS = "formatted_address";

	String address;

	public GetPlaceAPIResponseHandler(String url, Handler parentHandler) {
		
		super(url);
		Log.e(TAG, url);
		this.mResponseHandler = parentHandler;
		msg = new Message();
		msgData = new Bundle();
		this.type = 0;
	}

	@Override
	protected void handleHttpResponse(String response, String errorMsg) {

		super.handleHttpResponse(response, errorMsg);

		if (errorMsg.trim().equalsIgnoreCase("")) {

			parser(response);
		} else {
			
			msg.what = ERROR;
			msgData.putString("response", errorMsg);
			msg.setData(msgData);
			this.mResponseHandler.sendMessage(msg);
			Log.e(TAG, errorMsg);
		}
	}

	private void parser(String response) {

		try {
			
			jObject = new JSONObject(response);
			Log.d("JSON Object", "" + jObject);

			String status = jObject.getString(TAG_STATUS);
			Log.e("statusResponse", status);
			
			if (status.equalsIgnoreCase("OK")) {

				jArray = jObject.getJSONArray(TAG_RESULT);
				Log.e("ResultObject", jArray + "");

				resultJsonObject = jArray.getJSONObject(0);
				
				address = resultJsonObject.getString(TAG_FORMATTED_ADDRESS);

				
				err_Msg = "OK";
			} else {

				err_Msg = "No data found";
			}

		} catch (JSONException e) {
			e.printStackTrace();

		}

		msg.what = SUCCESS;
		
		msgData.putString("err_Msg", err_Msg);
		msgData.putString("response", address);

		msgData.putInt("type", type);
		msg.setData(msgData);
		
		this.mResponseHandler.sendMessage(msg);

	}

}
