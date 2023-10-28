package com.codingapi.springboot.framework.dto.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    @Getter
    private int current;
    private int pageSize;

    @Getter
    private final RequestFilter requestFilter = new RequestFilter();

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
            requestFilter.syncParameter(servletRequest);
        } catch (Exception e) {
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
        return requestFilter.getStringFilter(key);
    }

    public String getStringFilter(String key, String defaultValue) {
        return requestFilter.getStringFilter(key, defaultValue);
    }

    public int getIntFilter(String key) {
        return requestFilter.getIntFilter(key);
    }

    public int getIntFilter(String key, int defaultValue) {
        return requestFilter.getIntFilter(key, defaultValue);
    }

    public boolean hasFilter() {
        return requestFilter.hasFilter();
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

    public PageRequest andFilter(String key, Relation relation, Object... value) {
        requestFilter.addFilter(key, relation, value);
        return this;
    }

    public PageRequest addFilter(String key, Object... value) {
        requestFilter.addFilter(key, value);
        return this;
    }

    public PageRequest andFilter(Filter... value) {
        requestFilter.andFilters(value);
        return this;
    }

    public PageRequest orFilters(Filter... filters) {
        requestFilter.orFilters(filters);
        return this;
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }
}