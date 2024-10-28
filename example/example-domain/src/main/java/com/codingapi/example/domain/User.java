package com.codingapi.example.domain;

import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User implements IFlowOperator {

    private long id;

    private String name;

    private String username;
    private String password;
    private boolean isFlowManager;
    private User entrustOperator;
    private long createTime;

    public User(String name, PasswordEncoder passwordEncoder) {
        this.name = name;
        this.username = name;
        this.password = passwordEncoder.encode(name);
    }


    public static User admin(PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName("admin");
        user.setUsername("admin");
        user.setPassword("admin");
        user.encodePassword(passwordEncoder);
        return user;
    }


    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }


    @Override
    public long getUserId() {
        return id;
    }

    @Override
    public boolean isFlowManager() {
        return isFlowManager;
    }

    @Override
    public IFlowOperator entrustOperator() {
        return entrustOperator;
    }
}
