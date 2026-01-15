package com.codingapi.springboot.framework.context;

import com.codingapi.springboot.framework.dto.ICurrentOffset;

public interface ICurrentPageOffset {

    int getCurrentPage(ICurrentOffset target, int current);
}
