package com.codingapi.springboot.persistence;

import com.codingapi.springboot.persistence.scanner.SchemaScanner;
import com.codingapi.springboot.persistence.schema.SchemaExecutor;
import com.codingapi.springboot.persistence.schema.SchemaFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class AutoConfiguration {

    @Bean
    public SchemaScanner domainScanner(SchemaExecutor schemaExecutor, SchemaFactory schemaFactory) {
        return new SchemaScanner(schemaExecutor, schemaFactory);
    }

}
