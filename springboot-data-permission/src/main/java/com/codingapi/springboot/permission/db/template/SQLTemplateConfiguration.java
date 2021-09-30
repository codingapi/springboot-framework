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

    static class SQLTemplateInitializer{
        public SQLTemplateInitializer(List<SQLTemplate> templateList) {
            SQLTemplateContext.getInstance().addSQLTemplates(templateList);
        }
    }

    @Autowired(required = false)
    public SQLTemplateInitializer sqlTemplateInitializer(List<SQLTemplate> templateList) {
        return new SQLTemplateInitializer(templateList);
    }

    @Bean
    public H2SQLTemplate h2SQLTemplate(){
        return new H2SQLTemplate();
    }

}
