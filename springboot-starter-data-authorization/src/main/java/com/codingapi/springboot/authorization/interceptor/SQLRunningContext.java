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

    private SQLRunningContext() {
    }

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
                DataPermissionSQL dataPermissionSQL = sqlInterceptor.postHandler(sql);
                sqlInterceptor.afterHandler(sql, dataPermissionSQL.getNewSql(), null);
                return SQLInterceptState.intercept(sql, dataPermissionSQL.getNewSql(),dataPermissionSQL.getTableAlias());
            } catch (SQLException exception) {
                sqlInterceptor.afterHandler(sql, null, exception);
                throw exception;
            } finally {
                // 重置拦截器状态
                skipInterceptor.set(false);
            }
        }
        return SQLInterceptState.unIntercept(sql);
    }


    /**
     * 跳过数据权限拦截
     * @param supplier 业务逻辑
     * @return T
     * @param <T> T
     */
    public <T> T skipDataAuthorization(java.util.function.Supplier<T> supplier) {
        try {
            skipInterceptor.set(true);
            return (T) supplier.get();
        } finally {
            skipInterceptor.set(false);
        }
    }

}
