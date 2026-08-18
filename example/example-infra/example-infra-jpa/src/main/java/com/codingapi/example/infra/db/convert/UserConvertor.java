package com.codingapi.example.infra.db.convert;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.infra.db.entity.UserEntity;

public class UserConvertor {

    public static User convert(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setUserMetric(entity.getUserMetric());
        user.setCreateTime(entity.getCreateTime());
        return user;
    }

    public static UserEntity convert(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setCreateTime(user.getCreateTime());
        return entity;
    }
}
