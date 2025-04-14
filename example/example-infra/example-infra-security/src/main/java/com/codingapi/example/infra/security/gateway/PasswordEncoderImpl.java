package com.codingapi.example.infra.security.gateway;

import com.codingapi.example.domain.user.gateway.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordEncoderImpl implements PasswordEncoder, org.springframework.security.crypto.password.PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = new  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
