package com.codingapi.springboot.fast.script;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScriptRequest {

    private final HttpServletRequest request;

    public String getParameter(String key, String defaultValue) {
        String result = request.getParameter(key);
        return result == null ? defaultValue : result;
    }

}
