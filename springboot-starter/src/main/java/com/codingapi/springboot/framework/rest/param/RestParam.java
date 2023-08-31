package com.codingapi.springboot.framework.rest.param;

public interface RestParam {

    default RestParamBuilder toParameters() {
        return RestParamBuilder.parser(this);
    }

}
