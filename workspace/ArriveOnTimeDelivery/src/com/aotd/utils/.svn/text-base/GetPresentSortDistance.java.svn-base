package com.aotd.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.location.Location;
import android.util.Log;

import com.aotd.model.DispatchAllListModel;

public class GetPresentSortDistance {
	
	
	private ArrayList<DispatchAllListModel> returnDataFeeds, dataFeeds;
	
	public GetPresentSortDistance(ArrayList<DispatchAllListModel> dataFeeds){
		
		this.dataFeeds = dataFeeds;
	}
	
	double distanceOneFactor = 0, distanceTwoFactor = 0;
	double rorOneFactor = 0;
	double rorTwoFactor = 0;
	double timeFactor = 0;
	double orderTime;
	double priorityTime;
	double lat1, lng1, lat2, lng2, lat3, lng3;
	
	private DispatchAllListModel tempDispatchAllListModel;
	
	private double priviousdistanceFactor;
	private ArrayList<DispatchAllListModel> finalDataFeeds = new ArrayList<DispatchAllListModel>();

	public ArrayList<DispatchAllListModel> sortData(){
		
		returnDataFeeds = new ArrayList<DispatchAllListModel>();
		
		
		priviousdistanceFactor = 0;

		
		
//		lat1 = Utils.LATITUDE;
//		lng1 = Utils.LONGITUDE;
		
		lat1 = 29.359511;
		lng1 = -98.480086;
		
		if(lat1==0.0 && lng1 == 0.0){
			
			lat1 = 29.359511;
			lng1 = -98.480086;
			
		}
		
//		lat1 = 29.464475;
//		lng1 = -98.446733;
//		
	
		
		do{
			distanceOneFactor = 0;
			distanceTwoFactor = 0;
			
			rorOneFactor = 0;
			rorTwoFactor = 0;
			
			for(int i=0; i<dataFeeds.size(); i++){
				
				
				lat2 = dataFeeds.get(i).getLatitude();
				lng2 = dataFeeds.get(i).getLongitude();
				
				lat3 = dataFeeds.get(i).getDlLatitude();
				lng3 = dataFeeds.get(i).getDlLongitude();
				
//				distanceOneFactor = distFrom( lat1, lng1, lat2, lng2);
				
				//miles
				distanceOneFactor = (distBWTwoLocations( lat1, lng1, lat2, lng2)+distBWTwoLocations( lat2, lng2, lat3, lng3))*0.000621371192;
				
				Log.e("","distanceOneFactor "+distanceOneFactor+" distanceTwoFactor "+distanceTwoFactor);
				
				String priorityHoursAndMinArray[] = dataFeeds.get(i).getHour().split("\\.");
                double priorityHoursInMins =  Double.parseDouble(priorityHoursAndMinArray[0].toString())*60;
				double priorityMins = Double.parseDouble(priorityHoursAndMinArray[1].toString());
				priorityTime = priorityHoursInMins+priorityMins;
				
				
				String timeOderString = dataFeeds.get(i).getRDDate().split(",")[0].toString();
				String hoursAndMinArray[] = timeOderString.split(":");
				double orderHoursIntoMins = Double.parseDouble(hoursAndMinArray[0].toString())*60;
				double orderMins = Double.parseDouble(hoursAndMinArray[1].split("\\ ")[0]);

				
				if(timeOderString.contains("PM")){
					orderTime = orderHoursIntoMins+orderMins+720;
				}else{
					orderTime = orderHoursIntoMins+orderMins;
				}
				double dlTime = orderTime+priorityTime;
				
				//first time calculate current time
				//2nd time onwards current time is added with previous delivery time
				double currenttimeInMins = ((System.currentTimeMillis()/60000)%60)+(priviousdistanceFactor*3/2);

				timeFactor = dlTime - currenttimeInMins;
				
				rorOneFactor = distanceOneFactor/timeFactor;
				
				if(rorTwoFactor == 0){
					
					rorTwoFactor = rorOneFactor;
					tempDispatchAllListModel = dataFeeds.get(i);
				}else{
					
					if( rorTwoFactor > rorOneFactor){
						
						distanceTwoFactor = distanceOneFactor;

						rorTwoFactor = rorOneFactor;
						tempDispatchAllListModel = dataFeeds.get(i);
						
					}
				}
				
				
			}
			priviousdistanceFactor = distanceTwoFactor+priviousdistanceFactor;

			returnDataFeeds.add(tempDispatchAllListModel);	
			dataFeeds.remove(tempDispatchAllListModel);
			
			lat1 = tempDispatchAllListModel.getDlLatitude();
			lng1 = tempDispatchAllListModel.getDlLongitude();
			Log.e("","dataFeeds "+dataFeeds.size());
			
		}while(dataFeeds.size()>0);
		
		//first feed based on RORFactor
		finalDataFeeds.add(returnDataFeeds.get(0));
		
		return returnDataFeeds;
		
	}
	
//	public ArrayList<DispatchAllListModel> secondSortData(ArrayList<DispatchAllListModel> returnDataFeeds, double priviousdistanceFactor1){
//		
//		priviousdistanceFactor = priviousdistanceFactor1;
//		
//		lat1 = returnDataFeeds.get(0).getDlLatitude();
//		lng1 = returnDataFeeds.get(0).getDlLongitude();
//		
//		
//		do{
//			
//			distanceOneFactor = 0;
//			distanceTwoFactor = 0;
//			rorOneFactor = 0;
//			rorTwoFactor = 0;
//			
//			
//			
//			
//			for(int i=1; i<returnDataFeeds.size(); i++){
//				
//				
//				lat2 = dataFeeds.get(i).getLatitude();
//				lng2 = dataFeeds.get(i).getLongitude();
//				
//				lat3 = dataFeeds.get(i).getDlLatitude();
//				lng3 = dataFeeds.get(i).getDlLongitude();
//				
//				//miles
//				distanceOneFactor = (distBWTwoLocations( lat1, lng1, lat2, lng2)+distBWTwoLocations( lat2, lng2, lat3, lng3))*0.000621371192;
//
//				String priorityHoursAndMinArray[] = dataFeeds.get(i).getHour().split(".");
//                double priorityHoursInMins =  Double.parseDouble(priorityHoursAndMinArray[0].toString())*60;
//				double priorityMins = Double.parseDouble(priorityHoursAndMinArray[1].toString());
//				priorityTime = priorityHoursInMins+priorityMins;
//				
//				
//				String timeOderString = dataFeeds.get(i).getRDDate().split(",")[i].toString();
//				String hoursAndMinArray[] = timeOderString.split(":");
//				double orderHoursIntoMins = Double.parseDouble(hoursAndMinArray[0].toString())*60;
//				double orderMins = Double.parseDouble(hoursAndMinArray[1].toString());
//				
//				double currenttimeInMins = ((System.currentTimeMillis()/60000)%60)+(priviousdistanceFactor*3/2);
//				
//				if(timeOderString.contains("PM")){
//					orderTime = orderHoursIntoMins+orderMins+720;
//				}else{
//					orderTime = orderHoursIntoMins+orderMins;
//				}
//				double dlTime = orderTime+priorityTime;
//				timeFactor = dlTime - currenttimeInMins;
//				
//				rorOneFactor = distanceOneFactor/timeFactor;
//				
//				if(rorTwoFactor == 0){
//					
//					rorTwoFactor = rorOneFactor;
//					tempDispatchAllListModel = dataFeeds.get(i);
//				}else{
//					
//					if( rorTwoFactor > rorOneFactor){
//						
//						distanceTwoFactor = distanceOneFactor;
//
//						rorTwoFactor = rorOneFactor;
//						tempDispatchAllListModel = dataFeeds.get(i);
//						
//					}
//				}
//				
//				
//			}
//			priviousdistanceFactor = distanceTwoFactor+priviousdistanceFactor;
//			finalDataFeeds.add(tempDispatchAllListModel);
//			lat1 = tempDispatchAllListModel.getDlLatitude();
//			lng1 = tempDispatchAllListModel.getDlLongitude();
//			
//			returnDataFeeds.remove(tempDispatchAllListModel);
//			
//			
//			Log.e("","dataFeeds "+dataFeeds.size());
//			
//
//			
//			
//		}while(returnDataFeeds.size()>1);
//		
//		
//		
//		return finalDataFeeds;
//		
//		
//	}
	
	private double distBWTwoLocations(double lat1, double lng1, double lat2,
			double lng2) {
		
		Location locationA = new Location("point A");  

	    locationA.setLatitude(lat1);  
	    locationA.setLongitude(lng1);  

	    Location locationB = new Location("point B");  

	    locationB.setLatitude(lat2);  
	    locationB.setLongitude(lng2);  

	    double distance = locationA.distanceTo(locationB);

	    Log.v("log", "kkk distance "+distance);
	    
	    
		return roundDecimal(distance, 2);
	}
	 public double roundDecimal(double value, int decimalPlace)
     {
             BigDecimal bd = new BigDecimal(value);
             
             bd = bd.setScale(decimalPlace, 6);
             
             Log.v("log", "kkk rounded distance "+ bd.doubleValue());
             
             return bd.doubleValue();
     }
	 
	
}
