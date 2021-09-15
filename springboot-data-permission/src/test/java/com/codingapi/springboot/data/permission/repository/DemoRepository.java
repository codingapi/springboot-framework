package com.codingapi.springboot.data.permission.repository;

import com.codingapi.springboot.data.permission.entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository extends JpaRepository<Demo,Integer> {

}
