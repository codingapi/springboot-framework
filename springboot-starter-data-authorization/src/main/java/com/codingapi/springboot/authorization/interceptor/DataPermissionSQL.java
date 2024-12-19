package com.codingapi.springboot.authorization.interceptor;

import com.codingapi.springboot.authorization.enhancer.TableColumnAliasContext;
import lombok.Getter;

@Getter
public class DataPermissionSQL {

    private final String sql;
    private final String newSql;
    private final TableColumnAliasContext aliasContext;

    public DataPermissionSQL(String sql, String newSql, TableColumnAliasContext aliasContext) {
        this.sql = sql;
        this.newSql = newSql;
        this.aliasContext = aliasContext;
    }
}
