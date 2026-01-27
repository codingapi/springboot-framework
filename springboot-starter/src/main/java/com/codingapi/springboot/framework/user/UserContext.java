package com.codingapi.springboot.framework.user;

import lombok.Getter;

public class UserContext {

    private final ThreadLocal<IUser> threadLocal = new ThreadLocal<>();

    @Getter
    private final static UserContext instance = new UserContext();

    private UserContext() {
    }

    public void setCurrent(IUser user) {
        threadLocal.set(user);
    }

    public IUser current() {
        return threadLocal.get();
    }
}