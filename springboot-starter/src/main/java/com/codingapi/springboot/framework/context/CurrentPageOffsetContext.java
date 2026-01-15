package com.codingapi.springboot.framework.context;

import lombok.Getter;
import lombok.Setter;

public class CurrentPageOffsetContext {

    @Getter
    private static final CurrentPageOffsetContext instance = new CurrentPageOffsetContext();

    private CurrentPageOffsetContext(){}

    @Setter
    private int offset;

    public int getCurrentPage(int current) {
        return current - offset;
    }
}
