package com.codingapi.springboot.example.controller;

import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DemoController {

    @GetMapping("/index")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public String index() {
        String loginUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return "hello:" + loginUserName;
    }

    @GetMapping("/error")
    public Response error() {
        throw new LocaleMessageException("hello.error");
    }
}
