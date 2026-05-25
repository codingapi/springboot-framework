package com.codingapi.springboot.framework.script.service;

import com.codingapi.springboot.framework.script.annotation.ScriptFunction;
import com.codingapi.springboot.framework.script.annotation.ScriptType;
import com.codingapi.springboot.framework.script.meta.GroovyFunction;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 脚本类型解析对象
 */
public class GroovyTypeParser {

    private final Class<?> clazz;

    private final GroovyType object;

    public GroovyTypeParser(Class<?> clazz) {
        this.clazz = clazz;
        this.object = TempGroovyTypeCache.getInstance().getOrCreate(clazz);
        this.object.setDataType(clazz);
    }


    private void resetObjectMeta(GroovyType target, ScriptType scriptType) {
        target.setDataType(scriptType.dataType());
        target.setName(scriptType.name());
        target.setDescription(scriptType.description());
    }

    private void loadObjectMeta() {
        ScriptType scriptType = clazz.getAnnotation(ScriptType.class);
        if (scriptType != null) {
            this.resetObjectMeta(object, scriptType);
        }
    }

    private void loadFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        for (Field field : fields) {
            ScriptType scriptType = field.getAnnotation(ScriptType.class);
            if (scriptType != null) {
                GroovyType groovyType = TempGroovyTypeCache.getInstance().getOrCreate(scriptType.dataType());
                this.resetObjectMeta(groovyType, scriptType);
                this.object.addField(groovyType);
            }
        }
    }

    private void loadMethods() {
        Method[] methods = this.clazz.getDeclaredMethods();
        for (Method method : methods) {
            ScriptFunction scriptFunction = method.getAnnotation(ScriptFunction.class);
            if (scriptFunction != null) {
                GroovyFunction groovyFunction = new GroovyFunction();
                if (StringUtils.hasText(scriptFunction.name())) {
                    groovyFunction.setName(scriptFunction.name());
                } else {
                    groovyFunction.setName(method.getName());
                }

                if (StringUtils.hasText(scriptFunction.description())) {
                    groovyFunction.setDescription(scriptFunction.description());
                }


                ScriptType[] requests = scriptFunction.requests();
                if (requests != null) {
                    for (ScriptType request : requests) {
                        GroovyType requestObject = TempGroovyTypeCache.getInstance().getOrCreate(request.dataType());
                        this.resetObjectMeta(requestObject, request);
                        groovyFunction.addRequest(requestObject);
                    }
                }

                Class<?> returnType = scriptFunction.returnType();
                groovyFunction.setReturnType(TempGroovyTypeCache.getInstance().getOrCreate(returnType));
                this.object.addFunction(groovyFunction);
            }
        }
    }


    public GroovyType parser() {
        this.loadObjectMeta();
        this.loadFields();
        this.loadMethods();
        return this.object;
    }
}
