package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.annotation.ScriptFunction;

public class BaseRequest {

    @ScriptFunction(
            name = "hello",
            description = "测试"
    )
    public void hello() {
        System.out.println("hello");
    }
}
