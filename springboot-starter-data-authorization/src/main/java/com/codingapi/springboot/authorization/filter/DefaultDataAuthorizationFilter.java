package com.codingapi.springboot.authorization.filter;

import com.codingapi.springboot.authorization.handler.Condition;

public class DefaultDataAuthorizationFilter implements DataAuthorizationFilter{

    @Override
    public <T> T columnAuthorization(String tableName, String columnName, T value) {
        return value;
    }

    @Override
    public Condition rowAuthorization(String tableName, String tableAlias) {
        return null;
    }

    @Override
    public boolean supportColumnAuthorization(String tableName, String columnName, Object value) {
        return false;
    }

    @Override
    public boolean supportRowAuthorization(String tableName, String tableAlias) {
        return false;
    }

}
