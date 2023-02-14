package com.codingapi.springboot.security.jwt;

import org.springframework.security.core.context.SecurityContextHolder;

public class TokenContext {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private TokenContext() {
    }

    public static Token current() {
        return (Token) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static void pushExtra(String extra){
        threadLocal.set(extra);
    }

    public static String getExtra(){
        return threadLocal.get();
    }
}
