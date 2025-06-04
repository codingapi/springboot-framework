package com.codingapi.example.domain.user.entity;

import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

    public final static String USER_ADMIN_USERNAME = "admin";
    public final static String USER_ADMIN_PASSWORD = "admin";
    public final static String USER_ADMIN_NAME = "admin";

    private long id;
    private UserMetric userMetric;
    private boolean isFlowManager;
    private User entrustOperator;
    private long createTime;

    public static User admin(PasswordEncoder passwordEncoder) {
        UserMetric metric = UserMetric.createAdmin();

        User user = new User();
        user.setUserMetric(metric);
        user.encodePassword(passwordEncoder);
        return user;
    }

    public String getName(){
        return userMetric.getName();
    }

    public String getUsername(){
        return userMetric.getUsername();
    }

    public String getPassword(){
        return userMetric.getPassword();
    }

    public void removeEntrust() {
        this.entrustOperator = null;
    }

    public void changeManager() {
        this.isFlowManager = !this.isFlowManager;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.userMetric.encodePassword(passwordEncoder);
    }
}
