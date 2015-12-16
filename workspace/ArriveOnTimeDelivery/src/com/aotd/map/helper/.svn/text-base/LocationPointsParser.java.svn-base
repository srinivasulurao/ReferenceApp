package com.aotd.map.helper;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aotd.helpers.HttpLoader;
import com.google.android.maps.GeoPoint;

public class LocationPointsParser extends HttpLoader {

	Handler parentHandler;
	boolean step_found = false;
	boolean distance_tag = false;

	RoadModel modelobj = null;
	boolean start_location = false;
	boolean end_location = false;
	ArrayList<GeoPoint> pointToDraw;

	public LocationPointsParser(Handler handler, String feedUrl) {
		super(feedUrl);
		Log.e("direction url", feedUrl);
		parentHandler = handler;

	}

	public void parse(String inputStream, String errMsg) {

		if (errMsg.toString().length() == 0) {

//			XmlPullParser parser = Xml.newPullParser();
			try {
				// parser.setInput(new StringReader(inputStream));
				// //Log.e("net","connected");
				// int eventType=parser.getEventType();
				//
				//
				// boolean done = false;
				// while(eventType!=XmlPullParser.END_DOCUMENT && !done)
				// {
				// String name=null;
				// switch (eventType) {
				// case XmlPullParser.START_DOCUMENT:
				//
				// break;
				// case XmlPullParser.START_TAG:
				//
				// name=parser.getName();
				// if(name.equalsIgnoreCase("status"))
				// {
				// if(parser.nextText().equalsIgnoreCase("OK")){
				// modelobj=new RoadModel();
				// }
				// }else if(modelobj!=null){
				//
				// if (name.equalsIgnoreCase("step")){
				// step_found = true;
				// }else if (name.equalsIgnoreCase("start_location")){
				// start_location = true;
				// }else if (name.equalsIgnoreCase("end_location")){
				// end_location = true;
				// }else if(name.equalsIgnoreCase("lat") && step_found &&
				// start_location){
				// Double lat=Double.parseDouble(parser.nextText());
				// modelobj.lat_arr.add(lat);
				//
				// }else if(name.equalsIgnoreCase("lng") && step_found &&
				// start_location){
				// Double lng=Double.parseDouble(parser.nextText());
				// modelobj.long_arr.add(lng);
				//
				// }else if(name.equalsIgnoreCase("lat") && step_found &&
				// end_location){
				// Double lat=Double.parseDouble(parser.nextText());
				// modelobj.lat_arr.add(lat);
				//
				// }else if(name.equalsIgnoreCase("lng") && step_found &&
				// end_location){
				// Double lng=Double.parseDouble(parser.nextText());
				// modelobj.long_arr.add(lng);
				//
				// }else if(name.equalsIgnoreCase("distance") && !step_found){
				// distance_tag = true;
				// }else if(name.equalsIgnoreCase("text")&& distance_tag){
				// modelobj.mDescription = parser.nextText();
				// }
				// }
				// break;
				//
				// case XmlPullParser.END_TAG:
				// name=parser.getName();
				// if(name.equalsIgnoreCase("step"))
				// {
				// step_found = false;
				// }else if(name.equalsIgnoreCase("start_location")){
				// start_location = false;
				// }else if(name.equalsIgnoreCase("end_location")){
				// end_location = false;
				// }
				//
				//
				// break;
				// }
				// eventType=parser.next();
				// }

				String result = inputStream;
				//Log.e("direction response", result);
				modelobj=new RoadModel();
				JSONObject jsonObject = new JSONObject(result);
				JSONArray routeArray = jsonObject.getJSONArray("routes");
				JSONObject routes = routeArray.getJSONObject(0);
				JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
				JSONArray legs = routes.getJSONArray("legs");
				JSONObject first_leg = legs.getJSONObject(0);
				JSONObject leg_length = first_leg.getJSONObject("distance");
				
				modelobj.mDescription = leg_length.getString("text");
				
				String encodedString = overviewPolylines.getString("points");
				pointToDraw = decodePoly(encodedString);
				if(pointToDraw.size() > 0){					
					
					
					for(GeoPoint point: pointToDraw){
						modelobj.lat_arr.add(point.getLatitudeE6()/1E6);
						modelobj.long_arr.add(point.getLongitudeE6()/1E6);
					}
				}

			} catch (Exception e) {
				errMsg = "Invalid response from Daleelo Server";
			}
		}

		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 10;

		if (modelobj != null) {
			messageData.putSerializable("points", modelobj);
		} else {
			errMsg = "Invalid response from Daleelo Server";
		}
		messageData.putString("HttpError", errMsg);

		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

	private ArrayList<GeoPoint> decodePoly(String encoded) {

		ArrayList<GeoPoint> poly = new ArrayList<GeoPoint>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			poly.add(p);
		}

		return poly;
	}
}
