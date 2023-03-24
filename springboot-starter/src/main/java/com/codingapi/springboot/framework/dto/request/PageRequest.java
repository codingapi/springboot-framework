package com.codingapi.springboot.framework.dto.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    private int current;
    private int pageSize;

    private final org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current>0?current--:0, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = PageRequest.of(current, pageSize, sort);
    }

    public PageRequest() {
        this(0, 20, Sort.unsorted());
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current>0?current-1:0;
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
        return current == 0? this: new PageRequest(current-1,getPageSize(),getSort());
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
}

