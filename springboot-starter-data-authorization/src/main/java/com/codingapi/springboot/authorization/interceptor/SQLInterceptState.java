package com.codingapi.springboot.authorization.interceptor;

import com.codingapi.springboot.authorization.enhancer.TableColumnAliasContext;

/**
 * SQL拦截状态
 */
public class SQLInterceptState {

    private final boolean state;

    private final String sql;

    private final String newSql;

    private final TableColumnAliasContext aliasContext;

    private SQLInterceptState(boolean state, String sql, String newSql, TableColumnAliasContext aliasContext) {
        this.state = state;
        this.sql = sql;
        this.newSql = newSql;
        this.aliasContext = aliasContext;
    }

    /**
     * 拦截
     */
    public static SQLInterceptState intercept(String sql, String newSql, TableColumnAliasContext aliasContext) {
        return new SQLInterceptState(true, sql, newSql, aliasContext);
    }

    /**
     * 不拦截
     */
    public static SQLInterceptState unIntercept(String sql) {
        return new SQLInterceptState(false, sql, sql, null);
    }

    public String getTableName(String tableName) {
        return aliasContext.getTableName(tableName);
    }

    public String getColumnName(String tableName, String columnName) {
        return aliasContext.getColumnName(tableName, columnName);
    }

    public String getSql() {
        if (state) {
            return newSql;
        } else {
            return sql;
        }
    }

    public boolean hasIntercept() {
        return state;
    }


}
