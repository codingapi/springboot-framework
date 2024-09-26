package com.codingapi.example.event;

import com.codingapi.example.domain.Node;
import com.codingapi.springboot.framework.event.IAsyncEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lorne
 * @since 1.0.0
 */
@Setter
@Getter
@AllArgsConstructor
public class NodeEvent implements IAsyncEvent {

    private Node old;
    private Node now;

}
