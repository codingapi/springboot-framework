package com.codingapi.springboot.flow.error;

public abstract class ErrorResult {

    public boolean isNode(){
        return this instanceof NodeResult;
    }

    public boolean isOperator(){
        return this instanceof OperatorResult;
    }

}
