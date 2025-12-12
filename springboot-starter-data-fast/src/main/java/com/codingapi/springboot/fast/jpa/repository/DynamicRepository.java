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
public interface DynamicRepository<T, ID> extends BaseRepository<T, ID> {

    default <V> List<V> dynamicListQuery(SQLBuilder<V> builder) {
        return JpaQueryContext.getInstance().getJpaQuery().listQuery(builder);
    }

    default List<T> dynamicListQuery(String sql, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().listQuery(getEntityClass(), sql, params);
    }

    default <V> List<V> dynamicListQuery(Class<V> clazz, String sql, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().listQuery(clazz, sql, params);
    }

    default <V> Page<V> dynamicPageQuery(SQLBuilder<V> builder, PageRequest request) {
        return JpaQueryContext.getInstance().getJpaQuery().pageQuery(builder, request);
    }

    default Page<T> dynamicPageQuery(String sql, String countSql, PageRequest request, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().pageQuery(getEntityClass(), sql, countSql, request, params);
    }

    default Page<T> dynamicPageQuery(String sql, PageRequest request, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().pageQuery(getEntityClass(), sql, request, params);
    }

    default <V> Page<V> dynamicPageQuery(Class<V> clazz, String sql, String countSql, PageRequest request, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().pageQuery(clazz, sql, countSql, request, params);
    }

    default Page<MapViewResult> dynamicMapPageQuery(QueryColumns columns, String sql, String countSql, PageRequest request, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().pageMapQuery(columns, sql, countSql, request, params);
    }

    default List<MapViewResult> dynamicMapListQuery(QueryColumns columns, String sql, Object... params) {
        return JpaQueryContext.getInstance().getJpaQuery().listMapQuery(columns, sql, params);
    }

}
