package com.aotd.parsers;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;

public class GetNoteParser extends HttpLoader {
	
	private Handler parentHandler;
	public GetNoteParser(String url, Handler parentHandler) {
		
		super(url);
		this.parentHandler = parentHandler;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void parse(String inputStream,String errMsg){
		
		if(errMsg.length() == 0){
			
			XmlPullParser parser = Xml.newPullParser();		
			
			try{
				
				parser.setInput(new StringReader(inputStream));
				
				boolean done = false;
				boolean isValidXmlBeforeEndDoc = false;
				int eventType = parser.getEventType();
				String noteText = "";
				
				while ( !done){
					
					switch (eventType){
					
					case XmlPullParser.START_TAG:
						
						String name = parser.getName();
						if (name.equalsIgnoreCase("error")){
							try{
								errMsg=parser.nextText();
								
							}catch(Exception ex){}
							isValidXmlBeforeEndDoc = true;
							
						}else if (name.equalsIgnoreCase("notes")){
							noteText = parser.nextText();
							//Log.d("note", parser.nextText());
							isValidXmlBeforeEndDoc = true;								
						}
						break;
						
					case XmlPullParser.END_DOCUMENT: 
						
						if (isValidXmlBeforeEndDoc == false){
							
							errMsg="Invalid response from AOTD Server";
							
						}else{
							
							Message messageToParent = new Message();
							Bundle messageData = new Bundle();
							messageToParent.what = 1;
							messageData.putString("note",noteText);
							messageToParent.setData(messageData);
							parentHandler.sendMessage(messageToParent);
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