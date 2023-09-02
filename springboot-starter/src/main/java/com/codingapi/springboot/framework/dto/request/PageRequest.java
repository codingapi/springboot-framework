package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.beans.PropertyDescriptor;
import java.util.Optional;

public class PageRequest extends org.springframework.data.domain.PageRequest {

    @Getter
    private int current;
    private int pageSize;
    private QueryFilter filter;

    private org.springframework.data.domain.PageRequest pageRequest;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current > 0 ? current-- : 0, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.pageRequest = PageRequest.of(current, pageSize, sort);
    }

    public PageRequest() {
        this(0, 20, Sort.unsorted());
    }

    public void setCurrent(int current) {
        this.current = current > 0 ? current - 1 : 0;
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
        }else{
            pageRequest.getSort().and(sort);
        }
    }

    public void addFilter(QueryFilter filter){
        this.filter = filter;
    }

    public boolean hasFilter(){
        return this.filter!=null;
    }

    public <T> Example<T> getExample(Class<T> clazz){
        if(this.filter ==null){
            return null;
        }
        try {
            Object entity = clazz.getDeclaredConstructor().newInstance();
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
            for (PropertyDescriptor descriptor : descriptors) {
                String name = descriptor.getName();
                Object value = filter.getValue(name);
                if (value != null) {
                    descriptor.getWriteMethod().invoke(entity,value);
                }
            }
            return (Example<T>) Example.of(entity);
        }catch (Exception e){
            return null;
        }
    }
}

