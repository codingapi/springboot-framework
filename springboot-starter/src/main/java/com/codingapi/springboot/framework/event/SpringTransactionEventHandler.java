package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.properties.PropertiesContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * handler订阅的Spring触发器,在异步的情况下可配置多线程。
 */
@Slf4j
public class SpringTransactionEventHandler extends SpringEventHandler implements InitializingBean {

    /**
     * 异步多线程的KEY
     */
    private  ExecutorService executorService;

    public SpringTransactionEventHandler(List<IHandler> handlers) {
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
        log.info("SpringTransactionEventHandler Success Initial.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newFixedThreadPool(PropertiesContext.getInstance().getHandlerThreadPoolSize());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public synchronized void dispatch(DomainEvent domainEvent) {
        String traceId = domainEvent.getTraceId();

        if (domainEvent.isSync()) {
            try {
                EventTraceContext.getInstance().createEventKey(traceId);
                ApplicationHandlerUtils.getInstance().handler(domainEvent.getEvent());
            } finally {
                EventTraceContext.getInstance().checkEventState();
            }
        } else {
            executorService.execute(() -> {
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
