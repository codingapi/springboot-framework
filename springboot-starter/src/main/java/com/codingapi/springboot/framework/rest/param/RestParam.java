package com.codingapi.springboot.framework.rest.param;

public interface RestParam {

    default RestParamBuilder getParameters() {
        return RestParamBuilder.parser(this);
    }

}
