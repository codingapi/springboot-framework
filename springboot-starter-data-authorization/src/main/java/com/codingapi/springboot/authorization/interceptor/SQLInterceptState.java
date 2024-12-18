package com.codingapi.springboot.authorization.interceptor;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * SQL拦截状态
 */
public class SQLInterceptState {

    private final boolean state;

    private final String sql;

    private final String newSql;

    private final Map<String,String> tableAlias;

    private SQLInterceptState(boolean state, String sql, String newSql, Map<String, String> tableAlias) {
        this.state = state;
        this.sql = sql;
        this.newSql = newSql;
        this.tableAlias = tableAlias;
    }

    /**
     * 拦截
     */
    public static SQLInterceptState intercept(String sql, String newSql,Map<String, String> tableAlias) {
        return new SQLInterceptState(true, sql, newSql,tableAlias);
    }

    /**
     * 不拦截
     */
    public static SQLInterceptState unIntercept(String sql) {
        return new SQLInterceptState(false, sql, sql,null);
    }


    public String getTableName(String tableName){
        if(tableAlias!=null) {
            String value = tableAlias.get(tableName);
            if(StringUtils.hasText(value)){
                return value;
            }else {
                return tableName;
            }
        }
        return tableName;
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
