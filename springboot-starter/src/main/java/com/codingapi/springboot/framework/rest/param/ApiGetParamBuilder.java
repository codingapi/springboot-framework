package com.codingapi.springboot.framework.rest.param;

import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.PropertyDescriptor;

public class ApiGetParamBuilder {

    private final MultiValueMap<String, String> uriVariables;

    private ApiGetParamBuilder() {
        this.uriVariables = new LinkedMultiValueMap<>();
    }

    public static ApiGetParamBuilder create() {
        return new ApiGetParamBuilder();
    }

    @SneakyThrows
    public static ApiGetParamBuilder parser(Object obj) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(obj.getClass());
        ApiGetParamBuilder builder = create();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            ;
            String value = BeanUtils.getProperty(obj, name);
            if (value != null) {
                builder.add(name, value);
            }
        }
        return builder;
    }

    public MultiValueMap<String, String> build() {
        return uriVariables;
    }

    public ApiGetParamBuilder add(String key, String value) {
        uriVariables.add(key, value);
        return this;
    }
}
