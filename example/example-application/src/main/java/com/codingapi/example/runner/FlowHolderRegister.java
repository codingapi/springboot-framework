package com.codingapi.example.runner;

import com.codingapi.springboot.flow.content.FlowHolder;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FlowHolderRegister implements ApplicationRunner {

    private final ApplicationContext spring;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //注册spring容器
        FlowHolder.getInstance().register(spring);
    }

}
