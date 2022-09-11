package com.codingapi.springboot.generator;

import com.codingapi.springboot.generator.dao.IdKeyDao;
import com.codingapi.springboot.generator.properties.GeneratorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "codingapi.id.generator")
    public GeneratorProperties generatorProperties() {
        return new GeneratorProperties();
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public IdKeyDao idKeyDao(GeneratorProperties generatorProperties) {
        IdKeyDao keyDao = new IdKeyDao(generatorProperties.getJdbcUrl());
        IdGeneratorContext.getInstance().init(keyDao);
        return keyDao;
    }

}
