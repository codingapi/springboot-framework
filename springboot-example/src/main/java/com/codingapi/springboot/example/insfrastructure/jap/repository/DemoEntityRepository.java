package com.codingapi.springboot.example.insfrastructure.jap.repository;

import com.codingapi.springboot.example.insfrastructure.entity.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoEntityRepository extends JpaRepository<DemoEntity,Integer> {


}
