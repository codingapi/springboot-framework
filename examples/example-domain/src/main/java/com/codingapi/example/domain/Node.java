package com.codingapi.example.domain;

import com.codingapi.example.em.State;
import com.codingapi.example.event.NodeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
public class Node implements Cloneable {

    private long id;

    private String name;

    private String url;

    private State state;

    @SneakyThrows
    public void swap(Node target) {
        Node old = (Node) this.clone();
        this.url = target.getUrl();
        this.name = target.getName();
        EventPusher.push(new NodeEvent(old, this));
    }

}
