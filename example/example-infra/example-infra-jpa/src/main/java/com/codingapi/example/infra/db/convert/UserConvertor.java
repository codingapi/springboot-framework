package com.codingapi.example.infra.db.convert;

import com.codingapi.example.domain.user.entity.User;
import com.codingapi.example.infra.db.entity.UserEntity;
import com.codingapi.example.infra.db.jpa.UserEntityRepository;

public class UserConvertor {

    public static User convert(UserEntity entity, UserEntityRepository userEntityRepository) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setUserMetric(entity.getUserMetric());
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
