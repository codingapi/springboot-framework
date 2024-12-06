package com.codingapi.springboot.authorization;


import com.codingapi.springboot.authorization.filter.DataAuthorizationFilter;
import com.codingapi.springboot.authorization.handler.ColumnHandler;
import com.codingapi.springboot.authorization.handler.RowHandler;
import com.codingapi.springboot.authorization.interceptor.SQLInterceptor;
import com.codingapi.springboot.authorization.register.ConditionHandlerRegister;
import com.codingapi.springboot.authorization.register.DataAuthorizationContextRegister;
import com.codingapi.springboot.authorization.register.ResultSetHandlerRegister;
import com.codingapi.springboot.authorization.register.SQLInterceptorRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataAuthorizationConfiguration {

    @Bean
    public ConditionHandlerRegister conditionHandlerRegister(@Autowired(required = false) RowHandler rowHandler) {
        return new ConditionHandlerRegister(rowHandler);
    }

    @Bean
    public ResultSetHandlerRegister resultSetHandlerRegister(@Autowired(required = false) ColumnHandler columnHandler) {
        return new ResultSetHandlerRegister(columnHandler);
    }

    @Bean
    public SQLInterceptorRegister sqlInterceptorRegister(@Autowired(required = false) SQLInterceptor sqlInterceptor) {
        return new SQLInterceptorRegister(sqlInterceptor);
    }

    @Bean
    public DataAuthorizationContextRegister dataAuthorizationContextRegister(@Autowired(required = false) List<DataAuthorizationFilter> dataAuthorizationFilters) {
        return new DataAuthorizationContextRegister(dataAuthorizationFilters);
    }
}
