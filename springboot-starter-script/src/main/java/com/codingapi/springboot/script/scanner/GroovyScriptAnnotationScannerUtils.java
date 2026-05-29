package com.codingapi.springboot.script.scanner;

import com.codingapi.springboot.framework.reflect.ObjectAnnotationFieldUtils;
import com.codingapi.springboot.script.annotation.GroovyScript;

/**
 * {@link GroovyScript} 注解扫描工具类型
 */
public class GroovyScriptAnnotationScannerUtils {

    public static GroovyScriptFieldResult findGroovyScriptFields(Object target) {
        return new GroovyScriptFieldResult(ObjectAnnotationFieldUtils.findFieldAnnotationValue(target, GroovyScript.class, String.class));
    }

}
