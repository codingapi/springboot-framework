package com.codingapi.springboot.framework.dto.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    private int current;
    private int pageSize;

    private final org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = PageRequest.of(current, pageSize, sort);
    }

    public PageRequest() {
        this(0, 20, Sort.unsorted());
    }

    public static org.springframework.data.domain.PageRequest of(int page, int size) {
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

    public static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }

    public static org.springframework.data.domain.PageRequest of(int page, int size, Sort.Direction direction, String... properties) {
        return org.springframework.data.domain.PageRequest.of(page, size, direction, properties);
    }

    public static org.springframework.data.domain.PageRequest ofSize(int pageSize) {
        return org.springframework.data.domain.PageRequest.ofSize(pageSize);
    }

    public static Pageable unpaged() {
        return Pageable.unpaged();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
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
        return pageRequest.next();
    }

    @Override
    public org.springframework.data.domain.PageRequest previous() {
        return pageRequest.previous();
    }

    @Override
    public org.springframework.data.domain.PageRequest first() {
        return pageRequest.first();
    }

    @Override
    public boolean equals(Object obj) {
        return pageRequest.equals(obj);
    }

    @Override
    public org.springframework.data.domain.PageRequest withPage(int pageNumber) {
        return pageRequest.withPage(pageNumber);
    }

    @Override
    public org.springframework.data.domain.PageRequest withSort(Sort.Direction direction, String... properties) {
        return pageRequest.withSort(direction, properties);
    }

    @Override
    public org.springframework.data.domain.PageRequest withSort(Sort sort) {
        return pageRequest.withSort(sort);
    }

    @Override
    public int hashCode() {
        return pageRequest.hashCode();
    }

    @Override
    public String toString() {
        return pageRequest.toString();
    }

    @Override
    public int getPageNumber() {
        return pageRequest.getPageNumber();
    }

    @Override
    public long getOffset() {
        return pageRequest.getOffset();
    }

    @Override
    public boolean hasPrevious() {
        return pageRequest.hasPrevious();
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

