package com.codingapi.springboot.fast.executor;

import lombok.AllArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@AllArgsConstructor
public class JpaExecutor {

    private final EntityManager entityManager;

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
