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
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.RNABatchIdModel;
import com.aotd.model.RxItemModel;

public class RNAParser extends HttpLoader {
	
	//private ArrayList <CarsListModel> carsListFeeds = null;
	private Handler parentHandler;
	ArrayList<RNABatchIdModel> marrDispatchList;
	boolean isValidXmlBeforeEndDoc = false;
	
	public RNAParser(Handler handler,String feedUrl , ArrayList<RNABatchIdModel> _arrDispatchList) 
	{
		super(feedUrl);
		parentHandler=handler;		 
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
	        	
	            parser.setInput(new StringReader(inputStream));
	            int eventType = parser.getEventType();	           
	            RNABatchIdModel mRNABatchIdModel = null;
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
	                    	if(name.equalsIgnoreCase("info")){
	                    		noOrdersAvailable = true;
	                    		isValidXmlBeforeEndDoc = true;
	                    	}	
	                    	else
	                    	{	
		                        if (name.equalsIgnoreCase("rna-info")){	                        	
		                        	isValidXmlBeforeEndDoc = true;			                        	
		                        }else if(isValidXmlBeforeEndDoc && name.equalsIgnoreCase("rnaorder")){
		                        	mRNABatchIdModel = new RNABatchIdModel();
		                        }else if (isValidXmlBeforeEndDoc && mRNABatchIdModel!=null ){                 	
		                        
		                        	if (name.equalsIgnoreCase("batchId")){	 
		                        		mRNABatchIdModel.setBatchId(parser.nextText());
		                        	}else if (name.equalsIgnoreCase("stationId")){
		                        		mRNABatchIdModel.setStationId((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("deviceName")){
		                            	mRNABatchIdModel.setDeviceName((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("status")){
		                            	mRNABatchIdModel.setStatus((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("DPDate")){
		                            	mRNABatchIdModel.setDPDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("PUDate")){
		                            	mRNABatchIdModel.setPUDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("DLDate")){
		                            	mRNABatchIdModel.setDLDate((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("batchDetails")){
		                            	mRNABatchIdModel.setBatchDetails((parser.nextText()));
		                            }else if (name.equalsIgnoreCase("sign")){
		                            	mRNABatchIdModel.setSign((parser.nextText()));
		                            }
		                        }
	                    	}
	                        break;
	                        
	                    case XmlPullParser.END_TAG:
	                        name = parser.getName();
	                        if (name.equalsIgnoreCase("rnaorder")){
	                        	
	                        	marrDispatchList.add(mRNABatchIdModel);
	                        	
	                    	
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

		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;	
		if(errMsg.length() == 0)
		{
			if(noOrdersAvailable)
			{
				errMsg= "No RNA Orders are available";
			}
		}
		messageData.putString("HttpError",errMsg);	
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}

}
