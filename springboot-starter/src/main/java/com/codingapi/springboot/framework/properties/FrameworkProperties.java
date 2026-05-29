package com.codingapi.springboot.framework.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrameworkProperties {

    /**
     *  事件异步线程数量，默认20
     */
    private int handlerThreadPoolSize = 20;


}
