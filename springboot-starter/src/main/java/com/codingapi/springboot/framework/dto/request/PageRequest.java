package com.codingapi.springboot.framework.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class PageRequest extends org.springframework.data.domain.PageRequest {

    private int current;
    private int pageSize;

    public PageRequest(int current, int pageSize, Sort sort) {
        super(current, pageSize, sort);
        this.current = current;
        this.pageSize = pageSize;
    }

    public PageRequest() {
        this(0,20,Sort.unsorted());
    }

}

