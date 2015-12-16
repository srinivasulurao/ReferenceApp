package com.rna.activities;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.aotd.helpers.RNADataBase;
import com.aotd.model.RNABatchIdModel;
import com.aotd.parsers.SyncDataParser;
import com.aotd.parsers.UploadImageDataParser;
import com.aotd.utils.Utils;

public class SyncRNAData {

	private Context context;
	private ArrayList<RNABatchIdModel> marrDispatchListOffline = null;
	private int itemCount = 0;
	private StringBuffer order_ids = new StringBuffer();
	private String sep = "";
	private Handler handler;

	private final int UPDATE_DONE 	=	10;
	private final int ERROR			=	11;
	private final int NO_UPDATES	=	12;
	
	public SyncRNAData(Context context, Handler handler){
		
		this.context 		= 	context;
		this.handler 		= 	handler;
		
		syncData();
	}
	
	private void syncData(){
		
		
		
		RNADataBase _RNADataBase = new RNADataBase(context);
		marrDispatchListOffline = _RNADataBase.getSyncabelData();
		
		if(marrDispatchListOffline.size()>0){
			Log.e("Sync Data", "******************* updateDatabase records found "+marrDispatchListOffline.size());
			syncDataToServer();
		}else{
			Log.e("Sync Data", "******************* NO_UPDATES:: records not found");

			handler.sendEmptyMessage(NO_UPDATES);
		}
			
	}
	
	
	private void syncDataToServer(){
	
		try
		{
			
			if(marrDispatchListOffline.get(itemCount).getDLDate().startsWith("0000")){
				
				Log.e("Sync Data", "******************* up to pickup and dispatch");
				
				String batchId		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getBatchId(),"UTF-8");
				String puDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");
				
				String url = String.format(Utils.OFFLINE_RNA_PICKUP, batchId, puDate); 
				Log.e("", "Sync data "+url);
				
				SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncDataHandler());
				mSyncDataParser.start(); 
				
			}else{
				
				
				try {
					
					String batchId			= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getBatchId(),"UTF-8");
					String rna_xmlData		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");	
					String rna_nhomecode	= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDLDate(),"UTF-8");	
					
					String url = Utils.RNA_RXCheckin_URL;
					UploadImageDataParser mUploadImageDataParser = new UploadImageDataParser(new RNADataUploadHandler(),url,"rna");
					//Base64.encodeToString(marrSignModel.get(0).getBytes(), Base64.DEFAULT)
					String data = rna_xmlData+"||||"+ Base64.encodeToString(marrDispatchListOffline.get(itemCount).getSignature(), Base64.DEFAULT) ;
					data = data + "||||SYSTEM||||MYDEVICENAME||||"+batchId+"||||"+rna_nhomecode+"||||08/14/2012 12:34:00||||08/14/2012 17:34:00||||NurseInitials";
					Log.v("data",data);
					byte[] data_in_bytes = data.getBytes();					
					mUploadImageDataParser.responseBytes = data_in_bytes;
					mUploadImageDataParser.isPost=true;
					mUploadImageDataParser.isUpload = true;
					mUploadImageDataParser.start(); 
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
				
			}
			
		}catch(Exception e){
			
			Log.e("Sync Data", "******************* error");
			e.printStackTrace();
			handler.sendEmptyMessage(ERROR);
		}	
		
	}
	
	
	
	private void rnaSignUploadToAotdServer() 
	{
		try {
			
			String batchId		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getBatchId(),"UTF-8");
			String puDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getPUDate(),"UTF-8");	
			String dlDate		= URLEncoder.encode(marrDispatchListOffline.get(itemCount).getDLDate(),"UTF-8");	
			
			
			String url = String.format(Utils.OFFLINE_RNA_ORDER_DELIVERY, batchId, puDate, dlDate); 
			
			Log.e("", "Sync data "+url);
			
			SyncDataParser mSyncDataParser= new SyncDataParser(url, new SyncDataHandler());		
			Hashtable<String, String> parameters = new Hashtable<String, String>();
			Log.i("AOTD service debug",url);
			parameters.put("firstname", "rna");
			
			mSyncDataParser.isMultipart=true;
			mSyncDataParser.params = parameters;
			
			mSyncDataParser.imgBytes = marrDispatchListOffline.get(itemCount).getSign().getBytes();
			mSyncDataParser.mFileName = marrDispatchListOffline.get(itemCount).getFileName();
			mSyncDataParser.start(); 
			
			
		} catch (Exception e) {
			// TODO: handle exception
	}
		
		
		
	}
	
	
	
	
	class RNADataUploadHandler extends Handler
	{
		
		public void handleMessage(android.os.Message msg)
		{ 
			if(msg.what ==2)
			{
				String errorMsg=msg.getData().getString("HttpError");
				String successMsg=msg.getData().getString("success");
				
				if(errorMsg.length()>0){
								
					Log.v("sucessmsg",errorMsg);
					handler.sendEmptyMessage(ERROR);
					
				}else{	
					
					rnaSignUploadToAotdServer();
				}
				
			}
		}
	}
	
	
	
	class SyncDataHandler extends Handler
	{
		public void handleMessage(android.os.Message msg)
		{
			Log.e("offline Database", "back to handler");
			
			if(msg.what == 1){
				
				Log.e(" Database Updated", "Synce data updated " + itemCount+" record");
				
				order_ids.append(sep+ "'"+marrDispatchListOffline.get(itemCount).getBatchId()+"'");
				sep = ",";
				
				
				marrDispatchListOffline.remove(0);
				
				if(marrDispatchListOffline.size()>0){
					
					syncDataToServer();
					
				}else{
					
					RNADataBase _RNADataBase = new RNADataBase(context);
					_RNADataBase.updatedDone(order_ids.toString());
					handler.sendEmptyMessage(UPDATE_DONE);
					
				}				
				
			}else if(msg.what == 11){				
				
				handler.sendEmptyMessage(ERROR);		
				
			}
		}
	}
}
