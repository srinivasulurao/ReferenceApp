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
import com.aotd.model.DispatchListModel;

public class UploadImageDataParser extends HttpLoader {
	
	
	private Handler parentHandler;
	ArrayList<DispatchListModel> marrDispatchList;
	String successMsg;
	String parseFor = "AOTD";
	public UploadImageDataParser(Handler handler,String feedUrl, String parseFor){
		super(feedUrl);
		parentHandler=handler;
		this.parseFor = parseFor;
		
		
		
	}
	
	
	@Override	
	public void parse(String inputStream,String errMsg) 
	{
		
		if(errMsg.toString().length() == 0)
		{
			if(parseFor.equalsIgnoreCase("rna"))
			{
				XmlPullParser parser = Xml.newPullParser();
				
		        try {
			        	boolean isValidXmlBeforeEndDoc = false;
			            parser.setInput(new StringReader(inputStream));
			            int eventType = parser.getEventType();	           
			            DispatchListModel mDispatchListModel = null;
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
			                    	if(name.equalsIgnoreCase("string")){
			                    		successMsg = parser.nextText();		                    		
			                    		isValidXmlBeforeEndDoc = true;
			                    		if(successMsg.equalsIgnoreCase("true"))
			                    		{
			                    			successMsg = "Successfully checked in to RNA";
			                    		}else{
			                    			errMsg = "Error in processing checkin into RNA, contact support";
			                    		}
			                    	}
		                        break;
		                        
		                    case XmlPullParser.END_TAG:
	                 
		                        break;
		                    case XmlPullParser.END_DOCUMENT: 
								
								if (isValidXmlBeforeEndDoc == false){
									errMsg="Invalid response from RNA Server";
	
								}
								done = true;
			
								break;
							
		                }
		                if(eventType != XmlPullParser.END_DOCUMENT)
							eventType = parser.next();
		            }
		        
		        }
		        catch (Exception e) {
		        	
		        	 errMsg="Invalid response from RNA Server";
		        
		        }
			}else{ // for AOTD
				XmlPullParser parser = Xml.newPullParser();
				
		        try {
			        	boolean isValidXmlBeforeEndDoc = false;
			            parser.setInput(new StringReader(inputStream));
			            int eventType = parser.getEventType();	           
			            DispatchListModel mDispatchListModel = null;
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
			                    	if(name.equalsIgnoreCase("info")){
			                    		successMsg = parser.nextText();		                    		
			                    		isValidXmlBeforeEndDoc = true;
			                    	}else{	
				                        if (name.equalsIgnoreCase("error")){
				                        	errMsg = parser.nextText();
				                        	isValidXmlBeforeEndDoc = true;
				                        	
				                        }
			                    	}
		                        break;
		                        
		                    case XmlPullParser.END_TAG:
	                 
		                        break;
		                    case XmlPullParser.END_DOCUMENT: 
								
								if (isValidXmlBeforeEndDoc == false){
									errMsg="Invalid response from AOTD Server";

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
		}
		
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		if(parseFor.equalsIgnoreCase("rna"))
		{
			messageToParent.what = 2;
		}else{
			messageToParent.what = 0;
		}
		messageData.putString("HttpError",errMsg);
		messageData.putString("success",successMsg);		
		
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
