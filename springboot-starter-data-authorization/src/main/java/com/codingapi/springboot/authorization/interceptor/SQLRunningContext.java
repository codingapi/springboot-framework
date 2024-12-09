package com.codingapi.springboot.authorization.interceptor;

import lombok.Getter;

import java.sql.SQLException;

/**
 * SQLRunningContext SQL执行拦截上下文
 */
public class SQLRunningContext {

    @Getter
    private final static SQLRunningContext instance = new SQLRunningContext();

    private final ThreadLocal<Boolean> skipInterceptor = ThreadLocal.withInitial(() -> false);

    private SQLRunningContext() {}

    /**
     * 拦截SQL
     *
     * @param sql sql
     * @return SQLInterceptState
     * @throws SQLException SQLException
     */
    public SQLInterceptState intercept(String sql) throws SQLException {
        SQLInterceptor sqlInterceptor = SQLInterceptorContext.getInstance().getSqlInterceptor();

        if (skipInterceptor.get()) {
            return SQLInterceptState.unIntercept(sql);
        }

        if (sqlInterceptor.beforeHandler(sql)) {
            // 在拦截器中执行的查询操作将不会被拦截
            skipInterceptor.set(true);
            try {
                String newSql = sqlInterceptor.postHandler(sql);
                sqlInterceptor.afterHandler(sql, newSql, null);
                return SQLInterceptState.intercept(sql, newSql);
            } catch (SQLException exception) {
                sqlInterceptor.afterHandler(sql, null, exception);
                throw exception;
            }finally {
                // 重置拦截器状态
                skipInterceptor.set(false);
            }
        }
        return SQLInterceptState.unIntercept(sql);
    }




}
