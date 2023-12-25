package com.codingapi.springboot.persistence.schema;

import com.codingapi.springboot.persistence.property.SchemaProperty;
import lombok.Getter;

@Getter
public abstract class SearchSchema {

    protected final SchemaProperty property;

    public SearchSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }

    public abstract String schema();

    public Object getByIdValue(Object domain) {
        return property.getIdBeanProperty().get(domain);
    }

}
