package com.codingapi.springboot.authorization;

import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.Condition;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptState;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据权限上下文
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

    public void clearDataAuthorizationFilters() {
        this.filters.clear();
    }

    public <T> T columnAuthorization(SQLInterceptState interceptState, String tableName, String columnName, T value) {
        if (interceptState != null && interceptState.hasIntercept()) {
//            String realTableName = interceptState.getTableName(tableName);
            for (DataAuthorizationFilter filter : filters) {
                if (filter.supportColumnAuthorization(tableName, columnName, value)) {
                    return filter.columnAuthorization(tableName, columnName, value);
                }
            }
        }
        return value;
    }

    public Condition rowAuthorization(String tableName, String tableAlias) {
        if (StringUtils.hasText(tableName) && StringUtils.hasText(tableAlias)) {
            for (DataAuthorizationFilter filter : filters) {
                if (filter.supportRowAuthorization(tableName, tableAlias)) {
                    return filter.rowAuthorization(tableName, tableAlias);
                }
            }
        }
        return null;
    }

}
