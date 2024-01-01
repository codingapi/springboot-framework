package com.codingapi.springboot.fast.jpa;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

@AllArgsConstructor
public class JPAQueryContextRegister implements InitializingBean {

    private JPAQuery JPAQuery;

    @Override
    public void afterPropertiesSet() throws Exception {
        JpaQueryContext.getInstance().setJPAQuery(JPAQuery);
    }

}
