package com.codingapi.springboot.authorization.register;


import com.codingapi.springboot.authorization.handler.RowHandler;
import com.codingapi.springboot.authorization.handler.RowHandlerContext;

public class ConditionHandlerRegister {

    public ConditionHandlerRegister(RowHandler rowHandler) {
        if (rowHandler != null) {
            RowHandlerContext.getInstance().setRowHandler(rowHandler);
        }
    }
}
