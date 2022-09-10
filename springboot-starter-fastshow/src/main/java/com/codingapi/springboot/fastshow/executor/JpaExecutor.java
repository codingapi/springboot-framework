package com.codingapi.springboot.fastshow.executor;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@AllArgsConstructor
public class JpaExecutor {

    private final EntityManager entityManager;

    @ResponseBody
    public Object execute(String jpaSql,Object[] args){
        Query query =  entityManager.createQuery(jpaSql);
        if(args!=null) {
            for (int i = 0; i < args.length; i++) {
                query.setParameter(i+1,args[i]);
            }
        }
        return query.getResultList();
    }
}
