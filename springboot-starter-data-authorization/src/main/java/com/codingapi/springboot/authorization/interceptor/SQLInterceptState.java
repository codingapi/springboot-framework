package com.codingapi.springboot.authorization.interceptor;

/**
 * SQL拦截状态
 */
public class SQLInterceptState {

    private final boolean state;

    private final String sql;

    private final String newSql;

    private SQLInterceptState(boolean state, String sql, String newSql) {
        this.state = state;
        this.sql = sql;
        this.newSql = newSql;
    }

    /**
     * 拦截
     */
    public static SQLInterceptState intercept(String sql, String newSql) {
        return new SQLInterceptState(true, sql, newSql);
    }

    /**
     * 不拦截
     */
    public static SQLInterceptState unIntercept(String sql) {
        return new SQLInterceptState(false, sql, sql);
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
