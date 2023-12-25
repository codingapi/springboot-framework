package com.codingapi.springboot.persistence.schema;

import com.codingapi.springboot.persistence.property.BeanProperty;
import com.codingapi.springboot.persistence.property.SchemaProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SaveSchema  {

    protected final SchemaProperty property;

    public SaveSchema(Schema schema) {
        this.property = schema.getSchemaProperty();
    }

    public String schema() {
        return schema(true);
    }

    public abstract String schema(boolean hasId);


    public Object[] getSaveValues(Object object, boolean hasId) {
        List<Object> values = new ArrayList<>();
        for (BeanProperty beanProperty : property.getProperties(hasId)) {
            values.add(beanProperty.get(object));
        }
        return values.toArray();
    }

    public Object[] getSaveValues(Object object) {
        return getSaveValues(object, true);
    }
}
