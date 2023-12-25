package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.schema.DeleteSchema;
import com.codingapi.springboot.persistence.schema.Schema;

public class JdbcDeleteSchema extends DeleteSchema {

    public JdbcDeleteSchema(Schema schema) {
        super(schema);
    }

    @Override
    public String schema() {
        return "DELETE FROM " + property.getSchemaName() + " WHERE id = ?";
    }
}
