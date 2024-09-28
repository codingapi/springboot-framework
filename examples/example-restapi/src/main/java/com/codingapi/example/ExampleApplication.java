package com.codingapi.example;

import com.codingapi.springboot.framework.boot.DynamicApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        DynamicApplication.run(ExampleApplication.class, args);
    }

}
