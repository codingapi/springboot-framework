package com.codingapi.springboot.authorization.handler;

/**
 *  行数据权限处理器
 */
public interface RowHandler {

    /**
     * 查询条件拦截
     *
     * @param subSql     查询子SQL语句
     * @param tableName  表名
     * @param tableAlias 表别名
     * @return 条件语句
     */
    Condition handler(String subSql, String tableName, String tableAlias);


}
