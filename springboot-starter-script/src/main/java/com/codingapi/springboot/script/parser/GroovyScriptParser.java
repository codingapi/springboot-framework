package com.codingapi.springboot.script.parser;

import com.codingapi.springboot.script.annotation.GroovyScript;
import lombok.SneakyThrows;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;

class GroovyScriptParser {

    private final Object target;
    private final GroovyScriptParserService parserService;

    private final Class<?> clazz;

    private final List<String> list;


    public GroovyScriptParser(Object target,GroovyScriptParserService parserService) {
        this.target = target;
        this.parserService = parserService;
        this.clazz = target.getClass();
        this.list = new ArrayList<>();
    }


    @SneakyThrows
    private void loadObjectFields() {
        if(parserService.hasTarget(this.target)){
            return;
        }
        parserService.addTarget(this.target);
        Field[] fields = this.clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            Object value = field.get(this.target);
            if (value != null) {
                if (fieldClass.isPrimitive()
                        || ClassUtils.isPrimitiveOrWrapper(fieldClass)
                        || fieldClass == String.class) {
                    GroovyScript groovyScript = field.getAnnotation(GroovyScript.class);
                    if (groovyScript != null) {
                        if (value instanceof String str) {
                            this.list.add(str);
                        }
                    }
                } else {
                    GroovyScriptParser parser = new GroovyScriptParser(value,this.parserService);
                    this.list.addAll(parser.parser());
                }
            }
        }
    }

    public List<String> parser() {
        if (this.target instanceof Collection<?>) {
            for (Object item : (Collection) this.target) {
                GroovyScriptParser groovyScriptParser = new GroovyScriptParser(item,this.parserService);
                this.list.addAll(groovyScriptParser.parser());
            }
        } else if (this.target instanceof Map<?, ?>) {
            for (Object item : ((Map) this.target).values()) {
                GroovyScriptParser groovyScriptParser = new GroovyScriptParser(item,this.parserService);
                this.list.addAll(groovyScriptParser.parser());
            }
        } else if (this.target instanceof Set<?>) {
            for (Object item : ((Set) this.target)) {
                GroovyScriptParser groovyScriptParser = new GroovyScriptParser(item,this.parserService);
                this.list.addAll(groovyScriptParser.parser());
            }
        } else {
            this.loadObjectFields();
        }
        return this.list.stream().distinct().toList();
    }


}
