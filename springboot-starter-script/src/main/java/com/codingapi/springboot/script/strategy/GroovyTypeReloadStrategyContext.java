package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.meta.GroovyType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GroovyTypeReloadStrategyContext {

    private final List<GroovyTypeReloadStrategy> strategies;

    @Getter
    private final static GroovyTypeReloadStrategyContext instance = new GroovyTypeReloadStrategyContext();

    private GroovyTypeReloadStrategyContext() {
        this.strategies = new ArrayList<>();
    }

    public void addReloadStrategy(GroovyTypeReloadStrategy gateway) {
        this.strategies.add(gateway);
    }

    public void reload(Class<?> clazz, GroovyType groovyType) {
        for (GroovyTypeReloadStrategy strategy : strategies) {
            if (strategy.support(clazz)) {
                strategy.reload(groovyType);
            }
        }
    }

}
