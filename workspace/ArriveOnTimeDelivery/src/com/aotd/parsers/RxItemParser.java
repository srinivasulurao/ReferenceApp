package com.aotd.parsers;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;


import com.aotd.model.RxItemModel;

public class RxItemParser  {
	
	private ArrayList <RxItemModel> arr_rxitem = null;
	private Handler parentHandler;
	String errMsg = "";
	
	
	public RxItemParser(Handler handler,ArrayList<RxItemModel> arr) 
	{
		
		parentHandler=handler;	
		arr_rxitem = arr;
	}
	
	
	
	public void parse(String data) 
	{
	
			XmlPullParser parser = Xml.newPullParser();
			
	        try {
		        	boolean isValidXmlBeforeEndDoc = false;
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
		                        	mRxItemObject = new RxItemModel();
		                        }else if (isValidXmlBeforeEndDoc && mRxItemObject!=null){               
		                        
		                        	if (name.equalsIgnoreCase("ControlledDrug")){	 
		                        		mRxItemObject.setControlledDrug(parser.nextText());
		                        	}else if (name.equalsIgnoreCase("NHome")){
		                        		mRxItemObject.setNHome((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("NHomeCode")){
		                            	mRxItemObject.setNHomeCode((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("RxDescription")){
		                            	mRxItemObject.setRxDescription((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("RxN")){
		                            	mRxItemObject.setRxN((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("RxQuantity")){
		                            	mRxItemObject.setRxQuantity((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("Status")){
		                            	mRxItemObject.setStatus((parser.nextText()));
		                            }
		                        }
	                    	
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase("RxItem")){
	                        	
	                        	arr_rxitem.add(mRxItemObject);
	                        	mRxItemObject = null;
	                    	
	                        }                      
	                        break;
	                    case XmlPullParser.END_DOCUMENT: 
							
							if (!isValidXmlBeforeEndDoc) 
							{
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

		
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 1;		
		messageData.putString("HttpError",errMsg);
		//messageData.putSerializable("arrMyCarsList",carsListFeeds);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
