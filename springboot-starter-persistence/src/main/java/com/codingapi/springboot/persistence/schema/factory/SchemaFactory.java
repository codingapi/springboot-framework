package com.codingapi.springboot.persistence.schema.factory;

import com.codingapi.springboot.persistence.schema.Schema;

public interface SchemaFactory {

    Schema getSchema(Class<?> domainClass);
}
