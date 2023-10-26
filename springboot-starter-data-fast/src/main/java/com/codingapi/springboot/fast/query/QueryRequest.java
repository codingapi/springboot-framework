package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        if (!request.hasFilter()) {
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
            PageRequest.Filter value = request.getFilters().get(name);
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

    public <T> List<Predicate> getPredicate(Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        List<String> properties = getClazzProperties();
        for (String key : request.getFilters().keySet()) {
            PageRequest.Filter filter = request.getFilters().get(key);
            if (filter.isOr() || properties.contains(key)) {
                if (filter.isEqual()) {
                    predicates.add(criteriaBuilder.equal(root.get(key), filter.getValue()[0]));
                }

                if (filter.isLike()) {
                    String matchValue = (String) filter.getValue()[0];
                    predicates.add(criteriaBuilder.like(root.get(key), "%" + matchValue + "%"));
                }

                if (filter.isBetween()) {
                    Object value1 = filter.getValue()[0];
                    Object value2 = filter.getValue()[2];
                    if (value1 instanceof Integer && value2 instanceof Integer) {
                        predicates.add(criteriaBuilder.between(root.get(key), (Integer) value1, (Integer) value2));
                    }

                    if (value1 instanceof Long && value2 instanceof Long) {
                        predicates.add(criteriaBuilder.between(root.get(key), (Long) value1, (Long) value2));
                    }

                    if (value1 instanceof Date && value2 instanceof Date) {
                        predicates.add(criteriaBuilder.between(root.get(key), (Date) value1, (Date) value2));
                    }
                }

                if (filter.isGreaterThan()) {
                    Object value = filter.getValue()[0];
                    if (value instanceof Integer) {
                        predicates.add(criteriaBuilder.greaterThan(root.get(key), (Integer) value));
                    }
                    if (value instanceof Long) {
                        predicates.add(criteriaBuilder.greaterThan(root.get(key), (Long) value));
                    }
                    if (value instanceof Date) {
                        predicates.add(criteriaBuilder.greaterThan(root.get(key), (Date) value));
                    }
                }

                if (filter.isGreaterThanEqual()) {
                    Object value = filter.getValue()[0];
                    if (value instanceof Integer) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Integer) value));
                    }
                    if (value instanceof Long) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Long) value));
                    }
                    if (value instanceof Date) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Date) value));
                    }
                }

                if (filter.isLessThan()) {
                    Object value = filter.getValue()[0];
                    if (value instanceof Integer) {
                        predicates.add(criteriaBuilder.lessThan(root.get(key), (Integer) value));
                    }
                    if (value instanceof Long) {
                        predicates.add(criteriaBuilder.lessThan(root.get(key), (Long) value));
                    }
                    if (value instanceof Date) {
                        predicates.add(criteriaBuilder.lessThan(root.get(key), (Date) value));
                    }
                }

                if (filter.isLessThanEqual()) {
                    Object value = filter.getValue()[0];
                    if (value instanceof Integer) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(key), (Integer) value));
                    }
                    if (value instanceof Long) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(key), (Long) value));
                    }
                    if (value instanceof Date) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(key), (Date) value));
                    }
                }

                if (filter.isIn()) {
                    Object[] value = filter.getValue();
                    predicates.add(criteriaBuilder.in(root.get(key)).value(value));
                }

                if (filter.isOr()) {
                    if(key.equals("OR")) {
                        Object[] value = filter.getValue();

                        // 创建Predicate的列表，用于收集所有的OR条件
                        List<Predicate> orPredicates = new ArrayList<>();

                        // 循环遍历value数组，每两个为一组
                        for (int i = 0; i < value.length; i += 2) {
                            orPredicates.add(criteriaBuilder.equal(root.get((String) value[i]), value[i + 1]));
                        }

                        // 使用or方法连接所有的Predicate
                        predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
                    }else{
                        Object[] value = filter.getValue();
                        predicates.add(criteriaBuilder.equal(root.get(key), value[0]));
                    }
                }
            }
        }

        return predicates;
    }
}