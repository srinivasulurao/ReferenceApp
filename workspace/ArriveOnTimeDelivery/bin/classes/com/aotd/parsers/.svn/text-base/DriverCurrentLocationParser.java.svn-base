package com.aotd.parsers;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;

public class DriverCurrentLocationParser extends HttpLoader {
	
	//private ArrayList <CarsListModel> carsListFeeds = null;
	private Handler parentHandler;
	String cityname="";
	String locality = "";
	
	public DriverCurrentLocationParser(String feedUrl,Handler handler) 
	{
		super(feedUrl);
		parentHandler=handler;
	
	}

	@Override	
	public void parse(String inputStream,String errMsg) 
	{
		
		if(errMsg.toString().length() == 0)
		{
	
			XmlPullParser parser = Xml.newPullParser();
			
	        try {
	        	boolean isValidXmlBeforeEndDoc = false;
	            parser.setInput(new StringReader(inputStream));
	            int eventType = parser.getEventType();	  
	        
	            boolean done = false;
	           
	           
	            while ( !done)
	            {
	            	
	                String name = null;
	                switch (eventType)
	                {
	                    case XmlPullParser.START_DOCUMENT:
	                    	
	                        break;
	                    case XmlPullParser.START_TAG:
	                    	name = parser.getName();
	                    	if(name.equalsIgnoreCase("Kml")){	                    		
	                    		isValidXmlBeforeEndDoc = true;
	                    	}else if(isValidXmlBeforeEndDoc && name.equalsIgnoreCase("address")){
	                    		cityname = parser.nextText();
	                    	}else if(isValidXmlBeforeEndDoc && name.equalsIgnoreCase("LocalityName")){
	                    		locality = parser.nextText();
	                    	}               		               		
	                    	
	                    	
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                                           
	                        break;
	                    case XmlPullParser.END_DOCUMENT: 
							
							if (!isValidXmlBeforeEndDoc) 
							{
								errMsg="Invalid response from WebServer";
							}
							
							done = true;
		
							break;
						
	                }
	                if(eventType != XmlPullParser.END_DOCUMENT)
						eventType = parser.next();
	            }
	        
	        }
	        catch (Exception e) {
	        	
	        	 errMsg="Invalid response from AOTD Server";
	        
	        }

		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;		
		messageData.putString("HttpError",errMsg);
		messageData.putString("cityname",cityname);	
		messageData.putString("locality",locality);	
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
