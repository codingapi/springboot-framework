package com.codingapi.springboot.fast.jpa;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

@AllArgsConstructor
public class JpaQueryContextRegister implements InitializingBean {

    private JpaQuery JPAQuery;

    @Override
    public void afterPropertiesSet() throws Exception {
        JpaQueryContext.getInstance().setJpaQuery(JPAQuery);
    }

}
