package com.codingapi.springboot.framework.trigger;


/**
 * 触发逻辑
 * @param <T> 触发DTO对象 {@link Trigger}
 */
public interface TriggerHandler<T extends  Trigger> {

    /**
     * 是否进入触发器
     * @param trigger 触发对象  {@link Trigger}
     * @return true进入 false 不进入
     */
    boolean preTrigger(T trigger);

    /**
     * 触发执行逻辑
     * @param trigger 触发对象  {@link Trigger}
     */
    void trigger(T trigger);

    /**
     * 执行完成以后是否删除触发器
     * @param trigger 触发对象  {@link Trigger}
     * @param canTrigger 是否执行过程trigger逻辑 true执行过程 false未执行
     * @return true删除
     */
    default boolean remove(T trigger, boolean canTrigger){
        return false;
    }
}
