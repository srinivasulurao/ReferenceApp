package com.sportsteamkarma.data.client;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sportsteamkarma.data.DataContract;

import java.util.ArrayList;
import java.util.List;

public final class DataClient {
    private static final String AUTHORITY = "com.sportsteamkarma.provider";

    ContentResolver contentResolver;

    public DataClient(Context context) {
        contentResolver = context.getContentResolver();
    }

    public List<String> getTeamNames() {
        String[] columns = {"team_name"};
        Uri uri = Uri.fromParts("content", "com.sportsteamkarma.provider/sport/1/league/1/team",
            null);
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);

        List<String> teamNames = new ArrayList<>();

        if (cursor != null) {
            cursor.moveToFirst();
            do {
                teamNames.add(cursor.getString(cursor.getColumnIndexOrThrow(
                    DataContract.Team.COLUMN_NAME_TEAM_NAME)));
            } while (cursor.moveToNext());
        }

        return teamNames;
    }
}
