package com.aotd.map.helper;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;

public class MapRouteDetailsParser extends HttpLoader {
	
	Handler parentHandler;
	ArrayList<RouteDetailsModel> routeDetail_arr=null;
	
	public MapRouteDetailsParser(Handler handler,String feedUrl , ArrayList<RouteDetailsModel> _arrMyList){
		super(feedUrl);
		
		parentHandler=handler;
		routeDetail_arr = _arrMyList;
		
	}
	
	public void parse(String inputStream,String errMsg) {
		
		boolean step_distace = false;
		boolean distace = false;
		boolean duration = false;
		boolean step  =false;
		if(errMsg.toString().length() == 0)
		{
			
			XmlPullParser parser=Xml.newPullParser();
			try 
			{
				parser.setInput(new StringReader(inputStream));
				Log.e("net","connected");
				int eventType=parser.getEventType();
				RouteDetailsModel modelobj=null;	
				RouteDetailsModel extra_modelobj=null;	
				boolean done = false;
				while(eventType!=XmlPullParser.END_DOCUMENT && !done)
				{
					String name=null;
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						
						break;
					case XmlPullParser.START_TAG:
						name=parser.getName();
						if(name.equalsIgnoreCase("step"))
						{
							modelobj=new RouteDetailsModel();
							step=true;
							
							
						}else if (modelobj!=null){
							
							if (name.equalsIgnoreCase("html_instructions")) {
								modelobj.setHtml_instructions(parser.nextText());
								Log.e("", "html_instructions");
							}
							else if(name.equalsIgnoreCase("distance")){
								step_distace = true;
								Log.e("", "businessTitle");
							}else if(name.equalsIgnoreCase("text")){
								if(step_distace){
									modelobj.setStep_distance_text(parser.nextText());
									step_distace = false;
								}    		
								Log.e("", "businessTitle");
							}	 
							
						}else if(name.equalsIgnoreCase("duration")&&!step){
							
							extra_modelobj = new RouteDetailsModel();
							duration = true;
							
						}else if(name.equalsIgnoreCase("distance")&&!step){
							
							distace = true;
							
						}else if(name.equalsIgnoreCase("text")){
							
							if(duration){
								extra_modelobj.setDuration_text(parser.nextText());
								duration=false;
							}else if(distace){
								extra_modelobj.setDistance_text(parser.nextText());
								distace=false;
							}
						}
						
						break;
						
					case XmlPullParser.END_TAG:
						name=parser.getName();
						if(name.equalsIgnoreCase("step")  && modelobj!=null)
						{			
							routeDetail_arr.add(modelobj);
							Log.e("@@@@@", " "+routeDetail_arr.size());
							modelobj = null;	
							step=false;
						}else if(name.equalsIgnoreCase("DirectionsResponse")  && extra_modelobj!=null){
							
							routeDetail_arr.add(extra_modelobj);
							extra_modelobj=null;
						}
						break;
						
					}
					eventType=parser.next();
				}
			} 
			catch (Exception e)
			{
				
				errMsg="Invalid response from Google Server";
				throw new RuntimeException(e);
				
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