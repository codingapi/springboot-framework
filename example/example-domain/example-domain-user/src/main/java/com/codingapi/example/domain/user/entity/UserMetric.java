package com.codingapi.example.domain.user.entity;

import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UserMetric {

    private String name;
    private String username;
    private String password;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public static UserMetric createAdmin() {
        return new UserMetric(User.USER_ADMIN_NAME, User.USER_ADMIN_USERNAME, User.USER_ADMIN_PASSWORD);
    }

}
