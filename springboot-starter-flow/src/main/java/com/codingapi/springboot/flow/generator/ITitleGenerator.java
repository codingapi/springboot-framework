package com.codingapi.springboot.flow.generator;

import com.codingapi.springboot.flow.content.FlowContent;

/**
 * 标题生成器
 */
public interface ITitleGenerator {


    /**
     * 生成标题
     * @param flowContent 流程内容
     * @return 标题
     */
    String generate(FlowContent flowContent);

}
