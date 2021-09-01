package com.codingapi.springboot.framework.command;

import com.codingapi.springboot.framework.dto.response.Response;

public interface IExecutor<R extends Response,C> {

    R execute(C command);    
    
}
