package com.codingapi.example.infra.convert;

import com.codingapi.example.domain.User;
import com.codingapi.example.infra.entity.UserEntity;

public class UserConvertor {

    public static User convert(UserEntity entity){
        if(entity==null){
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        return user;
    }

    public static UserEntity convert(User user){
        if(user==null){
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        return entity;
    }
}
