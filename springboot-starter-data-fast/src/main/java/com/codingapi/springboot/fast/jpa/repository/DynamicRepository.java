package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.fast.jpa.JpaQueryContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
@SuppressWarnings("unchecked")
public interface DynamicRepository<T, ID> extends JpaRepository<T, ID> {

    default Class<?> getEntityClass() {
        ResolvableType resolvableType = ResolvableType.forClass(this.getClass()).as(DynamicRepository.class);
        return resolvableType.getGeneric(new int[]{0}).resolve();
    }

    default List<T> dynamicListQuery(String sql, Object... params) {
        return (List<T>) JpaQueryContext.getInstance().getJPAQuery().listQuery(getEntityClass(), sql, params);
    }

    default <V> List<V> dynamicListQuery(Class<V> clazz, String sql, Object... params) {
        return (List<V>) JpaQueryContext.getInstance().getJPAQuery().listQuery(clazz, sql, params);
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

}
