package com.codingapi.springboot.persistence.property;

import lombok.Getter;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.List;
import java.util.stream.Collectors;


public class SchemaProperty {

    private final List<BeanProperty> properties;
    @Getter
    private final String schemaName;
    @Getter
    private final BeanProperty idBeanProperty;

    public SchemaProperty(Class<?> domainClazz) {
        PropertyUtils propertyUtils = new PropertyUtils();
        propertyUtils.setSkipMissingProperties(true);
        this.properties = propertyUtils.getProperties(domainClazz).stream()
                .map(BeanProperty::new).collect(Collectors.toList());
        this.idBeanProperty = new BeanProperty(propertyUtils.getProperty(domainClazz, "id"));
        this.schemaName = domainClazz.getSimpleName();
    }


    public List<BeanProperty> getProperties(boolean hasId) {
        if(hasId){
            return properties;
        }else{
            return properties.stream()
                    .filter(beanProperty -> !beanProperty.getName().equals("id"))
                    .collect(Collectors.toList());
        }
    }

    public List<BeanProperty> getProperties() {
        return getProperties(true);
    }

    public boolean hasIdValue(Object domain) {
        return idBeanProperty.hasIdValue(domain);
    }

    public void setIdValue(Object domain, Number key) {
        idBeanProperty.setIdValue(domain, key);
    }
}
