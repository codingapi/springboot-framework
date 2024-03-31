package com.codingapi.springboot.security.controller;

import com.codingapi.springboot.framework.dto.response.SingleResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
@AllArgsConstructor
public class VersionController {

    private final Environment env;

    @GetMapping("/version")
    public SingleResponse<String> version() {
        return SingleResponse.of(env.getProperty("application.version", "-"));
    }
}
