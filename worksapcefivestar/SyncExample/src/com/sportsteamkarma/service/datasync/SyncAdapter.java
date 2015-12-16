package com.sportsteamkarma.service.datasync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.JsonReader;
import android.util.Log;

import com.sportsteamkarma.data.DataContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String AUTHORITY = "com.sportsteamkarma.provider";
    private static final String PREFIX = "content://" + AUTHORITY + "/";
    private static final String TAG = "Sync";

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient contentProviderClient, SyncResult syncResult) {

        // naive implementation, delete replace everything

        Log.i(TAG, "starting sync");

        SyncResult result = new SyncResult();

        try {
            deleteSports(contentProviderClient);
            insertSports(contentProviderClient);
        } catch (RemoteException | IOException e) {
            syncResult.hasHardError();
            Log.e(TAG, e.getMessage(), e);
        }

        Log.i(TAG, "done sync");
    }

    private void deleteSports(ContentProviderClient contentProviderClient) throws RemoteException {
        Cursor cursor = contentProviderClient.query(Uri.parse(PREFIX + "/sport"),
                new String[] {DataContract.Sport._ID}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long sportId = cursor.getLong(0);
                deleteSport(sportId, contentProviderClient);
            } while (cursor.moveToNext());
        }
    }

    private void deleteSport(long sportId, ContentProviderClient contentProviderClient) throws RemoteException {
        // delete all of the leagues in sport sportId
        Log.i(TAG, "deleting sport: " + sportId);
        Cursor cursor = contentProviderClient.query(Uri.parse(PREFIX + "/sport/" + sportId + "/league"),
                new String[] {DataContract.League._ID}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long leagueId = cursor.getLong(0);
                deleteLeague(leagueId, contentProviderClient);
            } while (cursor.moveToNext());

            // delete sport sportId
            contentProviderClient.delete(Uri.parse(PREFIX + "/sport/" + sportId), null, null);
        }
    }

    /**
     * Delete all the teams in league leagueId
     */
    private void deleteLeague(long leagueId, ContentProviderClient contentProviderClient) throws RemoteException {
        contentProviderClient.delete(Uri.parse(PREFIX + "/league/" + leagueId + "/team"), null, null);
    }

    private void insertSports(ContentProviderClient contentProviderClient) throws RemoteException, IOException {
        URL url = new URL("http", "192.168.117.166", 3000, "/api/sports");
        URLConnection conn = url.openConnection();

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
             JsonReader reader = new JsonReader(bufReader)) {

            reader.beginArray();

            Long id = null;
            String sportName = null;

            while (reader.hasNext()) {
                reader.beginObject();

                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextLong();
                    } else if (name.equals("name")) {
                        sportName = reader.nextString();
                    }
                }

                reader.endObject();

                ContentValues contentValues = new ContentValues();
                contentValues.put(DataContract.Sport.COLUMN_NAME_SPORT_NAME, sportName);

                Log.i(TAG, "inserting sport " + contentValues);
                contentProviderClient.insert(Uri.parse(PREFIX + "/sport/" + id), contentValues);
                Log.i(TAG, "inserted sport " + contentValues);
            }
            reader.endArray();
        }
    }
}
