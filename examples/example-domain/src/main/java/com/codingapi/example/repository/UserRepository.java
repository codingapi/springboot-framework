package com.codingapi.example.repository;

import com.codingapi.example.domain.User;

import java.util.List;

public interface UserRepository {

    void save(User user);

    User getUserByUsername(String username);

    User getUserById(long id);

    List<User> findUserByIds(List<Long> ids);
}
