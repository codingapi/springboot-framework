package com.codingapi.springboot.framework.controller;

import com.codingapi.springboot.framework.dto.response.MapResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class DemoController {

    @GetMapping("/hello")
    public MapResponse hello(){
        return MapResponse.create().add("hello","hello");
    }

}
