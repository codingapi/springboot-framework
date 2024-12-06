package com.codingapi.springboot.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataAuthorizationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataAuthorizationConfiguration.class,args);
    }

}
