package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.boot.DynamicApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FrameWorkApplication {

    public static void main(String[] args) {
        DynamicApplication.run(FrameWorkApplication.class, args);
    }
}
