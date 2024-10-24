package com.codingapi.example.register;

import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.example.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleAutoConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }
}
