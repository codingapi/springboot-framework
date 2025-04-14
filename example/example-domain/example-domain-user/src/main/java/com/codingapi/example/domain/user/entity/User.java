package com.codingapi.example.domain.user.entity;

import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

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

    public void removeEntrust() {
        this.entrustOperator = null;
    }

    public void changeManager() {
        this.isFlowManager = !this.isFlowManager;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
