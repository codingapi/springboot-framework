package com.codingapi.springboot.authorization.handler;

import lombok.Getter;
import lombok.Setter;

@Setter
public class RowHandlerContext {

    @Getter
    private final static RowHandlerContext instance = new RowHandlerContext();

    @Getter
    private RowHandler rowHandler;

    private RowHandlerContext() {
        this.rowHandler = new DefaultRowHandler();
    }
}
