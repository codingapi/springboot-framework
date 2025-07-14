package com.codingapi.springboot.fast.jpa;

import com.codingapi.springboot.fast.jpa.map.QueryColumnsContext;
import com.codingapi.springboot.fast.jpa.map.QueryColumns;
import com.codingapi.springboot.fast.jpa.map.MapViewResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@AllArgsConstructor
public class JPAQuery {

    private final EntityManager entityManager;

    public <T> List<T> listQuery(SQLBuilder<T> builder) {
        return listQuery(builder.getClazz(),builder.getSQL(),builder.getParams());
    }

    public <T> List<T> listQuery(Class<T> clazz, String sql, Object... params) {
        TypedQuery<T> query = entityManager.createQuery(sql, clazz);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query.getResultList();
    }

    public <T> Page<T> pageQuery(SQLBuilder<T> builder,PageRequest pageRequest) {
        return pageQuery(builder.getClazz(), builder.getSQL(), builder.getCountSQL(),pageRequest,builder.getParams());
    }


    public <T> Page<T> pageQuery(Class<T> clazz, String sql, PageRequest pageRequest, Object... params) {
        return pageQuery(clazz,sql,"SELECT COUNT(1) " + sql,pageRequest,params);
    }

    public <T> Page<T> pageQuery(Class<T> clazz, String sql, String countSql, PageRequest pageRequest, Object... params) {
        TypedQuery<T> query = entityManager.createQuery(sql, clazz);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        query.setMaxResults(pageRequest.getPageSize());
        return new PageImpl<T>(query.getResultList(), pageRequest, countQuery(countSql, params));
    }


    private long countQuery(String sql, Object... params) {
        TypedQuery<Long> query = entityManager.createQuery(sql, Long.class);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query.getSingleResult();
    }

    public Page<MapViewResult> pageMapQuery(QueryColumns columns, String sql, String countSql, PageRequest pageRequest, Object... params) {
        String querySql = "SELECT new com.codingapi.springboot.fast.jpa.map.MapViewResult('"+columns.getKey()+"',"+ String.join(",", columns.getColumnSql()) + ") " + sql;
        String countQuerySql = "SELECT COUNT(1) " + countSql;
        Page<MapViewResult> result = pageQuery(MapViewResult.class,querySql,countQuerySql,pageRequest,params);
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
        return result;
    }

    public List<MapViewResult> listMapQuery(QueryColumns columns, String sql, Object... params) {
        String querySql = "SELECT new com.codingapi.springboot.fast.jpa.map.MapViewResult('"+columns.getKey()+"',"+ String.join(",", columns.getColumnSql()) + ") " + sql;
        List<MapViewResult> result =  listQuery(MapViewResult.class,querySql,params);
        QueryColumnsContext.getInstance().clearCache(columns.getKey());
        return result;
    }
}
