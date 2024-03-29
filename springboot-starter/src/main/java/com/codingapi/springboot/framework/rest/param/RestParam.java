package com.codingapi.springboot.framework.rest.param;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class RestParam {

    private final JSONObject jsonBody;
    private final MultiValueMap<String, String> mapBody;

    private RestParam() {
        this.jsonBody = new JSONObject();
        this.mapBody = new LinkedMultiValueMap<>();
    }

    public static RestParam create() {
        return new RestParam();
    }

    public static RestParam parser(Object obj) {
        RestParam builder = create();
        JSONObject object = (JSONObject) JSONObject.toJSON(obj);
        fetch(object, builder);
        return builder;
    }


    private static void fetch(JSONObject object, RestParam builder) {
        for (String key : object.keySet()) {
            Object value = object.getObject(key, Object.class);
            if (value != null) {
                builder.add(key, value);
            }
        }
    }

    public JSONObject toJsonRequest() {
        return jsonBody;
    }

    public MultiValueMap<String, String> toFormRequest() {
        return mapBody;
    }

    public RestParam add(String key, Object value) {
        return add(key, value, true);
    }

    public RestParam add(String key, Object value, boolean encode) {
        String stringValue = value.toString();
        String encodeValue = encode ? URLEncoder.encode(stringValue, StandardCharsets.UTF_8) : value.toString();
        jsonBody.put(key, value);
        mapBody.add(key, encodeValue);
        return this;
    }

}
