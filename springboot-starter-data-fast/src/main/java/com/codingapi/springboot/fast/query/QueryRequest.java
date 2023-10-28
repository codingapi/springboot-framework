package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.RequestFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryRequest {

    private final PageRequest request;
    private final Class<?> clazz;

    public QueryRequest(PageRequest request, Class<?> clazz) {
        this.request = request;
        this.clazz = clazz;
    }

    public <T> Example<T> getExample() {
        RequestFilter requestFilter = request.getRequestFilter();
        if (!requestFilter.hasFilter()) {
            return null;
        }
        Object entity = null;
        try {
            entity = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Filter value = requestFilter.getFilter(name);
            if (value != null) {
                try {
                    descriptor.getWriteMethod().invoke(entity, value.getFilterValue(descriptor.getPropertyType()));
                } catch (Exception e) {
                }
            }
        }
        return (Example<T>) Example.of(entity);
    }


    private List<String> getClazzProperties() {
        List<String> properties = new ArrayList<>();
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            properties.add(descriptor.getName());
        }
        return properties;
    }


    public <T> List<Order> getOrder(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Order> orderList = new ArrayList<>();
        request.getSort().forEach(sort -> {
            if (sort.getDirection().isAscending()) {
                orderList.add(criteriaBuilder.asc(root.get(sort.getProperty())));
            } else {
                orderList.add(criteriaBuilder.asc(root.get(sort.getProperty())));
            }
        });
        return orderList;
    }


    private <T> Predicate toPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Root<T> root, List<String> properties) {
        String key = filter.getKey();
        if (filter.isAndFilters() || filter.isOrFilters() || properties.contains(key)) {

            if (filter.isEqual()) {
                return criteriaBuilder.equal(root.get(key), filter.getValue()[0]);
            }

            if (filter.isLike()) {
                String matchValue = (String) filter.getValue()[0];
                return criteriaBuilder.like(root.get(key), "%" + matchValue + "%");
            }

            if (filter.isBetween()) {
                Object value1 = filter.getValue()[0];
                Object value2 = filter.getValue()[2];
                if (value1 instanceof Integer && value2 instanceof Integer) {
                    return criteriaBuilder.between(root.get(key), (Integer) value1, (Integer) value2);
                }

                if (value1 instanceof Long && value2 instanceof Long) {
                    return criteriaBuilder.between(root.get(key), (Long) value1, (Long) value2);
                }

                if (value1 instanceof Date && value2 instanceof Date) {
                    return criteriaBuilder.between(root.get(key), (Date) value1, (Date) value2);
                }
            }

            if (filter.isGreaterThan()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.greaterThan(root.get(key), (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.greaterThan(root.get(key), (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.greaterThan(root.get(key), (Date) value);
                }
            }

            if (filter.isGreaterThanEqual()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Date) value);
                }
            }

            if (filter.isLessThan()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.lessThan(root.get(key), (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.lessThan(root.get(key), (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.lessThan(root.get(key), (Date) value);
                }
            }

            if (filter.isLessThanEqual()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Date) value);
                }
            }

            if (filter.isIn()) {
                Object[] value = filter.getValue();
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get(key));
                for (Object item : value) {
                    in.value(item);
                }
                return in;
            }

            if (filter.isOrFilters()) {
                Filter[] orFilters = (Filter[]) filter.getValue();
                List<Predicate> orPredicates = new ArrayList<>();
                for (Filter orFilter : orFilters) {
                    Predicate predicate = toPredicate(orFilter, criteriaBuilder, root, properties);
                    if (predicate != null) {
                        orPredicates.add(predicate);
                    }
                }
                return criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            }

            if (filter.isAndFilters()) {
                Filter[] orFilters = (Filter[]) filter.getValue();
                List<Predicate> addPredicates = new ArrayList<>();
                for (Filter orFilter : orFilters) {
                    Predicate predicate = toPredicate(orFilter, criteriaBuilder, root, properties);
                    if (predicate != null) {
                        addPredicates.add(predicate);
                    }
                }
                return criteriaBuilder.and(addPredicates.toArray(new Predicate[0]));
            }
        }
        return null;
    }


    public <T> List<Predicate> getPredicate(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        List<String> properties = getClazzProperties();
        RequestFilter requestFilter = request.getRequestFilter();
        for (Filter filter : requestFilter.getFilters()) {
            Predicate predicate = toPredicate(filter, criteriaBuilder, root, properties);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        return predicates;
    }
}