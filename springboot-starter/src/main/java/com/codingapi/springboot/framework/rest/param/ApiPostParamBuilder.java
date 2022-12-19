package com.codingapi.springboot.framework.rest.param;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;

public class ApiPostParamBuilder {

    private final JSONObject jsonObject;

    private ApiPostParamBuilder() {
        this.jsonObject = new JSONObject();
    }

    public static ApiPostParamBuilder create() {
        return new ApiPostParamBuilder();
    }

    @SneakyThrows
    public static ApiPostParamBuilder parser(Object obj) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(obj.getClass());
        ApiPostParamBuilder builder = create();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Object value = PropertyUtils.getProperty(obj, name);
            if (value != null) {
                builder.add(name, value);
            }
        }
        return builder;
    }

    public JSONObject build() {
        return jsonObject;
    }

    public ApiPostParamBuilder add(String key, Object value) {
        jsonObject.put(key, value);
        return this;
    }

}
