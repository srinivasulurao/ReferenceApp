package com.aotd.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	
	private static DataBaseHelper mDBConnection;
	public static String Lock = "dblock";
	
	public static final String TABLE_NAME = "OfflineDispatchTable";
	public static final String COLUMN_ORDERID = "order_id";
	public static final String COLUMN_DRIVERID = "driver_id";
	public static final String COLUMN_RDDATEFORMAT = "RDDateFormat";
	public static final String COLUMN_MIN = "min";
	public static final String COLUMN_TIMEZONE = "timezone";
	// public static final String COLUMN_ORDERCOLOR = "orderColor";
	//public static final String COLUMN_ORDERSTATUS = "orderStatus";
	public static final String COLUMN_ACCOUNTNAME = "accountName";
	public static final String COLUMN_REF = "ref";
	public static final String COLUMN_PUINSTRUCTION = "PUInstruction";
	public static final String COLUMN_DLINSTRUCTION = "DLInstruction";
	public static final String COLUMN_SERVICENAME = "ServiceName";
	public static final String COLUMN_REQUESTOR = "Requestor";
	public static final String COLUMN_PIECE = "Piece";
	public static final String COLUMN_WEIGHT = "Weight";
	public static final String COLUMN_DPDATE = "DPDate";
	public static final String COLUMN_DLDATE = "DLDate";
	public static final String COLUMN_PUDATE = "PUDate";
	public static final String COLUMN_RDDATE = "RDDate";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_SIGNROUNDTRIP = "SignRoundTrip";
	public static final String COLUMN_SIGNDELIVERY = "SignDelivery";
	public static final String COLUMN_ISROUNDTRIP = "isRoundTrip";
	public static final String COLUMN_PCROUNDTRIP = "PCRoundTrip";
	public static final String COLUMN_SEQPU = "seqPU";
	public static final String COLUMN_COMPANYPU = "companyPU";
	public static final String COLUMN_ADDRESSPU = "addressPU";
	public static final String COLUMN_SUITPU = "suitPU";
	public static final String COLUMN_CITYPU = "cityPU";
	public static final String COLUMN_STATEPU = "statePU";
	public static final String COLUMN_ZIPPU = "zipPU";
	public static final String COLUMN_CELLPHONEPU = "cellPhonePU";
	public static final String COLUMN_HOMEPHONEPU = "homePhonePU";
	public static final String COLUMN_SEQDL = "seqDL";
	public static final String COLUMN_COMPANYDL = "companyDL";
	public static final String COLUMN_ADDRESSDL = "addressDL";
	public static final String COLUMN_SUITDL = "suitDL";
	public static final String COLUMN_CITYDL = "cityDL";
	public static final String COLUMN_STATEDL = "stateDL";
	public static final String COLUMN_ZIPDL = "zipDL";
	public static final String COLUMN_CELLPHONEDL = "cellPhoneDL";
	public static final String COLUMN_HOMEPHONEDL = "homePhoneDL";
	public static final String COLUMN_LASTNAME = "lastname";
	public static final String COLUMN_SIGNATURE = "signature";
	public static final String COLUMN_NOTES = "notes";
	public static final String COLUMN_WAITTIME = "waittime";
	public static final String COLUMN_BOXES = "boxes";
	public static final String COLUMN_VEHICLE = "vehicle";
	public static final String COLUMN_SIGNATUREDL = "signaturedl";
	public static final String COLUMN_LASTNAMEDL = "lastnamedl";
	public static final String COLUMN_ACTION = "action";
	
	
	private static String[] allColumn = {
		COLUMN_ORDERID,
		COLUMN_DRIVERID,
		COLUMN_RDDATEFORMAT,
		COLUMN_MIN,
		COLUMN_TIMEZONE,
		//COLUMN_ORDERCOLOR,
		//COLUMN_ORDERSTATUS,
		COLUMN_ACCOUNTNAME,
		COLUMN_REF,
		COLUMN_PUINSTRUCTION,
		COLUMN_DLINSTRUCTION,
		COLUMN_SERVICENAME,
		COLUMN_REQUESTOR,
		COLUMN_PIECE,
		COLUMN_WEIGHT,
		COLUMN_DPDATE,
		COLUMN_DLDATE,
		COLUMN_PUDATE,
		COLUMN_RDDATE,
		COLUMN_HOUR,
		COLUMN_SIGNROUNDTRIP,
		COLUMN_SIGNDELIVERY,
		COLUMN_ISROUNDTRIP,
		COLUMN_PCROUNDTRIP,
		COLUMN_SEQPU,
		COLUMN_COMPANYPU,
		COLUMN_ADDRESSPU,
		COLUMN_SUITPU,
		COLUMN_CITYPU,
		COLUMN_STATEPU,
		COLUMN_ZIPPU,
		COLUMN_CELLPHONEPU,
		COLUMN_HOMEPHONEPU,
		COLUMN_SEQDL,
		COLUMN_COMPANYDL,
		COLUMN_ADDRESSDL,
		COLUMN_SUITDL,
		COLUMN_CITYDL,
		COLUMN_STATEDL,
		COLUMN_ZIPDL,
		COLUMN_CELLPHONEDL,
		COLUMN_HOMEPHONEDL,
		COLUMN_LASTNAME,
		COLUMN_SIGNATURE,
		COLUMN_NOTES,
		COLUMN_WAITTIME,
		COLUMN_BOXES,
		COLUMN_VEHICLE,
		COLUMN_ACTION
	};
	
	private static final String DATABASE_NAME = "arriveOnTimeDel.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME + "(" 
			+ COLUMN_ORDERID  +" varchar(20)"
			+"," + COLUMN_DRIVERID
			+"," + COLUMN_RDDATEFORMAT
			+"," + COLUMN_MIN
			+"," + COLUMN_TIMEZONE
			/*  +"," + COLUMN_ORDERCOLOR
			      +"," + COLUMN_ORDERSTATUS*/
			+"," + COLUMN_ACCOUNTNAME
			+"," + COLUMN_REF
			+"," + COLUMN_PUINSTRUCTION
			+"," + COLUMN_DLINSTRUCTION
			+"," + COLUMN_SERVICENAME
			+"," + COLUMN_REQUESTOR
			+"," + COLUMN_PIECE
			+"," + COLUMN_WEIGHT
			+"," + COLUMN_DPDATE +" Date"
			+"," + COLUMN_DLDATE +" Date"
			+"," + COLUMN_PUDATE +" Date"
			+"," + COLUMN_RDDATE +" Date"
			+"," + COLUMN_HOUR
			+"," + COLUMN_SIGNROUNDTRIP
			+"," + COLUMN_SIGNDELIVERY
			+"," + COLUMN_ISROUNDTRIP
			+"," + COLUMN_PCROUNDTRIP
			+"," + COLUMN_SEQPU
			+"," + COLUMN_COMPANYPU
			+"," + COLUMN_ADDRESSPU
			+"," + COLUMN_SUITPU
			+"," + COLUMN_CITYPU
			+"," + COLUMN_STATEPU
			+"," + COLUMN_ZIPPU
			+"," + COLUMN_CELLPHONEPU
			+"," + COLUMN_HOMEPHONEPU
			+"," + COLUMN_SEQDL
			+"," + COLUMN_COMPANYDL
			+"," + COLUMN_ADDRESSDL
			+"," + COLUMN_SUITDL
			+"," + COLUMN_CITYDL
			+"," + COLUMN_STATEDL
			+"," + COLUMN_ZIPDL
			+"," + COLUMN_CELLPHONEDL
			+"," + COLUMN_HOMEPHONEDL
			+"," + COLUMN_LASTNAME
			+"," + COLUMN_SIGNATURE + " BLOB"
			+"," + COLUMN_NOTES
			+"," + COLUMN_WAITTIME
			+"," + COLUMN_BOXES
			+"," + COLUMN_VEHICLE
			+"," + COLUMN_SIGNATUREDL + " BLOB"
			+"," + COLUMN_LASTNAMEDL
			+"," + COLUMN_ACTION +" varchar(10)"
			+"," + "UNIQUE(" + COLUMN_ORDERID + ") ON CONFLICT IGNORE"
			+ ");";
	
	public DataBaseHelper(Context context){
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	public static synchronized DataBaseHelper getDBAdapterInstance(Context context){
		synchronized(Lock){
			if (mDBConnection == null) {
				mDBConnection = new DataBaseHelper(context);
			}
			return mDBConnection;
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		
		db.execSQL(DATABASE_CREATE);
		db.execSQL("create table RnaOffilneData(batchid varchar(30), stationid varchar(30), status varchar(30), DPDate varchar(30), PUDate varchar(30), DLDate varchar(30), batchdetials varchar(1000), signature BLOB, filename varchar(50), action varchar(10), rnadata varchar(1000), rnahomecode varchar(1000))");
		db.execSQL("create table DataBackUp(order_id varchar(30), signaturedl BLOB, signature BLOB, firstname varchar(30), type varchar(10))");
		//db.execSQL("create table ReceivedMails (_Id  integer primary key autoincrement, Email_Id varchar(50), Uid integer , EmailDate date, EmailTime varchar(10), EmailFrom varchar(50) , Subject varchar(200), EmailStatus integer(1), EnF_Category integer(1), BnD_Category integer(1), MnP_Category integer(1), GnF_Category integer(1), Email_dateTime varchar(100) UNIQUE NOT NULL, MessageBody varchar(500), Attachment  varchar(100), TimeZone varchar(20));");
		Log.v("DataBaseHelper Class", "Table Created");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,  int oldVersion, int newVersion){
		
		// TODO Auto-generated method stub
		Log.w("OnUpgradeDatabase :","Upgrading database from version " + oldVersion + " to "+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}	
}