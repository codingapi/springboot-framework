package com.codingapi.example.handler;

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
public class NodeLogHandler implements IHandler<NodeEvent> {

    @Override
    public void handler(NodeEvent event) {
        log.info("log:{}", event.getNow());
    }

}
