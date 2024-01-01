package com.codingapi.springboot.fast.jpa;


import lombok.Getter;

public class JpaQueryContext {

    @Getter
    private static final JpaQueryContext instance = new JpaQueryContext();

    private JpaQueryContext() {

    }

    @Getter
    private JPAQuery JPAQuery;

    void setJPAQuery(JPAQuery JPAQuery) {
        this.JPAQuery = JPAQuery;
    }


}
