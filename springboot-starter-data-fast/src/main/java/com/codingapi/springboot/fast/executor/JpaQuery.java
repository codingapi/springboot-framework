package com.codingapi.springboot.fast.executor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class JpaQuery {
    private final Object[] args;
    private final Query query;
    private final String hql;
    private final String countHql;

    private final EntityManager entityManager;

    public JpaQuery(String hql, String countHql, Object[] args, EntityManager entityManager) {
        this.hql = hql;
        this.countHql = countHql;
        this.args = args;
        this.entityManager = entityManager;
        this.query = entityManager.createQuery(hql);
        this.initParameter(query);
    }

    /**
     * init query parameter
     */
    @SneakyThrows
    private void initParameter(Query query) {
        if (args != null && args.length > 0) {
            Set<Parameter<?>> parameters = query.getParameters();
            for (Parameter<?> parameter : parameters) {
                Integer position = parameter.getPosition();
                if (position != null) {
                    query.setParameter(position, args[position - 1]);
                }
                if (StringUtils.hasText(parameter.getName())) {
                    String name = parameter.getName();
                    Object obj = args[0];
                    Field field = ReflectionUtils.findField(obj.getClass(), name);
                    if (field != null) {
                        field.setAccessible(true);
                        query.setParameter(name, field.get(obj));
                    }
                }
            }
        }
    }

    /**
     * is Page Request
     */
    private boolean isPageable() {
        if (args != null && args.length > 0 && StringUtils.hasText(countHql)) {
            Object lastObj = args[args.length - 1];
            return lastObj instanceof Pageable;
        }
        return false;
    }

    /**
     * get PageRequest
     */
    private Pageable getPageable() {
        if (isPageable()) {
            Object lastObj = args[args.length - 1];
            return (Pageable) lastObj;
        }
        return null;
    }

    /**
     * execute get list result data
     */
    public Object getResultList() {
        if (isPageable()) {
            Pageable pageable = getPageable();
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            long total = getCount();
            return new PageImpl<>(query.getResultList(), pageable, total);
        }
        return query.getResultList();
    }


    /**
     * get sql execute data count
     */
    private long getCount() {
        Query countQuery = entityManager.createQuery(countHql);
        initParameter(countQuery);
        return (Long) countQuery.getSingleResult();
    }


    /**
     * get single result data
     */
    public Object getSingleResult() {
        return query.getSingleResult();
    }


}
