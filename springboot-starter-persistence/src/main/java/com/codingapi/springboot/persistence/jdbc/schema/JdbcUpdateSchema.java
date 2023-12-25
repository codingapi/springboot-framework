package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.property.BeanProperty;
import com.codingapi.springboot.persistence.schema.Schema;
import com.codingapi.springboot.persistence.schema.UpdateSchema;

public class JdbcUpdateSchema extends UpdateSchema {

    public JdbcUpdateSchema(Schema schema) {
        super(schema);
    }

    @Override
    public String schema() {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(property.getSchemaName()).append(" SET ");
        for (BeanProperty property : property.getProperties(false)) {
            sql.append(property.getName()).append(" = ?, ");
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" WHERE id = ?");
        return sql.toString();
    }

}
