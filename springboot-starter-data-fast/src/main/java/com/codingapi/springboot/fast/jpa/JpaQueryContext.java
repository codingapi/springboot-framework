package com.codingapi.springboot.fast.jpa;


import lombok.Getter;

public class JpaQueryContext {

    @Getter
    private static final JpaQueryContext instance = new JpaQueryContext();

    private JpaQueryContext() {

    }

    @Getter
    private JpaQuery jpaQuery;

    void setJpaQuery(JpaQuery jpaQuery) {
        this.jpaQuery = jpaQuery;
    }


}
