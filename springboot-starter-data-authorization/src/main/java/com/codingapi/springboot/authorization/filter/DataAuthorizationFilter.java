package com.codingapi.springboot.authorization.filter;

import com.codingapi.springboot.authorization.handler.Condition;

/**
 * 数据权限过滤器
 */
public interface DataAuthorizationFilter {

    /**
     * 列权限过滤
     * @param tableName 表名
     * @param columnName 列名
     * @param value 值
     * @return 过滤后的值
     * @param <T> T
     */
    <T> T columnAuthorization(String tableName, String columnName,T value);

    /**
     * 行权限过滤
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 过滤后拦截sql条件
     */
    Condition rowAuthorization(String tableName, String tableAlias);

    /**
     * 是否支持列权限过滤
     * @param tableName 表名
     * @param columnName 列名
     * @param value 值
     * @return 是否支持
     */
    boolean supportColumnAuthorization(String tableName, String columnName, Object value);

    /**
     * 是否支持行权限过滤
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return 是否支持
     */
    boolean supportRowAuthorization(String tableName, String tableAlias);
}
