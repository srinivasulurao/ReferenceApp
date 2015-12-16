package com.youflik.youflik.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.youflik.youflik.chat.StickersModel;
import com.youflik.youflik.models.ConversationsMessagesModel;
import com.youflik.youflik.models.ConversationsModel;


public class DataBaseHandler extends SQLiteOpenHelper{

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "youflik.db";

	// Labels table name
	private static final String TABLE_CONVERSATIONS = "conversation";

	// Labels Conversations-Table Columns Names
	private static final String CONVERSATION_ID = "conversation_id";
	private static final String LOGIN_USER_ID = "login_user_id";
	private static final String LOGIN_USER_JID = "login_user_jid";
	private static final String LOGIN_USER_RESOURCE = "login_user_resource";
	private static final String LOGIN_USER_DISPLAY_NAME = "login_user_display_name";
	private static final String WITH_USER_ID = "with_user_id";
	private static final String WITH_USER_JID = "with_user_jid";
	private static final String WITH_USER_RESOURCE = "with_user_resource";
	private static final String WITH_USER_DISPLAY_NAME= "with_user_display_name";
	private static final String WITH_USER_PROFILEPICURL= "with_user_profilepicurl";
	private static final String START_TIME = "start_time";
	private static final String END_TIME = "end_time";
	private static final String LAST_MESSAGE = "last_message";
	private static final String LAST_MESSAGE_DIRECTION  ="last_message_direction";
	private static final String MESSAGE_ISSEEN = "message_iseen";
	private static final String MESSAGE_ISSEEN_COUNT = "message_isseen_count";

	// Labels table name
	private static final String TABLE_CONVERSATIONS_MESSAGES = "convmessage";
	// Labels ConversationsMessages-Table Columns Names
	private static final String CONVMESSAGE_ID = "convmessage_id";
	private static final String CONVMESSAGE_TIME = "convmessage_time";
	private static final String CONVMESSAGE_DIRECTION = "convmessage_direction";
	private static final String CONVMESSAGE_TYPE = "convmessage_type";
	private static final String CONVMESSAGE_MESSAGE = "convmessage_message";
	// conversation_id default for both tables

	// Labels table Stickers Check 
	private static final String TABLE_STICKERS_CHECK = "check_stickers";

	private static final String STICKERS_CHECK_ID = "check_id";
	private static final String STICKERS_PRESENCE = "presence";
	private static final String STICKERS_DATE = "last_date";

	// Labels table Stickers

	private static final String TABLE_STICKERS = "stickers";

	private static final String STICKERS_ID = "stickers_id";
	private static final String STICKERS_PATH = "stickers_path";


	public DataBaseHandler(Context context)  {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String CREATE_CONVERSATION_TABLE = "CREATE TABLE " + TABLE_CONVERSATIONS + "("
				+ CONVERSATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LOGIN_USER_ID + " TEXT,"
				+ LOGIN_USER_JID + " TEXT," 
				+ LOGIN_USER_RESOURCE + " TEXT,"
				+ LOGIN_USER_DISPLAY_NAME + " TEXT,"
				+ WITH_USER_ID + " TEXT,"
				+ WITH_USER_JID + " TEXT,"
				+ WITH_USER_RESOURCE + " TEXT,"
				+ WITH_USER_DISPLAY_NAME + " TEXT,"
				+ WITH_USER_PROFILEPICURL + " TEXT,"
				+ START_TIME + " TEXT,"
				+ END_TIME + " TEXT,"
				+ LAST_MESSAGE + " TEXT,"
				+ LAST_MESSAGE_DIRECTION + " TEXT,"
				+ MESSAGE_ISSEEN + " TEXT,"
				+ MESSAGE_ISSEEN_COUNT + " TEXT"+")";


		String CREATE_CONVMESSAGE_TABLE = "CREATE TABLE " + TABLE_CONVERSATIONS_MESSAGES + "("
				+ CONVMESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CONVMESSAGE_TIME + " TEXT,"
				+ CONVMESSAGE_DIRECTION + " TEXT," 
				+ CONVMESSAGE_TYPE + " TEXT,"
				+ CONVMESSAGE_MESSAGE + " TEXT,"
				+ CONVERSATION_ID + " TEXT"+")";


		String CREATE_STICKERS_CHECK_TABLE = "CREATE TABLE " + TABLE_STICKERS_CHECK + "("
				+ STICKERS_CHECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ STICKERS_PRESENCE + " TEXT,"
				+ STICKERS_DATE + " TEXT"+")";

		String CREATE_STICKERS_TABLE = "CREATE TABLE " + TABLE_STICKERS + "("
				+ STICKERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ STICKERS_PATH + " TEXT"+")";

		System.out.println(CREATE_CONVERSATION_TABLE);
		System.out.println(CREATE_CONVMESSAGE_TABLE);

		db.execSQL(CREATE_CONVERSATION_TABLE);
		db.execSQL(CREATE_CONVMESSAGE_TABLE);
		db.execSQL(CREATE_STICKERS_CHECK_TABLE);
		db.execSQL(CREATE_STICKERS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATIONS_MESSAGES);
		// Create tables again
		onCreate(db);
	}

