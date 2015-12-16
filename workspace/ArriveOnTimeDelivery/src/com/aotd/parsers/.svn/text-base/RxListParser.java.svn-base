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
import com.aotd.model.RxItemModel;

public class RxListParser extends HttpLoader {
	
	
	private Handler parentHandler;
	String errMsg = "";
	String succMsg = "BatchId Successfully Checked Out";
	
	
	public RxListParser(String feedUrl,Handler handler) 
	{
		super(feedUrl);
		parentHandler=handler;	
		
	}
	
	
	
	public void parse(String data,String errMsg) 
	{
		
		if(errMsg.toString().length() == 0)
		{					
			XmlPullParser parser = Xml.newPullParser();
			
	        try {
		        	boolean isValidXmlBeforeEndDoc = false;
		        	boolean isValidXml = false;
		            parser.setInput(new StringReader(data));
		            int eventType = parser.getEventType();	           
		            RxItemModel mRxItemObject = null;
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
	                    		
		                        if (name.equalsIgnoreCase("RxList")){	                        	
		                        	isValidXmlBeforeEndDoc = true;
		                        	
		                        }else if(isValidXmlBeforeEndDoc && name.equalsIgnoreCase("RxItem")){
		                        	isValidXml = true;
		                        
		                        }
	                    	
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                                        
	                        break;
	                    case XmlPullParser.END_DOCUMENT: 
							
							if (!isValidXmlBeforeEndDoc || !isValidXml) 
							{
								errMsg="BatchId is not Valid";

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

		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 2;		
		messageData.putString("HttpError",errMsg);
		messageData.putString("success",succMsg);
		messageData.putString("xml_data",data);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
