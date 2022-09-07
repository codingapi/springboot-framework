package com.codingapi.springboot.permission.repository;

import com.codingapi.springboot.permission.entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository extends JpaRepository<Demo, Integer> {

}
