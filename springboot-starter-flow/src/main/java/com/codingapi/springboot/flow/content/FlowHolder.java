package com.codingapi.springboot.flow.content;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

import java.util.List;

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

    public <T> T getBean(Class<T> clazz) {
        if (spring != null) {
            return spring.getBean(clazz);
        }
        return null;
    }

    public <T> List<T> getBeans(Class<T> clazz) {
        if (spring != null) {
            return List.of(spring.getBeanNamesForType(clazz)).stream().map(name -> (T) spring.getBean(name)).toList();
        }
        return null;
    }
}
