package com.codingapi.springboot.framework.query.repository;

import com.codingapi.springboot.framework.query.entity.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository extends JpaRepository<Demo,Integer> {

}
