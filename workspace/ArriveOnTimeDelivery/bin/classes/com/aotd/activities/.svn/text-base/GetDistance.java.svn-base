package com.aotd.activities;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.location.Location;
import android.util.Log;

import com.aotd.model.DispatchAllListModel;
import com.aotd.utils.Utils;

public class GetDistance {
	
	
	private ArrayList<DispatchAllListModel> returnDataFeeds, dataFeeds;
	boolean isCurrentLocation;
	
	public GetDistance(ArrayList<DispatchAllListModel> dataFeeds,boolean isCurrentLocation){
		
		this.dataFeeds = dataFeeds;
		Log.e("","kkk con dataFeeds "+dataFeeds.size());
		this.isCurrentLocation = isCurrentLocation;

	}
	
	double distanceOne = 0, distanceTwo = 0;
	private DispatchAllListModel tempDispatchAllListModel;
	
	public ArrayList<DispatchAllListModel> sortData(){
		
		returnDataFeeds = new ArrayList<DispatchAllListModel>();
		
		double lat1, lng1, lat2, lng2;
		
		if (isCurrentLocation) {
			
			lat1 = Utils.LATITUDE;
			lng1 = Utils.LONGITUDE;
			if (lat1 != 0 && lng1 != 0) {
				
				lat1 = dataFeeds.get(0).getLatitude();
				lng1 = dataFeeds.get(0).getLongitude();
			}
			
		}else{
			
			lat1 = dataFeeds.get(0).getLatitude();
			lng1 = dataFeeds.get(0).getLongitude();
		
		}
		
		Log.i("", "kkk isCurrentLocation  "+isCurrentLocation+"  "+lat1+"   "+lng1);
		
		
//		lat1 = 29.464475;
//		lng1 = -98.446733;
		
		do{
			distanceOne = 0;
			distanceTwo = 0;
			for(int i=0; i<dataFeeds.size(); i++){
				
				Log.i("", "kkk order No:"+dataFeeds.get(i).getOrder_id());
				lat2 = dataFeeds.get(i).getDlLatitude();
				lng2 = dataFeeds.get(i).getDlLongitude();
				
				distanceOne = distFrom( lat1, lng1, lat2, lng2);
				
				//distanceOne = distBWTwoLocations( lat1, lng1, lat2, lng2);
				
				Log.e("","distanceOne "+distanceOne+" distanceTwo "+distanceTwo);
				
				if (distanceOne == 0.0) {						
					
					tempDispatchAllListModel = dataFeeds.get(i);
					break;
				}
				else if(distanceTwo == 0){
					
					distanceTwo = distanceOne;
					tempDispatchAllListModel = dataFeeds.get(i);
					
				} else{
					
					if( distanceTwo > distanceOne){
						
						distanceTwo = distanceOne;
						Log.e("","kkk distanceOne "+distanceOne);
						Log.e("","kkk dataFeeds "+dataFeeds.size());
						tempDispatchAllListModel = dataFeeds.get(i);
					}
				}
			}
			
			lat1 = tempDispatchAllListModel.getDlLatitude();
			lng1 = tempDispatchAllListModel.getDlLongitude();
			
			returnDataFeeds.add(tempDispatchAllListModel);
			dataFeeds.remove(tempDispatchAllListModel);
			
//			lat1 = tempDispatchAllListModel.getDlLatitude();
//			lng1 = tempDispatchAllListModel.getDlLongitude();
			Log.e("","kkk after loop dataFeeds "+dataFeeds.size());
			
		}while(dataFeeds.size()>0);
		
		return returnDataFeeds;
		
	}
	
	private double distBWTwoLocations(double lat1, double lng1, double lat2,double lng2) {
		
		Location locationA = new Location("point A");  

	    locationA.setLatitude(lat1);  
	    locationA.setLongitude(lng1);  

	    Location locationB = new Location("point B");  

	    locationB.setLatitude(lat2);  
	    locationB.setLongitude(lng2);  

	    double distance = locationA.distanceTo(locationB);

	    Log.v("log", "kkk distance "+distance);
	    
	    
//		return roundDecimal(distance, 2);
	    return distance;
	}
	 public double roundDecimal(double value, int decimalPlace)
     {
             BigDecimal bd = new BigDecimal(value);
             
             bd = bd.setScale(decimalPlace, 6);
             
             Log.v("log", "kkk rounded distance "+ bd.doubleValue());
             
             return bd.doubleValue();
     }
	 
	private float distFrom(double lat1, double lng1, double lat2, double lng2) {
		
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;
		
		int meterConversion = 1609;
		
		return new Float(dist * meterConversion).floatValue();
		
	}
}