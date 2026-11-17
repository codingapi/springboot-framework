package com.codingapi.springboot.framework.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Handler 注册表的订阅类型匹配与注销能力。
 */
class ApplicationHandlerUtilsTest {

    static class SubscribedEvent implements ISyncEvent {
    }

    static class OtherEvent implements ISyncEvent {
    }

    private final List<IHandler> registeredHandlers = new ArrayList<>();

    @AfterEach
    void cleanup() {
        ApplicationHandlerUtils.getInstance().removeHandlers(registeredHandlers);
        registeredHandlers.clear();
    }

    private void register(IHandler handler) {
        ApplicationHandlerUtils.getInstance().addHandler(handler);
        registeredHandlers.add(handler);
    }

    @Test
    void should_only_dispatch_event_to_handler_subscribed_it() {
        List<IEvent> received = new ArrayList<>();
        register(new IHandler<SubscribedEvent>() {
            @Override
            public void handler(SubscribedEvent event) {
                received.add(event);
            }
        });

        ApplicationHandlerUtils.getInstance().handler(new SubscribedEvent());
        ApplicationHandlerUtils.getInstance().handler(new OtherEvent());

        assertEquals(1, received.size());
        assertTrue(received.get(0) instanceof SubscribedEvent);
    }

    @Test
    void should_skip_handler_whose_subscribed_type_cannot_be_resolved() {
        List<IEvent> received = new ArrayList<>();
        IHandler<SubscribedEvent> lambdaHandler = event -> received.add(event);
        register(lambdaHandler);

        ApplicationHandlerUtils.getInstance().handler(new OtherEvent());

        assertTrue(received.isEmpty(), "无法解析订阅类型的 Handler 不应接收任何事件");
    }

    @Test
    void should_not_dispatch_event_after_handler_removed() {
        List<IEvent> received = new ArrayList<>();
        IHandler<SubscribedEvent> handler = new IHandler<SubscribedEvent>() {
            @Override
            public void handler(SubscribedEvent event) {
                received.add(event);
            }
        };
        register(handler);

        ApplicationHandlerUtils.getInstance().handler(new SubscribedEvent());
        assertEquals(1, received.size());

        ApplicationHandlerUtils.getInstance().removeHandlers(Collections.<IHandler>singletonList(handler));

        ApplicationHandlerUtils.getInstance().handler(new SubscribedEvent());
        assertEquals(1, received.size(), "已注销的 Handler 不应再接收事件");
    }
}
