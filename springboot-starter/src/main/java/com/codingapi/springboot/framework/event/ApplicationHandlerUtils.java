package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventException;
import com.codingapi.springboot.framework.exception.EventLoopException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
class ApplicationHandlerUtils implements IHandler<IEvent> {

    private static ApplicationHandlerUtils instance;
    private final List<IHandler<IEvent>> handlers;
    private final Set<String> warnedHandlerTypes = ConcurrentHashMap.newKeySet();


    private ApplicationHandlerUtils() {
        this.handlers = new ArrayList<>();
    }

    public static ApplicationHandlerUtils getInstance() {
        if (instance == null) {
            synchronized (ApplicationHandlerUtils.class) {
                if (instance == null) {
                    instance = new ApplicationHandlerUtils();
                }
            }
        }
        return instance;
    }

    public void addHandlers(List<IHandler> handlers) {
        if (handlers != null) {
            handlers.forEach(this::addHandler);
        }
    }


    public void addHandler(IHandler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    /**
     * 注销 Handler，供 Spring 上下文销毁时清理，避免注册表跨上下文残留。
     */
    public void removeHandlers(List<IHandler> handlers) {
        if (handlers != null) {
            this.handlers.removeAll(handlers);
        }
    }

    /**
     * 获取订阅的事件类型。
     *
     * @return 订阅的事件类型；无法解析（如以 lambda 形式注册、JDK 动态代理）时返回 {@code null}
     */
    private Class<?> getHandlerEventClass(IHandler<?> handler) {
        ResolvableType resolvableType = ResolvableType.forClass(handler.getClass()).as(IHandler.class);
        ResolvableType eventType = resolvableType.getGeneric(0);
        if (eventType.getType() instanceof TypeVariable) {
            return null;
        }
        return eventType.resolve();
    }


    @Override
    public void handler(IEvent event) {
        Class<?> eventClass = event.getClass();

        List<IHandler<IEvent>> matchHandlers = handlers
                .stream()
                .filter(handler -> {
                    Class<?> targetClass = getHandlerEventClass(handler);
                    if (targetClass == null) {
                        // 订阅类型不可解析时不能退化为「订阅所有事件」，否则会向 Handler 投递不相关事件并触发类型强转异常
                        if (warnedHandlerTypes.add(handler.getClass().getName())) {
                            log.warn("Handler [{}] 的订阅事件类型无法解析（lambda 或 JDK 动态代理），已跳过。"
                                    + "请使用具名类或匿名内部类实现 IHandler<T>", handler.getClass().getName());
                        }
                        return false;
                    }
                    return targetClass.isAssignableFrom(eventClass);
                })
                .sorted(Comparator.comparingInt(IHandler::order))
                .toList();

        if (matchHandlers.isEmpty()) {
            return;
        }

        List<Exception> errorStack = new ArrayList<>();
        boolean hasThrowException = false;
        for (IHandler<IEvent> handler : matchHandlers) {
            try {
                handler.handler(event);
            } catch (Exception e) {
                if (e instanceof EventLoopException) {
                    throw e;
                }
                try {
                    handler.error(e);
                    errorStack.add(e);
                } catch (Exception err) {
                    hasThrowException = true;
                    errorStack.add(err);
                }
            }
        }
        if (hasThrowException) {
            throw new EventException(errorStack);
        }
    }


}
