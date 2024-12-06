package com.codingapi.springboot.authorization.handler;

import com.codingapi.springboot.authorization.DataAuthorizationContext;

public class DefaultRowHandler implements RowHandler {

    @Override
    public Condition handler(String subSql, String tableName, String tableAlias) {
        return DataAuthorizationContext.getInstance().rowAuthorization(tableName, tableAlias);
    }
}
