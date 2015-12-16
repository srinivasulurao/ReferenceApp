package com.aotd.parsers;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import com.aotd.helpers.HttpLoader;
import com.aotd.utils.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;



public class LoginParser extends HttpLoader 
{
	private Handler parentHandler;
	
	public LoginParser(String url, Handler parentHandler)
	{
		super(url);
		this.parentHandler = parentHandler;
	}
  
	public void parse(String inputStream,String errMsg) 
	{
		
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
								if (name.equalsIgnoreCase("error")){
									try{
										errMsg=parser.nextText();
										
									}catch(Exception ex){}
									isValidXmlBeforeEndDoc = true;
								
								}else if (name.equalsIgnoreCase("user_id")){
										
									Utils.USER_ID = parser.nextText();
									isValidXmlBeforeEndDoc = true;								
								}else if (name.equalsIgnoreCase("roleName")){
									
									Utils.ROLENAME = parser.nextText();															
								}else if(name.equalsIgnoreCase("Name")){
									Utils.USERNAME = parser.nextText();
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
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);
	}
}
