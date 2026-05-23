package com.codingapi.springboot.framework.script.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  运行时绑定对象
 */
@Getter
@AllArgsConstructor
public class RuntimeBindObject {

    private String key;
    private Object object;
}
