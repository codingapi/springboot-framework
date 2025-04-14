package com.codingapi.example.infra.db.jpa;

import com.codingapi.example.infra.db.entity.UserEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

import java.util.List;

public interface UserEntityRepository extends FastRepository<UserEntity,Long> {

        UserEntity getUserEntityByUsername(String username);

        UserEntity getUserEntityById(long id);


        List<UserEntity> findUserEntityByIdIn(List<Long> ids);
}
