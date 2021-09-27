package com.codingapi.springboot.permission.db;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
public class DBTable {

    private final static String TABLE_NAME = "TABLE_NAME";

    private final Connection connection;

    public DBTable(Connection connection) {
        this.connection = connection;
    }

    public List<String> tableNames() throws SQLException {
        List<String> list = new ArrayList<>();
        DatabaseMetaData metaData =  connection.getMetaData();
        ResultSet rs =  metaData.getTables(
                null,
                null,
                "%",
                null);

        while (rs.next()){
            String tableName = rs.getString(TABLE_NAME);
            list.add(tableName);
        }

        DbUtils.close(rs);
        DbUtils.close(connection);
        return list;
    }
}
