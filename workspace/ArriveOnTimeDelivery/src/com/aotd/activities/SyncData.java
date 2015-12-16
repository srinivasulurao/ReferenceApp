package com.aotd.activities;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.aotd.helpers.AOTDDataBase;
import com.aotd.helpers.SyncUploadPDF;
import com.aotd.model.OfflineDataModel;
import com.aotd.parsers.SyncDataParser;
import com.aotd.utils.Utils;

public class SyncData {

	private Context context;
	private ArrayList<OfflineDataModel> marrDispatchListOffline = new ArrayList<OfflineDataModel>();
	private int itemCount = 0;
	private StringBuffer order_ids = new StringBuffer();
	private String sep = "";
	private Handler handler;

	private final int UPDATE_DONE 	=	10;
	private final int ERROR			=	11;
	private final int NO_UPDATES	=	12;
	private ArrayList<String> mFiles = new ArrayList<String>();

	
	public SyncData(Context context, Handler handler){
		
		this.context 		= 	context;
		this.handler 		= 	handler;
		
		syncData();
	}
	
	private void syncData(){
		
		AOTDDataBase _AOTDDataBase = new AOTDDataBase(context);
		marrDispatchListOffline = _AOTDDataBase.getSyncabelData();	
		mFiles = getUploadPDFAbsolutePathList();
		
		if((marrDispatchListOffline != null && marrDispatchListOffline.size()>0) || (mFiles!= null && mFiles.size() > 0)){
			if ((mFiles!= null && mFiles.size() > 0)) {
				
				Log.e("Sync Data", "kkk ******************* updateDatabase records found "+marrDispatchListOffline.size() +"       mFiles size "+mFiles.size());
			}
			syncDataToServer();
		}else{
			Log.e("Sync Data", "kkk ******************* NO_UPDATES records not found");
            /**
             * need to delete aotd folder...
             */
			handler.sendEmptyMessage(NO_UPDATES);
		}
			
	}
	
