package com.codingapi.springboot.persistence.schema;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public abstract class Schema {

    private final Class<?> domainClass;
    @Getter
    private final SchemaProperty schemaProperty;

    public Schema(Class<?> domainClass) {
        log.info("Schema init:{}", domainClass);
        this.domainClass = domainClass;
        this.schemaProperty = new SchemaProperty(domainClass);
    }


    public abstract BuildSchema buildSchema();

    public abstract SaveSchema insertSchema();

    public abstract SearchSchema getById();

    public boolean supports(Class<?> domainClass) {
        return this.domainClass.equals(domainClass);
    }


}
