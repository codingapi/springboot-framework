package com.codingapi.springboot.persistence.property;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Property {
    
    private final org.yaml.snakeyaml.introspector.Property property;

    public Property(org.yaml.snakeyaml.introspector.Property property) {
        this.property = property;
    }
    
    public boolean hasIdValue(Object domain) {
        if (property.getType().equals(int.class))
            return property.get(domain) != null && (int) property.get(domain) > 0;
        else if (property.getType().equals(long.class))
            return property.get(domain) != null && (long) property.get(domain) > 0;
        else if (property.getType().equals(double.class))
            return property.get(domain) != null && (double) property.get(domain) > 0;
        else if (property.getType().equals(float.class))
            return property.get(domain) != null && (float) property.get(domain) > 0;
        else if (property.getType().equals(short.class))
            return property.get(domain) != null && (short) property.get(domain) > 0;
        else if (property.getType().equals(byte.class))
            return property.get(domain) != null && (byte) property.get(domain) > 0;
        else if (property.getType().equals(boolean.class))
            return property.get(domain) != null && (boolean) property.get(domain);
        else if (property.getType().equals(Integer.class))
            return property.get(domain) != null && (Integer) property.get(domain) > 0;
        else if (property.getType().equals(Long.class))
            return property.get(domain) != null && (Long) property.get(domain) > 0;
        else if (property.getType().equals(Double.class))
            return property.get(domain) != null && (Double) property.get(domain) > 0;
        else if (property.getType().equals(Float.class))
            return property.get(domain) != null && (Float) property.get(domain) > 0;
        else if (property.getType().equals(Short.class))
            return property.get(domain) != null && (Short) property.get(domain) > 0;
        else if (property.getType().equals(Byte.class))
            return property.get(domain) != null && (Byte) property.get(domain) > 0;
        else if (property.getType().equals(Boolean.class))
            return property.get(domain) != null && (Boolean) property.get(domain);
        else if (property.getType().equals(String.class))
            return property.get(domain) != null && !((String) property.get(domain)).isEmpty();
        else if (property.getType().equals(Object.class))
            return property.get(domain) != null;
        else
            throw new RuntimeException("not support type");
    }


    public void setIdValue(Object domain, Number key) {
        try {
            if (property.getType().equals(int.class))
                property.set(domain, key.intValue());
            else if (property.getType().equals(long.class))
                property.set(domain, key.longValue());
            else if (property.getType().equals(double.class))
                property.set(domain, key.doubleValue());
            else if (property.getType().equals(float.class))
                property.set(domain, key.floatValue());
            else if (property.getType().equals(short.class))
                property.set(domain, key.shortValue());
            else if (property.getType().equals(byte.class))
                property.set(domain, key.byteValue());
            else if (property.getType().equals(boolean.class))
                property.set(domain, key.byteValue());
            else if (property.getType().equals(Integer.class))
                property.set(domain, key.intValue());
            else if (property.getType().equals(Long.class))
                property.set(domain, key.longValue());
            else if (property.getType().equals(Double.class))
                property.set(domain, key.doubleValue());
            else if (property.getType().equals(Float.class))
                property.set(domain, key.floatValue());
            else if (property.getType().equals(Short.class))
                property.set(domain, key.shortValue());
            else if (property.getType().equals(Byte.class))
                property.set(domain, key.byteValue());
            else if (property.getType().equals(Boolean.class))
                property.set(domain, key.byteValue());
            else if (property.getType().equals(String.class))
                property.set(domain, key.toString());
            else if (property.getType().equals(Object.class))
                property.set(domain, key);
        } catch (Exception e) {
            log.error("setIdValue error", e);
        }
    }
    

    public String getName() {
        return property.getName();
    }

    public Object get(Object domain) {
        try {
            return property.get(domain);
        } catch (Exception e) {
            log.error("get error", e);
            return null;
        }
    }

    public String getJdbcType(){
        if (property.getType().equals(int.class))
            return "INT";
        else if (property.getType().equals(long.class))
            return "BIGINT";
        else if (property.getType().equals(double.class))
            return "DOUBLE";
        else if (property.getType().equals(float.class))
            return "FLOAT";
        else if (property.getType().equals(short.class))
            return "SMALLINT";
        else if (property.getType().equals(byte.class))
            return "TINYINT";
        else if (property.getType().equals(boolean.class))
            return "BOOLEAN";
        else if (property.getType().equals(Integer.class))
            return "INT";
        else if (property.getType().equals(Long.class))
            return "BIGINT";
        else if (property.getType().equals(Double.class))
            return "DOUBLE";
        else if (property.getType().equals(Float.class))
            return "FLOAT";
        else if (property.getType().equals(Short.class))
            return "SMALLINT";
        else if (property.getType().equals(Byte.class))
            return "TINYINT";
        else if (property.getType().equals(Boolean.class))
            return "BOOLEAN";
        else if (property.getType().equals(String.class))
            return "VARCHAR(255)";
        else if (property.getType().equals(Object.class))
            return "VARCHAR(255)";
        else
            throw new RuntimeException("not support type");
    }
}
