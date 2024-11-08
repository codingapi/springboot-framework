package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventLoopException;
import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.Getter;

import java.util.*;

public class EventTraceContext {

    @Getter
    private final static EventTraceContext instance = new EventTraceContext();

    // trace key
    private final Set<String> traceKeys = new HashSet<>();

    // thread local
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // event listener state
    private final Map<String, Boolean> eventListenerState = new HashMap<>();

    // event stack
    private final Map<String, List<Class<?>>> eventStack = new HashMap<>();

    private EventTraceContext() {
    }

    String getOrCreateTrace() {
        String listenerKey = threadLocal.get();
        if (listenerKey != null) {
            return listenerKey.split("#")[0];
        }
        String traceId = UUID.randomUUID().toString();
        traceKeys.add(traceId);
        return traceId;
    }

    public String getListenerKey(){
        return threadLocal.get();
    }

    void createEventListener(String traceId) {
        String listenerKey = traceId + "#" + RandomGenerator.randomString(8);
        eventListenerState.put(listenerKey, false);
        threadLocal.set(listenerKey);
    }

    void checkListener() {
        String listenerKey = threadLocal.get();
        if (listenerKey != null) {
            boolean state = eventListenerState.get(listenerKey);
            if (!state) {
                // event execute finish
                String traceId = listenerKey.split("#")[0];
                traceKeys.remove(traceId);
                eventStack.remove(traceId);
            }
        }
        eventListenerState.remove(listenerKey);
        threadLocal.remove();
    }

    void addEvent(String traceId, IEvent event) {
        List<Class<?>> stack = eventStack.get(traceId);
        if (stack == null) {
            stack = new ArrayList<>();
        } else {
            if (stack.contains(event.getClass())) {
                //清空trace记录
                traceKeys.remove(traceId);
                eventStack.remove(traceId);
                eventListenerState.remove(traceId);
                threadLocal.remove();
                throw new EventLoopException(stack, event);
            }
        }
        stack.add(event.getClass());
        eventStack.put(traceId, stack);
    }
}
