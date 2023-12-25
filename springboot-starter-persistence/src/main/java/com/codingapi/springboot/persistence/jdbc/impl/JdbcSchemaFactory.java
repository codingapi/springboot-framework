package com.codingapi.springboot.persistence.jdbc.impl;

import com.codingapi.springboot.persistence.jdbc.schema.JdbcSchema;
import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.factory.SchemaFactory;

public class JdbcSchemaFactory implements SchemaFactory {

    @Override
    public Schema getSchema(Class<?> domainClass) {
        return new JdbcSchema(domainClass);
    }

}