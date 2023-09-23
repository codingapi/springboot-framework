package com.codingapi.springboot.fast.manager;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

import javax.persistence.EntityManager;

@AllArgsConstructor
public class EntityManagerInitializer implements InitializingBean {

    private final EntityManager entityManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        EntityManagerContent.getInstance().push(entityManager);
    }
}
