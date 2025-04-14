package com.codingapi.example.infra.db;

import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import com.codingapi.example.domain.user.repository.UserRepository;
import com.codingapi.example.domain.user.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoDomainConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

}
