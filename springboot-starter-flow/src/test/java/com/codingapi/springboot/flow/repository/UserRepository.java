package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository implements FlowOperatorRepository {

    private final List<User> cache = new ArrayList<>();

    public void save(User user) {
        if (user.getId() == 0) {
            cache.add(user);
            user.setId(cache.size());
        }
    }

    public User getById(long id) {
        for (User user : cache) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        return cache.stream().filter(user -> ids.contains(user.getId())).toList();
    }
}
