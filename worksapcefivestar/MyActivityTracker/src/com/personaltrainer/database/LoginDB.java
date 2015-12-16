package com.personaltrainer.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.personaltrainer.model.ActivitiesModel;
import com.personaltrainer.model.ActivityPoints;
import com.personaltrainer.model.RegistrationModel;
import com.personaltrainer.widgets.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.webkit.WebChromeClient.CustomViewCallback;

public class LoginDB extends SQLiteOpenHelper {

	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = Utils.appName_;

	Context con;
	/*
	 * REGISTRATION:
	 */
	private static final String TABLE_REGISTRATION = "registration";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME= "name";
	private static final String KEY_EMAIL= "email";
	private static final String KEY_PASSWORD= "password";
	private static final String KEY_CONFIRMPASSOWRD= "confirm_password";
	private static final String KEY_PH_NO= "phone_number";


	/*
	 * ACTIVITY:MORNING
	 */
	private static final String TABLE_MORNING_ACTIVITY= "morningactivity";
	private static final String ACTIVITY_ID = "morning_id";
	private static final String ACTIVITY_NAME= "_name";
	private static final String ACTIVITY_TIME= "time";
	private static final String ACTIVITY_POINTS= "points";
	private static final String ACTIVITY_MY_POINTS= "mypoints";
	private static final String MORNING_DATE= "morning_date";
	private static final String MORNING_CATEGORY= "morning_category";

	/*
	 * ACTIVITY:DEFAULT MORNING
	 */
	private static final String TABLE_DEFAULT_MORNING_ACTIVITY= "defaultmorningactivity";
	private static final String DEFAULT_ACTIVITY_ID = "defaultmorning_id";
	private static final String DEFAULT_ACTIVITY_NAME= "default_name";
	private static final String DEFAULT_ACTIVITY_TIME= "default_time";
	private static final String DEFAULT_ACTIVITY_POINTS= "default_points";
	private static final String DEFAULT_ACTIVITY_MY_POINTS= "default_mypoints";
	private static final String DEFAULT_MORNING_DATE= "default_morning_date";
	private static final String DEFAULT_MORNING_CATEGORY= "default_morning_category";


	/*
	 * ACTIVITY:NOON
	 */
	private static final String TABLE_NOON_ACTIVITY= "noonactivity";
	private static final String NOON_ID = "noon_id";
	private static final String NOON_NAME= "noon_name";
	private static final String NOON_TIME= "noon_time";
	private static final String NOON_POINTS= "noon_points";
	private static final String NOON_MY_POINTS= "noon_my_points";
	private static final String NOON_DATE= "noon_date";
	private static final String NOON_CATEGORY= "noon_category";

	/*
	 * ACTIVITY:DEFAULT NOON
	 */
	private static final String TABLE_DEFAULT_NOON_ACTIVITY= "defaultnoonactivity";
	private static final String DEFAULT_NOON_ID = "default_noon_id";
	private static final String DEFAULT_NOON_NAME= "default_noon_name";
	private static final String DEFAULT_NOON_TIME= "default_noon_time";
	private static final String DEFAULT_NOON_POINTS= "default_noon_points";
	private static final String DEFAULT_NOON_MY_POINTS= "default_noon_my_points";
	private static final String DEFAULT_NOON_DATE= "default_noon_date";
	private static final String DEFAULT_NOON_CATEGORY= "default_noon_category";



	/*
	 * ACTIVITY:Evening
	 */
	private static final String TABLE_EVENING_ACTIVITY= "eveningactivity";
	private static final String EVENING_ID = "evening_id";
	private static final String EVENING_NAME= "evening_name";
	private static final String EVENING_TIME= "evening_time";
	private static final String EVENING_POINTS= "evening_points";
	private static final String EVENING_MY_POINTS= "evening_my_points";
	private static final String EVENING_DATE= "evening_date";
	private static final String EVENING_CATEGORY= "evening_category";

	/*
	 * ACTIVITY:Default Evening
	 */
	private static final String TABLE_DEFAULT_EVENING_ACTIVITY= "defaulteveningactivity";
	private static final String DEFAULT_EVENING_ID = "default_evening_id";
	private static final String DEFAULT_EVENING_NAME= "default_evening_name";
	private static final String DEFAULT_EVENING_TIME= "default_evening_time";
	private static final String DEFAULT_EVENING_POINTS= "default_evening_points";
	private static final String DEFAULT_EVENING_MY_POINTS= "default_evening_my_points";
	private static final String DEFAULT_EVENING_DATE= "default_evening_date";
	private static final String DEFAULT_EVENING_CATEGORY= "default_evening_category";



	/*
	 * ACTIVITY:Night
	 */
	private static final String TABLE_NIGHT_ACTIVITY= "nightactivity";
	private static final String NIGHT_ID = "night_id";
	private static final String NIGHT_NAME= "night_name";
	private static final String NIGHT_TIME= "night_time";
	private static final String NIGHT_POINTS= "night_points";
	private static final String NIGHT_MY_POINTS= "night_my_points";
	private static final String NIGHT_DATE= "night_date";
	private static final String NIGHT_CATEGORY= "night_category";

	/*
	 * ACTIVITY:DEFAULT Night
	 */
	private static final String TABLE_DEFAULT_NIGHT_ACTIVITY= "defaultnightactivity";
	private static final String DEFAULT_NIGHT_ID = "default_night_id";
	private static final String DEFAULT_NIGHT_NAME= "default_night_name";
	private static final String DEFAULT_NIGHT_TIME= "default_night_time";
	private static final String DEFAULT_NIGHT_POINTS= "default_night_points";
	private static final String DEFAULT_NIGHT_MY_POINTS= "default_night_my_points";
	private static final String DEFAULT_NIGHT_DATE= "default_night_date";
	private static final String DEFAULT_NIGHT_CATEGORY= "default_night_category";



