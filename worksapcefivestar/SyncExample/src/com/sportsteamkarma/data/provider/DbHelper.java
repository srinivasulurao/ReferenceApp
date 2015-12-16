package com.sportsteamkarma.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sportsteamkarma.data.DataContract;
import com.sportsteamkarma.data.provider.sql.SQLDDL;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SportsTeamKarma.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DataContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (String stmt : SQLDDL.getSQLTableCreateStatements()) {
            sqLiteDatabase.execSQL(stmt);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // drop everything and recreate

        for (String stmt : SQLDDL.getSQLTableDropStatements()) {
            sqLiteDatabase.execSQL(stmt);
        }

        onCreate(sqLiteDatabase);
    }


}
