package com.sportsteamkarma.data.provider.sql;

import java.util.List;

public final class SQLiteHelper implements SQLHelper {

    @Override
    public String getTableCreateDDL(String tableName, List<SQLColumn> columns) {
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE ")
               .append(tableName)
               .append("(");

        int size = columns.size();
        for (int i = 0; i < size; i++) {
            SQLColumn column = columns.get(i);

            builder.append(column.getColumnName())
                   .append(" ")
                   .append(column.getColumnType());

            if (column.isPrimaryKey()) {
                builder.append(" PRIMARY KEY");
            }

            if (i < size - 1) {
                builder.append(",");
            }
        }

        builder.append(")");

        return builder.toString();
    }

    @Override
    public String getTableDropDDL(String tableName) {
        StringBuilder builder = new StringBuilder();
        builder.append("DROP TABLE IF EXISTS ")
               .append(tableName);
        return builder.toString();
    }

    @Override
    public String getSQLTypeInteger() {
        return "INTEGER";
    }

    @Override
    public String getSQLTypeString() {
        return "STRING";
    }
}
