package com.aotd.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aotd.model.DetailDeliveryModel;
import com.aotd.model.DispatchAllListModel;
import com.aotd.model.OfflineModel;
import com.aotd.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfflineDB extends SQLiteOpenHelper {

	Context mContext;
	
	private static final int DATABASE_VERSION = 7;
	private static final String DATABASE_NAME = "offlinedata";


	private static final String TABLE_NAME = "pending";
	
	
	
	private static final String KEY_ID = "id";
	private static final String KEY_UN= "username";
	private static final String KEY_PW= "password";
	private static final String KEY_ORDERID= "orderid";
	private static final String KEY_ACCOUNT_NAME= "account_name";
	private static final String KEY_ACCOUNT_NOTES= "account_notes";

	private static final String KEY_FORM_COMPANY= "from_company";
	private static final String KEY_FORM_ADDRESS= "from_address";
	private static final String KEY_FORM_SUITE= "from_Suite";
	private static final String KEY_FORM_CITY= "from_City";
	private static final String KEY_FORM_STATE= "from_State";
	private static final String KEY_FORM_ZIP= "from_Zip";
	private static final String KEY_FORM_CELLPHONE= "from_CELLPHONE";
	private static final String KEY_FORM_HOMEPHONE= "from_HOMEPHONE";
	private static final String KEY_FROM_INSTRUCTIONS= "from_instructions";

	private static final String KEY_TO_COMPANY= "to_company";
	private static final String KEY_TO_ADDRESS= "to_address";
	private static final String KEY_TO_SUITE= "to_Suite";
	private static final String KEY_TO_CITY= "to_City";
	private static final String KEY_TO_STATE= "to_State";
	private static final String KEY_TO_ZIP= "to_Zip";
	private static final String KEY_TO_CELLPHONE= "to_CELLPHONE";
	private static final String KEY_TO_HOMEPHONE= "to_HOMEPHONE";
	private static final String KEY_TO_INSTRUCTIONS= "to_instructions";

	private static final String KEY_ODERSTATUS= "status";
	private static final String KEY_ROUNDTRIP= "roundtrip";
	private static final String KEY_ORDERDETAILS= "orderdetails";
	private static final String KEY_SERVICETYPE= "servicetype";
	private static final String KEY_PICKUP_READY= "pickupready";
	private static final String KEY_PIECE= "piece";
	private static final String KEY_WEIGHT= "weight";
	private static final String KEY_ADMIN_NOTES= "adminnotes";
	
	private static final String KEY_LAST_NAME= "lastname";
	private static final String KEY_NOTES= "notes";
	private static final String KEY_URL = "url";
	private static final String KEY_FILENAME = "filename";
	private static final String KEY_IMGSTR= "imgstr";
	private static final String KEY_YES_NO= "yes_no";
	private static final String KEY_DATE_TIME = "datetime";
	private static final String KEY_TAG = "tag";




	public OfflineDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_ORDERID + " TEXT,"
				+ KEY_ACCOUNT_NAME + " TEXT,"
				+ KEY_ACCOUNT_NOTES + " TEXT,"

				+ KEY_FORM_COMPANY + " TEXT,"
				+ KEY_FORM_ADDRESS + " TEXT,"
				+ KEY_FORM_SUITE + " TEXT,"
				+ KEY_FORM_CITY + " TEXT,"
				+ KEY_FORM_STATE + " TEXT,"
				+ KEY_FORM_ZIP + " TEXT,"
				+ KEY_FORM_CELLPHONE + " TEXT,"
				+ KEY_FORM_HOMEPHONE+ " TEXT,"
				+ KEY_FROM_INSTRUCTIONS + " TEXT,"
				
				+ KEY_TO_COMPANY + " TEXT,"
				+ KEY_TO_ADDRESS + " TEXT,"
				+ KEY_TO_SUITE + " TEXT,"
				+ KEY_TO_CITY + " TEXT,"
				+ KEY_TO_STATE + " TEXT,"
				+ KEY_TO_ZIP + " TEXT,"
				+ KEY_TO_CELLPHONE + " TEXT,"
				+ KEY_TO_HOMEPHONE+ " TEXT,"
				+ KEY_TO_INSTRUCTIONS + " TEXT,"
				
				+ KEY_ODERSTATUS + " TEXT,"
				+ KEY_ROUNDTRIP + " TEXT,"
				+ KEY_ORDERDETAILS + " TEXT,"
				+ KEY_SERVICETYPE + " TEXT,"
				+ KEY_PICKUP_READY + " TEXT,"
				+ KEY_PIECE + " TEXT,"
				+ KEY_WEIGHT + " TEXT,"
				+ KEY_UN + " TEXT,"
				+ KEY_PW + " TEXT,"
				
				+ KEY_ADMIN_NOTES + " TEXT,"
				
				+ KEY_LAST_NAME + " TEXT,"
				+ KEY_NOTES + " TEXT,"
				+ KEY_URL + " TEXT,"
				+ KEY_FILENAME + " TEXT,"
				+ KEY_IMGSTR + " TEXT,"
				+ KEY_YES_NO + " TEXT,"
				+ KEY_DATE_TIME + " TEXT,"
				+ KEY_TAG + " TEXT"	+ ")";	
				
				
		
				
		
		
		
		
		//+ KEY_ADMIN_NOTES + " TEXT" + ")";
		
		
		
		db.execSQL(CREATE_TABLE);
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
		
	}
	
	public void addData(String status,DispatchAllListModel al)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		SharedPreferences loginprefs1;
		loginprefs1	= mContext.getSharedPreferences("loginprefs", 0);	
		String prefUserid = loginprefs1.getString("username","");
		String userRole = loginprefs1.getString("role","");
		String password = loginprefs1.getString("password","");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		values.put(KEY_ORDERID, al.getOrder_id());
		values.put(KEY_ACCOUNT_NAME,al.getAccountName());
		values.put(KEY_ACCOUNT_NOTES,al.getAccountnotes());
		
		values.put(KEY_FORM_COMPANY,al.getCompany());
		values.put(KEY_FORM_ADDRESS,al.getAddress());
		values.put(KEY_FORM_SUITE,al.getSuit());
		values.put(KEY_FORM_CITY,al.getCity());
		values.put(KEY_FORM_STATE,al.getState());
		values.put(KEY_FORM_ZIP,al.getZip());
		values.put(KEY_FORM_CELLPHONE,al.getPuCellPhone());
		values.put(KEY_FORM_HOMEPHONE,al.getPuHomephone());
		values.put(KEY_FROM_INSTRUCTIONS,"");
		
		values.put(KEY_TO_COMPANY,al.getDlcompany());
		values.put(KEY_TO_ADDRESS,al.getDladdress());
		values.put(KEY_TO_SUITE,al.getDlsuit());
		values.put(KEY_TO_CITY,al.getDlcity());
		values.put(KEY_TO_STATE,al.getDlstate());
		values.put(KEY_TO_ZIP,al.getDlzip());
		values.put(KEY_TO_CELLPHONE,al.getDlCellPhone());
		values.put(KEY_TO_HOMEPHONE,al.getDlHomePhone());
		values.put(KEY_TO_INSTRUCTIONS,"");
		
		values.put(KEY_ODERSTATUS, status);
		values.put(KEY_ROUNDTRIP,al.getIsRoundTrip());
		values.put(KEY_ORDERDETAILS,"");
		values.put(KEY_SERVICETYPE,al.getHour());
		values.put(KEY_PICKUP_READY,al.getRDDate());
		values.put(KEY_PIECE,al.getPeice());
		values.put(KEY_WEIGHT,al.getWeight());
		values.put(KEY_UN,prefUserid);
		values.put(KEY_PW,password);
		values.put(KEY_ADMIN_NOTES,"");
		
		values.put(KEY_LAST_NAME,"");
		values.put(KEY_NOTES,"");
		values.put(KEY_URL,"");
		values.put(KEY_FILENAME,"");
		values.put(KEY_IMGSTR,"");
		values.put(KEY_YES_NO,"");
		values.put(KEY_DATE_TIME,currentDateandTime);
		values.put(KEY_TAG,"");
		
		

		db.insert(TABLE_NAME, null, values);
		db.close();
	}
	
	
	public void addDataDeliver(String status,DispatchAllListModel al, JSONObject ja)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		SharedPreferences loginprefs1;
		loginprefs1	= mContext.getSharedPreferences("loginprefs", 0);	
		String prefUserid = loginprefs1.getString("username","");
		String userRole = loginprefs1.getString("role","");
		String password = loginprefs1.getString("password","");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		
		values.put(KEY_ORDERID, al.getOrder_id());
		values.put(KEY_ACCOUNT_NAME,al.getAccountName());
		values.put(KEY_ACCOUNT_NOTES,al.getAccountnotes());
		
		values.put(KEY_FORM_COMPANY,al.getCompany());
		values.put(KEY_FORM_ADDRESS,al.getAddress());
		values.put(KEY_FORM_SUITE,al.getSuit());
		values.put(KEY_FORM_CITY,al.getCity());
		values.put(KEY_FORM_STATE,al.getState());
		values.put(KEY_FORM_ZIP,al.getZip());
		values.put(KEY_FORM_CELLPHONE,al.getPuCellPhone());
		values.put(KEY_FORM_HOMEPHONE,al.getPuHomephone());
		
		/*values.put(KEY_FROM_INSTRUCTIONS,"");
		values.put(KEY_TO_INSTRUCTIONS,"");
		values.put(KEY_ROUNDTRIP,al.getIsRoundTrip());
		values.put(KEY_ORDERDETAILS,"");
		values.put(KEY_SERVICETYPE,al.getHour());
		values.put(KEY_PICKUP_READY,al.getRDDate());
		values.put(KEY_PIECE,"");
		values.put(KEY_WEIGHT,"");*/
		
		
		/*try{
		values.put(KEY_FROM_INSTRUCTIONS,ja.getString("LASTNAME"));
		values.put(KEY_TO_INSTRUCTIONS,ja.getString("NOTES"));
		values.put(KEY_ROUNDTRIP,ja.getString("URL"));
		values.put(KEY_ORDERDETAILS,"");
		values.put(KEY_SERVICETYPE,ja.getString("FILENAME"));
		values.put(KEY_PICKUP_READY,al.getRDDate());
		values.put(KEY_PIECE,ja.getString("IMGSTR"));
		values.put(KEY_WEIGHT,ja.getString("YES_NO"));
		
		}catch(Exception e){}*/
		

		try{
		values.put(KEY_LAST_NAME,ja.getString("LASTNAME"));
		values.put(KEY_NOTES,ja.getString("NOTES"));
		values.put(KEY_URL,ja.getString("URL"));
		values.put(KEY_ORDERDETAILS,"");
		values.put(KEY_FILENAME,ja.getString("FILENAME"));
		values.put(KEY_PICKUP_READY,al.getRDDate());
		values.put(KEY_IMGSTR,ja.getString("IMGSTR"));
		values.put(KEY_YES_NO,ja.getString("YES_NO"));
		values.put(KEY_DATE_TIME, currentDateandTime);
		values.put(KEY_TAG,"");
		
		
		}catch(Exception e){}
		
		
		
		values.put(KEY_TO_COMPANY,al.getDlcompany());
		values.put(KEY_TO_ADDRESS,al.getDladdress());
		values.put(KEY_TO_SUITE,al.getDlsuit());
		values.put(KEY_TO_CITY,al.getDlcity());
		values.put(KEY_TO_STATE,al.getDlstate());
		values.put(KEY_TO_ZIP,al.getDlzip());
		values.put(KEY_TO_CELLPHONE,al.getDlCellPhone());
		values.put(KEY_TO_HOMEPHONE,al.getDlHomePhone());
		
		
		values.put(KEY_ODERSTATUS, status);
		
		values.put(KEY_UN,prefUserid);
		values.put(KEY_PW,password);
		values.put(KEY_ADMIN_NOTES,"");
		
		
		
		

		db.insert(TABLE_NAME, null, values);
		db.close();
	}
	
	
	
	/*public ArrayList<OfflineModel> getAllContacts() 
	{
		ArrayList<OfflineModel> contactList = new ArrayList<OfflineModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				
				
				
				OfflineModel contact = new OfflineModel();
				
				contact.setId(cursor.getString(0));
				contact.setDLordersID((cursor.getString(1)));
				contact.setAccountName((cursor.getString(2)));
				contact.setAccountnotes((cursor.getString(3)));
				
				contact.setPUcompany((cursor.getString(4)));
				contact.setPUaddress((cursor.getString(5)));
				contact.setPUsuit((cursor.getString(6)));
				contact.setPUcity((cursor.getString(7)));
				contact.setPUstate((cursor.getString(8)));
				contact.setPUzip((cursor.getString(9)));
				contact.setPUcellPhone((cursor.getString(10)));
				contact.setPUhomephone((cursor.getString(11)));
				contact.setPUInstruction((cursor.getString(12)));
				
				contact.setDLcompany((cursor.getString(13)));
				contact.setDLaddress((cursor.getString(14)));
				contact.setDLsuit((cursor.getString(15)));
				contact.setDLcity((cursor.getString(16)));
				contact.setDLstate((cursor.getString(17)));
				contact.setDLzip((cursor.getString(18)));
				contact.setDLcellPhone((cursor.getString(19)));
				contact.setDLhomephone((cursor.getString(20)));
				contact.setDLInstruction((cursor.getString(21)));
				
				
				contact.setOrder_status((cursor.getString(22)));
				contact.setRoundTrip((cursor.getString(23)));
				contact.setServiceName((cursor.getString(25)));
				contact.setRDDate((cursor.getString(26)));
				contact.setPiece((cursor.getString(27)));
				contact.setWeight((cursor.getString(28)));
				contact.setUsername(cursor.getString(29)); //un
				contact.setPassword(cursor.getString(30)); //pw
				contact.setAdminNotes((cursor.getString(31)));
				
				
				// Adding contact to list
				contactList.add(contact);
				
				
				
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
		
	}*/
	
	public ArrayList<DispatchAllListModel> getAllContacts__() 
	{
		ArrayList<DispatchAllListModel> contactList = new ArrayList<DispatchAllListModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor.moveToFirst()) {
			do {
				
				DispatchAllListModel contact = new DispatchAllListModel();
				
				contact.setId(cursor.getString(0));
				contact.setOrder_id((cursor.getString(1)));
				contact.setAccountName((cursor.getString(2)));
				contact.setAccountnotes((cursor.getString(3)));
				
				//FROM:
				contact.setCompany((cursor.getString(4)));
				contact.setAddress((cursor.getString(5)));
				contact.setSuit((cursor.getString(6)));
				contact.setCity((cursor.getString(7)));
				contact.setState((cursor.getString(8)));
				contact.setZip((cursor.getString(9)));
				contact.setPuCellPhone((cursor.getString(10)));
				contact.setPuHomephone((cursor.getString(11)));
				contact.setPuinstruction((cursor.getString(12)));
				
				//TO:
				contact.setDlcompany((cursor.getString(13)));
				contact.setDladdress((cursor.getString(14)));
				contact.setDlsuit((cursor.getString(15)));
				contact.setDlcity((cursor.getString(16)));
				contact.setDlstate((cursor.getString(17)));
				contact.setDlzip((cursor.getString(18)));
				contact.setDlCellPhone((cursor.getString(19)));
				contact.setDlHomePhone((cursor.getString(20)));
				contact.setDlinstruction((cursor.getString(21)));
				
				contact.setOrder_Status((cursor.getString(22)));
				contact.setIsRoundTrip((cursor.getString(23)));
				contact.setHour((cursor.getString(25)));
				contact.setRDDate((cursor.getString(26)));
				contact.setPeice((cursor.getString(27)));
				contact.setWeight((cursor.getString(28)));
				contact.setUsername(cursor.getString(29)); //un
				contact.setPassword(cursor.getString(30)); //pw
				contact.setAdminnotes((cursor.getString(31))); //admin notes
				
				contact.setLastname((cursor.getString(32))); //last name
				contact.setNotes((cursor.getString(33))); //notes
				contact.setUrl((cursor.getString(34))); //url
				contact.setFilename((cursor.getString(35))); //file name
				contact.setImgstr((cursor.getString(36))); //Image
				contact.setYesno((cursor.getString(37))); //yes_no
				contact.setDatetime((cursor.getString(38))); //datetime
				contact.setTag((cursor.getString(39))); //tag
				
				
				
				
				
				contactList.add(contact);
				
				
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
		
	}
	
	
	
	public void delete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NAME, KEY_ID+"="+id, null);
	}
	

	
	public int deleteAll()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NAME, null, null);
	}

		

}
