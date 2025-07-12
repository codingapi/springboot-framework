package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import com.codingapi.springboot.fast.jpa.SQLBuilder;
import com.codingapi.springboot.fast.jpa.map.MapViewResult;
import com.codingapi.springboot.fast.jpa.map.QueryColumns;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
@SuppressWarnings("unchecked")
public interface DynamicRepository<T, ID> extends BaseRepository<T, ID> {

    default List<T> dynamicListQuery(SQLBuilder builder) {
        return (List<T>) JpaQueryContext.getInstance().getJPAQuery().listQuery(builder);
    }

    default List<T> dynamicListQuery(String sql, Object... params) {
        return (List<T>) JpaQueryContext.getInstance().getJPAQuery().listQuery(getEntityClass(), sql, params);
    }

    default <V> List<V> dynamicListQuery(Class<V> clazz, String sql, Object... params) {
        return (List<V>) JpaQueryContext.getInstance().getJPAQuery().listQuery(clazz, sql, params);
    }

    default Page<T> dynamicPageQuery(SQLBuilder builder, PageRequest request) {
        return (Page<T>) JpaQueryContext.getInstance().getJPAQuery().pageQuery(builder, request);
    }

    default Page<T> dynamicPageQuery(String sql, String countSql, PageRequest request, Object... params) {
        return (Page<T>) JpaQueryContext.getInstance().getJPAQuery().pageQuery(getEntityClass(), sql, countSql, request, params);
    }

    default Page<T> dynamicPageQuery(String sql, PageRequest request, Object... params) {
        return (Page<T>) JpaQueryContext.getInstance().getJPAQuery().pageQuery(getEntityClass(), sql, request, params);
    }

    default <V> Page<V> dynamicPageQuery(Class<V> clazz, String sql, String countSql, PageRequest request, Object... params) {
        return (Page<V>) JpaQueryContext.getInstance().getJPAQuery().pageQuery(clazz, sql, countSql, request, params);
    }

    default Page<MapViewResult> dynamicMapPageQuery(QueryColumns columns, String sql, String countSql, PageRequest request, Object... params) {
        return JpaQueryContext.getInstance().getJPAQuery().pageMapQuery(columns, sql, countSql, request, params);
    }

    default  List<MapViewResult> dynamicMapListQuery(QueryColumns columns, String sql, Object... params) {
        return (List<MapViewResult>) JpaQueryContext.getInstance().getJPAQuery().listMapQuery(columns, sql, params);
    }

}
