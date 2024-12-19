package com.codingapi.springboot.authorization.interceptor;

import lombok.Getter;
import lombok.Setter;

@Setter
public class SQLInterceptorContext {

    @Getter
    private final static SQLInterceptorContext instance = new SQLInterceptorContext();

    @Getter
    private SQLInterceptor sqlInterceptor;

    private SQLInterceptorContext() {
        this.sqlInterceptor = new DefaultSQLInterceptor();
    }


}
