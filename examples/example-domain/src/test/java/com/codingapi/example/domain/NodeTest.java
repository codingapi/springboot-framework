package com.codingapi.example.domain;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class NodeTest {

    @Test
    void swap() {
        Node node1 = new Node(1,"node1","http://node1", Node.State.ENABLE);
        Node node2 = new Node(1,"node2","http://node2", Node.State.ENABLE);

        node1.swap(node2);

        Assert.isTrue(node1.getName().equals("node2"), "swap service error.");
        Assert.isTrue(node2.getName().equals("node2"), "swap service error.");
    }
}