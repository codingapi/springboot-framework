package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.fast.jdbc.JdbcQueryContext;
import com.codingapi.springboot.fast.jpa.SQLBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface DynamicNativeRepository<T, ID> extends BaseRepository<T, ID> {

    default List<Map<String, Object>> dynamicNativeListMapQuery(SQLBuilder<?> builder) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForMapList(builder);
    }

    default List<Map<String, Object>> dynamicNativeListMapQuery(String sql, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForMapList(sql, params);
    }

    default List<T> dynamicNativeListQuery(String sql, Object... params) {
        return dynamicNativeListQuery(getEntityClass(), sql, params);
    }

    default <V> List<V> dynamicNativeListQuery(Class<V> clazz, String sql, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForList(sql, clazz, params);
    }

    default <V> List<V> dynamicNativeListQuery(SQLBuilder<V> sqlBuilder) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForList(sqlBuilder);
    }

    default Page<T> dynamicNativePageQuery(String sql, String countSql, PageRequest request, Object... params) {
        return dynamicNativePageQuery(getEntityClass(), sql, countSql, request, params);
    }

    default Page<T> dynamicNativePageQuery(String sql, PageRequest request, Object... params) {
        return dynamicNativePageQuery(getEntityClass(), sql, request, params);
    }

    default <V> Page<V> dynamicNativePageQuery(Class<V> clazz, String sql, String countSql, PageRequest request, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForPage(sql, countSql, clazz, request, params);
    }

    default <V> Page<V> dynamicNativePageQuery(Class<V> clazz, String sql, PageRequest request, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForPage(sql, clazz, request, params);
    }

    default <V> Page<V> dynamicNativePageQuery(SQLBuilder<V> sqlBuilder, PageRequest request) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForPage(sqlBuilder, request);
    }

    default Page<Map<String, Object>> dynamicNativeMapPageMapQuery(SQLBuilder<?> sqlBuilder,PageRequest request) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForMapPage(sqlBuilder,request);
    }

    default Page<Map<String, Object>> dynamicNativeMapPageMapQuery(String sql, String countSql, PageRequest request, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForMapPage(sql, countSql, request, params);
    }

    default Page<Map<String, Object>> dynamicNativeMapPageMapQuery(String sql, PageRequest request, Object... params) {
        return JdbcQueryContext.getInstance().getJdbcQuery().queryForMapPage(sql, request, params);
    }

}
