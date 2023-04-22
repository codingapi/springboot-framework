package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;
import com.codingapi.springboot.generator.properties.GeneratorJdbcProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "codingapi.id.jdbc.generator")
    public GeneratorJdbcProperties generatorJdbcProperties() {
        return new GeneratorJdbcProperties();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public IdKeyDao idKeyDao(GeneratorJdbcProperties generatorJdbcProperties) {
        IdKeyDao keyDao = new IdKeyDao(generatorJdbcProperties.openDataSource());
        IdGeneratorContext.getInstance().init(keyDao);
        return keyDao;
    }

}
