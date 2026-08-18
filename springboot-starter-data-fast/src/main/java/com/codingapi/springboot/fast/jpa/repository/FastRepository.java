package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.SearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 更强大的Repository对象
 *
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface FastRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, DynamicRepository<T, ID>, DynamicNativeRepository<T, ID> {

    default Page<T> findAll(PageRequest request) {
        if (request.hasFilter()) {
            // 全部为简单等值条件时走 Example 查询；包含 LIKE/范围/IN/OR 等复杂条件时自动切换 HQL 动态查询，
            // 避免非等值条件被 Example 静默降级为等值匹配
            if (request.getRequestFilter().isAllEqualFilter()) {
                Class<T> clazz = getEntityClass();
                ExampleBuilder exampleBuilder = new ExampleBuilder(request, clazz);
                return findAll(exampleBuilder.getExample(), request);
            }
            return pageRequest(request);
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }

    default Page<T> pageRequest(PageRequest request) {
        if (request.hasFilter()) {
            Class<T> clazz = getEntityClass();
            DynamicSQLBuilder dynamicSQLBuilder = new DynamicSQLBuilder(request, clazz);
            return dynamicPageQuery(dynamicSQLBuilder.getHQL(),dynamicSQLBuilder.getCountHQL(), request, dynamicSQLBuilder.getParams());
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }


    default Page<T> searchRequest(SearchRequest request) {
        Class<T> clazz = getEntityClass();
        return pageRequest(request.toPageRequest(clazz));
    }

}
