package com.codingapi.springboot.fast.jpa.map;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class QueryColumnsContext {
    private final Map<String, QueryColumns> cache = new HashMap<>();
    @Getter
    private final static QueryColumnsContext instance = new QueryColumnsContext();

    private QueryColumnsContext() {

    }

    public static QueryColumns build(String ... columns) {
        QueryColumns mapColumn = new QueryColumns();
        for (String column : columns) {
            mapColumn.addColumn(column);
        }
        QueryColumnsContext.getInstance().cache.put(mapColumn.getKey(), mapColumn);
        return mapColumn;
    }


    QueryColumns getQueryColumns(String key) {
        return this.cache.get(key);
    }

    public void clearCache(String key) {
        this.cache.remove(key);
    }
}