	public ArrayList<ConversationsMessagesModel> getConversionMessages(String ConversionID){

		ArrayList<ConversationsMessagesModel> chatmessagesList = new ArrayList<ConversationsMessagesModel>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CONVERSATIONS_MESSAGES + " WHERE " +CONVERSATION_ID +" = " + "'"+ ConversionID +"'";
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor1 = null;

		cursor1 = db.rawQuery(selectQuery, null);

		System.out.println(selectQuery);
		// looping through all rows and adding to list
		if(cursor1!=null){
			for (cursor1.moveToFirst(); !cursor1.isAfterLast(); cursor1.moveToNext()) {
				ConversationsMessagesModel chatmessages = new ConversationsMessagesModel();
				chatmessages.setConvmessage_id(cursor1.getInt(0));
				chatmessages.setConvmessage_time(cursor1.getString(1));
				chatmessages.setConvmessage_direction(cursor1.getString(2));
				chatmessages.setConvmessage_type(cursor1.getString(3));
				chatmessages.setConvmessage_message(cursor1.getString(4));
				chatmessages.setConversation_id(cursor1.getInt(5));
				chatmessagesList.add(chatmessages);
			} 
		}else{
			return null;
		}
		cursor1.close();
		db.close();
		// returning Messages
		return chatmessagesList;
	}

