package com.codingapi.springboot.script.parser;

import java.util.ArrayList;
import java.util.List;

public class GroovyScriptParserService {

    private final Object target;

    private final List<Object> parserList;

    public GroovyScriptParserService(Object target) {
        this.target = target;
        this.parserList = new ArrayList<>();
    }

    public List<String> parser() {
        return new GroovyScriptParser(this.target, this).parser();
    }

    public void addTarget(Object object) {
        this.parserList.add(object);
    }

    public boolean hasTarget(Object object) {
        return parserList.contains(object);
    }
}