	private ArrayList<String> getUploadPDFAbsolutePathList(){
		try {
			
        File mDirectory;
        String folderPath = Environment.getExternalStorageDirectory()+ "/AOTD/";
        mDirectory = new File(folderPath);
        
        if (mDirectory.exists()) {
			
        	// Get the files in the directory
        	File[] files = mDirectory.listFiles();
        	if (files != null && files.length > 0) {
        		for (File f : files) {
        			
        			String fileName = f.getAbsolutePath().toString().trim().substring(f.getAbsolutePath().toString().trim().lastIndexOf('/') + 1);
    				

    				if(!TextUtils.isEmpty(fileName) && fileName.contains("_sign")){
            			mFiles.add(f.getAbsolutePath());

    				}
        		}
        		
        		return mFiles;
        	}else{
        		
        		return null;
        	}
        	
		}else{
			
			return null;
		}

		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	private void syncDataToServer(){
	
		try
		{
			
			
// 			If DLdate is "0000" then update Dispatch and pick-Up
			
			if(marrDispatchListOffline != null && marrDispatchListOffline.size()>0 && marrDispatchListOffline.get(itemCount).getDLDate().startsWith("0000")){
				
				Log.e("Sync Data", "******************* up to pickup and dispatch");
				
				String roleName 	= URLEncoder.encode(Utils.ROLENAME,"UTF-8");
				String order_id		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getOrder_id(),"UTF-8");
				String dpDate 		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDPDate(),"UTF-8"); 	
				String pcDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");
				
				String url = String.format(Utils.OFFLINE_PICK_UP_UPDATE, roleName, order_id, dpDate, pcDate); 
				Log.e("", "Sync data "+url);
				
				SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncDataHandler());
				mSyncDataParser.start(); 
				
			}else if(marrDispatchListOffline != null && marrDispatchListOffline.size()>0 && !marrDispatchListOffline.get(itemCount).getDLDate().startsWith("0000") 
					&& marrDispatchListOffline.get(itemCount).getSignRoundTrip().length()==0){
				
				Log.e("Sync Data", "******************* Delivery and round trip pickup");
				
				String roleName 	= URLEncoder.encode(Utils.ROLENAME,"UTF-8");
				String order_id		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getOrder_id(),"UTF-8");
				String dpDate 		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDPDate(),"UTF-8"); 	
				String dlDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDLDate(),"UTF-8");	
				String puDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");	
				
				String roundTrip = "";
				if(marrDispatchListOffline.get(itemCount).getPcRoundtrip().equalsIgnoreCase("0"))
					roundTrip	= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getIsRoundtrip(),"UTF-8");
				else
					roundTrip = "1";
				
				String waittime		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getWaittime(),"UTF-8");	
				String transport	= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getVehicle(),"UTF-8");	
				String boxes		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getBoxes(),"UTF-8");
				
				String url = String.format(Utils.OFFLINE_DELIVERY_UPDATE, roleName, order_id, dpDate, dlDate, puDate, roundTrip,
						waittime, transport, boxes); 
				
				Log.e("", "Sync data "+url);
				
				SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncDataHandler());
				
				Hashtable<String, String> parameters = new Hashtable<String, String>();
				parameters.put("notes", marrDispatchListOffline.get(itemCount).getNotes());
				parameters.put("lastname", marrDispatchListOffline.get(itemCount).getLastname());
			
				mSyncDataParser.isMultipart=true;
				mSyncDataParser.params = parameters;

				mSyncDataParser.imgBytes = marrDispatchListOffline.get(itemCount).getSignature();
				mSyncDataParser.mFileName = marrDispatchListOffline.get(itemCount).getSignDelivery();
				mSyncDataParser.start();
				
			}else if(mFiles.size() >0 && !TextUtils.isEmpty(mFiles.get(itemCount).toString().trim())){
				/**
				 * sync pdf upload
				 */
				Log.e("", "kkk Sync data ");
				Log.e("", "kkk sync fileName : "+ mFiles.get(itemCount).toString().trim());
				
					SyncUploadPDF mSyncUploadPDF = new SyncUploadPDF(context, mFiles.get(itemCount).toString().trim(),new SyncDataHandler());
					mSyncUploadPDF.execute();
				
				
				
			}
			else{
				if (marrDispatchListOffline != null && marrDispatchListOffline.size()>0) {
					
				
				
				Log.e("Sync Data", "******************* Round trip Delivery");
				
				String roleName 	= URLEncoder.encode(Utils.ROLENAME,"UTF-8");
				String order_id		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getOrder_id(),"UTF-8");
				String dpDate 		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDPDate(),"UTF-8"); 	
				String dlDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDLDate(),"UTF-8");	
				String puDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");	
				String waittime		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getWaittime(),"UTF-8");	
				String transport	= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getVehicle(),"UTF-8");	
				String boxes		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getBoxes(),"UTF-8");	
				String signDelivery		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getSignDelivery(),"UTF-8");
				
				String url = String.format(Utils.OFFLINE_ROUNDTRIP_DELIVERY, roleName, order_id, dpDate, dlDate, puDate,
						waittime, transport, boxes, signDelivery); 
				
				Log.e("", "Sync data "+url);
				
				SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncSignatureHandler());
				
				Hashtable<String, String> parameters = new Hashtable<String, String>();
				parameters.put("notes", marrDispatchListOffline.get(itemCount).getNotes());
				parameters.put("lastname", marrDispatchListOffline.get(itemCount).getLastname());
				parameters.put("lastnamedl", marrDispatchListOffline.get(itemCount).getLastnamedl());
			
				mSyncDataParser.isMultipart=true;
				mSyncDataParser.params = parameters;

				mSyncDataParser.imgBytes = marrDispatchListOffline.get(itemCount).getSignaturedl();
				mSyncDataParser.mFileName = marrDispatchListOffline.get(itemCount).getSignRoundTrip();
