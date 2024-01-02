package com.codingapi.springboot.fast.script;

import org.springframework.web.bind.annotation.RequestMethod;

public enum ScriptMethod {

    GET, POST;


    public RequestMethod toRequestMethod() {
        if (this == GET) {
            return RequestMethod.GET;
        } else {
            return RequestMethod.POST;
        }
    }

}
