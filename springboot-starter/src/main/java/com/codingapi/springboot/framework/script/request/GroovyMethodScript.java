package com.codingapi.springboot.framework.script.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 脚本执行对象
 *
 * @param <T> 返回格式
 */
@Getter
public class GroovyMethodScript<T> {

    private final String method;

    private final String script;

    private final Class<T> returnType;

    private final List<Object> request;

    private final List<RuntimeBindObject> binds;

    public GroovyMethodScript(String method, String script, Class<T> returnType, Object... request) {
        this.method = method;
        this.script = script;
        this.returnType = returnType;
        this.request = new ArrayList<>();
        this.binds = new ArrayList<>();
        if (request != null) {
            this.request.addAll(Arrays.asList(request));
        }
    }

    public GroovyMethodScript(String script, Class<T> returnType, Object... request) {
        this("run", script, returnType, request);
    }

    /**
     * 获取参数
     */
    public Object[] getParams() {
        return this.request.toArray();
    }

    /**
     * 添加绑定对象
     * @param key 绑定key
     * @param bind 绑定对象
     */
    public void addBindObject(String key, Object bind) {
        this.binds.add(new RuntimeBindObject(key, bind));
    }


}
