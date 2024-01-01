package com.codingapi.springboot.fast.mapping;

import com.codingapi.springboot.framework.dto.response.Response;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

@Getter
public abstract class BaseMapping {

    protected String mapping;
    protected RequestMethod requestMethod;

    public BaseMapping(String mapping, RequestMethod requestMethod) {
        this.mapping = mapping;
        this.requestMethod = requestMethod;
    }


    @ResponseBody
    public abstract Response execute();

    public Method getExecuteMethod() {
        try {
            return this.getClass().getDeclaredMethod("execute");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
