package com.codingapi.springboot.fast.jpa;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@AllArgsConstructor
public class JPAQuery {

    private final EntityManager entityManager;

    public List<?> listQuery(Class<?> clazz, String sql, Object... params) {
        TypedQuery<?> query = entityManager.createQuery(sql, clazz);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query.getResultList();
    }

    public Page<?> pageQuery(Class<?> clazz, String sql, String countSql, PageRequest pageRequest, Object... params) {
        TypedQuery<?> query = entityManager.createQuery(sql, clazz);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        query.setMaxResults(pageRequest.getPageSize());
        return new PageImpl<>(query.getResultList(), pageRequest, countQuery(countSql, params));
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

}
