package com.codingapi.example.infra.jpa;

import com.codingapi.example.infra.entity.UserEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface UserEntityRepository extends FastRepository<UserEntity,Long> {

        UserEntity getByUsername(String username);
}
