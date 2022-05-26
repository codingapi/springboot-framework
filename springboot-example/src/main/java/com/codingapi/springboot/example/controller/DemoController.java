package com.codingapi.springboot.example.controller;

import com.codingapi.springboot.framework.exception.MessageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello(){
        throw new MessageException("hello.error");
    }
}
