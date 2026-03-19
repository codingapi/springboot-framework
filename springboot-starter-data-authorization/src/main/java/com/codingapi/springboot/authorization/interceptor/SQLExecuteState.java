package com.codingapi.springboot.authorization.interceptor;

import com.codingapi.springboot.authorization.enhancer.TableColumnAliasContext;

/**
 * SQL拦截状态
 */
public class SQLExecuteState {

    /**
     * 是否拦截SQL
     */
    private final boolean intercept;

    /**
     * 原始SQL
     */
    private final String sql;

    /**
     * 处理后的SQL
     */
    private final String newSql;

    /**
     * 表字段别名对象
     */
    private final TableColumnAliasContext aliasContext;

    private SQLExecuteState(boolean intercept, String sql, String newSql, TableColumnAliasContext aliasContext) {
        this.intercept = intercept;
        this.sql = sql;
        this.newSql = newSql;
        this.aliasContext = aliasContext;
    }

    /**
     * 拦截
     */
    public static SQLExecuteState intercept(String sql, String newSql, TableColumnAliasContext aliasContext) {
        return new SQLExecuteState(true, sql, newSql, aliasContext);
    }

    /**
     * 不拦截
     */
    public static SQLExecuteState unIntercept(String sql) {
        return new SQLExecuteState(false, sql, sql, null);
    }

    public String getTableName(String tableName) {
        return aliasContext.getTableName(tableName);
    }

    public String getColumnName(String tableName, String columnName) {
        return aliasContext.getColumnName(tableName, columnName);
    }

    public String getSql() {
        if (intercept) {
            return newSql;
        } else {
            return sql;
        }
    }

    public boolean hasIntercept() {
        return intercept;
    }


}
