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
import com.aotd.model.DispatchListModel;
import com.aotd.model.OfflineDataModel;

public class OfflineDispatchParser extends HttpLoader {
	
	private Handler parentHandler;
	
	private ArrayList<OfflineDataModel> marrOfflineDispatchList;
	private ArrayList<DispatchListModel> marrDispatchList ;
	
	public OfflineDispatchParser(Handler handler , String feedUrl, ArrayList<OfflineDataModel> _arrDispatchList, ArrayList<DispatchListModel> _arrDispatchListOne) {
		super(feedUrl);
		parentHandler = handler;
		marrOfflineDispatchList = _arrDispatchList;
		marrDispatchList = _arrDispatchListOne;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(String inputStream, String errMsg) {
		// TODO Auto-generated method stub
		boolean noOrdersAvailable = false;
		if(errMsg.length() == 0)
		{
			XmlPullParser parser = Xml.newPullParser();
			Log.e("Offline Database",  "Parsing Started");
			try{
					boolean isValidXmlBeforeEndDoc = false;
					parser.setInput(new StringReader(inputStream));
					int eventType = parser.getEventType();
					OfflineDataModel mOfflineData = null;
					DispatchListModel dispatchListModel = null;
					boolean done = false;
					boolean orderInfoFound = false;
					boolean PuAddress = false;
					boolean DLAddress = false;
					while(!done)
					{
						String name = null;
						switch(eventType)
						{
							case XmlPullParser.START_DOCUMENT:
								
								break;
							case XmlPullParser.START_TAG:
								name = parser.getName();
								if(name.equalsIgnoreCase("info"))
								{
									noOrdersAvailable = true;
		                    		isValidXmlBeforeEndDoc = true;
								}
								else
								{
									if(name.equalsIgnoreCase("dispatches-info"))
										isValidXmlBeforeEndDoc = true;
									else if(name.equalsIgnoreCase("order"))
									{
										dispatchListModel = new DispatchListModel();
										mOfflineData = new OfflineDataModel();
										isValidXmlBeforeEndDoc = true;
										orderInfoFound = true;
									}
									else if(name.equalsIgnoreCase("PUAddress"))
									{
										PuAddress = true;
									}
									else if(PuAddress == true)
									{
										if(name.equalsIgnoreCase("seq"))
											mOfflineData.setseqPU(parser.nextText());
										if(name.equalsIgnoreCase("company"))
											mOfflineData.setCompanyPU(parser.nextText());
										if(name.equalsIgnoreCase("address"))
											mOfflineData.setAddressPU(parser.nextText());
										if(name.equalsIgnoreCase("suit"))
											mOfflineData.setSuitPU(parser.nextText());
										if(name.equalsIgnoreCase("city"))
											mOfflineData.setCityPU(parser.nextText());
										if(name.equalsIgnoreCase("state"))
											mOfflineData.setStatePU(parser.nextText());
										if(name.equalsIgnoreCase("zip"))
											mOfflineData.setZipPU(parser.nextText());
										if(name.equalsIgnoreCase("cellPhone"))
											mOfflineData.setCellPhonePU(parser.nextText());
										if(name.equalsIgnoreCase("homePhone"))
											mOfflineData.setHomePhonePU(parser.nextText());
									}
									else if(name.equalsIgnoreCase("DLAddress"))
									{
										DLAddress = true;
									}
									else if(DLAddress == true)
									{
										if(name.equalsIgnoreCase("seq"))
											mOfflineData.setseqDL(parser.nextText());
										if(name.equalsIgnoreCase("company"))
											mOfflineData.setCompanyDL(parser.nextText());
										if(name.equalsIgnoreCase("address"))
											mOfflineData.setAddressDL(parser.nextText());
										if(name.equalsIgnoreCase("suit"))
											mOfflineData.setSuitDL(parser.nextText());
										if(name.equalsIgnoreCase("city"))
											mOfflineData.setCityDL(parser.nextText());
										if(name.equalsIgnoreCase("state"))
											mOfflineData.setStateDL(parser.nextText());
										if(name.equalsIgnoreCase("zip"))
											mOfflineData.setZipDL(parser.nextText());
										if(name.equalsIgnoreCase("cellPhone"))
											mOfflineData.setCellPhoneDL(parser.nextText());
										if(name.equalsIgnoreCase("homePhone"))
											mOfflineData.setHomePhoneDL(parser.nextText());
									}
									else if(orderInfoFound == true)
									{
										if (name.equalsIgnoreCase("order_id")){	 
											
											//Log.e("parser", "order nexttext" + parser.nextText());
											String str = parser.nextText();
			                        		mOfflineData.setOrder_id(str);
			                        		dispatchListModel.setOrder_id(str);
			                        		
			                        	}else if (name.equalsIgnoreCase("driver_id")){
			                        		
			                        		String str = parser.nextText();
			                        		mOfflineData.setDriver_id(str);
			                        		dispatchListModel.setDriver_id(str);
			                        		
			                            }else if (name.equalsIgnoreCase("RDDateFormat")){
			                            	mOfflineData.setRDDateFormat((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("min")){
			                        		mOfflineData.setMin((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("timeZone")){
			                            	mOfflineData.setTimeZone((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("accountName")){
			                            	mOfflineData.setAccountName((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("ref")){
			                            	mOfflineData.setRev((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("PUInstruction")){
			                            	mOfflineData.setPUInstruction((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("DLInstruction")){
			                            	mOfflineData.setDLInstruction((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("serviceName")){
			                            	mOfflineData.setServiceName((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("requestor")){
			                            	mOfflineData.setRequestor((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("piece")){
			                            	mOfflineData.setPiece((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("weight")){
			                            	mOfflineData.setWeight((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("DPDate")){
			                            	mOfflineData.setDPDate((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("DLDate")){
			                        		mOfflineData.setDLDate((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("PUDate")){
			                            	mOfflineData.setPUDate((parser.nextText()));
			                            }else if(name.equalsIgnoreCase("RDDate")){
			                            	
			                            	String str = parser.nextText();
			                            	mOfflineData.setRDDate(str);
			                        		dispatchListModel.setRDDate(str);
											
			                            }else if (name.equalsIgnoreCase("hour")){
			                        		mOfflineData.setHour((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("SignRoundTrip")){
			                            	mOfflineData.setSignRoundTrip((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("SignDelivery")){
			                            	mOfflineData.setSignDelivery((parser.nextText()));
			                            }else if (name.equalsIgnoreCase("isRoundTrip")){
			                            	
			                            	String str = parser.nextText();
			                            	mOfflineData.setIsRoundtrip(str);
			                        		dispatchListModel.setRoundTrip(str);
			                        		
			                            }else if (name.equalsIgnoreCase("PCRoundTrip")){
			                            	mOfflineData.setPcRoundtrip((parser.nextText()));
			                            }
										
										}
									}
								break;
							 case XmlPullParser.END_TAG:
			                        name = parser.getName();
			                        
			                        if (name.equalsIgnoreCase("order") && orderInfoFound == true){
			                        	Log.e("parser", "Order id is " +  mOfflineData.getOrder_id());
			                        	marrOfflineDispatchList.add(mOfflineData);
			                        	marrDispatchList.add(dispatchListModel);
			                        	orderInfoFound =false;
			                        	
			                        	mOfflineData = null;
			        					dispatchListModel = null;
			                    	
			                        }    
			                        else if (name.equalsIgnoreCase("PUAddress") && PuAddress == true){
			                        	
			                        	//marrOfflineDispatchList.add(mOfflineData);
			                        	PuAddress =false;
			                    	
			                        }
			                        else if (name.equalsIgnoreCase("DLAddress") && DLAddress == true){
			                        	
			                        	//marrOfflineDispatchList.add(mOfflineData);
			                        	DLAddress =false;
			                    	
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
			
			catch(Exception e)
			{
				System.out.println("Offline data exception is "  + e);
				errMsg = " Invalid response from AOTD server ";
			}
		}
		Message messageToParent = new Message();
		Bundle messageData = new Bundle();
		messageToParent.what = 0;
		if(errMsg.length() > 0)
		{
			Log.e("offline Database" , "error Msg: "+errMsg);
			messageToParent.what = 1;
		}
		messageData.putString("HttpError",errMsg);
		messageToParent.setData(messageData);
		parentHandler.sendMessage(messageToParent);

	}
}
