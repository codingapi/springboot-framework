package com.codingapi.springboot.example.ui.controller;

import com.codingapi.springboot.example.application.executor.DemoExecutor;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/demo")
@AllArgsConstructor
public class OpenController {

    private final DemoExecutor executor;

    @GetMapping("/save")
    public Response save(@RequestParam("name") String name){
        executor.create(name);
        return Response.buildSuccess();
    }
}
