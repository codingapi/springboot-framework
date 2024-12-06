package com.codingapi.springboot.authorization.interceptor;

import lombok.Getter;

import java.sql.SQLException;

/**
 * SQLRunningContext SQL执行拦截上下文
 */
public class SQLRunningContext {

    @Getter
    private final static SQLRunningContext instance = new SQLRunningContext();

    private SQLRunningContext() {
    }

    public SQLInterceptState intercept(String sql) throws SQLException {
        SQLInterceptor sqlInterceptor = SQLInterceptorContext.getInstance().getSqlInterceptor();
        if (sqlInterceptor.beforeHandler(sql)) {
            try {
                String newSql = sqlInterceptor.postHandler(sql);
                sqlInterceptor.afterHandler(sql, newSql, null);
                return SQLInterceptState.intercept(sql, newSql);
            } catch (SQLException exception) {
                sqlInterceptor.afterHandler(sql, null, exception);
                throw exception;
            }
        }
        return SQLInterceptState.unIntercept(sql);
    }
}
