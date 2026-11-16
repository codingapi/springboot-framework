package com.codingapi.springboot.framework.event;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件栈上下文。
 */
public class EventStackContext {

    private final Map<String, List<Class<?>>> eventClassStack = new ConcurrentHashMap<>();
    private final Map<String, List<IEvent>> eventStack = new ConcurrentHashMap<>();

    @Getter
    private final static EventStackContext instance = new EventStackContext();

    private EventStackContext() {
    }

    private void addEventClass(String traceId, IEvent event) {
        eventClassStack.computeIfAbsent(traceId, key -> Collections.synchronizedList(new ArrayList<>()))
                .add(event.getClass());
    }

    private void addEventStack(String traceId, IEvent event) {
        eventStack.computeIfAbsent(traceId, key -> Collections.synchronizedList(new ArrayList<>()))
                .add(event);
    }

    void addEvent(String traceId, IEvent event) {
        addEventClass(traceId, event);
        addEventStack(traceId, event);
    }

    boolean checkEventLoop(String traceId, IEvent event) {
        List<Class<?>> events = eventClassStack.get(traceId);
        if (events != null) {
            synchronized (events) {
                return events.contains(event.getClass());
            }
        }
        return false;
    }

    /**
     * 返回事件列表的快照，不暴露内部可变状态。
     */
    public List<IEvent> getEvents(String eventKey) {
        if (eventKey != null) {
            String traceId = eventKey.split("#")[0];
            List<IEvent> events = eventStack.get(traceId);
            if (events != null) {
                synchronized (events) {
                    return new ArrayList<>(events);
                }
            }
        }
        return null;
    }

    /**
     * 返回事件类型列表的快照，不暴露内部可变状态。
     */
    public List<Class<?>> getEventClasses(String eventKey) {
        if (eventKey != null) {
            String traceId = eventKey.split("#")[0];
            List<Class<?>> events = eventClassStack.get(traceId);
            if (events != null) {
                synchronized (events) {
                    return new ArrayList<>(events);
                }
            }
        }
        return null;
    }

    void remove(String traceId) {
        eventStack.remove(traceId);
        eventClassStack.remove(traceId);
    }
}
