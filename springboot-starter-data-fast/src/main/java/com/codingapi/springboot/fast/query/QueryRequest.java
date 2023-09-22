package com.codingapi.springboot.fast.query;

import com.codingapi.springboot.framework.dto.request.PageRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;

import java.beans.PropertyDescriptor;

public class QueryRequest {

    private final PageRequest request;

    public QueryRequest(PageRequest request) {
        this.request = request;
    }

    public <T> Example<T> getExample(Class<T> clazz) {
        if (!request.hasFilter()) {
            return null;
        }
        Object entity = null;
        try {
            entity = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            PageRequest.Filter value = request.getFilters().get(name);
            if (value != null) {
                try {
                    descriptor.getWriteMethod().invoke(entity, value.getValue()[0]);
                } catch (Exception e) {
                }
            }
        }
        return (Example<T>) Example.of(entity);
    }
}
