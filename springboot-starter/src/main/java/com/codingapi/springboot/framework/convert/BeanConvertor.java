package com.codingapi.springboot.framework.convert;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class BeanConvertor {

    public static <T, S> T convert(S source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            return null;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
