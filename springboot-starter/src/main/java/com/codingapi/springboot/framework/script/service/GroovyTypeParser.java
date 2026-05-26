package com.codingapi.springboot.framework.script.service;

import com.codingapi.springboot.framework.script.annotation.ScriptField;
import com.codingapi.springboot.framework.script.annotation.ScriptFunction;
import com.codingapi.springboot.framework.script.meta.GroovyField;
import com.codingapi.springboot.framework.script.meta.GroovyFunction;
import com.codingapi.springboot.framework.script.meta.GroovyMetadata;
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

    private final GroovyMetadata metadata;

    public GroovyTypeParser(Class<?> clazz, GroovyMetadata metadata) {
        this.clazz = clazz;
        this.metadata = metadata;
        this.object = new GroovyType();
    }


    private void loadFields() {
        Field[] fields = this.clazz.getDeclaredFields();
        for (Field field : fields) {
            ScriptField scriptField = field.getAnnotation(ScriptField.class);
            if (scriptField != null) {
                Class<?> clazz = field.getType();
                this.metadata.buildType(field.getType());
                GroovyField groovyField = new GroovyField();
                groovyField.setDataType(clazz.getSimpleName());
                groovyField.setName(field.getName());

                if (StringUtils.hasText(scriptField.name())) {
                    groovyField.setName(scriptField.name());
                }
                if (StringUtils.hasText(scriptField.description())) {
                    groovyField.setDescription(scriptField.description());
                }
                this.object.addField(groovyField);
            }
        }
    }

    private void loadMethods() {
        Method[] methods = this.clazz.getDeclaredMethods();
        for (Method method : methods) {
            ScriptFunction scriptFunction = method.getAnnotation(ScriptFunction.class);
            if (scriptFunction != null) {
                GroovyFunction groovyFunction = new GroovyFunction();
                groovyFunction.setName(method.getName());

                if (StringUtils.hasText(scriptFunction.name())) {
                    groovyFunction.setName(scriptFunction.name());
                }

                if (StringUtils.hasText(scriptFunction.description())) {
                    groovyFunction.setDescription(scriptFunction.description());
                }

                ScriptField[] parameters = scriptFunction.parameters();
                Map<String, ScriptField> parameterMap = new HashMap<>();
                if (parameters != null) {
                    for (ScriptField scriptField : parameters) {
                        parameterMap.put(scriptField.name(), scriptField);
                    }
                }

                Parameter[] methodParameters = method.getParameters();
                for (Parameter methodParameter : methodParameters) {
                    Class<?> clazz = methodParameter.getType();
                    this.metadata.buildType(clazz);

                    GroovyField groovyParameter = new GroovyField();
                    groovyParameter.setDataType(clazz.getSimpleName());
                    groovyParameter.setName(methodParameter.getName());

                    ScriptField scriptParameter = parameterMap.get(methodParameter.getName());
                    if (scriptParameter != null) {
                        if (StringUtils.hasText(scriptParameter.name())) {
                            groovyParameter.setName(scriptParameter.name());
                        }
                        if (StringUtils.hasText(scriptParameter.description())) {
                            groovyParameter.setDescription(scriptParameter.description());
                        }
                    }

                    groovyFunction.addParameter(groovyParameter);
                }

                Class<?> returnType = method.getReturnType();
                this.metadata.buildType(returnType);
                this.object.addFunction(groovyFunction);
            }
        }
    }


    public GroovyType parser() {
        this.loadFields();
        this.loadMethods();
        return this.object;
    }
}
