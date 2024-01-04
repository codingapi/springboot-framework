package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.RequestFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        return getClazzProperties(clazz);
    }

    private List<String> getClazzProperties(Class<?> clazz) {
        List<String> properties = new ArrayList<>();
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            Class<?> propertyType = descriptor.getPropertyType();
            String name = descriptor.getName();
            if (propertyType.getPackage() != null && !propertyType.getPackage().getName().startsWith("java")) {
                List<String> childProperties = getClazzProperties(propertyType);
                for (String child : childProperties) {
                    properties.add(name + "." + child);
                }
            }
            properties.add(name);
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

    private Path getPathByKey(Root root, String key) {
        String[] keys = key.split("\\.");
        if (keys.length > 1) {
            String lastKey = keys[keys.length-1];
            From current = root;
            for (int i=0;i< keys.length-1;i++) {
                String item  = keys[i];
                Set<Join> joins = current.getJoins();
                for (Join<?, ?> join : joins) {
                    if (join.getModel().getBindableJavaType().getSimpleName().equalsIgnoreCase(item)) {
                        current = join;
                    }
                }
            }
            return current.get(lastKey);
        } else {
            return root.get(key);
        }
    }


    private <T> Predicate toPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Root<T> root, List<String> properties) {
        String key = filter.getKey();
        if (filter.isAndFilters() || filter.isOrFilters() || properties.contains(key)) {

            Path path = getPathByKey(root, key);

            if (filter.isEqual()) {
                return criteriaBuilder.equal(path, filter.getValue()[0]);
            }

            if (filter.isLike()) {
                String matchValue = (String) filter.getValue()[0];
                return criteriaBuilder.like(path, "%" + matchValue + "%");
            }

            if (filter.isBetween()) {
                Object value1 = filter.getValue()[0];
                Object value2 = filter.getValue()[2];
                if (value1 instanceof Integer && value2 instanceof Integer) {
                    return criteriaBuilder.between(path, (Integer) value1, (Integer) value2);
                }

                if (value1 instanceof Long && value2 instanceof Long) {
                    return criteriaBuilder.between(path, (Long) value1, (Long) value2);
                }

                if (value1 instanceof Date && value2 instanceof Date) {
                    return criteriaBuilder.between(path, (Date) value1, (Date) value2);
                }
            }

            if (filter.isGreaterThan()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.greaterThan(path, (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.greaterThan(path, (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.greaterThan(path, (Date) value);
                }
            }

            if (filter.isGreaterThanEqual()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, (Date) value);
                }
            }

            if (filter.isLessThan()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.lessThan(path, (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.lessThan(path, (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.lessThan(path, (Date) value);
                }
            }

            if (filter.isLessThanEqual()) {
                Object value = filter.getValue()[0];
                if (value instanceof Integer) {
                    return criteriaBuilder.lessThanOrEqualTo(path, (Integer) value);
                }
                if (value instanceof Long) {
                    return criteriaBuilder.lessThanOrEqualTo(path, (Long) value);
                }
                if (value instanceof Date) {
                    return criteriaBuilder.lessThanOrEqualTo(path, (Date) value);
                }
            }

            if (filter.isIn()) {
                Object[] value = filter.getValue();
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
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


    private void fetchJoins(From root, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(OneToOne.class) != null) {
                Join<?, ?> join = root.join(field.getName(), JoinType.INNER);
                this.fetchJoins(join, field.getType());
            }
            if (field.getAnnotation(ManyToOne.class) != null) {
                Join<?, ?> join = root.join(field.getName(), JoinType.INNER);
                this.fetchJoins(join, field.getType());
            }
            if (field.getAnnotation(OneToMany.class) != null) {
                Join<?, ?> join = root.join(field.getName(), JoinType.INNER);
                this.fetchJoins(join, field.getType());
            }
            if (field.getAnnotation(ManyToMany.class) != null) {
                Join<?, ?> join = root.join(field.getName(), JoinType.INNER);
                this.fetchJoins(join, field.getType());
            }
        }
    }

    public <T> List<Predicate> getPredicate(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        List<String> properties = getClazzProperties();
        RequestFilter requestFilter = request.getRequestFilter();
        this.fetchJoins(root, clazz);
        for (Filter filter : requestFilter.getFilters()) {
            Predicate predicate = toPredicate(filter, criteriaBuilder, root, properties);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }
        return predicates;
    }
}