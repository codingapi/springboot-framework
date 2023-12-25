package com.codingapi.springboot.persistence.schema;

import com.codingapi.springboot.persistence.property.SchemaProperty;

public abstract class BuildSchema {

    public abstract String schema();

    protected final SchemaProperty property;

    public BuildSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }
}
