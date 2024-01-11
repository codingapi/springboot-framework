package com.codingapi.springboot.framework.dto.request;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestFilter {

    private final Map<String, Filter> filterMap = new HashMap<>();
    private final List<Filter> filterList = new ArrayList<>();

    public RequestFilter addFilter(String key, Relation relation, Object... value) {
        this.pushFilter(new Filter(key, relation, value));
        return this;
    }

    public RequestFilter addFilter(String key, Object... value) {
        this.pushFilter(new Filter(key, value));
        return this;
    }

    public RequestFilter andFilters(Filter... value) {
        this.pushFilter(new Filter(Filter.FILTER_AND_KEY, value));
        return this;
    }

    public RequestFilter orFilters(Filter... value) {
        this.pushFilter(new Filter(Filter.FILTER_OR_KEY, value));
        return this;
    }

    public List<Filter> getFilters() {
        return filterList;
    }

    public void pushFilter(Filter filter) {
        filterList.removeIf(item -> item.getKey().equals(filter.getKey()));
        filterList.add(filter);
        filterMap.put(filter.getKey(), filter);
    }


    public String getStringFilter(String key) {
        Filter filter = filterMap.get(key);
        if (filter != null) {
            return (String) filter.getValue()[0];
        }
        return null;
    }

    public String getStringFilter(String key, String defaultValue) {
        String value = getStringFilter(key);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return value;
    }

    public int getIntFilter(String key) {
        Filter filter = filterMap.get(key);
        if (filter != null) {
            String value = (String) filter.getValue()[0];
            if (StringUtils.hasText(value)) {
                return Integer.parseInt(value);
            }
            return 0;
        }
        return 0;
    }

    public int getIntFilter(String key, int defaultValue) {
        int value = getIntFilter(key);
        if (value == 0) {
            return defaultValue;
        }
        return value;
    }


    public boolean hasFilter() {
        return !this.filterMap.isEmpty();
    }


    public Filter getFilter(String name) {
        return this.filterMap.get(name);
    }

    public void removeFilter(String key) {
        this.filterMap.remove(key);
        this.filterList.removeIf(item -> item.getKey().equals(key));
    }
}
