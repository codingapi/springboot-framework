package com.codingapi.springboot.authorization.register;


import com.codingapi.springboot.authorization.interceptor.SQLInterceptor;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptorContext;

public class SQLInterceptorRegister {

    public SQLInterceptorRegister(SQLInterceptor sqlInterceptor) {
        if(sqlInterceptor!=null) {
            SQLInterceptorContext.getInstance().setSqlInterceptor(sqlInterceptor);
        }
    }
}
