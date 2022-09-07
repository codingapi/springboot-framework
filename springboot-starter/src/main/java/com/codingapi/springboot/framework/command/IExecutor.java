package com.codingapi.springboot.framework.command;

import com.codingapi.springboot.framework.dto.response.Response;

public interface IExecutor {

    interface Command<R extends Response, C> {
        R execute(C command);
    }

    interface Void<R extends Response> {
        R execute();
    }

}
