package com.codingapi.springboot.script.strategy;

import com.codingapi.springboot.script.GroovyScript;
import com.codingapi.springboot.script.meta.GroovyMetadata;

/**
 *  脚本元数据构建策略
 */
public interface GroovyMetadataGenerateStrategy {

    boolean support(GroovyScript script);

    GroovyMetadata generate(GroovyScript script);
}
