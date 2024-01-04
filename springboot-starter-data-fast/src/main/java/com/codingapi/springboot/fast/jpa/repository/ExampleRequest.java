package com.codingapi.springboot.fast.jpa.repository;

import com.codingapi.springboot.framework.dto.request.Filter;
import com.codingapi.springboot.framework.dto.request.PageRequest;
import com.codingapi.springboot.framework.dto.request.RequestFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;

import java.beans.PropertyDescriptor;

public class ExampleRequest {

    private final PageRequest request;
    private final Class<?> clazz;

    public ExampleRequest(PageRequest request, Class<?> clazz) {
        this.request = request;
        this.clazz = clazz;
    }

    public <T> Example<T> getExample() {
        RequestFilter requestFilter = request.getRequestFilter();
        if (!requestFilter.hasFilter()) {
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
            Filter value = requestFilter.getFilter(name);
            if (value != null) {
                try {
                    descriptor.getWriteMethod().invoke(entity, value.getFilterValue(descriptor.getPropertyType()));
                } catch (Exception e) {
                }
            }
        }
        return (Example<T>) Example.of(entity);
    }

}