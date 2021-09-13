package com.codingapi.springboot.data.permission.sql.analyze;

public class SqlAnalyzer {

    private final String sql;

    public SqlAnalyzer(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        //todo analyzer sql
        return sql;
    }
}
