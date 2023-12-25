package com.codingapi.springboot.persistence.schema;

public interface SchemaFactory {

    Schema getSchema(Class<?> domainClass);
}
