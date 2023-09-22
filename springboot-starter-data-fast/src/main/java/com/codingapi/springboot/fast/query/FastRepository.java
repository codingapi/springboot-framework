package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface FastRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    default Page<T> findAll(PageRequest request) {
        if (request.hasFilter()) {
            Class<T> clazz = getDomainClass();
            QueryRequest queryRequest = new QueryRequest(request);
            return findAll(queryRequest.getExample(clazz), request);
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }


    @SuppressWarnings("unchecked")
    default Class<T> getDomainClass() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass()).as(FastRepository.class);
        return (Class<T>) resolvableType.getGeneric(0).resolve();
    }


    default Page<T> findAllByRequest(PageRequest request) {
        if (request.hasFilter()) {
            Specification<T> specification = (root, query, criteriaBuilder) -> {

                List<Predicate> predicates = new ArrayList<>();
                for (String key : request.getFilters().keySet()) {
                    PageRequest.Filter filter = request.getFilters().get(key);
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
                }

                List<jakarta.persistence.criteria.Order> orderList = new ArrayList<>();
                request.getSort().forEach(sort -> {
                    if (sort.getDirection().isAscending()) {
                        orderList.add(criteriaBuilder.asc(root.get(sort.getProperty())));
                    } else {
                        orderList.add(criteriaBuilder.asc(root.get(sort.getProperty())));
                    }
                });

                return query.where(predicates.toArray(new Predicate[0])).orderBy(orderList).getRestriction();
            };

            return findAll(specification, request);
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }

}
