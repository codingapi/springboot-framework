package com.codingapi.springboot.framework.rest.param;

public interface ApiGetParam {

    default ApiGetParamBuilder getParameters() {
        return ApiGetParamBuilder.parser(this);
    }

}
