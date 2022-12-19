package com.codingapi.springboot.framework.rest.param;

public interface ApiPostParam {

    default ApiPostParamBuilder getParameters() {
        return ApiPostParamBuilder.parser(this);
    }

}
