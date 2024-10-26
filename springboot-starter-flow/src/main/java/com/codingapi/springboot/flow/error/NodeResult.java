package com.codingapi.springboot.flow.error;

import lombok.Getter;

/**
 *  节点的异常匹配对象
 */
@Getter
public class NodeResult extends ErrorResult{

    private final String node;

    public NodeResult(String node) {
        this.node = node;
    }
}
