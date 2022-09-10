package com.codingapi.springboot.example.infrastructure.jap.repository;

import com.codingapi.springboot.example.infrastructure.entity.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoEntityRepository extends JpaRepository<DemoEntity, Integer> {


}
