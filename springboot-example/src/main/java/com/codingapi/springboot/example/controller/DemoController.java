package com.codingapi.springboot.example.controller;

import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/error")
    public Response error(){
        throw new LocaleMessageException("hello.error");
    }

    @GetMapping("/hello")
    public SingleResponse<String> hello(){
        return SingleResponse.of("hello");
    }



}
