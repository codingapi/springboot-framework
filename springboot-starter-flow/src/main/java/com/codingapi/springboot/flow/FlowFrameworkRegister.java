package com.codingapi.springboot.flow;

import com.codingapi.springboot.flow.content.FlowSessionBeanProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class FlowFrameworkRegister implements InitializingBean {

    private final ApplicationContext application;

    @Override
    public void afterPropertiesSet() throws Exception {
        FlowSessionBeanProvider.getInstance().register(application);
    }
}
