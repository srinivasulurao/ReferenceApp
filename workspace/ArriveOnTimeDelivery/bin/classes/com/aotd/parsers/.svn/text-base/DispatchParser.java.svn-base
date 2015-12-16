package com.aotd.parsers;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import com.aotd.helpers.HttpLoader;
import com.aotd.model.DispatchListModel;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;



public class DispatchParser extends HttpLoader {
	
	//private ArrayList <CarsListModel> carsListFeeds = null;
	private Handler parentHandler;
	ArrayList<DispatchListModel> marrDispatchList;
	
	public DispatchParser(Handler handler,String feedUrl , ArrayList<DispatchListModel> _arrDispatchList){
		super(feedUrl);
		parentHandler=handler;
		//carsListFeeds = new ArrayList<CarsListModel>();  
		marrDispatchList = _arrDispatchList;
	}
	
	
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
		            DispatchListModel mDispatchListModel = null;
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
		                    	if(name.equalsIgnoreCase("info")){
		                    		noOrdersAvailable = true;
		                    		isValidXmlBeforeEndDoc = true;
		                    	}else{	
			                        if (name.equalsIgnoreCase("dispatches-info")){	                        	
			                        	isValidXmlBeforeEndDoc = true;
			                        	
			                        }else if (name.equalsIgnoreCase("order")){	
			                        	mDispatchListModel = new DispatchListModel();
				                        isValidXmlBeforeEndDoc = true;	
				                        orderInfoFound = true;
				                      
			                        }else if (orderInfoFound == true){	                        		                        	
			                        	
			                        	if (name.equalsIgnoreCase("order_id")){	 
			                        		mDispatchListModel.setOrder_id(parser.nextText());
			                        	}else if (name.equalsIgnoreCase("driver_id")){
			                        		mDispatchListModel.setDriver_id((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("RDDateFormat")){
			                            	mDispatchListModel.setRDDate((parser.nextText()));
			                            }
			                        }
		                    	}
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase("order") && orderInfoFound == true){
	                        	
	                        	marrDispatchList.add(mDispatchListModel);
	                        	orderInfoFound =false;
	                    	
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
	        
	        }
	        catch (Exception e) {
	        	
	        	 errMsg="Invalid response from AOTD Server";
	        
	        }

		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;
		if(errMsg.length() == 0){
			
			
		}
		messageData.putString("HttpError",errMsg);
		//messageData.putSerializable("arrMyCarsList",carsListFeeds);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
