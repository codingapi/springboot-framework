package com.codingapi.springboot.permission.db.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
public class SQLTemplateConfiguration {

    @Bean
    public SQLTemplateInitializer sqlTemplateInitializer(@Autowired(required = false) List<SQLTemplate> templateList) {
        return new SQLTemplateInitializer(templateList);
    }

    @Bean
    public H2SQLTemplate h2SQLTemplate() {
        return new H2SQLTemplate();
    }

}
