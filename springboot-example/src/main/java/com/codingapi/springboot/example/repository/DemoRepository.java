package com.codingapi.springboot.example.repository;

import com.codingapi.springboot.example.domain.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lorne
 * @since 1.0.0
 */
public interface DemoRepository extends JpaRepository<Demo,Integer> {


}
