package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.properties.FrameworkProperties;
import com.codingapi.springboot.framework.properties.PropertiesContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringDefaultEventHandlerOrderTest {

    static class TraceAsyncEvent implements IAsyncEvent {
        private final int id;
        private final CountDownLatch latch;
        private final List<String> order;

        TraceAsyncEvent(int id, CountDownLatch latch, List<String> order) {
            this.id = id;
            this.latch = latch;
            this.order = order;
        }
    }

    @Test
    void async_events_in_same_trace_execute_in_order() throws Exception {
        PropertiesContext.getInstance().setProperties(new FrameworkProperties());
        List<String> order = new ArrayList<>();

        IHandler<TraceAsyncEvent> handler = new IHandler<TraceAsyncEvent>() {
            @Override
            public void handler(TraceAsyncEvent event) {
                synchronized (order) {
                    order.add("start-" + event.id);
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (order) {
                    order.add("end-" + event.id);
                }
                event.latch.countDown();
            }
        };

        SpringDefaultEventHandler eventHandler = new SpringDefaultEventHandler(Collections.singletonList(handler));
        eventHandler.afterPropertiesSet();

        CountDownLatch latch = new CountDownLatch(2);
        eventHandler.dispatch(new DomainEvent(new TraceAsyncEvent(1, latch, order), false, "trace-same"));
        eventHandler.dispatch(new DomainEvent(new TraceAsyncEvent(2, latch, order), false, "trace-same"));

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(Arrays.asList("start-1", "end-1", "start-2", "end-2"), order);
    }
}
