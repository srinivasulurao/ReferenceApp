package com.aotd.parsers;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;

public class PickupParser extends HttpLoader{
	String 			 Response;
	boolean			 isValidXmlBeforeEndDoc = false;
	
	private Handler parentHandler;
	
	public PickupParser(String url, Handler parentHandler){
		super(url);
		this.parentHandler = parentHandler;
	}

	public void parse(String inputStream,String errMsg) 
	{
		String successMsg = "";
		//boolean successTagFound = false;
	
		if(errMsg.length() == 0)
		{
			
			XmlPullParser parser = Xml.newPullParser();		
			try 
			{
				parser.setInput(new StringReader(inputStream));
				
				
				boolean done = false;
				boolean isValidXmlBeforeEndDoc = false;
				int eventType = parser.getEventType();
				while ( !done) 
				{
					switch (eventType) 
					{
					
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							String name = parser.getName();
							if (name.equalsIgnoreCase("Error")) 
							{
								try{
									errMsg=parser.nextText();
									
								}catch(Exception ex){}
								isValidXmlBeforeEndDoc = true;
							
							}else if (name.equalsIgnoreCase("info")){
								
								isValidXmlBeforeEndDoc = true;
								successMsg=parser.nextText();
								
							}
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
			} catch (Exception e) {
				
				errMsg="Invalid response from AOTD Server";	
			} 
		}
		
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;		
		messageData.putString("HttpError",errMsg);
		messageData.putString("success",successMsg );
		
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);
	}

	
	
}
