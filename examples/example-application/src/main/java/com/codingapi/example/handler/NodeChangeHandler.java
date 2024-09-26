package com.codingapi.example.handler;

import com.codingapi.example.domain.Node;
import com.codingapi.example.event.NodeEvent;
import com.codingapi.springboot.framework.handler.Handler;
import com.codingapi.springboot.framework.handler.IHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lorne
 * @since 1.0.0
 */
@Slf4j
@Handler
public class NodeChangeHandler implements IHandler<NodeEvent> {

    @Override
    public void handler(NodeEvent event) {
        Node old = event.getOld();
        Node now = event.getNow();
        log.info("old:{},now:{}",old,now);
    }

}
