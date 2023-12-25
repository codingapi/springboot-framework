package com.codingapi.springboot.persistence.scanner;

import com.codingapi.springboot.persistence.schema.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SchemaContext {

    private final static SchemaContext INSTANCE = new SchemaContext();

    private final List<Schema> schemas;

    private SchemaContext() {
        this.schemas = new ArrayList<>();
    }

    public void register(Schema schema) {
        this.schemas.add(schema);
    }

    public Schema getSchema(Class<?> domainClass) {
        return schemas.stream()
                .filter(schema -> schema.getDomainClass().equals(domainClass))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("schema not found"));
    }

    public static SchemaContext getInstance() {
        return INSTANCE;
    }
}
