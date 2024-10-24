package com.codingapi.example.service;

import com.codingapi.example.domain.User;
import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.example.repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public final static String USER_ADMIN = "admin";

    public void initAdmin() {
        User admin = userRepository.getUserByUsername(USER_ADMIN);
        if (admin == null) {
            admin = User.admin(passwordEncoder);
            userRepository.save(admin);
        }
    }

}
