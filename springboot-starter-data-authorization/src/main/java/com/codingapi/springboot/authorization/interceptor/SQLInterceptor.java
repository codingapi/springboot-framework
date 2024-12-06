package com.codingapi.springboot.authorization.interceptor;

import java.sql.SQLException;

/**
 * SQL查询条件处理器
 */
public interface SQLInterceptor {


    /**
     * 前置处理
     *
     * @param sql sql
     * @return 是否处理
     */
    boolean beforeHandler(String sql);


    /**
     * 处理sql
     *
     * @param sql sql
     * @return 处理后的sql newSql
     * @throws SQLException
     */
    String postHandler(String sql) throws SQLException;


    /**
     * 后置处理
     * @param sql sql
     * @param newSql newSql
     * @param exception exception
     */
    void afterHandler(String sql, String newSql, SQLException exception);


}
