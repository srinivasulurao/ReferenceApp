package com.sportsteamkarma.data;

import android.provider.BaseColumns;

public final class DataContract {
    // If the database schema changes, this version must be incremented.
    public static final int DATABASE_VERSION = 6;

    private DataContract() {}

    public static abstract class Sport implements BaseColumns {
        public static final String TABLE_NAME = "sport";
        public static final String COLUMN_NAME_SPORT_NAME = "name";
    }

    public static abstract class League implements BaseColumns {
        public static final String TABLE_NAME = "league";
        public static final String COLUMN_NAME_SPORT_ID = "sport_id";
        public static final String COLUMN_NAME_LEAGUE_NAME = "name";
    }

    public static abstract class Team implements BaseColumns {
        public static final String TABLE_NAME = "team";
        public static final String COLUMN_NAME_LEAGUE_ID = "league_id";
        public static final String COLUMN_NAME_TEAM_NAME = "name";
    }


}
