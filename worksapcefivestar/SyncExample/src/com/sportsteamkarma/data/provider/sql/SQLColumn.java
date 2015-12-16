package com.sportsteamkarma.data.provider.sql;

public final class SQLColumn {

    private String columnName;
    private String columnType;
    private boolean primaryKey = false;

    public SQLColumn(String columnName, String columnType, boolean primaryKey) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.primaryKey = primaryKey;
    }

    public SQLColumn(String columnName, String columnType) {
        this(columnName, columnType, false);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }
}
