package com.aotd.parsers;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;

import com.aotd.helpers.HttpLoader;
import com.aotd.model.DispatchAllListModel;

public class DispatchOpenParser extends HttpLoader {
	
	//private ArrayList <CarsListModel> carsListFeeds = null;
	private Handler parentHandler;
	ArrayList<DispatchAllListModel> marrDispatchList;
	private String sStatus="";
	
	public DispatchOpenParser(Handler handler,String feedUrl , ArrayList<DispatchAllListModel> _arrDispatchList) 
	{
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
	            DispatchAllListModel mDispatchPresentListModel = null;
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
	                    	}	
	                    	else
	                    	{	
		                        if (name.equalsIgnoreCase("dispatches-info")){	                        	
		                        	isValidXmlBeforeEndDoc = true;
		                        	
		                        }else if (name.equalsIgnoreCase("order")){	
		                        	mDispatchPresentListModel = new DispatchAllListModel();
			                        isValidXmlBeforeEndDoc = true;	
			                        orderInfoFound = true;
			                      
		                        }else if (orderInfoFound == true){	       		                        	
		                        
		                        	if (name.equalsIgnoreCase("order_id")){	 
		                        		mDispatchPresentListModel.setOrder_id(parser.nextText());
		                        	}else if (name.equalsIgnoreCase("driver_id")){
		                        		mDispatchPresentListModel.setDriver_id((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("RDDateFormat")){
		                            	mDispatchPresentListModel.setRDDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("min")){
		                        		mDispatchPresentListModel.setMin((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("timeZone")){
		                            	mDispatchPresentListModel.setTimezone((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("orderColor")){
		                        		mDispatchPresentListModel.setOrderColor((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("accountName")){
		                            	mDispatchPresentListModel.setAccountName((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("DLDate")){
		                        		mDispatchPresentListModel.setDLDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("PUDate")){
		                            	mDispatchPresentListModel.setPUDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("hour")){
		                        		mDispatchPresentListModel.setHour((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("orderStatus"))
		                            {
		                            	String s1 = parser.nextText();
		                            	mDispatchPresentListModel.setOrderStatus(s1);
		                            	if(s1.equals("Open Order") || s1.equals("Picked up"))
		                            	{
		                            		sStatus = "Open Order";
		                            	}
		                            	else
		                            	{
		                            		sStatus = "";
		                            	}
		                            	
		                            }
		                            else if (name.equalsIgnoreCase("SignRoundTrip")){
		                            	mDispatchPresentListModel.setSignRoundTrip((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("SignDelivery")){
		                            	mDispatchPresentListModel.setSignDelivery((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("isRoundTrip")){
		                            	mDispatchPresentListModel.setIsRoundTrip((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("PCRoundTrip")){
		                            	mDispatchPresentListModel.setPCRoundTrip((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("company")){
		                            	mDispatchPresentListModel.setCompany((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("address")){
		                            	mDispatchPresentListModel.setAddress((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("suit")){
		                            	mDispatchPresentListModel.setSuit((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("city")){
		                            	mDispatchPresentListModel.setCity((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("state")){
		                            	mDispatchPresentListModel.setState((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("zip")){
		                            	mDispatchPresentListModel.setZip((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dlcompany")){
		                            	mDispatchPresentListModel.setDlcompany((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dladdress")){
		                            	mDispatchPresentListModel.setDladdress((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dlsuit")){
		                            	mDispatchPresentListModel.setDlsuit((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dlcity")){
		                            	mDispatchPresentListModel.setDlcity((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dlstate")){
		                            	mDispatchPresentListModel.setDlstate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("dlzip")){
		                            	mDispatchPresentListModel.setDlzip((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("pulat")){
		                            	mDispatchPresentListModel.setLatitude(Double.parseDouble(parser.nextText().toString()));
		                            }else if (name.equalsIgnoreCase("pulog")){
		                            	mDispatchPresentListModel.setLongitude(Double.parseDouble(parser.nextText().toString()));
		                            }else if (name.equalsIgnoreCase("dllat")){
		                            	mDispatchPresentListModel.setDlLatitude(Double.parseDouble(parser.nextText().toString()));
		                            }else if (name.equalsIgnoreCase("dllog")){
		                            	mDispatchPresentListModel.setDlLongitude(Double.parseDouble(parser.nextText().toString()));
		                            }
		                        }
	                    	}
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase("order") && orderInfoFound == true){
	                        	
	                        	if(sStatus.equals("Open Order")){
	                        	marrDispatchList.add(mDispatchPresentListModel);}
	                        	orderInfoFound =false;
	                    	
	                        }                      
	                        break;
	                    case XmlPullParser.END_DOCUMENT: 
							
							if (isValidXmlBeforeEndDoc == false) 
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
	        	
	        	e.printStackTrace();
	        	 errMsg="Invalid response from AOTD Server";
	        
	        }

		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;
		if(errMsg.length() == 0)
		{
			if(noOrdersAvailable)
			{
				errMsg= "No Orders are  available";
			}
		}
		messageData.putString("HttpError",errMsg);
		//messageData.putSerializable("arrMyCarsList",carsListFeeds);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
