package com.codingapi.springboot.security.jwt;

import org.springframework.security.core.context.SecurityContextHolder;

public class TokenContext {

    private TokenContext(){}

    public static Token current(){
        return (Token)SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
