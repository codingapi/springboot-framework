package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SearchRequestTest {



    @Test
    void test(){
        Class<?> type = getKeyType(Demo.class,"deleted");
        System.out.println(type);
    }

    private Class<?> getKeyType(Class<?> clazz, String key) {
        String[] keys = key.split("\\.");
        Class<?> keyClass = clazz;

        for (String k : keys) {
            keyClass = findFieldInHierarchy(keyClass, k);

            if (keyClass == null) {
                throw new IllegalArgumentException("Field " + k + " not found in class hierarchy.");
            }
        }
        return keyClass;
    }

    private Class<?> findFieldInHierarchy(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;

        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    return field.getType();
                }
            }
            currentClass = currentClass.getSuperclass(); // 向上查找父类
        }
        return null;
    }


    @Setter
    @Getter
    public class Demo extends BaseEntity{

        private long id;
        private String name;

    }

    @Setter
    @Getter
    public class BaseEntity{

        private int state;
        private boolean deleted;
    }
}
