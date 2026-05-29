package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GroovyTypeFixStrategyContext {

    private final List<GroovyTypeFixStrategy> strategies;

    @Getter
    private final static GroovyTypeFixStrategyContext instance = new GroovyTypeFixStrategyContext();

    private GroovyTypeFixStrategyContext() {
        this.strategies = new ArrayList<>();
    }

    public void addFixStrategy(GroovyTypeFixStrategy gateway) {
        this.strategies.add(gateway);
    }


    public void fix(GroovyScript groovyScript, Class<?> clazz, GroovyType groovyType) {
        for (GroovyTypeFixStrategy strategy : strategies) {
            if (strategy.support(clazz)) {
                strategy.fix(groovyScript, groovyType);
            }
        }
    }

}
