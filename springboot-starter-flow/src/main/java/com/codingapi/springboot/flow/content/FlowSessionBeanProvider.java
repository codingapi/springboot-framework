package com.codingapi.springboot.flow.content;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

/**
 * 流程回话 spring bean 提供者
 */
public class FlowSessionBeanProvider {

    @Getter
    private static final FlowSessionBeanProvider instance = new FlowSessionBeanProvider();

    private FlowSessionBeanProvider() {
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
