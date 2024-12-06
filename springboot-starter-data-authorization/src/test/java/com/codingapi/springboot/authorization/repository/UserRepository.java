package com.codingapi.springboot.authorization.repository;

import com.codingapi.springboot.authorization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
