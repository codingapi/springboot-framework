package com.codingapi.springboot.framework.dto.offset;

public interface ICurrentPageOffset {

    int getCurrentPage(ICurrentOffset target, int current);
}
