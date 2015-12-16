package com.aotd.map.helper;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.aotd.model.AddressComponentModel;
import com.aotd.model.LocationModel;
import com.aotd.model.LocationResultModel;

public class LocationParser extends LocationBaseParser{
	
	private final int FOUND_RESULT					= 0;
	private final int NO_RESULT						= 1;
	private final int ERROR_WHILE_GETTING_RESULT	= 2;
	private final int FINISH						= 4;
	
	private LocationModel mLocationModel = null;
	private LocationResultModel mLocationResultModel = null;
	private AddressComponentModel mAddressComponentModel = null;
	private Handler parentHandler;
	boolean requiredTagFound=false;
	public LocationParser(String feedUrl,Handler handler) 
	{
		super(feedUrl);
		parentHandler=handler;
		Log.e("in side the parser", "in side the parser");
	}
	
	
	public void parse() 
	{
		
	      XmlPullParser parser = Xml.newPullParser();
	        try {
	          
	            parser.setInput(this.getInputStream(), null);
	            int eventType = parser.getEventType();
	            boolean done = false;
	            while (eventType != XmlPullParser.END_DOCUMENT && !done)
	            {
	                String name = null;
	                switch (eventType)
	                {
	                    case XmlPullParser.START_DOCUMENT:
	                    	
	           
	                    	break;
	                    case XmlPullParser.START_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase(GEOCODE_RESPONSE))
	                        {
	                        	mLocationModel=new LocationModel();
	                        	mLocationModel.result = new ArrayList<LocationResultModel>();
		                    } else if (mLocationModel != null)     {
	                        	if (name.equalsIgnoreCase(STATUS)){	 
	                        		mLocationModel.setStatus(parser.nextText());
	                        	}else if (name.equalsIgnoreCase(RESULT)){
	                        		mLocationResultModel = new LocationResultModel();
	                        		mLocationResultModel.address_component = new ArrayList<AddressComponentModel>();
	                            }else if(mLocationResultModel != null){
	                            	if(name.equalsIgnoreCase(FORMATTED_ADDRESS)){
	                            		mLocationResultModel.setFormatted_address(parser.nextText());
	                            	}else if(name.equalsIgnoreCase(ADDRESS_COMPONENT)){
	                            		mAddressComponentModel = new AddressComponentModel();
	                            		mAddressComponentModel.type = new ArrayList<String>();
	                            	}else if(mAddressComponentModel != null){
	                            		if(name.equalsIgnoreCase(LONG_NAME)){
	                            			mAddressComponentModel.setLongName(parser.nextText());
	                            		}else if(name.equalsIgnoreCase(TYPE)){
	                            			mAddressComponentModel.type.add(parser.nextText());
	                            		}
	                            	}
	                            		
	                            }
	                        }
	                        break;
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase(ADDRESS_COMPONENT) && mAddressComponentModel != null){
	                        	mLocationResultModel.address_component.add(mAddressComponentModel);
	                        	mAddressComponentModel=null;
	                        }else if(name.equalsIgnoreCase(RESULT) && mLocationResultModel != null){
	                        	requiredTagFound=true;
	                        	mLocationModel.result.add(mLocationResultModel);
	                        	mLocationResultModel=null;
	                        }
	                        else if (name.equalsIgnoreCase(GEOCODE_RESPONSE)){
	                        	if(requiredTagFound){
		                        		Message messageToParent = new Message();
		                        		Bundle messageData = new Bundle();
		                        		messageToParent.what = FOUND_RESULT;
		                        		messageData.putSerializable("locationresult",mLocationModel);
		                        		messageToParent.setData(messageData);
		                        		parentHandler.sendMessage(messageToParent);	
                   		         	}else{
	                   		         	Message messageToParent = new Message();
		                				Bundle messageData = new Bundle();
		                				messageToParent.what = NO_RESULT;
		                				messageData.putString("notfound","notfound");
		                				messageToParent.setData(messageData);
		                				// send message to mainThread
		                				parentHandler.sendMessage(messageToParent);
                   		         	}
	                            done = true;
	                        }
	                        break;
	                }
	                eventType = parser.next();
	            }
	        } catch (Exception e) {
	        	Message messageToParent = new Message();
				Bundle messageData = new Bundle();
				messageToParent.what = ERROR_WHILE_GETTING_RESULT;
				messageData.putString("connectionTimeOut","Internet connection gone");
				messageToParent.setData(messageData);
				// send message to mainThread
				parentHandler.sendMessage(messageToParent);	
//	            throw new RuntimeException(e);
	        }
			
	        
	}

}

