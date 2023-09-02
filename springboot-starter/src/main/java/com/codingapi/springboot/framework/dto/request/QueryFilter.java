package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class QueryFilter {

    private final Map<String,Object> filters = new HashMap<>();

    public QueryFilter add(String key,Object value){
        filters.put(key, value);
        return this;
    }

    public static QueryFilter of(String key,Object value){
        QueryFilter filter = new QueryFilter();
        filter.add(key, value);
        return filter;
    }

    public Object getValue(String key) {
        return filters.get(key);
    }
}
