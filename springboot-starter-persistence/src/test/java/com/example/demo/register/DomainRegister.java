package com.example.demo.register;

import com.example.demo.domain.Demo;
import com.codingapi.springboot.persistence.register.DomainClassRegister;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DomainRegister implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        DomainClassRegister.INSTANCE.register(Demo.class);
    }
}
