package com.codingapi.example.infra.convert;

import com.codingapi.example.domain.User;
import com.codingapi.example.infra.entity.UserEntity;
import com.codingapi.example.infra.jpa.UserEntityRepository;

public class UserConvertor {

    public static User convert(UserEntity entity, UserEntityRepository userEntityRepository) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setFlowManager(entity.isFlowManager());
        user.setCreateTime(entity.getCreateTime());
        if (entity.getEntrustOperatorId() != 0) {
            user.setEntrustOperator(convert(userEntityRepository.getUserEntityById(entity.getEntrustOperatorId()), userEntityRepository));
        }
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
        if(user.getEntrustOperator()!=null) {
            entity.setEntrustOperatorId(user.getEntrustOperator().getId());
            entity.setEntrustOperatorName(user.getEntrustOperator().getName());
        }
        entity.setFlowManager(user.isFlowManager());
        entity.setCreateTime(user.getCreateTime());
        return entity;
    }
}
