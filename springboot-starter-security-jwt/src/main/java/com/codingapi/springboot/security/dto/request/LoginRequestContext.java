package com.codingapi.springboot.security.dto.request;

public class LoginRequestContext {

    private static LoginRequestContext context = new LoginRequestContext();
    private final ThreadLocal<LoginRequest> threadLocal = new ThreadLocal<>();

    private LoginRequestContext() {

    }

    public static LoginRequestContext getInstance() {
        return context;
    }

    public void set(LoginRequest request) {
        threadLocal.set(request);
    }

    public LoginRequest get() {
        return threadLocal.get();
    }

    public void clean() {
        threadLocal.set(null);
    }

}
