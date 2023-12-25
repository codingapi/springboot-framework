package com.codingapi.springboot.persistence.schema;

import com.codingapi.springboot.persistence.property.BeanProperty;
import com.codingapi.springboot.persistence.property.SchemaProperty;

public abstract class UpdateSchema {

    public abstract String updateSchema();

    protected final SchemaProperty property;

    public UpdateSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }

    public Object[] getUpdateValues(Object object) {
        BeanProperty idBeanProperty = property.getIdBeanProperty();
        Object[] values = new Object[property.getProperties(true).size()];
        int i = 0;
        for (BeanProperty beanProperty : property.getProperties(false)) {
            values[i] = beanProperty.get(object);
            i++;
        }
        values[i] = idBeanProperty.get(object);
        return values;
    }


}
