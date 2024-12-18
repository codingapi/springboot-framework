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

    /**
     * 添加数据权限过滤器
     * @param filter 数据权限过滤器
     */
    public void addDataAuthorizationFilter(DataAuthorizationFilter filter) {
        this.filters.add(filter);
    }

    /**
     *  清空数据权限过滤器
     */
    public void clearDataAuthorizationFilters() {
        this.filters.clear();
    }

    /**
     * 列权限
     * @param interceptState 拦截状态
     * @param tableName 表名（或别名）
     * @param columnName 列名 （或别名）
     * @param value 值
     * @return T
     * @param <T> 泛型
     */
    public <T> T columnAuthorization(SQLInterceptState interceptState, String tableName, String columnName, T value) {
        if (interceptState != null && interceptState.hasIntercept()) {
            String realTableName = interceptState.getTableName(tableName);
            String realColumnName = interceptState.getColumnName(tableName,columnName);

            for (DataAuthorizationFilter filter : filters) {
                if (filter.supportColumnAuthorization(realTableName, realColumnName, value)) {
                    return filter.columnAuthorization(realTableName, realColumnName, value);
                }
            }
        }
        return value;
    }

    /**
     * 行权限
     * @param tableName 表名
     * @param tableAlias 别名
     * @return Condition 增加的过滤条件
     */
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
