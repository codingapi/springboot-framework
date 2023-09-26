package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface FastRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    default Page<T> findAll(PageRequest request) {
        if (request.hasFilter()) {
            Class<T> clazz = getDomainClass();
            QueryRequest queryRequest = new QueryRequest(request, clazz);
            return findAll(queryRequest.getExample(), request);
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
            Class<T> clazz = getDomainClass();
            Specification<T> specification = (root, query, criteriaBuilder) -> {
                QueryRequest queryRequest = new QueryRequest(request, clazz);
                List<Predicate> predicates = queryRequest.getPredicate(root, criteriaBuilder);
                List<Order> orderList = queryRequest.getOrder(root, criteriaBuilder);
                return query.where(predicates.toArray(new Predicate[0])).orderBy(orderList).getRestriction();
            };

            return findAll(specification, request);
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }

}