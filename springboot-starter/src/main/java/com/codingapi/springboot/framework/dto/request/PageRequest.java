package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    @Getter
    @Setter
    private int current;

    @Setter
    @Getter
    private int pageSize;

    @Getter
    private final RequestFilter requestFilter = new RequestFilter();


    private org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = org.springframework.data.domain.PageRequest.of(current, pageSize, sort);
    }


    public PageRequest() {
        this(0, 20, Sort.unsorted());
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
    public Sort getSort() {
        return pageRequest.getSort();
    }

    @Override
    public PageRequest next() {
        return new PageRequest(current + 1, getPageSize(), getSort());
    }

    @Override
    public PageRequest previous() {
        return current == 0 ? this : new PageRequest(current - 1, getPageSize(), getSort());
    }

    @Override
    public PageRequest first() {
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

    public void removeFilter(String key) {
        requestFilter.removeFilter(key);
    }

    public PageRequest addFilter(String key, Relation relation, Object... value) {
        requestFilter.addFilter(key, relation, value);
        return this;
    }

    public PageRequest addFilter(String key, Object... value) {
        requestFilter.addFilter(key, value);
        return this;
    }

    public PageRequest andFilter(Filter... filters) {
        requestFilter.andFilters(filters);
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