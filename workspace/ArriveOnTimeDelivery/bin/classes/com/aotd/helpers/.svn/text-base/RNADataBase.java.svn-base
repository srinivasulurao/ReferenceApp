package com.aotd.helpers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import com.aotd.model.RNABatchIdModel;
import com.aotd.model.RNABatchIdModel;
import com.aotd.model.RNABatchIdModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RNADataBase {
	
	DataBaseHelper mDatabase_helper;
	SQLiteDatabase mDatabase;
	
	public static final String TABLE_NAME 			= "RnaOffilneData";
	public static final String COLUMN_BATCHID 		= "batchid";
	public static final String COLUMN_STATIONID 	= "stationid";
	public static final String COLUMN_STATUS		= "status";
	public static final String COLUMN_DPDATE 		= "DPDate";
	public static final String COLUMN_PUDATE 		= "PUDate";
	public static final String COLUMN_DLDATE 		= "DLDate";
	public static final String COLUMN_BATCH_DETAILS = "batchdetials";
	public static final String COLUMN_SIGNATURE 	= "signature";
	public static final String COLUMN_FILENAME 		= "filename";
	public static final String COLUMN_RNA_DATA 		= "rnadata";
	public static final String COLUMN_RNA_HOME_CODE	= "rnahomecode";
	public static final String COLUMN_ACTION 		= "action";
	String mResult;

	public RNADataBase(Context context) 
	{
		mDatabase_helper = DataBaseHelper.getDBAdapterInstance(context);
	}
	
	
	public void checkRNADatabaseData()
	{
		Cursor cursor = null;
		try
		{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery("SELECT * FROM RnaOffilneData", null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					
					
					Log.e("","Offline data :: "
							+" BATCHID "+cursor.getString(cursor.getColumnIndex(COLUMN_BATCHID))
							+" DPDATE "+cursor.getString(cursor.getColumnIndex(COLUMN_DPDATE))
							+" PUDate "+cursor.getString(cursor.getColumnIndex(COLUMN_PUDATE))
							+" DLDate "+cursor.getString(cursor.getColumnIndex(COLUMN_DLDATE))
							+" ACTION "+cursor.getString(cursor.getColumnIndex(COLUMN_ACTION)));
					
					
				} while (cursor.moveToNext());
			}
			
		}
		finally
		{
			if (cursor != null) {
				
				cursor.close();
			}
		}
		
	}
	
	public void insertNewRNA(String batchId, String batchDetail, String DPDate)
	{
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_BATCHID, batchId);
		values.put(COLUMN_STATIONID, "111");
		values.put(COLUMN_STATUS, "Open");
		values.put(COLUMN_DPDATE, DPDate);
		values.put(COLUMN_PUDATE, "0000-00-00 00:00:00");
		values.put(COLUMN_DLDATE, "0000-00-00 00:00:00");
		values.put(COLUMN_BATCH_DETAILS, batchDetail);
		db.insert(TABLE_NAME, null, values);
		db.close(); 
	}
	
	
	
	public ArrayList<RNABatchIdModel> getOfflineRNAOrders( String today, String forDate )
	{
			
		
		String sql = "";
		if(forDate.equalsIgnoreCase("present")){
			
			sql = "select * from RnaOffilneData where  (DPDate >= '" + today + " 00:00:00' and DPDate <= '" + today + " 23:59:59')";
			
		}else if(forDate.equalsIgnoreCase("past")){
			
			sql = "select * from RnaOffilneData where (DPDate < '" + today + " 00:00:00')";
			
		}else{
			sql = "select * from RnaOffilneData where (DPDate > '" + today + " 23:59:59')";
			
		}
		
		Log.e("","sql***** "+ sql);
		ArrayList<RNABatchIdModel> rnaDispatchList = new ArrayList<RNABatchIdModel>();
		Cursor cursor = null;
		
		try
		{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery(sql, null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					
					RNABatchIdModel result = new RNABatchIdModel();
					result.setBatchId(cursor.getString(cursor.getColumnIndex(COLUMN_BATCHID)));
					result.setStationId(cursor.getString(cursor.getColumnIndex(COLUMN_STATIONID)));
					result.setDeviceName("MYDEVICENAME");
					result.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
					result.setDPDate(cursor.getString(cursor.getColumnIndex(COLUMN_DPDATE)));
					result.setPUDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUDATE)));
					result.setDLDate(cursor.getString(cursor.getColumnIndex(COLUMN_DLDATE)));
					result.setBatchDetails(cursor.getString(cursor.getColumnIndex(COLUMN_BATCH_DETAILS)));
					result.setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)));					
					rnaDispatchList.add(result);
					
				} while (cursor.moveToNext());
			}
			
		}
		finally
		{
			if (cursor != null) {
				
				cursor.close();
			}
		}
		
		return rnaDispatchList; 
	}
	
	
	
	public ArrayList<RNABatchIdModel> getSyncabelData()
	{
		ArrayList<RNABatchIdModel> dispatchList = new ArrayList<RNABatchIdModel>();
		Cursor cursor = null;
		try
		{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery("SELECT * FROM RnaOffilneData WHERE action = 'update'", null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					
					RNABatchIdModel result = new RNABatchIdModel();
					result.setBatchId(cursor.getString(cursor.getColumnIndex(COLUMN_BATCHID)));
					result.setStationId(cursor.getString(cursor.getColumnIndex(COLUMN_STATIONID)));
					result.setDeviceName("MYDEVICENAME");
					result.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
					result.setDPDate(cursor.getString(cursor.getColumnIndex(COLUMN_DPDATE)));
					result.setPUDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUDATE)));
					result.setDLDate(cursor.getString(cursor.getColumnIndex(COLUMN_DLDATE)));
					result.setBatchDetails(cursor.getString(cursor.getColumnIndex(COLUMN_BATCH_DETAILS)));
					result.setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)));
					result.setSignature(cursor.getBlob(cursor.getColumnIndex(COLUMN_SIGNATURE)));	
					
					dispatchList.add(result);
					
				} while (cursor.moveToNext());
			}
			
		}
		finally
		{
			if (cursor != null) {
				
				cursor.close();
			}
		}
		
		
		return dispatchList; 
	}
	
	
	
	public void updatedDone(String batchids){
		
		try
		{
			String sql = "UPDATE RnaOffilneData SET action = 'none' WHERE batchid in (" + batchids + ")";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();				
			mDatabase.execSQL(sql);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
	}
	
	
	public void updatePickup(String batchId, String PUDate, String action){
		
		try
		{
			String sql = "UPDATE RnaOffilneData SET PUDate = '" + PUDate + "', action = '" + action + "', status = 'Picked-Up' WHERE batchid = '" + batchId + "'";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();				
			mDatabase.execSQL(sql);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
	}
	
	
	public void updateDelivery(String batchId, String rnaData, String rnaHomeCode){
		
		try
		{
			String sql = "UPDATE RnaOffilneData SET rnadata = '" + rnaData + "', rnahomecode = '" + rnaHomeCode + "' WHERE batchid = '" + batchId + "'";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();				
			mDatabase.execSQL(sql);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
	}
	
	
	
	public void updateRNADelivery(String batchId, Hashtable params, byte[] signature)
	{
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase(); 
		ContentValues values =  new ContentValues();
		
		Enumeration keys = params.keys();
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			String value = (String)params.get(key);
			values.put(key, value);
			
			
		}
		
		values.put("action", "update");
		values.put(COLUMN_SIGNATURE, signature);
		db.update(TABLE_NAME, values, COLUMN_BATCHID + "='" + batchId+ "'", null);
		
	}
	
}