	public ArrayList<ConversationsModel> getConversions(){
		ArrayList<ConversationsModel> chatConversionsList = new ArrayList<ConversationsModel>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CONVERSATIONS +" order  by datetime(" +END_TIME+") DESC" ;
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		System.out.println(selectQuery);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ConversationsModel conversions = new ConversationsModel();
				conversions.setConversation_id(cursor.getInt(0));
				conversions.setLogin_user_id(cursor.getInt(1));
				conversions.setLogin_user_jid(cursor.getString(2));
				conversions.setLogin_user_resource(cursor.getString(3));
				conversions.setLogin_user_display_name(cursor.getString(4));
				conversions.setWith_user_id(cursor.getInt(5));
				conversions.setWith_user_jid(cursor.getString(6));
				conversions.setWith_user_resource(cursor.getString(7));
				conversions.setWith_user_display_name(cursor.getString(8));
				conversions.setWith_user_profilepicurl(cursor.getString(9));
				conversions.setStart_time(cursor.getString(10));
				conversions.setEnd_time(cursor.getString(11));
				conversions.setLast_message(cursor.getString(12));
				conversions.setLast_message_direction(cursor.getString(13));
				conversions.setMessage_iseen(cursor.getString(14));
				conversions.setMessage_isseen_count(cursor.getString(15));

				/*conversions.setConversation_id(cursor.getColumnIndex(CONVERSATION_ID));
				conversions.setLogin_user_id(cursor.getColumnIndex(LOGIN_USER_ID));
				conversions.setLogin_user_jid(cursor.getColumnIndex(LOGIN_USER_JID));
				conversions.setLogin_user_resource(cursor.getColumnIndex(LOGIN_USER_RESOURCE));
				conversions.setLogin_user_display_name(cursor.getColumnIndex(LOGIN_USER_DISPLAY_NAME));
				conversions.setWith_user_id(cursor.getColumnIndex(WITH_USER_ID));
				conversions.setWith_user_jid(cursor.getColumnName(columnIndex)(WITH_USER_JID));
				conversions.setWith_user_resource(cursor.getColumnIndex(WITH_USER_RESOURCE));
				conversions.setWith_user_display_name(cursor.getColumnIndex(WITH_USER_DISPLAY_NAME));
				conversions.setWith_user_profilepicurl(cursor.getColumnIndex(WITH_USER_PROFILEPICURL));
				conversions.setStart_time(cursor.getColumnIndex(START_TIME));
				conversions.setEnd_time(cursor.getColumnIndex(END_TIME));
				conversions.setLast_message(cursor.getColumnIndex(LAST_MESSAGE));
				conversions.setLast_message_direction(cursor.getColumnIndex(LAST_MESSAGE_DIRECTION));*/
				chatConversionsList.add(conversions);
			} while (cursor.moveToNext());
		}
		// closing connection
		cursor.close();
		db.close();

		// returning Messages
		return chatConversionsList;
	}


	/**
	 * Inserting new Message into convmessage table
	 * */
	public void insertConversionsMessages(ConversationsMessagesModel messages){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(CONVMESSAGE_ID, messages.getConvmessage_id());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(new Date());
		System.out.println(strDate);
		values.put(CONVMESSAGE_TIME, strDate);
		values.put(CONVMESSAGE_DIRECTION, messages.getConvmessage_direction());
		values.put(CONVMESSAGE_TYPE, messages.getConvmessage_type());
		values.put(CONVMESSAGE_MESSAGE, messages.getConvmessage_message());
		values.put(CONVERSATION_ID,messages.getConversation_id());
		// Inserting Row
		db.insert(TABLE_CONVERSATIONS_MESSAGES, null, values);
		db.close(); // Closing database connection
	}


	/**
	 * Inserting new Message into convmessage table
	 * */
	public void insertConversions(ConversationsModel conversions){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String StartDate = sdf.format(new Date());
		String endDate = sdf.format(new Date());
		//values.put(CONVERSATION_ID, conversions.getConversation_id());
		values.put(LOGIN_USER_ID, conversions.getLogin_user_id());
		values.put(LOGIN_USER_JID, conversions.getLogin_user_jid());
		values.put(LOGIN_USER_RESOURCE, conversions.getLogin_user_resource());
		values.put(LOGIN_USER_DISPLAY_NAME, conversions.getLogin_user_display_name());
		values.put(WITH_USER_ID, conversions.getWith_user_id());
		values.put(WITH_USER_JID, conversions.getWith_user_jid());
		values.put(WITH_USER_RESOURCE, conversions.getWith_user_resource());
		values.put(WITH_USER_DISPLAY_NAME, conversions.getWith_user_display_name());
		values.put(WITH_USER_PROFILEPICURL, conversions.getWith_user_profilepicurl());
		values.put(START_TIME, StartDate);
		values.put(END_TIME, endDate);
		values.put(LAST_MESSAGE, conversions.getLast_message());
		values.put(LAST_MESSAGE_DIRECTION, conversions.getLast_message_direction());
		values.put(MESSAGE_ISSEEN, conversions.getMessage_iseen());
		values.put(MESSAGE_ISSEEN_COUNT, conversions.getMessage_isseen_count());

		// Inserting Row
		db.insert(TABLE_CONVERSATIONS, null, values);
		db.close(); // Closing database connection
	}

	public ConversationsModel checkConversationID(String convJID){
		ConversationsModel conversions = new ConversationsModel();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CONVERSATIONS + " WHERE " +WITH_USER_JID +" = " + "'"+ convJID +"'";
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if(cursor != null && cursor.getCount() > 0){
			if (cursor.moveToFirst()) {
				do {
					conversions.setConversation_id(cursor.getInt(0));
					conversions.setLogin_user_id(cursor.getInt(1));
					conversions.setLogin_user_jid(cursor.getString(2));
					conversions.setLogin_user_resource(cursor.getString(3));
					conversions.setLogin_user_display_name(cursor.getString(4));
					conversions.setWith_user_id(cursor.getInt(5));
					conversions.setWith_user_jid(cursor.getString(6));
					conversions.setWith_user_resource(cursor.getString(7));
					conversions.setWith_user_display_name(cursor.getString(8));
					conversions.setWith_user_profilepicurl(cursor.getString(9));
					conversions.setStart_time(cursor.getString(10));
					conversions.setEnd_time(cursor.getString(11));
					conversions.setLast_message(cursor.getString(12));
					conversions.setLast_message_direction(cursor.getString(13));
					conversions.setMessage_iseen(cursor.getString(14));
					conversions.setMessage_isseen_count(cursor.getString(15));
				} while (cursor.moveToNext());
			}
			// closing connection
			cursor.close();
			db.close();
		}else{
			return null;
		}
		return conversions;
	}

	public void updateImageUrlinConverse(String ConversionJID,String ImageUrl,String withUserName,String withUserID){

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(WITH_USER_PROFILEPICURL, ImageUrl);
		values.put(WITH_USER_DISPLAY_NAME, withUserName);
		values.put(WITH_USER_ID, withUserID);
		db.update(TABLE_CONVERSATIONS, values, WITH_USER_JID + "=?", new String[] {ConversionJID});
		db.close();

	}

	public void updateLastMessageinConversations(String conversationJID,String Message,String LastTime,String Direction,String messageisseen,String messageiscount){

		SQLiteDatabase db = this.getWritableDatabase();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(new Date());
		ContentValues values = new ContentValues();
		values.put(LAST_MESSAGE, Message);
		values.put(END_TIME, strDate);
		values.put(LAST_MESSAGE_DIRECTION, Direction);
		values.put(MESSAGE_ISSEEN, messageisseen);
		values.put(MESSAGE_ISSEEN_COUNT, messageiscount);
		db.update(TABLE_CONVERSATIONS, values, CONVERSATION_ID + "=?", new String[] {conversationJID});
		System.out.println("Updated");
		db.close();

	}



	public void changeIsseen(String conversationJID,String isSeenText,String isseenCount){

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MESSAGE_ISSEEN, isSeenText);
		values.put(MESSAGE_ISSEEN_COUNT, isseenCount);
		db.update(TABLE_CONVERSATIONS, values, CONVERSATION_ID + "=?", new String[] {conversationJID});
		db.close();
	}



	public ConversationsMessagesModel getConversionMessagesUpdating(String ConversionID, String lastDate){

		ConversationsMessagesModel chatmessages = new ConversationsMessagesModel();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_CONVERSATIONS_MESSAGES + " WHERE " +CONVERSATION_ID +" = " + "'"+ ConversionID +"'" + " AND " + CONVMESSAGE_TIME +" > " + "'"+ lastDate +"'";
		System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		System.out.println(selectQuery);
		// looping through all rows and adding to list
		if(cursor != null && cursor.getCount() > 0){
			if (cursor.moveToFirst()) {
				do {
					chatmessages.setConvmessage_id(cursor.getInt(0));
					chatmessages.setConvmessage_time(cursor.getString(1));
					chatmessages.setConvmessage_direction(cursor.getString(2));
					chatmessages.setConvmessage_type(cursor.getString(3));
					chatmessages.setConvmessage_message(cursor.getString(4));
					chatmessages.setConversation_id(cursor.getInt(5));
				} while (cursor.moveToNext());
			}
			// closing connection
			cursor.close();
			db.close();
		}else{
			return null;
		}

		// returning Messages
		return chatmessages;
	}


	public int clearConversations(String ConversationsID){
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TABLE_CONVERSATIONS_MESSAGES, CONVERSATION_ID+"="+ConversationsID, null);
	}

	public int deleteConversations(String ConversationsID){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONVERSATIONS, CONVERSATION_ID+"="+ConversationsID, null);
		return db.delete(TABLE_CONVERSATIONS_MESSAGES, CONVERSATION_ID+"="+ConversationsID, null);
	}

	public void AddStickers(ArrayList<StickersModel> stickers){

		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("delete from "+ TABLE_STICKERS);
		db.execSQL("delete from "+ TABLE_STICKERS_CHECK);

		ContentValues values = new ContentValues();

		for(StickersModel sticker : stickers){
			values.put(STICKERS_PATH, sticker.getStickerUrl());
			// Inserting Row

			db.insert(TABLE_STICKERS, null, values);
			System.out.println(TABLE_STICKERS);
		}

		//values.put(CONVMESSAGE_ID, messages.getConvmessage_id());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = sdf.format(new Date());
		System.out.println(strDate);

		ContentValues valuesforCheckSticker = new ContentValues();
		valuesforCheckSticker.put(STICKERS_PRESENCE, "YES");
		valuesforCheckSticker.put(STICKERS_DATE, strDate);
		db.insert(TABLE_STICKERS_CHECK, null, valuesforCheckSticker);
		System.out.println(TABLE_STICKERS_CHECK);
		db.close(); // Closing database connection

	}

	public String checkStickersDate(){

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_STICKERS_CHECK ;
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if(cursor != null && cursor.getCount() > 0){

			if (cursor.moveToFirst()) {
				//String lastdate = cursor.getString(2);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
				String currentDate = sdf.format(new Date());

				try {
					Date dbDate = sdfDay.parse(cursor.getString(2));
					Date presentDate = sdfDay.parse(currentDate);

					if (dbDate.equals(presentDate)){
						System.out.println("dbDate.equals(presentDate)");
						cursor.close();
						db.close();
						return "1";
					}else {
						System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!dbDate.equals(presentDate)");
						cursor.close();
						db.close();
						return "2";
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		cursor.close();
		db.close();
		return "0";
	}

	public ArrayList<StickersModel> getStickers(){
		ArrayList<StickersModel> stickersList = new ArrayList<StickersModel>();

		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_STICKERS ;
		//System.out.println(selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		System.out.println(selectQuery);
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				StickersModel Sticker = new StickersModel();
				Sticker.setStickerUrl(cursor.getString(1));
				stickersList.add(Sticker);
			} while (cursor.moveToNext());
		}
		// closing connection
		cursor.close();
		db.close();
		// returning Stickers
		return stickersList;
	}

}
