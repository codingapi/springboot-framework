package com.codingapi.springboot.fast.manager;

import lombok.Getter;

import javax.persistence.EntityManager;

public class EntityManagerContent {

    @Getter
    private EntityManager entityManager;


    private EntityManagerContent() {
    }


    @Getter
    private final static EntityManagerContent instance = new EntityManagerContent();


    public void push(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public void detach(Object entity) {
        if (entityManager != null) {
            entityManager.detach(entity);
        }
    }
}
