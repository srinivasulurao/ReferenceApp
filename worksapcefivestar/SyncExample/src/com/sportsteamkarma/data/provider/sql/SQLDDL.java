package com.sportsteamkarma.data.provider.sql;

import static com.sportsteamkarma.data.DataContract.Sport;
import static com.sportsteamkarma.data.DataContract.League;
import static com.sportsteamkarma.data.DataContract.Team;

import java.util.ArrayList;
import java.util.List;

public final class SQLDDL {

    private SQLDDL() {}

    public static List<String> getSQLTableCreateStatements() {
        List<String> statements = new ArrayList<>();
        statements.add(getCreateSportTableDDL());
        statements.add(getCreateLeagueTableDDL());
        statements.add(getCreateTeamTableDDL());
        return statements;
    }

    public static List<String> getSQLTableDropStatements() {
        SQLHelper sqlHelper = new SQLiteHelper();

        List<String> statements = new ArrayList<>();
        statements.add(sqlHelper.getTableDropDDL(Sport.TABLE_NAME));
        statements.add(sqlHelper.getTableDropDDL(League.TABLE_NAME));
        statements.add(sqlHelper.getTableDropDDL(Team.TABLE_NAME));
        return statements;
    }

    private static String getCreateSportTableDDL() {
        SQLHelper sqlHelper = new SQLiteHelper();

        List<SQLColumn> columns = new ArrayList<>();
        columns.add(new SQLColumn(Sport._ID, sqlHelper.getSQLTypeInteger()));
        columns.add(new SQLColumn(Sport.COLUMN_NAME_SPORT_NAME, sqlHelper.getSQLTypeString()));

        return sqlHelper.getTableCreateDDL(Sport.TABLE_NAME, columns);
    }

    private static String getCreateLeagueTableDDL() {
        SQLHelper sqlHelper = new SQLiteHelper();

        List<SQLColumn> columns = new ArrayList<>();
        columns.add(new SQLColumn(League._ID, sqlHelper.getSQLTypeInteger()));
        columns.add(new SQLColumn(League.COLUMN_NAME_LEAGUE_NAME, sqlHelper.getSQLTypeString()));

        return sqlHelper.getTableCreateDDL(League.TABLE_NAME, columns);
    }

    private static String getCreateTeamTableDDL() {
        SQLHelper sqlHelper = new SQLiteHelper();

        List<SQLColumn> columns = new ArrayList<>();
        columns.add(new SQLColumn(Team._ID, sqlHelper.getSQLTypeInteger()));
        columns.add(new SQLColumn(Team.COLUMN_NAME_TEAM_NAME, sqlHelper.getSQLTypeString()));

        return sqlHelper.getTableCreateDDL(Team.TABLE_NAME, columns);
    }
}
