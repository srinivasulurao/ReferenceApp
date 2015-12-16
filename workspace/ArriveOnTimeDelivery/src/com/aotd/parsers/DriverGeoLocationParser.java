package com.aotd.parsers;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;
import com.aotd.model.DriverGeoLocationModel;

public class DriverGeoLocationParser extends HttpLoader 
{
	private Handler parentHandler;
	
	public DriverGeoLocationParser(String url, Handler parentHandler)
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
				ArrayList<DriverGeoLocationModel> mGeoLocation = null;
				DriverGeoLocationModel mDriverGeoLocationModel = null;
				while ( !done) 
				{
					String currentTag = parser.getName();
					switch (eventType) 
					{
					
						case XmlPullParser.START_DOCUMENT:
							mGeoLocation = new ArrayList<DriverGeoLocationModel>();
							break;
						case XmlPullParser.START_TAG:
							if(currentTag.equalsIgnoreCase("driver")){
								mDriverGeoLocationModel = new DriverGeoLocationModel();
							}else if(mDriverGeoLocationModel != null){
								
								if(currentTag.equalsIgnoreCase("name")){
									isValidXmlBeforeEndDoc = true;
									mDriverGeoLocationModel.setName(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("dispatchNo")){
									mDriverGeoLocationModel.setDispatchNo(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("email")){
									mDriverGeoLocationModel.setEmail(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("phone")){
									mDriverGeoLocationModel.setPhone(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("commissionRate")){
									mDriverGeoLocationModel.setCommissionRate(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("address")){
									mDriverGeoLocationModel.setAddress(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("city")){
									mDriverGeoLocationModel.setCity(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("state")){
									mDriverGeoLocationModel.setState(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("zip")){
									mDriverGeoLocationModel.setZip(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("driverlicense")){
									mDriverGeoLocationModel.setDriverLicense(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("ssn")){
									mDriverGeoLocationModel.setSsn(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("lat")){
									mDriverGeoLocationModel.setLat(parser.nextText());
								}else if(currentTag.equalsIgnoreCase("lon")){
									mDriverGeoLocationModel.setLon(parser.nextText());
								}
							}
							break;
						case XmlPullParser.END_TAG:
							if(currentTag.equalsIgnoreCase("driver")){
								mGeoLocation.add(mDriverGeoLocationModel);
								mDriverGeoLocationModel = null;
							}
							break;
						case XmlPullParser.END_DOCUMENT: 
							
							if (!isValidXmlBeforeEndDoc) 
							{
								errMsg="Invalid response from AOTD Server";

							}else{
								Message messageToParent = new Message();
								Bundle messageData = new Bundle();
								messageToParent.what = 1;
								messageData.putSerializable("response",mGeoLocation);
								messageToParent.setData(messageData);
								parentHandler.sendMessage(messageToParent);
							}
							done = true;
		
							return;
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
