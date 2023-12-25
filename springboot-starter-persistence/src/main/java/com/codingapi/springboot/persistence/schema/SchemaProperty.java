package com.codingapi.springboot.persistence.schema;

import lombok.Getter;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.List;
import java.util.stream.Collectors;


public class SchemaProperty {

    private final List<Property> properties;
    @Getter
    private final String schemaName;
    @Getter
    private final Property idProperty;

    public SchemaProperty(Class<?> domainClazz) {
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        this.properties = propertyUtils.getProperties(domainClazz).stream()
                .map(Property::new).collect(Collectors.toList());
        this.idProperty = new Property(propertyUtils.getProperty(domainClazz, "id"));
        this.schemaName = domainClazz.getSimpleName();
    }


    public List<Property> getProperties(boolean hasId) {
        if(hasId){
            return properties;
        }else{
            return properties.stream()
                    .filter(property -> !property.getName().equals("id"))
                    .collect(Collectors.toList());
        }
    }

    public List<Property> getProperties() {
        return getProperties(true);
    }

    public boolean hasIdValue(Object domain) {
        return idProperty.hasIdValue(domain);
    }

    public void setIdValue(Object domain, Number key) {
        idProperty.setIdValue(domain, key);
    }
}
