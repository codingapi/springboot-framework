package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.schema.BuildSchema;
import com.codingapi.springboot.persistence.schema.SaveSchema;
import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.SearchSchema;

public class JdbcSchema extends Schema {

    public JdbcSchema(Class<?> domainClass) {
        super(domainClass);
    }

    @Override
    public BuildSchema buildSchema() {
        return new JdbcBuildSchema(this);
    }

    @Override
    public SaveSchema insertSchema() {
        return new JdbcSaveSchema(this);
    }

    @Override
    public SearchSchema getById() {
        return new JdbcSearchSchema(this);
    }
}
