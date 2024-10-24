package com.codingapi.example.repository;

import com.codingapi.example.domain.User;

public interface UserRepository {

    User getUserByUsername(String username);

    void save(User user);
}
