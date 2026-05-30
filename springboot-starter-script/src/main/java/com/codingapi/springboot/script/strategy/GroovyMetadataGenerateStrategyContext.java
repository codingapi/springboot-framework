package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本元数据构建策略上下文
 */
public class GroovyMetadataGenerateStrategyContext {

    @Getter
    private final static GroovyMetadataGenerateStrategyContext instance = new GroovyMetadataGenerateStrategyContext();

    private final List<GroovyMetadataGenerateStrategy> strategies;

    private GroovyMetadataGenerateStrategyContext() {
        this.strategies = new ArrayList<>();
    }

    public void addGenerateStrategy(GroovyMetadataGenerateStrategy strategy) {
        this.strategies.add(strategy);
    }


    /**
     * 清空策略
     */
    public void clear(){
        this.strategies.clear();
    }

    public GroovyMetadata generate(GroovyScript groovyScript) {
        for (GroovyMetadataGenerateStrategy strategy : this.strategies) {
            if (strategy.support(groovyScript)) {
                return strategy.generate(groovyScript);
            }
        }
        return null;
    }

}