//				mSyncDataParser.imgBytes = marrDispatchListOffline.get(itemCount).getSignature();
//				mSyncDataParser.mFileName = marrDispatchListOffline.get(itemCount).getSignDelivery();
				mSyncDataParser.start();
				}else {
					
					Log.e("Sync Data", "******************* else case");
					handler.sendEmptyMessage(ERROR);
				}
			}

			
			
		}catch(Exception e){
			
			Log.e("Sync Data", "******************* error");
			e.printStackTrace();
			handler.sendEmptyMessage(ERROR);
		}	
		
	}
	
	
	private void updateDeliverySignature(){
		
		try {
			
			String roleName 	= URLEncoder.encode(Utils.ROLENAME,"UTF-8");
			String order_id		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getOrder_id(),"UTF-8");
			
			String url = String.format(Utils.OFFLINE_SIGNATURE_DELIVERY, roleName, order_id); 
			
			Log.e("", "Sync data "+url);
			
			SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncDataHandler());
			
			Hashtable<String, String> parameters = new Hashtable<String, String>();
			parameters.put("lastname", "aotd");
			mSyncDataParser.isMultipart=true;
			mSyncDataParser.params = parameters;
			
			mSyncDataParser.imgBytes = marrDispatchListOffline.get(itemCount).getSignature();
			mSyncDataParser.mFileName = marrDispatchListOffline.get(itemCount).getSignDelivery();
			mSyncDataParser.start();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}
	
	
	
	class SyncDataHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			Log.e("offline Database", "kkk back to handler marrDispatchListOffline size "+ marrDispatchListOffline.size());
			Log.e("offline Database", "kkk back to handler msg.what "+ msg.what);

			if(msg.what == 1){
				
				Log.e(" Database Updated", "Synce data updated " + itemCount+" record");
				
				order_ids.append(sep+ marrDispatchListOffline.get(itemCount).getOrder_id());
				sep = ",";
				
				
				marrDispatchListOffline.remove(0);
				Log.e("offline Database", "kkk back to handler after remove data marrDispatchListOffline size "+ marrDispatchListOffline.size());

				if(marrDispatchListOffline.size() <=0 ){
					if (order_ids.length() > 0) {
						AOTDDataBase _AOTDDataBase = new AOTDDataBase(context);
						_AOTDDataBase.updatedDone(order_ids.toString());
						handler.sendEmptyMessage(UPDATE_DONE);
					}
					
					
				}	
				if((marrDispatchListOffline != null && marrDispatchListOffline.size()>0) || (mFiles!= null && mFiles.size() > 0)){

					
					syncDataToServer();
				}
//				}else{
//					
//					AOTDDataBase _AOTDDataBase = new AOTDDataBase(context);
//					_AOTDDataBase.updatedDone(order_ids.toString());
//					handler.sendEmptyMessage(UPDATE_DONE);
//					
//				}				
				
			}else if(msg.what == 11){				
				
				handler.sendEmptyMessage(ERROR);		
				
			}else if(msg.what == 12){

				mFiles.remove(0);
				
				Log.e("offline Database", "kkk back to handler msg.what "+ msg.what+" "+(marrDispatchListOffline != null && marrDispatchListOffline.size()>0)+" "+(mFiles!= null && mFiles.size() > 0));

				if((marrDispatchListOffline != null && marrDispatchListOffline.size()>0) || (mFiles!= null && mFiles.size() > 0)){
					Log.e("offline Database", "kkk back to handler msg.what if ...."+ msg.what);

					syncDataToServer();
				}else{
					handler.sendEmptyMessage(UPDATE_DONE);
				}
				
			}else if(msg.what == 13){
				
				handler.sendEmptyMessage(ERROR);	
			}
		}
	}
	
	
	
	class SyncSignatureHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			Log.e("offline Database", "back to handler");
			
			if(msg.what == 1){
				
				Log.e(" Database Updated", "Synce data updated " + itemCount+" record");
				
				String strMsg = msg.getData().getString("success");
				
				
				if(strMsg.equalsIgnoreCase("roundtrip delivery success")){
					
					updateDeliverySignature();
									
				}else{
					
					order_ids.append(sep+ marrDispatchListOffline.get(itemCount).getOrder_id());
					sep = ",";
					
					
					marrDispatchListOffline.remove(0);
					
					if(marrDispatchListOffline.size() <=0 ){
						
						if (order_ids.length() > 0) {
							AOTDDataBase _AOTDDataBase = new AOTDDataBase(context);
							_AOTDDataBase.updatedDone(order_ids.toString());
							handler.sendEmptyMessage(UPDATE_DONE);
						}
						
						
					}	
					if((marrDispatchListOffline != null && marrDispatchListOffline.size()>0) || (mFiles!= null && mFiles.size() > 0)){

						
						syncDataToServer();
					}
//					}else{
//						
//						AOTDDataBase _AOTDDataBase = new AOTDDataBase(context);
//						_AOTDDataBase.updatedDone(order_ids.toString());
//						handler.sendEmptyMessage(UPDATE_DONE);
//						
//					}						
					
				}
				
			}else if(msg.what == 11){		
				
				
				if(Utils.checkNetwork(context)){
					
					handler.sendEmptyMessage(UPDATE_DONE);

				}else{
					
					handler.sendEmptyMessage(ERROR);		
				}
				
			}
		}
	}
}