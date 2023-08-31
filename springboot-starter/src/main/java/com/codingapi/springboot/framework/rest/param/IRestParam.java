package com.codingapi.springboot.framework.rest.param;

public interface IRestParam {

    default RestParam toParameters() {
        return RestParam.parser(this);
    }

}
