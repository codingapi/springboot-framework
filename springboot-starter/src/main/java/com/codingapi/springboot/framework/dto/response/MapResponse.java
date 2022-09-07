package com.codingapi.springboot.framework.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class MapResponse extends Response {

    private Map<String, Object> data;

    public MapResponse() {
        this.data = new HashMap<>();
    }

    public static MapResponse create() {
        MapResponse singleResponse = new MapResponse();
        singleResponse.setSuccess(true);
        return singleResponse;
    }

    public static MapResponse empty() {
        MapResponse mapResponse = new MapResponse();
        mapResponse.setSuccess(true);
        mapResponse.setData(null);
        return mapResponse;
    }

    public MapResponse add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
