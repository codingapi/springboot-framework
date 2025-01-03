package com.codingapi.springboot.authorization.interceptor;


import com.codingapi.springboot.authorization.enhancer.DataPermissionSQLEnhancer;
import com.codingapi.springboot.authorization.handler.RowHandler;
import com.codingapi.springboot.authorization.handler.RowHandlerContext;
import com.codingapi.springboot.authorization.properties.DataAuthorizationPropertyContext;
import com.codingapi.springboot.authorization.utils.SQLUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class DefaultSQLInterceptor implements SQLInterceptor {

    @Override
    public boolean beforeHandler(String sql) {
        return SQLUtils.isQuerySql(sql);
    }


    @Override
    public void afterHandler(String sql, String newSql, SQLException exception) {
        if(exception!=null){
            log.error("sql:{}",sql);
            log.error("newSql:{}",newSql);
            log.error(exception.getMessage(),exception);
        }
        if (DataAuthorizationPropertyContext.getInstance().showSql()) {
            log.info("newSql:{}", newSql);
        }
    }

    @Override
    public DataPermissionSQL postHandler(String sql) throws SQLException {
        RowHandler rowHandler = RowHandlerContext.getInstance().getRowHandler();
        DataPermissionSQLEnhancer sqlEnhancer = new DataPermissionSQLEnhancer(sql, rowHandler);
        return new DataPermissionSQL(sql, sqlEnhancer.getNewSQL(), sqlEnhancer.getTableAlias());
    }
}
