package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.schema.*;

public class JdbcSchema extends Schema {

    public JdbcSchema(Class<?> domainClass) {
        super(domainClass);
    }

    @Override
    public BuildSchema buildSchema() {
        return new JdbcBuildSchema(this);
    }

    @Override
    public SaveSchema saveSchema() {
        return new JdbcSaveSchema(this);
    }

    @Override
    public SearchSchema searchSchema() {
        return new JdbcSearchSchema(this);
    }

    @Override
    public DeleteSchema deleteSchema() {
        return new JdbcDeleteSchema(this);
    }

    @Override
    public UpdateSchema updateSchema() {
        return new JdbcUpdateSchema(this);
    }
}
