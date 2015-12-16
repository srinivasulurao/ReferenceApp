package com.aotd.parsers;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aotd.helpers.HttpLoader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetNoOfRoutesAPIResponseHandler extends HttpLoaderService {

	private final String TAG = GetNoOfRoutesAPIResponseHandler.class.getSimpleName();
	public static final int SUCCESS = 1;
	public static final int ERROR = 0;
	String err_Msg = null;

	private Handler mRouteHandler;
	private Message msg;
	private Bundle msgData;
	private int type;

	JSONArray jArray, typesJsonArray, stepArray, legsJsonArray, startingArray;
	JSONObject errorJsonObject, resultJsonObject, jObject, dataObject,legsJsonOject, startLocOject;

	private static final String TAG_STATUS = "status";
	private static final String TAG_RESULT = "routes";

	ArrayList<String> latitudeArray, longitudeArray;
	
	int noOfRoutes;

	public GetNoOfRoutesAPIResponseHandler(String url, Handler parentHandler) {
		
		super(url);
		Log.e(TAG, "kkk GetNoOfRoutesAPIResponseHandler:"+url);
		this.mRouteHandler = parentHandler;
		msg = new Message();
		msgData = new Bundle();
		this.type = 0;

	}

	protected void handleHttpResponse(String response, String errorMsg) {

		Log.e(TAG, "kkk HttpLoader:");

		super.handleHttpResponse(response, errorMsg);

		if (errorMsg.trim().equalsIgnoreCase("")) {

			parser(response);
		} else {
			
			msg.what = ERROR;
			msgData.putString("response", errorMsg);
			msg.setData(msgData);
			this.mRouteHandler.sendMessage(msg);
			Log.e(TAG, errorMsg);
		}
	}

	private void parser(String response) {

		Log.e(TAG, "kkk parser response:"+response);

		latitudeArray = new ArrayList<String>();
		longitudeArray = new ArrayList<String>();
		
		try {
			
			jObject = new JSONObject(response);
			Log.d("JSON Object", "" + jObject);

			String status = jObject.getString(TAG_STATUS);
			Log.e("statusResponse", status);
			
			if (status.equalsIgnoreCase("OK")) {

				jArray = jObject.getJSONArray(TAG_RESULT);
				noOfRoutes = jArray.length();

				Log.e("ResultObject", "kkk json Array"+jArray);

				for (int i = 0; i < jArray.length(); i++) {

					dataObject = jArray.getJSONObject(i);
					legsJsonArray = dataObject.getJSONArray("legs");

					legsJsonOject = legsJsonArray.getJSONObject(0);
					stepArray = legsJsonOject.getJSONArray("steps");

					for (int j = 0; j < stepArray.length(); j++) {

						startLocOject = stepArray.getJSONObject(j);

						if (i == 0) {

							latitudeArray.add(startLocOject.getJSONObject("start_location").getString("lat"));
							longitudeArray.add(startLocOject.getJSONObject("start_location").getString("lng"));
						} 

					}
					err_Msg = "OK";
				}
			} else {

				err_Msg = "No data found";
			}
		} catch (JSONException e) {
			e.printStackTrace();

			Log.e("JSONException", e.getMessage().toString() + "");
			err_Msg = "No data found";
		}

		for (int i = 0; i < latitudeArray.size(); i++) {

			Log.e("latArray", latitudeArray.get(i));
			Log.e("lonArray", longitudeArray.get(i));
		}
		
		msg.what = SUCCESS;
		msgData.putString("err_Msg", err_Msg);

		msgData.putStringArrayList("latArray", latitudeArray);
		msgData.putStringArrayList("lonArray", longitudeArray);

		Log.i(TAG, "kkk type "+type);
		msgData.putInt("type", type);
		msg.setData(msgData);
		this.mRouteHandler.sendMessage(msg);

	}

}
