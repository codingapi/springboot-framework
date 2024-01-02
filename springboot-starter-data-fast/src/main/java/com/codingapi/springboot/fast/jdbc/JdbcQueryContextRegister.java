package com.codingapi.springboot.fast.jdbc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;

@AllArgsConstructor
public class JdbcQueryContextRegister implements InitializingBean {

    private JdbcQuery jdbcQuery;

    @Override
    public void afterPropertiesSet() throws Exception {
        JdbcQueryContext.getInstance().setJdbcQuery(jdbcQuery);
    }

}
