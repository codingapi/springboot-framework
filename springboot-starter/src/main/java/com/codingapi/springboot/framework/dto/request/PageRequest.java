package com.codingapi.springboot.framework.dto.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    @Getter
    private int current;
    private int pageSize;

    private final Map<String, Object> filters = new HashMap<>();

    @Getter
    private HttpServletRequest servletRequest;

    private org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current > 0 ? current-- : 0, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = PageRequest.of(current, pageSize, sort);

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
                this.filters.put(key, value);
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

    public Map<String, Object> getFilters() {
        return filters;
    }

    public String getStringFilter(String key) {
        return (String) filters.get(key);
    }

    public String getStringFilter(String key, String defaultValue) {
        String result = (String) filters.get(key);
        return result == null ? defaultValue : result;
    }

    public int getIntFilter(String key) {
        return Integer.parseInt((String) filters.get(key));
    }

    public int getIntFilter(String key, int defaultValue) {
        String result = (String) filters.get(key);
        return result == null ? defaultValue : Integer.parseInt(result);
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

    public PageRequest addFilter(String key, Object value) {
        this.filters.put(key, value);
        return this;
    }

    public boolean hasFilter() {
        return !this.filters.isEmpty();
    }

    public <T> Example<T> getExample(Class<T> clazz) {
        if (!hasFilter()) {
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
            Object value = filters.get(name);
            if (value != null) {
                try {
                    descriptor.getWriteMethod().invoke(entity, value);
                } catch (Exception e) {
                }
            }
        }
        return (Example<T>) Example.of(entity);

    }
}

