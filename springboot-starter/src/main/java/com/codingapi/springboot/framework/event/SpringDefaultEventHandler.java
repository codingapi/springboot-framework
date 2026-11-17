package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.properties.PropertiesContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * handler订阅的Spring触发器。
 *
 * <p>事件默认在事务提交后分发（AFTER_COMMIT + fallbackExecution），保证订阅方
 * 能读到已提交数据；异步事件交由 {@link OrderedAsyncDispatcher} 按 traceId 串行分发。</p>
 */
@Slf4j
public class SpringDefaultEventHandler extends SpringEventHandler implements InitializingBean, DisposableBean {

    private final List<IHandler> handlers;

    private ExecutorService executorService;

    private OrderedAsyncDispatcher dispatcher;

    public SpringDefaultEventHandler(List<IHandler> handlers) {
        this.handlers = handlers;
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
        log.info("SpringDefaultEventHandler Success Initial.");
    }

    @Override
    public void afterPropertiesSet() {
        executorService = Executors.newFixedThreadPool(PropertiesContext.getInstance().getHandlerThreadPoolSize());
        dispatcher = new OrderedAsyncDispatcher(executorService, PropertiesContext.getInstance().isEventAsyncOrdered());
    }

    @Override
    public void destroy() {
        ApplicationHandlerUtils.getInstance().removeHandlers(handlers);
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log.info("SpringDefaultEventHandler Destroyed, handlers unregistered.");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void dispatch(DomainEvent domainEvent) {
        String traceId = domainEvent.getTraceId();

        if (domainEvent.isSync()) {
            try {
                EventTraceContext.getInstance().createEventKey(traceId);
                ApplicationHandlerUtils.getInstance().handler(domainEvent.getEvent());
            } finally {
                EventTraceContext.getInstance().checkEventState();
            }
        } else {
            dispatcher.execute(traceId, () -> {
                try {
                    EventTraceContext.getInstance().createEventKey(traceId);
                    ApplicationHandlerUtils.getInstance().handler(domainEvent.getEvent());
                } finally {
                    EventTraceContext.getInstance().checkEventState();
                }
            });
        }
    }
}
