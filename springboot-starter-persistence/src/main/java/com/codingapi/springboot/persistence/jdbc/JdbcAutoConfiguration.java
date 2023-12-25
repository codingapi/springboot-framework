package com.codingapi.springboot.persistence.jdbc;

import com.codingapi.springboot.persistence.DomainPersistence;
import com.codingapi.springboot.persistence.jdbc.executor.JdbcSchemaExecutor;
import com.codingapi.springboot.persistence.jdbc.impl.JdbcDomainPersistence;
import com.codingapi.springboot.persistence.jdbc.impl.JdbcSchemaFactory;
import com.codingapi.springboot.persistence.schema.executor.SchemaExecutor;
import com.codingapi.springboot.persistence.schema.factory.SchemaFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@ConditionalOnClass(JdbcTemplate.class)
@Configurable
public class JdbcAutoConfiguration {

    @Bean
    public SchemaExecutor schemaExecutor(JdbcTemplate jdbcTemplate) {
        return new JdbcSchemaExecutor(jdbcTemplate);
    }

    @Bean
    public DomainPersistence domainPersistence(JdbcTemplate jdbcTemplate) {
        return new JdbcDomainPersistence(jdbcTemplate);
    }

    @Bean
    public SchemaFactory schemaFactory() {
        return new JdbcSchemaFactory();
    }
}
