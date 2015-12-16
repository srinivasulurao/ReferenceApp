package com.aotd.parsers;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;
import com.aotd.model.AddressComponentModel;
import com.aotd.model.LocationModel;
import com.aotd.model.LocationResultModel;


public class CurrentLocationParser extends HttpLoader{
	
	private final String TAG = CurrentLocationParser.class.getSimpleName();
	private final int SUCCESS					= 1;
	private final int ERROR						= 0;
		
	private LocationModel mLocationModel = null;
	private LocationResultModel mLocationResultModel = null;
	private AddressComponentModel mAddressComponentModel = null;
	private Handler parentHandler;
	boolean requiredTagFound=false;
	private Message msg;
	private Bundle msgData;
	private Handler mResponseHandler;

	public CurrentLocationParser(String feedUrl,Handler handler) 
	{
		super(feedUrl);
		parentHandler=handler;
		Log.e("in side the parser", "in side the parser");
	}
	
	@Override
	protected void parse(String response, String errorMsg) {
		// TODO Auto-generated method stub
		super.parse(response, errorMsg);
	
		if(errorMsg.trim().equalsIgnoreCase("")){
			Log.e(TAG, response);
			parse(response);
		}else{
			Message messageToParent = new Message();
			Bundle messageData = new Bundle();
			messageToParent.what = ERROR;
			messageData.putString("error", errorMsg );
			messageToParent.setData(messageData);
			this.mResponseHandler.sendMessage(messageToParent);
			Log.e(TAG, errorMsg);
		}
	}
	public void parse(String response) 
	{
		
	      XmlPullParser parser = Xml.newPullParser();
	        try {
				String xml = response;
				parser.setInput(new StringReader(xml));
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
	                        if (name.equalsIgnoreCase("GeocodeResponse"))
	                        {
	                        	mLocationModel=new LocationModel();
	                        	mLocationModel.result = new ArrayList<LocationResultModel>();
		                    } else if (mLocationModel != null)     {
	                        	if (name.equalsIgnoreCase("status")){	 
	                        		mLocationModel.setStatus(parser.nextText());
	                        	}else if (name.equalsIgnoreCase("result")){
	                        		mLocationResultModel = new LocationResultModel();
	                        		mLocationResultModel.address_component = new ArrayList<AddressComponentModel>();
	                            }else if(mLocationResultModel != null){
	                            	if(name.equalsIgnoreCase("formatted_address")){
	                            		mLocationResultModel.setFormatted_address(parser.nextText());
	                            	}else if(name.equalsIgnoreCase("address_component")){
	                            		mAddressComponentModel = new AddressComponentModel();
	                            		mAddressComponentModel.type = new ArrayList<String>();
	                            	}else if(mAddressComponentModel != null){
	                            		if(name.equalsIgnoreCase("long_name")){
	                            			mAddressComponentModel.setLongName(parser.nextText());
	                            		}else if(name.equalsIgnoreCase("type")){
	                            			mAddressComponentModel.type.add(parser.nextText());
	                            		}
	                            	}
	                            		
	                            }
	                        }
	                        break;
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase("address_component") && mAddressComponentModel != null){
	                        	mLocationResultModel.address_component.add(mAddressComponentModel);
	                        	mAddressComponentModel=null;
	                        }else if(name.equalsIgnoreCase("result") && mLocationResultModel != null){
	                        	requiredTagFound=true;
	                        	mLocationModel.result.add(mLocationResultModel);
	                        	mLocationResultModel=null;
	                        }
	                        else if (name.equalsIgnoreCase("GeocodeResponse")){
	                        	if(requiredTagFound){
		                        		Message messageToParent = new Message();
		                        		Bundle messageData = new Bundle();
		                        		messageToParent.what = SUCCESS;
		                        		messageData.putSerializable("locationresult",mLocationModel);
		                        		messageToParent.setData(messageData);
		                        		parentHandler.sendMessage(messageToParent);	
                   		         	}else{
	                   		         	Message messageToParent = new Message();
		                				Bundle messageData = new Bundle();
		                				messageToParent.what = ERROR;
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
				messageToParent.what = ERROR;
				messageData.putString("connectionTimeOut","Internet connection gone");
				messageToParent.setData(messageData);
				// send message to mainThread
				parentHandler.sendMessage(messageToParent);	
	            throw new RuntimeException(e);
	        }
			
	        
	}

}

