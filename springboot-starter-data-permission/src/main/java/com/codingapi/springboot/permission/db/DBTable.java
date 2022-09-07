package com.codingapi.springboot.permission.db;

import com.codingapi.springboot.permission.db.template.SQLTemplate;
import com.codingapi.springboot.permission.db.template.SQLTemplateContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
public class DBTable {

    private final static String TABLE_NAME = "TABLE_NAME";

    private final static String PREFIX_NAME = "PERMISSION_";

    private final Connection connection;

    private final SQLTemplate sqlTemplate;

    public DBTable(Connection connection) throws SQLException {
        this.connection = connection;
        String driverName = connection.getMetaData().getDriverName();
        this.sqlTemplate = SQLTemplateContext.getInstance().template(driverName);
        if (sqlTemplate == null) {
            throw new SQLException(String.format("not exits %s SQLTemplate", driverName));
        }
    }

    public void scanner() throws SQLException {
        List<String> tableNames = this.tableNames();
        log.info("tableNames:{}", tableNames);
        log.info("driver:{}", connection.getMetaData().getDriverName());
        for (String tableName : tableNames) {
            try {
                createTable(tableName);
            } catch (Exception e) {
                log.warn("create table error: {}.", e.getLocalizedMessage());
            }
        }
    }

    private void createTable(String tableName) throws SQLException {
        String sql = sqlTemplate.create(loadTableName(tableName));
        log.info("sql=>{}", sql);
        Statement statement = connection.createStatement();
        statement.execute(sql);
        DbUtils.close(statement);
    }

    private String loadTableName(String tableName) {
        return String.format("%s%s", PREFIX_NAME, tableName);
    }

    private List<String> tableNames() throws SQLException {
        List<String> list = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getTables(
                null,
                null,
                "%",
                null);

        while (rs.next()) {
            String tableName = rs.getString(TABLE_NAME);
            list.add(tableName);
        }

        DbUtils.close(rs);
        return list;
    }

    public void close() throws SQLException {
        DbUtils.close(connection);
    }
}
