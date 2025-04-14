package com.codingapi.example.domain.user.repository;


import com.codingapi.example.domain.user.entity.User;

public interface UserRepository {

    User getUserByUsername(String username);

    User getUserById(long id);

    void save(User user);

    void delete(long id);
}
