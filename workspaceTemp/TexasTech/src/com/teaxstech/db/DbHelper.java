package com.teaxstech.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;

import com.texastech.bean.Reminder;

public class DbHelper {

	public static final String TBL_REMINDER = "Reminder";
	
	public static final String TBL_TODO = "ToDo";
	
	private Context context;

	private SqliteOpenHelper helper = null;

	public DbHelper(Context context) throws Exception {
		this.context = context;
		helper = new SqliteOpenHelper(context);
		helper.createDataBase();
	}
	
	
	///////////////////////////////////////////////////////
	public List<Reminder> getReminderList(String tblName) {
		List<Reminder> reminders = new ArrayList<Reminder>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = helper.getWritableDatabase();
			cursor = db.rawQuery("SELECT rowid,* FROM "+tblName, null);
			if (cursor.moveToFirst()) {
				do {
					Reminder reminder = new Reminder();
					reminder.setId(cursor.getInt(0));
					reminder.setName(cursor.getString(1));
					reminder.setReminderType(cursor.getString(2));
					reminder.setReminderDateTime(cursor.getString(3));
					reminder.setReminderTime(cursor.getString(4));
					reminders.add(reminder);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
			if (cursor != null)
				cursor.close();
		} 
		return reminders;
	}
	
	
	public void deleteReminderRow(String tblName,int rowId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete(tblName, "rowid" + " = ?",new String[] { String.valueOf(rowId) });
		db.close();
	}
	
	
	public int saveReminder(String tblName,Reminder reminder) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		int rowID=-1;
		try {
			db = helper.getWritableDatabase();
			InsertHelper helper = new InsertHelper(db, tblName);

			ContentValues value = new ContentValues();
			value.put("Name", 		reminder.getName());
			value.put("Type", 		reminder.getReminderType());
			value.put("DateTime", 	reminder.getReminderDateTime());
			value.put("Time", 		reminder.getReminderTime());
			rowID = (int)helper.insert(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null)
				db.close();
			if (cursor != null)
				cursor.close();
		}
		return rowID;
	}
	
	 
	
	
	
	public Reminder getReminderDetail(String tblName,int rowId) {
		SQLiteDatabase db = null;
		Reminder detail=null;
		db = helper.getWritableDatabase();
		Cursor mCursor = db.rawQuery("SELECT * FROM "+tblName+" WHERE rowid=?", new String[] { String.valueOf(rowId) });
		try {
			if(mCursor.moveToFirst()){
				detail = new Reminder();
				detail.setName( mCursor.getString(mCursor.getColumnIndex("Name")));
				detail.setReminderTime(mCursor.getString(mCursor.getColumnIndex("Time")));
				detail.setReminderType(mCursor.getString(mCursor.getColumnIndex("Type"))); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (db != null)
				db.close();
			
			if(mCursor!=null){
				mCursor.close();
			}
		}
		return detail;
	}
}
