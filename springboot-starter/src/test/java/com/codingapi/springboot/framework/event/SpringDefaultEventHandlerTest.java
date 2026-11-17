package com.codingapi.springboot.framework.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Spring 上下文销毁时 Handler 注册表必须同步注销，避免跨上下文污染。
 */
class SpringDefaultEventHandlerTest {

    static class ContextEvent implements ISyncEvent {
    }

    @Test
    void should_unregister_handlers_after_destroy() throws Exception {
        List<IEvent> received = new ArrayList<>();
        IHandler<ContextEvent> handler = new IHandler<ContextEvent>() {
            @Override
            public void handler(ContextEvent event) {
                received.add(event);
            }
        };

        SpringDefaultEventHandler eventHandler = new SpringDefaultEventHandler(Collections.<IHandler>singletonList(handler));

        ApplicationHandlerUtils.getInstance().handler(new ContextEvent());
        assertEquals(1, received.size());

        eventHandler.destroy();

        ApplicationHandlerUtils.getInstance().handler(new ContextEvent());
        assertEquals(1, received.size(), "上下文销毁后 Handler 应被注销，不再接收事件");
    }
}
