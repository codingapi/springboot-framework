package com.codingapi.springboot.framework.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 端到端顺序回归：事件必须经由 {@link EventPusher#push(IEvent)} 真实推送，
 * 覆盖「业务操作连续 push 共享 trace + 异步严格串行」这一此前遗漏的场景。
 */
@SpringBootTest
class EventOrderE2ETest {

    static class OrderedAsyncEvent implements IAsyncEvent {
        final String name;

        OrderedAsyncEvent(String name) {
            this.name = name;
        }
    }

    static final List<String> ORDER = Collections.synchronizedList(new ArrayList<>());
    static final List<String> TRACES = Collections.synchronizedList(new ArrayList<>());
    static volatile CountDownLatch LATCH;

    static class OrderedAsyncEventHandler implements IHandler<OrderedAsyncEvent> {
        @Override
        public void handler(OrderedAsyncEvent event) {
            String eventKey = EventTraceContext.getInstance().getEventKey();
            synchronized (ORDER) {
                ORDER.add("start-" + event.name);
                TRACES.add(eventKey == null ? null : eventKey.split("#")[0]);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (ORDER) {
                ORDER.add("end-" + event.name);
            }
            if (LATCH != null) {
                LATCH.countDown();
            }
        }
    }

    @TestConfiguration
    static class HandlerConfig {
        @Bean
        IHandler<OrderedAsyncEvent> orderedAsyncEventHandler() {
            return new OrderedAsyncEventHandler();
        }
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void reset() {
        ORDER.clear();
        TRACES.clear();
        LATCH = null;
    }

    @Test
    void transaction_push_events_share_trace_and_execute_in_order() throws Exception {
        LATCH = new CountDownLatch(2);
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.executeWithoutResult(status -> {
            EventPusher.push(new OrderedAsyncEvent("A"));
            EventPusher.push(new OrderedAsyncEvent("B"));
        });

        assertTrue(LATCH.await(5, TimeUnit.SECONDS), "两个异步事件应全部执行完成");
        assertEquals(java.util.Arrays.asList("start-A", "end-A", "start-B", "end-B"), ORDER,
                "同一业务操作中连续 push 的异步事件必须严格按 push 顺序串行执行");

        Set<String> distinctTraces = new HashSet<>(TRACES);
        assertEquals(1, distinctTraces.size(), "同一事务内的连续 push 应共享同一 traceId");
    }

    @Test
    void batch_push_same_type_events_not_treated_as_loop() throws Exception {
        LATCH = new CountDownLatch(2);
        EventPusher.push(new OrderedAsyncEvent("B1"), new OrderedAsyncEvent("B2"));

        assertTrue(LATCH.await(5, TimeUnit.SECONDS), "批量异步事件应全部执行完成");
        assertEquals(java.util.Arrays.asList("start-B1", "end-B1", "start-B2", "end-B2"), ORDER,
                "批量内同类事件应串行执行，且不被误判为循环调用");
        assertEquals(1, new HashSet<>(TRACES).size(), "批量事件应共享同一 traceId");
    }

    @Test
    void rollback_transaction_does_not_dispatch_events() throws Exception {
        LATCH = new CountDownLatch(1);
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        assertThrows(IllegalStateException.class, () -> template.executeWithoutResult(status -> {
            EventPusher.push(new OrderedAsyncEvent("R"));
            throw new IllegalStateException("force rollback");
        }));

        // AFTER_COMMIT 语义：事务回滚不应触发事件，等待客户端确认无 handler 执行
        Thread.sleep(300);
        assertTrue(ORDER.isEmpty(), "事务回滚后事件不应被分发");
    }
}
