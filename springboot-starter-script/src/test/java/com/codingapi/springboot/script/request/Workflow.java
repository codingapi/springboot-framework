package com.codingapi.springboot.script.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
public class Workflow {

    private String name;
    private Node node;
    private List<Node> nodes;
    private Workflow target;

    public void addNode(Node node) {
        this.node = node;
        if (this.nodes == null) {
            this.nodes = new ArrayList<>();
        }
        this.nodes.add(node);
        this.target = this;
    }
}
