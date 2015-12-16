package com.aotd.parsers;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoaderOne;

public class DispatchCountParser extends HttpLoaderOne 
{
	private Handler parentHandler;
	
	public DispatchCountParser(String url, Handler parentHandler)
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
				String noOfDispatches = "", orderIds = "";
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
								
								if (name.equalsIgnoreCase("newDispatchCount")){
									
									noOfDispatches = parser.nextText().trim();
								
								}else if (name.equalsIgnoreCase("orderIds")){
									
									orderIds = parser.nextText().trim();
									isValidXmlBeforeEndDoc = true;
								
								}
								
								break;
								
							case XmlPullParser.END_TAG: 
								
								
								String name1 = parser.getName();
								
								if (name1.equalsIgnoreCase("DispatchCount")){
									
									done = true;
									
									if (!isValidXmlBeforeEndDoc){
										
										errMsg="Invalid response from AOTD Server";
										Message messageToParent = new Message();
										Bundle messageData = new Bundle();
										messageToParent.what = 0;
										messageData.putString("error",errMsg);
										messageToParent.setData(messageData);
										parentHandler.sendMessage(messageToParent);
										
									}else{
										
										Message messageToParent = new Message();
										Bundle messageData = new Bundle();
										messageToParent.what = 1;
										messageData.putString("response",noOfDispatches);
										messageData.putString("orderIds",orderIds);
										messageToParent.setData(messageData);
										parentHandler.sendMessage(messageToParent);
									}
								}
			
						}
						eventType = parser.next();
				   }
			} catch (Exception e) {
				errMsg="Invalid response from AOTD Server";
	
			} 
		}
		
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;
		messageData.putString("error",errMsg);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);
	}
}
