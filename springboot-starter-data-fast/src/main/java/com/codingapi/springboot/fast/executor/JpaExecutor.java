package com.codingapi.springboot.fast.executor;

import com.codingapi.springboot.framework.dto.response.MultiResponse;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import java.util.Collection;

@AllArgsConstructor
public class JpaExecutor {

    private final EntityManager entityManager;

    public Object execute(String hql,String countHql,Object[] args,Class<?> returnType){
        JpaQuery query = new JpaQuery(hql,countHql,args,entityManager);

        if(returnType.equals(SingleResponse.class)){
            return SingleResponse.of(query.getSingleResult());
        }

        if(returnType.equals(MultiResponse.class)){
            Object returnData =  query.getResultList();
            if(Page.class.isAssignableFrom(returnData.getClass())) {
                return MultiResponse.of((Page)returnData);
            }

            if(Collection.class.isAssignableFrom(returnData.getClass())) {
                return MultiResponse.of((Collection) returnData);
            }
        }

        return query.getResultList();
    }
}
