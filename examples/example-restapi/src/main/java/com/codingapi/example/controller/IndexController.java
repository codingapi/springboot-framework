package com.codingapi.example.controller;

import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.exception.LocaleMessageException;
import com.codingapi.springboot.security.gateway.TokenContext;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class IndexController {

    @GetMapping("/index")
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    public String index() {
        String username = TokenContext.current().getUsername();
        return "hello:" + username;
    }

    @GetMapping("/error")
    public Response error() {
        throw new LocaleMessageException("hello.error");
    }
}
