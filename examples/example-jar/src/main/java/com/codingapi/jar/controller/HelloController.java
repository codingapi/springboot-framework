package com.codingapi.jar.controller;

import com.codingapi.jar.entity.Test;
import com.codingapi.jar.service.HiService;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/open/hello")
@AllArgsConstructor
public class HelloController {

    private final HiService hiService;

    @GetMapping("/hi")
    public List<Test> hi() {
        return hiService.hi();
    }

    @GetMapping("/test")
    public Response test() {
        hiService.init();
        return Response.buildSuccess();
    }


}
