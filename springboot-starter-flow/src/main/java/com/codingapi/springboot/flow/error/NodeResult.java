package com.codingapi.springboot.flow.error;

import lombok.Getter;

@Getter
public class NodeResult extends ErrorResult{

    private String node;

    public NodeResult(String node) {
        this.node = node;
    }
}
