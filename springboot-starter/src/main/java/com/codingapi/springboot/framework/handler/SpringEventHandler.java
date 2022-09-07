package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * handler订阅的Spring触发器,在异步的情况下可配置多线程。
 */
@Slf4j
public class SpringEventHandler implements ApplicationListener<DomainEvent> {

    /**
     * 异步多线程的KEY
     * 可通过 System.setProperty(THREAD_KEY,"20") 调整线程数
     */
    public final static String THREAD_KEY = "Handler.ThreadPools";

    private final ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(System.getProperty(THREAD_KEY, "10")));

    public SpringEventHandler(List<IHandler> handlers) {
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
    }

    @Override
    public void onApplicationEvent(DomainEvent domainEvent) {
        if (domainEvent.isSync()) {
            ApplicationHandlerUtils.getInstance().handler(domainEvent.getEvent());
        } else {
            executorService.execute(() -> {
                ApplicationHandlerUtils.getInstance().handler(domainEvent.getEvent());
            });
        }
    }

}
