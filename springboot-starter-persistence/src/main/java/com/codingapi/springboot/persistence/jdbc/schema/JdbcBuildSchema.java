package com.codingapi.springboot.persistence.jdbc.schema;

import com.codingapi.springboot.persistence.schema.BuildSchema;
import com.codingapi.springboot.persistence.property.BeanProperty;
import com.codingapi.springboot.persistence.schema.Schema;

import java.util.List;

public class JdbcBuildSchema extends BuildSchema {

    public JdbcBuildSchema(Schema schema) {
        super(schema);
    }

    @Override
    public String schema() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(property.getSchemaName());
        sql.append(" (");
        sql.append("id INT PRIMARY KEY AUTO_INCREMENT,");
        List<BeanProperty> properties = property.getProperties(false);
        for (int i = 0; i < properties.size(); i++) {
            BeanProperty beanProperty = properties.get(i);
            sql.append(beanProperty.getName());
            sql.append(" ");
            sql.append(beanProperty.getJdbcType());
            if (i != properties.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return sql.toString();
    }
}
