package com.codingapi.springboot.script.parser;

import com.codingapi.springboot.framework.reflect.ObjectAnnotationFieldUtils;
import com.codingapi.springboot.script.annotation.GroovyScript;
import com.codingapi.springboot.script.parser.pojo.GroovyScriptFieldResult;

/**
 * {@link GroovyScript} 注解扫描工具类型
 */
public class GroovyScriptAnnotationUtils {

    public static GroovyScriptFieldResult findGroovyScriptFields(Object target) {
        return new GroovyScriptFieldResult(ObjectAnnotationFieldUtils.findFieldAnnotationValue(target, GroovyScript.class, String.class));
    }

}
