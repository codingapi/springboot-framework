package com.codingapi.springboot.persistence.schema;


import com.codingapi.springboot.persistence.property.SchemaProperty;
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
        this.check();
    }

    public void check() {
        if (!this.schemaProperty.hasIdProperty()) {
            throw new RuntimeException("schema id property not found");
        }
    }

    public abstract BuildSchema buildSchema();

    public abstract SaveSchema saveSchema();

    public abstract SearchSchema searchSchema();

    public abstract DeleteSchema deleteSchema();

    public abstract UpdateSchema updateSchema();

}
