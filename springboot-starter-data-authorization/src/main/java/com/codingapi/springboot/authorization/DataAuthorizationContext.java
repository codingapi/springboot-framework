package com.codingapi.springboot.authorization;

import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *  数据权限上下文
 */
public class DataAuthorizationContext {

    @Getter
    private final static DataAuthorizationContext instance = new DataAuthorizationContext();

    private final List<DataAuthorizationFilter> filters;

    private DataAuthorizationContext() {
        this.filters = new ArrayList<>();
    }

    public void addDataAuthorizationFilter(DataAuthorizationFilter filter) {
        this.filters.add(filter);
    }

    public <T> T columnAuthorization(String tableName, String columnName, T value) {
        for (DataAuthorizationFilter filter : filters) {
            if (filter.supportColumnAuthorization(tableName, columnName, value)) {
                return filter.columnAuthorization(tableName, columnName, value);
            }
        }
        return value;
    }

    public Condition rowAuthorization(String tableName, String tableAlias) {
        for (DataAuthorizationFilter filter : filters) {
            if (filter.supportRowAuthorization(tableName, tableAlias)) {
                return filter.rowAuthorization(tableName, tableAlias);
            }
        }
        return null;
    }

}
