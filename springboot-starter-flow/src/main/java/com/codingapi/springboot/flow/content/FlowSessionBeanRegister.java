package com.codingapi.springboot.flow.content;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

/**
 * 流程回话 spring bean 注册对象
 */
public class FlowSessionBeanRegister {

    @Getter
    private static final FlowSessionBeanRegister instance = new FlowSessionBeanRegister();

    private FlowSessionBeanRegister() {
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
