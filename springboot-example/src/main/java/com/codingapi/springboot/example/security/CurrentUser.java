package com.codingapi.springboot.example.security;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lorne
 * @since 1.0.0
 */
public class CurrentUser {

    // 演示数据,实际数据通过拦截器设置
    private final static int DEFAULT_USERID = 1;

    private static CurrentUser instance;

    private CurrentUser(){
    }

    public static CurrentUser getInstance() {
        if(instance==null) {
            synchronized (CurrentUser.class) {
                if(instance==null){
                    instance = new CurrentUser();
                }
            }
        }
        return instance;
    }


    @Setter
    @Getter
    private int userId = DEFAULT_USERID;

}
