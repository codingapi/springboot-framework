package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    @Getter
    private int current;
    private int pageSize;

    @Getter
    private final Map<String, Filter> filters = new HashMap<>();

    @Getter
    private HttpServletRequest servletRequest;

    private org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current > 0 ? current-- : 0, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = org.springframework.data.domain.PageRequest.of(current, pageSize, sort);

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            this.servletRequest = attributes.getRequest();
            this.syncParameter();
        } catch (Exception e) {
        }
    }


    private void syncParameter() {
        Enumeration<String> enumeration = servletRequest.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = servletRequest.getParameter(key);
            if (StringUtils.hasText(value)) {
                addFilter(key, value);
            }
        }
    }

    public PageRequest() {
        this(0, 20, Sort.unsorted());
    }

    public void setCurrent(int current) {
        this.current = current > 0 ? current - 1 : 0;
    }

    public String getParameter(String key) {
        return servletRequest.getParameter(key);
    }

    public String getParameter(String key, String defaultValue) {
        String result = servletRequest.getParameter(key);
        return result == null ? defaultValue : result;
    }

    public int getIntParameter(String key) {
        return Integer.parseInt(servletRequest.getParameter(key));
    }

    public int getIntParameter(String key, int defaultValue) {
        String result = servletRequest.getParameter(key);
        return result == null ? defaultValue : Integer.parseInt(result);
    }


    public String getStringFilter(String key) {
        Filter filter = (Filter) filters.get(key);
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
        Filter filter = (Filter) filters.get(key);
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


    @Override
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Sort getSort() {
        return pageRequest.getSort();
    }

    @Override
    public org.springframework.data.domain.PageRequest next() {
        return new PageRequest(current + 1, getPageSize(), getSort());
    }

    @Override
    public org.springframework.data.domain.PageRequest previous() {
        return current == 0 ? this : new PageRequest(current - 1, getPageSize(), getSort());
    }

    @Override
    public org.springframework.data.domain.PageRequest first() {
        return new PageRequest(0, getPageSize(), getSort());
    }

    @Override
    public int getPageNumber() {
        return current;
    }

    @Override
    public long getOffset() {
        return (long) current * (long) pageSize;
    }

    @Override
    public boolean hasPrevious() {
        return current > 0;
    }

    @Override
    public Pageable previousOrFirst() {
        return pageRequest.previousOrFirst();
    }

    @Override
    public boolean isPaged() {
        return pageRequest.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return pageRequest.isUnpaged();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return pageRequest.getSortOr(sort);
    }

    @Override
    public Optional<Pageable> toOptional() {
        return pageRequest.toOptional();
    }

    public void addSort(Sort sort) {
        Sort nowSort = pageRequest.getSort();
        if (nowSort == Sort.unsorted()) {
            this.pageRequest = new PageRequest(getCurrent(), getPageSize(), sort);
        } else {
            pageRequest.getSort().and(sort);
        }
    }

    public PageRequest addFilter(String key, FilterRelation relation, Object... value) {
        putFilter(key, relation, value);
        return this;
    }

    public PageRequest addFilter(String key, Object... value) {
        return this.addFilter(key, FilterRelation.EUQAL, value);
    }

    public boolean hasFilter() {
        return !this.filters.isEmpty();
    }

    @Setter
    @Getter
    public static class Filter {
        private String key;
        private Object[] value;

        private FilterRelation relation;

        public boolean isEqual() {
            return relation == FilterRelation.EUQAL;
        }

        public boolean isLike() {
            return relation == FilterRelation.LIKE;
        }

        public boolean isBetween() {
            return relation == FilterRelation.BETWEEN;
        }

        public boolean isIn() {
            return relation == FilterRelation.IN;
        }

        public boolean isGreaterThan() {
            return relation == FilterRelation.GREATER_THAN;
        }

        public boolean isLessThan() {
            return relation == FilterRelation.LESS_THAN;
        }

        public boolean isGreaterThanEqual() {
            return relation == FilterRelation.GREATER_THAN_EQUAL;
        }

        public boolean isLessThanEqual() {
            return relation == FilterRelation.LESS_THAN_EQUAL;
        }

        public Object getFilterValue(Class<?> clazz) {
            Object val = value[0];
            if (val instanceof String) {
                if (clazz == Integer.class) {
                    return Integer.parseInt((String) val);
                }
                if (clazz == Long.class) {
                    return Long.parseLong((String) val);
                }
                if (clazz == Double.class) {
                    return Double.parseDouble((String) val);
                }
                if (clazz == Float.class) {
                    return Float.parseFloat((String) val);
                }
            }
            return value[0];
        }

    }

    private void putFilter(String key, FilterRelation relation, Object... val) {
        Filter filter = new Filter();
        filter.setKey(key);
        filter.setValue(val);
        filter.setRelation(relation);
        this.filters.put(key, filter);
    }

    public enum FilterRelation {
        EUQAL,
        LIKE,
        BETWEEN,
        IN,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_EQUAL,
        LESS_THAN_EQUAL,
    }


    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }
}