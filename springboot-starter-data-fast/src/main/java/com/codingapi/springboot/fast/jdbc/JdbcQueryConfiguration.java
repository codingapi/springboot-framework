package com.codingapi.springboot.fast.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class JdbcQueryConfiguration {

    @Bean
    public JdbcQuery jdbcQuery(JdbcTemplate jdbcTemplate) {
        return new JdbcQuery(jdbcTemplate);
    }

    @Bean
    public JdbcQueryContextRegister jdbcQueryContextRegister(JdbcQuery jdbcQuery){
        return new JdbcQueryContextRegister(jdbcQuery);
    }


}
