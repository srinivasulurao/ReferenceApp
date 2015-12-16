package com.fivestarchicken.lms.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	 
    // Database Name
    private static final String DATABASE_NAME = "fivestarchicken_lms";
	//private final Context dbContext;

	/*public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}
	
*/
	public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 

	public void onCreate(SQLiteDatabase database) {

		database.execSQL(Table.User.CREATE_TABLE);
		//database.execSQL(Table.Exam.CREATE_TABLE);
		database.execSQL(Table.ExamModule.CREATE_TABLE);
		database.execSQL(Table.Question.CREATE_TABLE);
		database.execSQL(Table.Answer.CREATE_TABLE);
		database.execSQL(Table.Result.CREATE_TABLE);
		database.execSQL(Table.SyncStatus.CREATE_TABLE);
		database.execSQL(Table.ModuleDetail.CREATE_TABLE);
		database.execSQL(Table.Blog.CREATE_TABLE);
		database.execSQL(Table.BlogDetail.CREATE_TABLE);
		database.execSQL(Table.Configure.CREATE_TABLE);
		database.execSQL(Table.Certificate.CREATE_TABLE);
	}

	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {

		database.execSQL(Table.User.DROP_TABLE);
		//database.execSQL(Table.Exam.DROP_TABLE);
		database.execSQL(Table.ExamModule.DROP_TABLE);
		database.execSQL(Table.Question.DROP_TABLE);
		database.execSQL(Table.Answer.DROP_TABLE);
		database.execSQL(Table.Result.DROP_TABLE);
		database.execSQL(Table.SyncStatus.DROP_TABLE);
		database.execSQL(Table.ModuleDetail.DROP_TABLE);
		database.execSQL(Table.Blog.DROP_TABLE);
		database.execSQL(Table.BlogDetail.DROP_TABLE);
		database.execSQL(Table.Configure.DROP_TABLE);
		database.execSQL(Table.Certificate.DROP_TABLE);

	}




		
}
