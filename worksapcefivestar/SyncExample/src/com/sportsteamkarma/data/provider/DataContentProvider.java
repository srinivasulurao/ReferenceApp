package com.sportsteamkarma.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import com.sportsteamkarma.data.DataContract;


import java.util.List;

public class DataContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int SPORTS = 1;
    private static final int SPORT_ID = 2;
    private static final int LEAGUES = 3;
    private static final int LEAGUE_ID = 4;
    private static final int TEAMS = 5;
    private static final int TEAM_ID = 6;

    private static final String AUTHORITY = "com.sportsteamkarma.provider";

    static {
        uriMatcher.addURI(AUTHORITY, "sport", SPORTS);
        uriMatcher.addURI(AUTHORITY, "sport/#", SPORT_ID);
        uriMatcher.addURI(AUTHORITY, "sport/#/league", LEAGUES);
        uriMatcher.addURI(AUTHORITY, "sport/#/league/#", LEAGUE_ID);
        uriMatcher.addURI(AUTHORITY, "league/#/team", TEAMS);
        uriMatcher.addURI(AUTHORITY, "league/#/team/#", TEAM_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs,
        String sortOrder) {

        sortOrder = TextUtils.isEmpty(sortOrder) ? "_ID ASC" : sortOrder;

        List<String> segments;
        segments = uri.getPathSegments();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assert db != null;

        switch (uriMatcher.match(uri)) {
            case SPORTS:
                return db.query(DataContract.Sport.TABLE_NAME, columns, selection, selectionArgs,
                        null, null, sortOrder);
            case SPORT_ID:
                assert segments != null;
                return db.query(DataContract.Sport.TABLE_NAME, columns,
                        buildSelection(DataContract.Sport._ID + "=?", selection),
                        buildSelectionArgs(new String[] {segments.get(1)}, selectionArgs),
                        null, null, sortOrder);
            case LEAGUES:
                assert segments != null;
                return db.query(DataContract.League.TABLE_NAME, columns,
                        buildSelection(DataContract.Sport._ID + "=?", selection),
                        buildSelectionArgs(new String[] {segments.get(1)}, selectionArgs),
                        null, null, sortOrder);
            case LEAGUE_ID:
                assert segments != null;
                return db.query(DataContract.League.TABLE_NAME, columns,
                        buildSelection(DataContract.League.COLUMN_NAME_SPORT_ID + "=? AND " +
                                DataContract.League._ID + "=?", selection),
                        buildSelectionArgs(
                                new String[] {segments.get(1), segments.get(3)}, selectionArgs),
                        null, null, sortOrder);
            case TEAMS:
                assert segments != null;
                return db.query(DataContract.Team.TABLE_NAME, columns,
                        buildSelection(DataContract.Team.COLUMN_NAME_LEAGUE_ID + "=?", selection),
                        buildSelectionArgs(new String[] {segments.get(1)}, selectionArgs),
                        null, null, sortOrder);
            case TEAM_ID:
                assert segments != null;
                return db.query(DataContract.Team.TABLE_NAME, columns,
                        buildSelection(DataContract.Team.COLUMN_NAME_LEAGUE_ID + "=? AND " +
                                DataContract.Team._ID + "=?", selection),
                        buildSelectionArgs(new String[] {segments.get(1), segments.get(3)},
                                selectionArgs),
                        null, null, sortOrder);
            default:
                throw new RuntimeException("No content provider URI match.");
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SPORTS:
                return "vnd.android.cursor.dir/vnd.com.sportsteamkarma.provider.sport";
            case SPORT_ID:
                return "vnd.android.cursor.item/vnd.com.sportsteamkarma.provider.sport";
            case LEAGUES:
                return "vnd.android.cursor.dir/vnd.com.sportsteamkarma.provider.league";
            case LEAGUE_ID:
                return "vnd.android.cursor.item/vnd.com.sportsteamkarma.provider.league";
            case TEAMS:
                return "vnd.android.cursor.dir/vnd.com.sportsteamkarma.provider.team";
            case TEAM_ID:
                return "vnd.android.cursor.item/vnd.com.sportsteamkarma.provider.team";
            default:
                throw new RuntimeException("No content provider URI match.");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        List<String> segments;
        segments = uri.getPathSegments();
        assert segments != null;
        String id;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assert db != null;

        switch (uriMatcher.match(uri)) {
            case SPORT_ID:
                id = segments.get(0);
                contentValues.put(DataContract.Sport._ID, id);
                db.insertOrThrow(DataContract.Sport.TABLE_NAME, null, contentValues);
                return Uri.parse("content://" + AUTHORITY + "/sport/" + id);
            case LEAGUE_ID:
                String sportId = segments.get(0);
                id = segments.get(1);
                contentValues.put(DataContract.League.COLUMN_NAME_SPORT_ID, sportId);
                contentValues.put(DataContract.League._ID, id);
                db.insertOrThrow(DataContract.League.TABLE_NAME, null, contentValues);
                return Uri.parse("content://" + AUTHORITY + "/sport/" + sportId + "/league/" + id);
            case TEAM_ID:
                String leagueId = segments.get(0);
                id = segments.get(1);
                contentValues.put(DataContract.Team.COLUMN_NAME_LEAGUE_ID, leagueId);
                contentValues.put(DataContract.Team._ID, id);
                db.insertOrThrow(DataContract.Team.TABLE_NAME, null, contentValues);
                return Uri.parse("content://" + AUTHORITY + "/league/" + leagueId + "/team/" + id);
            default:
                throw new RuntimeException("No content provider URI match.");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        List<String> segments;
        segments = uri.getPathSegments();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assert db != null;

        switch (uriMatcher.match(uri)) {
            case SPORTS:
                return db.delete(DataContract.Sport.TABLE_NAME, null, null);
            case SPORT_ID:
                assert segments != null;
                return db.delete(DataContract.Sport.TABLE_NAME,
                        buildSelection(DataContract.Sport._ID + "=?", selection),
                        buildSelectionArgs(new String[]{segments.get(0)}, selectionArgs));
            case LEAGUES:
                assert segments != null;
                return db.delete(DataContract.League.TABLE_NAME,
                        buildSelection(DataContract.Sport._ID + "=?", selection),
                        buildSelectionArgs(new String[]{segments.get(0)}, selectionArgs));
            case LEAGUE_ID:
                assert segments != null;
                return db.delete(DataContract.League.TABLE_NAME,
                        buildSelection(DataContract.League.COLUMN_NAME_SPORT_ID + "=? AND " +
                                DataContract.League._ID + "=?", selection),
                        buildSelectionArgs(
                                new String[]{segments.get(0), segments.get(1)}, selectionArgs));
            case TEAMS:
                assert segments != null;
                return db.delete(DataContract.Team.TABLE_NAME,
                        buildSelection(DataContract.Team.COLUMN_NAME_LEAGUE_ID + "=?", selection),
                        buildSelectionArgs(new String[]{segments.get(0)}, selectionArgs));
            case TEAM_ID:
                assert segments != null;
                return db.delete(DataContract.Team.TABLE_NAME,
                        buildSelection(DataContract.Team.COLUMN_NAME_LEAGUE_ID + "=? AND " +
                                DataContract.Team._ID + "=?", selection),
                        buildSelectionArgs(new String[]{segments.get(0), segments.get(1)},
                                selectionArgs));
            default:
                throw new RuntimeException("No content provider URI match.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private String buildSelection(String baseSelection, String selection) {
        if (TextUtils.isEmpty(selection)) {
            return baseSelection;
        }
        return TextUtils.concat(baseSelection, " AND (", selection, ")").toString();
    }

    private String[] buildSelectionArgs(String[] baseArgs, String[] selectionArgs) {
        if (selectionArgs == null || selectionArgs.length == 0) {
            return baseArgs;
        }
        return ArrayUtils.addAll(baseArgs, selectionArgs);
    }
}
