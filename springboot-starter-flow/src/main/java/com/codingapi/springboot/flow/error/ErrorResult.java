package com.codingapi.springboot.flow.error;


/**
 * 异常匹配的结果对象
 */
public abstract class ErrorResult {

    public boolean isNode(){
        return this instanceof NodeResult;
    }

    public boolean isOperator(){
        return this instanceof OperatorResult;
    }

}
