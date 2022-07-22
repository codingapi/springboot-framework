package com.codingapi.springboot.example.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    public static class BeanInit{

        public BeanInit(EndpointMapping endpointMapping, DemoApi demoApi) {
            try {
                endpointMapping.addGetMapping("/open/demo/findByName",demoApi,DemoApi.class.getMethod("findByName", String.class));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Bean
    public BeanInit beanInit(EndpointMapping endpointMapping, DemoApi demoApi){
        return new BeanInit(endpointMapping, demoApi);
    }

}
