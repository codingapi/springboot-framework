package com.codingapi.springboot.framework.script.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  运行时绑定对象
 */
@Getter
@AllArgsConstructor
public class GroovyBindObject {

    /**
     * 绑定的访问对象
     */
    private String key;
    /**
     * 绑定的对象实例
     */
    private Object object;

}
