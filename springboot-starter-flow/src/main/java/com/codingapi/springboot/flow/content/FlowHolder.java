package com.codingapi.springboot.flow.content;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

/**
 * 流程持有者
 */
public class FlowHolder {

    @Getter
    private static final FlowHolder instance = new FlowHolder();

    private FlowHolder() {
    }

    private ApplicationContext spring;

    public void register(ApplicationContext spring) {
        this.spring = spring;
    }

    public Object getBean(String beanName) {
        if (spring != null) {
            return spring.getBean(beanName);
        }
        return null;
    }

}
