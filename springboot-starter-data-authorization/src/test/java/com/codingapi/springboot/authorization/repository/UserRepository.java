package com.codingapi.springboot.authorization.repository;

import com.codingapi.springboot.authorization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {


    List<User> findUserByDepartId(long departId);
}
