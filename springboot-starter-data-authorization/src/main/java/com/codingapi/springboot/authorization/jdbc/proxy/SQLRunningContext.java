package com.codingapi.springboot.authorization.jdbc.proxy;

import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptor;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptorContext;
import lombok.Getter;

import java.sql.SQLException;
import java.util.function.Supplier;

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
    SQLInterceptState intercept(String sql) throws SQLException {
        SQLInterceptor sqlInterceptor = SQLInterceptorContext.getInstance().getSqlInterceptor();

        if (skipInterceptor.get()) {
            return SQLInterceptState.unIntercept(sql);
        }

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


    /**
     * 执行SQL查询 （非拦截模型执行）
     *
     * @param supplier 业务逻辑
     * @param <T>      T
     * @return T
     */
    public <T> T run(Supplier<T> supplier) {
        try {
            skipInterceptor.set(true);
            return supplier.get();
        } finally {
            skipInterceptor.set(false);
        }
    }
}
