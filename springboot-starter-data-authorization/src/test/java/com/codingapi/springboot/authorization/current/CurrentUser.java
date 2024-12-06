package com.codingapi.springboot.authorization.current;

import com.codingapi.springboot.authorization.entity.User;
import lombok.Getter;

public class CurrentUser {

    @Getter
    private final static CurrentUser instance = new CurrentUser();

    private final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    private CurrentUser(){
    }

    public void setUser(User user){
        threadLocal.set(user);
    }

    public User getUser(){
        return threadLocal.get();
    }

    public void remove(){
        threadLocal.remove();
    }
}
