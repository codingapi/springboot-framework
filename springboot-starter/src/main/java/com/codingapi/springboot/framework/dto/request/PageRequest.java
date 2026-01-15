package com.codingapi.springboot.framework.dto.request;

import com.codingapi.springboot.framework.dto.offset.ICurrentOffset;
import com.codingapi.springboot.framework.dto.offset.context.CurrentPageOffsetContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

public class PageRequest extends org.springframework.data.domain.PageRequest implements ICurrentOffset {

    @Getter
    private int current;

    @Setter
    private int pageSize;

    @Getter
    private Sort sort;

    @Getter
    private final RequestFilter requestFilter = new RequestFilter();


    public PageRequest(int current, int pageSize, Sort sort) {
        super(current, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
        this.sort = sort;
    }


    public PageRequest() {
        this(0, 20, Sort.unsorted());
    }

    public void setCurrent(int current) {
        this.current = CurrentPageOffsetContext.getInstance().getCurrentPage(this,current);
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

    public void addSort(Sort sort) {
        Sort nowSort = this.sort;
        if (nowSort == Sort.unsorted()) {
            this.sort = sort;
        } else {
            this.sort.and(sort);
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