	/*
	 * ACTIVITY :POINTS
	 */
	private static final String TABLE_ACTIVITY_POINTS = "activitypoints";
	private static final String POINTS_ID = "points_id";
	private static final String POINTS_MORNING = "points_morning";
	private static final String POINTS_NOON= "points_noon";
	private static final String POINTS_EVENING= "points_evening";
	private static final String POINTS_NIGHT= "points_night";
	private static final String POINTS_DATE= "points_date";
	private static final String POINTS_MESSAGE= "points_message";
	private static final String POINTS_TITLE= "points_title";
	private static final String POINTS_IMAGE= "points_image";
	private static final String POINTS_TOTAL= "points_total";
	private static final String POINTS_DAY= "points_day";

	/*
	 * Morning Points
	 */
	private static final String TABLE_MORNING_POINTS = "morningpoints";
	private static final String MORNING_POINTS_ID = "morning_points_id";
	private static final String MORNING_POINTS_NAME  = "morning_points_name";
	private static final String MORNING_POINTS_POINTS= "morning_points_points";
	private static final String MORNING_POINTS_DATE= "morning_points_date";

	/*
	 * Afternoon Points
	 */
	private static final String TABLE_NOON_POINTS = "noonpoints";
	private static final String NOON_POINTS_ID = "noon_points_id";
	private static final String NOON_POINTS_NAME  = "noon_points_name";
	private static final String NOON_POINTS_POINTS= "noon_points_points";
	private static final String NOON_POINTS_DATE= "noon_points_date";

	/*
	 * Evening Points
	 */
	private static final String TABLE_EVENING_POINTS = "eveningpoints";
	private static final String EVENING_POINTS_ID = "evening_points_id";
	private static final String EVENING_POINTS_NAME  = "evening_points_name";
	private static final String EVENING_POINTS_POINTS= "evening_points_points";
	private static final String EVENING_POINTS_DATE= "evening_points_date";

	/*
	 * Night Points
	 */
	private static final String TABLE_NIGHT_POINTS = "nightpoints";
	private static final String NIGHT_POINTS_ID = "night_points_id";
	private static final String NIGHT_POINTS_NAME  = "night_points_name";
	private static final String NIGHT_POINTS_POINTS= "night_points_points";
	private static final String NIGHT_POINTS_DATE= "night_points_date";

	/*
	 * NOTES
	 */
	private static final String TABLE_NOTES = "notes";
	private static final String NOTES_ID = "notes_id";
	private static final String NOTES_DATE = "notes_date";
	private static final String NOTES_CONTENT = "notes_content";
	private static final String NOTES_ARCHIEVE = "notes_archieve";
	private static final String NOTES_AUDIO = "notes_audio";
	private static final String NOTES_PICTURE = "notes_picture";

	/*
	 * NOTES UNIMPORTANT.
	 */
	private static final String TABLE_NOTES_ = "notes_";
	private static final String NOTES_ID_ = "notes_id_";
	private static final String NOTES_DATE_ = "notes_date_";
	private static final String NOTES_CONTENT_ = "notes_content_";
	private static final String NOTES_AUDIO_ = "notes_audio_";
	private static final String NOTES_PICTURE_ = "notes_picture_";

	/*
	 * TO-DO's
	 */
	private static final String TABLE_TODO = "todo";
	private static final String TODO_ID = "todo_id";
	private static final String TODO_NAME = "todo_name";
	private static final String TODO_POINTS = "todo_points";
	private static final String TODO_ADDEDD = "todo_added";
	private static final String TODO_DATE = "todo_date";
	private static final String TODO_ENABLE = "todo_enable";




