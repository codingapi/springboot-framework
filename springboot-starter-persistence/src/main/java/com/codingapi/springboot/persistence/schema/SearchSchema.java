package com.codingapi.springboot.persistence.schema;

import lombok.Getter;

@Getter
public abstract class SearchSchema {

    protected final SchemaProperty property;

    public SearchSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }

    public abstract String getById();

    public Object getByIdValue(Object domain) {
        return property.getIdProperty().get(domain);
    }

}
