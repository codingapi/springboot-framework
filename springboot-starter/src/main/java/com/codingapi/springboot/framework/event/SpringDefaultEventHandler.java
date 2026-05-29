package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.properties.PropertiesContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * handler订阅的Spring触发器,在异步的情况下可配置多线程。
 */
@Slf4j
public class SpringDefaultEventHandler extends SpringEventHandler implements InitializingBean {

    /**
     * 异步多线程的KEY
     */
    private ExecutorService executorService;

    public SpringDefaultEventHandler(List<IHandler> handlers) {
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
        log.info("SpringDefaultEventHandler Success Initial.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newFixedThreadPool(PropertiesContext.getInstance().getHandlerThreadPoolSize());
    }

    @EventListener
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
