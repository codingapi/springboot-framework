package com.codingapi.springboot.script.meta;

import lombok.Getter;
import lombok.Setter;

/**
 * 脚本对象类型
 */
@Getter
@Setter
public class GroovyField {

    /**
     * 访问名称
     */
    private String name;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 数据类型
     */
    private String dataType;



}
