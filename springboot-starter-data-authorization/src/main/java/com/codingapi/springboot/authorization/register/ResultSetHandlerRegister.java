package com.codingapi.springboot.authorization.register;


import com.codingapi.springboot.authorization.handler.ColumnHandler;
import com.codingapi.springboot.authorization.handler.ColumnHandlerContext;

public class ResultSetHandlerRegister {

    public ResultSetHandlerRegister(ColumnHandler columnHandler){
        if(columnHandler !=null) {
            ColumnHandlerContext.getInstance().setColumnHandler(columnHandler);
        }
    }

}
