package com.codingapi.springboot.fast.dynamic;


import lombok.Getter;

public class DynamicQueryContext {

    @Getter
    private static final DynamicQueryContext instance = new DynamicQueryContext();

    private DynamicQueryContext() {

    }

    @Getter
    private DynamicQuery dynamicQuery;

    void setDynamicQuery(DynamicQuery dynamicQuery) {
        this.dynamicQuery = dynamicQuery;
    }


}