	public LoginDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		con = context;

	}



	@Override
	public void onCreate(SQLiteDatabase db) {

		//Registration Table:
		String CREATE_REGISTRATION_TABLE = "CREATE TABLE " + TABLE_REGISTRATION + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_NAME + " TEXT,"
				+ KEY_EMAIL + " TEXT,"
				+ KEY_PASSWORD + " TEXT,"
				+ KEY_CONFIRMPASSOWRD + " TEXT,"
				+ KEY_PH_NO + " TEXT" + ")";

		//ACtivity MORNING Table:
		String CREATE_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_MORNING_ACTIVITY + "("
				+ ACTIVITY_ID + " INTEGER PRIMARY KEY," 
				+ ACTIVITY_NAME + " TEXT,"
				+ ACTIVITY_TIME + " TEXT,"
				+ ACTIVITY_POINTS + " TEXT,"
				+ ACTIVITY_MY_POINTS + " TEXT,"
				+ MORNING_DATE + " TEXT,"
				+ MORNING_CATEGORY + " TEXT" + ")";

		//ACtivity DEFAULT MORNING Table:
		String CREATE_DEFAULT_ACTIVITY_TABLE = "CREATE TABLE " + TABLE_DEFAULT_MORNING_ACTIVITY + "("
				+ DEFAULT_ACTIVITY_ID + " INTEGER PRIMARY KEY," 
				+ DEFAULT_ACTIVITY_NAME + " TEXT,"
				+ DEFAULT_ACTIVITY_TIME + " TEXT,"
				+ DEFAULT_ACTIVITY_POINTS + " TEXT,"
				+ DEFAULT_ACTIVITY_MY_POINTS + " TEXT,"
				+ DEFAULT_MORNING_DATE + " TEXT,"
				+ DEFAULT_MORNING_CATEGORY + " TEXT" + ")";



		//ACtivity NOON Table:
		String CREATE_NOON_TABLE = "CREATE TABLE " + TABLE_NOON_ACTIVITY + "("
				+ NOON_ID + " INTEGER PRIMARY KEY," 
				+ NOON_NAME + " TEXT,"
				+ NOON_TIME + " TEXT,"
				+ NOON_POINTS + " TEXT,"
				+ NOON_MY_POINTS + " TEXT,"
				+ NOON_DATE + " TEXT,"
				+ NOON_CATEGORY + " TEXT" + ")";

		//ACtivity NOON Table:
		String CREATE_DEFAULT_NOON_TABLE = "CREATE TABLE " + TABLE_DEFAULT_NOON_ACTIVITY + "("
				+ DEFAULT_NOON_ID + " INTEGER PRIMARY KEY," 
				+ DEFAULT_NOON_NAME + " TEXT,"
				+ DEFAULT_NOON_TIME + " TEXT,"
				+ DEFAULT_NOON_POINTS + " TEXT,"
				+ DEFAULT_NOON_MY_POINTS + " TEXT,"
				+ DEFAULT_NOON_DATE + " TEXT,"
				+ DEFAULT_NOON_CATEGORY + " TEXT" + ")";



		//ACtivity Evening Table:
		String CREATE_EVENING_TABLE = "CREATE TABLE " + TABLE_EVENING_ACTIVITY + "("
				+ EVENING_ID + " INTEGER PRIMARY KEY," 
				+ EVENING_NAME + " TEXT,"
				+ EVENING_TIME + " TEXT,"
				+ EVENING_POINTS + " TEXT,"
				+ EVENING_MY_POINTS + " TEXT,"
				+ EVENING_DATE + " TEXT,"
				+ EVENING_CATEGORY + " TEXT" + ")";


		//ACtivity DEFAULT_ Evening Table:
		String CREATE_DEFAULT_EVENING_TABLE = "CREATE TABLE " + TABLE_DEFAULT_EVENING_ACTIVITY + "("
				+ DEFAULT_EVENING_ID + " INTEGER PRIMARY KEY," 
				+ DEFAULT_EVENING_NAME + " TEXT,"
				+ DEFAULT_EVENING_TIME + " TEXT,"
				+ DEFAULT_EVENING_POINTS + " TEXT,"
				+ DEFAULT_EVENING_MY_POINTS + " TEXT,"
				+ DEFAULT_EVENING_DATE + " TEXT,"
				+ DEFAULT_EVENING_CATEGORY + " TEXT" + ")";



		//ACtivity Night Table:
		String CREATE_NIGHT_TABLE = "CREATE TABLE " + TABLE_NIGHT_ACTIVITY + "("
				+ NIGHT_ID + " INTEGER PRIMARY KEY," 
				+ NIGHT_NAME + " TEXT,"
				+ NIGHT_TIME + " TEXT,"
				+ NIGHT_POINTS + " TEXT,"
				+ NIGHT_MY_POINTS + " TEXT,"
				+ NIGHT_DATE + " TEXT,"
				+ NIGHT_CATEGORY + " TEXT" + ")";

		//ACtivity DEFAULT_Night Table:
		String CREATE_DEFAULT_NIGHT_TABLE = "CREATE TABLE " + TABLE_DEFAULT_NIGHT_ACTIVITY + "("
				+ DEFAULT_NIGHT_ID + " INTEGER PRIMARY KEY," 
				+ DEFAULT_NIGHT_NAME + " TEXT,"
				+ DEFAULT_NIGHT_TIME + " TEXT,"
				+ DEFAULT_NIGHT_POINTS + " TEXT,"
				+ DEFAULT_NIGHT_MY_POINTS + " TEXT,"
				+ DEFAULT_NIGHT_DATE + " TEXT,"
				+ DEFAULT_NIGHT_CATEGORY + " TEXT" + ")";



		//Activity Points Table:
		String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_POINTS + "("
				+ POINTS_ID + " INTEGER PRIMARY KEY," 
				+ POINTS_MORNING + " TEXT,"
				+ POINTS_NOON + " TEXT,"
				+ POINTS_EVENING + " TEXT,"
				+ POINTS_NIGHT + " TEXT,"
				+ POINTS_DATE + " TEXT,"
				+ POINTS_MESSAGE + " TEXT,"
				+ POINTS_TITLE + " TEXT,"
				+ POINTS_IMAGE + " TEXT,"
				+ POINTS_TOTAL + " TEXT,"
				+ POINTS_DAY + " TEXT" + ")";

		//Morning Points Table:
		String CREATE_MORNING_POINTS_TABLE = "CREATE TABLE " + TABLE_MORNING_POINTS + "("
				+ MORNING_POINTS_ID + " INTEGER PRIMARY KEY," 
				+ MORNING_POINTS_NAME + " TEXT,"
				+ MORNING_POINTS_POINTS + " TEXT,"
				+ MORNING_POINTS_DATE + " TEXT" + ")";

		//Afternoon Points Table:
		String CREATE_NOON_POINTS_TABLE = "CREATE TABLE " + TABLE_NOON_POINTS + "("
				+ NOON_POINTS_ID + " INTEGER PRIMARY KEY," 
				+ NOON_POINTS_NAME + " TEXT,"
				+ NOON_POINTS_POINTS + " TEXT,"
				+ NOON_POINTS_DATE + " TEXT" + ")";

		//Evening Points Table:
		String CREATE_EVENING_POINTS_TABLE = "CREATE TABLE " + TABLE_EVENING_POINTS + "("
				+ EVENING_POINTS_ID + " INTEGER PRIMARY KEY," 
				+ EVENING_POINTS_NAME + " TEXT,"
				+ EVENING_POINTS_POINTS + " TEXT,"
				+ EVENING_POINTS_DATE + " TEXT" + ")";

		//Night Points Table:
		String CREATE_NIGHT_POINTS_TABLE = "CREATE TABLE " + TABLE_NIGHT_POINTS + "("
				+ NIGHT_POINTS_ID + " INTEGER PRIMARY KEY," 
				+ NIGHT_POINTS_NAME + " TEXT,"
				+ NIGHT_POINTS_POINTS + " TEXT,"
				+ NIGHT_POINTS_DATE + " TEXT" + ")";

		//NOTES Table:
		String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
				+ NOTES_ID + " INTEGER PRIMARY KEY," 
				+ NOTES_DATE + " TEXT,"
				+ NOTES_CONTENT + " TEXT,"
				+ NOTES_ARCHIEVE + " TEXT,"
				+ NOTES_AUDIO + " TEXT,"
				+ NOTES_PICTURE + " TEXT" + ")";

		//NOTES_ Table:
		String CREATE_NOTES_TABLE_ = "CREATE TABLE " + TABLE_NOTES_ + "("
				+ NOTES_ID_ + " INTEGER PRIMARY KEY," 
				+ NOTES_DATE_ + " TEXT,"
				+ NOTES_CONTENT_ + " TEXT,"
				+ NOTES_AUDIO + " TEXT,"
				+ NOTES_PICTURE + " TEXT" + ")";

		//TO-DO Table:
		String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
				+ TODO_ID + " INTEGER PRIMARY KEY," 
				+ TODO_NAME + " TEXT,"
				+ TODO_POINTS + " TEXT,"
				+ TODO_ADDEDD + " TEXT,"
				+ TODO_DATE + " TEXT,"
				+ TODO_ENABLE + " TEXT" + ")";



		db.execSQL(CREATE_REGISTRATION_TABLE);

		db.execSQL(CREATE_ACTIVITY_TABLE);
		db.execSQL(CREATE_DEFAULT_ACTIVITY_TABLE);

		db.execSQL(CREATE_NOON_TABLE);
		db.execSQL(CREATE_DEFAULT_NOON_TABLE);

		db.execSQL(CREATE_EVENING_TABLE);
		db.execSQL(CREATE_DEFAULT_EVENING_TABLE);

		db.execSQL(CREATE_NIGHT_TABLE);
		db.execSQL(CREATE_DEFAULT_NIGHT_TABLE);

		db.execSQL(CREATE_POINTS_TABLE);
		db.execSQL(CREATE_MORNING_POINTS_TABLE);
		db.execSQL(CREATE_NOON_POINTS_TABLE);
		db.execSQL(CREATE_EVENING_POINTS_TABLE);
		db.execSQL(CREATE_NIGHT_POINTS_TABLE);
		db.execSQL(CREATE_NOTES_TABLE);
		db.execSQL(CREATE_NOTES_TABLE_);
		db.execSQL(CREATE_TODO_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATION);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MORNING_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_MORNING_ACTIVITY);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOON_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_NOON_ACTIVITY);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENING_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_EVENING_ACTIVITY);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NIGHT_ACTIVITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_NIGHT_ACTIVITY);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MORNING_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOON_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENING_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NIGHT_POINTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES_);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

		onCreate(db);
	}


	/*
	 *REGISTRATION: INSERT OPERATION:
	 */
	public void addContact(RegistrationModel reg)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_NAME, reg.get_name());
		values.put(KEY_EMAIL,reg.get_email());
		values.put(KEY_PASSWORD,reg.get_password());
		values.put(KEY_CONFIRMPASSOWRD,reg.get_confirmPassword());
		values.put(KEY_PH_NO,reg.get_phone());

		db.insert(TABLE_REGISTRATION, null, values);
		db.close();
	}

	/*
	 * ACTIVITIES: INSERT OPERATION : MORNING
	 */
	public void addActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ACTIVITY_NAME, am.get_name());
		values.put(ACTIVITY_TIME,am.get_time());
		values.put(ACTIVITY_POINTS,am.get_points());
		values.put(ACTIVITY_MY_POINTS,"0");
		values.put(MORNING_CATEGORY, am.get_category());

		db.insert(TABLE_MORNING_ACTIVITY, null, values);
		db.close();
	}

	/*
	 * ACTIVITIES: INSERT OPERATION : DEFAULT_MORNING
	 */
	public void addDefaultActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DEFAULT_ACTIVITY_NAME, am.get_name());
		values.put(DEFAULT_ACTIVITY_TIME,am.get_time());
		values.put(DEFAULT_ACTIVITY_POINTS,am.get_points());
		values.put(DEFAULT_MORNING_CATEGORY, am.get_category());

		db.insert(TABLE_DEFAULT_MORNING_ACTIVITY, null, values);
		db.close();
	}


	/*
	 * TO-DO LIST: INSERT OPERATION 
	 */
	public void addTODOActivities(String todo_name, String todo_points, String todo_added, String sEnable)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(TODO_NAME, todo_name);
		values.put(TODO_POINTS,todo_points);
		values.put(TODO_ADDEDD,todo_added);
		values.put(TODO_DATE,Utils.getTodaysDate(con));
		values.put(TODO_ENABLE,sEnable);

		db.insert(TABLE_TODO, null, values);
		db.close();

	}

	/*
	 * ACTIVITIES: INSERT OPERATION : NOON
	 */
	public void addNoonActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NOON_NAME, am.get_name());
		values.put(NOON_TIME,am.get_time());
		values.put(NOON_POINTS,am.get_points());
		values.put(NOON_MY_POINTS,"0");
		values.put(NOON_CATEGORY, am.get_category());

		db.insert(TABLE_NOON_ACTIVITY, null, values);
		db.close();

	}

	/*
	 * ACTIVITIES: INSERT OPERATION :DEFAULT NOON
	 */
	public void addDefaultNoonActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DEFAULT_NOON_NAME, am.get_name());
		values.put(DEFAULT_NOON_TIME,am.get_time());
		values.put(DEFAULT_NOON_POINTS,am.get_points());
		values.put(DEFAULT_NOON_CATEGORY, am.get_category());

		db.insert(TABLE_DEFAULT_NOON_ACTIVITY, null, values);
		db.close();

	}




	/*
	 * ACTIVITIES: INSERT OPERATION : EVENING
	 */
	public void addEveningActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(EVENING_NAME, am.get_name());
		values.put(EVENING_TIME,am.get_time());
		values.put(EVENING_POINTS,am.get_points());
		values.put(EVENING_CATEGORY, am.get_category());

		db.insert(TABLE_EVENING_ACTIVITY, null, values);
		db.close();

	}

	/*
	 * ACTIVITIES: INSERT OPERATION :DEFAULT_ EVENING
	 */
	public void addDefaultEveningActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DEFAULT_EVENING_NAME, am.get_name());
		values.put(DEFAULT_EVENING_TIME,am.get_time());
		values.put(DEFAULT_EVENING_POINTS,am.get_points());
		values.put(DEFAULT_EVENING_CATEGORY, am.get_category());

		db.insert(TABLE_DEFAULT_EVENING_ACTIVITY, null, values);
		db.close();

	}



	/*
	 * ACTIVITIES: INSERT OPERATION : Night
	 */
	public void addNightActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NIGHT_NAME, am.get_name());
		values.put(NIGHT_TIME,am.get_time());
		values.put(NIGHT_POINTS,am.get_points());
		values.put(NIGHT_MY_POINTS,"0");
		values.put(NIGHT_CATEGORY, am.get_category());

		db.insert(TABLE_NIGHT_ACTIVITY, null, values);
		db.close();

	}

	/*
	 * ACTIVITIES: INSERT OPERATION : DEFAULT_Night
	 */
	public void addDefaultNightActivities(ActivitiesModel am)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(DEFAULT_NIGHT_NAME, am.get_name());
		values.put(DEFAULT_NIGHT_TIME,am.get_time());
		values.put(DEFAULT_NIGHT_POINTS,am.get_points());
		values.put(DEFAULT_NIGHT_CATEGORY, am.get_category());

		db.insert(TABLE_DEFAULT_NIGHT_ACTIVITY, null, values);
		db.close();

	}



	/*
	 * ACTIVITIES POINTS INSERT OPERATION:: Individual
	 */
	public void addActivitiesPoints(String pts,String date,int from)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		if(from == Utils.MORNING )
		{
			values.put(POINTS_MORNING, pts);
			values.put(POINTS_DATE, date);
		}
		else if(from == Utils.NOON)
		{
			values.put(POINTS_NOON, pts);
			values.put(POINTS_DATE, date);
		}
		else if(from == Utils.EVENING)
		{
			values.put(POINTS_EVENING, pts);
			values.put(POINTS_DATE, date);
		}
		else if(from == Utils.NIGHT)
		{
			values.put(POINTS_NIGHT, pts);
			values.put(POINTS_DATE, date);
		}

		db.insert(TABLE_ACTIVITY_POINTS, null, values);
		db.close();

	}

	/*
	 * ACTIVITIES POINTS INSERT OPERATION:: ALL
	 */
	public void addActivitiesPointsAll(String ptsMorning,String ptsNoon,String ptsEvening,String ptsNight,String date,
			String msg,String title,String img,String total,String sData)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(POINTS_MORNING, ptsMorning);
		values.put(POINTS_NOON, ptsNoon);
		values.put(POINTS_EVENING, ptsEvening);
		values.put(POINTS_NIGHT, ptsNight);
		values.put(POINTS_DATE, date);
		values.put(POINTS_MESSAGE, msg);
		values.put(POINTS_TITLE, title);
		values.put(POINTS_IMAGE, img);
		values.put(POINTS_TOTAL, total);
		values.put(POINTS_DAY, sData);

		db.insert(TABLE_ACTIVITY_POINTS, null, values);
		db.close();

	}

	//Morning Points Insert:
	public void addMorningPoints(String sName, String sPoints, String sDate)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();


		values.put(MORNING_POINTS_NAME, sName);
		values.put(MORNING_POINTS_POINTS,sPoints);
		values.put(MORNING_POINTS_DATE,sDate);

		db.insert(TABLE_MORNING_POINTS, null, values);
		db.close();
	}

	//Afternoon Points Insert:
	public void addNoonPoints(String sName, String sPoints,  String sDate)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NOON_POINTS_NAME, sName);
		values.put(NOON_POINTS_POINTS,sPoints);
		values.put(NOON_POINTS_DATE,sDate);

		db.insert(TABLE_NOON_POINTS, null, values);
		db.close();
	}

	//Evening Points Insert:
	public void addEveningPoints(String sName, String sPoints, String sDate)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(EVENING_POINTS_NAME, sName);
		values.put(EVENING_POINTS_POINTS,sPoints);
		values.put(EVENING_POINTS_DATE,sDate);

		db.insert(TABLE_EVENING_POINTS, null, values);
		db.close();
	}

	//Night Points Insert:
	public void addNightPoints(String sName, String sPoints, String sDate)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NIGHT_POINTS_NAME, sName);
		values.put(NIGHT_POINTS_POINTS,sPoints);
		values.put(NIGHT_POINTS_DATE,sDate);

		db.insert(TABLE_NIGHT_POINTS, null, values);
		db.close();
	}

	//NOTES Insert:
	public void addNotes(String sDate, String sContent, String sAudio, String sPicture)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NOTES_DATE, sDate);
		values.put(NOTES_CONTENT,sContent);
		values.put(NOTES_ARCHIEVE,"false");
		values.put(NOTES_AUDIO,sAudio);
		values.put(NOTES_PICTURE,sPicture);

		db.insert(TABLE_NOTES, null, values);
		db.close();
	}

	//NOTES_ Insert:
	public void addNotes_(String sDate, String sContent, String sAudio, String sPicture)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(NOTES_DATE_, sDate);
		values.put(NOTES_CONTENT_,sContent);
		values.put(NOTES_AUDIO,sAudio);
		values.put(NOTES_PICTURE,sPicture);
		db.insert(TABLE_NOTES_, null, values);
		db.close();
	}


	//REGISTRATION: Get All Contacts:
	public List<RegistrationModel> getAllContacts() 
	{
		List<RegistrationModel> contactList = new ArrayList<RegistrationModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_REGISTRATION;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);


		if (cursor.moveToFirst()) {
			do {
				RegistrationModel contact = new RegistrationModel();
				contact.set_id((Integer.parseInt(cursor.getString(0))));
				contact.set_name((cursor.getString(1)));
				contact.set_email((cursor.getString(2)));
				contact.set_password((cursor.getString(3)));
				contact.set_confirmPassword((cursor.getString(4)));
				contact.set_phone((cursor.getString(5)));

				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		return contactList;
	}


	//Get TO-DO List:
	public Cursor getTodoList()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_TODO;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		return cursor;
	}

	//ACTIVITIES: Get all Contacts : MORNING
	public List<ActivitiesModel> getActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_MORNING_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		return contactList;
	}

	//ACTIVITIES: Get all Contacts : DEFAULT MORNING
	public List<ActivitiesModel> getDefaultActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_DEFAULT_MORNING_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());
		}
		return contactList;
	}



	//ACTIVITIES: Get all Contacts : NOON
	public List<ActivitiesModel> getNoonActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_NOON_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}

	//ACTIVITIES: Get all Contacts : DEFAULT_NOON
	public List<ActivitiesModel> getDefaultNoonActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_DEFAULT_NOON_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}



	//ACTIVITIES: Get all Contacts : EVENING
	public List<ActivitiesModel> getEveningActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_EVENING_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}


	//ACTIVITIES: Get all Contacts : DEFAULT_EVENING
	public List<ActivitiesModel> getDefaultEveningActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_DEFAULT_EVENING_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}



	//ACTIVITIES: Get all Contacts : Night
	public List<ActivitiesModel> getNightActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_NIGHT_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}

	//ACTIVITIES: Get all Contacts :Default Night
	public List<ActivitiesModel> getDefaultNightActivityContacts() 
	{
		List<ActivitiesModel> contactList = new ArrayList<ActivitiesModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_DEFAULT_NIGHT_ACTIVITY;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do {
				ActivitiesModel contact = new ActivitiesModel();
				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.set_name(cursor.getString(1));
				contact.set_time(cursor.getString(2));
				contact.set_points(cursor.getString(3));
				contact.set_mypoints(cursor.getString(4));
				contact.set_date(cursor.getString(5));
				contact.set_category(cursor.getString(6));

				contactList.add(contact);
			} while (cursor.moveToNext());

		}
		return contactList;

	}



	//GET ACTIVITIES POINTS
	public List<ActivityPoints> getActivityPoints()
	{
		List<ActivityPoints> contactList = new ArrayList<ActivityPoints>();
		String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_POINTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) 
		{
			do 
			{
				ActivityPoints contact = new ActivityPoints();
				contact.setId(Integer.parseInt(cursor.getString(0)));
				contact.setMorning_points(cursor.getString(1));
				contact.setNoon_points(cursor.getString(2));
				contact.setEvening_points(cursor.getString(3));
				contact.setNight_points(cursor.getString(4));
				contact.setDate(cursor.getString(5));
				contact.setMessage(cursor.getString(6));
				contact.setTitle(cursor.getString(7));
				contact.setImgvalue(cursor.getString(8));
				contact.setTotal(cursor.getString(9));
				contact.setData(cursor.getString(10));

				contactList.add(contact);

			}while (cursor.moveToNext());
		}
		return contactList;
	}

	//Get Morning Points:
	public Cursor getMorningPoints()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_MORNING_POINTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	//Get Noon Points:
	public Cursor getNoonPoints()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_NOON_POINTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	//Get Evening Points:
	public Cursor getEveningPoints()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_EVENING_POINTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	//Get Night Points:
	public Cursor getNightPoints()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_NIGHT_POINTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	//Get NOTES:
	public Cursor getNotes()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}

	//Get NOTES_:
	public Cursor getNotes_()
	{
		String selectQuery = "SELECT  * FROM " + TABLE_NOTES_;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;
	}


	// REGISTRATION: Getting contacts Count : REGISTRATION
	public int getContactsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_REGISTRATION;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : MORNING
	public int getActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_MORNING_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : DEFAULT_MORNING
	public int getDefaultActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_DEFAULT_MORNING_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : NOON
	public int getNoonActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_NOON_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count :DEFAULT NOON
	public int getDefaultNoonActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_DEFAULT_NOON_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}


	// ACTIVITIES: Getting contacts Count : EVENING
	public int getEveningActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_EVENING_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : DEFAULT_EVENING
	public int getDefaultEveningActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_DEFAULT_EVENING_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : NIGHT
	public int getNightActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_NIGHT_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	// ACTIVITIES: Getting contacts Count : DEFAULT_NIGHT
	public int getDefaultNightActivitiesCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_DEFAULT_NIGHT_ACTIVITY;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//GET ACTIVITY POINTS COUNT
	public int getActivityPointsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_ACTIVITY_POINTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//GET MORNING ACTIVITY POINTS COUNT
	public int getMorningActivityPointsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_MORNING_POINTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//GET NOON ACTIVITY POINTS COUNT
	public int getNOONActivityPointsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_NOON_POINTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//GET EVENINGACTIVITY POINTS COUNT
	public int getEveningActivityPointsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_EVENING_POINTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//GET NIGHT ACTIVITY POINTS COUNT
	public int getNightActivityPointsCount() 
	{
		String countQuery = "SELECT  * FROM " + TABLE_NIGHT_POINTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	//REGISTRATION:Delete Row by ID:
	public void delete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_REGISTRATION, KEY_ID+"="+id, null);
	}

	//Activity:Delete Row by ID: MORNING
	public void Activitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_MORNING_ACTIVITY, ACTIVITY_ID+"="+id, null);
	}

	//Activity:Delete Row by ID: DEFAULT_MORNING
	public void DefaultActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_DEFAULT_MORNING_ACTIVITY, DEFAULT_ACTIVITY_ID+"="+id, null);
	}

	//Activity:Delete Row by ID: NOON
	public void NoonActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NOON_ACTIVITY, NOON_ID+"="+id, null);
	}

	//Activity:Delete Row by ID: DefaultNOON
	public void DefaultNoonActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_DEFAULT_NOON_ACTIVITY, DEFAULT_NOON_ID+"="+id, null);
	}



	//Activity:Delete Row by ID: EVENING
	public void EveningActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_EVENING_ACTIVITY, EVENING_ID+"="+id, null);
	}

	//Activity:Delete Row by ID:DEFAULT_ EVENING
	public void DefaultEveningActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_DEFAULT_EVENING_ACTIVITY, DEFAULT_EVENING_ID+"="+id, null);
	}



	//Activity:Delete Row by ID: NIGHT
	public void NightActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NIGHT_ACTIVITY, NIGHT_ID+"="+id, null);
	}

	//Activity:Delete Row by ID:DEFAULT_ NIGHT
	public void DefaultNightActivitydelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_DEFAULT_NIGHT_ACTIVITY, DEFAULT_NIGHT_ID+"="+id, null);
	}



	//Actiivity points: Delete Row by Id
	public void ActivityPointsdelete_byID(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_ACTIVITY_POINTS, POINTS_ID+"="+id, null);
	}

	//Morning Points:: Delete row by Id.
	public void MorningPointsDeleteRowById(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_MORNING_POINTS, MORNING_POINTS_ID+"="+id, null);
	}

	//NOTES:: Delete row by Id.
	public void NotesDeleteRowById(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NOTES, NOTES_ID+"="+id, null);
	}

	//NOTES_:: Delete row by Id.
	public void Notes_DeleteRowById(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_NOTES_, NOTES_ID_+"="+id, null);
	}

	//TO-DO:: Delete row by Id.
	public void ToDoDeleteRowById(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		db.delete(TABLE_TODO, TODO_ID+"="+id, null);
	}

	//REGISTRATION::Delete Table:
	public int RegistrationdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_REGISTRATION, null, null);
	}


	//MORNING ACTIVITY::Delete Table:
	public int ActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_MORNING_ACTIVITY, null, null);
	}

	//DEFAULTMORNING ACTIVITY::Delete Table:
	public int DefaultActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_DEFAULT_MORNING_ACTIVITY, null, null);
	}



	//NOOON ACTIVITY::Delete Table:
	public int NoonActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NOON_ACTIVITY, null, null);
	}

	//DEFAULT_NOOON ACTIVITY::Delete Table:
	public int DefaultNoonActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_DEFAULT_NOON_ACTIVITY, null, null);
	}


	//EVENING ACTIVITY::Delete Table:
	public int EveningActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_EVENING_ACTIVITY, null, null);
	}

	//DefaultEVENING ACTIVITY::Delete Table:
	public int DefaultEveningActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_DEFAULT_EVENING_ACTIVITY, null, null);
	}



	//NIGHT ACTIVITY::Delete Table:
	public int NightActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NIGHT_ACTIVITY, null, null);
	}

	//DefaultNIGHT ACTIVITY::Delete Table:
	public int DefaultNightActivitydeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_DEFAULT_NIGHT_ACTIVITY, null, null);
	}



	//ACTIVITY POINTS::Delete Table:
	public int ActivityPointsdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_ACTIVITY_POINTS, null, null);
	}

	//MORNING POINTS::Delete Table:
	public int MorningActivityPointsdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_MORNING_POINTS, null, null);
	}

	//NOON POINTS::Delete Table:
	public int NoonActivityPointsdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NOON_POINTS, null, null);
	}

	//Evening POINTS::Delete Table:
	public int EveningActivityPointsdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_EVENING_POINTS, null, null);
	}

	//Night POINTS::Delete Table:
	public int NightActivityPointsdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NIGHT_POINTS, null, null);
	}

	//NOTES::Delete Table:
	public int NotesdeleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NOTES, null, null);
	}

	//NOTES_::Delete Table:
	public int Notes_deleteTable()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		return db.delete(TABLE_NOTES_, null, null);
	}


	//MORNING Activity:Time Update.
	public void UpdateActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(ACTIVITY_TIME, updateTime);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}

	//DEFAULT_MORNING Activity:Time Update.
	public void UpdateDefaultActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_ACTIVITY_TIME, updateTime);
		db.update(TABLE_DEFAULT_MORNING_ACTIVITY, args, DEFAULT_ACTIVITY_ID + "=" + id, null);
	}

	//MORNING Activity:Name Update.
	public void UpdateActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(ACTIVITY_NAME, updateTime);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}
	//DEFAULT_MORNING Activity:Name Update.
	public void UpdateDefaultActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_ACTIVITY_NAME, updateTime);
		db.update(TABLE_DEFAULT_MORNING_ACTIVITY, args, DEFAULT_ACTIVITY_ID + "=" + id, null);
	}

	//MORNING Activity:DATE Update.
	public void UpdateActivityDate(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(MORNING_DATE, updateTime);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}

	//MORNING Activity:CATEGORY Update.
	public void UpdateActivityCategory(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(MORNING_CATEGORY, updateTime);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}




	//NOON Activity:Time Update.
	public void UpdateNoonActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_TIME, updateTime);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}
	//DEFAULT_NOON Activity:Time Update.
	public void UpdateDefaultNoonActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NOON_TIME, updateTime);
		db.update(TABLE_DEFAULT_NOON_ACTIVITY, args, DEFAULT_NOON_ID + "=" + id, null);
	}

	//NOON Activity:Name Update.
	public void UpdateNoonActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_NAME, updateTime);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}
	//DEFAULT_NOON Activity:Name Update.
	public void UpdateDefaultNoonActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NOON_NAME, updateTime);
		db.update(TABLE_DEFAULT_NOON_ACTIVITY, args, DEFAULT_NOON_ID + "=" + id, null);
	}

	//NOON Activity:DATE Update.
	public void UpdateNoonActivityDate(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_DATE, updateTime);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}
	//NOON Activity:CATEGORY Update.
	public void UpdateNoonActivityCategory(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_CATEGORY, updateTime);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}


	//EVENING Activity:Time Update.
	public void UpdateEveningActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_TIME, updateTime);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}
	//DEFAULT_EVENING Activity:Time Update.
	public void UpdateDefaultEveningActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_EVENING_TIME, updateTime);
		db.update(TABLE_DEFAULT_EVENING_ACTIVITY, args, DEFAULT_EVENING_ID + "=" + id, null);
	}

	//EVENING Activity:NAME Update.
	public void UpdateEveningActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_NAME, updateTime);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}
	//DEFAULT_EVENING Activity:NAME Update.
	public void UpdateDefaultEveningActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_EVENING_NAME, updateTime);
		db.update(TABLE_DEFAULT_EVENING_ACTIVITY, args, DEFAULT_EVENING_ID + "=" + id, null);
	}

	//EVENING Activity:DATE Update.
	public void UpdateEveningActivityDate(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_DATE, updateTime);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}
	//EVENING Activity:CATEGORY Update.
	public void UpdateEveningActivityCategory(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_CATEGORY, updateTime);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}



	//NIGHT Activity:Time Update.
	public void UpdateNightActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_TIME, updateTime);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}
	//DEFAULT_NIGHT Activity:Time Update.
	public void UpdateDefaultNightActivityTime(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NIGHT_TIME, updateTime);
		db.update(TABLE_DEFAULT_NIGHT_ACTIVITY, args, DEFAULT_NIGHT_ID + "=" + id, null);
	}

	//NIGHT Activity:Name Update.
	public void UpdateNightActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_NAME, updateTime);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}
	//DEFAULT_NIGHT Activity:Name Update.
	public void UpdateDefaultNightActivityName(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NIGHT_NAME, updateTime);
		db.update(TABLE_DEFAULT_NIGHT_ACTIVITY, args, DEFAULT_NIGHT_ID + "=" + id, null);
	}

	//NIGHT Activity:DATEUpdate.
	public void UpdateNightActivityDate(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_DATE, updateTime);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}
	//NIGHT Activity:CATEGORY Update.
	public void UpdateNightActivityCategory(String updateTime,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_CATEGORY, updateTime);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}


	//MORNING Activity:Points update.
	public void UpdateActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(ACTIVITY_POINTS, updatePoints);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}
	//DEFAULT_MORNING Activity:Points update.
	public void UpdateDefaultActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_ACTIVITY_POINTS, updatePoints);
		db.update(TABLE_DEFAULT_MORNING_ACTIVITY, args, DEFAULT_ACTIVITY_ID + "=" + id, null);
	}

	public void UpdateActivityMyPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(ACTIVITY_MY_POINTS, updatePoints);
		db.update(TABLE_MORNING_ACTIVITY, args, ACTIVITY_ID + "=" + id, null);
	}

	//NOON Activity:Points update.
	public void UpdateNoonActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_POINTS, updatePoints);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}
	//DefaultNOON Activity:Points update.
	public void UpdateDefaultNoonActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NOON_POINTS, updatePoints);
		db.update(TABLE_DEFAULT_NOON_ACTIVITY, args, DEFAULT_NOON_ID + "=" + id, null);
	}

	public void UpdateNoonActivityMyPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOON_MY_POINTS, updatePoints);
		db.update(TABLE_NOON_ACTIVITY, args, NOON_ID + "=" + id, null);
	}

	//EVENING Activity:Points update.
	public void UpdateEveningActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_POINTS, updatePoints);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}
	//DEFAULT_EVENING Activity:Points update.
	public void UpdateDefaultEveningActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_EVENING_POINTS, updatePoints);
		db.update(TABLE_DEFAULT_EVENING_ACTIVITY, args, DEFAULT_EVENING_ID + "=" + id, null);
	}

	public void UpdateEveningActivityMyPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(EVENING_MY_POINTS, updatePoints);
		db.update(TABLE_EVENING_ACTIVITY, args, EVENING_ID + "=" + id, null);
	}

	//NIGHT Activity:Points update.
	public void UpdateNightActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_POINTS, updatePoints);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}
	//DEFAULT_NIGHT Activity:Points update.
	public void UpdateDefaultNightActivityPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(DEFAULT_NIGHT_POINTS, updatePoints);
		db.update(TABLE_DEFAULT_NIGHT_ACTIVITY, args, DEFAULT_NIGHT_ID + "=" + id, null);
	}

	public void UpdateNightActivityMyPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NIGHT_MY_POINTS, updatePoints);
		db.update(TABLE_NIGHT_ACTIVITY, args, NIGHT_ID + "=" + id, null);
	}


	//Activity points: Update Morning Coloumn
	public void UpdateMorningPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_MORNING, updatePoints);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Activity points: Update Noon Coloumn
	public void UpdateNoonPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_NOON, updatePoints);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Activity points: Update Evening Coloumn
	public void UpdateEveningPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_EVENING, updatePoints);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Activity points: Update Night Coloumn
	public void UpdateNightPoints(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_NIGHT, updatePoints);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Activity points: Update Night Coloumn
	public void UpdatePointsMessage(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_MESSAGE, updatePoints);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Update Activity Points table:
	public void UpdateActivityPoints(int id, String ptsMorning,String ptsNoon,String ptsEvening,String ptsNight,
			String strMessage,String strTitle,String imgValue,String total,String sData)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(POINTS_MORNING, ptsMorning);
		args.put(POINTS_NOON, ptsNoon);
		args.put(POINTS_EVENING, ptsEvening);
		args.put(POINTS_NIGHT, ptsNight);
		args.put(POINTS_MESSAGE, strMessage);
		args.put(POINTS_TITLE, strTitle);
		args.put(POINTS_IMAGE, imgValue);
		args.put(POINTS_TOTAL, total);
		args.put(POINTS_DAY, sData);
		db.update(TABLE_ACTIVITY_POINTS, args, POINTS_ID + "=" + id, null);
	}

	//Update Notes Content:
	public void UpdateNotes(String date, String updatePoints,int id, String sAudio, String sPicture)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOTES_CONTENT, updatePoints);
		args.put(NOTES_DATE, date);
		args.put(NOTES_AUDIO, sAudio);
		args.put(NOTES_PICTURE, sPicture);
		db.update(TABLE_NOTES, args, NOTES_ID + "=" + id, null);
	}

	//Update Notes Content:
	public void UpdateNotesArchieve(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOTES_ARCHIEVE, updatePoints);
		db.update(TABLE_NOTES, args, NOTES_ID + "=" + id, null);
	}

	//Update Notes_ Content:
	public void UpdateNotes_(String date, String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(NOTES_CONTENT_, updatePoints);
		args.put(NOTES_DATE_, date);
		db.update(TABLE_NOTES_, args, NOTES_ID_ + "=" + id, null);
	}

	//Update TODO Check :
	public void UpdateTodoCheck(String updatePoints,int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues args = new ContentValues();
		args.put(TODO_ADDEDD, updatePoints);
		db.update(TABLE_TODO, args, TODO_ID + "=" + id, null);
	}
	
	//Update TODO Enable :
		public void UpdateTodoEnable(String updatePoints,int id)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues args = new ContentValues();
			args.put(TODO_ENABLE, updatePoints);
			db.update(TABLE_TODO, args, TODO_ID + "=" + id, null);
		}
	
	//Update TODO Points :
		public void UpdateTodoPoints(String updatePoints,int id)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues args = new ContentValues();
			args.put(TODO_POINTS, updatePoints);
			db.update(TABLE_TODO, args, TODO_ID + "=" + id, null);
		}
		
		//Update TODO DATE :
		public void UpdateTodoDate(String updatePoints,int id)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues args = new ContentValues();
			args.put(TODO_DATE, updatePoints);
			db.update(TABLE_TODO, args, TODO_ID + "=" + id, null);
		}


}
