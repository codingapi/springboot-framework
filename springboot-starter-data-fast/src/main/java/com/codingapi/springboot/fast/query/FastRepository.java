package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface FastRepository<T,ID> extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

    default Page<T> findAll(PageRequest request){
        if(request.hasFilter()){
            Class<T> clazz = getDomainClass();
            return findAll(request.getExample(clazz),request);
        }
        return findAll((org.springframework.data.domain.PageRequest)request);
    }


    @SuppressWarnings("unchecked")
    default Class<T> getDomainClass() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass()).as(FastRepository.class);
        return (Class<T>) resolvableType.getGeneric(0).resolve();
    }

}
