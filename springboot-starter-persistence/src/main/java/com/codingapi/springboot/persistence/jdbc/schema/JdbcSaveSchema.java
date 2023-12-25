package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.property.BeanProperty;
import com.codingapi.springboot.persistence.schema.SaveSchema;
import com.codingapi.springboot.persistence.schema.Schema;

public class JdbcSaveSchema extends SaveSchema {

    public JdbcSaveSchema(Schema schema) {
        super(schema);
    }

    @Override
    public String saveSchema(boolean hasId) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(property.getSchemaName());
        sql.append(" (");
        for (BeanProperty beanProperty : property.getProperties(hasId)) {
            sql.append(beanProperty.getName());
            sql.append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(") VALUES (");
        for (BeanProperty beanProperty : property.getProperties(hasId)) {
            sql.append("?, ");
        }
        sql.delete(sql.length() - 2, sql.length());
        sql.append(")");
        return sql.toString();
    }


}
