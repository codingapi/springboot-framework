package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.RequestFilter;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicRequest {

    private final PageRequest request;
    private final Class<?> clazz;

    private final List<Object> params = new ArrayList<>();
    private int paramIndex = 1;

    public DynamicRequest(PageRequest request, Class<?> clazz) {
        this.request = request;
        this.clazz = clazz;
    }


    public String getHql() {
        StringBuilder hql = new StringBuilder("FROM " + clazz.getSimpleName() + " WHERE ");
        RequestFilter requestFilter = request.getRequestFilter();
        if (requestFilter.hasFilter()) {
            List<Filter> filters = requestFilter.getFilters();
            for (int i = 0; i < filters.size(); i++) {
                Filter filter = filters.get(i);
                this.buildSQL(filter, hql);
                if (i != filters.size() - 1) {
                    hql.append(" AND ");
                }
            }
        }

        Sort sort = request.getSort();
        if (sort.isSorted()) {
            hql.append(" ORDER BY ");
            List<Sort.Order> orders = sort.toList();
            for (int i = 0; i < orders.size(); i++) {
                Sort.Order order = orders.get(i);
                hql.append(order.getProperty()).append(" ").append(order.getDirection().name());
                if (i != orders.size() - 1) {
                    hql.append(",");
                }
            }
        }

        return hql.toString();
    }


    private void buildSQL(Filter filter, StringBuilder hql) {
        if (filter.isOrFilters()) {
            Filter[] orFilters = (Filter[]) filter.getValue();
            if (orFilters.length > 0) {
                hql.append(" ( ");
                for (int i = 0; i < orFilters.length; i++) {
                    Filter orFilter = orFilters[i];
                    this.buildSQL(orFilter, hql);
                    if (i != orFilters.length - 1) {
                        hql.append(" OR ");
                    }

                }
                hql.append(" )");
            }
        }

        if (filter.isAndFilters()) {
            Filter[] andFilters = (Filter[]) filter.getValue();
            if (andFilters.length > 0) {
                hql.append(" ( ");
                for (int i = 0; i < andFilters.length; i++) {
                    Filter andFilter = andFilters[i];
                    this.buildSQL(andFilter, hql);
                    if (i != andFilters.length - 1) {
                        hql.append(" AND ");
                    }
                }
                hql.append(" )");
            }
        }

        if (filter.isEqual()) {
            hql.append(filter.getKey()).append(" = ?").append(paramIndex);
            params.add(filter.getValue()[0]);
            paramIndex++;
        }
        if (filter.isLike()) {
            hql.append(filter.getKey()).append(" LIKE ?").append(paramIndex);
            params.add("%" + filter.getValue()[0] + "%");
            paramIndex++;
        }
        if (filter.isIn()) {
            hql.append(filter.getKey()).append(" IN (").append("?").append(paramIndex).append(")");
            params.add(Arrays.asList(filter.getValue()));
            paramIndex++;
        }
        if (filter.isGreaterThan()) {
            hql.append(filter.getKey()).append(" > ?").append(paramIndex);
            params.add(filter.getValue()[0]);
            paramIndex++;
        }
        if (filter.isLessThan()) {
            hql.append(filter.getKey()).append(" < ?").append(paramIndex);
            params.add(filter.getValue()[0]);
            paramIndex++;
        }
        if (filter.isGreaterThanEqual()) {
            hql.append(filter.getKey()).append(" >= ?").append(paramIndex);
            params.add(filter.getValue()[0]);
            paramIndex++;
        }
        if (filter.isLessThanEqual()) {
            hql.append(filter.getKey()).append(" <= ?").append(paramIndex);
            params.add(filter.getValue()[0]);
            paramIndex++;
        }
        if (filter.isBetween()) {
            hql.append(filter.getKey()).append(" BETWEEN ?").append(paramIndex).append(" AND ?").append(paramIndex + 1);
            params.add(filter.getValue()[0]);
            params.add(filter.getValue()[1]);
            paramIndex += 2;
        }
    }


    public Object[] getParams() {
        return params.toArray();
    }
}