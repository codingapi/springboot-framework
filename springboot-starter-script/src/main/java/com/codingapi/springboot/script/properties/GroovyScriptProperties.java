package com.codingapi.springboot.script.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroovyScriptProperties {

    /**
     * 临时脚本到期时间，默认15分钟
     */
    private long tempValidTime = 1000 * 60 * 15;

    /**
     * 脚本执行对象最大缓存大小
     */
    private int shellMaxCacheSize = 10 * 1024;


}
