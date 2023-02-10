package com.codingapi.springboot.framework.rest.param;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.beans.PropertyDescriptor;

public class RestParamBuilder {

    private final JSONObject jsonBody;
    private final MultiValueMap<String, String> mapBody;

    private RestParamBuilder() {
        this.jsonBody = new JSONObject();
        this.mapBody = new LinkedMultiValueMap<>();
    }

    public static RestParamBuilder create() {
        return new RestParamBuilder();
    }

    @SneakyThrows
    public static RestParamBuilder parser(Object obj) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(obj.getClass());
        RestParamBuilder builder = create();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Object value = PropertyUtils.getProperty(obj, name);
            if (value != null) {
                builder.add(name, value);
            }
        }
        return builder;
    }

    public JSONObject toJsonRequest() {
        return jsonBody;
    }

    public MultiValueMap<String, String>  toFormRequest() {
        return mapBody;
    }

    public RestParamBuilder add(String key, Object value) {
        jsonBody.put(key, value);
        mapBody.add(key, value.toString());
        return this;
    }


}
