package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventLoopException;
import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.Getter;

import java.util.*;

/**
 * 事件跟踪上下文
 */
public class EventTraceContext {

    @Getter
    private final static EventTraceContext instance = new EventTraceContext();

    // trace key
    private final Set<String> traceKeys = new HashSet<>();

    // thread local
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // event listenerKey state
    private final Map<String, Boolean> eventKeyState = new HashMap<>();


    private EventTraceContext() {
    }

    String getOrCreateTrace() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            return eventKey.split("#")[0];
        }
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        traceKeys.add(traceId);
        return traceId;
    }

    /**
     * get event key
     * traceId = eventKey.split("#")[0]
     */
    public String getEventKey() {
        return threadLocal.get();
    }

    void createEventKey(String traceId) {
        String eventKey = traceId + "#" + RandomGenerator.randomString(8);
        eventKeyState.put(eventKey, false);
        threadLocal.set(eventKey);
    }

    void checkEventState() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            boolean state = eventKeyState.get(eventKey);
            if (!state) {
                // event execute finish
                String traceId = eventKey.split("#")[0];
                traceKeys.remove(traceId);
                EventStackContext.getInstance().remove(traceId);
            }
        }
        eventKeyState.remove(eventKey);
        threadLocal.remove();
    }

    void addEvent(String traceId, IEvent event) {
        boolean hasEventLoop = EventStackContext.getInstance().checkEventLoop(traceId, event);
        if (hasEventLoop) {
            List<Class<?>> stack = EventStackContext.getInstance().getEventClasses(traceId);
            traceKeys.remove(traceId);
            EventStackContext.getInstance().remove(traceId);
            eventKeyState.remove(traceId);
            threadLocal.remove();
            throw new EventLoopException(stack, event);
        }
        EventStackContext.getInstance().addEvent(traceId, event);
    }
}
