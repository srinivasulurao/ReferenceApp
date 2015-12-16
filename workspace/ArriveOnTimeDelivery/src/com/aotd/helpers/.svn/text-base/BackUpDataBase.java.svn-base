package com.aotd.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aotd.model.SignatureModel;

public class BackUpDataBase {
	
	DataBaseHelper mDatabase_helper;
	SQLiteDatabase mDatabase;
	
	public BackUpDataBase(Context context){
		
		mDatabase_helper = DataBaseHelper.getDBAdapterInstance(context);
	}
	
	
	public void checkData(){
		
		Cursor cursor = null;
		
		try{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery("SELECT * FROM DataBackUp", null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				
				do {
					
					Log.e("","BackUp Data :: "
							+" Order_Id "+cursor.getString(cursor.getColumnIndex("order_id"))
							+" First_Name "+cursor.getString(cursor.getColumnIndex("firstname"))
							+" Type "+cursor.getString(cursor.getColumnIndex("type")));

				} while (cursor.moveToNext());
			}
			
		}finally{
			
			if (cursor != null) {
				
				cursor.close();
			}
		}
	}
	
	
	
	public void deleteData(String order_id){
		
		try
		{
			String sql = "delete from DataBackUp where order_id = '" + order_id+"'";
			Log.e("", sql);
			mDatabase = mDatabase_helper.getWritableDatabase();				
			mDatabase.execSQL(sql);
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}finally{}
	}
	
	
	public void insertData(String order_id, byte[] signature,String firstname, String type){
	
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("order_id", order_id);
		values.put("signature", signature);
		values.put("firstname", firstname);
		values.put("type", type);
		db.insert("DataBackUp", null, values);
		db.close(); 
		
	}
	
	
	public void insertSecondSignData(String order_id, byte[] signature,String firstname, String type){
		
		SQLiteDatabase db = mDatabase_helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("order_id", order_id);
		values.put("signature", signature);
		values.put("signaturedl", signature);
		values.put("firstname", firstname);
		values.put("type", type);
		db.insert("DataBackUp", null, values);
		db.close(); 
		
	}
	
	
	
	public SignatureModel getData(String order_id){
		
		Cursor cursor = null;
		SignatureModel _SignatureModel = null;
		
		try{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery("SELECT * FROM DataBackUp where order_id = '" + order_id+"'" , null);
			
			if (cursor.getCount() > 0) {
				
				_SignatureModel = new SignatureModel();

				cursor.moveToFirst();
				
				do {
					
					_SignatureModel.setBytes(cursor.getBlob(cursor.getColumnIndex("signature")));
					_SignatureModel.setLastname(cursor.getString(cursor.getColumnIndex("firstname")));
					
				} while (cursor.moveToNext());
			}
			
		}finally{
			
			if (cursor != null) {
				
				cursor.close();
			}
		}
		
		return _SignatureModel;
	}
	
	
	public SignatureModel getSecondSignData(String order_id){
		
		Cursor cursor = null;
		SignatureModel _SignatureModel = null;
		
		try{
			
			mDatabase = mDatabase_helper.getWritableDatabase();
			cursor  = mDatabase.rawQuery("SELECT * FROM DataBackUp where order_id = '" + order_id+"'" , null);
			
			if (cursor.getCount() > 0) {
				
				_SignatureModel = new SignatureModel();

				cursor.moveToFirst();
				
				do {
					
					_SignatureModel.setBytes(cursor.getBlob(cursor.getColumnIndex("signaturedl")));
					_SignatureModel.setLastname(cursor.getString(cursor.getColumnIndex("firstname")));
					
				} while (cursor.moveToNext());
			}
			
		}finally{
			
			if (cursor != null) {
				
				cursor.close();
			}
		}
		
		return _SignatureModel;
	}
}