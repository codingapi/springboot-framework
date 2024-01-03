package com.codingapi.springboot.fast.repository;

import com.codingapi.springboot.fast.entity.User;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface UserRepository extends FastRepository<User,Integer> {


}
