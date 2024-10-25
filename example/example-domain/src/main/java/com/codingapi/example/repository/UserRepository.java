package com.codingapi.example.repository;

import com.codingapi.example.domain.User;

public interface UserRepository {

    User getUserByUsername(String username);

    User getUserById(long id);

    void save(User user);

    void delete(long id);
}
