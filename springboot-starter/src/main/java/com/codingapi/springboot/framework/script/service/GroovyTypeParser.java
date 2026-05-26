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

                Parameter[] methodParameters = method.getParameters();
                Map<String, Parameter> parameterMap = new HashMap<>();
                for (Parameter parameter : methodParameters) {
                    parameterMap.put(parameter.getName(), parameter);
                }

                ScriptField[] parameters = scriptFunction.parameters();
                if (parameters != null) {
                    for (ScriptField parameter : parameters) {
                        Parameter methodParameter = parameterMap.get(parameter.name());
                        if (methodParameter != null) {
                            Class<?> clazz = methodParameter.getType();
                            this.metadata.buildType(clazz);

                            GroovyField groovyParameter = new GroovyField();
                            groovyParameter.setDataType(clazz.getSimpleName());
                            groovyParameter.setName(methodParameter.getName());

                            if (StringUtils.hasText(parameter.name())) {
                                groovyParameter.setName(parameter.name());
                            }
                            if (StringUtils.hasText(parameter.description())) {
                                groovyParameter.setDescription(parameter.description());
                            }

                            groovyFunction.addParameter(groovyParameter);
                        }
                    }
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
