package com.codingapi.springboot.framework.trigger;


public interface TriggerHandler<T extends  Trigger> {

    boolean preTrigger(T trigger);

    void trigger(T trigger);

    boolean remove();

}
