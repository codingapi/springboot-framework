package com.codingapi.springboot.script.temp;

import com.codingapi.springboot.script.GroovyScript;
import lombok.Getter;

/**
 * 临时脚本对象
 * 数据将会自动清理
 */
@Getter
public class TempGroovyScript {

    private final GroovyScript groovyScript;
    private final long clearTime;

    public TempGroovyScript(GroovyScript groovyScript, long clearTime) {
        this.groovyScript = groovyScript;
        this.clearTime = clearTime;
    }

    public String getKey() {
        return this.groovyScript.getKey();
    }
}
