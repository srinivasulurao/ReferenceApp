package com.aotd.parsers;

import java.io.StringReader;


import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;
import com.aotd.model.DispatchAllListModel;

public class AuthenticationParser extends HttpLoader {
	
	//private ArrayList <CarsListModel> carsListFeeds = null;
	private Handler parentHandler;
	String authentication="";
	
	
	public AuthenticationParser(Handler handler,String feedUrl) 
	{
		super(feedUrl);
		parentHandler=handler;
	
	}
//	<DriverAuthenticationData><DriverAuthentication>
//	<AuthenticationStatus>true</AuthenticationStatus>
//	<DriverInitials>SYS</DriverInitials>
//	</DriverAuthentication></DriverAuthenticationData>
//	
	@Override	
	public void parse(String inputStream,String errMsg) 
	{
		boolean noOrdersAvailable=false;
		if(errMsg.toString().length() == 0)
		{
	
			XmlPullParser parser = Xml.newPullParser();
			
	        try {
	        	boolean isValidXmlBeforeEndDoc = false;
	            parser.setInput(new StringReader(inputStream));
	            int eventType = parser.getEventType();	           
	            DispatchAllListModel mDispatchPresentListModel = null;
	            boolean done = false;
	            boolean orderInfoFound = false;
	           
	            while ( !done)
	            {
	            	
	                String name = null;
	                switch (eventType)
	                {
	                    case XmlPullParser.START_DOCUMENT:
	                    	
	                        break;
	                    case XmlPullParser.START_TAG:
	                    	name = parser.getName();
	                    	if(name.equalsIgnoreCase("DriverAuthenticationData")){	                    		
	                    		isValidXmlBeforeEndDoc = true;
	                    	}else if(isValidXmlBeforeEndDoc && name.equalsIgnoreCase("AuthenticationStatus")){
	                    		authentication = parser.nextText();
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
		messageData.putString("Authentication",authentication);	
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
