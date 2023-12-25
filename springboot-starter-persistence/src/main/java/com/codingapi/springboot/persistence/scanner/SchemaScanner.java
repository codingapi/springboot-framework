package com.codingapi.springboot.persistence.scanner;

import com.codingapi.springboot.persistence.register.DomainClassRegister;
import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.executor.SchemaExecutor;
import com.codingapi.springboot.persistence.schema.SchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

@Slf4j
public class SchemaScanner implements ApplicationRunner {

    private final SchemaExecutor schemaExecutor;

    private final SchemaFactory schemaFactory;

    public SchemaScanner(SchemaExecutor schemaExecutor, SchemaFactory schemaFactory) {
        this.schemaExecutor = schemaExecutor;
        this.schemaFactory = schemaFactory;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Class<?>> domainClasses = DomainClassRegister.INSTANCE.getClasses();
        for (Class<?> domainClass : domainClasses) {
            Schema schema = schemaFactory.getSchema(domainClass);
            SchemaContext.INSTANCE.register(schema);
            schemaExecutor.create(schema);
        }
    }

}
