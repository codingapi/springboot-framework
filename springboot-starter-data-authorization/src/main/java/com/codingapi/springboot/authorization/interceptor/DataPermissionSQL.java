package com.codingapi.springboot.authorization.interceptor;

import lombok.Getter;

import java.util.Map;

@Getter
public class DataPermissionSQL {

    private final String sql;
    private final String newSql;
    private final Map<String,String> tableAlias;

    public DataPermissionSQL(String sql, String newSql, Map<String, String> tableAlias) {
        this.sql = sql;
        this.newSql = newSql;
        this.tableAlias = tableAlias;
    }
}
