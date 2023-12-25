package com.codingapi.springboot.persistence.schema;

import com.codingapi.springboot.persistence.property.SchemaProperty;

public abstract class DeleteSchema {

    public abstract String schema();

    protected final SchemaProperty property;

    public DeleteSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }
}
