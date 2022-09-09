package com.codingapi.springboot.example.insfrastructure.query;

import com.codingapi.springboot.fastshow.mapping.MvcEndpointMapping;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FastShowConfiguration implements ApplicationRunner {

    private final MvcEndpointMapping mvcEndpointMapping;

    private final DemoQueryApi demoQueryApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //todo add scanner to add mapping
        mvcEndpointMapping.addGetMapping("/open/demo/findByName",demoQueryApi,DemoQueryApi.class.getMethod("findByName", String.class));
    }

}
