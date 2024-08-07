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
            Class<T> clazz = getEntityClass();
            ExampleBuilder exampleBuilder = new ExampleBuilder(request, clazz);
            return findAll(exampleBuilder.getExample(), request);
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }

    default Page<T> pageRequest(PageRequest request) {
        if (request.hasFilter()) {
            Class<T> clazz = getEntityClass();
            DynamicSQLBuilder dynamicSQLBuilder = new DynamicSQLBuilder(request, clazz);
            return dynamicPageQuery(dynamicSQLBuilder.getHQL(), request, dynamicSQLBuilder.getParams());
        }
        return findAll((org.springframework.data.domain.PageRequest) request);
    }


    default Page<T> searchRequest(SearchRequest request) {
        Class<T> clazz = getEntityClass();
        return pageRequest(request.toPageRequest(clazz));
    }

}
