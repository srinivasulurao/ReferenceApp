package com.sportsteamkarma.data.provider.sql;

import java.util.List;

public interface SQLHelper {

    public String getTableCreateDDL(String tableName, List<SQLColumn> columns);

    public String getTableDropDDL(String tableName);

    public String getSQLTypeInteger();

    public String getSQLTypeString();

}
