package com.codingapi.springboot.framework.script.service;

import com.codingapi.springboot.framework.script.annotation.ScriptFunction;
import com.codingapi.springboot.framework.script.annotation.ScriptType;
import com.codingapi.springboot.framework.script.meta.GroovyFunction;
import com.codingapi.springboot.framework.script.meta.GroovyType;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚本类型解析对象
 */
public class GroovyTypeParser {

    private final Class<?> clazz;

    private final GroovyType object;

    public GroovyTypeParser(Class<?> clazz) {
        this.clazz = clazz;
        this.object = new GroovyType();
        this.object.setDataType(clazz);
        this.object.initFields();
        this.object.initFunctions();
    }


    private void resetObjectMeta(GroovyType target, ScriptType scriptType) {
        if (StringUtils.hasText(scriptType.name())) {
            target.setName(scriptType.name());
        }
        if (StringUtils.hasText(scriptType.description())) {
            target.setDescription(scriptType.description());
        }
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
                GroovyType groovyType = TempGroovyTypeCache.getInstance().getOrCreate(field.getType());
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

                Parameter[] methodParameters = method.getParameters();
                Map<String, Parameter> parameterMap = new HashMap<>();
                for (Parameter parameter : methodParameters) {
                    parameterMap.put(parameter.getName(), parameter);
                }

                ScriptType[] parameters = scriptFunction.parameters();
                if (parameters != null) {
                    for (ScriptType parameter : parameters) {
                        Parameter methodParameter = parameterMap.get(parameter.name());
                        if (methodParameter != null) {
                            GroovyType requestObject = TempGroovyTypeCache.getInstance().getOrCreate(methodParameter.getType());
                            this.resetObjectMeta(requestObject, parameter);
                            groovyFunction.addParameter(requestObject);
                        }
                    }
                }

                Class<?> returnType = method.getReturnType();
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
